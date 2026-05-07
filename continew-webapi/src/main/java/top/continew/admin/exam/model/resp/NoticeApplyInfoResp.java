package top.continew.admin.exam.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2026/5/6 15:35
 */
@Data
public class NoticeApplyInfoResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 备注
     */
    private String remark;

    /**
     * 报名截止时间
     */
    @Schema(description = "报名截止时间")
    private LocalDate applyDeadline;


    /**
     * 考试等级 0-无 1一级 2 二级
     */
    @Schema(description = "考试等级  0-无 1一级 2 二级")
    private Integer examLevel;

    /**
     * 通知绑定的项目
     */
    private List<NoticeProjectResp> projectRespList;

    /**
     * 考生信息
     */
    private NoticeCandidateResp noticeCandidateResp;

    /**
     * 考生与通知的关系
     */
    private ExamineeNoticeApplyResp examineeNoticeApplyResp;
}
