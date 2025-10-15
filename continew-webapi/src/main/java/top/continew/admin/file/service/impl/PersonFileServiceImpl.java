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

package top.continew.admin.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.file.mapper.PersonFileMapper;
import top.continew.admin.file.model.dto.Document;
import top.continew.admin.file.model.entity.PersonFile;
import top.continew.admin.file.model.entity.PersonFileDO;
import top.continew.admin.file.model.resp.PersonFileResp;
import top.continew.admin.file.model.vo.PersonFileVo;
import top.continew.admin.file.service.PersonFileService;
import top.continew.admin.generator.model.query.PersonFileQuery;
import top.continew.admin.generator.model.req.PersonFileReq;
import top.continew.admin.generator.model.resp.PersonFileDetailResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.util.List;

/**
 * @author Anton
 * @date 2025/4/23-9:34
 */

@Service
@RequiredArgsConstructor
public class PersonFileServiceImpl extends BaseServiceImpl<PersonFileMapper, PersonFileDO, PersonFileResp, PersonFileDetailResp, PersonFileQuery, PersonFileReq> implements PersonFileService {

    private final PersonFileMapper personFileMapper;

    /**
     * 分页查询个人档案
     * 
     * @param page
     * @return
     */
    @Override
    public PersonFileVo queryPersonFile(Page<PersonFile> page) {
        Long total = personFileMapper.queryPersonFileTotal();
        List<PersonFile> personFiles = personFileMapper.queryPersonFile(page);
        PersonFileVo personFileVo = new PersonFileVo();
        personFileVo.setRecords(personFiles);
        personFileVo.setTotal(total);
        return personFileVo;
    }

    //重写分页查询方法
    @Override
    public PageResp<PersonFileResp> page(PersonFileQuery query, PageQuery pageQuery) {

        QueryWrapper<PersonFileDO> queryWrapper = this.buildQueryWrapper(query);
        if (query.getNickName() != null) {
            queryWrapper.like("su.nickname", query.getNickName());
        }
        if (query.getPlanName() != null) {
            queryWrapper.like("tep.exam_plan_name", query.getPlanName());
        }
        queryWrapper.eq("tep.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<PersonFileResp> page = baseMapper.selectPersonFilePage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        PageResp<PersonFileResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        //2.查询考生资料
        for (PersonFileResp personFileResp : build.getList()) {
            List<Document> documents = personFileMapper.queryCandidateDocument(personFileResp.getCandidateId());
            personFileResp.setDocumentList(documents);
        }
        return build;
    }

    @Override
    public List<Document> getOneCandidateDocument(Long id) {
        return personFileMapper.queryCandidateDocument(id);
    }

    @Override
    public PageResp<PersonFileResp> getPersonFilePage(PersonFileQuery query, PageQuery pageQuery) {

        QueryWrapper<PersonFileDO> queryWrapper = this.buildQueryWrapper(query);
        if (query.getNickName() != null) {
            queryWrapper.like("su.nickname", query.getNickName());
        }
        if (query.getPlanName() != null) {
            queryWrapper.like("tep.exam_plan_name", query.getPlanName());
        }
        queryWrapper.eq("tep.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<PersonFileResp> page = baseMapper.selectPersonFilePage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        PageResp<PersonFileResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        //2.查询考生资料
        for (PersonFileResp personFileResp : build.getList()) {
            List<Document> documents = personFileMapper.queryCandidateDocument(personFileResp.getCandidateId());
            personFileResp.setDocumentList(documents);
        }
        return build;
    }
}
