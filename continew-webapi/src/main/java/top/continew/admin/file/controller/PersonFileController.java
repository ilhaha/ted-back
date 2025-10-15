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

package top.continew.admin.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.file.model.dto.Document;
import top.continew.admin.file.model.resp.PersonFileResp;
import top.continew.admin.file.service.PersonFileService;
import top.continew.admin.generator.model.query.PersonFileQuery;
import top.continew.admin.generator.model.req.PersonFileReq;
import top.continew.admin.generator.model.resp.PersonFileDetailResp;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * @author Anton
 * @date 2025/4/23-8:48
 */

@RestController
@CrudRequestMapping(value = "/file/personFile", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class PersonFileController extends BaseController<PersonFileService, PersonFileResp, PersonFileDetailResp, PersonFileQuery, PersonFileReq> {

    //    @GetMapping()
    //    public PersonFileVo getPersonFile(@RequestParam(defaultValue = "1") Integer current,  // 直接接收参数
    //                                      @RequestParam(defaultValue = "10") Integer size){
    //        Page<PersonFile> page = new Page<>(current, size);
    //        return personFileService.queryPersonFile(page);
    //    }

    @GetMapping("/getOneCandidateDocument/{id}")
    public List<Document> getOneCandidateDocument(@PathVariable Long id) {
        return baseService.getOneCandidateDocument(id);
    }

    @GetMapping("/getPersonFilePage")
    @SaCheckPermission("files:personFile:page")
    public PageResp<PersonFileResp> getPersonFilePage(PersonFileQuery query, @Validated PageQuery pageQuery) {
        return baseService.getPersonFilePage(query, pageQuery);
    }
}
