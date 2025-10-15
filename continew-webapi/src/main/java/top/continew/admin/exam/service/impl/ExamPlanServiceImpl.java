/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.PlanConstant;
import top.continew.admin.common.util.DateUtil;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ClassroomMapper;
import top.continew.admin.exam.mapper.ExamRecordsMapper;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.exam.model.req.ExamPlanSaveReq;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.invigilate.mapper.PlanInvigilateMapper;
import top.continew.admin.invigilate.model.entity.PlanInvigilateDO;
import top.continew.admin.invigilate.model.enums.InvigilateStatus;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.query.ExamPlanQuery;
import top.continew.admin.exam.model.req.ExamPlanReq;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.resp.ExamPlanResp;
import top.continew.admin.exam.service.ExamPlanService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 考试计划业务实现
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Service
@RequiredArgsConstructor
public class ExamPlanServiceImpl extends BaseServiceImpl<ExamPlanMapper, ExamPlanDO, ExamPlanResp, ExamPlanDetailResp, ExamPlanQuery, ExamPlanReq> implements ExamPlanService {

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private PlanInvigilateMapper planInvigilateMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Override
    public PageResp<ExamPlanResp> page(ExamPlanQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamPlanDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tep.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<ExamPlanDetailResp> page = baseMapper.selectExamPlanPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        page.getRecords().forEach(item -> item.setStatusString(PlanConstant.getStatusString(item.getStatus())));

        PageResp<ExamPlanResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    @Override
    public ExamPlanDetailResp get(Long id) {
        ExamPlanDetailResp examPlanDetailResp = baseMapper.selectDetailById(id);
        this.fill(examPlanDetailResp);
        return examPlanDetailResp;
    }

    @Transactional
    @Override
    public void save(ExamPlanSaveReq examPlanSaveReq) {
        // 禁止计划重名
        ExamPlanDO examPlanName = baseMapper.selectOne(new QueryWrapper<ExamPlanDO>()
            .eq("exam_plan_name", examPlanSaveReq.getExamPlanName())
            .eq("is_deleted", 0));
        ValidationUtils.throwIfNotNull(examPlanName, "考试计划名称已存在");
        ExamPlanDO examPlanDO = getExamPlanDO(examPlanSaveReq);
        ValidationUtils.throwIf(!DateUtil.validateEnrollmentTime(examPlanDO.getEnrollEndTime(), examPlanDO
            .getStartTime()), "报名结束时间不能晚于考试开始时间");
        // 判断计划考试时间对应的考场是否空闲
        ValidationUtils.throwIf(hasClassroomTimeConflict(examPlanDO.getStartTime(),
                examPlanDO.getEndTime(),
                examPlanSaveReq.getClassroomId()) > 0,
                "所选考场在所选时间段前后30分钟内已有考试计划，请调整考试时间或更换考场。");
        BeanUtils.copyProperties(examPlanSaveReq, examPlanDO);
        //填充最大人数字段 根据考场id获取最大人数
        List<Long> maxCandidates = classroomMapper.getMaxCandidates(examPlanSaveReq.getClassroomId());
        long count = maxCandidates.stream().mapToLong(Long::longValue).sum();

        examPlanDO.setMaxCandidates(Math.toIntExact(count));
        this.save(examPlanDO);
        // 添加计划和考场关联表
        examPlanMapper.savePlanClassroom(examPlanDO.getId(), examPlanSaveReq.getClassroomId());
    }

    /**
     * 判读考试计划的考试时间对应的考场是否冲突
     * @param startTime
     * @param endTime
     * @param classroomId
     * @return
     */
    private Integer hasClassroomTimeConflict(LocalDateTime startTime, LocalDateTime endTime,  List<Long> classroomId) {
        return baseMapper.hasClassroomTimeConflict(startTime,endTime,classroomId);
    }

    /**
     * 时间转换
     */
    private ExamPlanDO getExamPlanDO(ExamPlanSaveReq examPlanSaveReq) {
//        List<String> dateRange = examPlanSaveReq.getDateRange();
        List<String> enrollList = examPlanSaveReq.getEnrollList();
        ExamPlanDO examPlanDO = new ExamPlanDO();
        // 年份
//        examPlanDO.setPlanYear(dateRange.get(0).substring(0, dateRange.get(0).indexOf("-")));
        examPlanDO.setPlanYear(String.valueOf(examPlanSaveReq.getStartTime().getYear()));
        // 报名时间
        examPlanDO.setEnrollStartTime(DateUtil.parse(enrollList.get(0)));
        examPlanDO.setEnrollEndTime(DateUtil.parse(enrollList.get(1)));
        // 考试结束时间
//        examPlanDO.setStartTime(DateUtil.parse(dateRange.get(0)));
//        examPlanDO.setEndTime(DateUtil.parse(dateRange.get(1)));
        ProjectDO projectDO = projectMapper.selectById(examPlanSaveReq.getExamProjectId());
        examPlanDO.setStartTime(examPlanSaveReq.getStartTime());
        examPlanDO.setEndTime(examPlanSaveReq.getStartTime().plusMinutes(projectDO.getExamDuration()));
        return examPlanDO;
    }

    @Override
    public String valid(Long examPlanId, Integer status) {

        ValidationUtils.throwIfNull(examPlanId, "考试计划字段为空");

        Long userId = TokenLocalThreadUtil.get().getUserId();

        QueryWrapper<ExamPlanDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", examPlanId);
        ExamPlanDO examPlanDO = examPlanMapper.selectOne(queryWrapper);

        ValidationUtils.throwIfNull(examPlanDO, "考试计划不存在");

        String approvedUsers = examPlanDO.getApprovedUsers();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        ExamPlanDO examPlanResp = new ExamPlanDO();
        BeanUtils.copyProperties(examPlanDO, examPlanResp);
        //        status == 0  未通过
        if (status == 0) {
            if (approvedUsers == null || approvedUsers.isEmpty()) {
                StringBuilder approveUser = new StringBuilder(userId.toString());
                examPlanResp.setApprovedUsers(approveUser.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.FAIL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划,主任未通过";
            } else {
                String existApproveUser = examPlanDO.getApprovedUsers();
                StringBuilder existApproveUserBuilder = new StringBuilder(existApproveUser);

                StringBuilder approveUser = new StringBuilder(userId.toString());
                existApproveUserBuilder.append(",").append(approveUser);

                examPlanResp.setApprovedUsers(existApproveUserBuilder.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.FAIL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划,市监局未通过";
            }
        }

        //        status == 1
        if (status == 1) {
            if (approvedUsers == null || approvedUsers.isEmpty()) {
                StringBuilder approveUser = new StringBuilder(userId.toString());
                examPlanResp.setApprovedUsers(approveUser.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.AUDIT_SUPERVISION_APPROVAL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划主任通过,待市监局审核";
            } else {
                String existApproveUser = examPlanDO.getApprovedUsers();
                StringBuilder existApproveUserBuilder = new StringBuilder(existApproveUser);

                StringBuilder approveUser = new StringBuilder(userId.toString());
                existApproveUserBuilder.append(",").append(approveUser);

                examPlanResp.setApprovedUsers(existApproveUserBuilder.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.SUCCESS.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划主任,市监局都通过";
            }

        }
        return "";
    }

    @Override
    public List<ExamPlanDO> getAllList() {
        return examPlanMapper.selectAllList();
    }

    /**
     * 获取考试计划考场
     * 
     * @param planId
     * @return
     */
    @Override
    public List<Long> getPlanExamClassroom(Long planId) {
        return examPlanMapper.getPlanExamClassroom(planId);
    }

    /**
     * 修改考试计划考场
     * 
     * @param planId
     * @param classroomId
     * @return
     */
    @Override
    public String updatePlanExamClassroom(Long planId, List<Long> classroomId) {
        examPlanMapper.deletePLanExamClassroom(planId);
        examPlanMapper.savePlanClassroom(planId, classroomId);
        return null;
    }

    @Override
    public Integer selectClassroomId(Integer planId) {
        return examPlanMapper.selectClassroomById(planId);
    }

    @Override
    @Transactional
    public Boolean endExam(Long planId) {
        // 需改考试计划状态
        LambdaUpdateWrapper<ExamPlanDO> examPlanDOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        examPlanDOLambdaUpdateWrapper.eq(ExamPlanDO::getId, planId)
            .set(ExamPlanDO::getStatus, PlanConstant.OVER.getStatus());
        // 修改监考员状态
        LambdaUpdateWrapper<PlanInvigilateDO> planInvigilateDOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        planInvigilateDOLambdaUpdateWrapper.eq(PlanInvigilateDO::getExamPlanId, planId)
            .set(PlanInvigilateDO::getInvigilateStatus, InvigilateStatus.PENDING_REVIEW.getCode());
        planInvigilateMapper.update(planInvigilateDOLambdaUpdateWrapper);
        return this.update(examPlanDOLambdaUpdateWrapper);
    }

    @Override
    public List<ProjectVo> getSelectOptions() {
        List<ProjectVo> projectVoList = examPlanMapper.getSelectOptions();
        for (ProjectVo projectVo : projectVoList) {
            projectVo.setDisabled(false);
        }
        return projectVoList;
    }

    @Override
    public void update(ExamPlanReq req, Long id) {
        List<String> enrollList = req.getEnrollList();
        List<String> dateRangeList = req.getDateRange();
        req.setEnrollStartTime(DateUtil.parse(enrollList.get(0)));
        req.setEnrollEndTime(DateUtil.parse(enrollList.get(1)));

        req.setStartTime(DateUtil.parse(dateRangeList.get(0)));
        req.setEndTime(DateUtil.parse(dateRangeList.get(1)));

        if (!DateUtil.validateEnrollmentTime(req.getEnrollEndTime(), req.getStartTime()))
            ValidationUtils.validate("报名结束时间不能晚于考试开始时间");

        super.update(req, id);
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        QueryWrapper<PlanInvigilateDO> wrapper = new QueryWrapper<PlanInvigilateDO>();
        wrapper.in("exam_plan_id", ids);
        planInvigilateMapper.delete(wrapper);
        //删除监考计划关联表
        super.delete(ids);
    }
}