package top.continew.admin.worker.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 作业人员报名实体
 *
 * @author ilhaha
 * @since 2025/11/03 11:15
 */
@Data
@TableName("ted_worker_apply")
public class WorkerApplyDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 作业人员姓名
     */
    private String candidateName;

    /**
     * 作业人员性别
     */
    private String gender;

    /**
     * 作业人员手机号
     */
    private String phone;

    /**
     * 报名资格申请表路径
     */
    private String qualificationPath;

    /**
     * 报名资格申请表名称
     */
    private String qualificationName;

    /**
     * 身份证号
     */
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    private String facePhoto;

    /**
     * 审核状态:0待审核,1已生效,2未通过
     */
    private Integer status;

    /**
     * 删除标记(0未删,1已删)
     */
    private Integer isDeleted;
}