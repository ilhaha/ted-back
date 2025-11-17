package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import top.continew.starter.security.crypto.annotation.FieldEncrypt;

import java.io.Serial;
import java.time.*;

/**
 * 机构考生关联信息
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Data
@Schema(description = "机构考生关联信息")
public class OrgCandidateResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID
     */
    @Schema(description = "机构ID")
    private Long orgId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long candidateId;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private Long projectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;


    /**
     * 状态 (负1-拒绝, 0-退出，1-待通过，2-已加入)
     */
    @Schema(description = "状态 (负1-拒绝, 0-退出，1-待通过，2-已加入)")
    private Integer status;

//    @FieldEncrypt
    @Schema(description = "考生手机号")
    private String phoneNumber;

    @Schema(description = "考生姓名")
    private String nickName;

    @Schema(description = "实名信息")
    private String idCardPhotos;

}