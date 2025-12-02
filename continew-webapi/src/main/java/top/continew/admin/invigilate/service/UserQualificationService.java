package top.continew.admin.invigilate.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.invigilate.model.query.UserQualificationQuery;
import top.continew.admin.invigilate.model.req.UserQualificationReq;
import top.continew.admin.invigilate.model.resp.UserQualificationDetailResp;
import top.continew.admin.invigilate.model.resp.UserQualificationResp;

/**
 * 监考员资质证明业务接口
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
public interface UserQualificationService extends BaseService<UserQualificationResp, UserQualificationDetailResp, UserQualificationQuery, UserQualificationReq> {}