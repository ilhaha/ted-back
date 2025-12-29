package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 人员复审信息表详情信息
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "人员复审信息表详情信息")
public class PersonQualificationDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @ExcelProperty(value = "姓名")
    private String name;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @ExcelProperty(value = "身份证号")
    private String idCard;

    /**
     * 文化程度
     */
    @Schema(description = "文化程度")
    @ExcelProperty(value = "文化程度")
    private String education;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    @ExcelProperty(value = "联系电话")
    private String phone;

    /**
     * 聘用单位
     */
    @Schema(description = "聘用单位")
    @ExcelProperty(value = "聘用单位")
    private String employer;

    /**
     * 资格项目代码
     */
    @Schema(description = "资格项目代码")
    @ExcelProperty(value = "资格项目代码")
    private String qualificationCategoryCode;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    @ExcelProperty(value = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;
}