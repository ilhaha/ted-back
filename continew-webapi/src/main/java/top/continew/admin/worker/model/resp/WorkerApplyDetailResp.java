package top.continew.admin.worker.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 作业人员报名详情信息
 *
 * @author ilhaha
 * @since 2025/11/03 11:15
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "作业人员报名详情信息")
public class WorkerApplyDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    @ExcelProperty(value = "班级ID")
    private Long classId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    @ExcelProperty(value = "作业人员姓名")
    private String candidateName;

    /**
     * 作业人员性别
     */
    @Schema(description = "作业人员性别")
    @ExcelProperty(value = "作业人员性别")
    private String gender;

    /**
     * 作业人员手机号
     */
    @Schema(description = "作业人员手机号")
    @ExcelProperty(value = "作业人员手机号")
    private String phone;

    /**
     * 报名资格申请表路径
     */
    @Schema(description = "报名资格申请表路径")
    @ExcelProperty(value = "报名资格申请表路径")
    private String qualificationPath;

    /**
     * 报名资格申请表名称
     */
    @Schema(description = "报名资格申请表名称")
    @ExcelProperty(value = "报名资格申请表名称")
    private String qualificationName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @ExcelProperty(value = "身份证号")
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    @Schema(description = "身份证正面存储地址")
    @ExcelProperty(value = "身份证正面存储地址")
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    @Schema(description = "身份证反面存储地址")
    @ExcelProperty(value = "身份证反面存储地址")
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    @Schema(description = "一寸免冠照存储地址")
    @ExcelProperty(value = "一寸免冠照存储地址")
    private String facePhoto;

    /**
     * 审核状态:0待审核,1已生效,2未通过
     */
    @Schema(description = "审核状态:0待审核,1已生效,2未通过")
    @ExcelProperty(value = "审核状态:0待审核,1已生效,2未通过")
    private Integer status;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Integer isDeleted;
}