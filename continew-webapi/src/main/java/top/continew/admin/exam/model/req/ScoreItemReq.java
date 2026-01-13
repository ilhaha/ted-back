package top.continew.admin.exam.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "单条成绩信息")
public class ScoreItemReq {

    @NotNull(message = "考试记录ID不能为空")
    @Schema(description = "考试记录ID")
    private Long recordId;

    @NotNull(message = "成绩不能为空")
    @Min(value = 0, message = "成绩不能小于0")
    @Max(value = 100, message = "成绩不能大于100")
    @Schema(description = "成绩")
    private Integer scores;
}