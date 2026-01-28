package top.continew.admin.exam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.resp.PlanApplyClassDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.PlanApplyClassDO;

/**
* 考试计划报考班级 Mapper
*
* @author ilhaha
* @since 2026/01/28 09:17
*/
public interface PlanApplyClassMapper extends BaseMapper<PlanApplyClassDO> {

    IPage<PlanApplyClassDetailResp> selectExamPlanPage(@Param("page") Page<Object> objectPage,
                                                       @Param(Constants.WRAPPER) QueryWrapper<PlanApplyClassDO> queryWrapper);
}