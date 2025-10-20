package top.continew.admin.exam.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamIdcardQuery;
import top.continew.admin.exam.model.req.ExamIdcardReq;
import top.continew.admin.exam.model.resp.ExamIdcardDetailResp;
import top.continew.admin.exam.model.resp.ExamIdcardResp;
import top.continew.admin.exam.service.ExamIdcardService;

/**
 * 考生身份证信息管理 API
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
@Tag(name = "考生身份证信息管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examIdcard", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class ExamIdcardController extends BaseController<ExamIdcardService, ExamIdcardResp, ExamIdcardDetailResp, ExamIdcardQuery, ExamIdcardReq> {

    /**
     * 考生根据身份证号查看是否已实名
     * @param username
     * @return
     */
    @SaIgnore
    @GetMapping("/verifyRealName")
    public Boolean verifyRealName(String username) {
        return baseService.verifyRealName(username);
    }

    /**
     * 添加实名验证
     * @param examIdcardReq
     * @return
     */
    @SaIgnore
    @PostMapping("/save/realName")
    public Long saveRealName(@RequestBody ExamIdcardReq examIdcardReq) {
        return baseService.saveRealName(examIdcardReq);
    }
}