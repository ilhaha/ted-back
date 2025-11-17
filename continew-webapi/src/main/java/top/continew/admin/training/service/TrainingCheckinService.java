package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.TrainingCheckinQuery;
import top.continew.admin.training.model.req.TrainingCheckinReq;
import top.continew.admin.training.model.resp.TrainingCheckinDetailResp;
import top.continew.admin.training.model.resp.TrainingCheckinResp;

/**
 * 培训签到记录业务接口
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
public interface TrainingCheckinService extends BaseService<TrainingCheckinResp, TrainingCheckinDetailResp, TrainingCheckinQuery, TrainingCheckinReq> {
    String generateQRCode(Long trainingId);

    boolean doCheckin(String realName, String idCard, Long trainingId, Long orgId, Long ts, String sign);
}