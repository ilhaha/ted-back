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

package top.continew.admin.document.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.document.model.dto.UserDTO;
import top.continew.admin.document.model.resp.DocumentResp;
import top.continew.admin.document.model.resp.DocumentTypeAddResp;
import top.continew.admin.document.model.resp.DocumentCandidatesResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.document.model.entity.DocumentDO;

import java.util.List;

/**
 * 资料核心存储 Mapper
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
public interface DocumentMapper extends BaseMapper<DocumentDO> {
    @Select("select t2.type_name from ted.ted_document as t1 left join ted.ted_document_type as t2 on t1.type_id = t2.id where t1.id = #{id}")
    String getTypeName(@Param("id") Long Id);

    @Select("select id,type_name as typeName from ted.ted_document_type where is_deleted = 0")
    List<DocumentTypeAddResp> getDocumentType();

    List<UserDTO> getUserInfoList();

    UserDTO getUserInfo(@Param("id") Long Id);

    /*
      上传资料传入资料表
      @param documentReq
     */
    void uploadDocument(DocumentDO documentDo);

    /*
      上传资料传入考生资料关联表
      @param documentReq
     */
    @Insert("insert into ted.ted_examinee_document(document_id,ted.ted_examinee_document.examinee_id) values(#{documentId},#{userId})")
    void uploadCandidatesDocument(@Param("documentId") Long documentId, @Param("userId") Long userId);

    /*
    考生端获取资料列表
     */
    IPage<DocumentCandidatesResp> getDocumentList(Page<DocumentResp> page,
                                                  @Param(Constants.WRAPPER) QueryWrapper<DocumentDO> queryWrapper);

    /*
      获取资料id
     */
    Long selectId(@Param("certificateId") Long certificateId, @Param("typeId") Long typeId);

    /**
     * 更新审核状态与备注
     */
    int updateAuditStatus(@Param("id") Long id,
                          @Param("status") Integer status,
                          @Param("auditRemark") String auditRemark,
                          @Param("updateUser") Long updateUser);
}