package top.continew.admin.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.PlanConstant;
import top.continew.admin.common.constant.ProjectConstant;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.enums.ProjectEnum;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.exam.model.req.ReviewWeldingExamApplicationReq;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.resp.ExamPlanResp;
import top.continew.admin.training.mapper.OrgCategoryRelationMapper;
import top.continew.admin.training.mapper.OrgMapper;
import top.continew.admin.training.model.dto.OrgDTO;
import top.continew.admin.training.model.entity.OrgCategoryRelationDO;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.WeldingExamApplicationMapper;
import top.continew.admin.exam.model.entity.WeldingExamApplicationDO;
import top.continew.admin.exam.model.query.WeldingExamApplicationQuery;
import top.continew.admin.exam.model.req.WeldingExamApplicationReq;
import top.continew.admin.exam.model.resp.WeldingExamApplicationDetailResp;
import top.continew.admin.exam.model.resp.WeldingExamApplicationResp;
import top.continew.admin.exam.service.WeldingExamApplicationService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 机构申请焊接考试项目业务实现
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
@Service
@RequiredArgsConstructor
public class WeldingExamApplicationServiceImpl extends BaseServiceImpl<WeldingExamApplicationMapper, WeldingExamApplicationDO, WeldingExamApplicationResp, WeldingExamApplicationDetailResp, WeldingExamApplicationQuery, WeldingExamApplicationReq> implements WeldingExamApplicationService {

    private final OrgMapper orgMapper;

    private final ProjectMapper projectMapper;

    private final OrgCategoryRelationMapper orgCategoryRelationMapper;

    /**
     * 重新修改
     *
     * @param req
     * @param id
     */
    @Override
    public void update(WeldingExamApplicationReq req, Long id) {
        WeldingExamApplicationDO weldingExamApplicationDO = baseMapper.selectById(id);
        ValidationUtils.throwIfNull(weldingExamApplicationDO, "所选信息已不存在");
        long count = baseMapper.selectCount(new LambdaQueryWrapper<WeldingExamApplicationDO>()
                .eq(WeldingExamApplicationDO::getProjectCode, req.getProjectCode())
                .ne(WeldingExamApplicationDO::getId, id)
//                .eq(WeldingExamApplicationDO::getStatus, WeldingExamApplicationStatusEnum.WAIT_REVIEW.getValue())
                .eq(WeldingExamApplicationDO::getOrgId, weldingExamApplicationDO.getOrgId()));

        ValidationUtils.throwIf(count > 0,
                "项目【" + req.getProjectCode() + "】已存在");
        // 判断项目代码是否属于分焊接类型的
//        ProjectDO projectDO = projectMapper.selectOne(
//                new LambdaQueryWrapper<ProjectDO>()
//                        .eq(ProjectDO::getProjectCode, req.getProjectCode())
//        );
//
//        if (ObjectUtil.isNotNull(projectDO)) {
//            Long categoryId = projectDO.getCategoryId();
//            ValidationUtils.throwIf(
//                    !metalId.equals(categoryId) && !nonmetalId.equals(categoryId),
//                    "项目代码属于非焊接类"
//            );
//            Integer weldingType = req.getWeldingType();
//            ValidationUtils.throwIf(
//                    metalId.equals(categoryId) && WeldingTypeEnum.NON_METAL.getValue().equals(weldingType),
//                    "项目代码属于金属焊接"
//            );
//            ValidationUtils.throwIf(
//                    nonmetalId.equals(categoryId) && WeldingTypeEnum.METAL.getValue().equals(weldingType),
//                    "项目代码属于非金属焊接"
//            );
//        }
        baseMapper.update(new LambdaUpdateWrapper<WeldingExamApplicationDO>()
                .eq(WeldingExamApplicationDO::getId, id)
                .set(WeldingExamApplicationDO::getProjectCode, req.getProjectCode())
                .set(WeldingExamApplicationDO::getWeldingType, req.getWeldingType())
                .set(WeldingExamApplicationDO::getProjectName, req.getWeldingType())
                .set(WeldingExamApplicationDO::getSubmittedAt, LocalDateTime.now())
                .set(WeldingExamApplicationDO::getApplicationReason, req.getApplicationReason())
                .set(WeldingExamApplicationDO::getStatus, WeldingExamApplicationStatusEnum.WAIT_REVIEW.getValue())
                .set(WeldingExamApplicationDO::getReviewComment, null)
                .set(WeldingExamApplicationDO::getReviewedAt, null));
    }

