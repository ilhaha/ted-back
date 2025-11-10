package top.continew.admin.training.model.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 机构培训价格新增/修改请求DTO（数据传输对象，接收前端参数）
 */
@Data
public class OrgTrainingPriceDTO {

    private Long id; 

    private Long orgId; // 关联机构表主键

    @NotNull(message = "八大类项目ID不能为空")
    private Long projectId; // 关联八大类项目字典表主键

    @NotNull(message = "培训价格不能为空")
    @DecimalMin(value = "0.01", message = "培训价格必须大于0元") // 最小0.01元（避免0或负数）
    @Digits(integer = 8, fraction = 2, message = "培训价格整数部分最多8位，小数部分最多2位") // 金额格式限制
    private BigDecimal price; // 培训价格
}