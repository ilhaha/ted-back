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

import lombok.RequiredArgsConstructor;

import org.apache.tika.utils.StringUtils;
import org.springframework.stereotype.Service;

import top.continew.admin.common.util.AESWithHMAC;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.LicenseCertificateMapper;
import top.continew.admin.exam.model.entity.LicenseCertificateDO;
import top.continew.admin.exam.model.query.LicenseCertificateQuery;
import top.continew.admin.exam.model.req.LicenseCertificateReq;
import top.continew.admin.exam.model.resp.LicenseCertificateDetailResp;
import top.continew.admin.exam.model.resp.LicenseCertificateResp;
import top.continew.admin.exam.service.LicenseCertificateService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 人员及许可证书信息业务实现
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
@Service
@RequiredArgsConstructor
public class LicenseCertificateServiceImpl extends BaseServiceImpl<LicenseCertificateMapper, LicenseCertificateDO, LicenseCertificateResp, LicenseCertificateDetailResp, LicenseCertificateQuery, LicenseCertificateReq> implements LicenseCertificateService {

    private final AESWithHMAC aesWithHMAC;

    @Override
    public PageResp<LicenseCertificateResp> page(LicenseCertificateQuery query, PageQuery pageQuery) {
        String idcardNo = query.getIdcardNo();
        if (!StringUtils.isBlank(idcardNo)) {
            query.setIdcardNo(aesWithHMAC.encryptAndSign(idcardNo));
        }
        PageResp<LicenseCertificateResp> page = super.page(query, pageQuery);
        List<LicenseCertificateResp> decryptedList = page.getList()
            .stream()
            .peek(item -> item.setIdcardNo(aesWithHMAC.verifyAndDecrypt(item.getIdcardNo())))
            .collect(Collectors.toList());
        page.setList(decryptedList);
        return page;
    }

}