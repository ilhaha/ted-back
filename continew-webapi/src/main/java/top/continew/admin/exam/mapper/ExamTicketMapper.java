package top.continew.admin.exam.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;

/**
 * ExamTicketMapper —— 准考证联合查询 Mapper
 */
@Mapper
public interface ExamTicketMapper {

    /**
     * 根据用户ID + 准考证号查询准考证信息
     *
     * @param userId 用户ID
     * @param examNumber 准考证号
     * @return CandidateTicketDTO
     */
    CandidateTicketDTO findTicketByUserAndExamNumber(
            @Param("userId") Long userId,
            @Param("examNumber") String examNumber);
}
