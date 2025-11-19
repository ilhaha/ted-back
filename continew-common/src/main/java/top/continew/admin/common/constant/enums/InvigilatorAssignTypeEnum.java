package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvigilatorAssignTypeEnum {

    RANDOM_FIRST(1, "第一次随机分配"),
    RANDOM_SECOND(2, "第二次随机分配"),
    ADMIN_ASSIGN(3, "管理员指派");

    private final Integer value;
    private final String description;

}