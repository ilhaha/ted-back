package top.continew.admin.exam.model.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2026/5/11 15:08
 */
@Data
public class ProjectExamResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 考试办理类型
     */
    private Integer examAttemptType;
}
