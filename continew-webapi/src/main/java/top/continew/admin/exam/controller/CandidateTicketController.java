package top.continew.admin.exam.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import top.continew.admin.exam.service.CandidateTicketService;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/exam/ticket")
@RequiredArgsConstructor
public class CandidateTicketController {

    private final CandidateTicketService candidateTicketService;

    @PostMapping("/download")
    public void downloadTicket(@RequestBody Map<String, Object> params,
                               HttpServletResponse response) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String examNumber = params.get("examNumber").toString();

            candidateTicketService.generateTicket(userId, examNumber, response);
        } catch (Exception e) {
            log.error("下载准考证失败", e);
            try {
                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain;charset=UTF-8");
                try (OutputStream os = response.getOutputStream()) {
                    os.write(("下载失败：" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception ex) {
                log.error("响应错误信息失败", ex);
            }
        }
    }
}
