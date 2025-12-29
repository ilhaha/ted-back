package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 人员复审信息表查询条件
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
@Data
@Schema(description = "人员复审信息表查询条件")
public class PersonQualificationQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @Query(type = QueryType.EQ)
    private String name;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @Query(type = QueryType.EQ)
    private String idCard;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    @Query(type = QueryType.EQ)
    private Long createUser;
}