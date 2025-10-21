package top.continew.admin.training.model.query;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 机构考生关联查询条件
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Data
@Schema(description = "机构考生关联查询条件")
public class OrgCandidateQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;



    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    @QueryIgnore
    private String candidateName;

    /**
     * 机构对应的项目id
     */
    @Schema(description = "机构对应的项目id")
    @Query(type = QueryType.EQ)
    private Long projectId;

    /**
     * 查询类型
     */
    @Schema(description = "查询类型")
    @QueryIgnore
    private String type;


}