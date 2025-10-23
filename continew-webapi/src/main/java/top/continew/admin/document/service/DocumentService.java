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
import top.continew.admin.document.model.resp.*;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.document.model.query.DocumentQuery;
import top.continew.admin.document.model.req.DocumentReq;

import java.util.List;

/**
 * 资料核心存储业务接口
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
public interface DocumentService extends BaseService<DocumentResp, DocumentDetailResp, DocumentQuery, DocumentReq> {

    public void upload(DocumentReq req);//上传资料

    public List<DocumentTypeAddResp> getDocumentType();//获取资料类型

    /**
     * 获取资料列表
     *
     * @param query     查询参数
     * @param pageQuery 分页查询参数
     * @return PageResp<DocumentCandidatesResp>
     */
    public PageResp<DocumentCandidatesResp> listDocument(DocumentQuery query, PageQuery pageQuery);

    /**
     * 通过二维码上传上传考生资料
     *
     * @return
     */
    Boolean qrcodeUpload(QrcodeUploadReq qrcodeUploadReq);

}