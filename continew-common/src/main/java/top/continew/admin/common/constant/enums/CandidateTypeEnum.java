package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/11/3 19:45
 */
@Getter
@RequiredArgsConstructor
public enum CandidateTypeEnum {

    WORKER(false, "作业人员"),

    INSPECTION(true, "检验人员");

    private final Boolean value;

    private final String description;
}
