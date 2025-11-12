package top.continew.admin.training.service;

import top.continew.admin.training.model.vo.SelectClassVO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgClassQuery;
import top.continew.admin.training.model.req.OrgClassReq;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.resp.OrgClassResp;

import java.util.List;

/**
 * 培训机构班级业务接口
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
public interface OrgClassService extends BaseService<OrgClassResp, OrgClassDetailResp, OrgClassQuery, OrgClassReq> {
    /**
     * 根据项目类型和班级类型获取班级选择器
     * @param projectId
     * @param classType
     * @return
     */
    List<SelectClassVO> getSelectClassByProject(Long projectId, Integer classType);
}