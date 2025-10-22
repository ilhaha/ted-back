package top.continew.admin.exam.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.aspose.cells.PdfSaveOptions;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.mapper.ExamTicketMapper;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.util.ImageSheetWriteHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateTicketServiceImpl implements CandidateTicketService {

    private final ExamTicketMapper examTicketMapper;
    @Resource
    private AESWithHMAC aesWithHMAC;

    @Value("${excel.template.url}")
    private String excelTemplateUrl;

    private static final String TEMP_DIR = System.getProperty("user.dir") + File.separator + "temp" + File.separator;

    @Override
    public void generateTicket(Long userId, String examNumber, HttpServletResponse response) throws Exception {
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) tempDir.mkdirs();

        String tempExcelPath = null;
        String tempPdfPath = null;

        try {
            // 1️ 查询准考证数据
            String encryptedExamNumber = aesWithHMAC.encryptAndSign(examNumber);
            CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, encryptedExamNumber);
            dto.setPhoto("https://onedt-exam-system.oss-cn-shenzhen.aliyuncs.com/2025/10/22/68f8b90a5814e0d894c63901.jfif");
            if (dto == null) throw new RuntimeException("未找到该用户的准考证数据！");

            dto.setTicketId(examNumber);
            dto.setIdCard(aesWithHMAC.verifyAndDecrypt(dto.getIdCard()));
            log.info("查询到的准考证数据：{}", dto);

            // 2️ 填充文本数据
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

            // 3️ 下载照片 (OSS URL → byte[])
            byte[] photoBytes = null;
            if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
                try (InputStream is = new URL(dto.getPhoto()).openStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    photoBytes = baos.toByteArray();
                }
            }

            // 4️ 生成临时 Excel
            tempExcelPath = TEMP_DIR + "ticket_excel_" + System.currentTimeMillis() + ".xlsx";
            fillExcelTemplate(dataMap, tempExcelPath, photoBytes);

            verifyTempFile(tempExcelPath, "Excel");

            // 5️ Excel 转 PDF
            tempPdfPath = TEMP_DIR + "ticket_pdf_" + System.currentTimeMillis() + ".pdf";
            excelToPdf(tempExcelPath, tempPdfPath);

            verifyTempFile(tempPdfPath, "PDF");

            // 6️ 输出下载
            downloadPdf(tempPdfPath, response, "准考证_" + examNumber + ".pdf");

        } finally {
            deleteTempFile(tempExcelPath);
            deleteTempFile(tempPdfPath);
        }
    }


    /**
     * 填充 Excel 模板
     */
    /**
     * 填充 Excel 模板并插入照片到 D2
     */
    private void fillExcelTemplate(Map<String, Object> dataMap, String outputPath, byte[] photoBytes) throws Exception {
        try (InputStream templateIs = getRemoteTemplateInputStream(excelTemplateUrl)) {
            List<Map<String, Object>> dataList = new ArrayList<>();
            dataList.add(dataMap);

            var writer = EasyExcel.write(outputPath)
                    .withTemplate(templateIs)
                    .sheet();

            // 仅当有照片时注册图片写入
            if (photoBytes != null && photoBytes.length > 0) {
                // D2:D6 → row1=1, col1=3, row2=6, col2=4
                writer.registerWriteHandler(new ImageSheetWriteHandler(1, 3, 6, 4, photoBytes));
            }

            writer.doFill(dataList);
        }
    }



    private InputStream getRemoteTemplateInputStream(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("远程模板获取失败，响应码：" + connection.getResponseCode());
        }
        return connection.getInputStream();
    }

    /**
     * Excel 转 PDF
     */
    private void excelToPdf(String excelPath, String pdfPath) throws Exception {
        com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook(excelPath);
        PdfSaveOptions options = new PdfSaveOptions();
        workbook.save(pdfPath, options);
    }

    /**
     * 下载 PDF 到前端
     */
    private void downloadPdf(String pdfPath, HttpServletResponse response, String fileName) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        response.setHeader("Cache-Control", "no-store");

        try (InputStream is = new FileInputStream(pdfPath);
             OutputStream os = response.getOutputStream()) {
            FileCopyUtils.copy(is, os);
        }
    }

    /**
     * 校验临时文件有效性
     */
    private void verifyTempFile(String filePath, String fileType) {
        if (filePath == null) throw new RuntimeException(fileType + "临时文件路径为空");
        File file = new File(filePath);
        if (!file.exists()) throw new RuntimeException(fileType + "临时文件不存在：" + filePath);
        if (file.length() == 0) throw new RuntimeException(fileType + "临时文件为空：" + filePath);
        if (!file.canRead()) throw new RuntimeException(fileType + "临时文件不可读取：" + filePath);
        log.info("{}临时文件生成成功，路径：{}，大小：{}字节", fileType, filePath, file.length());
    }

    /**
     * 删除临时文件
     */
    private void deleteTempFile(String filePath) {
        if (filePath == null) return;
        File file = new File(filePath);
        if (file.exists() && !file.delete()) log.warn("临时文件删除失败：{}", filePath);
    }

    /**
     * 安全处理空值
     */
    private String getSafeValue(String value) {
        return value == null ? "" : value;
    }
}
