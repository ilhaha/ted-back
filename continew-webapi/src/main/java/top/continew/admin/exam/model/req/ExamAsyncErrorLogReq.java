package top.continew.admin.exam.model.req;


import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考试异步任务错误日志参数
 *
 * @author ilhaha
 * @since 2026/04/09 15:04
 */
@Data
@Schema(description = "创建或修改考试异步任务错误日志参数")
public class ExamAsyncErrorLogReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    private Long planId;

    /**
     * 报名ID
     */
    @Schema(description = "报名ID")
    private Long enrollId;

    /**
     * 失败步骤
     */
    @Schema(description = "失败步骤")
    @Length(max = 50, message = "失败步骤长度不能超过 {max} 个字符")
    private String step;

    /**
     * 错误简要信息
     */
    @Schema(description = "错误简要信息")
    @Length(max = 65535, message = "错误简要信息长度不能超过 {max} 个字符")
    private String errorMsg;

    /**
     * 异常堆栈信息
     */
    @Schema(description = "异常堆栈信息")
    @Length(max = 65535, message = "异常堆栈信息长度不能超过 {max} 个字符")
    private String stackTrace;

    /**
     * 处理状态：0-未处理，1-已处理
     */
    @Schema(description = "处理状态：0-未处理，1-已处理")
    private Integer status;
}