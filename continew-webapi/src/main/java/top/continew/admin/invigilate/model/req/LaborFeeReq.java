package top.continew.admin.invigilate.model.req;


import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 创建或修改考试劳务费配置参数
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Data
@Schema(description = "创建或修改考试劳务费配置参数")
public class LaborFeeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 实操考试劳务费单价（元）
     */
    @Schema(description = "实操考试劳务费单价（元）")
    private BigDecimal practicalFee;
    /**
     * 理论考试劳务费单价（元）
     */
    @Schema(description = "理论考试劳务费单价（元）")
    private BigDecimal theoryFee;
    /**
     * 备注
     */
    @Schema(description = "备注")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
    /**
     * 是否启用：1启用 0禁用
     */
    @Schema(description = "是否启用：1启用 0禁用")
    private Boolean isEnabled;
}