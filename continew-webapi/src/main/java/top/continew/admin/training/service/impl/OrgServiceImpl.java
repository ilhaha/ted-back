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

import cn.crane4j.core.util.StringUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.excel.support.cglib.beans.BeanMap;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import me.zhyd.oauth.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.req.user.UserOrgDTO;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.training.mapper.*;
import top.continew.admin.training.model.dto.OrgDTO;
import top.continew.admin.training.model.entity.*;
import top.continew.admin.training.model.req.OrgApplyPreReq;
import top.continew.admin.training.model.resp.OrgCandidatesResp;
import top.continew.admin.training.model.vo.*;
import top.continew.admin.util.InMemoryMultipartFile;
import top.continew.admin.util.RedisUtil;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.model.query.OrgQuery;
import top.continew.admin.training.model.req.OrgReq;
import top.continew.admin.training.model.resp.OrgDetailResp;
import top.continew.admin.training.model.resp.OrgResp;
import top.continew.admin.training.service.OrgService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 机构信息业务实现
 *
 * @author Anton
 * @since 2025/04/07 10:53
 */
@Service
@RequiredArgsConstructor
public class OrgServiceImpl extends BaseServiceImpl<OrgMapper, OrgDO, OrgResp, OrgDetailResp, OrgQuery, OrgReq> implements OrgService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private OrgMapper orgMapper;

    @Resource
    private OrgCategoryRelationMapper orgCategoryRelMapper;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private OrgClassMapper orgClassMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrgCandidateMapper orgCandidateMapper;

    @Value("${examine.userRole.organizationId}")
    private Long organizationId;

    @Value("${examine.userRole.candidatesId}")
    private Long candidatesId;

    @Value("${examine.deptId.examCenterId}")
    private Long examCenterId;

    @Resource
    private OrgUserMapper orgUserMapper;

    @Value("${qrcode.url}")
    private String qrcodeUrl;

    @Resource
    private UploadService uploadService;

    @Resource
    private EnrollPreMapper enrollPreMapper;

    private static final long EXPIRE_TIME = 7;  // 过期时间（天）

    @Override
    public PageResp<OrgResp> page(OrgQuery query, PageQuery pageQuery) {
        PageResp<OrgResp> page = super.page(query, pageQuery);
        List<OrgResp> list = page.getList();

        if (list != null && !list.isEmpty()) {
            // 1 提取 orgIds
            List<Long> orgIds = list.stream().map(OrgResp::getId).toList();

            // 2查询机构分类信息
            List<Map<String, Object>> categoryRows = orgCategoryRelMapper.listCategoryInfoByOrgIds(orgIds);
            Map<Long, String> categoryMap = categoryRows.stream()
                    .collect(Collectors.groupingBy(
                            r -> ((Number) r.get("org_id")).longValue(),
                            Collectors.mapping(
                                    r -> (String) r.get("name"),
                                    Collectors.joining("、")
                            )
                    ));

            // 3 查询机构账号信息（每个机构一个账号）
            List<Map<String, Object>> accountRows = orgUserMapper.listAccountNamesByOrgIds(orgIds);

            Map<Long, String> accountMap = accountRows.stream()
                    .collect(Collectors.toMap(
                            r -> ((Number) r.get("org_id")).longValue(),
                            r -> {
                                String nickname = (String) r.get("nickname");
                                String username = (String) r.get("username");
                                String decryptedUsername = aesWithHMAC.verifyAndDecrypt(username);
                                return nickname + " [ " + decryptedUsername + " ] ";
                            },
                            (v1, v2) -> v1
                    ));

            // 4 设置分类名和账号名
            list.forEach(org -> {
                org.setCategoryNames(categoryMap.getOrDefault(org.getId(), ""));
                org.setAccountName(accountMap.getOrDefault(org.getId(), ""));
            });
        }

        return page;
    }


    @Override
    public OrgDetailResp get(Long id) {
        OrgDetailResp orgDetailResp = super.get(id);
        //获取机构下的考生信息（有可能为空）
//        List<String> studentInfo = orgMapper.getStudentInfo(id);

//        ValidationUtils.throwIfNull(studentInfo, "机构信息不存在");
//        orgDetailResp.setCandidateName(studentInfo);

        // 获取八大类id
        List<OrgCategoryRelationDO> orgCategoryRelationDOS = orgCategoryRelMapper.selectList(new LambdaQueryWrapper<OrgCategoryRelationDO>().eq(OrgCategoryRelationDO::getOrgId, id));
        orgDetailResp.setCategoryIds(orgCategoryRelationDOS.stream().map(OrgCategoryRelationDO::getCategoryId).collect(Collectors.toList()));
//
//        List<String> categoryNames = orgCategoryRelMapper.listCategoryNamesByOrgId(id);
//        orgDetailResp.setCategoryNames(categoryNames == null ? "" : String.join(",", categoryNames));

        return orgDetailResp;
    }

    @Override
    @Transactional // 添加事务保证原子性
    public Long add(OrgReq req) {
        // 社会代号、机构名称、机构信用代码不可重复
        List<OrgDO> orgDOList = baseMapper.selectList(new LambdaQueryWrapper<OrgDO>().eq(OrgDO::getName, req.getName())
                .or()
                .eq(OrgDO::getCode, req.getCode())
                .or()
                .eq(OrgDO::getSocialCode, req.getSocialCode()));
        ValidationUtils.throwIfNotEmpty(orgDOList, "机构代号、机构名称、机构信用代码已存在");
        Long orgId = super.add(req);
        List<Long> categoryIds = req.getCategoryIds();

        if (CollectionUtil.isNotEmpty(categoryIds)) {
            List<OrgCategoryRelationDO> relations = categoryIds.stream()
                    .map(categoryId -> {
                        OrgCategoryRelationDO relation = new OrgCategoryRelationDO();
                        relation.setOrgId(orgId);
                        relation.setCategoryId(categoryId);
                        relation.setCreateUser(TokenLocalThreadUtil.get().getUserId());
                        relation.setIsDeleted(false); // 设置未删除状态
                        return relation;
                    })
                    .collect(Collectors.toList());

            // 检查插入结果
            orgCategoryRelMapper.insertBatch(relations);
        }

        redisTemplate.delete(RedisConstant.EXAM_ORGANIZATION_QUERY);
        return orgId;
    }

    @Override
    @Transactional // 确保事务性
    public void update(OrgReq req, Long id) {
        List<OrgDO> orgDOList = baseMapper.selectList(new LambdaQueryWrapper<OrgDO>()
                .ne(OrgDO::getId, id)
                .and(new Consumer<LambdaQueryWrapper<OrgDO>>() {
                    @Override
                    public void accept(LambdaQueryWrapper<OrgDO> orgDOLambdaQueryWrapper) {
                        orgDOLambdaQueryWrapper.eq(OrgDO::getName, req.getName())
                                .or()
                                .eq(OrgDO::getCode, req.getCode())
                                .or()
                                .eq(OrgDO::getSocialCode, req.getSocialCode());
                    }
                }));
        ValidationUtils.throwIfNotEmpty(orgDOList, "机构代号、机构名称、机构信用代码已存在");

        super.update(req, id);
        List<Long> newCategoryIds = req.getCategoryIds();
        if (CollectionUtil.isNotEmpty(newCategoryIds)) {
            orgCategoryRelMapper.deleteByOrgId(id);

            // 插入新的关系
            List<OrgCategoryRelationDO> relations = newCategoryIds.stream()
                    .map(categoryId -> {
                        OrgCategoryRelationDO relation = new OrgCategoryRelationDO();
                        relation.setOrgId(id);
                        relation.setCategoryId(categoryId);
                        relation.setCreateUser(TokenLocalThreadUtil.get().getUserId());
                        relation.setIsDeleted(false); // 设置未删除状态
                        return relation;
                    })
                    .collect(Collectors.toList());
            orgCategoryRelMapper.insertBatch(relations);
        }

        // 清理缓存
        redisTemplate.delete(RedisConstant.EXAM_ORGANIZATION_QUERY);
    }

    @Override
    public void delete(List<Long> ids) {
        // 删除主表数据
        this.removeByIds(ids);

        orgCategoryRelMapper.delete(
                new LambdaQueryWrapper<OrgCategoryRelationDO>()
                        .in(OrgCategoryRelationDO::getOrgId, ids)
        );

        orgUserMapper.delete(new LambdaQueryWrapper<TedOrgUser>().in(TedOrgUser::getOrgId, ids));

        redisTemplate.delete(RedisConstant.EXAM_ORGANIZATION_QUERY);
    }

    /**
     * 机构注册学员 非批量注册
     *
     * @param userDTO;
     */
    @Override
    public void orgSignUp(UserOrgDTO userDTO) {
        Long orgId = orgMapper.getOrgId(TokenLocalThreadUtil.get().getUserId()).getId();//获取机构id
        Long candidateId = orgMapper.orgSignUp(userDTO);//获取考生id
        orgMapper.linkCandidateWithOrg(orgId, candidateId);//添加到关联表
        //添加到关联表
    }

    @Override
    //填充机构id字段和默认密码字段 数据处理
    public List<UserOrgDTO> processUserCredentials(List<UserOrgDTO> userDTOList) {
        // 1. 前置校验强化
        final Long userId = Optional.ofNullable(TokenLocalThreadUtil.get())
                .orElseThrow(() -> new AuthException("用户未登录"))
                .getUserId();

        OrgDTO orgInfo = orgMapper.getOrgId(userId);
        if (orgInfo == null) {
            throw new BusinessException("机构信息不存在");
        }

        if (CollectionUtils.isEmpty(userDTOList)) {
            log.warn("空数据处理请求");
            return Collections.emptyList(); // 返回空集合而非null
        }

        // 2. 并行流处理提升效率
        return userDTOList.parallelStream()
                .filter(Objects::nonNull) // 过滤空对象
                .peek(userDTO -> {
                    try {
                        // 3. 密码生成逻辑修正
                        String username = validateUsername(userDTO.getUsername());
                        String rawPassword = generatePassword(orgInfo.getCode(), username);
                        validatePhone(userDTO.getPhone());
                        // 4. 加密存储
                        userDTO.setPassword(rawPassword);
                        userDTO.setOrgId(orgInfo.getId());
                        userDTO.setDeptId(examCenterId);
                        userDTO.setRoleId(candidatesId);
                    } catch (Exception e) {
                        log.warn("用户数据校验失败 ");
                        throw e; // 触发异常处理
                    }
                })
                .collect(Collectors.toList());
    }

    // 密码生成工具方法
    private String generatePassword(String orgCode, String username) {
        //todo 添加校验
        return orgCode.trim() + username.substring(username.length() - 6);
    }

    // 用户名标准化
    private String validateUsername(String username) {
        ValidationUtils.throwIfBlank(username, "用户名不能为空");
        ValidationUtils.throwIf(!username
                .matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$"), "身份证格式错误");
        return username.trim();
    }

    private void validatePhone(String phone) {
        ValidationUtils.throwIfBlank(phone, "手机号不能为空");
        ValidationUtils.throwIf(!phone.matches("^1[3-9]\\d{9}$"), "手机号格式错误");
    }

    //批量存入redis
    @Override
    public void batchSaveRedis(List<UserOrgDTO> userDTOList) {
        if (CollectionUtil.isEmpty(userDTOList)) {
            log.warn("数据为空");
            return;
        }
        try {
            // 遍历用户列表
            for (UserOrgDTO userDTO : userDTOList) {
                if (StringUtils.isBlank(userDTO.getUsername())) {
                    log.warn("用户名为空，跳过该条数据");
                    continue;
                }

                // 构建 Redis key
                String redisKey = RedisConstant.EXAM_STUDENTS_REGISTER + userDTO.getUsername();

                // 使用 BeanMap 将对象转换为 Map，排除 username 字段
                Map<String, String> userMap = new HashMap<>();
                BeanMap beanMap = BeanMap.create(userDTO);
                beanMap.forEach((key, value) -> {
                    if (value != null && !"username".equals(key)) {
                        userMap.put(key.toString(), value.toString());
                    }
                });

                // 将数据存入 Redis，使用 Hash 结构
                stringRedisTemplate.opsForHash().putAll(redisKey, userMap);

                // 设置过期时间
                stringRedisTemplate.expire(redisKey, EXPIRE_TIME, TimeUnit.DAYS);
            }
            log.warn("批量存入Redis成功");
        } catch (Exception e) {
            log.error("批量存入Redis失败", e);
            throw new RuntimeException("批量存入Redis失败: " + e.getMessage());
        }
    }

    //分页查询考生信息
    public PageResp<OrgCandidatesResp> getCandidateList(OrgQuery query, PageQuery pageQuery, String type) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long orgId = getOrgId(userTokenDo.getUserId());
        QueryWrapper<OrgDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("toc.org_id", orgId);
        queryWrapper.eq("toc.is_deleted", 0);
        if ("add".equals(type)) {
            queryWrapper.eq("toc.status", 1);
        } else {
            queryWrapper.eq("toc.status", 2);
        }
        super.sort(queryWrapper, pageQuery);
        IPage<OrgCandidatesResp> page = baseMapper.getCandidatesList(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
        PageResp<OrgCandidatesResp> pageResp = PageResp.build(page, OrgCandidatesResp.class);
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }


    @Override
    public PageResp<OrgResp> getAllOrgInfo(OrgQuery orgQuery, PageQuery pageQuery, String orgStatus) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        QueryWrapper<OrgDO> qw = this.buildQueryWrapper(orgQuery);
        qw.eq("o.is_deleted", 0);
        boolean bool = !"all".equals(orgStatus);
        if (bool) {
            qw.eq("c.candidate_id", userTokenDo.getUserId());
            qw.eq("c.is_deleted", 0);
            qw.eq("status", 2);
        }
        Page<OrgDO> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        // 执行分页查询(默认查找所有机构)
        IPage<OrgResp> iPage = baseMapper.getOrgList(page, qw, userTokenDo.getUserId(), bool);
        // 将查询结果转换成 PageResp 对象
        return PageResp.build(iPage, OrgResp.class);
    }


    @Override
    public OrgDetailResp getOrgDetail(Long orgId) {
        if (orgId == null)
            throw new BusinessException("请选择机构");
        // 调用orgMapper的getOrgDetail方法，传入项目id，获取机构详情
        OrgDetailResp orgDetailResp = orgMapper.getOrgDetail(orgId);

        // 返回所有考生名称
        // orgDetailResp.setCandidateName(orgMapper.getStudentInfo(orgId));
        return orgDetailResp;
    }

    // 考生查看考生机构表 status 和 remark
    @Override
    public AgencyStatusVO getAgencyStatus(Long orgId) {
        if (orgId == null)
            throw new BusinessException("请选择机构");

        // 调用 Mapper，获取包含 status 和 remark 的结果
        AgencyStatusVO agencyStatusVO = orgMapper.getAgencyStatus(orgId, TokenLocalThreadUtil.get().getUserId());

        // 无数据时，返回默认值（status=0，remark为空）
        if (agencyStatusVO == null) {
            agencyStatusVO = new AgencyStatusVO();
            agencyStatusVO.setStatus(0);
            agencyStatusVO.setRemark("");  // 或根据需求设为 null
        }

        return agencyStatusVO;  // 返回包含两个字段的结果
    }

    @Override
    public Integer studentAddAgency(Long orgId, Long projectId) {
        if (orgId == null)
            throw new BusinessException("请选择机构");
        Long userId = TokenLocalThreadUtil.get().getUserId();
        // 查找机构与考生关联状态等于1的数据
        Integer agencyStatus = orgMapper.findAgency(orgId, userId);
        if (agencyStatus > 0) {
            return -1;
        }
        // 查找机构与考生关联状态等于-1的数据
        AgencyStatusVO agencyStatusVO = orgMapper.getAgencyStatus(orgId, TokenLocalThreadUtil.get().getUserId());
        if (agencyStatusVO != null && agencyStatusVO.getStatus() != null && agencyStatusVO.getStatus() == -1) {
            OrgCandidateDO orgCandidateDO = new OrgCandidateDO();
            orgCandidateDO.setId(orgId);
            orgCandidateDO.setProjectId(projectId);
            orgCandidateDO.setId(agencyStatusVO.getId());
            orgCandidateDO.setStatus(1);
            return orgCandidateMapper.update(orgCandidateDO,new LambdaUpdateWrapper<OrgCandidateDO>().eq(OrgCandidateDO::getId,agencyStatusVO.getId())
                    .set(OrgCandidateDO::getRemark,null));
        }
        OrgCandidateDO orgCandidateDO = new OrgCandidateDO();
        orgCandidateDO.setOrgId(orgId);
        orgCandidateDO.setCandidateId(userId);
        orgCandidateDO.setProjectId(projectId);
        orgCandidateDO.setStatus(1);
        return orgCandidateMapper.insert(orgCandidateDO);
    }

    @Override
    public Integer studentDelAgency(Long orgId) {
        if (orgId == null)
            throw new BusinessException("请选择机构");
        Long userId = TokenLocalThreadUtil.get().getUserId();
        return orgMapper.studentDelAgency(orgId, userId);
    }

    @Override
    public Integer approveStudent(Long orgId, Long userId) {
        if (userId == null)
            throw new BusinessException("请选择考生");
        return orgMapper.approveStudent(orgId, userId);
    }

    @Override
    public Integer refuseStudent(Long orgId, Long userId) {
        if (userId == null)
            throw new BusinessException("请选择考生");
        return orgMapper.refuseStudent(orgId, userId);
    }

    @Override
    public Long getOrgId(Long userId) {
        return orgMapper.getOrgId(userId).getId();
    }

    @Override
    public OrgDetailResp getOrgInfo() {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDetailResp enrollInfoResp = baseMapper.selectByUserId(userTokenDo.getUserId());
        //输出en

        return enrollInfoResp;

    }

    @Override
    public List<String> getOrgAccounts(String id) {

        //获取机构下的账号信息（有可能为空）
        List<String> acountInfo = orgMapper.getAcountInfo(id);

        ValidationUtils.throwIfNull(acountInfo, "机构信息不存在");
        return acountInfo;
    }

    /**
     * 绑定用户到机构
     *
     * @param orgId
     * @param userId
     */
    @Override
    public void bindUserToOrg(String orgId, String userId) {
        //1.判断是否能绑定
        //1.1判断该用户是否已经被绑定
        Long org = orgUserMapper.selectByUserId(userId);
        ValidationUtils.throwIfNotNull(org, "该用户已经被绑定");
        //2.绑定
        TedOrgUser tedOrgUser = new TedOrgUser();
        tedOrgUser.setOrgId(Long.valueOf(orgId));
        tedOrgUser.setUserId(Long.valueOf(userId));
        tedOrgUser.setCreateUser(TokenLocalThreadUtil.get().getUserId());
        orgUserMapper.insert(tedOrgUser);
    }

    /**
     * 获取可绑定的用户列表
     *
     * @return
     */
    @Override
    public List<UserVO> getBindableUsers() {
        List<UserVO> userVOList = orgMapper.getBindableUsers(organizationId);
        if (!ObjectUtil.isEmpty(userVOList)) {
            userVOList.stream().map(item -> {
                item.setNickname(item.getNickname() + " [ " + aesWithHMAC.verifyAndDecrypt(item.getUsername()) + " ] ");
                return item;
            }).collect(Collectors.toList());
        }
        //ValidationUtils.throwIfNull(userVOList, "用户信息不存在");
        return userVOList;
    }

    /**
     * 解绑机构用户
     *
     * @param orgId
     * @return
     */
    @Override
    public Boolean unbindUserToOrg(Long orgId) {
        return orgUserMapper.delete(new LambdaQueryWrapper<TedOrgUser>().eq(TedOrgUser::getOrgId, orgId)) > 0;
    }

    /**
     * 获取机构对应的分类-项目级联选择
     *
     * @return
     */
    @Override
    public List<ProjectCategoryVO> getSelectCategoryProject(Long orgId) {
        List<ProjectCategoryVO> parentList = Collections.emptyList();
        if (ObjectUtil.isEmpty(orgId)) {
            // 获取当前登录的用户信息
            UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
            // 查询所有父级分类
            parentList = baseMapper.getSelectCategoryProject(userTokenDo.getUserId());
        } else {
            // 查询所有父级分类
            parentList = baseMapper.getSelectCategoryProjectByOrgId(orgId);
        }

        if (parentList.isEmpty()) {
            return parentList;
        }

        // 提取父级 ID
        List<Long> parentIds = parentList.stream()
                .map(ProjectCategoryVO::getValue)
                .toList();

        // 查询子级分类
        List<ProjectCategoryVO> childrenList = projectMapper.getSelectCategoryProject(parentIds);

        // 构建父子关系
        for (ProjectCategoryVO parent : parentList) {
            List<ProjectCategoryVO> children = childrenList.stream()
                    .filter(child -> Objects.equals(child.getParentId(), parent.getValue()))
                    .collect(Collectors.toList());
            parent.setChildren(children);
        }
        return parentList;
    }

    /**
     * 删除机构与用户关联信息
     *
     * @param orgId 机构ID
     * @return Boolean 删除是否成功
     */
    @Override
    public Boolean deleteOrgUserRelation(Long orgId) {
        int deletedCount = orgUserMapper.delete(
                new LambdaQueryWrapper<TedOrgUser>()
                        .eq(TedOrgUser::getOrgId, orgId)
        );
        return deletedCount > 0;
    }

    /**
     * 删除机构及其关联的用户信息
     *
     * @param ids 机构ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOrgWithRelations(Long ids) {
        // 删除机构信息
        this.removeById(ids);

        // 删除机构与用户关联信息
        orgUserMapper.delete(
                new LambdaQueryWrapper<TedOrgUser>()
                        .eq(TedOrgUser::getOrgId, ids)
        );
        // 删除机构与分类关联信息
        orgCategoryRelMapper.delete(
                new LambdaQueryWrapper<OrgCategoryRelationDO>()
                        .eq(OrgCategoryRelationDO::getOrgId, ids)
        );

    }


    /**
     * 获取机构对应的分类-项目-班级级联选择
     *
     * @param orgId
     * @return
     */
    @Override
    public List<ProjectCategoryVO> getSelectCategoryProjectClass(Long orgId) {
        List<ProjectCategoryVO> parentList = Collections.emptyList();

        // 1 获取一级分类（机构对应的分类）
        if (ObjectUtil.isEmpty(orgId)) {
            UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
            parentList = baseMapper.getSelectCategoryProject(userTokenDo.getUserId());
        } else {
            parentList = baseMapper.getSelectCategoryProjectByOrgId(orgId);
        }

        if (parentList.isEmpty()) {
            return parentList;
        }

        // 2️ 获取所有父级ID（一级分类ID）
        List<Long> parentIds = parentList.stream()
                .map(ProjectCategoryVO::getValue)
                .toList();

        // 3️ 查询第二层（项目）
        List<ProjectCategoryVO> projectList = projectMapper.getSelectCategoryProject(parentIds);

        // 4️ 查询第三层（班级）—— 用项目ID查询
        if (!projectList.isEmpty()) {
            List<Long> projectIds = projectList.stream()
                    .map(ProjectCategoryVO::getValue)
                    .toList();

            // 调用专门的查询班级方法
            List<ProjectCategoryVO> classList = orgClassMapper.getSelectClassByProjectIds(projectIds);

            // 5️ 绑定第三层到第二层
            for (ProjectCategoryVO project : projectList) {
                List<ProjectCategoryVO> classes = classList.stream()
                        .filter(c -> Objects.equals(c.getParentId(), project.getValue()))
                        .collect(Collectors.toList());
                project.setChildren(classes);
            }
        }

        // 6️ 绑定第二层到第一层
        for (ProjectCategoryVO parent : parentList) {
            List<ProjectCategoryVO> children = projectList.stream()
                    .filter(child -> Objects.equals(child.getParentId(), parent.getValue()))
                    .collect(Collectors.toList());
            parent.setChildren(children);
        }

        return parentList;
    }

    /**
     * 获取机构对应的项目-班级级联选择
     *
     * @return
     */
    @Override
    public List<ProjectCategoryVO> getSelectProjectClass(Long orgId, Long projectId) {
        // 查询原始数据（项目 + 班级）
        List<OrgProjectClassVO> flatList = baseMapper.getSelectProjectClass(orgId, projectId);

        // 按项目分组
        Map<Long, ProjectCategoryVO> projectMap = new LinkedHashMap<>();

        for (OrgProjectClassVO item : flatList) {
            // 如果项目节点不存在，则新建
            projectMap.computeIfAbsent(item.getProjectId(), id -> {
                ProjectCategoryVO projectVO = new ProjectCategoryVO();
                projectVO.setValue(item.getProjectId());
                projectVO.setLabel(item.getProjectLabel());
                projectVO.setChildren(new ArrayList<>());
                return projectVO;
            });

            // 创建班级节点并挂在对应项目下
            ProjectCategoryVO classVO = new ProjectCategoryVO();
            classVO.setValue(item.getClassId());
            classVO.setLabel(item.getClassLabel());

            projectMap.get(item.getProjectId()).getChildren().add(classVO);
        }

        return new ArrayList<>(projectMap.values());
    }

    /**
     * 根据报考状态获取机构对应的项目-班级-考生级联选择 （预报名）
     *
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<ProjectCategoryVO> getSelectProjectClassCandidate(Long projectId) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDTO orgDTO = orgMapper.getOrgId(userTokenDo.getUserId());
        List<OrgProjectClassCandidateVO> flatList = baseMapper.getSelectProjectClassCandidate(orgDTO.getId(), projectId);

        // 组装层级结构
        Map<Long, ProjectCategoryVO> projectMap = new LinkedHashMap<>();

        for (OrgProjectClassCandidateVO item : flatList) {
            // 一级：项目
            ProjectCategoryVO project = projectMap.computeIfAbsent(item.getProjectId(), id -> {
                ProjectCategoryVO vo = new ProjectCategoryVO();
                vo.setValue(id);
                vo.setLabel(item.getProjectLabel());
                vo.setChildren(new ArrayList<>());
                return vo;
            });

            // 二级：班级
            ProjectCategoryVO clazz = project.getChildren().stream()
                    .filter(c -> c.getValue().equals(item.getClassId()))
                    .findFirst()
                    .orElseGet(() -> {
                        ProjectCategoryVO vo = new ProjectCategoryVO();
                        vo.setValue(item.getClassId());
                        vo.setLabel(item.getClassLabel());
                        vo.setChildren(new ArrayList<>());
                        project.getChildren().add(vo);
                        return vo;
                    });

            // 三级：学员
            if (item.getCandidateId() != null) {
                ProjectCategoryVO student = new ProjectCategoryVO();
                student.setValue(item.getCandidateId());
                student.setLabel(item.getNickname());
                clazz.getChildren().add(student);
            }
        }

        return new ArrayList<>(projectMap.values());
    }

    /**
     * 机构预报名
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean applyPre(OrgApplyPreReq orgApplyPreReq) {
        Long examPlanId = orgApplyPreReq.getExamPlanId();
        List<Long> candidateIds = orgApplyPreReq.getCandidateIds();

        List<EnrollPreDO> insertPreList = candidateIds.stream()
                .map(candidateId -> createEnrollPre(candidateId, examPlanId))
                .toList();

        return enrollPreMapper.insertBatch(insertPreList);
    }

    /**
     * 获取所有的机构作为选择器返回
     * @return
     */
    @Override
    public List<SelectOrgVO> getOrgSelect() {
        return baseMapper.getOrgSelect();
    }

    /**
     * 封装创建单个 EnrollPreDO 的逻辑
     */
    private EnrollPreDO createEnrollPre(Long candidateId, Long examPlanId) {
        EnrollPreDO preDO = new EnrollPreDO();
        preDO.setCandidateId(candidateId);
        preDO.setPlanId(examPlanId);
        preDO.setStatus(0);
        try {
            // 生成二维码 URL
            String qrContent = buildQrContent(candidateId, examPlanId);

            // 生成二维码图片并上传
            String qrUrl = generateAndUploadQr(candidateId, qrContent);
            // 设置二维码地址
            preDO.setUploadQrcode(qrUrl);
        } catch (Exception e) {
            throw new BusinessException("二维码生成失败，请稍后重试");
        }
        return preDO;
    }

    /**
     * 生成二维码内容
     */
    private String buildQrContent(Long candidateId, Long examPlanId) throws UnsupportedEncodingException {
        String encryptedCandidateId = URLEncoder.encode(aesWithHMAC.encryptAndSign(String.valueOf(candidateId)), StandardCharsets.UTF_8);
        String encryptedPlanId = URLEncoder.encode(aesWithHMAC.encryptAndSign(String.valueOf(examPlanId)), StandardCharsets.UTF_8);
        return qrcodeUrl + "?candidateId=" + encryptedCandidateId + "&planId=" + encryptedPlanId;
    }

    /**
     * 生成二维码并上传，返回 URL
     */
    private String generateAndUploadQr(Long candidateId, String qrContent) throws IOException {
        BufferedImage image = QrCodeUtil.generate(qrContent, 300, 300);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();

            MultipartFile file = new InMemoryMultipartFile(
                    "file",
                    candidateId + ".png",
                    "image/png",
                    bytes
            );

            GeneralFileReq fileReq = new GeneralFileReq();
            fileReq.setType("pic");

            FileInfoResp fileInfo = uploadService.upload(file, fileReq);
            return fileInfo.getUrl();
        }
    }

}