package top.continew.admin.worker.model.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import top.continew.starter.security.mask.annotation.JsonMask;
import top.continew.starter.security.mask.enums.MaskType;

import java.io.Serial;
import java.time.*;
import java.util.Map;

/**
 * 作业人员报名信息
 *
 * @author ilhaha
 * @since 2025/11/03 11:15
 */
@Data
@Schema(description = "作业人员报名信息")
public class WorkerApplyResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    private Long classId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    private String candidateName;

    /**
     * 作业人员性别
     */
    @Schema(description = "作业人员性别")
    private String gender;

    /**
     * 作业人员手机号
     */
    @Schema(description = "作业人员手机号")
    @JsonMask(MaskType.MOBILE_PHONE)
    private String phone;

    /**
     * 报名资格申请表路径
     */
    @Schema(description = "报名资格申请表路径")
    private String qualificationPath;

    /**
     * 报名资格申请表名称
     */
    @Schema(description = "报名资格申请表名称")
    private String qualificationName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
//    @JsonMask(MaskType.ID_CARD)
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    @Schema(description = "身份证正面存储地址")
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    @Schema(description = "身份证反面存储地址")
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    @Schema(description = "一寸免冠照存储地址")
    private String facePhoto;

    /**
     * 审核状态:0待审核,1已生效,2未通过
     */
    @Schema(description = "审核状态:0待审核,1已生效,2未通过")
    private Integer status;

    /**
     * 报名方式，0作业人员自报名，1机构批量导入
     */
    @Schema(description = "报名方式，0作业人员自报名，1机构批量导入")
    private Integer applyType;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    private Integer isDeleted;

    /**
     * 八大类名称
     */
    @Schema(description = "八大类名称")
    private String categoryName;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    private String className;

    /**
     * 审核意见或退回原因
     */
    @Schema(description = "审核意见或退回原因")
    private String remark;

    /**
     * 资料名称与资料路径映射
     */
    @Schema(description = "资料名称与资料路径映射")
    @ExcelProperty(value = "资料名称与资料路径映射")
    private Map<String, String> docMap;
}