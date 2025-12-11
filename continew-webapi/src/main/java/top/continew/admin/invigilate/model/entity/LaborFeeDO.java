package top.continew.admin.invigilate.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 考试劳务费配置实体
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Data
@TableName("ted_labor_fee")
public class LaborFeeDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 实操考试劳务费单价（元）
     */
    private BigDecimal practicalFee;

    /**
     * 理论考试劳务费单价（元）
     */
    private BigDecimal theoryFee;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用：1启用 0禁用
     */
    private Boolean isEnabled;
}