package top.continew.admin.document.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 机构报考-考生上传资料查询条件
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@Schema(description = "机构报考-考生上传资料查询条件")
public class DocumentPreQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构报考-考生扫码上传文件表id
     */
    @Schema(description = "机构报考-考生扫码上传文件表id")
    @Query(type = QueryType.EQ)
    private Long enrollPreUploadId;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    @Query(type = QueryType.EQ)
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    @Query(type = QueryType.EQ)
    private Long typeId;
}