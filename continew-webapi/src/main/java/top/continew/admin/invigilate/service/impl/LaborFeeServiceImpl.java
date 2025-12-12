package top.continew.admin.invigilate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import top.continew.starter.core.exception.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.invigilate.mapper.LaborFeeMapper;
import top.continew.admin.invigilate.model.entity.LaborFeeDO;
import top.continew.admin.invigilate.model.query.LaborFeeQuery;
import top.continew.admin.invigilate.model.req.LaborFeeReq;
import top.continew.admin.invigilate.model.resp.LaborFeeDetailResp;
import top.continew.admin.invigilate.model.resp.LaborFeeResp;
import top.continew.admin.invigilate.service.LaborFeeService;

/**
 * 考试劳务费配置业务实现
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Service
@RequiredArgsConstructor
public class LaborFeeServiceImpl extends BaseServiceImpl<LaborFeeMapper, LaborFeeDO, LaborFeeResp, LaborFeeDetailResp, LaborFeeQuery, LaborFeeReq> implements LaborFeeService {


    @Autowired
    private LaborFeeMapper laborFeeMapper;

    /**
     * 分页查询考试劳务费配置
     *
     * @param query     查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public PageResp<LaborFeeResp> page(LaborFeeQuery query, PageQuery pageQuery) {
        Page<LaborFeeDO> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());

        LambdaQueryWrapper<LaborFeeDO> wrapper = Wrappers.<LaborFeeDO>lambdaQuery()
                // 按查询条件过滤
                .eq(query.getIsEnabled() != null, LaborFeeDO::getIsEnabled, query.getIsEnabled())

                // 启用数据永远置顶
                .orderByDesc(LaborFeeDO::getIsEnabled)
                // 按更新时间降序
                .orderByDesc(LaborFeeDO::getUpdateTime);

        Page<LaborFeeDO> result = laborFeeMapper.selectPage(page, wrapper);
        return PageResp.build(result, LaborFeeResp.class);
    }

    /**
     * 更新劳务费状态
     *
     * @param req 劳务费实体
     * @return 更新结果
     */
    @Override
    public boolean toggleLaborFeeEnabled(LaborFeeReq req) {

        boolean targetEnabled = Boolean.TRUE.equals(req.getIsEnabled());
        Long currentId = req.getId();

        // 启用逻辑
        if (targetEnabled) {
            // 禁用其他已启用的记录
            LambdaUpdateWrapper<LaborFeeDO> updateWrapper = Wrappers.<LaborFeeDO>lambdaUpdate()
                    .eq(LaborFeeDO::getIsEnabled, true)
                    .ne(currentId != null, LaborFeeDO::getId, currentId);

            LaborFeeDO updateDO = new LaborFeeDO();
            updateDO.setIsEnabled(false); // 不要链式调用
            laborFeeMapper.update(updateDO, updateWrapper);
        } else {
            // 禁止全部禁用
            Long enabledCount = laborFeeMapper.selectCount(
                    Wrappers.<LaborFeeDO>lambdaQuery().eq(LaborFeeDO::getIsEnabled, true)
            );
            if (enabledCount == 1) {
                LaborFeeDO onlyEnabled = laborFeeMapper.selectOne(
                        Wrappers.<LaborFeeDO>lambdaQuery()
                                .eq(LaborFeeDO::getIsEnabled, true)
                                .last("LIMIT 1")
                );
                if (onlyEnabled != null && onlyEnabled.getId().equals(currentId)) {
                    throw new BusinessException("必须至少保留一条启用的配置，不能全部禁用");
                }
            }
        }

        // 更新当前记录
        LaborFeeDO laborFeeDO = convertToEntity(req);
        laborFeeDO.setIsEnabled(targetEnabled);
        return laborFeeMapper.updateById(laborFeeDO) > 0;
    }

    /**
     * 将请求对象转换为实体对象
     *
     * @param req 劳务费请求对象
     * @return 劳务费实体对象
     */
    private LaborFeeDO convertToEntity(LaborFeeReq req) {
        LaborFeeDO laborFeeDO = new LaborFeeDO();
        laborFeeDO.setId(req.getId());
        laborFeeDO.setPracticalFee(req.getPracticalFee());
        laborFeeDO.setTheoryFee(req.getTheoryFee());
        laborFeeDO.setRemark(req.getRemark());
        laborFeeDO.setIsEnabled(req.getIsEnabled());
        return laborFeeDO;

    }
    @Override
    public Long add(LaborFeeReq req) {
        LaborFeeDO laborFeeDO = convertToEntity(req);
        // 默认不启用
        laborFeeDO.setIsEnabled(false);
        laborFeeMapper.insert(laborFeeDO);
        return laborFeeDO.getId();
    }

}
