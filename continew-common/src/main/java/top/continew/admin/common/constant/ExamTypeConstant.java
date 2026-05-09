package top.continew.admin.common.constant;

/**
 * 考试办理类型常量
 */
public final class ExamTypeConstant {

    private ExamTypeConstant() {
    }

    /**
     * 无
     */
    public static final Integer NONE = 0;

    /**
     * 取证考试
     */
    public static final Integer CERTIFICATE_EXAM = 1;

    /**
     * 换证考试
     */
    public static final Integer RENEWAL_EXAM = 2;

    /**
     * 免考换证
     */
    public static final Integer EXEMPTION_RENEWAL = 3;
}