package top.continew.admin.common.constant.enums;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpecialCertificationApplicantAuditStatusEnum {

    UNREVIEWED(0, "未审核"),
    APPROVED(1, "审核通过"),
    RETURN_FOR_CORRECTION(2, "退回补正"),
    FAKE_MATERIAL(3, "虚假资料（禁止再次申报）");


    private final Integer value;
    private final String description;

}
