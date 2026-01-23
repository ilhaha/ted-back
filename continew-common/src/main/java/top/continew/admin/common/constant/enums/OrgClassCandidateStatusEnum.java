package top.continew.admin.common.constant.enums;

import lombok.Getter;

/**
 * 班级学员状态枚举
 */
@Getter
public enum OrgClassCandidateStatusEnum {

    /**
     * 正在班级
     */
    IN_CLASS(0, "正在班级"),

    /**
     * 已考试
     */
    EXAMINED(1, "已考试");

    private final Integer value;
    private final String desc;

    OrgClassCandidateStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
