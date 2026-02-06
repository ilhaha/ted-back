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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.enums.ExamPlanTypeEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.examconnect.model.resp.ExamPaperVO;
import top.continew.admin.examconnect.service.QuestionBankService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.util.ExcelUtilReactive;
import top.continew.admin.common.util.InMemoryMultipartFile;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.mapper.WorkerExamTicketMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.worker.model.entity.WorkerExamTicketDO;
import top.continew.starter.core.exception.BusinessException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("candidateTicketReactiveService")
@RequiredArgsConstructor
public class CandidateTicketReactiveServiceImpl implements CandidateTicketService {

    private final ExamTicketMapper examTicketMapper;
    private final ExamIdcardMapper examIdcardMapper;
    private final AESWithHMAC aesWithHMAC;
    private final ExcelUtilReactive excelUtilReactive;
    private final RestTemplate restTemplate = new RestTemplate(); // 同步下载照片
    private final UserMapper userMapper;
    private final WorkerApplyMapper workerApplyMapper;

    private final FileService fileService;

    @Value("${excel.template.admission-ticket.url}")
    private String excelTemplateUrl;

    @Resource
    private EnrollMapper enrollMapper;

    @Resource
    private WorkerExamTicketMapper workerExamTicketMapper;

    private final QuestionBankService questionBankService;

    private final CandidateExamPaperMapper candidateExamPaperMapper;

    private final ExamPlanMapper examPlanMapper;

    private final ProjectMapper projectMapper;

    @Override
    public String generateWorkerTicket(Long userId, String idCard, String examNumber, Long classId, String className) {
        // 1. 查询准考证数据（同步）
        CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, examNumber);
        if (dto == null) {
            throw new RuntimeException("未找到该用户的准考证数据！");
        }

        //

        // 2. 查询照片URL（同步）
        WorkerApplyDO workerApplyDO = workerApplyMapper.selectOne(new LambdaQueryWrapper<WorkerApplyDO>()
            .eq(WorkerApplyDO::getIdCardNumber, idCard)
            .eq(WorkerApplyDO::getClassId, classId));

        String photoUrl = workerApplyDO != null ? workerApplyDO.getFacePhoto() : null;

        // 3. 解密并组装数据
        dto.setTicketId(aesWithHMAC.verifyAndDecrypt(examNumber));
        dto.setIdCard(aesWithHMAC.verifyAndDecrypt(dto.getIdCard()));
        dto.setClassCode(className);
        Map<String, Object> dataMap = assembleData(dto);

        // 4. 同步下载照片
        byte[] photoBytes = loadPhotoSync(photoUrl);

        // 5. 同步生成PDF响应
        String fileName = "准考证_" + examNumber + ".pdf";

        ResponseEntity<byte[]> responseEntity = excelUtilReactive
            .generatePdfResponseEntitySync(dataMap, excelTemplateUrl, photoBytes, fileName);

