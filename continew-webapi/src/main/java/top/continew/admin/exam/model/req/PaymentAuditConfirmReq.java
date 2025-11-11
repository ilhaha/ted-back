package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/11/11 18:55
 */
@Data
public class PaymentAuditConfirmReq {

    /** 考试计划ID */
    @NotNull(message = "二维码已被篡改或参数缺失，请重新获取")
    private Long id;

    /**
     * 缴费凭证URL
     */
    @NotBlank(message = "未上传缴费凭证")
    private String paymentProofUrl;

    /**
     * 缴费凭证审核状态
     */
    private Integer auditStatus;

}
