package top.continew.admin.common.constant.enums;

import lombok.Getter;

/**
 * 考试类型枚举
 * 0 - 理论考试
 * 1 - 实操考试
 */
@Getter
public enum ExamTypeEnum {

    /**
     * 理论考试
     */
    THEORY(0, "理论考试"),

    /**
     * 实操考试
     */
    PRACTICE(1, "实操考试");

    private final Integer value;
    private final String desc;

    ExamTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
