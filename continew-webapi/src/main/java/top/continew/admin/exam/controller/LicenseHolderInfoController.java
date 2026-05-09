package top.continew.admin.exam.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.LicenseHolderInfoQuery;
import top.continew.admin.exam.model.req.LicenseHolderInfoReq;
import top.continew.admin.exam.model.resp.LicenseHolderInfoDetailResp;
import top.continew.admin.exam.model.resp.LicenseHolderInfoResp;
import top.continew.admin.exam.service.LicenseHolderInfoService;

import java.util.List;

/**
 * 持证信息管理 API
 *
 * @author ilhaha
 * @since 2026/05/08 15:26
 */
@Tag(name = "持证信息管理 API")
@RestController
@CrudRequestMapping(value = "/exam/licenseHolderInfo", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class LicenseHolderInfoController extends BaseController<LicenseHolderInfoService, LicenseHolderInfoResp, LicenseHolderInfoDetailResp, LicenseHolderInfoQuery, LicenseHolderInfoReq> {

    /**
     * 保存用户持证信息
     * @param reqs
     * @return
     */
    @PostMapping("/user")
    @SaCheckPermission("exam:licenseHolderInfo:add")
    public Boolean saveLicenseHolderInfo(@RequestBody List<LicenseHolderInfoReq> reqs) {
        return baseService.saveLicenseHolderInfo(reqs);
    }

    /**
     * 获取当前用户的持证信息
     * @return
     */
    @GetMapping("/user")
    public List<LicenseHolderInfoResp> getInfoByUser(){
        return baseService.getInfoByUser();
    }
}