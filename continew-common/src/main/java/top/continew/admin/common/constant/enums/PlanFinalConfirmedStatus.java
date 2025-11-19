package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanFinalConfirmedStatus {

    ADMIN_PENDING(0, "待管理员确定"),
    DIRECTOR_PENDING(1, "待中心主任确定"),
    DIRECTOR_CONFIRMED(2, "中心主任确定"),
    DIRECTOR_REJECTED(3, "中心主任驳回");

    private final Integer value;
    private final String description;
}

