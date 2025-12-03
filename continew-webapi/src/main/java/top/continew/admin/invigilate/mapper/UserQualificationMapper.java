package top.continew.admin.invigilate.mapper;

import org.springframework.data.repository.query.Param;
import top.continew.admin.invigilate.model.dto.UserQualificationDTO;
import top.continew.admin.invigilate.model.resp.UserQualificationResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.invigilate.model.entity.UserQualificationDO;

import java.util.List;

/**
* 监考员资质证明 Mapper
*
* @author ilhaha
* @since 2025/12/02 16:55
*/
public interface UserQualificationMapper extends BaseMapper<UserQualificationDO> {

    List<UserQualificationDTO> listByUserId(@Param("userId") Long userId);
}