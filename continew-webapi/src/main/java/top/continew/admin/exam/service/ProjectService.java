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

package top.continew.admin.exam.service;

import top.continew.admin.document.model.resp.DocumentTypeResp;
import top.continew.admin.exam.model.req.ExamLocationReqStr;
import top.continew.admin.exam.model.resp.StudentProjectDetailResp;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.exam.model.vo.ProjectWithClassroomVO;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ProjectQuery;
import top.continew.admin.exam.model.req.ProjectReq;
import top.continew.admin.exam.model.resp.ProjectDetailResp;
import top.continew.admin.exam.model.resp.ProjectResp;

import java.util.List;
import java.util.Map;

/**
 * 项目业务接口
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
public interface ProjectService extends BaseService<ProjectResp, ProjectDetailResp, ProjectQuery, ProjectReq> {

    /**
     * 分页查询
     *
     * @return
     */
    //    Page<ProjectDO> pageQuery(ProjectReq query, PageQuery pageQuery);

    String insertLocation(Long projectId, List<Long> localtionList);

    List<ExamLocationReqStr> getLocation(Long projectId);

    List<ProjectVo> getLocationSelect(Long projectId);

    //    List<ProjectVo> getClassRoomSelect(Long locationId);
    List<Map<String, Object>> getClassRoomSelect(Long locationId);

    List<ProjectVo> getDeptProject();

    public void delete(List<Long> ids);

    public List<ProjectVo> notBindLocation(Long id);

    public List<ProjectVo> getBindLocation(Long id);

    /**
     * 项目解绑地址
     */
    void projectDelLocation(Long projectId, List<Long> locationIds);

    /**
     * 获取项目没有绑定的资料列表 label、value
     *
     * @param id
     * @return
     */
    List<ProjectVo> notBindDocument(Long id);

    /**
     * 根据项目id与资料id集合插入关联表
     *
     * @param projectId
     * @param documentList
     * @return
     */
    String insertDocument(Long projectId, List<Long> documentList);

    /**
     * 根据项目id查询资料列表
     */
    List<DocumentTypeResp> getDocument(Long projectId);

    /**
     * 项目解绑资料
     */
    void projectDelDocument(Long projectId, List<Long> documentIds);

    /**
     * 考生端分页获取项目数据
     *
     * @return
     */
    PageResp<ProjectResp> getAllProject(ProjectQuery query, PageQuery pageQuery);

    PageResp<ProjectResp> getProjectByStatus(ProjectQuery query, PageQuery pageQuery, Long projectStatus);

    /**
     * 考生端根据项目id查询项目详情
     *
     * @param ProjectReq req,Long projectId
     * @return
     */
    void examine(ProjectReq req, Long projectId);

    /**
     * 考生端查询项目详细信息
     */
    StudentProjectDetailResp getStudentProjectDetail(Long projectId);

    /**
     * 获取项目下拉选项
     */
    List<ProjectVo> selectOptions();

    /**
     * 查询所有有考场的考试项目
     * @return
     */
    List<ProjectWithClassroomVO> getProjectsWithClassrooms();

    /**
     * 机构获取所属全部项目
     * @param query
     * @param pageQuery
     * @return
     */
    PageResp<ProjectResp> orgGetAllProject(ProjectQuery query, PageQuery pageQuery);
}