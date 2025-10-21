package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构班级与考生关联表详情信息
 *
 * @author ilhaha
 * @since 2025/10/21 16:48
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "机构班级与考生关联表详情信息")
public class OrgClassCandidateDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级id
     */
    @Schema(description = "班级id")
    @ExcelProperty(value = "班级id")
    private Long classId;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @ExcelProperty(value = "考生id")
    private Long candidateId;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    @ExcelProperty(value = "删除标记")
    private Integer isDeleted;
}