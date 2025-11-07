package top.continew.admin.exam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.continew.admin.exam.model.entity.SpecialCertificationApplicantDO;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;

/**
* 考生缴费审核 Mapper
*
* @author ilhaha
* @since 2025/11/04 10:17
*/
public interface ExamineePaymentAuditMapper extends BaseMapper<ExamineePaymentAuditDO> {
    /**
     * page查询缴费审核信息表
     */
    IPage<ExamineePaymentAuditResp> getExamineePaymentAudits(@Param("page") Page<ExamineePaymentAuditResp> page,
                                                             @Param(Constants.WRAPPER) QueryWrapper<ExamineePaymentAuditDO> queryWrapper);


    /**
     * 取消报名，更改缴费审核
     */
    @Update("UPDATE ted.ted_examinee_payment_audit SET audit_status = 6, is_deleted = 1 WHERE exam_plan_id = #{examPlanId} AND examinee_id = #{userId}")
    void deleteFromPayment(@Param("examPlanId") Long examPlanId, @Param("userId") Long userId);


}