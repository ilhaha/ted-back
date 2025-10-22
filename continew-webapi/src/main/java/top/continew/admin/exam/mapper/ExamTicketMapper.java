package top.continew.admin.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;

/**
 * 准考证数据访问层
 */
public interface ExamTicketMapper extends BaseMapper<CandidateTicketDTO> {

    /**
     * 根据用户ID和准考证号查询考生信息
     */
    CandidateTicketDTO findTicketByUserAndExamNumber(Long userId, String examNumber);
}