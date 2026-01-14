package top.continew.admin.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.training.model.resp.CandidateTypeDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.CandidateTypeDO;

import java.util.List;

/**
* 考生类型 Mapper
*
* @author ilhaha
* @since 2026/01/14 11:16
*/
public interface CandidateTypeMapper extends BaseMapper<CandidateTypeDO> {
    void insertBatchIgnore(@Param("list") List<CandidateTypeDO> candidateTypes);

    IPage<CandidateTypeDetailResp> getWorkerPage(@Param("page") Page<Object> objectPage,
                                                 @Param(Constants.WRAPPER) QueryWrapper<CandidateTypeDO> queryWrapper);

}