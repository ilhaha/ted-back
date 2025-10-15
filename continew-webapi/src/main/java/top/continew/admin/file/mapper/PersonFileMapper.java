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

package top.continew.admin.file.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.file.model.dto.Document;
import top.continew.admin.file.model.resp.PersonFileResp;
import top.continew.starter.data.mp.base.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.file.model.entity.PersonFile;
import top.continew.admin.file.model.entity.PersonFileDO;

import java.util.List;

/**
 * @author Anton
 * @date 2025/4/23-9:14
 */

@Mapper
public interface PersonFileMapper extends BaseMapper<PersonFileDO> {

    /**
     * 分页查询个人档案
     * 
     * @param page
     * @return
     */
    @Select("select tep.exam_plan_name,su.nickname,ter.exam_scores from ted_exam_plan tep " + "inner join ted_exam_records ter on tep.id = ter.plan_id " + "inner join sys_user su on ter.candidate_id = su.id")
    List<PersonFile> queryPersonFile(Page<PersonFile> page);

    @Select("select count(*) from ted_exam_plan tep " + "inner join ted_exam_records ter on tep.id = ter.plan_id " + "inner join sys_user su on ter.candidate_id = su.id")
    Long queryPersonFileTotal();

    IPage<PersonFileResp> selectPersonFilePage(@Param("page") Page<Object> objectPage,
                                               @Param(Constants.WRAPPER) QueryWrapper<PersonFileDO> queryWrapper);

    @Select("select " + "tdt.type_name as documentName, " + "td.doc_path as documentUrl, " + "td.status as status " + "from ted_document td " + "inner join ted.ted_document_type tdt " + "on td.type_id = tdt.id " + "where td.status = 1 " + "and td.id in (select ted.document_id " + "from ted_examinee_document ted " + "where ted.examinee_id = #{candidateId}) ")
    List<Document> queryCandidateDocument(Long candidateId);
}
