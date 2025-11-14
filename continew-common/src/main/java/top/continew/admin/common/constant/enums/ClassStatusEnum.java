package top.continew.admin.common.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ClassStatusEnum {

    ENROLLING(0, "招生中"),
    STOPPED(1, "停止招生");

    private final Integer value;

    private final String description;

    ClassStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
