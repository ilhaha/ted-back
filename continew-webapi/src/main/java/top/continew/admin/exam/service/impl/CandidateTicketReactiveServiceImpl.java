package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ExamIdcardMapper;
import top.continew.admin.exam.mapper.ExamTicketMapper;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;
import top.continew.admin.exam.model.entity.ExamIdcardDO;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.util.ExcelUtilReactive;
import top.continew.admin.util.InMemoryMultipartFile;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;

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


    @Override
    public String generateWorkerTicket(Long userId,String idCard, String examNumber,Long classId) {
        try {
            // 1. 查询准考证数据（同步）
            CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, examNumber);
            if (dto == null) {
                throw new RuntimeException("未找到该用户的准考证数据！");
            }

            // 2. 查询照片URL（同步）
            WorkerApplyDO workerApplyDO =
                    workerApplyMapper.selectOne(new LambdaQueryWrapper<WorkerApplyDO>()
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

            ResponseEntity<byte[]> responseEntity = excelUtilReactive.generatePdfResponseEntitySync(dataMap, excelTemplateUrl, photoBytes, fileName);

            MultipartFile pdfFile = new InMemoryMultipartFile(
                    "file",
                    idCard + "_WORKER_准考证.pdf",
                    "application/pdf",
                    responseEntity.getBody()
            );


            // 上传 OSS
            FileInfoResp fileInfoResp = fileService.upload(pdfFile, new GeneralFileReq());

            return fileInfoResp.getUrl();

        } catch (Exception e) {
            log.error("生成准考证失败：userId={}, examNumber={}", userId, examNumber, e);
            return null;
        }
    }

    // 完全同步执行，适配MVC
    @Override
    public ResponseEntity<byte[]> generateTicket(Long userId, String examNumber) {
        try {
            // 1. 查询准考证数据（同步）
            String encryptedExamNumber = aesWithHMAC.encryptAndSign(examNumber);
            CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, encryptedExamNumber);
            if (dto == null) {
                throw new RuntimeException("未找到该用户的准考证数据！");
            }

            // 2. 查询照片URL（同步）
            QueryWrapper<ExamIdcardDO> queryWrapper = new QueryWrapper<ExamIdcardDO>()
                    .eq("id_card_number", dto.getIdCard())
                    .eq("is_deleted", 0)
                    .select("face_photo");
            ExamIdcardDO idCard = examIdcardMapper.selectOne(queryWrapper);
            String photoUrl = idCard != null ? idCard.getFacePhoto() : null;

            // 3. 解密并组装数据
            dto.setTicketId(examNumber);
            dto.setIdCard(aesWithHMAC.verifyAndDecrypt(dto.getIdCard()));
            Map<String, Object> dataMap = assembleData(dto);
            log.info("照片 URL={}, 准考证数据Map={}", photoUrl, dataMap);

            // 4. 同步下载照片
            byte[] photoBytes = loadPhotoSync(photoUrl);
            log.info("下载照片完成，大小={}KB", photoBytes.length / 1024);

            // 5. 同步生成PDF响应
            String fileName = "准考证_" + examNumber + ".pdf";
            return excelUtilReactive.generatePdfResponseEntitySync(dataMap, excelTemplateUrl, photoBytes, fileName);

        } catch (Exception e) {
            log.error("生成准考证失败：userId={}, examNumber={}", userId, examNumber, e);
            String errorMsg = "下载准考证失败：" + e.getMessage();
            return ResponseEntity.internalServerError()
                    .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                    .body(errorMsg.getBytes(java.nio.charset.StandardCharsets.UTF_8));
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