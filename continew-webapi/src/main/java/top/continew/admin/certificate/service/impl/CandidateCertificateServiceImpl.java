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

package top.continew.admin.certificate.service.impl;

import cn.crane4j.core.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.certificate.mapper.ReexamineMapper;
import top.continew.admin.certificate.model.dto.ReexaminationDTO;
import top.continew.admin.certificate.model.entity.ReexamineDO;
import top.continew.admin.common.constant.ErrorMessageConstant;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.util.Result;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.certificate.mapper.CandidateCertificateMapper;
import top.continew.admin.certificate.model.entity.CandidateCertificateDO;
import top.continew.admin.certificate.model.query.CandidateCertificateQuery;
import top.continew.admin.certificate.model.req.CandidateCertificateReq;
import top.continew.admin.certificate.model.resp.CandidateCertificateDetailResp;
import top.continew.admin.certificate.model.resp.CandidateCertificateResp;
import top.continew.admin.certificate.service.CandidateCertificateService;

import java.util.List;

/**
 * 考生证件业务实现
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Service
@RequiredArgsConstructor
public class CandidateCertificateServiceImpl extends BaseServiceImpl<CandidateCertificateMapper, CandidateCertificateDO, CandidateCertificateResp, CandidateCertificateDetailResp, CandidateCertificateQuery, CandidateCertificateReq> implements CandidateCertificateService {
    //需要获取的信息 当前考生id 证件种类id 关联获取项目名称
    //判断当前用户角色 如果是监管机构和管理员 输出所有考生的所有证件信息 并不用提供报名按钮
    //如果是考生 输出当前考生的所有证件信息 并未尚未拥有的证书提供报名按钮
    @Resource
    private CandidateCertificateMapper candidateCertificateMapper;

    @Resource
    private ReexamineMapper reexamineMapper;

    //    /**
    //     * 考生分页获取考生证件列表
    //     * @param query     查询条件
    //     * @param pageQuery 分页查询条件
    //     * @return
    //     */
    //    public PageResp<CandidateCertificateResp> page(CandidateCertificateQuery query, PageQuery pageQuery) {
    //        //获取当前用户信息
    //        UserTokenDo userInfo = TokenLocalThreadUtil.get();
    //        ValidationUtils.throwIfNull(userInfo, ErrorMessageConstant.USER_AUTHENTICATION_FAILED);
    //
    //        // 1. 分页查询主数据
    //        QueryWrapper<CandidateCertificateDO> queryWrapper = buildQueryWrapper(query);
    //        super.sort(queryWrapper, pageQuery);
    //        IPage<CandidateCertificateResp> page = baseMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper,userInfo.getUserId());
    //
    //        PageResp<CandidateCertificateResp> result = PageResp.build(page, super.getListClass());
    //        result.getList().forEach(candidateCertificateResp -> {
    //            //填充考生名称
    //            candidateCertificateResp.setCandidateName(userInfo.getNickname());
    //            if(candidateCertificateResp.getCertificateStatus()==0){//未持有对应证书
    //
    //            }
    //        });
    //        //3. 填充其他数据
    //        result.getList().forEach(this::fill);
    //        return result;
    //}

    /**
     * 管理员分页获取考生证件列表
     * 
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return
     */
    @Override
    public PageResp<CandidateCertificateResp> page(CandidateCertificateQuery query, PageQuery pageQuery) {
        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        ValidationUtils.throwIfNull(userInfo, ErrorMessageConstant.USER_AUTHENTICATION_FAILED);

        // 1. 分页查询主数据
        QueryWrapper<CandidateCertificateDO> queryWrapper = this.buildQueryWrapper(query);
        //查询条件 只查询未删除的证书信息
        queryWrapper.eq("tcc.is_deleted", 0);
        if (!(query.getCertificateName() == null)) {
            queryWrapper.like("tct.certificate_name", query.getCertificateName());
        }
        if (!(query.getProjectName() == null)) {
            queryWrapper.like("p.projectName", query.getCertificateStatus());
        }
        super.sort(queryWrapper, pageQuery);
        IPage<CandidateCertificateResp> page;
        //不是管理员只能看到当前父部门以下的证书信息
        if (userInfo.getUserId() == 1L) {
            page = baseMapper.selectAllPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
        } else {

            //循环查找
            Long parentDeptId = userInfo.getParentDeptId();
            Long temp = null;
            while (parentDeptId == 1L) {
                temp = parentDeptId;
                parentDeptId = baseMapper.selectParentDeptId(parentDeptId);
            }
            queryWrapper.eq("p.dept_id", temp);
            page = baseMapper.selectDeptPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper, temp);
        }

        PageResp<CandidateCertificateResp> result = PageResp.build(page, super.getListClass());
        //3. 填充其他数据
        result.getList().forEach(this::fill);
        return result;
    }

    @Override
    public PageResp<CandidateCertificateResp> getCandidateCertificateList(CandidateCertificateQuery query,
                                                                          PageQuery pageQuery) {
        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        QueryWrapper<CandidateCertificateDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tcc.is_deleted", 0);
        queryWrapper.ne("tcc.certificate_status", 0);
        queryWrapper.eq("tcc.candidate_id", userInfo.getUserId());

        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询
        IPage<CandidateCertificateResp> page = baseMapper.getCandidateCertificateList(new Page<>(pageQuery
            .getPage(), pageQuery.getSize()), queryWrapper);

        // 将查询结果转换成 PageResp 对象
        PageResp<CandidateCertificateResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;
    }

    @Override
    public List<CandidateCertificateResp> getUserCertificate() {
        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        return baseMapper.getUserCertificate(userInfo.getUserId());
    }

    @Override
    public List<CandidateCertificateResp> getUserCertificateList(String candidateId) {
        //直接通过前端返回的id查询证书库
        return baseMapper.getUserCertificate(Long.valueOf(candidateId));
    }

    @Override
    public Result submitReexamination(ReexaminationDTO request) {

        //1.判断是否已经提交过（通过个人id和证件id）
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();

        QueryWrapper<ReexamineDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("certificate_id", request.getCertificateId());
        queryWrapper.eq("applicant_id", userId);
        if (reexamineMapper.selectOne(queryWrapper) != null) {
            return Result.error("已经提交过该证件复审表，无需再次提交");
        }
        // 2. 参数校验
        if (StringUtils.isEmpty(request.getCertificateId()) || StringUtils.isEmpty(request
            .getApplicationFormUrl()) || StringUtils.isEmpty(request.getQualificationCertUrl())) {
            return Result.error("证件参数不能为空");
        }

        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        ReexamineDO reexamineDO = new ReexamineDO();

        reexamineDO.setCertificateId(Long.valueOf(request.getCertificateId()));
        reexamineDO.setApplicantId(userInfo.getUserId());
        reexamineDO.setApplicantFormUrl(request.getApplicationFormUrl());
        reexamineDO.setCertificateUrl(request.getQualificationCertUrl());
        reexamineDO.setReexaminStatus(0);
        reexamineMapper.insert(reexamineDO);

        return Result.success("提交成功");
    }

}