package top.continew.admin.common.constant.enums;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/11/24 15:12
 */
@Getter
@RequiredArgsConstructor
public enum EnrollExamStatusEnum {
    NOT_SIGNED(0, "未签到"),
    SIGNED(1, "已签到"),
    SUBMITTED(2, "已交卷");

    private final int value;
    private final String description;
}
