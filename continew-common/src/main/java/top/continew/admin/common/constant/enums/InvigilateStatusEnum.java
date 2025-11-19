package top.continew.admin.common.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum InvigilateStatusEnum {

    NOT_START(0, "待监考"),
    TO_FILL(1, "待录入"),
    TO_AUDIT(2, "待审核"),
    FINISHED(3, "已完成"),
    TO_CONFIRM(4, "待监考员确认"),
    REJECTED(5, "监考员拒绝监考");

    private final Integer value;
    private final String description;

    InvigilateStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}

