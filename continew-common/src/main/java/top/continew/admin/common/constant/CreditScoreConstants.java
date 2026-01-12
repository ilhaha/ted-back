package top.continew.admin.common.constant;

/**
 * 信誉分阈值常量
 */
public final class CreditScoreConstants {

    private CreditScoreConstants() {
    }

    /** 优秀 */
    public static final int EXCELLENT = 90;

    /** 良好 */
    public static final int GOOD = 80;

    /** 及格线 */
    public static final int PASS = 70;

    /** 最低分 */
    public static final int MIN = 0;

    /** 最高分 */
    public static final int MAX = 100;

    /**
     * 根据信誉分计算报名顺延天数
     */
    public static int calcDelayDays(int creditScore) {
        if (creditScore >= CreditScoreConstants.EXCELLENT) {
            return 0;
        }
        if (creditScore >= CreditScoreConstants.GOOD) {
            return 1;
        }
        if (creditScore >= CreditScoreConstants.PASS) {
            return 2;
        }
        return -1; // 不允许报名
    }

}
