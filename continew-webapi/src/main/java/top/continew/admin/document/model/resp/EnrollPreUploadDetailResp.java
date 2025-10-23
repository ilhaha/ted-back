package top.continew.admin.document.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;

/**
 * 机构报考-考生扫码上传文件详情信息
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "机构报考-考生扫码上传文件详情信息")
public class EnrollPreUploadDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;


    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @ExcelProperty(value = "考生id")
    private Long candidatesId;


    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    @ExcelProperty(value = "考生姓名")
    private String nickname;

    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    @ExcelProperty(value = "考试计划名称")
    private String examPlanName;


    /**
     * 考试计划id
     */
    @Schema(description = "考试计划id")
    @ExcelProperty(value = "考试计划id")
    private String planId;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    @ExcelProperty(value = "机构名称")
    private String orgName;

    /**
     * 考生机构班级id
     */
    @Schema(description = "考生机构班级id")
    @ExcelProperty(value = "考生机构班级id")
    private String batchId;

    /**
     * 报名资格申请表URL
     */
    @Schema(description = "报名资格申请表URL")
    @ExcelProperty(value = "报名资格申请表URL")
    private String qualificationFileUrl;

    /**
     * 审核状态（0未审核，1审核通过，2退回补正，3虚假资料-禁止再次申报项目）
     */
    @Schema(description = "审核状态（0未审核，1审核通过，2退回补正，3虚假资料-禁止再次申报项目）")
    @ExcelProperty(value = "审核状态（0未审核，1审核通过，2退回补正，3虚假资料-禁止再次申报项目）")
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    @Schema(description = "审核意见或退回原因")
    @ExcelProperty(value = "审核意见或退回原因")
    private String remark;

    /**
     * 是否删除（0否，1是）
     */
    @Schema(description = "是否删除（0否，1是）")
    @ExcelProperty(value = "是否删除（0否，1是）")
    private Boolean isDeleted;
}