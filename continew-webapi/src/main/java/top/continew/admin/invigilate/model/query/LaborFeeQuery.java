package top.continew.admin.invigilate.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 考试劳务费配置查询条件
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Data
@Schema(description = "考试劳务费配置查询条件")
public class LaborFeeQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 是否启用：1启用 0禁用
     */
    @Schema(description = "是否启用：1启用 0禁用")
    private Boolean isEnabled;
}