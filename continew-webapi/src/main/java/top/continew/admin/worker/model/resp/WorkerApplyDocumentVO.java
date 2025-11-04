package top.continew.admin.worker.model.resp;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/11/4 10:58
 */

@Data
public class WorkerApplyDocumentVO {
    /** 材料类型ID */
    private Long typeId;

    /** 材料类型名称 */
    private String typeName;

    /** 多个文件路径（逗号分隔） */
    private String docPaths;
}
