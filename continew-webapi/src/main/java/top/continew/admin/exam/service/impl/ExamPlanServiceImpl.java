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

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import net.dreamlu.mica.core.utils.ObjectUtil;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.PlanConstant;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.DateUtil;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.ExamPlanDTO;
import top.continew.admin.exam.model.dto.ExamPlanExcelRowDTO;
import top.continew.admin.exam.model.dto.TimeRangeDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.req.ExamPlanSaveReq;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.examconnect.model.resp.ExamPaperVO;
import top.continew.admin.examconnect.service.QuestionBankService;
import top.continew.admin.invigilate.mapper.PlanInvigilateMapper;
import top.continew.admin.invigilate.model.entity.PlanInvigilateDO;
import top.continew.admin.invigilate.model.enums.InvigilateStatus;
import top.continew.admin.invigilate.model.resp.AvailableInvigilatorResp;
import top.continew.admin.invigilate.model.resp.InvigilatorAssignResp;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.worker.mapper.WorkerExamTicketMapper;
import top.continew.admin.worker.model.entity.WorkerExamTicketDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.query.ExamPlanQuery;
import top.continew.admin.exam.model.req.ExamPlanReq;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.resp.ExamPlanResp;
import top.continew.admin.exam.service.ExamPlanService;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考试计划业务实现
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Service
@RequiredArgsConstructor
public class ExamPlanServiceImpl extends BaseServiceImpl<ExamPlanMapper, ExamPlanDO, ExamPlanResp, ExamPlanDetailResp, ExamPlanQuery, ExamPlanReq> implements ExamPlanService {

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private PlanInvigilateMapper planInvigilateMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private PlanClassroomMapper planClassroomMapper;

    @Resource
    private SpecialCertificationApplicantMapper specialCertificationApplicantMapper;

    @Resource
    private ExamineePaymentAuditMapper examineePaymentAuditMapper;

    @Resource
    private ExamPlanClassroomMapper examPlanClassroomMapper;

    @Value("${examine.userRole.invigilatorId}")
    private Long invigilatorId;

    private final QuestionBankService questionBankService;

    private final EnrollMapper enrollMapper;

    private final CandidateTicketService candidateTicketReactiveService;

    private final AESWithHMAC aesWithHMAC;

    private final UserMapper userMapper;

    private final WorkerExamTicketMapper workerExamTicketMapper;

