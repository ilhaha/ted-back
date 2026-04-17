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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.PlanConstant;
import top.continew.admin.common.constant.enums.ExamPlanStatusEnum;
import top.continew.admin.common.constant.enums.ExamPlanTypeEnum;
import top.continew.admin.common.constant.enums.NoticeAuditStatusEnum;
import top.continew.admin.config.ExamSpecialConfig;
import top.continew.admin.exam.mapper.ExamNoticePlanMapper;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.model.entity.ExamNoticePlanDO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.req.ExamNoticeAuditReq;
import top.continew.admin.exam.model.req.ExamNoticeExamProjectReq;
import top.continew.admin.exam.model.resp.*;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamNoticeMapper;
import top.continew.admin.exam.model.entity.ExamNoticeDO;
import top.continew.admin.exam.model.query.ExamNoticeQuery;
import top.continew.admin.exam.model.req.ExamNoticeReq;
import top.continew.admin.exam.service.ExamNoticeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 无损检测、检验人员考试通知业务实现
 *
 * @author ilhaha
 * @since 2026/04/14 15:20
 */
@Service
@RequiredArgsConstructor
public class ExamNoticeServiceImpl extends BaseServiceImpl<ExamNoticeMapper, ExamNoticeDO, ExamNoticeResp, ExamNoticeDetailResp, ExamNoticeQuery, ExamNoticeReq> implements ExamNoticeService {

    private final ExamSpecialConfig examSpecialConfig;

    private final ExamPlanMapper examPlanMapper;

    private final ExamNoticePlanMapper examNoticePlanMapper;

    /**
     * 重写page
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ExamNoticeResp> page(ExamNoticeQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamNoticeDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("ten.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<ExamNoticeDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        PageResp<ExamNoticeResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 重写修改
     *
     * @param req
     * @param id
     */
    @Override
    public void update(ExamNoticeReq req, Long id) {
        // 判断标题是否存在
        ValidationUtils.throwIf(baseMapper.selectCount(new LambdaQueryWrapper<ExamNoticeDO>()
                .eq(ExamNoticeDO::getTitle, req.getTitle()).ne(ExamNoticeDO::getId, id)) > 0, "标题已存在");
        Long categoryId = req.getCategoryId();
        List<ExamNoticeExamProjectReq> projectList = req.getProjectList();
        ValidationUtils.throwIf(ObjectUtil.isEmpty(projectList), "未选择报考项目");
        // 先删除之前的关系
        deleteNoticeAndPlan(CollectionUtil.toList(id));
        boolean isExamptRenew = examSpecialConfig.getExemptRenewIds().contains(categoryId);
        addExamNoticeAndPlan(req,projectList,isExamptRenew,id);
        req.setStatus(NoticeAuditStatusEnum.PENDING.getValue());
        super.update(req, id);
    }

    /**
     * 重写删除
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {

        deleteNoticeAndPlan(ids);

        // 3 最后删通知
        super.delete(ids);
    }

    private void deleteNoticeAndPlan(List<Long> ids) {
        List<ExamNoticePlanDO> relations = examNoticePlanMapper.selectList(
                new LambdaQueryWrapper<ExamNoticePlanDO>()
                        .in(ExamNoticePlanDO::getNoticeId, ids)
        );

        if (CollUtil.isNotEmpty(relations)) {

            List<Long> planIds = relations.stream()
                    .map(ExamNoticePlanDO::getPlanId)
                    .distinct()
                    .toList();

            // 1 先删关联表
            examNoticePlanMapper.deleteByIds(
                    relations.stream().map(ExamNoticePlanDO::getId).toList()
            );

            // 2 再删计划
            if (CollUtil.isNotEmpty(planIds)) {
                examPlanMapper.deleteByIds(planIds);
            }
        }
    }

    /**
     * 重写详情
     *
     * @param id
     * @return
     */
    @Override
    public ExamNoticeDetailResp get(Long id) {
        ExamNoticeDetailResp examNoticeDetailResp = super.get(id);
        // 查询当前通知对应的计划信息
        List<ExamNoticePlanDO> examNoticePlanDOS = examNoticePlanMapper.selectList(new LambdaQueryWrapper<ExamNoticePlanDO>()
                .eq(ExamNoticePlanDO::getNoticeId, id));
        List<Long> planId = examNoticePlanDOS.stream().map(ExamNoticePlanDO::getPlanId).toList();
        List<ExamPlanDO> examPlanDOS = examPlanMapper.selectByIds(planId);
        List<ExamNoticeExamProjectResp> projectList = examPlanDOS.stream().map(item -> {
            ExamNoticeExamProjectResp examNoticeExamProjectResp = new ExamNoticeExamProjectResp();
            examNoticeExamProjectResp.setExamTime(item.getStartTime());
            examNoticeExamProjectResp.setProjectId(item.getExamProjectId());
            return examNoticeExamProjectResp;
        }).toList();
        examNoticeDetailResp.setProjectList(projectList);

        return examNoticeDetailResp;
    }

