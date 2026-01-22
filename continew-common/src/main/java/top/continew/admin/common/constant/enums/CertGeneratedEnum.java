package top.continew.admin.common.constant.enums;

import lombok.Getter;

@Getter
public enum CertGeneratedEnum {

    NOT_GRANTED(0, "未许可"),
    GRANTED(1, "已许可");

    private final Integer code;
    private final String desc;

    CertGeneratedEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
