package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanFinalConfirmedStatus {

    UNCONFIRMED(0, "最终考试时间地点未确定"),
    CONFIRMED(1, "最终考试时间地点已确定");

    private final Integer value;
    private final String description;

}
