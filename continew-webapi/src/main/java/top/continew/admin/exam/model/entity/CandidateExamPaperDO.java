package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考生试卷实体
 *
 * @author ilhaha
 * @since 2025/11/19 16:05
 */
@Data
@TableName("ted_candidate_exam_paper")
public class CandidateExamPaperDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    private Long enrollId;

    /**
     * 试卷 JSON 内容
     */
    private String paperJson;

    /**
     * 删除标记(0未删,1已删)
     */
    private Integer isDeleted;
}