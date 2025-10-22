package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构考生预报名详情信息
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "机构考生预报名详情信息")
public class EnrollPreDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @ExcelProperty(value = "考生id")
    private Long candidateId;

    /**
     * 计划id
     */
    @Schema(description = "计划id")
    @ExcelProperty(value = "计划id")
    private Long planId;

    /**
     * 上传资料二维码
     */
    @Schema(description = "上传资料二维码")
    @ExcelProperty(value = "上传资料二维码")
    private String uploadQrcode;

    /**
     * 资料上传状态 0-资料待补充 1-报考资料已齐全
     */
    @Schema(description = "资料上传状态 0-资料待补充 1-报考资料已齐全")
    @ExcelProperty(value = "资料上传状态 0-资料待补充 1-报考资料已齐全")
    private Integer status;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    @Schema(description = "逻辑删除 0-未删除 1-已删除")
    @ExcelProperty(value = "逻辑删除 0-未删除 1-已删除")
    private Integer isDeleted;
}