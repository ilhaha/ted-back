package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 考生身份证信息实体
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
@Data
@TableName("ted_exam_idcard")
public class ExamIdcardDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 人像照片路径
     */
    private String facePhoto;

    /**
     * 逻辑删除标志：0否 1是
     */
    private Integer isDeleted;
}