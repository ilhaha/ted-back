package top.continew.admin.worker.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 作业人员准考证实体
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Data
@TableName("ted_worker_exam_ticket")
public class WorkerExamTicketDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    private Long enrollId;

    /**
     * 作业人员姓名
     */
    private String candidateName;

    /**
     * 准考证地址
     */
    private String ticketUrl;

    /**
     * 删除标记(0未删,1已删)
     */
    private Integer isDeleted;
}