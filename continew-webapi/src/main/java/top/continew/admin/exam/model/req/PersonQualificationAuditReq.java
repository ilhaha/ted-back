package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.continew.admin.common.model.resp.BaseResp;

@Data
public class PersonQualificationAuditReq  extends BaseResp {

    /**
     * 审核状态：1-通过，2-不通过
     */
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    /**
     * 审核意见（不通过时必填）
     */
    private String auditRemark;
}