    /**
     * 重写添加
     *
     * @param req
     * @return
     */
    @Override
    public Long add(WeldingExamApplicationReq req) {
        // 获取机构信息
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDTO orgDTO = orgMapper.getOrgId(userTokenDo.getUserId());
        ValidationUtils.throwIfNull(orgDTO, "机构信息不存在");

        long count = baseMapper.selectCount(new LambdaQueryWrapper<WeldingExamApplicationDO>()
                .eq(WeldingExamApplicationDO::getProjectCode, req.getProjectCode())
//                .eq(WeldingExamApplicationDO::getStatus, WeldingExamApplicationStatusEnum.WAIT_REVIEW.getValue())
                .eq(WeldingExamApplicationDO::getOrgId, orgDTO.getId()));

        ValidationUtils.throwIf(count > 0,
                "项目【" + req.getProjectCode() + "】已存在");

        // 判断项目代码是否属于分焊接类型的
//        ProjectDO projectDO = projectMapper.selectOne(
//                new LambdaQueryWrapper<ProjectDO>()
//                        .eq(ProjectDO::getProjectCode, req.getProjectCode())
//        );

//        if (ObjectUtil.isNotNull(projectDO)) {
//            Long categoryId = projectDO.getCategoryId();
//            ValidationUtils.throwIf(
//                    !metalId.equals(categoryId) && !nonmetalId.equals(categoryId),
//                    "项目代码属于非焊接类"
//            );
//            Integer weldingType = req.getWeldingType();
//            ValidationUtils.throwIf(
//                    metalId.equals(categoryId) && WeldingTypeEnum.NON_METAL.getValue().equals(weldingType),
//                    "项目代码属于金属焊接"
//            );
//            ValidationUtils.throwIf(
//                    nonmetalId.equals(categoryId) && WeldingTypeEnum.METAL.getValue().equals(weldingType),
//                    "项目代码属于非金属焊接"
//            );
//        }

        req.setOrgId(orgDTO.getId());
        req.setProjectName(req.getProjectCode());
        return super.add(req);
    }

