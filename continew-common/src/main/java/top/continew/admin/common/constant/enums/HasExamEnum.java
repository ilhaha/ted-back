package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

@Getter
@RequiredArgsConstructor
public enum HasExamEnum implements BaseEnum<Integer> {

    NO(0, "否"),
    YES(1, "是");

    private final Integer value;
    private final String description;

}