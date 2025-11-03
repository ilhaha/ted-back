package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/11/3 16:32
 */
@Getter
@RequiredArgsConstructor
public enum WorkerApplyReviewStatus {

    PENDING_REVIEW(0, "待审核"),

    APPROVED(1, "审核通过"),

    REJECTED(2, "未通过"),

    FAKE_MATERIAL(3, "虚假材料");

    private final Integer value;
    private final String description;
}
