package top.continew.admin.invigilate.service;

import top.continew.admin.invigilate.model.dto.UserQualificationDTO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.invigilate.model.query.UserQualificationQuery;
import top.continew.admin.invigilate.model.req.UserQualificationReq;
import top.continew.admin.invigilate.model.resp.UserQualificationDetailResp;
import top.continew.admin.invigilate.model.resp.UserQualificationResp;

import java.util.List;

/**
 * 监考员资质证明业务接口
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
public interface UserQualificationService extends BaseService<UserQualificationResp, UserQualificationDetailResp, UserQualificationQuery, UserQualificationReq> {


    /**
     * 根据用户ID查询资质列表
     */
    List<UserQualificationDTO> listByUserId(Long userId);


    /**
     * 添加资质证明
     */
    boolean addQualification(UserQualificationReq req);

}