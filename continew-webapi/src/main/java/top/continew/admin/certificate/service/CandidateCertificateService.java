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

package top.continew.admin.certificate.service;

import top.continew.admin.certificate.model.dto.ReexaminationDTO;
import top.continew.admin.util.Result;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.certificate.model.query.CandidateCertificateQuery;
import top.continew.admin.certificate.model.req.CandidateCertificateReq;
import top.continew.admin.certificate.model.resp.CandidateCertificateDetailResp;
import top.continew.admin.certificate.model.resp.CandidateCertificateResp;

import java.util.List;

/**
 * 考生证件业务接口
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
public interface CandidateCertificateService extends BaseService<CandidateCertificateResp, CandidateCertificateDetailResp, CandidateCertificateQuery, CandidateCertificateReq> {
    /**
     * 获取考生证书列表
     *
     * @param query     查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResp<CandidateCertificateResp> getCandidateCertificateList(CandidateCertificateQuery query,
                                                                   PageQuery pageQuery);

    List<CandidateCertificateResp> getUserCertificate();

    List<CandidateCertificateResp> getUserCertificateList(String candidateId);

    Result submitReexamination(ReexaminationDTO request);
}