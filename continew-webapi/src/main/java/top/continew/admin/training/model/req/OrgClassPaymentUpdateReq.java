package top.continew.admin.training.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ilhaha
 * @Create 2026/1/9 11:36
 */
@Data
public class OrgClassPaymentUpdateReq {

    /**
     * 班级id
     */
    private Long id;

    /**
     * 缴费状态
     * 0-未缴费 1-已缴费 2-免费 3-审核未通过
     */
    private Integer payStatus;

    /**
     * 缴费凭证URL
     */
    private String payProofUrl;

}
