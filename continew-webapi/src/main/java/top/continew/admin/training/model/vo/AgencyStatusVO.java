package top.continew.admin.training.model.vo;

import lombok.Data;

@Data
public class AgencyStatusVO {
    private Long Id;
    private Long orgId;
    private Integer paymentStatus;
    private Integer status;
    private String remark;
}
