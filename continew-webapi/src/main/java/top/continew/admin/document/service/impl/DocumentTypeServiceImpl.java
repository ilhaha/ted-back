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

package top.continew.admin.document.service.impl;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import top.continew.admin.document.model.dto.DocumentTypeDTO;
import top.continew.admin.document.model.vo.DocumentTypeNameVO;
import top.continew.admin.document.service.cache.DocumentTypeCache;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.document.mapper.DocumentTypeMapper;
import top.continew.admin.document.model.entity.DocumentTypeDO;
import top.continew.admin.document.model.query.DocumentTypeQuery;
import top.continew.admin.document.model.req.DocumentTypeReq;
import top.continew.admin.document.model.resp.DocumentTypeDetailResp;
import top.continew.admin.document.model.resp.DocumentTypeResp;
import top.continew.admin.document.service.DocumentTypeService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 资料类型主业务实现
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Service
@RequiredArgsConstructor
public class DocumentTypeServiceImpl extends BaseServiceImpl<DocumentTypeMapper, DocumentTypeDO, DocumentTypeResp, DocumentTypeDetailResp, DocumentTypeQuery, DocumentTypeReq> implements DocumentTypeService {
    @Resource
    private DocumentTypeMapper documentTypeMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private DocumentTypeCache documentTypeCache;

    public Long add(DocumentTypeReq req) {

        List<DocumentTypeDTO> documentTypeCache1 = documentTypeCache.getDocumentTypeCache();
        //判断数据库是否已存在
        for (DocumentTypeDTO documentTypeDTO : documentTypeCache1) {
            if (documentTypeDTO.getTypeName().equals(req.getTypeName())) {
                //throw new RuntimeException("已存在该类型");
                //ValidationUtils.throwIf(true, "已存在该类型");
                throw new BusinessException("已存在该类型");
            }
        }
        Long result = super.add(req);
        if (result != null) {
            documentTypeCache.clearCache();
        }
        return result;
    }

    @Override
    public void update(DocumentTypeReq req, Long id) {
        super.update(req, id);
        documentTypeCache.clearCache();
    }

    @Override
    public void delete(List<Long> ids) {
        super.delete(ids);
        documentTypeCache.clearCache();
    }

    /**
     * 获取资料类型名称列表
     * 
     * @return
     */
    @Override
    public List<DocumentTypeNameVO> getDocumentTypeName() {
        //查询缓存
        List<DocumentTypeDTO> rDto = documentTypeCache.getDocumentTypeCache();
        return rDto.stream()
            .map(dto -> new DocumentTypeNameVO(dto.getId(), dto.getTypeName()))
            .collect(Collectors.toList());
    }
}