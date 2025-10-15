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
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.certificate.model.entity.CertificateProjectDO;
import top.continew.admin.common.enums.ProjectEnum;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.certificate.mapper.CertificateTypeMapper;
import top.continew.admin.certificate.model.entity.CertificateTypeDO;
import top.continew.admin.certificate.model.query.CertificateTypeQuery;
import top.continew.admin.certificate.model.req.CertificateTypeReq;
import top.continew.admin.certificate.model.resp.CertificateTypeDetailResp;
import top.continew.admin.certificate.model.resp.CertificateTypeResp;
import top.continew.admin.certificate.service.CertificateTypeService;

/**
 * 证件种类业务实现
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Service
@RequiredArgsConstructor
public class CertificateTypeServiceImpl extends BaseServiceImpl<CertificateTypeMapper, CertificateTypeDO, CertificateTypeResp, CertificateTypeDetailResp, CertificateTypeQuery, CertificateTypeReq> implements CertificateTypeService {
    //分页获取证书种类列表
    @Resource
    private CertificateTypeMapper certificateTypeMapper;

    public PageResp<CertificateTypeResp> page(CertificateTypeQuery query, PageQuery pageQuery) {
        // 1. 分页查询主数据
        QueryWrapper<CertificateTypeDO> queryWrapper = buildQueryWrapper(query);
        queryWrapper.eq("tct.is_deleted", 0);
        //如果查询项目名称
        if (StringUtils.isNotBlank(query.getProjectName())) {
            queryWrapper.like("p.project_name", query.getProjectName());
        }
        super.sort(queryWrapper, pageQuery);
        IPage<CertificateTypeResp> page = certificateTypeMapper.selectPageWithProjectName(new Page<>(pageQuery
            .getPage(), pageQuery.getSize()), queryWrapper);

        PageResp<CertificateTypeResp> result = PageResp.build(page, super.getListClass());
        //3. 填充其他数据
        result.getList().forEach(this::fill);
        return result;
    }

    @Override
    public void update(CertificateTypeReq req, Long id) {
        // 1. 校验数据
        //要求同一项目下的证书名称不能相同
        CertificateTypeDO certificateTypeName = certificateTypeMapper.selectType(new QueryWrapper<CertificateTypeDO>()
            .eq("certificate_name", req.getCertificateName())
            .eq("project_id", req.getProjectId())
            .ne("tct.id", id)
            .eq("tct.is_deleted", 0));
        ValidationUtils.throwIfNotNull(certificateTypeName, "证件种类名称重复");
        certificateTypeMapper.updateCertificateProject(req.getProjectId(), id);
        // 2. 更新数据
        super.update(req, id);
    }

    @Override
    public Long add(CertificateTypeReq req) {
        //证书种类名称不能重复
        CertificateTypeDO certificateName = certificateTypeMapper.selectOne(new QueryWrapper<CertificateTypeDO>()
            .eq("certificate_name", req.getCertificateName())
            .eq("is_Deleted", 0));
        ValidationUtils.throwIfNotNull(certificateName, "证件种类名称重复");
        CertificateTypeDO entity = BeanUtil.copyProperties(req, super.getEntityClass());
        baseMapper.insert(entity);
        CertificateProjectDO projectDo = new CertificateProjectDO();
        projectDo.setProjectId(req.getProjectId());
        projectDo.setCertificateTypeId(entity.getId());
        baseMapper.insertCertificateProject(projectDo);
        this.afterAdd(req, entity);
        return entity.getId();
    }

    @Override
    public CertificateTypeDetailResp get(Long id) {
        return baseMapper.getCertificateTypeDetail(id, ProjectEnum.MAINTENANCE);
    }

}