package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 人员及许可证书信息实体
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
@Data
@TableName("ted_license_certificate")
public class LicenseCertificateDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据来源
     */
    private String datasource;

    /**
     * 信息录入单位
     */
    private String infoinputorg;

    /**
     * 姓名
     */
    private String psnName;

    /**
     * 身份证号
     */
    private String idcardNo;

    /**
     * 原单位名称
     */
    private String originalComName;

    /**
     * 单位名称
     */
    private String comName;

    /**
     * 申请类型
     */
    private Integer applyType;

    /**
     * 申请日期
     */
    private LocalDate applyDate;

    /**
     * 是否审核
     */
    private Integer isVerify;

    /**
     * 是否操作
     */
    private Integer isOpr;

    /**
     * 证书类别
     */
    private String lcnsKind;

    /**
     * 证书分类
     */
    private String lcnsCategory;

    /**
     * 证书编号
     */
    private String lcnsNo;

    /**
     * 证书签发日期
     */
    private LocalDate certDate;

    /**
     * 授权日期
     */
    private LocalDate authDate;

    /**
     * 证书有效期
     */
    private LocalDate endDate;

    /**
     * 原授权单位
     */
    private String originalAuthCom;

    /**
     * 授权单位
     */
    private String authCom;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 证书项目名称
     */
    private String psnlcnsItem;

    /**
     * 证书项目代码
     */
    private String psnlcnsItemCode;

    /**
     * 许可范围
     */
    private String permitScope;

    /**
     * 明细备注
     */
    private String detailRemark;

    /**
     * 明细状态
     */
    private Integer detailState;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Integer isDeleted;
}