package top.continew.admin.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.CategoryMapper;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.entity.CategoryDO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.training.mapper.OrgMapper;
import top.continew.admin.training.mapper.OrgTrainingPriceMapper;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.training.model.entity.OrgTrainingPriceDO;
import top.continew.admin.training.model.entity.TedOrgUser;
import top.continew.admin.training.model.query.OrgTrainingPriceQuery;
import top.continew.admin.training.model.req.OrgTrainingPriceReq;
import top.continew.admin.training.model.resp.OrgTrainingPriceDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPriceResp;
import top.continew.admin.training.service.OrgTrainingPriceService;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.query.SortQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）业务实现
 *
 * @author
 * @since 2025/11/10
 */
@Service
@RequiredArgsConstructor
public class OrgTrainingPriceServiceImpl extends BaseServiceImpl<
        OrgTrainingPriceMapper,
        OrgTrainingPriceDO,
        OrgTrainingPriceResp,
        OrgTrainingPriceDetailResp,
        OrgTrainingPriceQuery,
        OrgTrainingPriceReq
        > implements OrgTrainingPriceService {

    @Resource
    private OrgTrainingPriceMapper orgTrainingPriceMapper;

    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private OrgUserMapper orgUserMapper;

    /**
     * 重写page
     */

    @Override
    public PageResp<OrgTrainingPriceResp> page(OrgTrainingPriceQuery query, PageQuery pageQuery) {
        //  调用父类分页查询
        PageResp<OrgTrainingPriceResp> pageResp = super.page(query, pageQuery);

        // 获取当前机构名称
        String orgName = TokenLocalThreadUtil.get().getNickname();

        //填充附加字段（机构名、八大类名）
        if (pageResp.getList() != null && !pageResp.getList().isEmpty()) {
            for (OrgTrainingPriceResp record : pageResp.getList()) {
                // 设置机构名称
                record.setOrgName(orgName);

                // 查询八大类名称
                if (record.getProjectId() != null) {
                    ProjectDO projectDO = projectMapper.selectOne(
                            new LambdaQueryWrapper<ProjectDO>()
                                    .eq(ProjectDO::getId, record.getProjectId())
                                    .eq(ProjectDO::getIsDeleted, 0)
                                    .select(ProjectDO::getProjectName)
                    );
                    record.setProjectName(projectDO != null ? projectDO.getProjectName() : null);
                }
            }
        }

        return pageResp;
    }


    /**
     * 新增机构培训价格
     *
     * @param req 新增请求参数
     * @return 新增成功的主键ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(OrgTrainingPriceReq req) {
        validateParam(req);

        TedOrgUser user = orgUserMapper.selectOne(
                new LambdaQueryWrapper<TedOrgUser>()
                        .eq(TedOrgUser::getUserId,TokenLocalThreadUtil.get().getUserId())
                        .eq(TedOrgUser::getIsDeleted, 0)
                        .select(TedOrgUser::getOrgId)
        );
        // 校验唯一约束（org_id + project_id 不能重复）
        OrgTrainingPriceDO exist = this.getOne(
                new LambdaQueryWrapper<OrgTrainingPriceDO>()
                        .eq(OrgTrainingPriceDO::getOrgId, user.getOrgId())
                        .eq(OrgTrainingPriceDO::getProjectId, req.getProjectId())
                        .eq(OrgTrainingPriceDO::getIsDeleted, 0)
        );

        ValidationUtils.throwIf(
                Objects.nonNull(exist),
                String.format("已存在价格记录，无需重复添加")
        );

        // 构建持久化实体
        OrgTrainingPriceDO entity = new OrgTrainingPriceDO();
        entity.setOrgId(user.getOrgId());
        entity.setProjectId(req.getProjectId());
        entity.setPrice(req.getPrice());

        // 插入数据
        orgTrainingPriceMapper.insert(entity);

        return entity.getId();
    }

    /**
     * 参数校验（非空+合法性）
     */
    private void validateParam(OrgTrainingPriceReq req) {
        ValidationUtils.throwIfNull(req, "请求参数不能为空");
        ValidationUtils.throwIfNull(req.getProjectId(), "八大类项目ID不能为空");
        ValidationUtils.throwIfNull(req.getPrice(), "培训价格不能为空");

        // 价格必须大于0，且最多2位小数
        ValidationUtils.throwIf(req.getPrice().compareTo(BigDecimal.ZERO) <= 0, "培训价格必须大于0元");

        String priceStr = req.getPrice().stripTrailingZeros().toPlainString();
        if (priceStr.contains(".")) {
            int decimalLen = priceStr.split("\\.")[1].length();
            ValidationUtils.throwIf(decimalLen > 2, "培训价格最多支持2位小数");
        }
    }
}
