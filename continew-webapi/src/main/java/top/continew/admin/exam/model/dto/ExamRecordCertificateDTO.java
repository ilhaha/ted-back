package top.continew.admin.exam.model.dto;

import lombok.Data;

@Data
public class ExamRecordCertificateDTO {

    /**
     * 考试记录ID
     */
    private Long id;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 考生昵称
     */
    private String nickname;

    /**
     * 用户名 / 身份证号
     */
    private String username;

    /**
     * 报名用户ID
     */
    private Long userId;

    /**
     * 工作单位
     */
    private String workUnit;

    /**
     * 一寸免冠照
     */
    private String facePhoto;

    /**
     * 项目分类名称
     */
    private String categoryName;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编码
     */
    private String projectCode;
}
