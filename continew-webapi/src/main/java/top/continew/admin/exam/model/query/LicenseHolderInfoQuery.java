package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 持证信息查询条件
 *
 * @author ilhaha
 * @since 2026/05/08 15:26
 */
@Data
@Schema(description = "持证信息查询条件")
public class LicenseHolderInfoQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @Query(type = QueryType.EQ)
    private Long examineeId;

    /**
     * 持证项目编码
     */
    @Schema(description = "持证项目编码")
    @Query(type = QueryType.EQ)
    private String projectCode;

    /**
     * 项目等级  0-无 1一级 2 二级
     */
    @Schema(description = "项目等级  0-无 1一级 2 二级")
    @Query(type = QueryType.EQ)
    private Boolean projectLevel;
}