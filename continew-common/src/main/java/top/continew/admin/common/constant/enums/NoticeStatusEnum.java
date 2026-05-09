package top.continew.admin.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeStatusEnum {

    /**
     * 待审核
     */
    PENDING_REVIEW(0, "待审核"),

    /**
     * 审核通过
     */
    APPROVED(1, "审核通过"),

    /**
     * 审核未通过
     */
    REJECTED(2, "审核未通过");

    /**
     * 状态值
     */
    private final Integer value;

    /**
     * 状态描述
     */
    private final String desc;
}