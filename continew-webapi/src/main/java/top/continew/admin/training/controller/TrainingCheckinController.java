package top.continew.admin.training.controller;
import cn.dev33.satoken.annotation.SaIgnore;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import top.continew.admin.training.model.dto.CheckinRequest;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.TrainingCheckinQuery;
import top.continew.admin.training.model.req.TrainingCheckinReq;
import top.continew.admin.training.model.resp.TrainingCheckinDetailResp;
import top.continew.admin.training.model.resp.TrainingCheckinResp;
import top.continew.admin.training.service.TrainingCheckinService;
import top.continew.starter.web.model.R;

import java.util.Map;

/**
 * 培训签到记录管理 API
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
@Tag(name = "培训签到记录管理 API")
@RestController
@CrudRequestMapping(value = "/training/trainingCheckin", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class TrainingCheckinController extends BaseController<TrainingCheckinService, TrainingCheckinResp, TrainingCheckinDetailResp, TrainingCheckinQuery, TrainingCheckinReq> {


    @Autowired
    private TrainingCheckinService trainingCheckinService;

    /**
     * 生成签到二维码地址
     */
    @GetMapping("/qrcode")
    public Object generateQRCode(@RequestParam Long trainingId) {

        String url = trainingCheckinService.generateQRCode(trainingId);

        return Map.of("url", url);
    }

    /**
     * H5 签到
     */
    @SaIgnore
    @PostMapping("/do")
    public Object doCheckin(@RequestBody CheckinRequest req) {
        boolean ok = trainingCheckinService.doCheckin(
                req.getRealName(),
                req.getIdCard(),
                req.getTrainingId(),
                req.getOrgId(),
                req.getTs(),
                req.getSign()
        );

        return Map.of("success", ok, "msg", "签到成功");
    }

    /** 导出培训签到记录（Excel） */
    @GetMapping("/exportExcel")
    public void exportExcel(TrainingCheckinQuery query, HttpServletResponse response) {
        trainingCheckinService.exportExcel(query, response);
    }

}