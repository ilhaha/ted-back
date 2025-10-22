package top.continew.admin.exam.service;

import jakarta.servlet.http.HttpServletResponse;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;

/**
 * 准考证生成业务接口
 */
public interface CandidateTicketService {

    /**
     * 生成准考证PDF并通过响应流返回
     * @param userId 用户ID
     * @param examNumber 准考证号
     * @param response 响应对象
     * @throws Exception 处理过程中的异常
     */
    void generateTicket(Long userId, String examNumber, HttpServletResponse response) throws Exception;
}