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

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.enums.WorkerPictureTypeEnum;
import top.continew.admin.common.model.dto.ExcelUploadFileResultDTO;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.model.resp.IdCardFileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.starter.core.exception.BusinessException;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel OLE 附件处理工具类
 */
public class ExcelMediaUtils {

    /**
     * 判断整行是否为空
     */
    public static boolean isRowEmpty(Row row) {
        if (row == null)
            return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断Excel中某个单元格是否有图片
     */
    public static boolean hasPicture(Workbook workbook, Sheet sheet, int row, int col) {
        if (workbook instanceof XSSFWorkbook) {
            XSSFSheet xssfSheet = (XSSFSheet)sheet;
            for (POIXMLDocumentPart dr : xssfSheet.getRelations()) {
                if (dr instanceof XSSFDrawing) {
                    XSSFDrawing drawing = (XSSFDrawing)dr;
                    for (XSSFShape shape : drawing.getShapes()) {
                        if (shape instanceof XSSFPicture) {
                            XSSFPicture picture = (XSSFPicture)shape;
                            XSSFClientAnchor anchor = picture.getPreferredSize();
                            if (anchor.getRow1() == row && anchor.getCol1() == col) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 上传指定单元格中的文件，并返回上传结果。
     *
     * <p>此方法会从 Excel 的指定行列中获取图片，如果图片存在，则调用 UploadService 上传到服务器，
     * 并返回包含图片 URL 和相关信息的 PictureUploadResultDTO。</p>
     *
     * @param workbook      Excel 工作簿对象
     * @param sheet         Excel 工作表对象
     * @param row           图片所在行（0-based）
     * @param col           图片所在列（0-based）
     * @param uploadService 上传服务，用于实际上传图片
     * @param type          图片类型，参见 WorkerPictureTypeEnum 的值
     * @return {@link ExcelUploadFileResultDTO} 包含上传后的图片 URL 和相关信息；如果单元格没有图片则返回 null
     * @throws RuntimeException 当上传失败或读取图片发生异常时抛出
     */
    public static ExcelUploadFileResultDTO excelUploadFile(Workbook workbook,
                                                           Sheet sheet,
                                                           int row,
                                                           int col,
                                                           UploadService uploadService,
                                                           Integer type) {

        ExcelUploadFileResultDTO dto = new ExcelUploadFileResultDTO();

        try {
            // 仅支持 XSSFWorkbook
            if (!(workbook instanceof XSSFWorkbook xssfWorkbook)) {
                return null;
            }

            XSSFSheet xssfSheet = (XSSFSheet)sheet;

            for (POIXMLDocumentPart dr : xssfSheet.getRelations()) {
                if (!(dr instanceof XSSFDrawing drawing))
                    continue;

                for (XSSFShape shape : drawing.getShapes()) {
                    if (!(shape instanceof XSSFPicture picture))
                        continue;

                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    if (anchor.getRow1() != row || anchor.getCol1() != col)
                        continue;

                    // 获取图片数据
                    XSSFPictureData pictureData = picture.getPictureData();
                    byte[] data = pictureData.getData();

                    String ext = pictureData.suggestFileExtension();
                    String fileName = row + "_" + col + "." + ext;

                    // 上传文件（转 MultipartFile）
                    MultipartFile file = new InMemoryMultipartFile("file", fileName, pictureData.getMimeType(), data);
                    // 身份证正面
                    if (WorkerPictureTypeEnum.ID_CARD_FRONT.getValue().equals(type)) {
                        IdCardFileInfoResp idCardFileInfoResp = uploadService.uploadIdCard(file, type);
                        dto.setIdCardPhotoFront(idCardFileInfoResp.getUrl());
                        dto.setIdCardNumber(idCardFileInfoResp.getIdCardNumber());
                        dto.setRealName(idCardFileInfoResp.getRealName());
                        dto.setGender(idCardFileInfoResp.getGender());
                    } else if (WorkerPictureTypeEnum.ID_CARD_BACK.getValue().equals(type)) {
                        // 身份证反面
                        IdCardFileInfoResp idCardFileInfoResp = uploadService.uploadIdCard(file, type);
                        dto.setIdCardPhotoBack(idCardFileInfoResp.getUrl());
                        dto.setValidEndDate(idCardFileInfoResp.getValidEndDate());
                    } else if (WorkerPictureTypeEnum.PASSPORT_PHOTO.getValue().equals(type)) {
                        // 如果是一寸照类型，先转换为标准一寸照片
                        //                        file = IDPhotoConverter.convertToOneInchPhoto(file);
                        IdCardFileInfoResp idCardFileInfoResp = uploadService.uploadIdCard(file, type);
                        dto.setFacePhoto(idCardFileInfoResp.getFacePhoto());
                    } else {
                        // 资料
                        GeneralFileReq fileReq = new GeneralFileReq();
                        FileInfoResp fileInfo = uploadService.upload(file, fileReq);
                        dto.setDocUrl(fileInfo.getUrl());
                    }
                    return dto;
                }
            }

        } catch (Exception e) {
            throw new BusinessException("第 " + (row + 1) + " 行第 " + (col + 1) + " 列错误: " + e.getMessage());
        }
        // 没有找到图片
        return null;
    }

    public static Map<String, List<String>> getOleAttachmentMapAndUpload(XSSFWorkbook workbook,
                                                                         int targetRow,
                                                                         UploadService uploadService,
                                                                         boolean upload) {

        Map<String, List<String>> attachmentMap = new HashMap<>();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            XSSFDrawing drawing = sheet.getDrawingPatriarch();
            if (drawing == null)
                continue;

            for (XSSFShape shape : drawing.getShapes()) {
                if (!(shape instanceof XSSFObjectData objData))
                    continue;

                XSSFClientAnchor anchor = (XSSFClientAnchor)shape.getAnchor();
                if (anchor == null || anchor.getRow1() != targetRow)
                    continue;

                String key = anchor.getRow1() + "_" + anchor.getCol1();
                if (attachmentMap.containsKey(key))
                    continue;

                try {
                    String ole2ClassName = objData.getOLE2ClassName();
                    if (!"Package".equals(ole2ClassName))
                        continue;

                    DirectoryEntry directory = objData.getDirectory();
                    if (!(directory instanceof DirectoryNode directoryNode))
                        continue;

                    // 解析 OLE 内嵌文件
                    Ole10Native ole10Native = Ole10Native.createFromEmbeddedOleObject(directoryNode);
                    byte[] fileBytes = ole10Native.getDataBuffer();
                    String originalFileName = ole10Native.getFileName();

                    // 若有文件名，先尝试转码
                    if (originalFileName != null && !originalFileName.isEmpty()) {
                        originalFileName = new String(originalFileName.getBytes("ISO-8859-1"), "GBK");
                        // 去掉路径，只保留文件名
                        originalFileName = Paths.get(originalFileName).getFileName().toString();
                    } else {
                        // 没有文件名，使用自定义命名
                        originalFileName = targetRow + "_" + anchor.getCol1();
                    }

                    String ext = "";
                    int dotIndex = originalFileName.lastIndexOf('.');
                    if (dotIndex >= 0 && dotIndex < originalFileName.length() - 1) {
                        ext = originalFileName.substring(dotIndex);
                    }

                    String fileName = targetRow + "_" + anchor.getCol1() + ext;
                    String uploadedUrl = null;

                    if (upload) {
                        String contentType = "application/pdf";
                        if (".doc".equals(ext) || ".docx".equals(ext)) {
                            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                        }
                        MultipartFile file = new InMemoryMultipartFile("file", fileName, contentType, fileBytes);

                        GeneralFileReq fileReq = new GeneralFileReq();
                        FileInfoResp fileInfo = uploadService.upload(file, fileReq);

                        if (fileInfo == null || fileInfo.getUrl() == null) {
                            throw new RuntimeException("上传失败，返回 URL 为 null");
                        }
                        uploadedUrl = fileInfo.getUrl();
                    }

                    attachmentMap.put(key, Arrays.asList(originalFileName, uploadedUrl));

                } catch (Exception e) {
                    if (upload) {
                        throw new RuntimeException("第 " + (targetRow + 1) + " 行 报名资格申请表 上传失败: " + e.getMessage(), e);
                    }
                }
            }
        }

        return attachmentMap;
    }

}
