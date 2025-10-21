package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构考生关联详情信息
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "机构考生关联详情信息")
public class OrgCandidateDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID
     */
    @Schema(description = "机构ID")
    @ExcelProperty(value = "机构ID")
    private Long orgId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @ExcelProperty(value = "考生ID")
    private Long candidateId;

    /**
     * 机构对应的项目id
     */
    @Schema(description = "机构对应的项目id")
    @ExcelProperty(value = "机构对应的项目id")
    private Long projectId;

    /**
     * 状态 (负1-拒绝, 0-退出，1-待通过，2-已加入)
     */
    @Schema(description = "状态 (负1-拒绝, 0-退出，1-待通过，2-已加入)")
    @ExcelProperty(value = "状态 (负1-拒绝, 0-退出，1-待通过，2-已加入)")
    private Integer status;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    @ExcelProperty(value = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;
}