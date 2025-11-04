package top.continew.admin.worker.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 作业人员报名查询条件
 *
 * @author ilhaha
 * @since 2025/11/03 11:15
 */
@Data
@Schema(description = "作业人员报名查询条件")
public class WorkerApplyQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    @Query(type = QueryType.EQ,columns = "twa.class_id")
    private Long classId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    @Query(type = QueryType.LIKE,columns = "twa.candidate_name")
    private String candidateName;


    /**
     * 审核状态:0待审核,1已生效,2未通过
     */
    @Schema(description = "审核状态:0待审核,1已生效,2未通过")
    @Query(type = QueryType.EQ,columns = "twa.status")
    private Integer status;
}