    /**
     * 重写添加
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(ExamNoticeReq req) {
        ValidationUtils.throwIf(baseMapper.selectCount(new LambdaQueryWrapper<ExamNoticeDO>()
                .eq(ExamNoticeDO::getTitle, req.getTitle())) > 0, "标题已存在");
        Long categoryId = req.getCategoryId();
        List<ExamNoticeExamProjectReq> projectList = req.getProjectList();
        ValidationUtils.throwIf(ObjectUtil.isEmpty(projectList), "未选择报考项目");
        boolean isExamptRenew = examSpecialConfig.getExemptRenewIds().contains(categoryId);
        Long reqId = super.add(req);
        addExamNoticeAndPlan(req, projectList, isExamptRenew, reqId);
        return reqId;
    }

    private void addExamNoticeAndPlan(ExamNoticeReq req, List<ExamNoticeExamProjectReq> projectList, boolean isExamptRenew, Long noticeId) {
        // 添加计划
        List<ExamPlanDO> planInsertList = projectList.stream().map(item -> {
            ExamPlanDO examPlanDO = new ExamPlanDO();
            examPlanDO.setExamPlanName(req.getTitle() + " - " + item.getProjectCode());
            examPlanDO.setExamProjectId(item.getProjectId());
            LocalDateTime startTime = isExamptRenew ? LocalDateTime.now() : item.getExamTime();
            examPlanDO.setStartTime(startTime);
            examPlanDO.setPlanYear(String.valueOf(startTime.getYear()));
            examPlanDO.setMaxCandidates(0);
            examPlanDO.setPlanType(ExamPlanTypeEnum.INSPECTION.getValue());
            examPlanDO.setStatus(ExamPlanStatusEnum.IN_FORCE.getValue());
            return examPlanDO;
        }).toList();
        examPlanMapper.insertBatch(planInsertList);
        // 添加计划与通知关系表
        List<ExamNoticePlanDO> noticePlanInsertList = planInsertList.stream().map(item -> {
            ExamNoticePlanDO examNoticePlanDO = new ExamNoticePlanDO();
            examNoticePlanDO.setNoticeId(noticeId);
            examNoticePlanDO.setPlanId(item.getId());
            return examNoticePlanDO;
        }).toList();
        examNoticePlanMapper.insertBatch(noticePlanInsertList);
    }

    /**
     * 审核
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditExamNotice(ExamNoticeAuditReq req) {
        List<ExamNoticeDO> examNoticeDOS = baseMapper.selectByIds(req.getIds());
        ValidationUtils.throwIfEmpty(examNoticeDOS,"所选审核数据不存在");
        List<ExamNoticeDO> updateList = examNoticeDOS.stream().map(item -> {
            ExamNoticeDO examNoticeDO = new ExamNoticeDO();
            examNoticeDO.setId(item.getId());
            examNoticeDO.setStatus(req.getStatus());
            return examNoticeDO;
        }).toList();
        return baseMapper.updateBatchById(updateList);
    }
}