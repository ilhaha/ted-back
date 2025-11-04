package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamineePaymentAuditMapper;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.query.ExamineePaymentAuditQuery;
import top.continew.admin.exam.model.req.ExamineePaymentAuditReq;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditDetailResp;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
import top.continew.admin.exam.service.ExamineePaymentAuditService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考生缴费审核业务实现
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Service
@RequiredArgsConstructor
public class ExamineePaymentAuditServiceImpl extends BaseServiceImpl<ExamineePaymentAuditMapper, ExamineePaymentAuditDO, ExamineePaymentAuditResp, ExamineePaymentAuditDetailResp, ExamineePaymentAuditQuery, ExamineePaymentAuditReq> implements ExamineePaymentAuditService {


    @Resource
    private ExamineePaymentAuditMapper examineePaymentAuditMapper;

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ProjectMapper examProjectMapper;



    /**
     * 考生查看缴费审核表信息
     *
     * @param examineeId
     * @return
     */
    @Override
    public Boolean verifyPaymentAudit(Long examineeId) {
        QueryWrapper<ExamineePaymentAuditDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("examinee_id", examineeId);
        return baseMapper.selectCount(queryWrapper) > 0;
    }


    /**
     * 报名审核通过生成缴费审核表
     *
     */
    @Override
    public void generatePaymentAudit(Long examPlanId, Long examineeId, Long enrollId) {
        // 前置校验
        ValidationUtils.throwIfNull(examPlanId, "考试计划ID不能为空");
        ValidationUtils.throwIfNull(examineeId, "考生ID不能为空");
        ValidationUtils.throwIfNull(enrollId, "报名ID不能为空");

        // 查询考试计划，获取项目ID
        ExamPlanDO examPlanDO = examPlanMapper.selectOne(
                new LambdaQueryWrapper<ExamPlanDO>()
                        .eq(ExamPlanDO::getId, examPlanId)
                        .eq(ExamPlanDO::getIsDeleted, false)
                        .select(ExamPlanDO::getExamProjectId)
                        .last("LIMIT 1")
        );
        ValidationUtils.throwIfNull(examPlanDO, "考试计划不存在");

        Long examProjectId = examPlanDO.getExamProjectId();
        ValidationUtils.throwIfNull(examProjectId, "考试计划未关联项目ID，无法生成缴费记录");

        // 查询项目缴费金额
        ProjectDO projectDO = examProjectMapper.selectOne(
                new LambdaQueryWrapper<ProjectDO>()
                        .eq(ProjectDO::getId, examProjectId)
                        .eq(ProjectDO::getIsDeleted, false)
                        .select(ProjectDO::getExamFee)
                        .last("LIMIT 1")
        );
        ValidationUtils.throwIfNull(projectDO, "考试项目不存在");

        BigDecimal paymentAmount = BigDecimal.valueOf(projectDO.getExamFee());
        ValidationUtils.throwIfNull(paymentAmount, "项目未设置缴费金额，无法生成缴费记录");

        //  构建缴费审核记录并入库
        ExamineePaymentAuditDO paymentAuditDO = new ExamineePaymentAuditDO();
        paymentAuditDO.setExamineeId(examineeId);
        paymentAuditDO.setExamPlanId(examPlanId);
        paymentAuditDO.setEnrollId(enrollId);
        paymentAuditDO.setPaymentAmount(paymentAmount);
        paymentAuditDO.setAuditStatus(0); // 待缴费状态
        paymentAuditDO.setCreateTime(LocalDateTime.now());
        paymentAuditDO.setIsDeleted(false);

        int insertCount = examineePaymentAuditMapper.insert(paymentAuditDO);
        ValidationUtils.throwIf(insertCount <= 0, "生成缴费审核记录失败");
    }

    /**
     * 重写page方法
     *
     */
    @Override
    public PageResp<ExamineePaymentAuditResp> page(ExamineePaymentAuditQuery query, PageQuery pageQuery) {
        // 构建查询条件
        QueryWrapper<ExamineePaymentAuditDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tepa.is_deleted", 0);
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);
        // 执行分页查询
        IPage<ExamineePaymentAuditDO> page = baseMapper.getExamineePaymentAudits(
                new Page<>(pageQuery.getPage(), pageQuery.getSize()),
                queryWrapper
        );
        // 将查询结果转换成 PageResp 对象
        PageResp<ExamineePaymentAuditResp> pageResp = PageResp.build(page, super.getListClass());
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

}
