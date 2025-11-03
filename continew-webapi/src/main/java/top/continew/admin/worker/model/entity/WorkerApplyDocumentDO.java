package top.continew.admin.worker.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 作业人员报名上传的资料实体
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Data
@TableName("ted_worker_apply_document")
public class WorkerApplyDocumentDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业人员报名表ID
     */
    private Long workerApplyId;

    /**
     * 资料存储路径
     */
    private String docPath;

    /**
     * 关联资料类型ID
     */
    private Long typeId;

    /**
     * 删除标记(0未删,1已删)
     */
    private Integer isDeleted;
}