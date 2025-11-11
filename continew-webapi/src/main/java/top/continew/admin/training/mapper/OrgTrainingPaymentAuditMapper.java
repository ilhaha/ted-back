package top.continew.admin.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.OrgTrainingPaymentAuditDO;

/**
* 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程） Mapper
*
* @author ilhaha
* @since 2025/11/10 09:04
*/
public interface OrgTrainingPaymentAuditMapper extends BaseMapper<OrgTrainingPaymentAuditDO> {
    /**
     * page查询缴费审核信息表
     */
    IPage<OrgTrainingPaymentAuditResp> getTrainingPaymentAudits(@Param("page") Page<OrgTrainingPaymentAuditResp> page,
                                                             @Param(Constants.WRAPPER) QueryWrapper<OrgTrainingPaymentAuditDO> queryWrapper);

}