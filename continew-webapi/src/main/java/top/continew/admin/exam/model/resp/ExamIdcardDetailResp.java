package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生身份证信息详情信息
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生身份证信息详情信息")
public class ExamIdcardDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @ExcelProperty(value = "姓名")
    private String realName;

    /**
     * 性别（男 女）
     */
    @Schema(description = "性别（男 女）")
    @ExcelProperty(value = "性别（男 女）")
    private String gender;

    /**
     * 民族
     */
    @Schema(description = "民族")
    @ExcelProperty(value = "民族")
    private String nation;

    /**
     * 出生日期
     */
    @Schema(description = "出生日期")
    @ExcelProperty(value = "出生日期")
    private LocalDate birthDate;

    /**
     * 住址
     */
    @Schema(description = "住址")
    @ExcelProperty(value = "住址")
    private String address;

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    @ExcelProperty(value = "身份证号码")
    private String idCardNumber;

    /**
     * 签发机关
     */
    @Schema(description = "签发机关")
    @ExcelProperty(value = "签发机关")
    private String issuingAuthority;

    /**
     * 有效期开始日期
     */
    @Schema(description = "有效期开始日期")
    @ExcelProperty(value = "有效期开始日期")
    private LocalDate validStartDate;

    /**
     * 有效期截止日期
     */
    @Schema(description = "有效期截止日期")
    @ExcelProperty(value = "有效期截止日期")
    private LocalDate validEndDate;

    /**
     * 身份证正面照片路径
     */
    @Schema(description = "身份证正面照片路径")
    @ExcelProperty(value = "身份证正面照片路径")
    private String idCardPhotoFront;

    /**
     * 身份证反面照片路径
     */
    @Schema(description = "身份证反面照片路径")
    @ExcelProperty(value = "身份证反面照片路径")
    private String idCardPhotoBack;

    /**
     * 逻辑删除标志：0否 1是
     */
    @Schema(description = "逻辑删除标志：0否 1是")
    @ExcelProperty(value = "逻辑删除标志：0否 1是")
    private Integer isDeleted;
}