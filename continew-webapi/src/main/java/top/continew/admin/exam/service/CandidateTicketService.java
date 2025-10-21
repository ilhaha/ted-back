package top.continew.admin.exam.service;

/**
 * CandidateTicketService —— 准考证生成业务接口
 */
public interface CandidateTicketService {

    /**
     * 根据用户ID + 准考证号生成准考证（Word + PDF）
     *
     * @param userId 用户ID
     * @param examNumber 准考证号
     * @return PDF 文件路径
     * @throws Exception 异常
     */
    String generateTicket(Long userId, String examNumber) throws Exception;
}
