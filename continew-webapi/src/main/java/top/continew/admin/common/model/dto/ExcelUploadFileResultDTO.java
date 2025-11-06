package top.continew.admin.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author ilhaha
 * @Create 2025/11/5 13:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelUploadFileResultDTO {

    /** OSS 地址（普通图片） */
    private String docUrl;

    /** 识别出的姓名 */
    private String realName;

    /** 识别出的性别 */
    private String gender;

    /** 识别出的身份证号码 */
    private String idCardNumber;

    /** 身份证正面照 URL */
    private String idCardPhotoFront;

    /** 身份证反面照 URL */
    private String idCardPhotoBack;

    /** 一寸照或人脸照 URL */
    private String facePhoto;

    /**
     * 有效期截止日期
     */
    private LocalDate validEndDate;

}
