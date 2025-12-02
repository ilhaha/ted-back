package top.continew.admin.invigilate.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 监考员资质证明查询条件
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Data
@Schema(description = "监考员资质证明查询条件")
public class UserQualificationQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @Query(type = QueryType.EQ)
    private Long userId;

    /**
     * 八大类ID
     */
    @Schema(description = "八大类ID")
    @Query(type = QueryType.EQ)
    private Long categoryId;

    /**
     * 资质证明URL
     */
    @Schema(description = "资质证明URL")
    @Query(type = QueryType.EQ)
    private String qualificationUrl;
}