        MultipartFile pdfFile = new InMemoryMultipartFile("file", idCard + "_WORKER_准考证.pdf", "application/pdf", responseEntity
            .getBody());
        // 上传 OSS
        FileInfoResp fileInfoResp = fileService.upload(pdfFile, new GeneralFileReq());
        return fileInfoResp.getUrl();
    }

    @Override
    public List<Map<String, Object>> queryByIdCardAndPhone(String username, String phone) {

        // 身份证解密再加密
        String encryptedIdCard = aesWithHMAC.encryptAndSign(SecureUtils.decryptByRsaPrivateKey(username));

        // 电话号码解密再加密
        String encryptedPhone = aesWithHMAC.encryptAndSign(SecureUtils.decryptByRsaPrivateKey(phone));

        // 查询用户ID
        UserDO user = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, encryptedIdCard)
            .eq(UserDO::getPhone, encryptedPhone)
            .select(UserDO::getId));

        if (user == null || user.getId() == null) {
            throw new BusinessException("用户信息不存在或身份证和电话号码不匹配");
        }

        Long userId = user.getId();
        // 查询报名记录
        List<Long> enrollIds = enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
            .eq(EnrollDO::getUserId, userId)
            .eq(EnrollDO::getEnrollStatus, 1)
            .select(EnrollDO::getId)).stream().map(EnrollDO::getId).toList();

        if (enrollIds.isEmpty()) {
            throw new BusinessException("未找到该用户报名记录");
        }

        // 查询准考证
        List<Map<String, Object>> tickets = examTicketMapper.findTicketsByEnrollIds(enrollIds);

        if (tickets == null || tickets.isEmpty()) {
            throw new BusinessException("该用户暂无可下载准考证");
        }
        return tickets;
    }

    @Override
    public ResponseEntity<byte[]> generateByEnrollId(Long enrollId) {
        //通过报名id查询用户id和准考证号
        EnrollDO enrollDO = enrollMapper.selectById(enrollId);
        if (enrollDO == null) {
            throw new BusinessException("未找到该报名记录");
        }
        String encryptedExamNumber = enrollDO.getExamNumber();
        if (encryptedExamNumber == null || encryptedExamNumber.isEmpty()) {
            throw new BusinessException("准考证号为空，无法生成准考证");
        }
        //判断是作业人员还是检验人员
        if (enrollDO.getClassId() == null) {
            // 提取考试计划ID，提前校验非空
            Long examPlanId = enrollDO.getExamPlanId();
            if (examPlanId == null) {
                throw new BusinessException("考试计划ID为空，无法查询准考证截止时间");
            }
            // 通过考试计划id查找准考证下载截至时间
            ExamPlanDO examPlanDO = examPlanMapper.selectById(examPlanId);
            if (examPlanDO == null) {
                throw new BusinessException("未找到该考试计划记录");
            }
            // 获取准考证下载截止时间，校验非空
            LocalDateTime admitCardEndTime = examPlanDO.getAdmitCardEndTime();
            if (admitCardEndTime == null) {
                throw new BusinessException("准考证下载截至时间未设置，无法生成准考证");
            }
            // 截止时间 -> 毫秒级时间戳（使用固定北京时区，避免依赖服务器环境）
            long admitCardEndTimestamp = admitCardEndTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
            // 获取当前时间戳（毫秒级），进行截止时间校验
            long currentTimestamp = System.currentTimeMillis();
            if (currentTimestamp > admitCardEndTimestamp) {
                throw new BusinessException("准考证下载已截止，无法生成准考证");
            }
            //检验人员
            Long userId = enrollDO.getUserId();
            String examNumber = aesWithHMAC.verifyAndDecrypt(enrollDO.getExamNumber());
            // 调用同步方法，直接返回结果
            return generateTicket(userId, examNumber);
        } else {
            //查询作业人员准考证表
            WorkerExamTicketDO workerExamTicketDO = workerExamTicketMapper
                .selectOne(new LambdaQueryWrapper<WorkerExamTicketDO>().eq(WorkerExamTicketDO::getEnrollId, enrollId)
                    .select(WorkerExamTicketDO::getTicketUrl));
            if (workerExamTicketDO == null) {
                throw new BusinessException("未找到该作业人员准考证记录");
            }
            //获取准考证url
            String ticketUrl = workerExamTicketDO.getTicketUrl();
            //下载准考证转换成字节流
            try {
                byte[] pdfBytes = restTemplate.getForObject(ticketUrl, byte[].class);
                if (pdfBytes == null || pdfBytes.length == 0) {
                    throw new BusinessException("下载准考证失败，文件为空");
                }
                String examNumber = aesWithHMAC.verifyAndDecrypt(enrollDO.getExamNumber());
                String fileName = "准考证_" + examNumber + ".pdf";
                return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
            } catch (Exception e) {
                throw new BusinessException("下载准考证失败：" + e.getMessage());
            }
        }
    }

    // 完全同步执行，适配 MVC
    @Override
    public ResponseEntity<byte[]> generateTicket(Long userId, String examNumber) {

        // ========= 1. 查询准考证 =========
        String encryptedExamNumber = aesWithHMAC.encryptAndSign(examNumber);
        CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, encryptedExamNumber);

        if (dto == null) {
            throw new BusinessException("未找到该用户的准考证数据！");
        }

        // ========= 2. 截止时间校验 =========
        LocalDateTime admitCardEndTime = dto.getAdmitCardEndTime();
        if (admitCardEndTime == null) {
            throw new BusinessException("准考证下载截至时间未设置，无法生成准考证");
        }

        long admitCardEndTimestamp = admitCardEndTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();

        if (System.currentTimeMillis() > admitCardEndTimestamp) {
            throw new BusinessException("准考证下载已截止，无法生成准考证");
        }

        // ========= 3. 查询照片 =========
        ExamIdcardDO idCard = examIdcardMapper.selectOne(new QueryWrapper<ExamIdcardDO>().eq("id_card_number", dto
            .getIdCard()).eq("is_deleted", 0).select("face_photo"));

        String photoUrl = idCard != null ? idCard.getFacePhoto() : null;

        // ========= 4. 组装数据 =========
        dto.setTicketId(examNumber);
        dto.setIdCard(aesWithHMAC.verifyAndDecrypt(dto.getIdCard()));

        Map<String, Object> dataMap = assembleData(dto);

        // ========= 5. 查报名信息 =========
        EnrollDO record = enrollMapper.selectOne(new LambdaQueryWrapper<EnrollDO>().eq(EnrollDO::getUserId, userId)
            .eq(EnrollDO::getExamNumber, encryptedExamNumber)
            .select(EnrollDO::getExamPlanId, EnrollDO::getId));

        if (record == null) {
            throw new BusinessException("未找到报名信息");
        }

        // ========= 6. 异步生成试卷 =========
        //判断是否需要生成试卷（只有检验检测一级考试才需要生成试卷）
        ExamPlanDO examPlanDO = examPlanMapper.selectById(record.getExamPlanId());
        ProjectDO projectDO = projectMapper.selectById(examPlanDO.getExamProjectId());
        if (ExamPlanTypeEnum.INSPECTION.getValue().equals(examPlanDO.getPlanType()) && projectDO
            .getIsTheory() != null && projectDO.getIsTheory() == 1 && projectDO.getProjectLevel() != null && projectDO
                .getProjectLevel() == 1) {
            // 异步生成试卷
            asyncGenerateExamPaper(record.getExamPlanId(), record.getId());
        }

        // ========= 7. 下载照片 =========
        byte[] photoBytes = loadPhotoSync(photoUrl);

        // ========= 8. 返回 PDF =========
        String fileName = "准考证_" + examNumber + ".pdf";
        return excelUtilReactive.generatePdfResponseEntitySync(dataMap, excelTemplateUrl, photoBytes, fileName);
    }

    @Async
    public void asyncGenerateExamPaper(Long planId, Long enrollId) {
        try {
            // 数据库检查是否已生成试卷
            Long count = candidateExamPaperMapper.selectCount(new QueryWrapper<CandidateExamPaperDO>()
                .eq("enroll_id", enrollId));
            if (count != null && count > 0) {
                log.info("试卷已生成过，跳过 enrollId={}", enrollId);
                return;
            }
            //  生成试卷
            ObjectMapper objectMapper = new ObjectMapper();
            ExamPaperVO examPaperVO = questionBankService.generateExamQuestionBank(planId);

            CandidateExamPaperDO candidateExamPaperDO = new CandidateExamPaperDO();
            candidateExamPaperDO.setPaperJson(objectMapper.writeValueAsString(examPaperVO));
            candidateExamPaperDO.setEnrollId(enrollId);

            candidateExamPaperMapper.insert(candidateExamPaperDO);

            log.info("试卷生成成功 enrollId={}", enrollId);

        } catch (Exception e) {
            log.error("异步生成试卷失败 enrollId={}", enrollId, e);
        }
    }

    // 同步下载照片（适配MVC）
    private byte[] loadPhotoSync(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            log.warn("照片 URL 为空，返回空字节数组");
            return new byte[0];
        }
        try {
            // 同步下载照片
            log.info("开始同步下载照片 URL={}", photoUrl);
            byte[] photoBytes = restTemplate.getForObject(photoUrl, byte[].class);
            return photoBytes != null ? photoBytes : new byte[0];
        } catch (Exception e) {
            log.error("同步下载照片失败：URL={}", photoUrl, e);
            return new byte[0];
        }
    }

    // 组装数据（不变）
    private Map<String, Object> assembleData(CandidateTicketDTO dto) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", getSafeValue(dto.getName()));
        dataMap.put("idCard", getSafeValue(dto.getIdCard()));
        dataMap.put("ticketId", getSafeValue(dto.getTicketId()));
        dataMap.put("classCode", getSafeValue(dto.getClassCode()));
        dataMap.put("className", getSafeValue(dto.getClassName()));
        dataMap.put("examType", getSafeValue(dto.getExamType()));
        dataMap.put("examItem", getSafeValue(dto.getExamItem()));
        dataMap.put("examRoom", getSafeValue(dto.getExamRoom()));
        dataMap.put("examTime", getSafeValue(dto.getExamTime()));
        return dataMap;
    }

    private String getSafeValue(String v) {
        return v == null ? "" : v.trim();
    }
}