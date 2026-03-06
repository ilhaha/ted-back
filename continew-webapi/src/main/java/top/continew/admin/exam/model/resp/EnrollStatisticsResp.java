package top.continew.admin.exam.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import top.continew.admin.common.enums.GenderEnum;
import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 报名统计-报名成功考生信息
 *
 * @author hoshi
 * @since 2026/03/06
 */
@Data
@Schema(description = "报名统计-报名成功考生信息")
public class EnrollStatisticsResp   {


    /**
     * 报名ID
     */
    @Schema(description = "报名ID")
    private Long id;

    /**
     * 考试项目ID
     */
    @Schema(description = "考试项目ID")
    private String examProjectId;

    /**
     * 考试项目名称
     */
    @Schema(description = "考试项目名称")
    private String examProjectName;

    /**
     * 考试项目代码
     */
    @Schema(description = "考试项目代码")
    private String projectCode;

    /**
     * 项目类别
     */
    @Schema(description = "项目类别")
    private String categoryName;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    private String examPlanId;

    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    private String examPlanName;

    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间")
    private LocalDateTime examStartTime;


    /**
     * 报名截止时间
     */
    @Schema(description = "报名截止时间")
    private LocalDateTime enrollEndTime;

    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    private String nickName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    private String username;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private GenderEnum gender;

    /**
     * 准考证号
     */
    @Schema(description = "准考证号")
    private String examNumber;

    /**
     * 考生单位名称
     */
    @Schema(description = "考生单位名称")
    private String companyName;

     /**
     * 考生电话号码
     */
    @Schema(description = "考生电话号码")
    private String phone;

}