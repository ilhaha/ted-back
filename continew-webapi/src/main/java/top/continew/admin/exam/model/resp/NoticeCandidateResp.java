package top.continew.admin.exam.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2026/5/6 16:47
 */
@Data
public class NoticeCandidateResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学历认证状态（0待审、1已认证、2认证未通过、3待认证）
     */
    private Integer educationVerifyStatus;

    /**
     * 学历
     */
    private String education;

}
