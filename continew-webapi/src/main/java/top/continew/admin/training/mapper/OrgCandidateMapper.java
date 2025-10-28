package top.continew.admin.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.training.model.resp.OrgCandidateResp;
import top.continew.admin.training.model.resp.OrgCandidatesResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.OrgCandidateDO;

/**
* 机构考生关联 Mapper
*
* @author ilhaha
* @since 2025/10/21 13:52
*/
public interface OrgCandidateMapper extends BaseMapper<OrgCandidateDO> {
    IPage<OrgCandidateResp> getCandidatesList(@Param("page") Page<Object> objectPage, @Param(Constants.WRAPPER) QueryWrapper<OrgCandidateDO> queryWrapper);

    // 修改机构考生表状态
    void updateCandidateStatus(@Param("id") Long id, @Param("candidateId") Long candidateId, @Param("status") Integer status, @Param("remark") String remark, @Param("updateUser") Long updateUser);

}