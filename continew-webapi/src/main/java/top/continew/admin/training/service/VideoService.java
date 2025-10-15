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

package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.VideoQuery;
import top.continew.admin.training.model.req.VideoReq;
import top.continew.admin.training.model.resp.VideoDetailResp;
import top.continew.admin.training.model.resp.VideoResp;

/**
 * 视频业务接口
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
public interface VideoService extends BaseService<VideoResp, VideoDetailResp, VideoQuery, VideoReq> {
    /**
     * 自定义新增视频
     * 
     * @param videoReq
     * @return
     */
    Boolean customizeSave(VideoReq videoReq);

    /**
     * 自定义修改视频
     *
     * @param id
     * @param videoReq
     * @return
     */
    Boolean customizeUpdate(Long id, VideoReq videoReq);

    /**
     * 自定义删除视频
     * 
     * @param id
     * @param chapterId
     * @return
     */
    Boolean customizeDelete(Long id, Long chapterId);
}