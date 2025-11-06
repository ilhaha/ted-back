package top.continew.admin.worker.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.worker.model.req.*;
import top.continew.admin.worker.model.resp.WorkerApplyVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.worker.model.query.WorkerApplyQuery;
import top.continew.admin.worker.model.resp.WorkerApplyDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyResp;
import top.continew.admin.worker.service.WorkerApplyService;

import java.util.List;

/**
 * 作业人员报名管理 API
 *
 * @author ilhaha
 * @since 2025/10/31 10:20
 */
@Tag(name = "作业人员报名管理 API")
@RestController
@CrudRequestMapping(value = "/worker/workerApply", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class WorkerApplyController extends BaseController<WorkerApplyService, WorkerApplyResp, WorkerApplyDetailResp, WorkerApplyQuery, WorkerApplyReq> {

    /**
     * 机构批量导入
     * @param workerOrgImportReqs
     * @return
     */
    @PostMapping("/org/import")
    public Boolean orgImport(@RequestBody List<WorkerOrgImportReq> workerOrgImportReqs){
        return baseService.orgImport(workerOrgImportReqs);
    }

    /**
     * 审核作业人员报考
     * @return
     */
    @PostMapping("/review")
    public Boolean review(@Validated @RequestBody WorkerApplyReviewReq workerApplyReviewReq){
        return baseService.review(workerApplyReviewReq);
    }

    /**
     * 作业人员通过二维码重新上传资料
     * @param workerQrcodeUploadReq
     * @return
     */
    @SaIgnore
    @PostMapping("/rest/submit")
    public Boolean restSubmit(@Validated @RequestBody WorkerQrcodeUploadReq workerQrcodeUploadReq) {
        return baseService.restSubmit(workerQrcodeUploadReq);
    }

    /**
     * 作业人员通过二维码上传资料
     * @param workerQrcodeUploadReq
     * @return
     */
    @SaIgnore
    @PostMapping("/submit")
    public Boolean submit(@Validated @RequestBody WorkerQrcodeUploadReq workerQrcodeUploadReq) {
        return baseService.submit(workerQrcodeUploadReq);
    }

    /**
     * 根据身份证后六位、和班级id查询当前身份证报名信息
     * @param verifyReq
     * @return
     */
    @SaIgnore
    @PostMapping("/verify")
    public WorkerApplyVO verify(@Validated @RequestBody VerifyReq verifyReq){
        return baseService.verify(verifyReq);
    }
}