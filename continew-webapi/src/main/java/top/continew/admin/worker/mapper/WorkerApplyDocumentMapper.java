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

package top.continew.admin.worker.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.worker.model.dto.WorkerApplyDocAndNameDTO;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDocumentDO;

import java.util.List;

/**
 * 作业人员报名上传的资料 Mapper
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
public interface WorkerApplyDocumentMapper extends BaseMapper<WorkerApplyDocumentDO> {
    IPage<WorkerApplyDocumentDetailResp> page(@Param("page") Page<Object> page,
                                              @Param(Constants.WRAPPER) QueryWrapper<WorkerApplyDocumentDO> queryWrapper);

    /**
     * 获取作业人员对应的上传资料的名称
     * 
     * @param workerApplyIds
     * @return
     */
    List<WorkerApplyDocAndNameDTO> selectDocAndName(@Param("workerApplyIds") List<Long> workerApplyIds);
}