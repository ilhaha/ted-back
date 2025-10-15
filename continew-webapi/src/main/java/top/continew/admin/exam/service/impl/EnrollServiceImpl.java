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

package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.vo.ExamCandidateVO;
import top.continew.admin.exam.model.vo.ExamPlanVO;
import top.continew.admin.exam.model.vo.IdentityCardExamInfoVO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.query.EnrollQuery;
import top.continew.admin.exam.model.req.EnrollReq;
import top.continew.admin.exam.service.EnrollService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考生报名表业务实现
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
@Service
@RequiredArgsConstructor
public class EnrollServiceImpl extends BaseServiceImpl<EnrollMapper, EnrollDO, EnrollResp, EnrollDetailResp, EnrollQuery, EnrollReq> implements EnrollService {
    @Resource
    private EnrollMapper enrollMapper;
    @Resource
    private ExamPlanMapper examPlanMapper;
    @Resource
    private SpecialCertificationApplicantMapper specialCertificationApplicantMapper;
    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private LocationClassroomMapper locationClassroomMapper;

    @Resource
    private AESWithHMAC aesWithHMAC;

    /**
     * 获取报名相关所有信息
     * 分页
     *
     * @return
     */

    @Override
    // 重写父类方法，获取所有报名详情列表
    public EnrollDetailResp getAllDetailEnrollList(Long examPlanId) {
        // 调用enrollMapper的getAllDetailEnrollList方法，传入项目id，获取所有报名详情列表
        EnrollDetailResp detailEnrollList = enrollMapper.getAllDetailEnrollList(examPlanId, TokenLocalThreadUtil.get()
            .getUserId());
        // 返回所有报名详情列表
        detailEnrollList.setCertificates(enrollMapper.getCertificateList(examPlanId));
        detailEnrollList.setDocumentNames(enrollMapper.getDocumentList(examPlanId));

        return detailEnrollList;
    }

    //
    //获取带有状态的报名列表
    @Override
    public PageResp<EnrollStatusResp> getEnrollStatusList(EnrollQuery query, PageQuery pageQuery, Long enrollStatus) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        QueryWrapper<EnrollDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tep.is_deleted", 0);
        if (enrollStatus == null) {
            //只查询已经生效的考试计划
            queryWrapper.eq("tep.status", 3);
        }
        //     如果提供了 enrollStatus，则添加状态条件
        if (enrollStatus != null) {
            if (enrollStatus == 2) {
                queryWrapper.eq("te.enroll_status", 2).eq("tep.status", 6).eq("tep.is_deleted", 0);
            } else if (enrollStatus == 0) {
                queryWrapper.eq("te.enroll_status", enrollStatus)
                    .or()
                    .isNull("te.enroll_Status")
                    .eq("tep.status", 3)
                    .eq("tep.is_deleted", 0);
            } else if (enrollStatus == 1) {
                queryWrapper.eq("te.enroll_status", enrollStatus).eq("tep.status", 3).eq("tep.is_deleted", 0);

            }
        }
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询
        IPage<EnrollStatusResp> page = baseMapper.getEnrollList(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper, userTokenDo.getUserId());

