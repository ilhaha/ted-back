package top.continew.admin.exam.model.resp;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2025/11/11 15:47
 */
@Data
public class PaymentInfoVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）
     */
    private String noticeNo;

    /**
     * 缴费凭证URL
     */
    private String paymentProofUrl;

    /**
     * 审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回
     *
     */
    private Integer auditStatus;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 八大类名称
     */
    private String categoryName;

    /**
     * 姓名
     */
    private String nickname;

}
