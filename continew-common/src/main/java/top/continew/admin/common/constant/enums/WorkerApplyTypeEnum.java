package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/11/3 16:32
 */
@Getter
@RequiredArgsConstructor
public enum WorkerApplyTypeEnum {

    /** 作业人员扫码报名 */
    SCAN_APPLY(0, "作业人员扫码报名"),

    /** 机构批量导入 */
    ORG_IMPORT(1, "机构批量导入");

    private final Integer value;
    private final String description;
}
