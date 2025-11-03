package top.continew.admin.worker.model.req;


import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改作业人员报名上传的资料参数
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Data
@Schema(description = "创建或修改作业人员报名上传的资料参数")
public class WorkerApplyDocumentReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}