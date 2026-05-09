package top.continew.admin.common.constant;

/**
 * 学历、专业、持证规则常量
 */
public final class CertificateUploadRuleConstant {

    private CertificateUploadRuleConstant() {
    }

    /**
     * 学历
     */
    /**
     * 学历
     */
    public static final String EDUCATION_JUNIOR_HIGH = "初中";
    public static final String EDUCATION_HIGH_SCHOOL = "高中";
    public static final String EDUCATION_VOCATIONAL_HIGH = "职高";
    public static final String EDUCATION_TECHNICAL_SCHOOL = "技校";
    public static final String EDUCATION_SECONDARY = "中专";
    public static final String EDUCATION_COLLEGE = "大专";
    public static final String EDUCATION_BACHELOR = "本科";
    public static final String EDUCATION_BACHELOR_OR_ABOVE = "本科及以上";
    /**
     * 专业类型
     */
    public static final String MAJOR_TYPE_SCIENCE = "理工科";
    public static final String MAJOR_TYPE_ENGINEERING = "非理工科";

    /**
     * 持证时长（月）
     */
    public static final long MONTHS_SIX = 6L;
    public static final long MONTHS_TWELVE = 12L;
    public static final long MONTHS_THIRTY_SIX = 36L;
}