package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/12/24 14:40
 */
@Getter
@RequiredArgsConstructor
public enum ExamScoreInputTypeEnum {

    OPER(1, "实操成绩"),

    ROAD(2, "道路成绩");



    private final Integer value;

    private final String description;
}
