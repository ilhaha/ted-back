package top.continew.admin.exam.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2026/5/14 9:07
 */
@Data
public class ExamineeNoticeApplyAuditReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    private Long id;

    /**
     * 状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试
     */
    @Schema(description = "状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试")
    private Integer status;

    /**
     * 驳回意见
     */
    @Schema(description = "驳回意见")
    private String remark;
}
