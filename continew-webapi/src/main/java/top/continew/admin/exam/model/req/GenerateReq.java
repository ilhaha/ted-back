package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/12/25 9:07
 */
@Data
public class GenerateReq {

    /**
     * 考试记录id
     */
    @NotEmpty(message = "未选择考试记录")
    private List<Long> recordIds;

    /**
     * 计划类型，0-作业人员 1-检验人员
     */
    @NotNull(message = "未填写计划类型")
    private Integer planType;

}

