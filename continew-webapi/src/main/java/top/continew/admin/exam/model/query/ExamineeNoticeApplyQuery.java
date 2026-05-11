package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生资料关系查询条件
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Data
@Schema(description = "考生资料关系查询条件")
public class ExamineeNoticeApplyQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @Query(type = QueryType.EQ)
    private Long examineeId;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @Query(type = QueryType.LIKE,columns = "su.nickname")
    private String nickname;

    /**
     * 身份证
     */
    @Schema(description = "身份证")
    @QueryIgnore
    private String username;


    /**
     * 通知ID
     */
    @Schema(description = "通知ID")
    @Query(type = QueryType.EQ,columns = "tena.notice_id")
    private Long noticeId;

    /**
     * 状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试
     */
    @Schema(description = "状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试")
    @Query(type = QueryType.EQ,columns = "tena.status")
    private Integer status;
}