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
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.common.constant.*;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.*;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.req.GenerateReq;
import top.continew.admin.exam.model.req.InputScoresReq;
import top.continew.admin.exam.model.vo.CandidatesClassRoomVo;
import top.continew.admin.invigilate.mapper.PlanInvigilateMapper;
import top.continew.admin.invigilate.model.entity.PlanInvigilateDO;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.model.entity.TedOrgUser;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.query.ExamRecordsQuery;
import top.continew.admin.exam.model.req.ExamRecordsReq;
import top.continew.admin.exam.model.resp.ExamRecordsDetailResp;
import top.continew.admin.exam.model.resp.ExamRecordsResp;
import top.continew.admin.exam.service.ExamRecordsService;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 考试记录业务实现
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
@Service
@RequiredArgsConstructor
public class ExamRecordsServiceImpl extends BaseServiceImpl<ExamRecordsMapper, ExamRecordsDO, ExamRecordsResp, ExamRecordsDetailResp, ExamRecordsQuery, ExamRecordsReq> implements ExamRecordsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private LocationClassroomMapper locationClassroomMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private EnrollMapper enrollMapper;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Value("${certificate.road-exam-type-id}")
    public Long roadExamTypeId;

    private final ExamViolationMapper examViolationMapper;

    private final LicenseCertificateMapper licenseCertificateMapper;

    private final PlanInvigilateMapper planInvigilateMapper;

    private final OrgUserMapper orgUserMapper;

    @Override
    public PageResp<ExamRecordsResp> examRecordsPage(ExamRecordsQuery query, PageQuery pageQuery) {
        //根据mapper查出考生名+证件名
        //封装返回结果
        QueryWrapper<ExamRecordsDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("ter.is_deleted", 0);
        String username = query.getUsername();
        if (StrUtil.isNotBlank(username)) {
            queryWrapper.eq("su.username", aesWithHMAC.encryptAndSign(username));
        }
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        if (Boolean.TRUE.equals(query.getIsOrgQuery())) {
            // 查询当前用户属于哪个机构
            UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
            TedOrgUser tedOrgUser = orgUserMapper.selectOne(new LambdaQueryWrapper<TedOrgUser>()
                .eq(TedOrgUser::getUserId, userTokenDo.getUserId())
                .select(TedOrgUser::getOrgId, TedOrgUser::getId)
                .last("limit 1"));
            queryWrapper.eq("toc.org_id", tedOrgUser.getOrgId());
        }
        // 查询
        IPage<ExamRecordDTO> page = baseMapper.getexamRecords(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper, roadExamTypeId);
        ;
        // 将查询结果转换成 PageResp 对象
        PageResp<ExamRecordsResp> pageResp = PageResp.build(page, super.getListClass());
        List<ExamRecordsResp> list = pageResp.getList();
        if (!ObjectUtils.isEmpty(list)) {
            pageResp.setList(list.stream().map(item -> {
                item.setUsername(aesWithHMAC.verifyAndDecrypt(item.getUsername()));
                return item;
            }).toList());
        }
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;
    }

    /**
     * 根据ID获取考试记录详情和新加字段
     *
     * @param id 考试记录ID
     * @return 考试记录详情
     */
    @Override
    public ExamRecordsDetailResp getRecordsById(Long id) {
        ExamRecordsDO examRecords = baseMapper.getRecordsById(id);
        ExamRecordsDetailResp detail = BeanUtil.toBean(examRecords, this.getDetailClass());
        this.fill(detail);
        return detail;
    }

    /**
     * 根据身份证号获取考生所有的考场
     *
     * @param username
     * @return
     */
    @Override
    public List<CandidatesClassRoomVo> getCandidatesClassRoom(String username) {
        String rawUsername = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(username));
        String aesUsername = aesWithHMAC.encryptAndSign(rawUsername);
        // 通过身份证获取考生信息
        UserDO userDO = userMapper.selectByUsername(aesUsername);
        ValidationUtils.throwIf(ObjectUtils.isEmpty(userDO), "无考试内容");
        // 根据考生ID获取考生的所有考试考场
        List<ExamRecordsDO> examRecordsDOS = baseMapper.selectList(new LambdaQueryWrapper<ExamRecordsDO>()
            .eq(ExamRecordsDO::getCandidateId, userDO.getId())
            .eq(ExamRecordsDO::getReviewStatus, ExamRecordsReviewStatusEnum.WAITING_EXAMINATION.getValue())
            .eq(ExamRecordsDO::getRegistrationProgress, ExamRecordsRegisterationProgressEnum.REVIEWED.getValue()));
        ValidationUtils.throwIf(ObjectUtils.isEmpty(examRecordsDOS), "无考试内容");
        // 根据计划ID查询考场地址
        List<Long> planIds = examRecordsDOS.stream().map(item -> {
            return item.getPlanId();
        }).collect(Collectors.toList());
        //        List<ExamPlanDO> examPlanDOS = examPlanMapper.selectList(new LambdaQueryWrapper<ExamPlanDO>()
        //            .in(ExamPlanDO::getId, planIds));
        //        List<Long> locationIds = examPlanDOS.stream().map(item -> {
        //            return item.getLocationId();
        //        }).collect(Collectors.toList());
        List<Long> locationIds = examPlanMapper.getPlanLocationIdsById(planIds);
        List<LocationClassroomDO> locationClassroomDOS = locationClassroomMapper
            .selectList(new LambdaQueryWrapper<LocationClassroomDO>()
                .in(LocationClassroomDO::getLocationId, locationIds));
        List<Integer> roomIds = locationClassroomDOS.stream().map(item -> {
            return item.getClassroomId();
        }).collect(Collectors.toList());
        List<ClassroomDO> classroomDOS = classroomMapper.selectList(new LambdaQueryWrapper<ClassroomDO>()
            .in(ClassroomDO::getId, roomIds));
        return Collections.emptyList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void candidatesAdd(ExamRecordsDO examRecordsDO) {
        // 1 查询是否已有考试记录
        ExamRecordsDO existingRecord = baseMapper.selectOne(new LambdaQueryWrapper<ExamRecordsDO>()
            .eq(ExamRecordsDO::getCandidateId, examRecordsDO.getCandidateId())
            .eq(ExamRecordsDO::getPlanId, examRecordsDO.getPlanId())
            .last("limit 1"));

        // 2 修改报名状态基础信息
        LambdaUpdateWrapper<EnrollDO> enrollUpdate = new LambdaUpdateWrapper<>();
        enrollUpdate.eq(EnrollDO::getUserId, examRecordsDO.getCandidateId())
            .eq(EnrollDO::getExamPlanId, examRecordsDO.getPlanId())
            .set(EnrollDO::getEnrollStatus, EnrollStatusConstant.COMPLETED)
            .set(EnrollDO::getExamStatus, !ExamViolationTypeEnum.NO_SWITCH.getValue()
                .equals(examRecordsDO.getViolationType())
                    ? EnrollStatusConstant.VIOLATION
                    : EnrollStatusConstant.SUBMITTED);

        enrollMapper.update(enrollUpdate);

        // 3 获取成绩（默认0）
        int newScore = examRecordsDO.getExamScores() != null ? examRecordsDO.getExamScores() : 0;

        // 4 判断是否补考已有记录
        if (existingRecord != null && ExamRecordAttemptEnum.RETAKE.getValue().equals(existingRecord.getAttemptType())) {
            int oldScore = existingRecord.getExamScores() != null ? existingRecord.getExamScores() : 0;

            // 4.1 新成绩高 → 更新旧记录
            if (newScore > oldScore) {
                existingRecord.setExamScores(newScore);
                existingRecord.setExamPaper(examRecordsDO.getExamPaper());
                baseMapper.updateById(existingRecord);
            }

            // 4.2 不管新旧高低，都处理违规
            handleViolation(examRecordsDO);

            // 补考逻辑结束
            return;
        }

        // 5 首考或无旧记录 → 处理违规
        Integer violationType = examRecordsDO.getViolationType();
        if (!Objects.equals(violationType, ExamViolationTypeEnum.NO_SWITCH.getValue())) {
            enrollUpdate.set(EnrollDO::getExamStatus, EnrollStatusConstant.VIOLATION);
            handleViolation(examRecordsDO);
        } else {
            enrollUpdate.set(EnrollDO::getExamStatus, EnrollStatusConstant.SUBMITTED);
        }

        // 7 插入考试记录
        // 获取计划是否有实操考试、道路考试
        ExamPresenceDTO examPlanOperAndRoadDTO = baseMapper.hasOperationOrRoadExam(examRecordsDO
            .getPlanId(), roadExamTypeId);
        // 标记是否有实操/道路
        boolean hasOper = ProjectHasExamTypeEnum.YES.getValue().equals(examPlanOperAndRoadDTO.getIsOperation());
        boolean hasRoad = ProjectHasExamTypeEnum.YES.getValue().equals(examPlanOperAndRoadDTO.getIsRoad());

        // 只有两个都没有时直接合格
        if (!hasOper && !hasRoad) {
            examRecordsDO.setOperScores(ExamRecordConstants.PASSING_SCORE);
            examRecordsDO.setOperInputStatus(ExamScoreEntryStatusEnum.ENTERED.getValue());

            examRecordsDO.setRoadInputStatus(ExamScoreEntryStatusEnum.ENTERED.getValue());

            examRecordsDO.setExamResultStatus(examRecordsDO.getExamScores() >= ExamRecordConstants.PASSING_SCORE
                ? ExamResultStatusEnum.PASSED.getValue()
                : ExamResultStatusEnum.FAILED.getValue());
        } else {
            // 如果任意一个存在，则成绩未录入
            if (!hasOper) {
                examRecordsDO.setOperScores(ExamRecordConstants.PASSING_SCORE);
                examRecordsDO.setOperInputStatus(ExamScoreEntryStatusEnum.ENTERED.getValue());
            }

            if (!hasRoad) {
                examRecordsDO.setRoadInputStatus(ExamScoreEntryStatusEnum.ENTERED.getValue());
            }

            examRecordsDO.setExamResultStatus(ExamResultStatusEnum.NOT_ENTERED.getValue());
        }

        // 插入记录
        baseMapper.insert(examRecordsDO);
    }

    /**
     * 录入实操、导入成绩
     *
     * @param inputScoresReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean inputScores(InputScoresReq inputScoresReq) {
        List<Long> recordIds = inputScoresReq.getRecordIds();
        ValidationUtils.throwIfEmpty(recordIds, "未选择考试记录");

        Integer scoresType = inputScoresReq.getScoresType();
        Integer scores = inputScoresReq.getScores();
        ValidationUtils.throwIfNull(scoresType, "成绩类型不能为空");
        ValidationUtils.throwIfNull(scores, "成绩不能为空");

        // 查询考试记录
        List<ExamRecordsDO> examRecordsDOS = baseMapper.selectByIds(recordIds);
        ValidationUtils.throwIfEmpty(examRecordsDOS, "所选的考试记录不存在");

        // 检查是否已生成证书
        List<Long> candidateIds = examRecordsDOS.stream()
            .filter(record -> ExamRecprdsHasCertofocateEnum.YES.getValue().equals(record.getIsCertificateGenerated()))
            .map(ExamRecordsDO::getCandidateId)
            .toList();

        if (!candidateIds.isEmpty()) {
            List<UserDO> users = userMapper.selectByIds(candidateIds);
            List<String> nicknames = users.stream().map(UserDO::getNickname).toList();
            ValidationUtils.throwIfNotEmpty(nicknames, String.join("、", nicknames) + " 已生成证书信息，无法再次录入成绩");
        }

        // 去重计划id
        List<Long> distinctPlanIds = examRecordsDOS.stream().map(ExamRecordsDO::getPlanId).distinct().toList();

        // 查询计划支持的考试类型
        List<CheckPlanHasExamTypeDTO> planExamTypes = baseMapper.checkPlanHasExamType(distinctPlanIds, roadExamTypeId);

        // 提前构建 planId -> CheckPlanHasExamTypeDTO 映射，提高查找效率
        Map<Long, CheckPlanHasExamTypeDTO> planTypeMap = planExamTypes.stream()
            .collect(Collectors.toMap(CheckPlanHasExamTypeDTO::getPlanId, dto -> dto));

        // 检查计划是否支持当前录入类型
        List<String> invalidPlans = planExamTypes.stream()
            .filter(dto -> (ExamScoreInputTypeEnum.OPER.getValue().equals(scoresType) && ProjectHasExamTypeEnum.NO
                .getValue()
                .equals(dto.getIsOperation())) || (ExamScoreInputTypeEnum.ROAD.getValue()
                    .equals(scoresType) && ProjectHasExamTypeEnum.NO.getValue().equals(dto.getIsRoad())))
            .map(CheckPlanHasExamTypeDTO::getPlanName)
            .distinct()
            .toList();

        ValidationUtils.throwIfNotEmpty(invalidPlans, String.join("、", invalidPlans) + " 计划不支持当前类型成绩录入");

        // 修改计划中所有监考员状态为已完成
        planInvigilateMapper.update(new LambdaUpdateWrapper<PlanInvigilateDO>()
            .in(PlanInvigilateDO::getExamPlanId, distinctPlanIds)
            .set(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.FINISHED.getValue()));

        // 遍历考试记录：设置成绩 + 计算合格状态
        examRecordsDOS.forEach(record -> {
            // 根据录入类型设置成绩
            if (ExamScoreInputTypeEnum.OPER.getValue().equals(scoresType)) {
                record.setOperScores(scores);
                record.setOperInputStatus(ExamScoreEntryStatusEnum.ENTERED.getValue());
            } else if (ExamScoreInputTypeEnum.ROAD.getValue().equals(scoresType)) {
                record.setRoadScores(scores);
                record.setRoadInputStatus(ExamScoreEntryStatusEnum.ENTERED.getValue());
            }

            // 获取计划考试类型
            CheckPlanHasExamTypeDTO planType = planTypeMap.get(record.getPlanId());
            boolean isOperRequired = planType != null && ProjectHasExamTypeEnum.YES.getValue()
                .equals(planType.getIsOperation());
            boolean isRoadRequired = planType != null && ProjectHasExamTypeEnum.YES.getValue()
                .equals(planType.getIsRoad());
            System.out.println(isRoadRequired);
            // 判断是否合格
            boolean passed = true;
            if (record.getExamScores() == null || record.getExamScores() < ExamRecordConstants.PASSING_SCORE)
                passed = false;
            if (isOperRequired && (record.getOperScores() == null || record
                .getOperScores() < ExamRecordConstants.PASSING_SCORE))
                passed = false;
            if (isRoadRequired && (record.getRoadScores() == null || record
                .getRoadScores() < ExamRecordConstants.PASSING_SCORE))
                passed = false;

            record.setExamResultStatus(passed
                ? ExamResultStatusEnum.PASSED.getValue()
                : ExamResultStatusEnum.FAILED.getValue());
        });

        // 批量更新成绩 + 合格状态
        baseMapper.updateBatchById(examRecordsDOS);

        return Boolean.TRUE;
    }

    /**
     * 生成资格证书
     *
     * @param generateReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean generateQualificationCertificate(GenerateReq generateReq) {

        List<Long> recordIds = generateReq.getRecordIds();
        ValidationUtils.throwIfEmpty(recordIds, "未选择考试记录");

        // 1 查询考试记录
        List<ExamRecordsDO> examRecordsDOS = baseMapper.selectByIds(recordIds);
        ValidationUtils.throwIfEmpty(examRecordsDOS, "所选的考试记录不存在");

        // 2 查询计划是否包含实操 / 道路考试
        List<Long> distinctPlanIds = examRecordsDOS.stream().map(ExamRecordsDO::getPlanId).distinct().toList();

        List<CheckPlanHasExamTypeDTO> planExamTypes = baseMapper.checkPlanHasExamType(distinctPlanIds, roadExamTypeId);

        // planId -> DTO
        Map<Long, CheckPlanHasExamTypeDTO> planExamTypeMap = planExamTypes.stream()
            .collect(Collectors.toMap(CheckPlanHasExamTypeDTO::getPlanId, Function.identity()));

        // 3 实操成绩未录入
        List<Long> noEntryOperScoresList = examRecordsDOS.stream().filter(record -> {
            CheckPlanHasExamTypeDTO plan = planExamTypeMap.get(record.getPlanId());
            return plan != null && ProjectHasExamTypeEnum.YES.getValue()
                .equals(plan.getIsOperation()) && ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                    .equals(record.getOperInputStatus());
        }).map(ExamRecordsDO::getCandidateId).toList();

        throwWithUserNames(noEntryOperScoresList, " 未录入实操成绩，无法生成");

        // 4 道路成绩未录入
        List<Long> noEntryRoadScoresList = examRecordsDOS.stream().filter(record -> {
            CheckPlanHasExamTypeDTO plan = planExamTypeMap.get(record.getPlanId());
            return plan != null && ProjectHasExamTypeEnum.YES.getValue()
                .equals(plan.getIsRoad()) && ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                    .equals(record.getRoadInputStatus());
        }).map(ExamRecordsDO::getCandidateId).toList();

        throwWithUserNames(noEntryRoadScoresList, " 未录入道路成绩，无法生成");

        // 5 成绩是否达标（≥70）
        List<Long> notPassList = examRecordsDOS.stream().filter(record -> {
            CheckPlanHasExamTypeDTO plan = planExamTypeMap.get(record.getPlanId());
            if (record.getExamScores() < ExamRecordConstants.PASSING_SCORE) {
                return true;
            }
            if (plan != null && ProjectHasExamTypeEnum.YES.getValue().equals(plan.getIsOperation()) && record
                .getOperScores() < ExamRecordConstants.PASSING_SCORE) {
                return true;
            }
            if (plan != null && ProjectHasExamTypeEnum.YES.getValue().equals(plan.getIsRoad()) && record
                .getRoadScores() < ExamRecordConstants.PASSING_SCORE) {
                return true;
            }
            return false;
        }).map(ExamRecordsDO::getCandidateId).toList();

        throwWithUserNames(notPassList, " 成绩未达标，无法生成资格证");

        // 6 是否已生成证书
        List<Long> hasCertificateList = examRecordsDOS.stream()
            .filter(record -> ExamRecprdsHasCertofocateEnum.YES.getValue().equals(record.getIsCertificateGenerated()))
            .map(ExamRecordsDO::getCandidateId)
            .toList();

        throwWithUserNames(hasCertificateList, " 已生成证书信息，无法再次生成");

        // 判断是作业人员还是检验人员
        List<ExamRecordCertificateDTO> examRecordCertificateDTOList = Collections.emptyList();
        if (ExamPlanTypeEnum.WORKER.getValue().equals(generateReq.getPlanType())) {
            // 作业人员
            examRecordCertificateDTOList = baseMapper.selectCertificateInfoByRecordIds(recordIds);
        } else {
            // TODO 补充检验人员生成资格证
        }
        if (ObjectUtil.isNotEmpty(examRecordCertificateDTOList)) {
            LambdaQueryWrapper<LicenseCertificateDO> deleteWrapper = buildDeleteWrapper(examRecordCertificateDTOList);
            licenseCertificateMapper.delete(deleteWrapper);

            LocalDate now = LocalDate.now();
            // 生成证书信息
            List<LicenseCertificateDO> insertList = examRecordCertificateDTOList.stream().map(item -> {
                LicenseCertificateDO licenseCertificateDO = new LicenseCertificateDO();
                licenseCertificateDO.setRecordId(item.getId());
                licenseCertificateDO.setDatasource(LicenseCertificateConstant.DATASOURCE);
                licenseCertificateDO.setInfoinputorg(LicenseCertificateConstant.INFO_INPUTORG);
                licenseCertificateDO.setPsnName(item.getNickname());
                licenseCertificateDO.setIdcardNo(item.getUsername());
                String workUnit = item.getWorkUnit();
                licenseCertificateDO.setOriginalComName(ObjectUtils.isEmpty(workUnit)
                    ? LicenseCertificateConstant.WORK_UNIT
                    : workUnit);
                licenseCertificateDO.setComName(ObjectUtils.isEmpty(workUnit)
                    ? LicenseCertificateConstant.WORK_UNIT
                    : workUnit);
                licenseCertificateDO.setFacePhoto(item.getFacePhoto());
                licenseCertificateDO.setApplyDate(now);
                licenseCertificateDO.setLcnsKind(item.getCategoryName());
                // 暂时使用时间戳做证书编号
                licenseCertificateDO.setLcnsNo(LicenseCertificateConstant.LCNS_NO_PREFIX + System.currentTimeMillis());
                licenseCertificateDO.setCertDate(now);
                licenseCertificateDO.setAuthDate(now);
                licenseCertificateDO.setEndDate(now.plusYears(LicenseCertificateConstant.VALIDITY_PERIOD_YEARS));
                licenseCertificateDO.setOriginalAuthCom(LicenseCertificateConstant.ORIGINAL_AUTH_COM);
                licenseCertificateDO.setAuthCom(LicenseCertificateConstant.AUTH_COM);
                licenseCertificateDO.setPsnlcnsItem(item.getProjectName());
                licenseCertificateDO.setPsnlcnsItemCode(item.getProjectCode());
                licenseCertificateDO.setCandidateId(item.getCandidateId());
                return licenseCertificateDO;
            }).toList();
            licenseCertificateMapper.insertBatch(insertList);
            // 修改状态
            baseMapper.update(new LambdaUpdateWrapper<ExamRecordsDO>().in(ExamRecordsDO::getId, recordIds)
                .set(ExamRecordsDO::getIsCertificateGenerated, ExamRecprdsHasCertofocateEnum.YES.getValue()));
        }

        return Boolean.TRUE;
    }

    private LambdaQueryWrapper<LicenseCertificateDO> buildDeleteWrapper(List<ExamRecordCertificateDTO> dtoList) {
        final List<ExamRecordCertificateDTO> finalList = dtoList;
        LambdaQueryWrapper<LicenseCertificateDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.and(w -> {
            for (ExamRecordCertificateDTO item : finalList) {
                w.or()
                    .eq(LicenseCertificateDO::getCandidateId, item.getCandidateId())
                    .eq(LicenseCertificateDO::getPsnlcnsItemCode, item.getProjectCode());
            }
        });
        return deleteWrapper;
    }

    /**
     * 下载资格证书
     * 
     * @param recordIds
     * @return
     */
    @Override
    public ResponseEntity<byte[]> downloadQualificationCertificate(List<Long> recordIds, Integer planType) {
        // TODD 后面要加入检验人员的下载操作
        // 1. 参数校验
        ValidationUtils.throwIfEmpty(recordIds, "未选择考试记录");

        // 2. 查询考试记录
        List<ExamRecordsDO> examRecordsDOS = baseMapper.selectByIds(recordIds);
        ValidationUtils.throwIfEmpty(examRecordsDOS, "已选的考试记录不存在，请刷新后重试");

        // 3. 查询证书信息（必须已生成）
        List<LicenseCertificateDO> certList = licenseCertificateMapper
            .selectList(new LambdaQueryWrapper<LicenseCertificateDO>()
                .in(LicenseCertificateDO::getRecordId, recordIds));
        ValidationUtils.throwIfEmpty(certList, "未查询到可导出的资格证信息");

        // 4. 构造 userId + planId 对
        List<UserPlanPairDTO> pairs = examRecordsDOS.stream().map(r -> {
            UserPlanPairDTO dto = new UserPlanPairDTO();
            dto.setUserId(r.getCandidateId());
            dto.setExamPlanId(r.getPlanId());
            return dto;
        }).toList();

        // 5. 查询班级名称
        List<EnrollWithClassDTO> enrollWithClassList = baseMapper.selectEnrollWithClass(pairs);

        ValidationUtils.throwIfEmpty(enrollWithClassList, "未查询到对应班级信息");

        // key：candidateId_planId → className
        Map<String, String> classNameMap = enrollWithClassList.stream()
            .collect(Collectors.toMap(e -> e.getCandidateId() + "_" + e.getPlanId(), EnrollWithClassDTO::getClassName, (
                                                                                                                        a,
                                                                                                                        b) -> a));

        // 6. 按班级分组证书
        Map<String, List<LicenseCertificateDO>> certByClass = certList.stream().collect(Collectors.groupingBy(cert -> {
            ExamRecordsDO record = examRecordsDOS.stream()
                .filter(r -> r.getId().equals(cert.getRecordId()))
                .findFirst()
                .orElseThrow();

            return classNameMap.get(record.getCandidateId() + "_" + record.getPlanId());
        }));

        // 7. 生成 ZIP
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos, Charset.forName("GBK"))) {

            for (Map.Entry<String, List<LicenseCertificateDO>> entry : certByClass.entrySet()) {

                String className = entry.getKey();
                List<LicenseCertificateDO> classCerts = entry.getValue();

                // 班级文件夹
                String classDir = className + LicenseCertificateConstant.PATH_SEPARATOR;
                zos.putNextEntry(new ZipEntry(classDir));
                zos.closeEntry();

                // pics 文件夹
                String picsDir = classDir + LicenseCertificateConstant.FACE_PHOTO_FOLDER;
                zos.putNextEntry(new ZipEntry(picsDir));
                zos.closeEntry();

                // ===== 生成 XML（一个班级一个）=====
                String xmlContent = generateClassXml(classCerts);

                ZipEntry xmlEntry = new ZipEntry(classDir + LicenseCertificateConstant.XML_NAME);
                zos.putNextEntry(xmlEntry);
                zos.write(xmlContent.getBytes(LicenseCertificateConstant.XML_CODING));
                zos.closeEntry();

                // ===== 写照片（示例，按你实际存储路径改）=====
                for (LicenseCertificateDO cert : classCerts) {
                    if (StringUtils.isBlank(cert.getFacePhoto())) {
                        continue;
                    }

                    try (InputStream in = new URL(cert.getFacePhoto()).openStream()) {
                        ZipEntry photoEntry = new ZipEntry(picsDir + aesWithHMAC.verifyAndDecrypt(cert
                            .getIdcardNo()) + LicenseCertificateConstant.FACE_PHOTO_SUFFIX);
                        zos.putNextEntry(photoEntry);
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                        zos.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统错误");
        }

        // 8. 返回下载
        String zipName = LocalDate.now() + LicenseCertificateConstant.RETURN_FILE_SUFFIX;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(LicenseCertificateConstant.ATTACHMENT, URLEncoder
            .encode(zipName, StandardCharsets.UTF_8));

        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
    }

    private String generateClassXml(List<LicenseCertificateDO> certList) {
        StringBuilder sb = new StringBuilder();
        sb.append(LicenseCertificateConstant.XML_HEAD);
        sb.append(LicenseCertificateConstant.ROOT_LABEL_START);
        for (LicenseCertificateDO cert : certList) {
            sb.append(generateCertificateXml(cert));
        }
        sb.append(LicenseCertificateConstant.ROOT_LABEL_END);
        return sb.toString();
    }

    private String generateCertificateXml(LicenseCertificateDO cert) {
        return LicenseCertificateConstant.XML_CONTENT.formatted(safe(cert.getDatasource()), safe(cert
            .getInfoinputorg()),
            // PersonInfo
            safe(cert.getPsnName()), safe(aesWithHMAC.verifyAndDecrypt(cert.getIdcardNo())), safe(cert
                .getOriginalComName()), safe(cert.getComName()), safe(cert.getApplyType()), formatDate(cert
                    .getApplyDate(), "yyyy/MM/dd"), safe(cert.getIsVerify()), safe(cert.getIsOpr()),

            // PsnLcnsGeneral
            safe(cert.getLcnsKind()), safe(cert.getLcnsCategory()), safe(cert.getLcnsNo()), formatDate(cert
                .getCertDate(), "yyyy/MM/dd"), formatDate(cert.getAuthDate(), "yyyy/MM/dd"), formatDate(cert
                    .getEndDate(), "yyyy-MM-dd"), safe(cert.getOriginalAuthCom()), safe(cert.getAuthCom()), safe(cert
                        .getRemark()), safe(cert.getState()),

            // PsnLcnsDetail
            safe(cert.getPsnlcnsItem()), safe(cert.getPsnlcnsItemCode()), safe(cert.getPermitScope()), safe(cert
                .getDetailRemark()), safe(cert.getDetailState()));
    }

    private String safe(Object val) {
        return val == null ? "" : String.valueOf(val);
    }

    private String formatDate(LocalDate date, String pattern) {
        return date == null ? "" : date.format(DateTimeFormatter.ofPattern(pattern));
    }

    private void throwWithUserNames(List<Long> candidateIds, String message) {
        if (ObjectUtils.isEmpty(candidateIds)) {
            return;
        }
        List<String> names = userMapper.selectByIds(candidateIds).stream().map(UserDO::getNickname).toList();

        ValidationUtils.throwIfNotEmpty(names, String.join("、", names) + message);
    }

    /**
     * 处理违规记录插入
     */
    private void handleViolation(ExamRecordsDO examRecordsDO) {
        Integer violationType = examRecordsDO.getViolationType();
        if (violationType == null || ExamViolationTypeEnum.NO_SWITCH.getValue().equals(violationType)) {
            return;
        }

        ExamViolationDO violation = new ExamViolationDO();
        violation.setCandidateId(examRecordsDO.getCandidateId());
        violation.setPlanId(examRecordsDO.getPlanId());
        violation.setViolationDesc(ExamViolationTypeEnum.getDescriptionByValue(violationType));

        List<String> violationScreenshots = examRecordsDO.getViolationScreenshots();
        if (violationScreenshots != null && !violationScreenshots.isEmpty()) {
            violation.setIllegalUrl(String.join(",", violationScreenshots));
        }
        examViolationMapper.insert(violation);
    }

}