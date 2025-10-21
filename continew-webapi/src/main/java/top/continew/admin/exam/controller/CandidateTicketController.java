package top.continew.admin.exam.controller;


import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.service.CandidateTicketService;

/**
 * <p>
 * CandidateTicketController —— 准考证生成接口控制层
 * </p>
 *
 * @since 2025-10-21
 */
@Slf4j
@RestController
@RequestMapping("/api/exam/ticket")
@RequiredArgsConstructor
public class CandidateTicketController {

    private final CandidateTicketService candidateTicketService;


    /**
     * 【接口】根据用户ID + 准考证号生成准考证 PDF
     *
     * 调用示例：
     * GET /api/exam/ticket/generate?userId=10001&examNumber=T20251021
     *
     * @param userId 用户ID（sys_user.id）
     * @param examNumber 准考证号（ted_enroll.exam_number）
     * @return PDF 文件路径
     */
    @GetMapping("/generate")
    public ResponseEntity<?> generateTicket(
            @RequestParam("userId") Long userId,
            @RequestParam("examNumber") String examNumber) {
        try {
            String pdfPath = candidateTicketService.generateTicket(userId, examNumber);
            return ResponseEntity.ok("✅ 准考证生成成功，PDF 路径：" + pdfPath);
        } catch (Exception e) {
            log.error(" 准考证生成失败", e);
            return ResponseEntity.internalServerError()
                    .body("生成准考证失败：" + e.getMessage());
        }
    }
}
