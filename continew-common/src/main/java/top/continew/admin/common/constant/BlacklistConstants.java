package top.continew.admin.common.constant;

/**
 * 黑名单相关常量
 */
public final class BlacklistConstants {

    private BlacklistConstants() {}

    /** 是否黑名单：否 */
    public static final Boolean NOT_BLACKLIST = false;

    /** 是否黑名单：是 */
    public static final Boolean IS_BLACKLIST = true;

    /** 黑名单时长：无 */
    public static final int DURATION_NONE = 0;

    /** 黑名单时长：1天 */
    public static final int DURATION_1_DAY = 1;

    /** 黑名单时长：1个月 */
    public static final int DURATION_1_MONTH = 2;

    /** 黑名单时长：3个月 */
    public static final int DURATION_3_MONTH = 3;

    /** 黑名单时长：6个月 */
    public static final int DURATION_6_MONTH = 4;

    /** 黑名单时长：1年 */
    public static final int DURATION_1_YEAR = 5;

    /** 黑名单时长：无期限 */
    public static final int DURATION_FOREVER = 6;
}
