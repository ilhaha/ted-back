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

import org.springframework.http.ResponseEntity;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.vo.ApplyListVO;
import top.continew.admin.exam.model.vo.ExamCandidateVO;
import top.continew.admin.exam.model.vo.IdentityCardExamInfoVO;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.EnrollQuery;
import top.continew.admin.exam.model.req.EnrollReq;

import java.util.List;
import java.util.Map;

/**
 * 考生报名表业务接口
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
public interface EnrollService extends BaseService<EnrollResp, EnrollDetailResp, EnrollQuery, EnrollReq> {
    /**
     * 获取报名相关所有信息
     * 
     * @return
     */

    public EnrollDetailResp getAllDetailEnrollList(Long examPlanId);

    /**
     * 获取带有状态的报名列表
     *
     * @param query        查询条件
     * @param pageQuery    分页参数
     * @param enrollStatus 报名状态（可选）
     * @return 分页结果
     */
    PageResp<EnrollStatusResp> getEnrollStatusList(EnrollQuery query, PageQuery pageQuery, Long enrollStatus);

    public EnrollStatusDetailResp getEnrollStatusDetail(Long examPlanId);

    //获取考生个人信息
    public EnrollInfoResp getEnrollInfo();

    /**
     * 考生报名
     *
     * @param enrollReq 报名请求
     * @param userId    用户ID
     * @param status    状态
     * @return 是否报名成功（0-人数超限，1-报名成功，2-数据库插入失败）
     */
    Boolean signUp(EnrollReq enrollReq, Long userId, Integer status);


    /**
     * 生成考生报名准考证
     *
     * @param enrollReq 报名请求
     * @param userId    用户ID
     * @param status    状态
     * @return 是否报名成功（0-人数超限，1-报名成功，2-数据库插入失败）
     */
    Boolean signUpdate(EnrollReq enrollReq, Long userId, Integer status);


    /**
     * 获取考试成绩
     * 
     * @param name
     * @param identity
     * @return
     */
    Map<String, String> getScore(String name, String identity);

    /**
     * 校验已报名考试时间
     * 
     * @param examPlanId 考试计划ID
     * @return 无返回值，仅校验时间冲突
     */
    void checkEnrollTime(Long examPlanId);

    IdentityCardExamInfoVO viewIdentityCard(Long examPlanId);

    /**
     * 获取考试计划对应考场的考生列表
     * 
     * @param enrollQuery
     * @param pageQuery
     * @param planId
     * @param classroomId
     * @return
     */
    PageResp<ExamCandidateVO> getExamCandidates(EnrollQuery enrollQuery,
                                                PageQuery pageQuery,
                                                Long planId,
                                                Long classroomId);

    /**
     * 取消报名
     * 
     * @param examPlanId 计划id
     */
    void cancelEnroll(Long examPlanId);

    /**
     * 下载某个考试的缴费通知单
     * @param enrollId
     * @return
     */
    ResponseEntity<byte[]> downloadAuditNotice(Long enrollId);

    /**
     * 下载某个班级的考试缴费通知单
     * @param classId
     * @param planId
     * @return
     */
    ResponseEntity<byte[]> downloadBatchAuditNotice(Long classId, Long planId);


    /**
     * 下载某个考生的准考证
     * @param enrollId
     * @return
     */
    ResponseEntity<byte[]> downloadTicket(Long enrollId);

    /**
     * 下载某个班级的准考证
     * @param classId
     * @param planId
     * @return
     */
    ResponseEntity<byte[]> downloadClassTicket(Long classId, Long planId);
}