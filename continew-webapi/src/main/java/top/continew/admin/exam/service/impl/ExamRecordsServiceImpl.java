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

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.common.constant.EnrollStatusConstant;
import top.continew.admin.common.constant.ExamRecordConstants;
import top.continew.admin.common.constant.ExamRecordsRegisterationProgressEnum;
import top.continew.admin.common.constant.ExamRecordsReviewStatusEnum;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.CheckPlanHasExamTypeDTO;
import top.continew.admin.exam.model.dto.ExamPresenceDTO;
import top.continew.admin.exam.model.dto.ExamRecordDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.req.InputScoresReq;
import top.continew.admin.exam.model.vo.CandidatesClassRoomVo;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
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

import java.util.*;
import java.util.stream.Collectors;

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

    private final ExamViolationMapper examViolationMapper;


    @Value("${certificate.road-exam-type-id}")
    public Long roadExamTypeId;

    @Override
    public PageResp<ExamRecordsResp> examRecordsPage(ExamRecordsQuery query, PageQuery pageQuery) {
        //根据mapper查出考生名+证件名
        //封装返回结果
        QueryWrapper<ExamRecordsDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("ter.is_deleted", 0);

        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询
        IPage<ExamRecordDTO> page = baseMapper.getexamRecords(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper, roadExamTypeId);

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
        ExamRecordsDO existingRecord = baseMapper.selectOne(
                new LambdaQueryWrapper<ExamRecordsDO>()
                        .eq(ExamRecordsDO::getCandidateId, examRecordsDO.getCandidateId())
                        .eq(ExamRecordsDO::getPlanId, examRecordsDO.getPlanId())
                        .last("limit 1")
        );

        // 2 修改报名状态基础信息
        LambdaUpdateWrapper<EnrollDO> enrollUpdate = new LambdaUpdateWrapper<>();
        enrollUpdate.eq(EnrollDO::getUserId, examRecordsDO.getCandidateId())
                .eq(EnrollDO::getExamPlanId, examRecordsDO.getPlanId())
                .set(EnrollDO::getEnrollStatus, EnrollStatusConstant.COMPLETED)
                .set(EnrollDO::getExamStatus, !ExamViolationTypeEnum.NO_SWITCH.getValue().equals(examRecordsDO.getViolationType())
                        ? EnrollStatusConstant.VIOLATION : EnrollStatusConstant.SUBMITTED);

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
        ExamPresenceDTO examPlanOperAndRoadDTO = baseMapper.hasOperationOrRoadExam(examRecordsDO.getPlanId(), roadExamTypeId);
        if (ProjectHasExamTypeEnum.NO.getValue().equals(examPlanOperAndRoadDTO.getIsOperation())) {
            // 没有实操考试默认考试成绩为70
            examRecordsDO.setOperScores(ExamRecordConstants.PASSING_SCORE);
            examRecordsDO.setOperInputStatus(ExamScoreEntryStatusEnum.ENTERED.getValue());
        }
        if (ProjectHasExamTypeEnum.NO.getValue().equals(examPlanOperAndRoadDTO.getIsRoad())) {
            examRecordsDO.setRoadInputStatus(ExamScoreEntryStatusEnum.ENTERED.getValue());
        }
        baseMapper.insert(examRecordsDO);
    }

    /**
     * 录入实操、导入成绩
     *
     * @param inputScoresReq
     * @return
     */
    @Override
    public Boolean inputScores(InputScoresReq inputScoresReq) {

        List<Long> recordIds = inputScoresReq.getRecordIds();
        ValidationUtils.throwIfEmpty(recordIds, "未选择考试记录");

        Integer scoresType = inputScoresReq.getScoresType();
        Integer scores = inputScoresReq.getScores();
        ValidationUtils.throwIfNull(scoresType, "成绩类型不能为空");
        ValidationUtils.throwIfNull(scores, "成绩不能为空");

        List<Long> planIds = inputScoresReq.getPlanIds();
        ValidationUtils.throwIfEmpty(planIds, "未选择考试记录");

        // 判断是否已生成了证书
        List<ExamRecordsDO> examRecordsDOS = baseMapper.selectByIds(recordIds);
        List<Long> candidateIds = examRecordsDOS.stream().filter(item -> {
            return ExamRecprdsHasCertofocateEnum.YES.getValue().equals(item.getIsCertificateGenerated());
        }).map(ExamRecordsDO::getCandidateId).toList();
        if (!ObjectUtils.isEmpty(candidateIds)) {
            List<UserDO> userDOS = userMapper.selectByIds(candidateIds);
            List<String> nikenameList = userDOS.stream().map(UserDO::getNickname).toList();
            ValidationUtils.throwIfNotEmpty(nikenameList,
                    String.join("、", nikenameList) + " 已生成证书信息，无法再次录入成绩"
            );
        }

        // 去重计划id
        Set<Long> distinctPlanIds = new HashSet<>(planIds);

        // 查询计划支持的考试类型
        List<CheckPlanHasExamTypeDTO> planExamTypes =
                baseMapper.checkPlanHasExamType(new ArrayList<>(distinctPlanIds), roadExamTypeId);

        LambdaUpdateWrapper<ExamRecordsDO> updateWrapper = new LambdaUpdateWrapper<>();

        if (ExamScoreInputTypeEnum.OPER.getValue().equals(scoresType)) {

            List<String> noOperPlanNames = planExamTypes.stream()
                    .filter(dto -> ProjectHasExamTypeEnum.NO.getValue().equals(dto.getIsOperation()))
                    .map(CheckPlanHasExamTypeDTO::getPlanName)
                    .distinct()
                    .toList();

            ValidationUtils.throwIfNotEmpty(
                    noOperPlanNames,
                    String.join("、", noOperPlanNames) + " 计划不支持实操成绩录入"
            );

            updateWrapper.set(ExamRecordsDO::getOperScores, scores)
                    .set(ExamRecordsDO::getOperInputStatus, ExamScoreEntryStatusEnum.ENTERED.getValue());

        } else if (ExamScoreInputTypeEnum.ROAD.getValue().equals(scoresType)) {

            List<String> noRoadPlanNames = planExamTypes.stream()
                    .filter(dto -> ProjectHasExamTypeEnum.NO.getValue().equals(dto.getIsRoad()))
                    .map(CheckPlanHasExamTypeDTO::getPlanName)
                    .distinct()
                    .toList();

            ValidationUtils.throwIfNotEmpty(
                    noRoadPlanNames,
                    String.join("、", noRoadPlanNames) + "计划不支持道路成绩录入"
            );

            updateWrapper.set(ExamRecordsDO::getRoadScores, scores)
                    .set(ExamRecordsDO::getRoadInputStatus, ExamScoreEntryStatusEnum.ENTERED.getValue());
        }

        // 批量修改成绩
        updateWrapper.in(ExamRecordsDO::getId, recordIds);
        return baseMapper.update(updateWrapper) > 0;
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