    /**
     * 重写page
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<WeldingExamApplicationResp> page(WeldingExamApplicationQuery query, PageQuery pageQuery) {
        QueryWrapper<WeldingExamApplicationDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("twea.is_deleted", 0);
        // 机构查询
        if (query.getIsOrgQuery()) {
            // 获取机构信息
            UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
            OrgDTO orgDTO = orgMapper.getOrgId(userTokenDo.getUserId());
            queryWrapper.eq("twea.org_id", orgDTO.getId());
        } else {
            queryWrapper.eq("twea.status", WeldingExamApplicationStatusEnum.WAIT_REVIEW.getValue());
        }

        super.sort(queryWrapper, pageQuery);
        IPage<WeldingExamApplicationDetailResp> page = baseMapper.orgAndAdminPage(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
        PageResp<WeldingExamApplicationResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 审核
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean review(ReviewWeldingExamApplicationReq req) {
        List<Long> reviewIds = req.getReviewIds();

        // 1. 查询审核记录
        List<WeldingExamApplicationDO> weldingExamApplicationDOS = baseMapper.selectByIds(reviewIds);
        ValidationUtils.throwIfEmpty(weldingExamApplicationDOS, "所选审核记录已不存在或已被处理");
        ValidationUtils.throwIf(
                weldingExamApplicationDOS.stream()
                        .anyMatch(item -> !WeldingExamApplicationStatusEnum.WAIT_REVIEW.getValue().equals(item.getStatus())),
                "选中的记录中包含非未审核状态的数据，请重新选择"
        );

        Integer auditStatus = req.getAuditStatus();

        // 2. 审核通过逻辑
//        if (WeldingExamApplicationStatusEnum.PASS_REVIEW.getValue().equals(auditStatus)) {
//
//            // 收集项目编码、焊接类型和机构列表
//            Map<String, Integer> projectCodeTypeMap = new HashMap<>();
//            Set<Long> metalOrgSet = new HashSet<>();
//            Set<Long> nonMetalOrgSet = new HashSet<>();
//
//            // 本次提交内部校验：相同项目编码不能对应不同焊接类型
//            for (WeldingExamApplicationDO item : weldingExamApplicationDOS) {
//                String code = item.getProjectCode();
//                Integer type = item.getWeldingType();
//
//                if (projectCodeTypeMap.containsKey(code)) {
//                    Integer existType = projectCodeTypeMap.get(code);
//                    if (!existType.equals(type)) {
//                        ValidationUtils.throwIf(Boolean.TRUE,"本次提交审核中，项目编码【" + code + "】存在不同焊接类型");
//                    }
//                } else {
//                    projectCodeTypeMap.put(code, type);
//                }
//
//                if (WeldingTypeEnum.METAL.getValue().equals(type)) {
//                    metalOrgSet.add(item.getOrgId());
//                } else if (WeldingTypeEnum.NON_METAL.getValue().equals(type)) {
//                    nonMetalOrgSet.add(item.getOrgId());
//                }
//            }
//
//            // 3. 构造新项目列表
//            List<ProjectDO> projectList = projectCodeTypeMap.entrySet().stream()
//                    .map(entry -> {
//                        String code = entry.getKey();
//                        Integer type = entry.getValue();
//                        ProjectDO projectDO = new ProjectDO();
//                        projectDO.setProjectCode(code);
//                        projectDO.setProjectName(code);
//                        projectDO.setProjectStatus(ProjectEnum.MAINTENANCE.getValue().longValue());
//                        projectDO.setExamFee(ProjectConstant.DEFAULT_EXAM_FEE);
//                        projectDO.setExamDuration(ProjectConstant.DEFAULT_EXAM_DURATION);
//                        projectDO.setProjectType(ExamPlanTypeEnum.WORKER.getValue());
//                        projectDO.setIsOperation(ProjectHasExamTypeEnum.YES.getValue());
//                        projectDO.setCategoryId(WeldingTypeEnum.METAL.getValue().equals(type) ?
//                                metalId : nonmetalId);
//                        projectDO.setDeptId(ProjectConstant.DEFAULT_DEPT_ID);
//                        return projectDO;
//                    }).toList();
//
//            // 4. 查询数据库已有项目并校验类型
//            List<ProjectDO> existingProjects = projectMapper.selectByCodes(
//                    projectList.stream().map(ProjectDO::getProjectCode).collect(Collectors.toSet())
//            );
//            Map<String, Long> existingProjectCodeCategoryMap = existingProjects.stream()
//                    .collect(Collectors.toMap(ProjectDO::getProjectCode, ProjectDO::getCategoryId));
//
//            for (ProjectDO project : projectList) {
//                String code = project.getProjectCode();
//                Long expectedCategoryId = project.getCategoryId();
//
//                if (existingProjectCodeCategoryMap.containsKey(code)) {
//                    Long existCategoryId = existingProjectCodeCategoryMap.get(code);
//                    if (!existCategoryId.equals(expectedCategoryId)) {
//                        ValidationUtils.throwIf(Boolean.TRUE,"项目编码【" + code + "】的焊接类型与已有项目不一致");
//                    }
//                }
//            }
//
//            // 5. 批量插入不存在的项目
//            List<ProjectDO> newProjects = projectList.stream()
//                    .filter(p -> !existingProjectCodeCategoryMap.containsKey(p.getProjectCode()))
//                    .toList();
//            if (!newProjects.isEmpty()) {
//                projectMapper.insertBatch(newProjects);
//            }
//
//            // 6. 更新机构类别关系
//            updateOrgCategoryRelations(metalOrgSet, metalId);
//            updateOrgCategoryRelations(nonMetalOrgSet, nonmetalId);
//        }

        // 7. 更新审核状态
        List<WeldingExamApplicationDO> updateList = weldingExamApplicationDOS.stream()
                .map(item -> {
                    WeldingExamApplicationDO updateDO = new WeldingExamApplicationDO();
                    updateDO.setId(item.getId());
                    updateDO.setStatus(auditStatus);
                    updateDO.setReviewComment(req.getReviewComment());
                    updateDO.setReviewedAt(LocalDateTime.now());
                    return updateDO;
                }).toList();

        baseMapper.updateBatchById(updateList);

        return Boolean.TRUE;
    }


    /**
     * 更新机构类别关系（先去重再批量插入）
     */
    private void updateOrgCategoryRelations(Set<Long> orgIds, Long categoryId) {
        if (orgIds.isEmpty()) return;

        // 查询已存在的关系
        List<Long> existingOrgIds = orgCategoryRelationMapper.selectList(new LambdaQueryWrapper<OrgCategoryRelationDO>()
                        .in(OrgCategoryRelationDO::getOrgId, orgIds)
                        .eq(OrgCategoryRelationDO::getCategoryId, categoryId))
                .stream()
                .map(OrgCategoryRelationDO::getOrgId)
                .toList();

        // 去掉已存在的
        Set<Long> toInsert = orgIds.stream()
                .filter(id -> !existingOrgIds.contains(id))
                .collect(Collectors.toSet());

        if (toInsert.isEmpty()) return;

        // 批量新增
        List<OrgCategoryRelationDO> newRelations = toInsert.stream()
                .map(orgId -> {
                    OrgCategoryRelationDO relation = new OrgCategoryRelationDO();
                    relation.setOrgId(orgId);
                    relation.setCategoryId(categoryId);
                    return relation;
                }).toList();

        orgCategoryRelationMapper.insertBatch(newRelations);
    }


}