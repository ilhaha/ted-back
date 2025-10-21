package top.continew.admin.exam.service.impl;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.mapper.ExamTicketMapper;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.util.TicketWordPdfUtil;


/**
 * CandidateTicketServiceImpl —— 准考证生成业务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateTicketServiceImpl implements CandidateTicketService {

    private final ExamTicketMapper examTicketMapper;
    @Resource
    private AESWithHMAC aesWithHMAC;


    @Override
    public String generateTicket(Long userId, String examNumber) throws Exception {
        // 1. 根据 userId + examNumber 查询数据
        examNumber = (aesWithHMAC.encryptAndSign(examNumber));
        CandidateTicketDTO dto = examTicketMapper.findTicketByUserAndExamNumber(userId, examNumber);
        if (dto == null) {
            throw new RuntimeException("未找到该用户的准考证数据！");
        }
//        // 2. 生成 Word + PDF 文件
//        String outputDir = "output";
//        String pdfPath = TicketWordPdfUtil.generateTicketDocAndPdf(dto, outputDir);
//        return pdfPath;
        return "功能开发中，敬请期待！";
    }
}
