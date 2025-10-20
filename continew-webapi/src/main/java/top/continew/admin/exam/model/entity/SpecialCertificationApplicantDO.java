package top.continew.admin.exam.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 特种设备人员资格申请实体
 *
 * 支持两种申报方式：
 * - 培训机构批量上传（applySource = 0，有 batchId）
 * - 考生个人单独报名（applySource = 1，无 batchId）
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
@Data
@TableName("ted_special_certification_applicant")
public class SpecialCertificationApplicantDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 考生ID
     */
    private Long candidatesId;

    /**
     * 所属培训/考试计划ID
     */
    private Long planId;

    /**
     * 班级批次ID（机构批量上传时关联，个人报名时为空）
     */
    private Long batchId;

    /**
     * 申请表或电子资料URL
     */
    private String imageUrl;

    /**
     * 审核状态：
     * 0：未审核
     * 1：审核通过
     * 2：退回补正
     * 3：虚假资料（禁止再次申报）
     */
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    private String remark;

    /**
     * 申报来源：
     * 0：培训机构批量上传
     * 1：考生个人申报
     */
    private Integer applySource;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除（0：否，1：是）
     */
    private Boolean isDeleted;

    /**
     * 计划名称（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String planName;
}