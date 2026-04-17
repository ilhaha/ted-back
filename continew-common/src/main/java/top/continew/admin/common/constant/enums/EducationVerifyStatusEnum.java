package top.continew.admin.common.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
@Getter
@RequiredArgsConstructor
public enum EducationVerifyStatusEnum {

    PENDING(0, "待审核"),
    PASSED(1, "已认证"),
    REJECTED(2, "认证未通过"),
    WAIT(3, "待认证");


    private final Integer value;

    private final String desc;

}