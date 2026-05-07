package top.continew.admin.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExamAttemptTypeEnum {

    /**
     * 初考
     */
    FIRST_EXAM(1, "初考"),

    /**
     * 补考
     */
    RETAKE_EXAM(2, "补考");

    /**
     * 值
     */
    private final Integer value;

    /**
     * 描述
     */
    private final String desc;
}