package top.continew.admin.exam.model.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ilhaha
 * @Create 2026/5/6 16:35
 */
@Data
public class NoticeProjectResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 项目代号
     */
    private String projectCode;

    /**
     * 项目考试等级（ 0无 1一级 2 二级）
     */
    private Integer projectLevel;

    /**
     * 是否有实操考试（0无，1有）
     */
    private Integer isOperation;

    /**
     * 是否有理论考试（0无，1有）
     */
    private Integer isTheory;

    /**
     * 项目收费标准
     */
    private Long examFee;

    /**
     * 考试场次类型（0既包含初试也包含补考，1只有初试，2只有补考，3无）
     */
    private Integer examAttemptType;

    /**
     * 实操类型（0默认实操，1拍片，2评片，3拍片+评片，4无）
     */
    private Integer practicalType;

    /**
     * 考试计划
     */
    private Long planId;

    /**
     * 考试时间
     */
    private LocalDateTime startTime;
}
