package top.continew.admin.exam.model.resp;

import lombok.Data;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;

import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2026/5/7 14:45
 */
@Data
public class NoticeUploadInfoResp implements Serializable {

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 相关学历
     */
    private String education;

    /**
     * 身份证正面照片路径
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面照片路径
     */
    private String idCardPhotoBack;

    /**
     * 人像照片路径
     */
    private String facePhoto;

    /**
     * 专业类型
     */
    private String majorType;

    /**
     * 报名状态
     */
    private Integer applyStatus;

    /**
     * 报名审核驳回原因
     */
    private String applyReason;

    /**
     * 项目必须上传资料列表(包括考生已上传、考生未上传)
     */
    private List<UploadedDocumentTypeVO> docList;

    /**
     * 项目非必须上传资料列表(包括考生已上传、考生未上传)
     */
    private List<UploadedDocumentTypeVO> optionalDocList;
}
