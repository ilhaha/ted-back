package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/12/24 14:40
 */
@Getter
@RequiredArgsConstructor
public enum ExamScoreEntryStatusEnum {

    NO_Entry(0, "未录入"),

    ENTERED(1, "已录入");



    private final Integer value;

    private final String description;
}
