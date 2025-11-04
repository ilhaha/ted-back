package top.continew.admin.worker.model.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/11/4 9:34
 */
@Data
public class WorkerUploadedDocsVO {

    /** 报名ID */
    private Long id;

    /** 考生姓名 */
    private String candidateName;

    /** 资质文件路径 */
    private String qualificationPath;

    /** 资质名称 */
    private String qualificationName;

    /** 身份证正面照片 */
    private String idCardPhotoFront;

    /** 身份证反面照片 */
    private String idCardPhotoBack;

    /** 备注 */
    private String remark;

    /** 人脸照片 */
    private String facePhoto;

    /** 状态 */
    private Integer status;

    /** 材料列表 */
    @JsonIgnore
    private String documents;

    /**资料列表*/
    private List<WorkerApplyDocumentVO> workerApplyDocuments;
}
