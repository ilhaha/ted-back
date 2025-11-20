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

package top.continew.admin.training.service.impl;

import cn.crane4j.core.util.ObjectUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.mapper.OrgTrainingPaymentAuditMapper;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.training.model.entity.OrgTrainingPaymentAuditDO;
import top.continew.admin.training.service.OrgService;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.OrgCandidateMapper;
import top.continew.admin.training.model.entity.OrgCandidateDO;
import top.continew.admin.training.model.query.OrgCandidateQuery;
import top.continew.admin.training.model.req.OrgCandidateReq;
import top.continew.admin.training.model.resp.OrgCandidateDetailResp;
import top.continew.admin.training.model.resp.OrgCandidateResp;
import top.continew.admin.training.service.OrgCandidateService;

/**
 * 机构考生关联业务实现
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Service
@RequiredArgsConstructor
public class OrgCandidateServiceImpl extends BaseServiceImpl<OrgCandidateMapper, OrgCandidateDO, OrgCandidateResp, OrgCandidateDetailResp, OrgCandidateQuery, OrgCandidateReq> implements OrgCandidateService {

    @Resource
    private OrgService orgService;

    @Resource
    private OrgClassCandidateMapper orgClassCandidateMapper;

    @Resource
    private OrgCandidateMapper orgCandidateMapper;

    @Resource
    private OrgTrainingPaymentAuditMapper orgTrainingPaymentAuditMapper;

    /**
     * 重写分页
     * 
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<OrgCandidateResp> page(OrgCandidateQuery query, PageQuery pageQuery) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long orgId = orgService.getOrgId(userTokenDo.getUserId());
        QueryWrapper<OrgCandidateDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("toc.org_id", orgId);
        queryWrapper.eq("toc.is_deleted", 0);
        if ("add".equals(query.getType())) {
            queryWrapper.eq("toc.status", 1);
        } else {
            queryWrapper.eq("toc.status", 2);
        }
        if (ObjectUtils.isNotEmpty(query.getCandidateName())) {
            queryWrapper.like("tei.real_name", query.getCandidateName());
        }
        queryWrapper.orderByAsc("toc.create_time");
        super.sort(queryWrapper, pageQuery);
        IPage<OrgCandidateResp> page = baseMapper.getCandidatesList(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);
        PageResp<OrgCandidateResp> pageResp = PageResp.build(page, OrgCandidateResp.class);
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    /**
     * 机构审核考生加入机构
     * 
     * @param orgCandidateReq 请求参数
     * @return 审核结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean review(OrgCandidateReq orgCandidateReq) {
        Integer status = orgCandidateReq.getStatus();
        if (status == null) {
            throw new BusinessException("缺少审核状态");
        }

        Long candidateId = orgCandidateReq.getCandidateId();
        Long id = orgCandidateReq.getId();
        String remark = orgCandidateReq.getRemark();
        Long userId = TokenLocalThreadUtil.get().getUserId();

        // === 通过报名表ID查询机构和项目 ===
        OrgCandidateDO orgCandidateDO = orgCandidateMapper.selectById(id);
        if (orgCandidateDO == null) {
            throw new BusinessException("未找到报名记录");
        }

        Long orgId = orgCandidateDO.getOrgId();
        Long projectId = orgCandidateDO.getProjectId();

        // === 审核通过时必须检查缴费 ===
        if (status.equals(2)) {
            OrgTrainingPaymentAuditDO paymentInfo = orgTrainingPaymentAuditMapper
                .selectOne(new LambdaQueryWrapper<OrgTrainingPaymentAuditDO>()
                    .eq(OrgTrainingPaymentAuditDO::getOrgId, orgId)
                    .eq(OrgTrainingPaymentAuditDO::getCandidateId, candidateId)
                    .eq(OrgTrainingPaymentAuditDO::getProjectId, projectId)
                    .eq(OrgTrainingPaymentAuditDO::getEnrollId, id)
                    .eq(OrgTrainingPaymentAuditDO::getIsDeleted, 0)
                    .last("LIMIT 1"));

            if (paymentInfo == null) {
                throw new BusinessException("未找到缴费记录，请先生成缴费通知单");
            }

            // 判断缴费状态
            if (paymentInfo.getAuditStatus() == null || paymentInfo.getAuditStatus() != 2) {
                throw new BusinessException("请先完成缴费审核后再审核通过");
            }

            // === 已缴费，插入班级关联 ===
            OrgClassCandidateDO orgClassCandidateDO = new OrgClassCandidateDO();
            orgClassCandidateDO.setCandidateId(candidateId);
            orgClassCandidateDO.setClassId(orgCandidateReq.getOrClassId());
            orgClassCandidateDO.setUpdateUser(userId);
            orgClassCandidateDO.setStatus(0);
            orgClassCandidateMapper.insert(orgClassCandidateDO);
        }

        // === 更新审核状态 ===
        orgCandidateMapper.updateCandidateStatus(id, candidateId, status, remark, userId);

        return true;
    }
}