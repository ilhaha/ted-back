package top.continew.admin.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtils {

    /**
     * 向指定单元格写入值（自动处理合并单元格）
     *
     * @param sheet    sheet
     * @param rowIndex 行索引（从0开始）
     * @param colIndex 列索引（从0开始）
     * @param value    写入的值
     */
    public static void setCellValue(Sheet sheet,
                                    int rowIndex,
                                    int colIndex,
                                    String value) {

        // 1️⃣ 判断是否属于合并单元格
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range.isInRange(rowIndex, colIndex)) {
                rowIndex = range.getFirstRow();
                colIndex = range.getFirstColumn();
                break;
            }
        }

        // 2️⃣ 获取或创建行
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        // 3️⃣ 获取或创建单元格
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }

        // 4️⃣ 写入值
        cell.setCellValue(value);
    }
}