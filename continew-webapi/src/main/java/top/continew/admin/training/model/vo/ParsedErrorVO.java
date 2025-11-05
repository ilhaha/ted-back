package top.continew.admin.training.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.continew.starter.security.mask.annotation.JsonMask;
import top.continew.starter.security.mask.enums.MaskType;

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2025/11/5 16:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParsedErrorVO implements Serializable {

    private Integer rowNum;

    private String excelName;

    @JsonMask(MaskType.MOBILE_PHONE)
    private String phone;

    private String errorMessage;
}
