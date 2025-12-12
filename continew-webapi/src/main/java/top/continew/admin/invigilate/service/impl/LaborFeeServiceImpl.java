package top.continew.admin.invigilate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.continew.starter.core.exception.BusinessException;
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
     * 更新劳务费状态
     *
     * @param req 劳务费实体
     * @return 更新结果
     */
    @Override
    public boolean toggleLaborFeeEnabled(LaborFeeReq req) {

        // 如果要启用，先检查是否已存在其他启用的数据
        if (Boolean.TRUE.equals(req.getIsEnabled())) {
            Long count = laborFeeMapper.selectCount(
                    new LambdaQueryWrapper<LaborFeeDO>()
                            .eq(LaborFeeDO::getIsEnabled, true)
                            .ne(LaborFeeDO::getId, req.getId())
            );

            if (count != null && count > 0) {
                throw new BusinessException("已存在启用的劳务费配置，只能启用一条");
            }
        }
        // 更新数据
        LaborFeeDO laborFeeDO = convertToEntity(req);
        laborFeeDO.setIsEnabled(req.getIsEnabled());

        int rows = laborFeeMapper.updateById(laborFeeDO);
        return rows > 0;
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
