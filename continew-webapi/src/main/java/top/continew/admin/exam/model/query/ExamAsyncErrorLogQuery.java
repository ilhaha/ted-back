package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考试异步任务错误日志查询条件
 *
 * @author ilhaha
 * @since 2026/04/09 15:04
 */
@Data
@Schema(description = "考试异步任务错误日志查询条件")
public class ExamAsyncErrorLogQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Query(type = QueryType.EQ, columns = "status")
    private Integer status;
}