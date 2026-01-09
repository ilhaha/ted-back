package top.continew.admin.common.constant.enums;

/**
 * 缴费状态
 * 0-未缴费
 * 1-待审核
 * 2-已缴费
 * 3-免缴
 * 4-审核未通过
 */
public enum OrgClassPayStatusEnum {

    UNPAID(0, "未缴费"),
    PENDING_AUDIT(1, "待审核"),
    PAID(2, "已缴费"),
    FREE(3, "免缴"),
    AUDIT_REJECTED(4, "审核未通过");

    private final Integer code;
    private final String desc;

    OrgClassPayStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrgClassPayStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrgClassPayStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}

