package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生身份证信息查询条件
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
@Data
@Schema(description = "考生身份证信息查询条件")
public class ExamIdcardQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @Query(type = QueryType.EQ)
    private String realName;

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    @Query(type = QueryType.EQ)
    private String idCardNumber;
}