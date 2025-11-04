package top.continew.admin.worker.model.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/31 11:26
 */
@Data
public class WorkerApplyVO implements Serializable {

    /**
     * 项目所需提交的报名资料
     */
    private List<ProjectNeedUploadDocVO> projectNeedUploadDocs;

    /**
     * 作业人员已上传的资料
     */
    private WorkerUploadedDocsVO workerUploadedDocs;
}
