package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/12/24 11:57
 */
@Getter
@RequiredArgsConstructor
public enum ProjectHasExamTypeEnum {
    NO(0, "无考试"),
    YES(1, "有考试");
    private final Integer value;
    private final String description;
}
