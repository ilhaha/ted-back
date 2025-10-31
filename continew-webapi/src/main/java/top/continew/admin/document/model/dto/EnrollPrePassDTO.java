package top.continew.admin.document.model.dto;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/10/29 19:55
 */
@Data
public class EnrollPrePassDTO {

    /** 报名预上传记录 ID */
    private Long id;

    /** 考生 ID */
    private Long candidatesId;

    /** 考试计划 ID */
    private Long planId;

    /** 报名资格申请表路径 */
    private String qualificationFileUrl;

    /** 批次 ID */
    private Long batchId;

    /** 文件类型 ID */
    private Long typeId;

    /** 文件路径（逗号拼接） */
    private String docPaths;

}
