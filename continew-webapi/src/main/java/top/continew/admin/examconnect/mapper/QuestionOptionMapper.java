package top.continew.admin.examconnect.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.req.dto.OptionDTO;

import java.util.List;

/**
 * 题目选项 Mapper
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
public interface QuestionOptionMapper extends BaseMapper<OptionDTO> {

    /**
     * 查询指定题目的所有选项
     */
    List<OptionDTO> selectByQuestionId(@Param("questionId") Long questionId);
}
