package top.continew.admin.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.OrgClassMapper;
import top.continew.admin.training.model.entity.OrgClassDO;
import top.continew.admin.training.model.query.OrgClassQuery;
import top.continew.admin.training.model.req.OrgClassReq;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.resp.OrgClassResp;
import top.continew.admin.training.service.OrgClassService;

/**
 * 培训机构班级业务实现
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Service
@RequiredArgsConstructor
public class OrgClassServiceImpl extends BaseServiceImpl<OrgClassMapper, OrgClassDO, OrgClassResp, OrgClassDetailResp, OrgClassQuery, OrgClassReq> implements OrgClassService {

    @Resource
    private OrgUserMapper orgUserMapper;

    /**
     * 重写分页查询
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<OrgClassResp> page(OrgClassQuery query, PageQuery pageQuery) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());
        QueryWrapper<OrgClassDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("toc.org_id", orgDO.getId());
        queryWrapper.eq("toc.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<OrgClassDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        PageResp<OrgClassResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 重写添加
     * @param req
     * @return
     */
    @Override
    public Long add(OrgClassReq req) {
        // 看看班级是否重名
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());
        LambdaQueryWrapper<OrgClassDO> orgClassDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orgClassDOLambdaQueryWrapper.eq(OrgClassDO::getClassName, req.getClassName());
        orgClassDOLambdaQueryWrapper.eq(OrgClassDO::getOrgId, orgDO.getId());
        ValidationUtils.throwIf(baseMapper.selectCount(orgClassDOLambdaQueryWrapper) > 0, " [ " + req.getClassName() + " ] 已存在");
        req.setOrgId(orgDO.getId());
        return super.add(req);
    }


    @Override
    public void update(OrgClassReq req, Long id) {
        // 获取当前登录用户的机构信息
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());

        // 检查是否重名（排除当前记录）
        LambdaQueryWrapper<OrgClassDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgClassDO::getClassName, req.getClassName())
                .eq(OrgClassDO::getOrgId, orgDO.getId())
                .ne(OrgClassDO::getId, id);
        ValidationUtils.throwIf(
                baseMapper.selectCount(wrapper) > 0,
                "班级名称 [ " + req.getClassName() + " ] 已存在"
        );

        // 设置机构 ID 并更新
        req.setOrgId(orgDO.getId());
        super.update(req, id);
    }
}