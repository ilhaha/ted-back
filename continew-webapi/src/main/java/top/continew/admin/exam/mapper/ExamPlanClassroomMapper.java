package top.continew.admin.exam.mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.exam.model.entity.ExamPlanClassroomDO;
import top.continew.starter.data.mp.base.BaseMapper;


import java.util.List;

/**
 * 考试计划考场关联表 Mapper
 *
 */
public interface ExamPlanClassroomMapper extends BaseMapper<ExamPlanClassroomDO> {

    /**
     * 通过考场ID查询所有关联的考试计划id
     * @param classroomId 考场ID
     * @return 考试计划与考场关联信息列表
     */
    List<ExamPlanClassroomDO> selectByClassroomId(@Param("classroomId") Long classroomId);
}
