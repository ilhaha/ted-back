package top.continew.admin.training.model.req;


import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改机构考生关联参数
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Data
@Schema(description = "创建或修改机构考生关联参数")
public class OrgCandidateReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long Id;

    /**
     * 考生id
     */
    private Long candidateId;

    /**
     * 机构班级id
     */
    private Long orClassId;

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 审核留言
     */
    private String remark;
}