package top.continew.admin.training.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.OrgCandidateQuery;
import top.continew.admin.training.model.req.OrgCandidateReq;
import top.continew.admin.training.model.resp.OrgCandidateDetailResp;
import top.continew.admin.training.model.resp.OrgCandidateResp;
import top.continew.admin.training.service.OrgCandidateService;

/**
 * 机构考生关联管理 API
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Tag(name = "机构考生关联管理 API")
@RestController
@CrudRequestMapping(value = "/training/orgCandidate", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class OrgCandidateController extends BaseController<OrgCandidateService, OrgCandidateResp, OrgCandidateDetailResp, OrgCandidateQuery, OrgCandidateReq> {

    /**
     * 机构审核考生加入机构
     * @param orgCandidateReq
     * @return
     */
    @PostMapping("/review")
    public Boolean review(@RequestBody OrgCandidateReq orgCandidateReq) {
        return baseService.review(orgCandidateReq);
    }
}