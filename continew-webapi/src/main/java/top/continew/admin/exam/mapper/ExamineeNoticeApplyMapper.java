package top.continew.admin.exam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.entity.ExamNoticeDO;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.ExamineeNoticeApplyDO;

/**
 * 考生资料关系 Mapper
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
public interface ExamineeNoticeApplyMapper extends BaseMapper<ExamineeNoticeApplyDO> {

    IPage<ExamineeNoticeApplyDetailResp> page(@Param("page") Page<Object> objectPage,
                                              @Param(Constants.WRAPPER) QueryWrapper<ExamineeNoticeApplyDO> queryWrapper);

}