package top.continew.admin.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TheoryExemptEnum {

    NOT_EXEMPT(0, "不免考"),
    EXEMPT(1, "免考");

    private final Integer value;
    private final String desc;

}