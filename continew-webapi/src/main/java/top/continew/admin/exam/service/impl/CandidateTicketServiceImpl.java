package top.continew.admin.exam.service.impl;

import com.alibaba.excel.EasyExcel;
import com.aspose.cells.PdfSaveOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ExamIdcardMapper;
import top.continew.admin.exam.mapper.ExamTicketMapper;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;
import top.continew.admin.exam.model.entity.ExamIdcardDO;
import top.continew.admin.exam.service.CandidateTicketService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateTicketServiceImpl implements CandidateTicketService {

    private final ExamTicketMapper examTicketMapper;
    @Resource
    private AESWithHMAC aesWithHMAC;

    @Value("${excel.template.url}")
    private String excelTemplateUrl;

    /** 最大并发生成数量（保护 Aspose / POI 的内存与 CPU 占用） */
    @Value("${ticket.gen.concurrent:8}")
    private int maxConcurrentGenerations;

    @Resource
    private ExamIdcardMapper examIdcardMapper;

    /** 模板缓存（字节）*/
    private volatile byte[] templateCache;

    /** 图片缓存（url -> bytes）*/
    private final Map<String, byte[]> photoCache = new ConcurrentHashMap<>();

    /** 并发控制信号量 */
    private Semaphore generationSemaphore;

    /** 内部常量：图片插入区域与边距（可按需调整） */
    private static final int COL_D_INDEX = 3; // D 列 -> index 3
    private static final int ROW_D2_INDEX = 1; // D2 -> row index 1
    private static final int ROW_D6_INDEX = 6; // 行结束 (使用 anchor row2)
    private static final int MARGIN_X = 200; // 左右内缩（0-1023）
    private static final int MARGIN_Y = 40;  // 上下内缩（0-255）

    // 初始化 semaphore（在构造后或首次使用时）
    private void ensureSemaphoreInitialized() {
        if (generationSemaphore == null) {
            synchronized (this) {
                if (generationSemaphore == null) {
                    generationSemaphore = new Semaphore(Math.max(1, maxConcurrentGenerations));
                    log.info("初始化 ticket generation semaphore, permits={}", maxConcurrentGenerations);
                }
            }
        }
    }

    @Override
    public void generateTicket(Long userId, String examNumber, HttpServletResponse response) throws Exception {
        ensureSemaphoreInitialized();

        // 查询数据
        CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, aesWithHMAC.encryptAndSign(examNumber));
        // 查询用户照片URL
        QueryWrapper<ExamIdcardDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id_card_number", dto.getIdCard())
                    .eq("is_deleted", 0)
                    .select("face_photo");
        dto.setPhoto(examIdcardMapper.selectOne(queryWrapper).getFacePhoto());
        if (dto == null) {
            throw new RuntimeException("未找到该用户的准考证数据！");
        }

        // 解密并设置
        dto.setTicketId(examNumber);
        dto.setIdCard(aesWithHMAC.verifyAndDecrypt(dto.getIdCard()));
        // 组装填充字段
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", getSafeValue(dto.getName()));
        dataMap.put("idCard", getSafeValue(dto.getIdCard()));
        dataMap.put("ticketId", getSafeValue(dto.getTicketId()));
        dataMap.put("classCode", getSafeValue(dto.getClassCode() != null ? dto.getClassCode().toString() : null));
        dataMap.put("className", getSafeValue(dto.getClassName()));
        dataMap.put("examType", getSafeValue(dto.getExamType()));
        dataMap.put("examItem", getSafeValue(dto.getExamItem()));
        dataMap.put("examRoom", getSafeValue(dto.getExamRoom()));
        dataMap.put("examTime", getSafeValue(dto.getExamTime()));

        // 下载或从缓存读取照片
        byte[] photoBytes = null;
        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            try {
                photoBytes = loadPhotoCached(dto.getPhoto());
                if (photoBytes == null || photoBytes.length == 0) {
                    log.warn("照片为空或下载失败：{}", dto.getPhoto());
                } else {
                    log.debug("照片大小：{} bytes", photoBytes.length);
                }
            } catch (Exception e) {
                log.warn("下载照片失败：{}, error: {}", dto.getPhoto(), e.getMessage());
            }
        }

        // 下面的转换是重量级操作，需要获取 Semaphore
        boolean permit = false;
        try {
            // 尝试获取许可，超时避免无限等待（可调整超时时间）
            permit = generationSemaphore.tryAcquire(60, TimeUnit.SECONDS);
            if (!permit) {
                throw new RuntimeException("当前生成任务繁忙，请稍后重试");
            }

            // 1) 使用 EasyExcel 在内存中填充模板 -> 得到 excelBytes
            byte[] excelBytes = fillTemplateToBytes(dataMap);

            // 2) 用 POI 在内存 Excel 上插入图片 -> 得到 excelWithPhoto
            byte[] excelWithPhoto = insertPhotoToExcelBytes(excelBytes, photoBytes);

            // 3) 使用 Aspose 在内存将 Excel 转为 PDF -> 得到 pdfBytes
            byte[] pdfBytes = convertExcelBytesToPdfBytes(excelWithPhoto);

            // 4) 写入响应流
            sendPdfResponse(response, pdfBytes, "准考证_" + examNumber + ".pdf");
        } finally {
            if (permit) {
                generationSemaphore.release();
            }
        }
    }

    // ============ 辅助方法 ============

    /**
     * 从缓存或远程获取模板字节（线程安全的懒加载）
     */
    private InputStream getRemoteTemplateInputStream(String urlStr) throws Exception {
        // 双重检查锁保证 templateCache 只加载一次
        if (templateCache == null) {
            synchronized (this) {
                if (templateCache == null) {
                    log.info("从远程下载 Excel 模板：{}", urlStr);
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(15000);
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new RuntimeException("远程模板获取失败，响应码：" + conn.getResponseCode());
                    }
                    try (InputStream is = conn.getInputStream();
                         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        byte[] buf = new byte[4096];
                        int n;
                        while ((n = is.read(buf)) != -1) baos.write(buf, 0, n);
                        templateCache = baos.toByteArray();
                    }
                }
            }
        }
        return new ByteArrayInputStream(templateCache);
    }

    /**
     * 从缓存获取照片（若不存在则下载并缓存）
     */
    private byte[] loadPhotoCached(String photoUrl) {
        return photoCache.computeIfAbsent(photoUrl, url -> {
            try {
                URL u = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(15000);
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    log.warn("图片请求返回非200：{} -> {}", url, conn.getResponseCode());
                    return new byte[0];
                }
                try (InputStream is = conn.getInputStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = is.read(buf)) != -1) baos.write(buf, 0, n);
                    return baos.toByteArray();
                }
            } catch (Exception e) {
                log.warn("下载图片异常：{} -> {}", url, e.getMessage());
                return new byte[0];
            }
        });
    }

    /**
     * 使用 EasyExcel 将模板和 dataMap 填充并返回 excel 字节数组（内存）
     */
    private byte[] fillTemplateToBytes(Map<String, Object> dataMap) throws Exception {
        try (InputStream templateIs = getRemoteTemplateInputStream(excelTemplateUrl);
             ByteArrayOutputStream excelOut = new ByteArrayOutputStream()) {

            List<Map<String, Object>> dataList = Collections.singletonList(dataMap);
            EasyExcel.write(excelOut)
                    .withTemplate(templateIs)
                    .sheet()
                    .doFill(dataList);

            return excelOut.toByteArray();
        }
    }

    /**
     * 在内存中的 Excel 字节上用 POI 插入图片（放在 D2:D6 区域并内缩留白）
     */
    private byte[] insertPhotoToExcelBytes(byte[] excelBytes, byte[] photoBytes) throws Exception {
        if (photoBytes == null || photoBytes.length == 0) {
            // 没有照片则直接返回原字节
            return excelBytes;
        }

        try (ByteArrayInputStream excelIn = new ByteArrayInputStream(excelBytes);
             XSSFWorkbook workbook = new XSSFWorkbook(excelIn);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                log.warn("Excel 没有 sheet，无法插入图片");
                workbook.write(out);
                return out.toByteArray();
            }

            Drawing<?> drawing = sheet.createDrawingPatriarch();
            CreationHelper helper = workbook.getCreationHelper();
            ClientAnchor anchor = helper.createClientAnchor();

            // D2:D6 -> col1=3,row1=1 ; col2=4,row2=6
            anchor.setCol1(COL_D_INDEX);
            anchor.setRow1(ROW_D2_INDEX);
            anchor.setCol2(COL_D_INDEX + 1); // end col (右边一列)
            anchor.setRow2(ROW_D6_INDEX);

            // 内缩偏移，防止遮盖方格线
            anchor.setDx1(MARGIN_X);
            anchor.setDy1(MARGIN_Y);
            anchor.setDx2(1023 - MARGIN_X);
            anchor.setDy2(255 - MARGIN_Y);

            int pictureIdx = workbook.addPicture(photoBytes, Workbook.PICTURE_TYPE_JPEG);
            drawing.createPicture(anchor, pictureIdx);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * 使用 Aspose 将 Excel 字节转换为 PDF 字节（内存）
     */
    private byte[] convertExcelBytesToPdfBytes(byte[] excelBytes) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(excelBytes);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            com.aspose.cells.Workbook wb = new com.aspose.cells.Workbook(in);

            PdfSaveOptions options = new PdfSaveOptions();
            // 优化参数（按需调整）
            // options.setOnePagePerSheet(true);
            // options.setOptimizationType(com.aspose.cells.Rendering.PdfOptimizationType.MINIMUM_SIZE);
            // options.setEmbedStandardWindowsFonts(false);
            wb.save(out, options);

            // Aspose Workbook 没有 closeable，但可以尝试 dispose（若可用）
            try {
                wb.dispose();
            } catch (Throwable ignored) {}

            return out.toByteArray();
        }
    }

    /**
     * 写 PDF 到 HttpServletResponse
     */
    private void sendPdfResponse(HttpServletResponse response, byte[] pdfBytes, String fileName) throws Exception {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new RuntimeException("生成 PDF 为空");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setHeader("Cache-Control", "no-store");
        try (OutputStream os = response.getOutputStream()) {
            FileCopyUtils.copy(new ByteArrayInputStream(pdfBytes), os);
            os.flush();
        }
    }

    /**
     * 安全处理 null -> ""
     */
    private String getSafeValue(String v) {
        return v == null ? "" : v;
    }
}
