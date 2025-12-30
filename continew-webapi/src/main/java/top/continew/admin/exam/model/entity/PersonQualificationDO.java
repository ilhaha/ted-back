package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 人员复审信息表实体
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
@Data
@TableName("ted_person_qualification")
public class PersonQualificationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 文化程度
     */
    private String education;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 聘用单位
     */
    private String employer;

    /**
     * 资格项目代码
     */
    private String qualificationCategoryCode;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核不通过
     */
    private Integer auditStatus;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Boolean isDeleted;
}