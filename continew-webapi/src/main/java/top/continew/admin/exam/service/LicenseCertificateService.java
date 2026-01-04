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

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.LicenseCertificateQuery;
import top.continew.admin.exam.model.req.LicenseCertificateReq;
import top.continew.admin.exam.model.resp.LicenseCertificateDetailResp;
import top.continew.admin.exam.model.resp.LicenseCertificateResp;

/**
 * 人员及许可证书信息业务接口
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
public interface LicenseCertificateService extends BaseService<LicenseCertificateResp, LicenseCertificateDetailResp, LicenseCertificateQuery, LicenseCertificateReq> {}