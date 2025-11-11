package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/11/11 15:45
 * 扫码获取缴费信息请求参数
 */
@Data
public class PaymentInfoReq {

    /** 考试计划ID */
    @NotBlank(message = "二维码已被篡改或参数缺失，请重新获取")
    private String planId;

    /** 考生ID */
    @NotBlank(message = "二维码已被篡改或参数缺失，请重新获取")
    private String candidateId;

    /** 报名ID */
    @NotBlank(message = "二维码已被篡改或参数缺失，请重新获取")
    private String enrollId;

    /** 班级ID */
    @NotBlank(message = "二维码已被篡改或参数缺失，请重新获取")
    private String classId;
}
