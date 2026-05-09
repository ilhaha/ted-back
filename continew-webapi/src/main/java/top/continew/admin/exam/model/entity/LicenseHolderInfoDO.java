package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 持证信息实体
 *
 * @author ilhaha
 * @since 2026/05/08 15:26
 */
@Data
@TableName("ted_license_holder_info")
public class LicenseHolderInfoDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    private Long examineeId;

    /**
     * 持证项目编码
     */
    private String projectCode;

    /**
     * 项目等级  0-无 1一级 2 二级
     */
    private Integer projectLevel;

    /**
     * 有效开始日期
     */
    private LocalDate validStartDate;

    /**
     * 有效结束日期
     */
    private LocalDate validEndDate;

    /**
     * 删除标记
     */
    private Boolean isDeleted;
}