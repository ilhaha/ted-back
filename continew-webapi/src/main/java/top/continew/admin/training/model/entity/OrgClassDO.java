package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 培训机构班级实体
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Data
@TableName("ted_org_class")
public class OrgClassDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级类型，0作业人员班级，1检验人员班级
     */
    private Integer classType;

    /**
     * 作业人员扫码报考二维码
     */
    private String qrcodeApplyUrl;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDeleted;
}