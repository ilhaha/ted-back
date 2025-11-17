package top.continew.admin.training.model.dto;

import lombok.Data;

@Data
public class CheckinRequest {
    private String realName;
    private String idCard;
    private Long trainingId;
    private Long orgId;
    private Long ts;
    private String sign;
}