    private final CandidateExamPaperMapper candidateExamPaperMapper;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public PageResp<ExamPlanResp> page(ExamPlanQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamPlanDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tep.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<ExamPlanDetailResp> page = baseMapper.selectExamPlanPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        page.getRecords().forEach(item -> item.setStatusString(PlanConstant.getStatusString(item.getStatus())));

        PageResp<ExamPlanResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    @Override
    public ExamPlanDetailResp get(Long id) {
        ExamPlanDetailResp examPlanDetailResp = baseMapper.selectDetailById(id);
        ValidationUtils.throwIfNull(examPlanDetailResp, "未查询到考试计划信息");
        // 查询计划对应的考场信息
        LambdaQueryWrapper<ExamPlanClassroomDO> examPlanClassroomDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examPlanClassroomDOLambdaQueryWrapper.eq(ExamPlanClassroomDO::getPlanId, id)
            .select(ExamPlanClassroomDO::getClassroomId);
        List<ExamPlanClassroomDO> examPlanClassroomDOS = examPlanClassroomMapper
            .selectList(examPlanClassroomDOLambdaQueryWrapper);
        if (ObjectUtil.isNotEmpty(examPlanClassroomDOS)) {
            examPlanDetailResp.setClassroomId(examPlanClassroomDOS.stream()
                .map(ExamPlanClassroomDO::getClassroomId)
                .collect(Collectors.toList()));
        }
        this.fill(examPlanDetailResp);
        return examPlanDetailResp;
    }

    @Transactional
    @Override
    public void save(ExamPlanSaveReq examPlanSaveReq) {
        // 禁止计划重名
        ExamPlanDO examPlanName = baseMapper.selectOne(new QueryWrapper<ExamPlanDO>()
            .eq("exam_plan_name", examPlanSaveReq.getExamPlanName())
            .eq("is_deleted", 0));
        ValidationUtils.throwIfNotNull(examPlanName, "考试计划名称已存在");
        ExamPlanDO examPlanDO = getExamPlanDO(examPlanSaveReq);
        ValidationUtils.throwIf(!DateUtil.validateEnrollmentTime(examPlanDO.getEnrollEndTime(), examPlanDO
            .getStartTime()), "报名结束时间不能晚于考试开始时间");
        // 判断计划考试时间对应的考场是否空闲
        ValidationUtils.throwIf(hasClassroomTimeConflict(examPlanDO.getStartTime(), examPlanDO
            .getEndTime(), examPlanSaveReq.getClassroomId(), null) > 0, "所选考场在所选时间段前后30分钟内已有考试计划，请调整考试时间或更换考场。");
        BeanUtils.copyProperties(examPlanSaveReq, examPlanDO);
        //填充最大人数字段 根据考场id获取最大人数
        List<Long> maxCandidates = classroomMapper.getMaxCandidates(examPlanSaveReq.getClassroomId());
        long count = maxCandidates.stream().mapToLong(Long::longValue).sum();

        examPlanDO.setMaxCandidates(Math.toIntExact(count));
        //最终确定考试时间以及地点状态
        examPlanDO.setIsFinalConfirmed(0);
        this.save(examPlanDO);
        // 添加计划和考场关联表
        examPlanMapper.savePlanClassroom(examPlanDO.getId(), examPlanSaveReq.getClassroomId());
    }

    /**
     * 判读考试计划的考试时间对应的考场是否冲突
     *
     * @param startTime
     * @param endTime
     * @param classroomId
     * @return
     */
    private Integer hasClassroomTimeConflict(LocalDateTime startTime,
                                             LocalDateTime endTime,
                                             List<Long> classroomId,
                                             Long planId) {
        return baseMapper.hasClassroomTimeConflict(startTime, endTime, classroomId, planId);
    }

    /**
     * 时间转换
     */
    private ExamPlanDO getExamPlanDO(ExamPlanSaveReq examPlanSaveReq) {
        //        List<String> dateRange = examPlanSaveReq.getDateRange();
        List<String> enrollList = examPlanSaveReq.getEnrollList();
        ExamPlanDO examPlanDO = new ExamPlanDO();
        // 年份
        //        examPlanDO.setPlanYear(dateRange.get(0).substring(0, dateRange.get(0).indexOf("-")));
        examPlanDO.setPlanYear(String.valueOf(examPlanSaveReq.getStartTime().getYear()));
        // 报名时间
        examPlanDO.setEnrollStartTime(DateUtil.parse(enrollList.get(0)));
        examPlanDO.setEnrollEndTime(DateUtil.parse(enrollList.get(1)));
        // 考试结束时间
        //        examPlanDO.setStartTime(DateUtil.parse(dateRange.get(0)));
        //        examPlanDO.setEndTime(DateUtil.parse(dateRange.get(1)));
        ProjectDO projectDO = projectMapper.selectById(examPlanSaveReq.getExamProjectId());
        examPlanDO.setStartTime(examPlanSaveReq.getStartTime());
        examPlanDO.setEndTime(examPlanSaveReq.getStartTime().plusMinutes(projectDO.getExamDuration()));
        return examPlanDO;
    }

    @Override
    public String valid(Long examPlanId, Integer status) {

        ValidationUtils.throwIfNull(examPlanId, "考试计划字段为空");

        Long userId = TokenLocalThreadUtil.get().getUserId();

        QueryWrapper<ExamPlanDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", examPlanId);
        ExamPlanDO examPlanDO = examPlanMapper.selectOne(queryWrapper);

        ValidationUtils.throwIfNull(examPlanDO, "考试计划不存在");

        String approvedUsers = examPlanDO.getApprovedUsers();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        ExamPlanDO examPlanResp = new ExamPlanDO();
        BeanUtils.copyProperties(examPlanDO, examPlanResp);
        //        status == 0  未通过
        if (status == 0) {
            if (approvedUsers == null || approvedUsers.isEmpty()) {
                StringBuilder approveUser = new StringBuilder(userId.toString());
                examPlanResp.setApprovedUsers(approveUser.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.FAIL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划,主任未通过";
            } else {
                String existApproveUser = examPlanDO.getApprovedUsers();
                StringBuilder existApproveUserBuilder = new StringBuilder(existApproveUser);

                StringBuilder approveUser = new StringBuilder(userId.toString());
                existApproveUserBuilder.append(",").append(approveUser);

                examPlanResp.setApprovedUsers(existApproveUserBuilder.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.FAIL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划,市监局未通过";
            }
        }

        //        status == 1
        if (status == 1) {
            if (approvedUsers == null || approvedUsers.isEmpty()) {
                StringBuilder approveUser = new StringBuilder(userId.toString());
                examPlanResp.setApprovedUsers(approveUser.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.AUDIT_SUPERVISION_APPROVAL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划主任通过,待市监局审核";
            } else {
                String existApproveUser = examPlanDO.getApprovedUsers();
                StringBuilder existApproveUserBuilder = new StringBuilder(existApproveUser);

                StringBuilder approveUser = new StringBuilder(userId.toString());
                existApproveUserBuilder.append(",").append(approveUser);

                examPlanResp.setApprovedUsers(existApproveUserBuilder.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.SUCCESS.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划主任,市监局都通过";
            }

        }
        return "";
    }

    @Override
    public List<ExamPlanDO> getAllList() {
        return examPlanMapper.selectAllList();
    }

    /**
     * 获取考试计划考场
     *
     * @param planId
     * @return
     */
    @Override
    public List<Long> getPlanExamClassroom(Long planId) {
        return examPlanMapper.getPlanExamClassroom(planId);
    }

    /**
     * 修改考试计划考场
     *
     * @param planId
     * @param classroomId
     * @return
     */
    @Override
    public Boolean updatePlanExamClassroom(Long planId, List<Long> classroomId) {
        examPlanMapper.deletePLanExamClassroom(planId);
        examPlanMapper.savePlanClassroom(planId, classroomId);
        // 通过考场id获取最大人数
        List<Long> maxCandidates = classroomMapper.getMaxCandidates(classroomId);
        long count = maxCandidates.stream().mapToLong(Long::longValue).sum();
        // 修改考试计划最大人数
        ExamPlanDO examPlanDO = new ExamPlanDO();
        examPlanDO.setId(planId);
        examPlanDO.setMaxCandidates(Math.toIntExact(count));
        examPlanMapper.updateById(examPlanDO);
        return true;
    }

    @Override
    public Integer selectClassroomId(Integer planId) {
        return examPlanMapper.selectClassroomById(planId);
    }

    @Override
    @Transactional
    public Boolean endExam(Long planId) {
        // 需改考试计划状态
        LambdaUpdateWrapper<ExamPlanDO> examPlanDOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        examPlanDOLambdaUpdateWrapper.eq(ExamPlanDO::getId, planId)
            .set(ExamPlanDO::getStatus, PlanConstant.OVER.getStatus());
        // 修改监考员状态
        LambdaUpdateWrapper<PlanInvigilateDO> planInvigilateDOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        planInvigilateDOLambdaUpdateWrapper.eq(PlanInvigilateDO::getExamPlanId, planId)
            .set(PlanInvigilateDO::getInvigilateStatus, InvigilateStatus.PENDING_REVIEW.getCode());
        planInvigilateMapper.update(planInvigilateDOLambdaUpdateWrapper);
        return this.update(examPlanDOLambdaUpdateWrapper);
    }

    @Override
    public List<ProjectVo> getSelectOptions() {
        List<ProjectVo> projectVoList = examPlanMapper.getSelectOptions();
        for (ProjectVo projectVo : projectVoList) {
            projectVo.setDisabled(false);
        }
        return projectVoList;
    }

    /**
     * 批量导入考试计划
     *
     * @param file
     */
    @Transactional
    @Override
    public void importExcel(MultipartFile file) {
        // 1. 文件校验
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException("仅支持Excel文件(.xlsx/.xls)");
        }

        // 定义时间解析器
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseStrict()
            .appendPattern("uuuu-MM-dd HH:mm")
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException("Excel中未找到有效工作表");
            }

            // 跳过前4行说明，第5行为空，第6行为表头
            int headerRowIndex = 3;
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                throw new BusinessException("Excel表头不存在或模板结构错误");
            }

            // 校验表头
            String[] expectedHeaders = {"计划名称", "考试项目代码", "考试考场编号", "报名开始时间", "报名结束时间", "考试开始时间"};
            for (int i = 0; i < expectedHeaders.length; i++) {
                Cell cell = headerRow.getCell(i);
                String actual = (cell == null) ? "" : cell.getStringCellValue().trim();
                if (!expectedHeaders[i].equals(actual)) {
                    throw new BusinessException("模板错误：第 " + (i + 1) + " 列应为 [" + expectedHeaders[i] + "]，实际为 [" + actual + "]");
                }
            }

            // 2. 读取 Excel 数据到内存对象列表
            List<ExamPlanExcelRowDTO> rowList = new ArrayList<>();
            Map<String, Integer> planNameMap = new HashMap<>();
            Map<Long, List<TimeRangeDTO>> classroomTimeMap = new HashMap<>();

            for (int i = headerRowIndex + 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue; // 空行跳过

                int rowIndex = i + 1;
                String planName = getCellString(row.getCell(0));
                String projectCode = getCellString(row.getCell(1));
                String classroomIdsStr = getCellString(row.getCell(2));
                String signupStart = getCellString(row.getCell(3));
                String signupEnd = getCellString(row.getCell(4));
                String examStart = getCellString(row.getCell(5));

                if (planName.isEmpty() && projectCode.isEmpty())
                    continue;

                // 基础非空校验
                if (planName.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：计划名称不能为空");
                if (projectCode.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：考试项目代码不能为空");
                if (classroomIdsStr.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：考试考场编号不能为空");

                // Excel 内部计划名称重复
                if (planNameMap.containsKey(planName)) {
                    int firstRow = planNameMap.get(planName);
                    throw new BusinessException("第" + rowIndex + "行：计划名称在 Excel 内部重复，已在第" + firstRow + "行出现");
                } else {
                    planNameMap.put(planName, rowIndex);
                }

                // 禁止计划重名（数据库）
                ExamPlanDO existingPlan = baseMapper.selectOne(new QueryWrapper<ExamPlanDO>()
                    .eq("exam_plan_name", planName)
                    .eq("is_deleted", 0));
                if (existingPlan != null) {
                    throw new BusinessException("第" + rowIndex + "行：计划名称已存在系统中");
                }

                // 检查项目代码是否存在
                ProjectDO projectDO = projectMapper.selectOne(new LambdaQueryWrapper<ProjectDO>()
                    .eq(ProjectDO::getProjectCode, projectCode));
                if (projectDO == null) {
                    throw new BusinessException("第" + rowIndex + "行：考试项目代码 [" + projectCode + "] 不存在，请对照《考试项目参照数据》文件进行填写");
                }

                // 解析时间
                LocalDateTime signupStartTime = parseDate(signupStart, "报名开始时间", rowIndex, formatter);
                LocalDateTime signupEndTime = parseDate(signupEnd, "报名结束时间", rowIndex, formatter);
                LocalDateTime examStartTime = parseDate(examStart, "考试开始时间", rowIndex, formatter);
                LocalDateTime examEndTime = examStartTime.plusMinutes(projectDO.getExamDuration());

                LocalDateTime now = LocalDateTime.now();
                if (signupStartTime.isBefore(now) || signupEndTime.isBefore(now) || examStartTime.isBefore(now)) {
                    throw new BusinessException("第" + rowIndex + "行：报名/考试时间必须晚于当前时间");
                }
                if (!signupStartTime.isBefore(signupEndTime)) {
                    throw new BusinessException("第" + rowIndex + "行：报名开始时间必须早于报名结束时间");
                }
                if (!signupEndTime.isBefore(examStartTime)) {
                    throw new BusinessException("第" + rowIndex + "行：报名结束时间必须早于考试开始时间");
                }

                // 拆分考场ID并校验格式
                List<Long> classroomIds = Arrays.stream(classroomIdsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(idStr -> {
                        try {
                            return Long.parseLong(idStr);
                        } catch (NumberFormatException e) {
                            throw new BusinessException("第" + rowIndex + "行：考场编号 [ " + idStr + " ] 不存在，请对照《考试项目参照数据》文件进行填写");
                        }
                    })
                    .collect(Collectors.toList());

                // Excel 内部考场时间冲突
                for (Long cid : classroomIds) {
                    classroomTimeMap.putIfAbsent(cid, new ArrayList<>());
                    List<TimeRangeDTO> ranges = classroomTimeMap.get(cid);
                    TimeRangeDTO newRange = new TimeRangeDTO(examStartTime, examEndTime, rowIndex);

                    for (TimeRangeDTO r : ranges) {
                        if (r.overlaps(newRange)) {
                            throw new BusinessException("第" + rowIndex + "行：考场 [" + cid + "] 在 Excel 内部与第" + r
                                .getRowIndex() + "行存在时间冲突");
                        }
                    }

                    ranges.add(newRange);
                }

                // 判断考场是否属于该项目的考场
                List<Long> projectRoomList = projectMapper.getProjectRoomByProjectId(projectDO.getId());
                Set<Long> projectRoomSet = new HashSet<>(projectRoomList);
                List<Long> invalidRooms = classroomIds.stream()
                    .filter(id -> !projectRoomSet.contains(id))
                    .collect(Collectors.toList());

                if (!invalidRooms.isEmpty()) {
                    throw new BusinessException("第" + rowIndex + "行：考场编号 " + invalidRooms + " 不属于项目 [" + projectDO
                        .getProjectCode() + "] 的考场范围");
                }
                // 添加到内存列表
                rowList.add(new ExamPlanExcelRowDTO(planName, projectDO
                    .getId(), classroomIds, signupStartTime, signupEndTime, examStartTime, examEndTime, rowIndex));
            }

            // 数据库时间冲突校验
            for (ExamPlanExcelRowDTO row : rowList) {
                boolean conflict = hasClassroomTimeConflict(row.getExamStartTime(), row.getExamEndTime(), row
                    .getClassroomIds(), null) > 0;
                if (conflict) {
                    throw new BusinessException("第" + row.getRowIndex() + "行：所选考场在系统中存在时间冲突");
                }
            }

            // 批量插入
            List<ExamPlanDO> insertList = rowList.stream().map(row -> {
                ExamPlanDO plan = new ExamPlanDO();
                plan.setExamPlanName(row.getPlanName());
                plan.setExamProjectId(row.getProjectId());
                plan.setEnrollStartTime(row.getSignupStartTime());
                plan.setEnrollEndTime(row.getSignupEndTime());
                plan.setStartTime(row.getExamStartTime());
                plan.setEndTime(row.getExamEndTime());
                plan.setPlanYear(String.valueOf(row.getExamStartTime().getYear()));
                List<ClassroomDO> classroomDOS = classroomMapper.selectByIds(row.getClassroomIds());
                plan.setMaxCandidates((int)classroomDOS.stream()
                    .mapToLong(c -> Optional.ofNullable(c.getMaxCandidates()).orElse(0L))
                    .sum());
                return plan;
            }).collect(Collectors.toList());

            if (!insertList.isEmpty()) {
                // 批量插入计划
                baseMapper.insertBatch(insertList);
                // 批量插入计划与考场关联表
                // 构建映射关系：计划名称 → 数据库 ID
                Map<String, Long> planIdMap = insertList.stream()
                    .collect(Collectors.toMap(ExamPlanDO::getExamPlanName, ExamPlanDO::getId));
                // 生成关联表数据
                List<PlancalssroomDO> relationList = new ArrayList<>();
                for (ExamPlanExcelRowDTO row : rowList) {
                    Long planId = planIdMap.get(row.getPlanName());
                    for (Long classroomId : row.getClassroomIds()) {
                        PlancalssroomDO rel = new PlancalssroomDO();
                        rel.setPlanId(planId);
                        rel.setClassroomId(classroomId);
                        rel.setEnrolledCount(0);
                        relationList.add(rel);
                    }
                }
                // 批量插入关联表
                if (!relationList.isEmpty()) {
                    planClassroomMapper.insertBatch(relationList);
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件读取失败");
        } catch (EncryptedDocumentException e) {
            throw new BusinessException("文件加密无法读取");
        }
    }

    /**
     * 机构获取符合自身八大类的考试计划
     *
     * @param pageQuery
     * @param examPlanQuery
     * @return
     */
    @Override
    public PageResp<OrgExamPlanVO> orgGetPlanList(ExamPlanQuery examPlanQuery, PageQuery pageQuery) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        QueryWrapper<ExamPlanDO> queryWrapper = this.buildQueryWrapper(examPlanQuery);
        queryWrapper.eq("tep.is_deleted", 0);
        // 执行分页查询
        IPage<OrgExamPlanVO> page = baseMapper.orgGetPlanList(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper, userTokenDo.getUserId());

        // 将查询结果转换成 PageResp 对象
        PageResp<OrgExamPlanVO> pageResp = PageResp.build(page, OrgExamPlanVO.class);
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;
    }

    /**
     * 解析日期
     *
     * @param text
     * @param fieldName
     * @param rowNum
     * @param formatter
     * @return
     */
    private LocalDateTime parseDate(String text, String fieldName, int rowNum, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(text.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new BusinessException("第" + rowNum + "行：" + fieldName + " [ " + text + " ] 日期错误");
        }
    }

    /**
     * 安全读取单元格内容（统一转String）
     */
    private String getCellString(Cell cell) {
        if (cell == null)
            return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    /**
     * 重新update
     *
     * @param req
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExamPlanReq req, Long id) {
        // 1. 查询考试计划
        ExamPlanDO examPlanDO = baseMapper.selectById(id);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到考试计划");

        // 2. 解析报名时间段
        List<String> enrollList = req.getEnrollList();
        if (CollUtil.isEmpty(enrollList) || enrollList.size() < 2) {
            ValidationUtils.validate("报名时间列表不完整");
        }
        req.setEnrollStartTime(DateUtil.parse(enrollList.get(0)));
        req.setEnrollEndTime(DateUtil.parse(enrollList.get(1)));

        // 校验报名结束时间不能晚于考试开始时间
        ValidationUtils.throwIf(!DateUtil.validateEnrollmentTime(req.getEnrollEndTime(), req
            .getStartTime()), "报名结束时间不能晚于考试开始时间");

        // 考试开始时间不能早于当前时间
        ValidationUtils.throwIf(req.getStartTime().isBefore(LocalDateTime.now()), "考试开始时间不能早于当前时间");

        Integer invigilatorCount = req.getInvigilatorCount();
        ValidationUtils.throwIf(invigilatorCount < req.getClassroomId().size(), "所设置的监考员人数不足以分配到全部考场");

        // 3. 校验最终确定考试时间及地点状态
        ValidationUtils.throwIf(PlanFinalConfirmedStatus.DIRECTOR_PENDING.getValue()
            .equals(req.getIsFinalConfirmed()), "最终确定考试时间及地点已确认");

        // 4.如果是新的考场人数小于之前的考场人数，无法成功
        LambdaQueryWrapper<ExamineePaymentAuditDO> examineePaymentAuditDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examineePaymentAuditDOLambdaQueryWrapper.eq(ExamineePaymentAuditDO::getExamPlanId, id)
            .eq(ExamineePaymentAuditDO::getAuditStatus, PaymentAuditStatusEnum.APPROVED.getValue());
        Long candidateCount = examineePaymentAuditMapper.selectCount(examineePaymentAuditDOLambdaQueryWrapper);
        Long totalCapacity = classroomMapper.selectByIds(req.getClassroomId())
            .stream()
            .mapToLong(ClassroomDO::getMaxCandidates)
            .sum();
        ValidationUtils.throwIf(candidateCount > totalCapacity, String
            .format("已报名 %d 人，但所选考场最多只能容纳 %d 人，请重新选择考场", candidateCount, totalCapacity));
        // 判断题库是否够考试
        ExamPaperVO examPaperVO = questionBankService.generateExamQuestionBank(id);
        ValidationUtils.throwIfNull(examPaperVO, "当前分类下的题目数量不足，无法满足出题要求");

        // 5. 如果是巡检类型，校验报名人员是否全部审核通过
        if (ExamPlanTypeEnum.INSPECTION.getValue().equals(examPlanDO.getPlanType())) {
            long pendingCount = specialCertificationApplicantMapper
                .selectCount(new LambdaQueryWrapper<SpecialCertificationApplicantDO>()
                    .eq(SpecialCertificationApplicantDO::getPlanId, id)
                    .notIn(SpecialCertificationApplicantDO::getStatus, Arrays
                        .asList(SpecialCertificationApplicantAuditStatusEnum.APPROVED
                            .getValue(), SpecialCertificationApplicantAuditStatusEnum.FAKE_MATERIAL.getValue())));
            ValidationUtils.throwIf(pendingCount > 0, "存在未审核考试报名申请，请审核后再确认考试时间或地点");
        }

        // 6. 校验考试人员缴费状态
        long unpaidCount = examineePaymentAuditMapper.selectCount(new LambdaQueryWrapper<ExamineePaymentAuditDO>()
            .eq(ExamineePaymentAuditDO::getExamPlanId, id)
            .notIn(ExamineePaymentAuditDO::getAuditStatus, Arrays.asList(PaymentAuditStatusEnum.APPROVED
                .getValue(), PaymentAuditStatusEnum.REFUNDED.getValue())));
        ValidationUtils.throwIf(unpaidCount > 0, "存在考试人员未提交缴费通知单或审核未通过，请到考生缴费审核管理完成审核");

        // 7. 计算考试结束时间
        ProjectDO projectDO = projectMapper.selectById(req.getExamProjectId());
        ValidationUtils.throwIfNull(projectDO, "未查询到考试项目");

        req.setStartTime(req.getStartTime());
        req.setEndTime(req.getStartTime().plusMinutes(projectDO.getExamDuration()));
        req.setIsFinalConfirmed(PlanFinalConfirmedStatus.DIRECTOR_PENDING.getValue());

        // 8.在选择的考试的时间，考场有没有是否有别的考试
        ValidationUtils.throwIf(hasClassroomTimeConflict(req.getStartTime(), req.getEndTime(), req
            .getClassroomId(), id) > 0, "所选考场在所选时间段前后30分钟内已有考试计划，请调整考试时间或更换考场。");

        req.setAssignType(InvigilatorAssignTypeEnum.RANDOM_FIRST.getValue());
        // 9. 执行更新
        super.update(req, id);

        // 10.修改考场和计划关系
        updatePlanExamClassroom(id, req.getClassroomId());

        // 11.随机分配监考人员
        randomInvigilator(req.getClassroomId(), examPlanDO.getStartTime(), examPlanDO
            .getEndTime(), id, invigilatorCount);
    }

    private void randomInvigilator(List<Long> classroomIds,
                                   LocalDateTime startTime,
                                   LocalDateTime endTime,
                                   Long planId,
                                   Integer invigilatorNum) {
        // 获取所选时间段有空闲的监考人员
        List<AvailableInvigilatorResp> availableInvigilatorList = planInvigilateMapper
            .selectAvailableInvigilators(startTime, endTime, planId, invigilatorId, null);
        ValidationUtils.throwIf(ObjectUtil.isEmpty(availableInvigilatorList) || availableInvigilatorList
            .size() < invigilatorNum, "当前时间段可分配的监考员数量不足");
        Collections.shuffle(availableInvigilatorList);
        List<AvailableInvigilatorResp> assignedAvailableInvigilatorList = availableInvigilatorList
            .subList(0, invigilatorNum);
        // 还要随机分配给classRoomNum
        Map<Long, List<AvailableInvigilatorResp>> assignmentMap = new HashMap<>();

        // 初始化 map
        classroomIds.forEach(cid -> assignmentMap.put(cid, new ArrayList<>()));

        // 第一轮：确保每个考场至少分配一个监考员
        for (int i = 0; i < classroomIds.size(); i++) {
            assignmentMap.get(classroomIds.get(i)).add(assignedAvailableInvigilatorList.get(i));
        }

        // 剩余监考员继续随机分配
        for (int i = classroomIds.size(); i < assignedAvailableInvigilatorList.size(); i++) {
            Long randomClassroomId = classroomIds.get(new Random().nextInt(classroomIds.size()));
            assignmentMap.get(randomClassroomId).add(assignedAvailableInvigilatorList.get(i));
        }

        List<PlanInvigilateDO> records = new ArrayList<>();

        for (Map.Entry<Long, List<AvailableInvigilatorResp>> entry : assignmentMap.entrySet()) {
            Long classroomId = entry.getKey();
            List<AvailableInvigilatorResp> list = entry.getValue();

            for (AvailableInvigilatorResp invigilatorResp : list) {
                PlanInvigilateDO record = new PlanInvigilateDO();
                // 考试计划 ID
                record.setExamPlanId(planId);
                // 监考员 ID
                record.setInvigilatorId(invigilatorResp.getId());
                // 考场号
                record.setClassroomId(classroomId);
                // 监考状态
                record.setInvigilateStatus(InvigilateStatusEnum.TO_CONFIRM.getValue());
                records.add(record);
            }
        }
        planInvigilateMapper.insertBatch(records);
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        QueryWrapper<PlanInvigilateDO> wrapper = new QueryWrapper<PlanInvigilateDO>();
        wrapper.in("exam_plan_id", ids);
        planInvigilateMapper.delete(wrapper);
        //删除监考计划关联表
        super.delete(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdatePlanMaxCandidates(List<ExamPlanDTO> planList) {
        if (planList != null && !planList.isEmpty()) {
            examPlanMapper.batchUpdatePlanMaxCandidates(planList);
        }
    }

    /**
     * 重新随机分配考试计划的监考员
     *
     * @param planId
     * @param invigilatorNum
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reRandomInvigilators(Long planId, Integer invigilatorNum) {
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到考试计划");
        // 判断是否已有监考员确认，如果有无法再次随机
        Long notStartCount = planInvigilateMapper.selectCount(new LambdaQueryWrapper<PlanInvigilateDO>()
            .eq(PlanInvigilateDO::getExamPlanId, examPlanDO.getId())
            .eq(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.NOT_START.getValue()));
        ValidationUtils.throwIf(notStartCount > 0, "已有监考员完成确认，当前无法重新随机分配！");
        // 1. 先删除原监考员
        planInvigilateMapper.delete(new LambdaQueryWrapper<PlanInvigilateDO>()
            .eq(PlanInvigilateDO::getExamPlanId, planId));

        // 2. 查询所有考场 classroomId
        List<Long> classroomIds = planClassroomMapper.selectList(new LambdaQueryWrapper<PlancalssroomDO>()
            .eq(PlancalssroomDO::getPlanId, planId)
            .select(PlancalssroomDO::getClassroomId)).stream().map(PlancalssroomDO::getClassroomId).toList();

        // 3. 随机分配监考逻辑
        randomInvigilator(classroomIds, examPlanDO.getStartTime(), examPlanDO.getEndTime(), planId, invigilatorNum);
        // 4.修改计划分配监考员类型
        baseMapper.update(new LambdaUpdateWrapper<ExamPlanDO>()
            .set(ExamPlanDO::getAssignType, InvigilatorAssignTypeEnum.RANDOM_SECOND.getValue())
            .eq(ExamPlanDO::getId, planId));
        return true;
    }

    /**
     * 获取可用监考员
     *
     * @param planId
     * @return
     */
    @Override
    public List<AvailableInvigilatorResp> getAvailableInvigilator(Long planId, Long rejectedInvigilatorId) {
        // 1. 查询考试计划
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到计划信息");

        // 2. 查询空闲监考员（包含状态=5可以忽略冲突）
        List<AvailableInvigilatorResp> availableInvigilatorResps = planInvigilateMapper
            .selectAvailableInvigilators(examPlanDO.getStartTime(), examPlanDO
                .getEndTime(), planId, invigilatorId, rejectedInvigilatorId);

        // 3. 查询已经安排的监考员（非被拒绝状态）
        List<Long> assignedInvigilatorIds = planInvigilateMapper.selectList(new LambdaQueryWrapper<PlanInvigilateDO>()
            .eq(PlanInvigilateDO::getExamPlanId, planId)
            .ne(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.REJECTED.getValue())
            .select(PlanInvigilateDO::getInvigilatorId)).stream().map(PlanInvigilateDO::getInvigilatorId).toList();

        // 4. 过滤已经安排的监考员
        if (ObjectUtil.isNotEmpty(assignedInvigilatorIds)) {
            availableInvigilatorResps = availableInvigilatorResps.stream()
                .filter(resp -> !assignedInvigilatorIds.contains(resp.getId()))
                .toList();
        }

        // 5. 返回最终可用监考员列表
        return availableInvigilatorResps;
    }

    /**
     * 中心主任确认考试
     *
     * @param planId
     * @param isFinalConfirmed
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean centerDirectorConform(Long planId, Integer isFinalConfirmed) {
        // 查询计划
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到考试计划信息");

        // 1. 查询该计划所有考场
        List<Long> classroomIds = examPlanMapper.getPlanExamClassroom(planId);
        ValidationUtils.throwIfEmpty(classroomIds, "考试计划未分配任何考场");

        // 2. 查询报名记录
        List<EnrollDO> enrollList = enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
            .eq(EnrollDO::getExamPlanId, planId));
        ValidationUtils.throwIfEmpty(enrollList, "未查询到考生报名信息");

        // 查询监考员列表
        List<InvigilatorAssignResp> invigilatorList = planInvigilateMapper.getListByPlanId(planId);
        ValidationUtils.throwIfEmpty(invigilatorList, "未选择监考员");

        // 检查监考员确认状态 —— 只有主任确认才需要校验
        if (PlanFinalConfirmedStatus.DIRECTOR_CONFIRMED.getValue().equals(isFinalConfirmed)) {
            for (InvigilatorAssignResp inv : invigilatorList) {
                ValidationUtils.throwIf(!InvigilateStatusEnum.NOT_START.getValue()
                    .equals(inv.getInvigilateStatus()), "存在监考员未确认监考或无法参加监考");
            }
        }

        // 更新构造器
        LambdaUpdateWrapper<ExamPlanDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ExamPlanDO::getId, planId).set(ExamPlanDO::getIsFinalConfirmed, isFinalConfirmed);

        // 情况 1：主任驳回
        if (PlanFinalConfirmedStatus.DIRECTOR_REJECTED.getValue().equals(isFinalConfirmed)) {

            updateWrapper.set(ExamPlanDO::getAssignType, null);

            // 删除监考员绑定
            planInvigilateMapper.delete(new LambdaQueryWrapper<PlanInvigilateDO>()
                .eq(PlanInvigilateDO::getExamPlanId, planId));
        }

        // 执行更新
        boolean success = baseMapper.update(updateWrapper) > 0;

        // 情况 2：主任确认，且是作业人员考试
        if (success && PlanFinalConfirmedStatus.DIRECTOR_CONFIRMED.getValue()
            .equals(isFinalConfirmed) && ExamPlanTypeEnum.WORKER.getValue().equals(examPlanDO.getPlanType())) {
            Collections.shuffle(classroomIds, RANDOM);
            List<WorkerExamTicketDO> ticketSaveList = new ArrayList<>();
            List<CandidateExamPaperDO> candidateExamPaperDOList = new ArrayList<>();
            // 按报名记录逐个生成准考证
            for (EnrollDO enroll : enrollList) {

                Long classroomId = findAvailableClassroom(classroomIds, planId);
                if (classroomId == null) {
                    break;
                }

                Integer seatNo = classroomMapper.getSeatNumber(classroomId, planId);
                if (seatNo == null) {
                    continue;
                }

                String seatStr = String.format("%03d", seatNo);
                String classroomStr = String.format("%03d", classroomId);
                String randomStr = String.format("%04d", RANDOM.nextInt(10000));

                // 最终准考证号
                String examNumber = examPlanDO.getPlanYear() + randomStr + classroomStr + seatStr;
                // 更新报名表
                EnrollDO upd = new EnrollDO();
                upd.setId(enroll.getId());
                upd.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                upd.setSeatId(Long.valueOf(seatStr));
                upd.setClassroomId(classroomId);
                enrollMapper.updateById(upd);

                // 查询用户
                UserDO user = userMapper.selectById(enroll.getUserId());
                if (user == null) {
                    continue;
                }

                // 调用确实耗时的方法：生成 PDF
                String ticketUrl = safeGenerateWorkerTicket(enroll.getUserId(), user.getUsername(), user
                    .getNickname(), upd.getExamNumber(), enroll.getClassId());

                // 保存准考证表
                WorkerExamTicketDO ticket = new WorkerExamTicketDO();
                ticket.setCandidateName(user.getNickname());
                ticket.setEnrollId(enroll.getId());
                ticket.setTicketUrl(ticketUrl);

                ticketSaveList.add(ticket);

                ObjectMapper objectMapper = new ObjectMapper();
                ExamPaperVO examPaperVO = questionBankService.generateExamQuestionBank(planId);
                CandidateExamPaperDO candidateExamPaperDO = new CandidateExamPaperDO();
                try {
                    candidateExamPaperDO.setPaperJson(objectMapper.writeValueAsString(examPaperVO));
                } catch (JsonProcessingException e) {
                    throw new BusinessException("系统错误");
                }
                candidateExamPaperDO.setEnrollId(enroll.getId());
                candidateExamPaperDOList.add(candidateExamPaperDO);
            }

            if (!ticketSaveList.isEmpty()) {
                workerExamTicketMapper.insertBatch(ticketSaveList);
            }

            if (!candidateExamPaperDOList.isEmpty()) {
                candidateExamPaperMapper.insertBatch(candidateExamPaperDOList);
            }

        }

        return success;
    }

    /**
     * 选择一个座位未满的考场
     * 每次尝试 +1，占位成功说明可用
     */
    private Long findAvailableClassroom(List<Long> classroomIds, Long planId) {
        for (Long classroomId : classroomIds) {
            if (classroomMapper.incrementEnrolledCount(classroomId, planId) > 0) {
                return classroomId;
            }
        }
        return null;
    }

    /**
     * 为单个考生生成准考证 PDF（附带错误兜底）
     */
    private String safeGenerateWorkerTicket(Long userId,
                                            String idCard,
                                            String nickname,
                                            String encryptedExamNo,
                                            Long classId) {
        try {
            return candidateTicketReactiveService.generateWorkerTicket(userId, idCard, encryptedExamNo, classId);
        } catch (Exception e) {
            throw new BusinessException("生成考生【" + nickname + "】的准考证失败，请稍后重试");
        }
    }
}