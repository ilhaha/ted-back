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

package top.continew.admin.document.service;

import top.continew.admin.document.model.req.QrcodeUploadReq;
import top.continew.admin.document.model.req.EnrollPreReviewReq;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.document.model.query.EnrollPreUploadQuery;
import top.continew.admin.document.model.req.EnrollPreUploadReq;
import top.continew.admin.document.model.resp.EnrollPreUploadDetailResp;
import top.continew.admin.document.model.resp.EnrollPreUploadResp;

/**
 * 机构报考-考生扫码上传文件业务接口
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
public interface EnrollPreUploadService extends BaseService<EnrollPreUploadResp, EnrollPreUploadDetailResp, EnrollPreUploadQuery, EnrollPreUploadReq> {

    /**
     * 通过二维码上传上传考生资料
     *
     * @return
     */
    Boolean qrcodeUpload(QrcodeUploadReq qrcodeUploadReq);

    /**
     * 机构报考
     * 
     * @param reviewReq
     * @return
     */
    Boolean review(EnrollPreReviewReq reviewReq);
}