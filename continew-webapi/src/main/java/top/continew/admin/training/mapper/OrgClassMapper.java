package top.continew.admin.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.vo.ProjectCategoryVO;
import top.continew.admin.training.model.vo.SelectClassVO;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.OrgClassDO;

import java.util.List;

/**
* 培训机构班级 Mapper
*
* @author ilhaha
* @since 2025/10/17 17:43
*/
public interface OrgClassMapper extends BaseMapper<OrgClassDO> {
    IPage<OrgClassDetailResp> page(@Param("page") Page page, @Param("ew") QueryWrapper<OrgClassDO> queryWrapper);

    List<ProjectCategoryVO> getSelectClassByProjectIds(List<Long> projectIds);

    List<SelectClassVO> getSelectClassByProject(@Param("projectId") Long projectId, @Param("classType") Integer classType);
}