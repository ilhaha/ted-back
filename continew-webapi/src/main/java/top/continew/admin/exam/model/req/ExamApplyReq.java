package top.continew.admin.exam.model.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.continew.admin.exam.model.req.dto.ProjectApplyDTO;

import java.math.BigDecimal;
import java.util.List;

// 请求类
@Data
public class ExamApplyReq {

    /**
     * 通知ID
     */
    @NotNull(message = "未选择报考通知")
    private Long noticeId;


    /**
     * projectList
     */
    @Valid
    @NotEmpty(message = "未选择报考项目")
    private List<ProjectApplyDTO> projectList;
}

