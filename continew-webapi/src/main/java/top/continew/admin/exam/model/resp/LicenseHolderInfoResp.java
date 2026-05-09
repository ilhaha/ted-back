package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 持证信息信息
 *
 * @author ilhaha
 * @since 2026/05/08 15:26
 */
@Data
@Schema(description = "持证信息信息")
public class LicenseHolderInfoResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long examineeId;

    /**
     * 持证项目编码
     */
    @Schema(description = "持证项目编码")
    private String projectCode;

    /**
     * 项目等级  0-无 1一级 2 二级
     */
    @Schema(description = "项目等级  0-无 1一级 2 二级")
    private Integer projectLevel;

    /**
     * 有效开始日期
     */
    @Schema(description = "有效开始日期")
    private LocalDate validStartDate;

    /**
     * 有效结束日期
     */
    @Schema(description = "有效结束日期")
    private LocalDate validEndDate;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间戳
     */
    @Schema(description = "更新时间戳")
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    private Boolean isDeleted;
}