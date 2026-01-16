package top.continew.admin.exam.model.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构申请焊接考试项目信息
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
@Data
@Schema(description = "机构申请焊接考试项目信息")
public class WeldingExamApplicationResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请机构ID
     */
    @Schema(description = "申请机构ID")
    private Long orgId;

    /**
     * 焊接类型：0-金属焊接，1-非金属焊接
     */
    @Schema(description = "焊接类型：0-金属焊接，1-非金属焊接")
    private Integer weldingType;

    /**
     * 焊接考试项目名称
     */
    @Schema(description = "焊接考试项目名称")
    private String projectName;

    /**
     * 考试项目代码
     */
    @Schema(description = "考试项目代码")
    private String projectCode;

    /**
     * 申请原因或说明
     */
    @Schema(description = "申请原因或说明")
    private String applicationReason;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     */
    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer status;

    /**
     * 审核意见/备注
     */
    @Schema(description = "审核意见/备注")
    private String reviewComment;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间")
    private LocalDateTime submittedAt;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime reviewedAt;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    private Boolean isDeleted;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间戳
     */
    @Schema(description = "更新时间戳")
    private LocalDateTime updateTime;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    @ExcelProperty(value = "机构名称")
    private String orgName;
}