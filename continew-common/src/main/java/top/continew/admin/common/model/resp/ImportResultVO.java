package top.continew.admin.common.model.resp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ImportResultVO<T> {

    /** 可成功导入的数据 */
    private List<T> successList = new ArrayList<>();

    /** 导入失败的数据 */
    private List<ImportFailVO> failList = new ArrayList<>();

    @Data
    public static class ImportFailVO {
        /** Excel 行号 */
        private Integer rowNum;
        /** 原始数据（可选，方便前端回显） */
        private Map<String, Object> rowData;
        /** 失败原因 */
        private String reason;
    }
}
