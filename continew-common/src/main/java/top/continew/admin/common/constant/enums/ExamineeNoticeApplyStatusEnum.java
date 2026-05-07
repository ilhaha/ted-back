package top.continew.admin.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExamineeNoticeApplyStatusEnum {

    /**
     * 待报名
     */
    PENDING_APPLY(0, "待报名"),

    /**
     * 报名待审核
     */
    PENDING_REVIEW(1, "报名待审核"),

    /**
     * 报名审核通过
     */
    REVIEW_APPROVED(2, "报名审核通过"),

    /**
     * 报名审核未通过
     */
    REVIEW_REJECTED(3, "报名审核未通过"),

    /**
     * 完成部分项目考试
     */
    PARTIAL_EXAM_COMPLETED(4, "完成部分项目考试"),

    /**
     * 已完成全部考试
     */
    ALL_EXAM_COMPLETED(5, "已完成全部考试");

    /**
     * 状态值
     */
    private final Integer value;

    /**
     * 状态描述
     */
    private final String desc;
}