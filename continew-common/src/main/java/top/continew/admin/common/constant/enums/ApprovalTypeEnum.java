package top.continew.admin.common.constant.enums;

import lombok.Getter;

@Getter
public enum ApprovalTypeEnum {

    INITIAL(0, "初申"),
    REEXAMINATION(1, "复审");

    private final Integer code;
    private final String desc;

    ApprovalTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
