package top.continew.admin.util;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;

public class ImageSheetWriteHandler implements SheetWriteHandler {

    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;
    private final byte[] imageBytes;

    public ImageSheetWriteHandler(int startRow, int startCol, int endRow, int endCol, byte[] imageBytes) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.imageBytes = imageBytes;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {}

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (imageBytes == null || imageBytes.length == 0) {
            return; // 没有图片就跳过
        }
        try {
            Sheet sheet = writeSheetHolder.getSheet();
            Workbook workbook = sheet.getWorkbook();

            int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
            CreationHelper helper = workbook.getCreationHelper();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();

            // 指定图片锚点：D2 到 D6（行列从 0 开始）
            anchor.setCol1(startCol);
            anchor.setRow1(startRow);
            anchor.setCol2(endCol);
            anchor.setRow2(endRow);

            drawing.createPicture(anchor, pictureIdx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
