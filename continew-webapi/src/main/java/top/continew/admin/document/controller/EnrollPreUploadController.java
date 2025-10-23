package top.continew.admin.document.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.document.model.req.QrcodeUploadReq;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.document.model.query.EnrollPreUploadQuery;
import top.continew.admin.document.model.req.EnrollPreUploadReq;
import top.continew.admin.document.model.resp.EnrollPreUploadDetailResp;
import top.continew.admin.document.model.resp.EnrollPreUploadResp;
import top.continew.admin.document.service.EnrollPreUploadService;

/**
 * 机构报考-考生扫码上传文件管理 API
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Tag(name = "机构报考-考生扫码上传文件管理 API")
@RestController
@CrudRequestMapping(value = "/document/enrollPreUpload", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class EnrollPreUploadController extends BaseController<EnrollPreUploadService, EnrollPreUploadResp, EnrollPreUploadDetailResp, EnrollPreUploadQuery, EnrollPreUploadReq> {


    /**
     * 通过二维码上传上传考生资料
     *
     * @return
     */
    @SaIgnore
    @PostMapping("/qrcode/upload")
    public Boolean qrcodeUpload(@Validated @RequestBody QrcodeUploadReq qrcodeUploadReq) {
        return baseService.qrcodeUpload(qrcodeUploadReq);
    }

}