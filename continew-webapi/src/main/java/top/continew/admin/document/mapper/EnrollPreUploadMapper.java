package top.continew.admin.document.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotEmpty;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.document.model.dto.EnrollPrePassDTO;
import top.continew.admin.document.model.resp.EnrollPreUploadDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.document.model.entity.EnrollPreUploadDO;

import java.util.List;

/**
* 机构报考-考生扫码上传文件 Mapper
*
* @author ilhaha
* @since 2025/10/23 10:03
*/
public interface EnrollPreUploadMapper extends BaseMapper<EnrollPreUploadDO> {

    String getUsernameById(@Param("candidateId") Long candidateId);

    IPage<EnrollPreUploadDetailResp> page(@Param("page") Page<Object> objectPage, @Param(Constants.WRAPPER) QueryWrapper<EnrollPreUploadDO> queryWrapper);

    List<EnrollPrePassDTO> selectPreDoc(@Param("reviewIds") List<Long> reviewIds);

}