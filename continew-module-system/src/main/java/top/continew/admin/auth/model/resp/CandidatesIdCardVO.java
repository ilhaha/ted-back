package top.continew.admin.auth.model.resp;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author ilhaha
 * @Create 2025/10/20 11:58
 */
@Data
public class CandidatesIdCardVO {
    /**
     * 姓名
     */
    private String realName;

    /**
     * 性别（男 女）
     */
    private String gender;

    /**
     * 民族
     */
    private String nation;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 住址
     */
    private String address;

    /**
     * 身份证号码
     */
    private String idCardNumber;

    /**
     * 签发机关
     */
    private String issuingAuthority;

    /**
     * 有效期开始日期
     */
    private LocalDate validStartDate;

    /**
     * 有效期截止日期
     */
    private LocalDate validEndDate;

    /**
     * 身份证正面照片路径
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面照片路径
     */
    private String idCardPhotoBack;

    /**
     * 逻辑删除标志：0否 1是
     */
    private Integer isDeleted;
}
