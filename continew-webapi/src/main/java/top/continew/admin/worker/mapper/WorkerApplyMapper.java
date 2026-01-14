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
import top.continew.admin.worker.model.resp.ProjectInfoVO;
import top.continew.admin.worker.model.resp.ProjectNeedUploadDocVO;
import top.continew.admin.worker.model.resp.WorkerApplyDetailResp;
import top.continew.admin.worker.model.resp.WorkerUploadedDocsVO;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;

import java.util.List;
import java.util.Map;

/**
 * 作业人员报名 Mapper
 *
 * @author ilhaha
 * @since 2025/10/31 10:20
 */
public interface WorkerApplyMapper extends BaseMapper<WorkerApplyDO> {

    List<ProjectNeedUploadDocVO> selectProjectNeedUploadDoc(@Param("classId") Long classId);

    IPage<WorkerApplyDetailResp> page(@Param("page") Page<Object> objectPage,
                                      @Param(Constants.WRAPPER) QueryWrapper<WorkerApplyDO> queryWrapper);

    WorkerUploadedDocsVO selectWorkerUploadedDocs(@Param("classId") Long classId, @Param("idCard") String idCard);

    ProjectInfoVO getProjectInfoByClassId(Long classId);

    List<Map<String, Object>> selectOrgIdByReviewIds(@Param("reviewIds") List<Long> reviewIds);
}