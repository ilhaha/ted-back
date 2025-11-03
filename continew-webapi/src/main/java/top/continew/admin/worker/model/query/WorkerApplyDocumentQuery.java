package top.continew.admin.worker.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 作业人员报名上传的资料查询条件
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Data
@Schema(description = "作业人员报名上传的资料查询条件")
public class WorkerApplyDocumentQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业人员报名表ID
     */
    @Schema(description = "作业人员报名表ID")
    @Query(type = QueryType.EQ)
    private Long workerApplyId;
}