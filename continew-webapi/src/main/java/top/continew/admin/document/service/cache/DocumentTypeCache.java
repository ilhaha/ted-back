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

package top.continew.admin.document.service.cache;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.document.mapper.DocumentTypeMapper;
import top.continew.admin.document.model.dto.DocumentTypeDTO;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DocumentTypeCache {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private DocumentTypeMapper documentTypeMapper;

    /**
     * 获取资料种类缓存方法
     */
    public List<DocumentTypeDTO> getDocumentTypeCache() {
        //查询缓存
        List<DocumentTypeDTO> rDto = (List<DocumentTypeDTO>)redisTemplate.opsForValue()
            .get(RedisConstant.ALL_DOCUMENT_TYPE_KEY);
        //缓存命中直接返回
        if (rDto != null) {
            return rDto;
        }
        //缓存未命中，查询数据库
        rDto = documentTypeMapper.selectAllDocumentType();
        //存入新缓存中
        redisTemplate.opsForValue()
            .set(RedisConstant.ALL_DOCUMENT_TYPE_KEY, rDto, RedisConstant.FIFTEEN_DAYS, TimeUnit.SECONDS);
        return rDto;
    }

    // 清除缓存 （在数据有变动时使用）
    public void clearCache() {
        redisTemplate.delete(RedisConstant.ALL_DOCUMENT_TYPE_KEY);
    }

}
