package top.continew.admin.worker.service;

import top.continew.admin.worker.model.req.*;
import top.continew.admin.worker.model.resp.WorkerApplyVO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.worker.model.query.WorkerApplyQuery;
import top.continew.admin.worker.model.resp.WorkerApplyDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyResp;

import java.util.List;

/**
 * 作业人员报名业务接口
 *
 * @author ilhaha
 * @since 2025/10/31 10:20
 */
public interface WorkerApplyService extends BaseService<WorkerApplyResp, WorkerApplyDetailResp, WorkerApplyQuery, WorkerApplyReq> {

    /**
     * 根据身份证后六位、和班级id查询当前身份证报名信息
     * @param verifyReq
     * @return
     */
    WorkerApplyVO verify(VerifyReq verifyReq);

    /**
     * 作业人员通过二维码上传资料
     * @param workerQrcodeUploadReq
     * @return
     */
    Boolean submit(WorkerQrcodeUploadReq workerQrcodeUploadReq);

    /**
     * 审核作业人员报考
     * @return
     */
    Boolean review(WorkerApplyReviewReq workerApplyReviewReq);

    /**
     * 作业人员通过二维码重新上传资料
     * @param workerQrcodeUploadReq
     * @return
     */
    Boolean restSubmit(WorkerQrcodeUploadReq workerQrcodeUploadReq);

    /**
     * 机构批量导入
     * @param workerOrgImportReqs
     * @return
     */
    Boolean orgImport(List<WorkerOrgImportReq> workerOrgImportReqs);
}