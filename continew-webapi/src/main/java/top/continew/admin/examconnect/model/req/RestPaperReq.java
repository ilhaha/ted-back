package top.continew.admin.examconnect.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/11/24 15:25
 * 监考员重新生成试卷请求参数
 */
@Data
public class RestPaperReq {


    @NotBlank(message = "未选择考生")
    private String examNumber;

    @NotNull(message = "未选择考生")
    private Long candidateId;

    @NotNull(message = "未选择考试计划")
    private Long planId;
}
