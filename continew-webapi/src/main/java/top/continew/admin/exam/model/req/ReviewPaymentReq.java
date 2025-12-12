package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/12/12 9:20
 */
@Data
public class ReviewPaymentReq {

    /**
     * 审核id
     */
    @NotEmpty(message = "未选择审核的数据")
    private List<Long> reviewIds;

    /**
     * 状态
     */
    @NotNull(message = "未选择审核状态")
    private Integer auditStatus;

    /**
     * 拒绝原因
     */
    private String rejectReason;

}
