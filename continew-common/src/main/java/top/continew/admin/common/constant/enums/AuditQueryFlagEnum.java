package top.continew.admin.common.constant.enums;

/**
 * 查询类型枚举
 * 1-报名资料审核
 * 2-缴费审核
 */
public enum AuditQueryFlagEnum {

    APPLY_AUDIT(1, "报名资料审核"),
    PAY_AUDIT(2, "缴费审核");

    private final Integer code;
    private final String desc;

    AuditQueryFlagEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
