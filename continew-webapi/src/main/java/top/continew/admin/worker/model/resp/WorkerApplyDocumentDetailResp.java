package top.continew.admin.worker.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 作业人员报名上传的资料详情信息
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "作业人员报名上传的资料详情信息")
public class WorkerApplyDocumentDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业人员报名表ID
     */
    @Schema(description = "作业人员报名表ID")
    @ExcelProperty(value = "作业人员报名表ID")
    private Long workerApplyId;

    /**
     * 资料存储路径
     */
    @Schema(description = "资料存储路径")
    @ExcelProperty(value = "资料存储路径")
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    @ExcelProperty(value = "关联资料类型ID")
    private Long typeId;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Integer isDeleted;

    /**
     * 资料类型
     */
    @Schema(description = "资料类型")
    @ExcelProperty(value = "资料类型")
    private String typeName;
}