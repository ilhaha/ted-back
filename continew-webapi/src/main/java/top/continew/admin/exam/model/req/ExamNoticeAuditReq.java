package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2026/4/17 8:39
 */
@Data
@ToString
public class ExamNoticeAuditReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 审核id
     */
    @NotEmpty(message = "未选择审核的通知")
    private List<Long> ids;

    /**
     * 审核状态
     */
    @NotNull(message = "未选择审核结果")
    private Integer status;
}