        // 将查询结果转换成 PageResp 对象
        PageResp<EnrollStatusResp> pageResp = PageResp.build(page, EnrollStatusResp.class);
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;
    }

    //获取带有状态的报名详情列表
    @Override
    public EnrollStatusDetailResp getEnrollStatusDetail(Long examPlanId) {
        // 调用enrollMapper的getAllDetailEnrollList方法，传入项目id，获取所有报名详情列表
        EnrollStatusDetailResp detailEnrollList = enrollMapper.getDetailEnroll(examPlanId);
        // 返回所有报名详情列表
        detailEnrollList.setCertificates(enrollMapper.getCertificateList(examPlanId));
        detailEnrollList.setDocumentNames(enrollMapper.getDocumentList(examPlanId));
        return detailEnrollList;
    }

    @Override
    public EnrollInfoResp getEnrollInfo() {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        EnrollInfoResp enrollInfoResp = enrollMapper.getEnrollInfo(userTokenDo.getUserId());
        enrollInfoResp.setDocumentList(enrollMapper.getStudentDocumentList(userTokenDo.getUserId()));
        //输出en
        System.out.println(enrollInfoResp);
        return enrollInfoResp;
    }

    @Transactional
    @Override
    public Boolean singUp(EnrollReq enrollReq, Long userId, Integer status) {
        synchronized (this) {

            //获取本场考试信息
            Long planId = enrollReq.getExamPlanId();
            ExamPlanDO examPlanDO = examPlanMapper.selectById(planId);
            ExamPlanVO examPlanVO = new ExamPlanVO();
            BeanUtils.copyProperties(examPlanDO, examPlanVO);
            examPlanVO.setClassroomList(examPlanMapper.getPlanExamClassroom(planId));

            //使用安全随机数生成器
            SecureRandom random = new SecureRandom();
            //获取考场列表
            List<Long> classroomList = examPlanVO.getClassroomList();
            Collections.shuffle(classroomList); // 随机打乱避免热点
            //尝试分配考场座位
            Long assignedClassroomId = null;
            for (Long classroomId : classroomList) {
                int updated = classroomMapper.incrementEnrolledCount(classroomId, examPlanDO.getId());
                if (updated > 0) {
                    assignedClassroomId = classroomId;
                    break;
                }
            }
            if (status != 2) {
                if (assignedClassroomId == null) {
                    throw new BusinessException("当前考试已满员，已自动将该计划剩余审核改为不通过");
                }

                //拼接准考证号
                //获取考场id
                String classroomId = String.format("%03d", assignedClassroomId);
                //获取考试年份
                String planYear = examPlanVO.getPlanYear();
                //获取对应考场的最大容纳人数
                String seatPart = String.format("%03d", classroomMapper.getSeatNumber(assignedClassroomId, examPlanDO
                    .getId()));
                //生成四位随机数字 转化为字符串
                String randomPart = String.format("%04d", random.nextInt(10000));
                //生成准考证号
                String examNumber = planYear + randomPart + classroomId + seatPart;
                //存入数据库
                EnrollDO enrollDO = new EnrollDO();
                // 补充报名表信息
                BeanUtils.copyProperties(enrollReq, enrollDO);
                enrollDO.setUserId(userId);
                enrollDO.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                enrollDO.setSeatId(Long.valueOf(seatPart));
                enrollDO.setClassroomId(Long.valueOf(classroomId));
                return enrollMapper.insert(enrollDO) > 0;
            }
        }
        return false;
    }

    @Override
    public Map<String, String> getScore(String username, String identity) {
        // 非空校验
        if (username == null || identity == null)
            return null;
        // 获取当前考生信息
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        // 判断是否为本人
        String name = userTokenDo.getUsername();
        if (!name.equals(username))
            return null;
        // 通过准考证号获取考试项目名和成绩
        Map<String, String> nameScore = enrollMapper.getScore(identity);
        ValidationUtils.throwIfNull(nameScore, "成绩未出，或者已过期");
        return nameScore;
    }

    /**
     * 校验时间 人数
     * 
     * @param examPlanId 考试计划ID
     */
    @Override
    public void checkEnrollTime(Long examPlanId) {
        //1.检查是否存在报名时间冲突
        List<EnrollResp> enrollRespList = enrollMapper.getEnrolledPlan(TokenLocalThreadUtil.get().getUserId());
        ExamPlanDO examPlanDO = examPlanMapper.selectById(examPlanId);
        enrollRespList.forEach(enrollResp -> {
            boolean isConflict = !enrollResp.getExamEndTime().isBefore(examPlanDO.getStartTime()) && !enrollResp
                .getExamStartTime()
                .isAfter(examPlanDO.getEndTime());

            ValidationUtils.throwIf(isConflict, "与已报名考试存在时间冲突");
        });
        //2.先是否可以报名（人数是否以达到上限)
        //2.1获取本场考试信息
        ExamPlanVO examPlanVO = new ExamPlanVO();
        BeanUtils.copyProperties(examPlanDO, examPlanVO);
        examPlanVO.setClassroomList(examPlanMapper.getPlanExamClassroom(examPlanId));
        int maxNumber = 0;
        for (Long classroomId : examPlanVO.getClassroomList()) {
            Long maxCandidates = classroomMapper.selectById(classroomId).getMaxCandidates();
            maxNumber += maxCandidates;
        }

        //编写sql查询当前考试人数

        Long actualCount = enrollMapper.getEnrollCount(examPlanId);
        //        if (actualCount + 1 > maxNumber) {
        //            throw new BusinessException("报名考试计划的人数已经达到:" + maxNumber);
        //        }
        ValidationUtils.throwIf(actualCount + 1 > maxNumber, "报名考试计划的人数已满:" + maxNumber);
        //校验当前时间与考试时间对比
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime enrollStartTime = examPlanDO.getEnrollStartTime();
        LocalDateTime enrollEndTime = examPlanDO.getEnrollEndTime();
        ValidationUtils.throwIf(now.isBefore(enrollStartTime) || now.isAfter(enrollEndTime), "当前时间不在报名时间内");
    }

    @Override
    public IdentityCardExamInfoVO viewIdentityCard(Long examPlanId) {
        ValidationUtils.throwIfNull(examPlanId, "请选择考试计划");
        Long userId = TokenLocalThreadUtil.get().getUserId();
        IdentityCardExamInfoVO identityCardExamInfoVO = enrollMapper.viewIdentityCardInfo(examPlanId, userId);
        identityCardExamInfoVO.setExamNumber(aesWithHMAC.verifyAndDecrypt(identityCardExamInfoVO.getExamNumber()));
        return identityCardExamInfoVO;
    }

    @Override
    public PageResp<ExamCandidateVO> getExamCandidates(EnrollQuery enrollQuery,
                                                       PageQuery pageQuery,
                                                       Long planId,
                                                       Long classroomId) {
        // 根据考试计划ID和考场ID查出考生的信息
        QueryWrapper<EnrollDO> queryWrapper = this.buildQueryWrapper(enrollQuery);

        queryWrapper.eq("ted_enroll.is_deleted", 0);
        queryWrapper.eq("ted_enroll.classroom_id", classroomId);
        queryWrapper.eq("ted_enroll.exam_plan_id", planId);
        super.sort(queryWrapper, pageQuery);

        IPage<ExamCandidateVO> page = baseMapper.getExamCandidates(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);
        // 将查询结果转换成 PageResp 对象
        PageResp<ExamCandidateVO> pageResp = PageResp.build(page, ExamCandidateVO.class);
        pageResp.setList(pageResp.getList().stream().map(item -> {
            item.setUsername(aesWithHMAC.verifyAndDecrypt(item.getUsername()));
            item.setExamNumber(aesWithHMAC.verifyAndDecrypt(item.getExamNumber()));
            return item;
        }).collect(Collectors.toList()));
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    @Override
    public void cancelEnroll(Long examPlanId) {
        Long userId = TokenLocalThreadUtil.get().getUserId();
        enrollMapper.cancelEnroll(examPlanId, userId);
    }

    //重写后台管理端分页
    @Override
    public PageResp<EnrollResp> page(EnrollQuery query, PageQuery pageQuery) {
        QueryWrapper<EnrollDO> queryWrapper = this.buildQueryWrapper(query);
        if (query.getNickName() != null) {
            queryWrapper.like("su.nickName", query.getNickName());
        }
        if (query.getPlanName() != null) {
            queryWrapper.like("tep.exam_plan_name", query.getPlanName());
        }
        queryWrapper.eq("te.is_deleted", 0).eq("te.enroll_Status", 1);
        super.sort(queryWrapper, pageQuery);
        IPage<EnrollResp> page = baseMapper.getEnrollPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);
        PageResp<EnrollResp> pageResp = PageResp.build(page, EnrollResp.class);
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }
}