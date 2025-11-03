package top.continew.admin.worker.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 作业人员报名查询条件
 *
 * @author ilhaha
 * @since 2025/11/03 11:15
 */
@Data
@Schema(description = "作业人员报名查询条件")
public class WorkerApplyQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    @Query(type = QueryType.EQ)
    private Long classId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    @Query(type = QueryType.EQ)
    private String candidateName;

    /**
     * 作业人员性别
     */
    @Schema(description = "作业人员性别")
    @Query(type = QueryType.EQ)
    private String gender;

    /**
     * 作业人员手机号
     */
    @Schema(description = "作业人员手机号")
    @Query(type = QueryType.EQ)
    private String phone;

    /**
     * 报名资格申请表名称
     */
    @Schema(description = "报名资格申请表名称")
    @Query(type = QueryType.EQ)
    private String qualificationName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @Query(type = QueryType.EQ)
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    @Schema(description = "身份证正面存储地址")
    @Query(type = QueryType.EQ)
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    @Schema(description = "身份证反面存储地址")
    @Query(type = QueryType.EQ)
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    @Schema(description = "一寸免冠照存储地址")
    @Query(type = QueryType.EQ)
    private String facePhoto;

    /**
     * 审核状态:0待审核,1已生效,2未通过
     */
    @Schema(description = "审核状态:0待审核,1已生效,2未通过")
    @Query(type = QueryType.EQ)
    private Integer status;
}