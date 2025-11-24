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
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.mapper.CandidateExamPaperMapper;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.mapper.ExamIdcardMapper;
import top.continew.admin.exam.mapper.ExamTicketMapper;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;
import top.continew.admin.exam.model.entity.CandidateExamPaperDO;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.ExamIdcardDO;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.examconnect.model.resp.ExamPaperVO;
import top.continew.admin.examconnect.service.QuestionBankService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.util.ExcelUtilReactive;
import top.continew.admin.util.InMemoryMultipartFile;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;
import top.continew.starter.core.exception.BusinessException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

    private final QuestionBankService questionBankService;

    private final CandidateExamPaperMapper candidateExamPaperMapper;

    @Override
    public String generateWorkerTicket(Long userId, String idCard, String examNumber, Long classId) {
        // 1. 查询准考证数据（同步）
        CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, examNumber);
        if (dto == null) {
            throw new RuntimeException("未找到该用户的准考证数据！");
        }

        // 2. 查询照片URL（同步）
        WorkerApplyDO workerApplyDO = workerApplyMapper.selectOne(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getIdCardNumber, idCard)
                .eq(WorkerApplyDO::getClassId, classId));

        String photoUrl = workerApplyDO != null ? workerApplyDO.getFacePhoto() : null;

        // 3. 解密并组装数据
        dto.setTicketId(aesWithHMAC.verifyAndDecrypt(examNumber));
        dto.setIdCard(aesWithHMAC.verifyAndDecrypt(dto.getIdCard()));
        Map<String, Object> dataMap = assembleData(dto);

        // 4. 同步下载照片
        byte[] photoBytes = loadPhotoSync(photoUrl);

        // 5. 同步生成PDF响应
        String fileName = "准考证_" + examNumber + ".pdf";

        ResponseEntity<byte[]> responseEntity = excelUtilReactive
                .generatePdfResponseEntitySync(dataMap, excelTemplateUrl, photoBytes, fileName);

        MultipartFile pdfFile =
                new InMemoryMultipartFile("file", idCard + "_WORKER_准考证.pdf", "application/pdf", responseEntity
                .getBody());
        // 上传 OSS
        FileInfoResp fileInfoResp = fileService.upload(pdfFile, new GeneralFileReq());
        return fileInfoResp.getUrl();
    }

    // 完全同步执行，适配MVC
    @Override
    public ResponseEntity<byte[]> generateTicket(Long userId, String examNumber) {
        try {
            // 查询准考证数据
            String encryptedExamNumber = aesWithHMAC.encryptAndSign(examNumber);
            CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, encryptedExamNumber);
            if (dto == null) {
                throw new RuntimeException("未找到该用户的准考证数据！");
            }

            // 查询照片URL
            QueryWrapper<ExamIdcardDO> queryWrapper = new QueryWrapper<ExamIdcardDO>().eq("id_card_number", dto
                .getIdCard()).eq("is_deleted", 0).select("face_photo");
            ExamIdcardDO idCard = examIdcardMapper.selectOne(queryWrapper);
            String photoUrl = idCard != null ? idCard.getFacePhoto() : null;

            // 解密并组装数据
            dto.setTicketId(examNumber);
            dto.setIdCard(aesWithHMAC.verifyAndDecrypt(dto.getIdCard()));
            Map<String, Object> dataMap = assembleData(dto);

            //查找考生报名信息
            EnrollDO record = enrollMapper.selectOne(new LambdaQueryWrapper<EnrollDO>().eq(EnrollDO::getUserId, userId)
                .eq(EnrollDO::getExamNumber, aesWithHMAC.encryptAndSign(examNumber))
                .select(EnrollDO::getExamPlanId, EnrollDO::getId));
            // 异步生成试卷
            asyncGenerateExamPaper(record.getExamPlanId(), record.getId());

            // 下载照片
            byte[] photoBytes = loadPhotoSync(photoUrl);

            //返回PDF响应
            String fileName = "准考证_" + examNumber + ".pdf";
            return excelUtilReactive.generatePdfResponseEntitySync(dataMap, excelTemplateUrl, photoBytes, fileName);

        } catch (Exception e) {
            log.error("生成准考证失败：", e);
            String errorMsg = "下载准考证失败：" + e.getMessage();
            return ResponseEntity.internalServerError()
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(errorMsg.getBytes(StandardCharsets.UTF_8));
        }
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
        dataMap.put("classCode", getSafeValue(dto.getClassCode() != null ? dto.getClassCode().toString() : null));
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