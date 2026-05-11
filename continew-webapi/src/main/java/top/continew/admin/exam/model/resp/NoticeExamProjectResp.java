package top.continew.admin.exam.model.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2026/5/11 14:57
 */
@Data
public class NoticeExamProjectResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 是否报考了这个项目
     */
    private Boolean isApply;

    /**
     * 考试场次类型：1初试，2补考
     */
    private Integer examAttemptType;
}
