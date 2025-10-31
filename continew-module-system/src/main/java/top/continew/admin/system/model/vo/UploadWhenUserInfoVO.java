package top.continew.admin.system.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/21 18:41
 */
@Data
public class UploadWhenUserInfoVO implements Serializable {
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 考生未上传的资料集合
     */
    private List<UploadedDocumentTypeVO> unuploadedDocumentTypes;

    /**
     * 考生已上传的资料集合
     */
    private List<UploadedDocumentTypeVO> uploadedDocumentTypes;

    /**
     * 计划信息
     */
    private PlanInfoVO planInfoVO;

    /**
     * 预报名信息
     */
    private EnrollPreInfoVO enrollPreInfoVO;
}
