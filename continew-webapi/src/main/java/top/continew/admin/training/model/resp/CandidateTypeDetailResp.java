package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生类型详情信息
 *
 * @author ilhaha
 * @since 2025/11/03 17:57
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生类型详情信息")
public class CandidateTypeDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @ExcelProperty(value = "考生id")
    private Long candidateId;

    /**
     * 类型，0作业人员，1检验人员
     */
    @Schema(description = "类型，0作业人员，1检验人员")
    @ExcelProperty(value = "类型，0作业人员，1检验人员")
    private Boolean type;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    @ExcelProperty(value = "删除标记")
    private Integer isDeleted;
}