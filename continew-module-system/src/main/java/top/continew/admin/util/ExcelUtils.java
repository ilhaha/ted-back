/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    public static void setCellValue(Sheet sheet, int rowIndex, int colIndex, String value) {

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