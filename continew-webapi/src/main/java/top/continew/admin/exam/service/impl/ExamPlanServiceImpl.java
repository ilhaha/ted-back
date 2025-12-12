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
import cn.hutool.core.util.RandomUtil;
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
import org.springframework.util.CollectionUtils;
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
import top.continew.admin.exam.model.req.AdjustPlanTimeReq;
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
import top.continew.admin.training.mapper.OrgClassMapper;
import top.continew.admin.training.model.entity.OrgClassDO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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


    @Value("${examine.userRole.invigilatorId}")
    private Long invigilatorId;

    private final QuestionBankService questionBankService;

    private final EnrollMapper enrollMapper;

    private final CandidateTicketService candidateTicketReactiveService;

    private final AESWithHMAC aesWithHMAC;

    private final UserMapper userMapper;

    private final WorkerExamTicketMapper workerExamTicketMapper;

    private final CandidateExamPaperMapper candidateExamPaperMapper;

    private final OrgClassMapper orgClassMapper;

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
//        LambdaQueryWrapper<ExamPlanClassroomDO> examPlanClassroomDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        examPlanClassroomDOLambdaQueryWrapper.eq(ExamPlanClassroomDO::getPlanId, id)
//                .select(ExamPlanClassroomDO::getClassroomId,ExamPlanClassroomDO::getPlanId);
//        List<ExamPlanClassroomDO> examPlanClassroomDOS = examPlanClassroomMapper
//                .selectList(examPlanClassroomDOLambdaQueryWrapper);
//        if (ObjectUtil.isNotEmpty(examPlanClassroomDOS)) {
//            examPlanDetailResp.setClassroomId(examPlanClassroomDOS.stream()
//                    .map(ExamPlanClassroomDO::getClassroomId)
//                    .collect(Collectors.toList()));
//        }
        this.fill(examPlanDetailResp);
        return examPlanDetailResp;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(ExamPlanSaveReq req) {

        // 禁止计划重名
        ExamPlanDO examPlanName = baseMapper.selectOne(new QueryWrapper<ExamPlanDO>()
                .eq("exam_plan_name", req.getExamPlanName())
                .eq("is_deleted", 0)
                .eq("plan_type",req.getPlanType()));
        ValidationUtils.throwIfNotNull(examPlanName, "考试计划名称已存在");

        // 1. 构建对象
        ExamPlanDO examPlanDO = getExamPlanDO(req);

        // 2. 校验报名结束时间
        ValidationUtils.throwIf(
                !DateUtil.validateEnrollmentTime(examPlanDO.getEnrollEndTime(), examPlanDO.getStartTime()),
                "报名结束时间不能晚于考试开始时间"
        );

        // 3. 合并考场 ID（理论 + 实操）
//        List<Long> classroomIds = new ArrayList<>();
//        List<Long> theoryIds = req.getTheoryClassroomId();
//        List<Long> operIds = req.getOperationClassroomId();
//        Integer maxCandidates = req.getMaxCandidates();
//
//        if (!CollectionUtils.isEmpty(theoryIds)) {
//            classroomIds.addAll(theoryIds);
//        }
//        if (!CollectionUtils.isEmpty(operIds)) {
//            classroomIds.addAll(operIds);
//        }
//
//        // 4. 判断考场是否冲突（为空则不会查数据库）
//        if (!CollectionUtils.isEmpty(classroomIds)) {
//            List<String> conflictClassrooms = baseMapper.listConflictClassrooms(req.getStartTime(), classroomIds);
//            ValidationUtils.throwIfNotEmpty(
//                    conflictClassrooms,
//                    "以下考场当天已存在考试：" + String.join("、", conflictClassrooms)
//            );
//        }
//
//        // 5. 验证理论/实操考场容量（避免空列表导致 SQL 报错）
//        long theoryCapacity = CollectionUtils.isEmpty(theoryIds)
//                ? 0
//                : classroomMapper.getMaxCandidates(theoryIds);
//
//        boolean hasOperCapacity = CollectionUtils.isEmpty(operIds);
//        long operCapacity = hasOperCapacity
//                ? 0
//                : classroomMapper.getMaxCandidates(operIds);
//
//        ValidationUtils.throwIf(maxCandidates > theoryCapacity,
//                "理论考场容量不足，无法容纳当前考试人数");
//
//        ValidationUtils.throwIf(!hasOperCapacity && maxCandidates > operCapacity,
//                "实操考场容量不足，无法容纳当前考试人数");

        // 6. copy 属性（必须在容量判断之后）
        BeanUtils.copyProperties(req, examPlanDO);

        // 7. 设置最终状态
        examPlanDO.setIsFinalConfirmed(PlanFinalConfirmedStatus.ADMIN_PENDING.getValue());

        // 8. 保存计划
        this.save(examPlanDO);

        // 9. 保存考场关联
//        if (!CollectionUtils.isEmpty(classroomIds)) {
//            examPlanMapper.savePlanClassroom(examPlanDO.getId(), classroomIds);
//        }
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
//        examPlanDO.setEndTime(examPlanSaveReq.getStartTime().plusMinutes(projectDO.getExamDuration()));
        examPlanDO.setPlanType(projectDO.getProjectType());
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
        Long maxCandidates = classroomMapper.getMaxCandidates(classroomId);
        // 修改考试计划最大人数
        ExamPlanDO examPlanDO = new ExamPlanDO();
        examPlanDO.setId(planId);
        examPlanDO.setMaxCandidates(Math.toIntExact(maxCandidates));
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
                .appendPattern("uuuu-MM-dd")
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
            String[] expectedHeaders = {"计划名称", "考试项目代码", "考试日期", "考试人数上限"};
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
            for (int i = headerRowIndex + 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue; // 空行跳过

                int rowIndex = i + 1;
                String planName = getCellString(row.getCell(0));
                String projectCode = getCellString(row.getCell(1));
                String examStart = getCellString(row.getCell(2));
                String maxCandidates = getCellString(row.getCell(3));

                if (planName.isEmpty() && projectCode.isEmpty())
                    continue;

                // 基础非空校验
                if (planName.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：计划名称不能为空");
                if (projectCode.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：考试项目代码不能为空");
                if (examStart.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：考试日期不能为空");
                if (maxCandidates.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：人员限额不能为空");

                // Excel 内部计划名称重复
                if (planNameMap.containsKey(planName)) {
                    int firstRow = planNameMap.get(planName);
                    throw new BusinessException("第" + rowIndex + "行：计划名称在 Excel 内部重复，已在第" + firstRow + "行出现");
                } else {
                    planNameMap.put(planName, rowIndex);
                }

                // 检查项目代码是否存在
                ProjectDO projectDO = projectMapper.selectOne(new LambdaQueryWrapper<ProjectDO>()
                        .eq(ProjectDO::getProjectCode, projectCode));
                if (projectDO == null) {
                    throw new BusinessException("第" + rowIndex + "行：考试项目代码 [" + projectCode + "] 不存在");
                }

                // 禁止计划重名（数据库）
                ExamPlanDO existingPlan = baseMapper.selectOne(new QueryWrapper<ExamPlanDO>()
                        .eq("exam_plan_name", planName)
                        .eq("is_deleted", 0)
                        .eq("plan_type",projectDO.getProjectType()));
                if (existingPlan != null) {
                    throw new BusinessException("第" + rowIndex + "行：计划名称已存在系统中");
                }

                // 解析考试时间
                LocalDateTime examStartTime = parseDate(examStart, "考试日期", rowIndex, formatter);
                if (examStartTime.isBefore(LocalDateTime.now())) {
                    throw new BusinessException("第" + rowIndex + "行：考试开始时间不能早于当前时间");
                }
                // 根据考试时间自动生成报名时间（提前 7 天）
                LocalDateTime signupStartTime = examStartTime.minusDays(7).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime signupEndTime = examStartTime.minusDays(1).withHour(23).withMinute(59).withSecond(59);

                rowList.add(new ExamPlanExcelRowDTO(planName, projectDO
                        .getId(), signupStartTime, signupEndTime, examStartTime,maxCandidates,projectDO.getProjectType(), rowIndex));
            }

            // 批量插入
            List<ExamPlanDO> insertList = rowList.stream().map(row -> {
                ExamPlanDO plan = new ExamPlanDO();
                plan.setExamPlanName(row.getPlanName());
                plan.setExamProjectId(row.getProjectId());
                plan.setEnrollStartTime(row.getSignupStartTime());
                plan.setPlanType(row.getProjectType());
                plan.setEnrollEndTime(row.getSignupEndTime());
                plan.setStartTime(row.getExamStartTime());
                plan.setMaxCandidates(Integer.parseInt(row.getMaxCandidates()));
                plan.setPlanYear(String.valueOf(row.getExamStartTime().getYear()));
                return plan;
            }).collect(Collectors.toList());

            if (!insertList.isEmpty()) {
                // 批量插入计划
                baseMapper.insertBatch(insertList);
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("考试人数上限只能填写数字");
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
            LocalDate date = LocalDate.parse(text.trim(), formatter);
            return date.atStartOfDay();
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

        // 2. 校验最终确定考试时间及地点状态
        ValidationUtils.throwIf(PlanFinalConfirmedStatus.DIRECTOR_PENDING.getValue()
                .equals(req.getIsFinalConfirmed()), "最终确定考试时间及地点已确认");

        // 3. 解析报名时间段
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
        ValidationUtils.throwIf(
                req.getStartTime().isBefore(LocalDateTime.now().minusSeconds(2)),
                "考试开始时间不能早于当前时间"
        );
        Integer invigilatorCount = req.getInvigilatorCount();
        List<Long> classroomIds = new ArrayList<>();
        List<Long> theoryIds = req.getTheoryClassroomId();
        List<Long> operIds = req.getOperationClassroomId();
        ValidationUtils.throwIf(invigilatorCount < (theoryIds.size() + operIds.size()), "所设置的监考员人数不足以分配到全部考场");

        if (!CollectionUtils.isEmpty(theoryIds)) {
            classroomIds.addAll(theoryIds);
        }
        if (!CollectionUtils.isEmpty(operIds)) {
            classroomIds.addAll(operIds);
        }

        // 2. 查询报名记录
//        ValidationUtils.throwIfEmpty(enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
//                .eq(EnrollDO::getExamPlanId, id)), "未查询到考生报名信息");

        if (!CollectionUtils.isEmpty(classroomIds)) {
            List<String> conflictClassrooms = baseMapper.listConflictClassrooms(req.getStartTime(), classroomIds);
            ValidationUtils.throwIfNotEmpty(
                    conflictClassrooms,
                    "以下考场当天已存在考试：" + String.join("、", conflictClassrooms)
            );
        }


//        // 4.如果是新的考场人数小于之前的考场人数，无法成功
//        LambdaQueryWrapper<ExamineePaymentAuditDO> examineePaymentAuditDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        examineePaymentAuditDOLambdaQueryWrapper.eq(ExamineePaymentAuditDO::getExamPlanId, id)
//                .eq(ExamineePaymentAuditDO::getAuditStatus, PaymentAuditStatusEnum.APPROVED.getValue());
//        Long candidateCount = examineePaymentAuditMapper.selectCount(examineePaymentAuditDOLambdaQueryWrapper);
//        Long totalCapacity = classroomMapper.selectByIds(req.getClassroomId())
//                .stream()
//                .mapToLong(ClassroomDO::getMaxCandidates)
//                .sum();
//        ValidationUtils.throwIf(candidateCount > totalCapacity, String
//                .format("已报名 %d 人，但所选考场最多只能容纳 %d 人，请重新选择考场", candidateCount, totalCapacity));

        // 5. 如果是检验类型，校验报名人员是否全部审核通过
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
//        ProjectDO projectDO = projectMapper.selectById(req.getExamProjectId());
//        ValidationUtils.throwIfNull(projectDO, "未查询到考试项目");

//        req.setStartTime(req.getStartTime());
//        req.setEndTime(req.getStartTime().plusMinutes(projectDO.getExamDuration()));

        req.setIsFinalConfirmed(PlanFinalConfirmedStatus.DIRECTOR_PENDING.getValue());

        // 8.在选择的考试的时间，考场有没有是否有别的考试
//        ValidationUtils.throwIf(hasClassroomTimeConflict(req.getStartTime(), req.getEndTime(), req
//                .getClassroomId(), id) > 0, "所选考场在所选时间段前后30分钟内已有考试计划，请调整考试时间或更换考场。");

        req.setAssignType(InvigilatorAssignTypeEnum.RANDOM_FIRST.getValue());

        // 判断题库是否够考试
        questionBankService.generateExamQuestionBank(id);

        // 9. 执行更新
        super.update(req, id);

        // 10.插入考场和计划关系
        if (!CollectionUtils.isEmpty(classroomIds)) {
            examPlanMapper.savePlanClassroom(examPlanDO.getId(), classroomIds);

            // 11.随机分配监考人员
            randomInvigilator(classroomIds, examPlanDO.getStartTime(), examPlanDO
                    .getEndTime(), id, invigilatorCount);
        }
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
                record.setInvigilateStatus(InvigilateStatusEnum.NOT_START.getValue());
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

        // 1. 查询该计划所有理论考场
        List<Long> classroomIds = examPlanMapper.getPlanExamTheoryClassroom(planId);
        ValidationUtils.throwIfEmpty(classroomIds, "考试计划未分配理论考场");

        // 2. 查询报名记录
        boolean isConfirmed = PlanFinalConfirmedStatus.DIRECTOR_CONFIRMED.getValue()
                .equals(isFinalConfirmed);
        List<EnrollDO> enrollList = enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
                .eq(EnrollDO::getExamPlanId, planId));
        ValidationUtils.throwIf(ObjectUtil.isEmpty(enrollList) && isConfirmed, "未查询到考生报名信息");

        // 查询监考员列表
        List<InvigilatorAssignResp> invigilatorList = planInvigilateMapper.getListByPlanId(planId);
        ValidationUtils.throwIfEmpty(invigilatorList, "未选择监考员");

        // 查询项目信息
        ProjectDO projectDO = projectMapper.selectById(examPlanDO.getExamProjectId());

        // 更新构造器
        LambdaUpdateWrapper<ExamPlanDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ExamPlanDO::getId, planId).set(ExamPlanDO::getIsFinalConfirmed, isFinalConfirmed);

        // 情况 1：主任驳回
        if (PlanFinalConfirmedStatus.DIRECTOR_REJECTED.getValue().equals(isFinalConfirmed)) {

            updateWrapper.set(ExamPlanDO::getAssignType, null);

            // 删除监考员绑定
            planInvigilateMapper.delete(new LambdaQueryWrapper<PlanInvigilateDO>()
                    .eq(PlanInvigilateDO::getExamPlanId, planId));

            // 删除考场
            planClassroomMapper.delete(new LambdaQueryWrapper<PlancalssroomDO>().eq(PlancalssroomDO::getPlanId, planId));
        }

        // 执行更新
        boolean success = baseMapper.update(updateWrapper) > 0;

        // 生成开考密码
        // 相同考场监考员开考密码相同
        Map<Long, List<InvigilatorAssignResp>> groupByClassroom =
                invigilatorList.stream().collect(Collectors.groupingBy(InvigilatorAssignResp::getClassroomId));

        List<PlanInvigilateDO> updateList = new ArrayList<>();

        for (Map.Entry<Long, List<InvigilatorAssignResp>> entry : groupByClassroom.entrySet()) {
            List<InvigilatorAssignResp> list = entry.getValue();

            // 为该教室生成统一密码
            String examPassword = generateExamPassword();

            // 更新每个监考员记录
            for (InvigilatorAssignResp item : list) {
                PlanInvigilateDO planInvigilateDO = new PlanInvigilateDO();
                planInvigilateDO.setId(item.getId());
                planInvigilateDO.setExamPassword(examPassword);
                updateList.add(planInvigilateDO);
            }
        }
        // 批量更新监考员密码
        if (!updateList.isEmpty()) {
            planInvigilateMapper.updateBatchById(updateList);
        }

        // 情况 2：主任确认，且是作业人员考试
        if (success && isConfirmed && ExamPlanTypeEnum.WORKER.getValue().equals(examPlanDO.getPlanType())) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
            Collections.shuffle(classroomIds, random);
            List<WorkerExamTicketDO> ticketSaveList = new ArrayList<>();
            List<CandidateExamPaperDO> candidateExamPaperDOList = new ArrayList<>();
            long serialNumber = 0;
            // 按报名记录逐个生成准考证
            ObjectMapper objectMapper = new ObjectMapper();

            // 查询班级信息
            List<Long> workerClassIds = enrollList.stream()
                    .map(EnrollDO::getClassId)
                    .distinct()
                    .toList();
            List<OrgClassDO> orgClassDOS = orgClassMapper.selectByIds(workerClassIds);

            Map<Long, String> orgClassNameMap = orgClassDOS.stream()
                    .collect(Collectors.toMap(
                            OrgClassDO::getId,
                            x -> Optional.ofNullable(x.getClassName()).orElse(""),
                            (k1, k2) -> k1
                    ));
            for (EnrollDO enroll : enrollList) {
                ++serialNumber;
                Long classroomId = classroomIds.get(random.nextInt(classroomIds.size()));
                // 最终准考证号
                String serialStr = String.format("%04d", serialNumber);
                String examDate = examPlanDO.getStartTime().format(formatter);
                String examNumber = projectDO.getProjectCode() + "B" + examDate + serialStr;
                // 更新报名表
                EnrollDO upd = new EnrollDO();
                upd.setId(enroll.getId());
                upd.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                upd.setSeatId(serialNumber);
                upd.setClassroomId(classroomId);
                enrollMapper.updateById(upd);

                // 查询用户
                UserDO user = userMapper.selectById(enroll.getUserId());
                if (user == null) {
                    continue;
                }

                // 调用确实耗时的方法：生成 PDF
                String ticketUrl = safeGenerateWorkerTicket(enroll.getUserId(), user.getUsername(), user
                        .getNickname(), upd.getExamNumber(), enroll.getClassId(),orgClassNameMap.get(enroll.getClassId()));

                // 保存准考证表
                WorkerExamTicketDO ticket = new WorkerExamTicketDO();
                ticket.setCandidateName(user.getNickname());
                ticket.setEnrollId(enroll.getId());
                ticket.setTicketUrl(ticketUrl);

                ticketSaveList.add(ticket);

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
     * 调整考试/报名时间
     * @param req
     * @param planId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean adjustPlanTime(AdjustPlanTimeReq req, Long planId) {
        // 1. 查询考试计划
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到考试计划");

        // 2. 解析报名时间段
        List<String> enrollList = req.getEnrollList();
        if (CollUtil.isEmpty(enrollList) || enrollList.size() < 2) {
            ValidationUtils.validate("报名时间列表不完整");
        }
        ExamPlanDO update = new ExamPlanDO();
        update.setEnrollStartTime(DateUtil.parse(enrollList.get(0)));
        update.setEnrollEndTime(DateUtil.parse(enrollList.get(1)));

        // 校验报名结束时间不能晚于考试开始时间
        ValidationUtils.throwIf(!DateUtil.validateEnrollmentTime(update.getEnrollEndTime(), req
                .getStartTime()), "报名结束时间不能晚于考试开始时间");

        // 考试开始时间不能早于当前时间
        ValidationUtils.throwIf(
                req.getStartTime().isBefore(LocalDateTime.now().minusSeconds(2)),
                "考试开始时间不能早于当前时间"
        );

        update.setId(planId);
        update.setStartTime(req.getStartTime());
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 随机生成监考员开考密码
     *
     * @return
     */
    private String generateExamPassword() {
        return RandomUtil.randomNumbers(6);
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
                                            Long classId,
                                            String className) {
        try {
            return candidateTicketReactiveService.generateWorkerTicket(userId, idCard, encryptedExamNo, classId,className);
        } catch (Exception e) {
            throw new BusinessException("生成考生【" + nickname + "】的准考证失败，请稍后重试");
        }
    }
}