package top.continew.admin.common.constant.enums;

/**
 * 资料提交状态枚举
 * 0-未提交 1-已提交 2-审核中 3-已通过 4-已驳回
 */
public enum OrgClassDocSubmitStatusEnum {

    NOT_SUBMITTED(0, "未提交"),
    SUBMITTED(1, "已提交");
    private final Integer code;
    private final String desc;

    OrgClassDocSubmitStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrgClassDocSubmitStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrgClassDocSubmitStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
