package top.continew.admin.exam.model.req;


import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 创建或修改考生缴费审核参数
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Data
@Schema(description = "创建或修改考生缴费审核参数")
public class ExamineePaymentAuditReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}