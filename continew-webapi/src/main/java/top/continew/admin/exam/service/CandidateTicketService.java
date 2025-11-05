package top.continew.admin.exam.service;


import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * 准考证生成业务接口
 */
public interface CandidateTicketService {
        ResponseEntity<byte[]> generateTicket(Long userId, String examNumber);
}