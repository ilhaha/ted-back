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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import top.continew.admin.certificate.mapper.CandidateCertificateMapper;
import top.continew.admin.certificate.model.entity.CandidateCertificateDO;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.certificate.mapper.ReexamineMapper;
import top.continew.admin.certificate.model.entity.ReexamineDO;
import top.continew.admin.certificate.model.query.ReexamineQuery;
import top.continew.admin.certificate.model.req.ReexamineReq;
import top.continew.admin.certificate.model.resp.ReexamineDetailResp;
import top.continew.admin.certificate.model.resp.ReexamineResp;
import top.continew.admin.certificate.service.ReexamineService;

import java.time.LocalDate;

/**
 * 复审业务实现
 *
 * @author Anton
 * @since 2025/04/29 08:48
 */
@Service
@RequiredArgsConstructor
public class ReexamineServiceImpl extends BaseServiceImpl<ReexamineMapper, ReexamineDO, ReexamineResp, ReexamineDetailResp, ReexamineQuery, ReexamineReq> implements ReexamineService {

    @Resource
    private CandidateCertificateMapper candidateCertificateMapper;

    /**
     * 自定义获取复审信息，增加证件名+考生名
     * 
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ReexamineResp> certificates(ReexamineQuery query, PageQuery pageQuery) {

        //根据mapper查出考生名+证件名
        //封装返回结果
        //        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        QueryWrapper<ReexamineDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tr.is_deleted", 0);

        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询
        IPage<ReexamineResp> page = baseMapper.getcertificatesList(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        // 将查询结果转换成 PageResp 对象
        PageResp<ReexamineResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;

    }

    @Override
    public void updateStatus(ReexamineResp request, Long id) {
        //跟新数据库
        ReexamineDO reexamineDO = new ReexamineDO();
        //拷贝
        BeanUtils.copyProperties(request, reexamineDO);

        baseMapper.updateById(reexamineDO);
        //判断一下是通过还是不通过
        //通过
        if (request.getReexaminStatus() == 1) {
            //todo 注意这里可能会有事务问题 通过后，更新candidate_certificate表，将延长到期时间（1年把）
            //获取证书到期时间,延长时间
            CandidateCertificateDO candidateCertificateDO = candidateCertificateMapper.selectById(request
                .getCertificateId());
            LocalDate localDate = candidateCertificateDO.getExpiryDate().plusYears(1);
            candidateCertificateDO.setExpiryDate(localDate);
            //更新
            candidateCertificateMapper.updateById(candidateCertificateDO);
        }
    }
}