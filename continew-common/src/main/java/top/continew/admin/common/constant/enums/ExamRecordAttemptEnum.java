package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/12/19 9:15
 */
@Getter
@RequiredArgsConstructor
public enum ExamRecordAttemptEnum {

    FIRST(0, "首考"),

    RETAKE(1, "补考");

    private final Integer value;

    private final String description;
}
