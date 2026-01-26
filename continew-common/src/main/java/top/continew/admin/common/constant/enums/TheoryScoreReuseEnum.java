package top.continew.admin.common.constant.enums;

import lombok.Getter;

/**
 * 是否复用理论成绩枚举
 */
@Getter
public enum TheoryScoreReuseEnum {

    NO(0, "否"),
    YES(1, "是");

    private final Integer value;
    private final String desc;

    TheoryScoreReuseEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
