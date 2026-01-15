package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生类型参数
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Data
@Schema(description = "创建或修改考生类型参数")
public class CandidateTypeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 是否黑名单 0-否 1-是
     */
    private Boolean isBlacklist;

    /**
     * 加入黑名单原因
     */
    private String blacklistReason;

    /**
     * 黑名单时长类型 0-无 1-1天 2-1个月 3-3个月 4-6个月 5-1年 6-无期限
     */
    private Integer blacklistDurationType;
}