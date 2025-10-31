package top.continew.admin.document.model.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/29 19:09
 */
@Data
public class EnrollPreReviewReq {

    @NotNull(message = "审核状态不能为空")
    private Integer status;

    private String remark;

    @NotEmpty(message = "审核列表不能为空")
    private List<Long> reviewIds;
}
