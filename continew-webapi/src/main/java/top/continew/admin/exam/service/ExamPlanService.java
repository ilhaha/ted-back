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
import top.continew.admin.auth.model.resp.ExamCandidateInfoVO;
import top.continew.admin.document.model.resp.ExamPlanClassStatsResp;
import top.continew.admin.exam.model.dto.ExamPlanDTO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.query.ExamRecordsQuery;
import top.continew.admin.exam.model.req.AdjustPlanTimeReq;
import top.continew.admin.exam.model.req.ExamPlanSaveReq;
import top.continew.admin.exam.model.req.ExamPlanStartReq;
import top.continew.admin.exam.model.resp.CascaderOptionResp;
import top.continew.admin.exam.model.resp.CascaderPlanResp;
import top.continew.admin.exam.model.vo.InvigilateExamPlanVO;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.invigilate.model.resp.AvailableInvigilatorResp;
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
    Boolean updatePlanExamClassroom(Long planId, List<Long> classroomId);

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
     * 
     * @param file
     */
    void importExcel(MultipartFile file);

    /**
     * 机构获取符合自身八大类的考试计划
     * 
     * @param pageQuery
     * @return
     */
    PageResp<OrgExamPlanVO> orgGetPlanList(ExamPlanQuery examPlanQuery, PageQuery pageQuery);

    void batchUpdatePlanMaxCandidates(List<ExamPlanDTO> planList);

    /**
     * 重新随机分配考试计划的监考员
     * 
     * @param planId
     * @param invigilatorNum
     * @return
     */
    Boolean reRandomInvigilators(Long planId, Integer invigilatorNum);

    /**
     * 获取可用监考员
     * 
     * @param planId
     * @return
     */
    List<AvailableInvigilatorResp> getAvailableInvigilator(Long planId, Long rejectedInvigilatorId);

    /**
     * 中心主任确认考试
     * 
     * @param planId
     * @param isFinalConfirmed
     * @return
     */
    Boolean centerDirectorConform(Long planId, Integer isFinalConfirmed);

    /**
     * 调整考试/报名时间
     * 
     * @param req
     * @param planId
     * @return
     */
    Boolean adjustPlanTime(AdjustPlanTimeReq req, Long planId);

    /**
     * 监考员获取监考计划列表
     *
     * @param examPlanQuery 考试计划查询参数
     * @param pageQuery     分页参数
     * @return 分页结果
     */
    PageResp<InvigilateExamPlanVO> invigilateGetPlanList(ExamPlanQuery examPlanQuery, PageQuery pageQuery);

    /**
     * 监考员进行开考
     *
     * @param req
     * @param req
     * @return
     */
    ExamCandidateInfoVO startExam(ExamPlanStartReq req);

    /**
     * 根据考生身份证获取考生的所有考试准考证号
     * 
     * @param username
     * @return
     */
    List<CascaderOptionResp> getExamNumbersByUsername(String username);

    /**
     * 根据计划考试人员类型获取项目-考试计划级联选择器
     * 
     * @param planType
     * @return
     */
    List<CascaderPlanResp> getCascaderProjectPlan(Integer planType, Boolean isOrgQuery);

    /**
     * 根据班级列表获取每个班级在考试计划下的报名人数、考试人数、及格人数、成绩录入情况和证书生成情况
     *
     * @param query
     * @param pageQuery
     * @return
     */
    PageResp<ExamPlanResp> getClassExamStatsPage(ExamPlanQuery query, PageQuery pageQuery);

    /**
     * 机构根据班级列表获取每个班级在考试计划下的报名人数、考试人数、及格人数、成绩录入情况
     *
     * @param query
     * @param pageQuery
     * @return
     */
    PageResp<ExamPlanResp> getClassExamStatsPageForOrg(ExamPlanQuery query, PageQuery pageQuery);
}