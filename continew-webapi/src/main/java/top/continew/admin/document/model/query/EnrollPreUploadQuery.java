package top.continew.admin.document.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 机构报考-考生扫码上传文件查询条件
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@Schema(description = "机构报考-考生扫码上传文件查询条件")
public class EnrollPreUploadQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    @Query(type = QueryType.LIKE,columns = "su.nickname")
    private Long candidatesName;

    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    @Query(type = QueryType.LIKE,columns = "ep.exam_plan_name")
    private String planName;

    /**
     * 机构id
     */
    @Schema(description = "机构id")
    @Query(type = QueryType.EQ,columns = "org.id")
    private Long orgId;
}