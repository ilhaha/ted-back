/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.training.mapper.OrgTrainingPriceMapper;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.model.entity.OrgTrainingPriceDO;
import top.continew.admin.training.model.entity.TedOrgUser;
import top.continew.admin.training.model.query.OrgTrainingPriceQuery;
import top.continew.admin.training.model.req.OrgTrainingPriceReq;
import top.continew.admin.training.model.resp.OrgTrainingPriceDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPriceResp;
import top.continew.admin.training.service.OrgTrainingPriceService;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）业务实现
 *
 * @author
 * @since 2025/11/10
 */
@Service
@RequiredArgsConstructor
public class OrgTrainingPriceServiceImpl extends BaseServiceImpl<OrgTrainingPriceMapper, OrgTrainingPriceDO, OrgTrainingPriceResp, OrgTrainingPriceDetailResp, OrgTrainingPriceQuery, OrgTrainingPriceReq> implements OrgTrainingPriceService {

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
        //  获取当前登录用户ID
        Long userId = TokenLocalThreadUtil.get().getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        //  根据用户ID查机构ID
        Long orgId = orgUserMapper.selectOne(new LambdaQueryWrapper<TedOrgUser>().eq(TedOrgUser::getUserId, userId)
            .eq(TedOrgUser::getIsDeleted, false)
            .select(TedOrgUser::getOrgId)) != null
                ? orgUserMapper.selectOne(new LambdaQueryWrapper<TedOrgUser>().eq(TedOrgUser::getUserId, userId)
                    .eq(TedOrgUser::getIsDeleted, false)
                    .select(TedOrgUser::getOrgId)).getOrgId()
                : null;

        if (orgId == null) {
            throw new BusinessException("未找到所属机构，请联系管理员");
        }

        // 在查询条件中设置机构ID，确保只查本机构
        query.setOrgId(orgId);

        // 执行分页查询（父类 page 会使用 orgId 过滤）
        PageResp<OrgTrainingPriceResp> pageResp = super.page(query, pageQuery);

        //  获取机构名称
        String orgName = TokenLocalThreadUtil.get().getNickname();
        // 填充机构名和项目名
        if (pageResp.getList() != null && !pageResp.getList().isEmpty()) {
            for (OrgTrainingPriceResp record : pageResp.getList()) {
                record.setOrgName(orgName);

                if (record.getProjectId() != null) {
                    ProjectDO projectDO = projectMapper.selectOne(new LambdaQueryWrapper<ProjectDO>()
                        .eq(ProjectDO::getId, record.getProjectId())
                        .eq(ProjectDO::getIsDeleted, 0)
                        .select(ProjectDO::getProjectName));
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

        TedOrgUser user = orgUserMapper.selectOne(new LambdaQueryWrapper<TedOrgUser>()
            .eq(TedOrgUser::getUserId, TokenLocalThreadUtil.get().getUserId())
            .eq(TedOrgUser::getIsDeleted, 0)
            .select(TedOrgUser::getOrgId));
        // 校验唯一约束（org_id + project_id 不能重复）
        OrgTrainingPriceDO exist = this.getOne(new LambdaQueryWrapper<OrgTrainingPriceDO>()
            .eq(OrgTrainingPriceDO::getOrgId, user.getOrgId())
            .eq(OrgTrainingPriceDO::getProjectId, req.getProjectId())
            .eq(OrgTrainingPriceDO::getIsDeleted, 0));

        ValidationUtils.throwIf(Objects.nonNull(exist), String.format("已存在价格记录，无需重复添加"));

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
