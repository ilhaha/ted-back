package top.continew.admin.util;

import org.apache.poi.xwpf.usermodel.*;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * <p>
 * TicketWordPdfUtil —— 准考证 Word/PDF 生成工具类
 * </p>
 *
 * <p>
 * 功能：
 * 1. 读取模板（准考证模板.docx）
 * 2. 替换占位符（${key}）
 * 3. 生成 Word 文件
 * 4. 调用 LibreOffice 转换为 PDF
 * </p>
 */
public class TicketWordPdfUtil {

    /** 模板路径（请放在 /mnt/data 或 resources 目录） */
    private static final String TEMPLATE_PATH = "/mnt/data/准考证模板.docx";

    /** LibreOffice 命令（Windows 请改为 soffice.exe 完整路径） */
    private static final String LIBREOFFICE_CMD = "soffice";

    /**
     * 生成准考证 Word 和 PDF 文件
     *
     * @param data 准考证数据对象
     * @param outputDir 输出目录
     * @return PDF 文件路径
     * @throws Exception 异常
     */
    public static String generateTicketDocAndPdf(CandidateTicketDTO data, String outputDir) throws Exception {
        Files.createDirectories(Paths.get(outputDir));

        String baseName = "准考证_" + data.getTicketId();
        String docxPath = outputDir + File.separator + baseName + ".docx";
        String pdfPath = outputDir + File.separator + baseName + ".pdf";

        try (InputStream in = new FileInputStream(TEMPLATE_PATH);
             XWPFDocument doc = new XWPFDocument(in)) {

            Map<String, String> map = buildPlaceholderMap(data);
            replacePlaceholders(doc, map);

            try (FileOutputStream out = new FileOutputStream(docxPath)) {
                doc.write(out);
            }
        }

        convertToPdf(docxPath, outputDir);
        return pdfPath;
    }

    /** 构建占位符映射 */
    private static Map<String, String> buildPlaceholderMap(CandidateTicketDTO data) {
        Map<String, String> map = new HashMap<>();
        map.put("name", safe(data.getName()));
        map.put("idCard", safe(data.getIdCard()));
        map.put("ticketId", safe(data.getTicketId()));
        map.put("classCode", String.valueOf(data.getClassCode()));
        map.put("className", safe(data.getClassName()));
        map.put("examType", safe(data.getExamType()));
        map.put("examItem", safe(data.getExamItem()));
        map.put("examRoom", safe(data.getExamRoom()));
        map.put("examTime", safe(data.getExamTime()));
        return map;
    }

    /** 替换 Word 模板占位符 */
    private static void replacePlaceholders(XWPFDocument doc, Map<String, String> map) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            for (XWPFRun run : p.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> e : map.entrySet()) {
                        String placeholder = "${" + e.getKey() + "}";
                        if (text.contains(placeholder)) {
                            run.setText(text.replace(placeholder, e.getValue()), 0);
                        }
                    }
                }
            }
        }

        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun run : p.getRuns()) {
                            String text = run.getText(0);
                            if (text != null) {
                                for (Map.Entry<String, String> e : map.entrySet()) {
                                    String placeholder = "${" + e.getKey() + "}";
                                    if (text.contains(placeholder)) {
                                        run.setText(text.replace(placeholder, e.getValue()), 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /** LibreOffice 转 PDF */
    private static void convertToPdf(String docxPath, String outputDir) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                LIBREOFFICE_CMD,
                "--headless",
                "--convert-to", "pdf",
                "--outdir", outputDir,
                docxPath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[LibreOffice] " + line);
            }
        }

        int exit = process.waitFor();
        if (exit != 0) {
            throw new RuntimeException("PDF 转换失败，LibreOffice 返回码：" + exit);
        }
    }

    /** 安全字符串 */
    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
