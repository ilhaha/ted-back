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

package top.continew.admin.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.continew.admin.file.model.dto.Document;
import top.continew.admin.file.model.entity.PersonFile;
import top.continew.admin.file.model.resp.PersonFileResp;
import top.continew.admin.file.model.vo.PersonFileVo;
import top.continew.admin.generator.model.query.PersonFileQuery;
import top.continew.admin.generator.model.req.PersonFileReq;
import top.continew.admin.generator.model.resp.PersonFileDetailResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;

import java.util.List;

/**
 * @author Anton
 * @date 2025/4/23-9:33
 */

public interface PersonFileService extends BaseService<PersonFileResp, PersonFileDetailResp, PersonFileQuery, PersonFileReq> {

    PersonFileVo queryPersonFile(Page<PersonFile> page);

    List<Document> getOneCandidateDocument(Long id);

    PageResp<PersonFileResp> getPersonFilePage(PersonFileQuery query, PageQuery pageQuery);
}
