package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生类型与禁考项目关联详情信息
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生类型与禁考项目关联详情信息")
public class CandidateTypeDisableProjectDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生类型ID
     */
    @Schema(description = "考生类型ID")
    @ExcelProperty(value = "考生类型ID")
    private Long candidateTypeId;

    /**
     * 禁考项目ID
     */
    @Schema(description = "禁考项目ID")
    @ExcelProperty(value = "禁考项目ID")
    private Long disableProjectId;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    @ExcelProperty(value = "删除标记")
    private Integer isDeleted;
}