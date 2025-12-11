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
}