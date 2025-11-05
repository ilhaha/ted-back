package top.continew.admin.util;

import com.alibaba.excel.EasyExcel;
import com.aspose.cells.PdfSaveOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ExcelUtilReactive {

    // 模板缓存（不变）
    private final Map<String, byte[]> templateCache = new ConcurrentHashMap<>();
    // 同步HTTP客户端（适配MVC）
    private final RestTemplate restTemplate = new RestTemplate();

    // ============ 核心修改：模板下载改为同步 ============
    public byte[] loadTemplateSync(String templateUrl) {
        // 先查缓存
        byte[] cached = templateCache.get(templateUrl);
        if (cached != null) {
            log.info("模板缓存命中，URL={}，大小={}KB", templateUrl, cached.length / 1024);
            return cached;
        }

        // 同步下载远程模板
        log.info("开始同步下载模板 URL={}", templateUrl);
        try {
            // 方式1：用RestTemplate（简单）
            byte[] templateBytes = restTemplate.getForObject(templateUrl, byte[].class);
            if (templateBytes == null || templateBytes.length == 0) {
                throw new RuntimeException("模板下载为空：" + templateUrl);
            }

            // 方式2：用HttpURLConnection（更灵活，可设置超时）
            /*
            URL url = new URL(templateUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000); // 15秒连接超时
            conn.setReadTimeout(15000);    // 15秒读取超时
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("模板下载失败，响应码：" + responseCode);
            }
            try (InputStream is = conn.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[4096];
                int n;
                while ((n = is.read(buf)) != -1) {
                    baos.write(buf, 0, n);
                }
                templateBytes = baos.toByteArray();
            }
            conn.disconnect();
            */

            templateCache.put(templateUrl, templateBytes);
            log.info("模板下载完成，URL={}，大小={}KB", templateUrl, templateBytes.length / 1024);
            return templateBytes;
        } catch (Exception e) {
            log.error("同步下载模板失败：URL={}", templateUrl, e);
            throw new RuntimeException("模板下载失败：" + e.getMessage(), e);
        }
    }

    // 填充Excel模板（同步）
    public byte[] fillTemplateToBytesSync(Map<String, Object> dataMap, String templateUrl) {
        try {
            byte[] templateBytes = loadTemplateSync(templateUrl);
            try (InputStream templateIs = new ByteArrayInputStream(templateBytes);
                 ByteArrayOutputStream excelOut = new ByteArrayOutputStream()) {
                List<Map<String, Object>> dataList = Collections.singletonList(dataMap);
                EasyExcel.write(excelOut)
                        .withTemplate(templateIs)
                        .sheet()
                        .doFill(dataList);
                byte[] excelBytes = excelOut.toByteArray();
                log.info("填充Excel模板完成，大小={}KB", excelBytes.length / 1024);
                return excelBytes;
            }
        } catch (Exception e) {
            log.error("填充Excel模板失败", e);
            throw new RuntimeException("Excel填充失败：" + e.getMessage(), e);
        }
    }

    // 插入照片（同步）
    public byte[] insertPhotoToExcelBytesSync(byte[] excelBytes, byte[] photoBytes) {
        try {
            if (photoBytes == null || photoBytes.length == 0) {
                log.info("没有有效照片，直接返回Excel原始字节");
                return excelBytes;
            }

            try (ByteArrayInputStream excelIn = new ByteArrayInputStream(excelBytes);
                 XSSFWorkbook workbook = new XSSFWorkbook(excelIn);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                Sheet sheet = workbook.getSheetAt(0);
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                CreationHelper helper = workbook.getCreationHelper();

                int startCol = 3; // D列
                int endCol = 3;
                int startRow = 1; // 第2行
                int endRow = 5;   // 第6行

                // 适配合并单元格
                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress range = sheet.getMergedRegion(i);
                    if (range.isInRange(startRow, startCol)) {
                        endCol = range.getLastColumn();
                        endRow = range.getLastRow();
                        log.info("检测到合并单元格，调整插入范围：col[{}->{}], row[{}->{}]",
                                startCol, endCol, startRow, endRow);
                        break;
                    }
                }

                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(startCol);
                anchor.setRow1(startRow);
                anchor.setCol2(endCol + 1);
                anchor.setRow2(endRow + 1);
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                anchor.setDx1(20000);
                anchor.setDy1(10000);
                anchor.setDx2(-20000);
                anchor.setDy2(-10000);

                int pictureIdx = workbook.addPicture(photoBytes, Workbook.PICTURE_TYPE_JPEG);
                drawing.createPicture(anchor, pictureIdx);

                workbook.write(out);
                byte[] result = out.toByteArray();
                log.info("Excel插入图片完成，大小={}KB", result.length / 1024);
                return result;
            }
        } catch (Exception e) {
            log.error("插入照片到Excel失败", e);
            throw new RuntimeException("图片插入失败：" + e.getMessage(), e);
        }
    }

    // Excel转PDF（同步）
    public byte[] convertExcelBytesToPdfBytesSync(byte[] excelBytes) {
        try {
            log.info("开始Excel转PDF，输入大小={}KB", excelBytes.length / 1024);
            try (ByteArrayInputStream in = new ByteArrayInputStream(excelBytes);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook(in);
                PdfSaveOptions options = new PdfSaveOptions();
                options.setCompliance(com.aspose.cells.PdfCompliance.PDF_A_1_B);
                workbook.save(out, options);
                workbook.dispose();

                byte[] pdfBytes = out.toByteArray();
                log.info("Excel转PDF完成，大小={}KB", pdfBytes.length / 1024);
                return pdfBytes;
            }
        } catch (Exception e) {
            log.error("Excel转PDF失败", e);
            throw new RuntimeException("PDF转换失败：" + e.getMessage(), e);
        }
    }

    // 生成PDF字节流（同步）
    public byte[] generatePdfBytesSync(Map<String, Object> dataMap, String templateUrl, byte[] photoBytes) {
        byte[] excelBytes = fillTemplateToBytesSync(dataMap, templateUrl);
        byte[] excelWithPhoto = insertPhotoToExcelBytesSync(excelBytes, photoBytes);
        return convertExcelBytesToPdfBytesSync(excelWithPhoto);
    }

    // 生成PDF响应（同步，适配MVC）
    public ResponseEntity<byte[]> generatePdfResponseEntitySync(Map<String, Object> dataMap,
                                                                String templateUrl,
                                                                byte[] photoBytes,
                                                                String fileName) {
        log.info("开始生成PDF响应：模板URL={}, 数据项数={}, 照片大小={}KB",
                templateUrl, dataMap.size(), photoBytes.length / 1024);

        try {
            byte[] pdfBytes = generatePdfBytesSync(dataMap, templateUrl, photoBytes);
            String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            log.info("生成PDF响应完成，文件名={}，大小={}KB", encodedName, pdfBytes.length / 1024);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("生成PDF响应失败", e);
            String errorMsg = "生成准考证失败：" + e.getMessage();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMsg.getBytes(StandardCharsets.UTF_8));
        }
    }
}
