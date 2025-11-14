package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/11/14 16:15
 */
@Getter
@RequiredArgsConstructor
public enum ExamPlanTypeEnum {

    WORKER(0, "作业人员"),

    INSPECTION(1, "检验人员");

    private final Integer value;

    private final String description;
}
