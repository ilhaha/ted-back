package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/12/24 11:57
 */
@Getter
@RequiredArgsConstructor
public enum ExamRecprdsHasCertofocateEnum {
    NO(0, "未生成"),
    YES(1, "已生成");
    private final Integer value;
    private final String description;
}
