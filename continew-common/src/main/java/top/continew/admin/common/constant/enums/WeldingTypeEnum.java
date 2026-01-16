package top.continew.admin.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeldingTypeEnum {

    METAL(0, "金属焊接"),
    NON_METAL(1, "非金属焊接");

    private final Integer value;
    private final String desc;

}
