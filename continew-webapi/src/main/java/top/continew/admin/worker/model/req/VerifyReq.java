package top.continew.admin.worker.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/10/31 11:20
 */
@Data
public class VerifyReq {

    @NotBlank(message = "身份证后六位未填写")
    private String idLast6;

    @NotNull(message = "二维码错误")
    private Long classId;
}
