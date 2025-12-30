package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 人员复审信息表信息
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
@Data
@Schema(description = "人员复审信息表信息")
public class PersonQualificationResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String name;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    private String idCard;

    /**
     * 文化程度
     */
    @Schema(description = "文化程度")
    private String education;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String phone;

    /**
     * 聘用单位
     */
    @Schema(description = "聘用单位")
    private String employer;

    /**
     * 资格项目代码
     */
    @Schema(description = "资格项目代码")
    private String qualificationCategoryCode;



    /**
     * 审核状态：0-待审核，1-审核通过，2-审核不通过
     */
    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核不通过")
    private Integer auditStatus;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;
}