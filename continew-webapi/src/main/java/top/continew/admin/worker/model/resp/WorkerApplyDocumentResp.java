package top.continew.admin.worker.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 作业人员报名上传的资料信息
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Data
@Schema(description = "作业人员报名上传的资料信息")
public class WorkerApplyDocumentResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业人员报名表ID
     */
    @Schema(description = "作业人员报名表ID")
    private Long workerApplyId;

    /**
     * 资料存储路径
     */
    @Schema(description = "资料存储路径")
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    private Long typeId;
}