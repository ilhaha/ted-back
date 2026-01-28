package top.continew.admin.common.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ScoreConfirmStatusEnum {

    UNCONFIRMED(0, "未确认"),
    CONFIRMED(1, "已确认");

    @EnumValue
    private final Integer value;

    private final String desc;

    ScoreConfirmStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
