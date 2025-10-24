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

package top.continew.admin.exam.service;

import net.dreamlu.mica.core.result.R;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.exam.model.dto.BatchAuditSpecialCertificationApplicantDTO;
import top.continew.admin.exam.model.req.SpecialCertificationApplicantListReq;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.SpecialCertificationApplicantQuery;
import top.continew.admin.exam.model.req.SpecialCertificationApplicantReq;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantDetailResp;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantResp;

/**
 * 特种设备人员资格申请业务接口
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
public interface SpecialCertificationApplicantService extends BaseService<SpecialCertificationApplicantResp, SpecialCertificationApplicantDetailResp, SpecialCertificationApplicantQuery, SpecialCertificationApplicantReq> {

    /**
     * 根据考生和计划ID查询申报记录
     *
     * @param planId 计划ID
     * @param applySource 申报来源（0机构 / 1个人 / null不区分）
     */
    SpecialCertificationApplicantResp getByCandidates(Long planId, Integer applySource);

    /**
     * 考生上传特种设备人员资格申请表
     * 
     * @param specialCertificationApplicantReq
     * @return
     */
    Boolean candidatesUpload(SpecialCertificationApplicantReq specialCertificationApplicantReq);

    /**
     * 机构代替一个及多个考生上传特种设备人员资格申请表
     * 
     * @param scar
     * @return
     */
    Boolean candidatesUploads(SpecialCertificationApplicantListReq scar);

    R batchAudit(BatchAuditSpecialCertificationApplicantDTO dto);

    R updateResult(SpecialCertificationApplicantReq req, Long id);
}