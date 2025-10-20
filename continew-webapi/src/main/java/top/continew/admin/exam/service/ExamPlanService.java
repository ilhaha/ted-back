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

import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.req.ExamPlanSaveReq;
import top.continew.admin.exam.model.resp.EnrollStatusResp;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamPlanQuery;
import top.continew.admin.exam.model.req.ExamPlanReq;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.resp.ExamPlanResp;

import java.util.List;

/**
 * 考试计划业务接口
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
public interface ExamPlanService extends BaseService<ExamPlanResp, ExamPlanDetailResp, ExamPlanQuery, ExamPlanReq> {

    /**
     * 保存考试计划
     *
     * @param examPlanSaveReq
     */
    void save(ExamPlanSaveReq examPlanSaveReq);

    String valid(Long id, Integer status);

    public List<ExamPlanDO> getAllList();

    /**
     * 获取考试计划考场
     *
     * @param planId
     * @return
     */
    List<Long> getPlanExamClassroom(Long planId);

    /**
     * 修改考试计划考场
     *
     * @param planId
     * @param classroomId
     * @return
     */
    String updatePlanExamClassroom(Long planId, List<Long> classroomId);

    /**
     * 查找考场id
     *
     * @param planId
     * @return
     */
    Integer selectClassroomId(Integer planId);

    /**
     * 结束考试计划
     *
     * @param planId
     * @return
     */
    Boolean endExam(Long planId);

    /**
     * 获取考试记录下拉框
     *
     * @return
     */
    List<ProjectVo> getSelectOptions();

    /**
     * 批量导入考试计划
     * @param file
     */
    void importExcel(MultipartFile file);

    /**
     * 机构获取符合自身八大类的考试计划
     * @param pageQuery
     * @return
     */
    PageResp<OrgExamPlanVO> orgGetPlanList(ExamPlanQuery examPlanQuery,PageQuery pageQuery);
}