package top.continew.admin.worker.model.resp;

import lombok.Data;
import org.checkerframework.common.value.qual.EnsuresMinLenIf;

import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2026/1/7 17:25
 */
@Data
public class DocDetailResp implements Serializable {

    /**
     * 报名资格申请表路径
     */
    private String qualificationPath;

    /**
     * 报名资格申请表名称
     */
    private String qualificationName;

    /**
     * 身份证住址
     */
    private String idCardAddress;

    /**
     * 身份证号
     */
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    private String facePhoto;

    // 资料集合
    private List<ProjectNeedUploadDocVO> uploadedDocs;
}
