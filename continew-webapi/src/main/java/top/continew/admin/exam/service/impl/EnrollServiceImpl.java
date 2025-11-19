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

import cn.crane4j.core.util.StringUtils;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import net.dreamlu.mica.core.result.R;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import top.continew.admin.common.constant.EnrollStatusConstant;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.vo.ApplyListVO;
import top.continew.admin.exam.model.vo.ExamCandidateVO;
import top.continew.admin.exam.model.vo.ExamPlanVO;
import top.continew.admin.exam.model.vo.IdentityCardExamInfoVO;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.query.EnrollQuery;
import top.continew.admin.exam.model.req.EnrollReq;
import top.continew.admin.exam.service.EnrollService;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    private AESWithHMAC aesWithHMAC;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private EnrollService enrollService;

    @Resource
    private ExamineePaymentAuditMapper examineePaymentAuditMapper;

    @Resource
    private UserMapper userMapper;

    private final RestTemplate restTemplate = new RestTemplate();


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
        //查询项目对应的收费标准
        QueryWrapper<ProjectDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",examPlanMapper.selectById(examPlanId).getExamProjectId())
            .eq("is_deleted", 0)
            .select("exam_fee");
        ProjectDO projectDO = projectMapper.selectOne(queryWrapper);
        // 返回所有报名详情列表
        detailEnrollList.setExamFee(projectDO.getExamFee());
        detailEnrollList.setCertificates(enrollMapper.getCertificateList(examPlanId));
        detailEnrollList.setDocumentNames(enrollMapper.getDocumentList(examPlanId));

        return detailEnrollList;
    }

    //
    //获取带有状态的报名列表
    @Override
    public PageResp<EnrollStatusResp> getEnrollStatusList(EnrollQuery query, PageQuery pageQuery, Long enrollStatus) {
        // 1️ 获取登录用户
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();

        // 2⃣ 构建基础查询
        QueryWrapper<EnrollDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tep.is_deleted", 0);
        queryWrapper.eq("tep.status", 3); // 默认查有效考试计划

        // 3️ 按状态过滤
        if (enrollStatus != null) {
            switch (enrollStatus.intValue()) {
                case 0: // 未报名
                    queryWrapper.nested(qw -> qw.eq("te.enroll_status", 0).or().isNull("te.enroll_status"));
                    break;
                case 1: // 已报名
                    queryWrapper.eq("te.enroll_status", 1);
                    break;
                case 2: // 已完成
                    queryWrapper.eq("te.enroll_status", 2).eq("tep.status", 6);
                    break;
                case 4: // 审核中
                    queryWrapper.eq("te.enroll_status", 4);
                    break;
                case 5: // 虚假信息
                    queryWrapper.eq("te.enroll_status", 5);
                    break;
                case 6: // 已过期
                    queryWrapper.eq("tep.status", 6);
                    break;
                default:
                    break;
            }
        }

        // 4️ 排序
        super.sort(queryWrapper, pageQuery);

        // 5️ 执行分页查询
        IPage<EnrollStatusResp> page = baseMapper.getEnrollList(
                new Page<>(pageQuery.getPage(), pageQuery.getSize()),
                queryWrapper,
                userTokenDo.getUserId()
        );

        // 6️ 封装返回结果
        PageResp<EnrollStatusResp> pageResp = PageResp.build(page, EnrollStatusResp.class);

        // 7️ 填充扩展字段
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
        return enrollInfoResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean signUp(EnrollReq enrollReq, Long userId, Integer status) {
        synchronized (this) {
            Long planId = enrollReq.getExamPlanId();
            ExamPlanDO examPlanDO = examPlanMapper.selectById(planId);
            ExamPlanVO examPlanVO = new ExamPlanVO();
            BeanUtils.copyProperties(examPlanDO, examPlanVO);
            examPlanVO.setClassroomList(examPlanMapper.getPlanExamClassroom(planId));

            SecureRandom random = new SecureRandom();
            List<Long> classroomList = examPlanVO.getClassroomList();
            Collections.shuffle(classroomList);

            Long assignedClassroomId = null;
            for (Long classroomId : classroomList) {
                int updated = classroomMapper.incrementEnrolledCount(classroomId, examPlanDO.getId());
                if (updated > 0) {
                    assignedClassroomId = classroomId;
                    break;
                }
            }

            if (status == 1) {
                if (assignedClassroomId == null) {
                    throw new BusinessException("当前考试已满员，已自动将该计划剩余审核改为不通过");
                }

                // 准考证号
                String classroomId = String.format("%03d", assignedClassroomId);
                String planYear = examPlanVO.getPlanYear();
                String seatPart = String.format("%03d", classroomMapper.getSeatNumber(assignedClassroomId, examPlanDO.getId()));
                String randomPart = String.format("%04d", random.nextInt(10000));
                String examNumber = planYear + randomPart + classroomId + seatPart;

                // 检查报名表是否已存在
                EnrollDO existing = enrollMapper.selectOne(new LambdaQueryWrapper<EnrollDO>()
                        .eq(EnrollDO::getExamPlanId, planId)
                        .eq(EnrollDO::getUserId, userId)
                        .eq(EnrollDO::getIsDeleted, false)
                        .last("LIMIT 1"));

                if (existing != null) {
                    // 如果之前有记录（比如上传资料时插入的状态=4），则只更新状态为已报名
                    existing.setEnrollStatus(EnrollStatusConstant.SIGNED_UP);
                    existing.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                    existing.setSeatId(Long.valueOf(seatPart));
                    existing.setClassroomId(Long.valueOf(classroomId));
                    existing.setUpdateTime(LocalDateTime.now());
                    enrollMapper.updateById(existing);
                    return true;
                } else {
                    // 如果之前没有记录，则插入新报名
                    EnrollDO enrollDO = new EnrollDO();
                    BeanUtils.copyProperties(enrollReq, enrollDO);
                    enrollDO.setUserId(userId);
                    enrollDO.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                    enrollDO.setSeatId(Long.valueOf(seatPart));
                    enrollDO.setClassroomId(Long.valueOf(classroomId));
                    enrollDO.setEnrollStatus(EnrollStatusConstant.SIGNED_UP); // 已报名
                    enrollDO.setIsDeleted(false);
                    enrollDO.setCreateTime(LocalDateTime.now());
                    enrollDO.setUpdateTime(LocalDateTime.now());
                    return enrollMapper.insert(enrollDO) > 0;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean signUpdate(EnrollReq enrollReq, Long userId, Integer status) {
        synchronized (this) {
            Long planId = enrollReq.getExamPlanId();
            ExamPlanDO examPlanDO = examPlanMapper.selectById(planId);
            ExamPlanVO examPlanVO = new ExamPlanVO();
            BeanUtils.copyProperties(examPlanDO, examPlanVO);
            examPlanVO.setClassroomList(examPlanMapper.getPlanExamClassroom(planId));

            SecureRandom random = new SecureRandom();
            List<Long> classroomList = examPlanVO.getClassroomList();
            Collections.shuffle(classroomList);

            Long assignedClassroomId = null;
            for (Long classroomId : classroomList) {
                int updated = classroomMapper.incrementEnrolledCount(classroomId, examPlanDO.getId());
                if (updated > 0) {
                    assignedClassroomId = classroomId;
                    break;
                }
            }

            if (status == 1) {
                if (assignedClassroomId == null) {
                    throw new BusinessException("当前考试已满员，已自动将该计划剩余审核改为不通过");
                }

                // 准考证号
                String classroomId = String.format("%03d", assignedClassroomId);
                String planYear = examPlanVO.getPlanYear();
                String seatPart = String.format("%03d", classroomMapper.getSeatNumber(assignedClassroomId, examPlanDO.getId()));
                String randomPart = String.format("%04d", random.nextInt(10000));
                String examNumber = planYear + randomPart + classroomId + seatPart;

                // 检查报名表是否已存在
                EnrollDO existing = enrollMapper.selectOne(new LambdaQueryWrapper<EnrollDO>()
                        .eq(EnrollDO::getExamPlanId, planId)
                        .eq(EnrollDO::getUserId, userId)
                        .eq(EnrollDO::getIsDeleted, false)
                        .last("LIMIT 1"));

                if (existing != null) {
                    // 如果之前有记录（比如上传资料时插入的状态=4），则只更新状态为已报名
                    existing.setEnrollStatus(EnrollStatusConstant.SIGNED_UP);
                    existing.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                    existing.setSeatId(Long.valueOf(seatPart));
                    existing.setClassroomId(Long.valueOf(classroomId));
                    existing.setUpdateTime(LocalDateTime.now());
                    enrollMapper.updateById(existing);
                    return true;
                } else {
                    // 如果之前没有记录，则插入新报名
                    EnrollDO enrollDO = new EnrollDO();
                    BeanUtils.copyProperties(enrollReq, enrollDO);
                    enrollDO.setUserId(userId);
                    enrollDO.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                    enrollDO.setSeatId(Long.valueOf(seatPart));
                    enrollDO.setClassroomId(Long.valueOf(classroomId));
                    // 已报名
                    enrollDO.setEnrollStatus(EnrollStatusConstant.SIGNED_UP);
                    enrollDO.setIsDeleted(false);
                    enrollDO.setCreateTime(LocalDateTime.now());
                    enrollDO.setUpdateTime(LocalDateTime.now());
                    return enrollMapper.insert(enrollDO) > 0;
                }
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
     * 校验时间人数
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
    /**
     * 生成并查看准考证信息
     *
     * @param examPlanId 考试计划ID
     * @return 准考证信息
     */
    @Override
    public IdentityCardExamInfoVO viewIdentityCard(Long examPlanId) {
        // 前置校验：入参 + 登录
        ValidationUtils.throwIfNull(examPlanId, "请选择考试计划");
        UserTokenDo userToken = TokenLocalThreadUtil.get();
        ValidationUtils.throwIfNull(userToken, "用户未登录，无法查看准考证信息");
        Long userId = userToken.getUserId();
        String userName = userToken.getNickname();

        //校验是否缴费
        ExamineePaymentAuditDO examineePaymentAuditDO = examineePaymentAuditMapper.selectOne(
                new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                        .eq(ExamineePaymentAuditDO::getExamPlanId,examPlanId)
                        .eq(ExamineePaymentAuditDO::getExamineeId,userId)
                        .eq(ExamineePaymentAuditDO::getIsDeleted,false)
                        .select(ExamineePaymentAuditDO::getAuditStatus)
        );
        ValidationUtils.throwIfNull(examineePaymentAuditDO, "未找到考试缴费记录，请先提交缴费凭证再查看准考证");
        ValidationUtils.throwIf(!Objects.equals(examineePaymentAuditDO.getAuditStatus(), 2),
                "缴费凭证未上传或未审核通过，无法查看准考证");


        // 校验报名资格
        SpecialCertificationApplicantDO applicantDO = specialCertificationApplicantMapper.selectOne(
                new LambdaQueryWrapper<SpecialCertificationApplicantDO>()
                        .eq(SpecialCertificationApplicantDO::getPlanId, examPlanId)
                        .eq(SpecialCertificationApplicantDO::getCandidatesId, userId)
                        .eq(SpecialCertificationApplicantDO::getIsDeleted, false)
                        .last("LIMIT 1")
        );
        ValidationUtils.throwIfNull(applicantDO, "未找到报名申请，请先提交报名再查看准考证");
        ValidationUtils.throwIf(!Objects.equals(applicantDO.getStatus(), 1),
                "报名状态无效（未通过/已取消），无法查看准考证");


        // 校验考试计划
        ExamPlanDO examPlanDO = examPlanMapper.selectById(examPlanId);
        ValidationUtils.throwIf(examPlanDO == null || examPlanDO.getIsFinalConfirmed() != 1,
                "考试计划考试时间和考试地点未最终确认，无法查看准考证信息");

        // 先查是否已生成准考证（避免重复生成）
        IdentityCardExamInfoVO identityCardExamInfoVO = enrollMapper.viewIdentityCardInfo(examPlanId, userId);

        if (identityCardExamInfoVO == null || StringUtils.isBlank(identityCardExamInfoVO.getExamNumber())) {
            // 若未生成准考证，则执行报名/准考证生成逻辑（要求 signUp 支持幂等）
            EnrollReq enrollReq = new EnrollReq();
            enrollReq.setExamPlanId(examPlanId);
            Boolean signUpResult = enrollService.signUpdate(enrollReq, userId, applicantDO.getStatus());
            ValidationUtils.throwIf(!signUpResult, "报名失败，无法生成准考证信息");

            // 再次查询准考证
            identityCardExamInfoVO = enrollMapper.viewIdentityCardInfo(examPlanId, userId);
            ValidationUtils.throwIfNull(identityCardExamInfoVO, "准考证信息生成失败，请稍后重试");
        }

        // 补全返回信息
        identityCardExamInfoVO.setUserId(userId);
        identityCardExamInfoVO.setName(userName);
        identityCardExamInfoVO.setShowStatus(1);
        ValidationUtils.throwIfBlank(identityCardExamInfoVO.getExamNumber(), "准考证号为空，无法查看");
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

        queryWrapper.eq("te.is_deleted", 0);
        queryWrapper.eq("te.classroom_id", classroomId);
        queryWrapper.eq("te.exam_plan_id", planId);
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

    @Transactional(rollbackFor = Exception.class)
    public void cancelEnroll(Long examPlanId) {
        Long userId = TokenLocalThreadUtil.get().getUserId();
        ValidationUtils.throwIfNull(examPlanId, "考试计划ID不能为空");
        ValidationUtils.throwIfNull(userId, "用户ID不能为空");

        // 获取报名详情（含考生ID筛选，避免跨用户查询）
        EnrollDetailResp detailEnroll = enrollMapper.getAllDetailEnrollList(examPlanId, userId);
        ValidationUtils.throwIfNull(detailEnroll, "未找到当前用户的报名信息，无法取消报名");

        // 报名状态校验
        Integer enrollStatus = detailEnroll.getEnrollStatus();
        // 黑名单校验（状态6）
        ValidationUtils.throwIf(Objects.equals(enrollStatus, 6),
                "被标记黑名单，无法报名和取消报名");

        // 考试时间校验
        LocalDateTime examStartTime = detailEnroll.getExamStartTime();
        ValidationUtils.throwIfNull(examStartTime, "考试开始时间为空，无法取消报名");
        LocalDateTime now = LocalDateTime.now();
        boolean canCancelByTime = ChronoUnit.DAYS.between(now, examStartTime) >= 5;
        ValidationUtils.throwIf(!canCancelByTime, "距离考试不足5天，无法取消报名");

        // 缴费状态校验（仅当前用户的缴费记录）
        ExamineePaymentAuditDO examineePaymentAuditDO = examineePaymentAuditMapper.selectOne(
                new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                        .eq(ExamineePaymentAuditDO::getExamPlanId, examPlanId)
                        .eq(ExamineePaymentAuditDO::getExamineeId, userId) // 关键：加考生ID筛选
                        .eq(ExamineePaymentAuditDO::getIsDeleted, false)
                        .select(ExamineePaymentAuditDO::getAuditStatus)
        );
        ValidationUtils.throwIf(
                examineePaymentAuditDO != null && Objects.equals(examineePaymentAuditDO.getAuditStatus(), 2),
                "缴费审核已通过，请先申请退款，通过后，再取消报名"
        );

        // 执行删除
        examineePaymentAuditMapper.deleteFromPayment(examPlanId, userId);
        enrollMapper.deleteFromEnroll(examPlanId, userId);
        enrollMapper.deleteFromApplicant(examPlanId, userId);
    }

    /**
     * 下载某个考试的缴费通知单
     * @param enrollId
     * @return
     */
    @Override
    public ResponseEntity<byte[]> downloadAuditNotice(Long enrollId) {
        EnrollDO enrollDO = baseMapper.selectById(enrollId);
        ValidationUtils.throwIfNull(enrollDO,"未查询到报名信息");
        ExamineePaymentAuditDO examineePaymentAuditDO = examineePaymentAuditMapper
                .selectOne(new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                        .eq(ExamineePaymentAuditDO::getEnrollId, enrollId));
        ValidationUtils.throwIfNull(examineePaymentAuditDO,"未生成缴费通知单");
        String auditNoticeUrl = examineePaymentAuditDO.getAuditNoticeUrl();
        ValidationUtils.throwIfNull(auditNoticeUrl, "未生成缴费通知单");
        try {
            // 使用 RestTemplate 下载远程文件
            byte[] pdfBytes = restTemplate.getForObject(new URI(auditNoticeUrl), byte[].class);
            // 设置 HTTP 头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 下载某个班级的考试缴费通知单
     * @param classId
     * @param planId
     * @return
     */
    @Override
    public ResponseEntity<byte[]> downloadBatchAuditNotice(Long classId, Long planId) {
        List<WorkerAuditNoticeResp> auditNoticeList = baseMapper.selectAuditNoticeToClass(classId, planId);
        ValidationUtils.throwIfEmpty(auditNoticeList, "该班级下未查询到报名信息");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {

            for (WorkerAuditNoticeResp notice : auditNoticeList) {
                String url = notice.getAuditNoticeUrl();
                String nickname = notice.getNickname();

                if (StrUtil.isBlank(url)) continue;

                byte[] pdfBytes = restTemplate.getForObject(new URI(url), byte[].class);
                if (pdfBytes == null || pdfBytes.length == 0) continue;

                String entryName = nickname + "_缴费通知单_" + new Date().getTime() + ".pdf";

                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(pdfBytes);
                zos.closeEntry();
            }

            zos.finish();

            byte[] zipBytes = baos.toByteArray();

            // 设置 HTTP 响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    /**
     * 重写删除
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 1️ 查询报名信息
        List<EnrollDO> enrollDOList = baseMapper.selectByIds(ids);
        ValidationUtils.throwIfEmpty(enrollDOList, "未选择取消报名的数据！");

        // 2️ 获取考试计划ID集合（用于查询计划信息）
        Set<Long> planIds = enrollDOList.stream()
                .map(EnrollDO::getExamPlanId)
                .collect(Collectors.toSet());

        // 3️ 批量查询考试计划信息
        List<ExamPlanDO> examPlanList = examPlanMapper.selectBatchIds(planIds);
        Map<Long, ExamPlanDO> planMap = examPlanList.stream()
                .collect(Collectors.toMap(ExamPlanDO::getId, Function.identity()));

        // 4️ 考试时间校验
        LocalDateTime now = LocalDateTime.now();
        for (EnrollDO enroll : enrollDOList) {
            ExamPlanDO plan = planMap.get(enroll.getExamPlanId());
            ValidationUtils.throwIfNull(plan, "找不到对应的考试计划：" + enroll.getExamPlanId());
            LocalDateTime examStartTime = plan.getStartTime();
            ValidationUtils.throwIfNull(examStartTime, "考试开始时间为空，无法取消报名");
            boolean canCancel = ChronoUnit.DAYS.between(now, examStartTime) >= 5;
            ValidationUtils.throwIf(!canCancel, "距离考试不足5天，无法取消报名");
        }

        // 5️ 批量删除缴费审核记录
        // 构造批量条件：同一语句删除多条
        examineePaymentAuditMapper.delete(
                new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                        .in(ExamineePaymentAuditDO::getEnrollId,
                                enrollDOList.stream().map(EnrollDO::getId).toList())
        );

        // 6️ 删除报名记录（父类批量删除）
        super.delete(ids);
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
        queryWrapper.eq("te.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);
        IPage<EnrollResp> page;
        // 机构查看报名情况
        if (ObjectUtil.isNotEmpty(query.getPlanId())) {
            page = baseMapper.getWorkerApplyList(new Page<>(pageQuery.getPage(), pageQuery
                    .getSize()), queryWrapper);
        }else {
            queryWrapper.eq("te.enroll_Status", 1);
            page = baseMapper.getEnrollPage(new Page<>(pageQuery.getPage(), pageQuery
                    .getSize()), queryWrapper);
        }
        PageResp<EnrollResp> pageResp = PageResp.build(page, EnrollResp.class);
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }
}