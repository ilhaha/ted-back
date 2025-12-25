package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 人员及许可证书信息查询条件
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
@Data
@Schema(description = "人员及许可证书信息查询条件")
public class LicenseCertificateQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @Query(type = QueryType.EQ)
    private String psnName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @Query(type = QueryType.EQ)
    private String idcardNo;
}