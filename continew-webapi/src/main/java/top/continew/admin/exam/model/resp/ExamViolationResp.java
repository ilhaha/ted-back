package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考试劳务费配置信息
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Data
@Schema(description = "考试劳务费配置信息")
public class ExamViolationResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    private Long planId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long candidateId;

    /**
     * 违规描述
     */
    @Schema(description = "违规描述")
    private String violationDesc;

    /**
     * 违规图片
     */
    @Schema(description = "违规图片")
    private String illegalUrl;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private Long updateUser;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}