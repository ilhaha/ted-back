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

package top.continew.admin.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.continew.admin.common.constant.ErrorMessageConstant;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.constant.UserConstant;
import top.continew.admin.common.enums.LocationStatusEnum;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.model.resp.AddressInfoResp;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.document.mapper.DocumentTypeMapper;
import top.continew.admin.document.model.entity.DocumentTypeDO;
import top.continew.admin.document.model.resp.DocumentTypeResp;
import top.continew.admin.document.service.cache.DocumentTypeCache;
import top.continew.admin.exam.mapper.ExamLocationMapper;
import top.continew.admin.exam.mapper.ProjLocAssocMapper;
import top.continew.admin.exam.mapper.ProjectDocumentTypeMapper;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.req.ExamLocationReqStr;
import top.continew.admin.exam.model.vo.*;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.util.AddressUtil;
import top.continew.admin.util.RedisUtil;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.query.ProjectQuery;
import top.continew.admin.exam.model.req.ProjectReq;
import top.continew.admin.exam.service.ProjectService;

import java.util.*;

import static top.continew.admin.common.constant.DeptConstant.ADMIN_ID;
import static top.continew.admin.common.constant.DeptConstant.BEIJING_MARKET_SUPERVISION_AND_ADMINISTRATION_ID;

/**
 * 项目业务实现
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends BaseServiceImpl<ProjectMapper, ProjectDO, ProjectResp, ProjectDetailResp, ProjectQuery, ProjectReq> implements ProjectService {

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ExamLocationMapper examLocationMapper;
    @Resource
    private ProjLocAssocMapper projLocAssocMapper;

    @Resource
    private AddressUtil addressUtil;

    @Resource
    private DocumentTypeMapper documentTypeMapper;

    @Resource
    private ProjectDocumentTypeMapper projectDocumentTypeMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private DocumentTypeCache documentTypeCache;

    /**
     * 重写新增方法
     * 重写后新增的方法能够从当前用户数据中获取当前登录部门id
     * 不允许重复添加(确保名称+八大类唯一)
     *
     * @param req 创建参数
     * @return
     */
    @Override
    @Transactional
    public Long add(ProjectReq req) {

        // ① 校验项目编码唯一
        ValidationUtils.throwIf(baseMapper.selectCount(new QueryWrapper<ProjectDO>().eq("project_code", req
            .getProjectCode()).eq("is_deleted", 0)) > 0, "项目编码已存在");

        // ② 校验项目名称 + 八大类唯一（你原来的逻辑）
        QueryWrapper<ProjectDO> queryWrapper = new QueryWrapper<ProjectDO>().eq("project_name", req.getProjectName())
            .eq("category_id", req.getCategoryId())
            .eq("is_deleted", 0);

        ProjectDO projectDO = baseMapper.selectOne(queryWrapper);
        ValidationUtils.throwIfNotNull(projectDO, "项目名称已存在");

        // ③ Anton 原逻辑
        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        ValidationUtils.throwIfNull(userInfo, ErrorMessageConstant.USER_AUTHENTICATION_FAILED);

        req.setDeptId(userInfo.getDeptId());
        req.setProjectStatus(1L);

        redisUtil.delete(RedisConstant.EXAM_PROJECT_SELECT);

        return super.add(req);
    }

    /**
     * 获取当前部门下的所有项目信息
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return
     */
    @Override
    public PageResp<ProjectResp> page(ProjectQuery query, PageQuery pageQuery) {
        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        ValidationUtils.throwIfNull(userInfo, ErrorMessageConstant.USER_AUTHENTICATION_FAILED);
        // 需要查询创建者
        ArrayList<Long> createUserIds = new ArrayList<>();
        if (!ObjectUtil.isEmpty(query.getCreateUser())) {
            userMapper.selectList(new QueryWrapper<UserDO>().like("nickname", query.getCreateUser())).forEach(item -> {
                createUserIds.add(item.getId());
            });
            query.setCreateUser(null);
        }

        QueryWrapper<ProjectDO> queryWrapper = this.buildQueryWrapper(query);
        if (!createUserIds.isEmpty())
            queryWrapper.in("tp.create_user", createUserIds);
        // 不是超管只能查找当前部门以及父类部门的所有项目信息
        if (!UserConstant.ADMIN_USER_ID.equals(userInfo.getUserId()))
            queryWrapper.in("tp.dept_id", userInfo.getDeptId(), userInfo.getParentDeptId());

        queryWrapper.eq("tp.is_deleted", 0);

        super.sort(queryWrapper, pageQuery);

        IPage<ProjectDetailResp> page = baseMapper.selectProjectPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        PageResp<ProjectResp> pageResp = PageResp.build(page, super.getListClass());
        pageResp.getList().forEach(item -> {
            this.fill(item);
        });
        return pageResp;

    }

    @Override
    public String insertLocation(Long projectId, List<Long> localtionLists) {
        ProjectDO projectDO = projectMapper.selectById(projectId);
        ValidationUtils.throwIfNull(projectDO, "项目不存在");
        ValidationUtils.throwIf(localtionLists.size() == 0, "地点集合不能为空");

        for (Long localtionList : localtionLists) {
            //        判断localtion表中是否存在list中所有对应的地点id
            ExamLocationDO examLocationDO = examLocationMapper.selectById(localtionList);
            ValidationUtils.throwIfNull(examLocationDO, "地点不存在");

            ProjLocAssocDO projLocAssocDO = new ProjLocAssocDO();
            projLocAssocDO.setProjectId(projectId);
            projLocAssocDO.setLocationId(localtionList);
            projLocAssocMapper.insert(projLocAssocDO);
        }

        return "地点和项目关联表插入成功";
    }

    public ArrayList<Long> getLocationIdByProjectId(Long projectId) {
        ValidationUtils.throwIfNull(projectId, "项目ID不能为空");
        QueryWrapper<ProjLocAssocDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("location_id").eq("project_id", projectId);
        List<ProjLocAssocDO> projLocAssocDOS = projLocAssocMapper.selectList(queryWrapper);
        ArrayList<Long> idList = new ArrayList<>();
        for (ProjLocAssocDO projLocAssocDO : projLocAssocDOS) {
            idList.add(projLocAssocDO.getLocationId());
        }
        return idList;
    }

    public List<ExamLocationReqStr> getExamLocationByLoccationIds(List<Long> localtionLists) {
        if (localtionLists == null || localtionLists.size() == 0)
            return new ArrayList<ExamLocationReqStr>();
        List<ExamLocationDO> examLocationDOS = examLocationMapper.selectByIds(localtionLists);
        ArrayList<ExamLocationReqStr> examLocationReqStrs = new ArrayList<>();

        for (ExamLocationDO examLocationDO : examLocationDOS) {
            ExamLocationReqStr examLocationReqStr = new ExamLocationReqStr();
            examLocationReqStr.setId(examLocationDO.getId());
            examLocationReqStr.setLocationName(examLocationDO.getLocationName());
            examLocationReqStr.setDetailedAddress(examLocationDO.getDetailedAddress());
            examLocationReqStr.setOperationalStatus(examLocationDO.getOperationalStatus());

            AddressInfoResp ids = addressUtil.getIds(examLocationDO.getProvinceId(), examLocationDO
                .getCityId(), examLocationDO.getStreetId());

            examLocationReqStr.setCity(ids.getCityName());
            examLocationReqStr.setProvince(ids.getProvinceName());
            examLocationReqStr.setStreet(ids.getAreaName());

            examLocationReqStrs.add(examLocationReqStr);
        }
        return examLocationReqStrs;
    }

    @Override
    public List<ExamLocationReqStr> getLocation(Long projectId) {
        ArrayList<Long> idList = getLocationIdByProjectId(projectId);
        if (idList == null || idList.size() == 0)
            return new ArrayList<ExamLocationReqStr>();
        List<ExamLocationReqStr> examLocationReqStrs = getExamLocationByLoccationIds(idList);
        examLocationReqStrs.forEach(item -> {
            item.setOperationalStatusStr(LocationStatusEnum.getStatusStrByCode(item.getOperationalStatus()));
        });
        return examLocationReqStrs;
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        ValidationUtils.throwIf(ids.size() == 0, "项目ID不能为空");
        projectMapper.deleteByIds(ids);
        QueryWrapper<ProjLocAssocDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("project_id", ids);
        redisUtil.delete(RedisConstant.EXAM_PROJECT_SELECT);
        projLocAssocMapper.delete(queryWrapper);

    }

    @Override
    public List<ProjectVo> notBindLocation(Long id) {
        ValidationUtils.throwIfEmpty(id, "请求id为空");

        // 获取已经绑定了的地址id
        List<Long> locationIds = baseMapper.selectBindingLocationByIds(id);
        List<ExamLocationDO> lists = null;
        if (locationIds == null || locationIds.isEmpty()) {
            lists = examLocationMapper.selectList(null);
        } else {
            lists = examLocationMapper.selectList(new QueryWrapper<ExamLocationDO>().notIn("id", locationIds));
        }
        // 获取未绑定的地址信息
        ArrayList<ProjectVo> vos = new ArrayList<>();

        lists.forEach(item -> {
            ProjectVo vo = new ProjectVo();
            vo.setValue(item.getId());
            vo.setLabel(item.getLocationName());
            vos.add(vo);
        });

        return vos;
    }

    @Override
    public List<ProjectVo> getBindLocation(Long id) {
        ValidationUtils.throwIfEmpty(id, "请求id为空");

        // 获取已经绑定了的地址id
        List<Long> locationIds = baseMapper.selectBindingLocationByIds(id);
        List<ExamLocationDO> examLocationDOS = examLocationMapper.selectByIds(locationIds);
        ArrayList<ProjectVo> vos = new ArrayList<>();
        examLocationDOS.forEach(item -> {
            ProjectVo projectVo = new ProjectVo();
            projectVo.setValue(item.getId());
            projectVo.setLabel(item.getLocationName());
            vos.add(projectVo);
        });
        return vos;
    }

    /**
     * 项目解绑地址
     */
    @Override
    public void projectDelLocation(Long projectId, List<Long> locationIds) {
        if (projectId == null || locationIds == null || locationIds.isEmpty())
            return;

        projLocAssocMapper.delete(new QueryWrapper<ProjLocAssocDO>().eq("project_id", projectId)
            .in("location_id", locationIds));

    }

    @Override
    public ProjectDetailResp get(Long id) {
        //        return baseMapper.get(id);
        return super.get(id);
    }

    @Override
    public List<ProjectVo> notBindDocument(Long id) {
        ValidationUtils.throwIfEmpty(id, "请求id为空");

        // 获取已经绑定了的资料id
        List<Long> documentIds = baseMapper.selectBindingDocumentByIds(id);

        List<DocumentTypeDO> lists = null;
        // 有绑定与一个没有绑定
        if (documentIds == null || documentIds.isEmpty()) {
            lists = documentTypeMapper.selectList(null);
        } else {
            lists = documentTypeMapper.selectList(new QueryWrapper<DocumentTypeDO>().notIn("id", documentIds));
        }

        ArrayList<ProjectVo> vos = new ArrayList<>();
        lists.forEach(item -> {
            ProjectVo vo = new ProjectVo();
            vo.setValue(item.getId());
            vo.setLabel(item.getTypeName());
            vos.add(vo);
        });

        return vos;
    }

    @Override
    public String insertDocument(Long projectId, List<Long> documentList) {
        ValidationUtils.throwIfNull(projectId, "项目不存在");
        ValidationUtils.throwIf(documentList == null || documentList.size() == 0, "资料集合不能为空");

        // 将关联表所有数据取出
        List<ProjectDocumentTypeDO> dos = projectDocumentTypeMapper.selectAll(projectId);

        HashMap<Long, ProjectDocumentTypeDO> map = new HashMap<>();
        for (ProjectDocumentTypeDO aDo : dos)
            map.put(aDo.getDocumentTypeId(), aDo);

        for (Long id : documentList) {
            ProjectDocumentTypeDO projectDocumentTypeDO = map.get(id);
            if (projectDocumentTypeDO != null) {
                // 已删除
                if (projectDocumentTypeDO.getIsDeleted() == 1) {
                    projectDocumentTypeMapper.updateDelStatus(projectDocumentTypeDO.getId());
                }
                continue;
            }
            ProjectDocumentTypeDO entity = new ProjectDocumentTypeDO();
            entity.setProjectId(projectId);
            entity.setDocumentTypeId(id);
            projectDocumentTypeMapper.insert(entity);
        }

        return "地点和项目关联表插入成功";
    }

    @Override
    public List<DocumentTypeResp> getDocument(Long projectId) {
        ValidationUtils.throwIfNull(projectId, "项目不存在");
        // 获取所有项目关联到的资料id
        List<DocumentTypeResp> resps = baseMapper.getBindingDocumentByIds(projectId);
        return resps;
    }

    @Override
    public void projectDelDocument(Long projectId, List<Long> documentIds) {
        if (projectId == null || documentIds == null || documentIds.isEmpty())
            return;

        projectDocumentTypeMapper.delete(new QueryWrapper<ProjectDocumentTypeDO>().eq("project_id", projectId)
            .in("document_type_id", documentIds));
    }

    @Override
    public PageResp<ProjectResp> getAllProject(ProjectQuery query, PageQuery pageQuery) {
        QueryWrapper<ProjectDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tp.is_deleted", 0);
        queryWrapper.and(wrapper -> wrapper.eq("tp.project_status", 2).or().eq("tp.project_status", 1));
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询，获取 TrainingResp 类型的分页数据
        IPage<ProjectResp> page = baseMapper.getAllProject(new Page<ProjectDO>(pageQuery.getPage(), pageQuery
            .getSize()), // 创建分页对象，指定当前页和每页大小
            queryWrapper// 查询条件
        );
        // 将查询结果转换成 PageResp 对象，方便前端处理
        PageResp<ProjectResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        // 返回分页查询结果
        return pageResp;
    }

    @Override
    public PageResp<ProjectResp> getProjectByStatus(ProjectQuery query, PageQuery pageQuery, Long projectStatus) {
        QueryWrapper<ProjectDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tp.is_deleted", 0);
        queryWrapper.eq("tp.project_status", projectStatus);
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询，获取 TrainingResp 类型的分页数据
        IPage<ProjectResp> page = baseMapper.getAllProject(new Page<ProjectDO>(pageQuery.getPage(), pageQuery
            .getSize()), // 创建分页对象，指定当前页和每页大小
            queryWrapper// 查询条件
        );
        // 将查询结果转换成 PageResp 对象，方便前端处理
        PageResp<ProjectResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        // 返回分页查询结果
        return pageResp;
    }

    @Override
    public List<ProjectVo> getLocationSelect(Long projectId) {
        return examLocationMapper.getLocationSelect(projectId);
    }

    //    @Override
    //    public List<ProjectVo> getClassRoomSelect(Long projectId) {
    ////        return examLocationMapper.getClassRoomSelect(projectId);
    //    }
    @Override
    public List<Map<String, Object>> getClassRoomSelect(Long projectId) {
        List<Map<String, Object>> rows = examLocationMapper.selectClassroomList(projectId);

        Map<Long, Map<String, Object>> locationMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            Long locationId = ((Number)row.get("location_id")).longValue();
            String locationName = (String)row.get("location_name");

            // 一级节点（考点）
            locationMap.putIfAbsent(locationId, new HashMap<>() {
                {
                    put("value", locationId);
                    put("label", locationName);
                    put("children", new ArrayList<Map<String, Object>>());
                }
            });

            // 子节点（考场）
            List<Map<String, Object>> children = (List<Map<String, Object>>)locationMap.get(locationId).get("children");
            children.add(Map.of("value", row.get("value"), "label", row.get("label")));
        }
        return new ArrayList<>(locationMap.values());
    }

    @Override
    public List<ProjectCategoryTreeVo> getDeptProject(Integer planType) {

        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        ValidationUtils.throwIfNull(userInfo, ErrorMessageConstant.USER_AUTHENTICATION_FAILED);

        Long deptId = null;
        if (!UserConstant.ADMIN_USER_ID.equals(userInfo.getUserId())) {
            deptId = userInfo.getDeptId();
        }

        List<ProjectCategoryProjectFlatVo> flats = baseMapper.getDeptProjectTree(deptId, planType);

        Map<Long, ProjectCategoryTreeVo> categoryMap = new LinkedHashMap<>();

        for (ProjectCategoryProjectFlatVo flat : flats) {

            ProjectCategoryTreeVo categoryVo = categoryMap.computeIfAbsent(flat.getCategoryId(), id -> {
                ProjectCategoryTreeVo vo = new ProjectCategoryTreeVo();
                vo.setValue(flat.getCategoryId());
                vo.setLabel(flat.getCategoryName());
                vo.setChildren(new ArrayList<>());
                return vo;
            });

            // 判断项目是否存在
            if (flat.getProjectId() != null) {
                ProjectVo projectVo = new ProjectVo();
                projectVo.setValue(flat.getProjectId());
                projectVo.setLabel(flat.getProjectName());
                projectVo.setIsOperation(flat.getIsOperation());

                categoryVo.getChildren().add(projectVo);
            }
        }

        return new ArrayList<>(categoryMap.values());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProjectReq req, Long id) {
        // 1. 查询原数据
        ProjectDO oldProject = baseMapper.selectById(id);
        ValidationUtils.throwIfNull(oldProject, "项目不存在");

        // 2. 校验 projectCode 唯一（排除自身）
        ValidationUtils.throwIf(lambdaQuery().eq(ProjectDO::getProjectCode, req.getProjectCode())
            .eq(ProjectDO::getIsDeleted, 0)
            .ne(ProjectDO::getId, id)
            .exists(), "项目编码已存在");

        // 3. 校验 名称 + 分类 唯一（排除自身）
        ValidationUtils.throwIf(lambdaQuery().eq(ProjectDO::getProjectName, req.getProjectName())
            .eq(ProjectDO::getCategoryId, req.getCategoryId())
            .eq(ProjectDO::getIsDeleted, 0)
            .ne(ProjectDO::getId, id)
            .exists(), "项目名称已存在");

        // 4. 项目状态变更权限校验
        if (!Objects.equals(req.getProjectStatus(), oldProject.getProjectStatus())) {
            UserTokenDo userInfo = TokenLocalThreadUtil.get();
            ValidationUtils.throwIfNull(userInfo, ErrorMessageConstant.USER_AUTHENTICATION_FAILED);
            // TODO 权限校验
        }
        // 5. 清缓存
        redisUtil.delete(RedisConstant.EXAM_PROJECT_SELECT);

        // 6. 更新
        super.update(req, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void examine(ProjectReq req, Long projectId) {
        UserTokenDo userInfo = TokenLocalThreadUtil.get();
        ValidationUtils.throwIfNull(userInfo, ErrorMessageConstant.USER_AUTHENTICATION_FAILED);

        // 检查权限
        //        boolean hasPermission = checkPermission(userInfo);
        //        ValidationUtils.throwIf(!hasPermission, "权限不足，无法进行审核");

        // 原有的审核逻辑
        ProjectDO projectDO = baseMapper.selectById(projectId);
        projectDO.setProjectStatus(req.getProjectStatus());

        baseMapper.updateById(projectDO);
    }

    @Override
    public StudentProjectDetailResp getStudentProjectDetail(Long projectId) {
        StudentProjectDetailResp studentProjectDetailResp = projectMapper.getProjectDetail(projectId);
        studentProjectDetailResp.setDocumentList(projectMapper.getDocumentList(projectId));
        studentProjectDetailResp.setLocationList(projectMapper.getLocationList(projectId));
        return studentProjectDetailResp;
    }

    @Override
    /**
     * 获取项目下拉框（现在是项目id->项目名称===改计划id->项目名称）
     */
    public List<ProjectVo> selectOptions() {
        List<ProjectVo> vo = (List<ProjectVo>)redisUtil
            .getStringToClass(RedisConstant.EXAM_PROJECT_SELECT, ProjectVo.class);
        if (ObjectUtil.isEmpty(vo)) {
            vo = baseMapper.selectOptions();
            redisUtil.setString(RedisConstant.EXAM_PROJECT_SELECT, vo);
        }
        return vo;
    }

    /**
     * 查询所有有考场的考试项目
     * 
     * @return
     */
    @Override
    public List<ProjectWithClassroomVO> getProjectsWithClassrooms() {
        List<ProjectClassroomFlatVO> flatList = projectMapper.getProjectsWithClassrooms();

        // 第一层：按项目分组
        Map<Long, ProjectWithClassroomVO> projectMap = new LinkedHashMap<>();
        for (ProjectClassroomFlatVO flat : flatList) {
            // 1️ 获取或创建项目对象
            ProjectWithClassroomVO projectVO = projectMap.computeIfAbsent(flat.getProjectId(), id -> {
                ProjectWithClassroomVO vo = new ProjectWithClassroomVO();
                vo.setProjectId(flat.getProjectId());
                vo.setProjectName(flat.getProjectName());
                vo.setProjectCode(flat.getProjectCode());
                vo.setCategoryName(flat.getCategoryName());
                vo.setProjectType(flat.getProjectType());
                vo.setLocations(new ArrayList<>()); // 初始化地点列表
                return vo;
            });
            // 2️ 在该项目下查找地点
            List<LocationVO> locations = projectVO.getLocations();
            LocationVO locationVO = locations.stream()
                .filter(l -> Objects.equals(l.getLocationName(), flat.getLocationName()))
                .findFirst()
                .orElseGet(() -> {
                    LocationVO newLoc = new LocationVO();
                    newLoc.setLocationName(flat.getLocationName());
                    newLoc.setClassrooms(new ArrayList<>());
                    locations.add(newLoc);
                    return newLoc;
                });
            // 3️ 添加考场
            ClassroomVO classroomVO = new ClassroomVO();
            classroomVO.setClassroomId(flat.getClassroomId());
            classroomVO.setClassroomName(flat.getClassroomName());
            locationVO.getClassrooms().add(classroomVO);
        }
        return new ArrayList<>(projectMap.values());
    }

    /**
     * 机构获取所属全部项目
     * 
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ProjectResp> orgGetAllProject(ProjectQuery query, PageQuery pageQuery) {
        QueryWrapper<ProjectDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tp.is_deleted", 0);
        queryWrapper.and(wrapper -> wrapper.eq("tp.project_status", 2));
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询，获取 TrainingResp 类型的分页数据
        IPage<ProjectResp> page = baseMapper.orgGetAllProject(new Page<ProjectDO>(pageQuery.getPage(), pageQuery
            .getSize()), // 创建分页对象，指定当前页和每页大小
            queryWrapper,// 查询条件
            TokenLocalThreadUtil.get().getUserId());
        // 将查询结果转换成 PageResp 对象，方便前端处理
        PageResp<ProjectResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        // 返回分页查询结果
        return pageResp;
    }

    /**
     * 根据项目的考试人员类型和是否有实操考试获取地点-考场级联选择器
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getLocalClassroomChoose(Long projectId, Integer isOperation) {
        ProjectDO projectDO = baseMapper.selectById(projectId);
        ValidationUtils.throwIfNull(projectDO, "未查询到考试项目信息");
        List<LocationClassroomVO> list = baseMapper.getLocationClassroomList(projectDO.getProjectType(), isOperation);

        // 转成前端 cascader 需要的格式
        Map<Long, Map<String, Object>> map = new LinkedHashMap<>();

        for (LocationClassroomVO item : list) {
            map.putIfAbsent(item.getLocationId(), new LinkedHashMap<>() {
                {
                    put("label", item.getLocationName());
                    put("value", item.getLocationId());
                    put("selectable", false); // 地点不可选
                    put("children", new ArrayList<>());
                }
            });

            if (item.getClassroomId() != null) {
                ((List<Object>)map.get(item.getLocationId()).get("children")).add(new HashMap<String, Object>() {
                    {
                        put("label", item.getClassroomName());
                        put("value", item.getClassroomId());
                    }
                });
            }
        }

        return new ArrayList<>(map.values());
    }

    /**
     * 检查用户是否有审核权限
     * 1. 管理员有权限
     * 2. 北京市场监管局用户有权限
     * 3. 北京市场监管局下属部门用户有权限
     *
     * @param userInfo 用户信息
     * @return 是否有权限
     */
    private boolean checkPermission(UserTokenDo userInfo) {
        // 管理员直接有权限
        if (Objects.equals(userInfo.getUserId(), ADMIN_ID)) {
            return true;
        }

        // 检查当前部门是否为北京市场监管局
        if (Objects.equals(userInfo.getDeptId(), BEIJING_MARKET_SUPERVISION_AND_ADMINISTRATION_ID)) {
            return true;
        }

        // 检查父部门链中是否有北京市场监管局
        Long parentDeptId = userInfo.getParentDeptId();
        while (parentDeptId != null && !Objects
            .equals(parentDeptId, BEIJING_MARKET_SUPERVISION_AND_ADMINISTRATION_ID)) {
            if (Objects.equals(parentDeptId, BEIJING_MARKET_SUPERVISION_AND_ADMINISTRATION_ID)) {
                return true;
            }
            parentDeptId = baseMapper.getParentDeptId(parentDeptId);
        }

        return false;
    }
}