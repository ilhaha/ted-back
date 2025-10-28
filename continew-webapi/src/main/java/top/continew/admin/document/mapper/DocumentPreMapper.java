package top.continew.admin.document.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.document.model.resp.DocumentPreDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.document.model.entity.DocumentPreDO;

/**
* 机构报考-考生上传资料 Mapper
*
* @author ilhaha
* @since 2025/10/23 10:03
*/
public interface DocumentPreMapper extends BaseMapper<DocumentPreDO> {
    IPage<DocumentPreDetailResp> selectDocumentPrePage(@Param("page") Page<Object> page,
                                                       @Param(Constants.WRAPPER) QueryWrapper<DocumentPreDO> queryWrapper);
}