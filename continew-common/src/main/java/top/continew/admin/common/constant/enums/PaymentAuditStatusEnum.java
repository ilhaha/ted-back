package top.continew.admin.common.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 缴费审核状态枚举
 * 0-待缴费 1-已缴费待审核 2-审核通过 3-审核驳回
 * 4-补正审核 5-退款审核 6-已退款 7-退款驳回
 */
@Getter
public enum PaymentAuditStatusEnum {

    TO_BE_PAID(0, "待缴费"),
    PAID_PENDING_REVIEW(1, "已缴费待审核"),
    APPROVED(2, "审核通过"),
    REJECTED(3, "审核驳回"),
    CORRECTION_REVIEW(4, "补正审核"),
    REFUND_REVIEW(5, "退款审核"),
    REFUNDED(6, "已退款"),
    REFUND_REJECTED(7, "退款驳回");

    @EnumValue
    private final Integer value;
    private final String desc;

    PaymentAuditStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
