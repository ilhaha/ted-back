package top.continew.admin.exam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.resp.WeldingExamApplicationDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.WeldingExamApplicationDO;

/**
* 机构申请焊接考试项目 Mapper
*
* @author ilhaha
* @since 2026/01/16 10:58
*/
public interface WeldingExamApplicationMapper extends BaseMapper<WeldingExamApplicationDO> {

    IPage<WeldingExamApplicationDetailResp> orgAndAdminPage(@Param("page") Page<Object> objectPage,
                                                            @Param(Constants.WRAPPER) QueryWrapper<WeldingExamApplicationDO> queryWrapper);
}