package top.continew.admin.exam.model.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 考试计划-项目缴费信息 DTO
 * 封装查询后的考试计划名称、项目名称、缴费金额
 */
@Data
public class ExamPlanProjectPaymentDTO {
    /** 考试计划名称 */
    private String examPlanName;
    /** 项目名称 */
    private String projectName;
    /** 缴费金额（元） */
    private BigDecimal paymentAmount;

    public ExamPlanProjectPaymentDTO(String examPlanName, String projectName, BigDecimal paymentAmount) {
        this.examPlanName = examPlanName;
        this.projectName = projectName;
        this.paymentAmount = paymentAmount;
    }
}