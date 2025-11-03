package top.continew.admin.worker.mapper;

import jakarta.validation.constraints.NotEmpty;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.worker.model.resp.ProjectNeedUploadDocVO;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;

import java.util.List;

/**
* 作业人员报名 Mapper
*
* @author ilhaha
* @since 2025/10/31 10:20
*/
public interface WorkerApplyMapper extends BaseMapper<WorkerApplyDO> {

    List<ProjectNeedUploadDocVO> selectProjectNeedUploadDoc(@Param("classId") Long classId);

}