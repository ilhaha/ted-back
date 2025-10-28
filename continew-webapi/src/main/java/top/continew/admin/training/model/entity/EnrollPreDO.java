package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 机构考生预报名实体
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
@Data
@TableName("ted_enroll_pre")
public class EnrollPreDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    private Long orgId;


    /**
     * 考生id
     */
    private Long candidateId;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 上传资料二维码
     */
    private String uploadQrcode;

    /**
     * 资料上传状态 0-资料待补充 1-报考资料已齐全
     */
    private Integer status;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    private Integer isDeleted;
}