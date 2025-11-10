package top.continew.admin.worker.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.worker.model.dto.WorkerApplyDocAndNameDTO;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDocumentDO;

import java.util.List;

/**
* 作业人员报名上传的资料 Mapper
*
* @author ilhaha
* @since 2025/10/31 09:35
*/
public interface WorkerApplyDocumentMapper extends BaseMapper<WorkerApplyDocumentDO> {
    IPage<WorkerApplyDocumentDetailResp> page(@Param("page") Page<Object> page,  @Param(Constants.WRAPPER) QueryWrapper<WorkerApplyDocumentDO> queryWrapper);

    /**
     * 获取作业人员对应的上传资料的名称
     * @param workerApplyIds
     * @return
     */
    List<WorkerApplyDocAndNameDTO> selectDocAndName(@Param("workerApplyIds") List<Long> workerApplyIds);
}