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
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.excel.support.cglib.beans.BeanMap;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import me.zhyd.oauth.exception.AuthException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.EnrollStatusConstant;
import top.continew.admin.common.constant.ImportWorkerTemplateConstant;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.constant.enums.ExamPlanStatusEnum;
import top.continew.admin.common.constant.enums.WorkerApplyReviewStatusEnum;
import top.continew.admin.common.constant.enums.WorkerApplyTypeEnum;
import top.continew.admin.common.constant.enums.WorkerPictureTypeEnum;
import top.continew.admin.common.model.dto.ExcelUploadFileResultDTO;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.entity.ClassroomDO;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.vo.ExamPlanVO;
import top.continew.admin.exam.service.ExamineePaymentAuditService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.req.user.UserOrgDTO;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.training.mapper.*;
import top.continew.admin.training.model.dto.OrgDTO;
import top.continew.admin.training.model.vo.ParsedExcelResultVO;
import top.continew.admin.training.model.vo.ParsedErrorVO;
import top.continew.admin.training.model.vo.ParsedSuccessVO;
import top.continew.admin.training.model.entity.*;
import top.continew.admin.training.model.req.OrgApplyReq;
import top.continew.admin.training.model.resp.OrgCandidatesResp;
import top.continew.admin.training.model.vo.*;
import top.continew.admin.util.ExcelMediaUtils;
import top.continew.admin.util.ExcelUtilReactive;
import top.continew.admin.util.InMemoryMultipartFile;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;
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
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Value("${qrcode.worker.upload.apply-doc.url}")
    private String qrcodeUrl;

    @Resource
    private UploadService uploadService;

    @Resource
    private EnrollPreMapper enrollPreMapper;

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private EnrollMapper enrollMapper;

    @Resource
    private WorkerApplyMapper workerApplyMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ExamineePaymentAuditService examineePaymentAuditService;


    @Resource
    private OrgTrainingPriceMapper orgTrainingPriceMapper;
    @Resource
    private OrgTrainingPaymentAuditMapper orgTrainingPaymentAuditMapper;

    @Resource
    private final ExcelUtilReactive excelUtilReactive;

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
        //获取机构id
        Long orgId = orgMapper.getOrgId(TokenLocalThreadUtil.get().getUserId()).getId();
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
        Long userId = TokenLocalThreadUtil.get().getUserId();

        // 调用 Mapper，获取包含 status 和 remark 的结果
        AgencyStatusVO agencyStatusVO = orgMapper.getAgencyStatus(orgId,userId,null);

        // 无数据时，返回默认值（status=0，remark为空）
        if (agencyStatusVO == null) {
            agencyStatusVO = new AgencyStatusVO();
            agencyStatusVO.setStatus(0);
            agencyStatusVO.setRemark("");  // 或根据需求设为 null
        }

        return agencyStatusVO;  // 返回包含两个字段的结果
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer studentAddAgency(Long orgId, Long projectId) {
        // 参数校验
        if (orgId == null) {
            throw new BusinessException("请选择机构");
        }
        if (projectId == null) {
            throw new BusinessException("请选择项目");
        }
        Long userId = TokenLocalThreadUtil.get().getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 查询机构-考生-项目关联状态
        AgencyStatusVO agencyStatusVO = orgMapper.getAgencyStatus(orgId, userId, projectId);
        if (agencyStatusVO != null) {
            Integer status = agencyStatusVO.getStatus();

            //  已存在有效关联
            if (status != null && status > 0) {
                return -1;
            }

            //  已存在无效关联，执行恢复
            if (status != null && status == -1) {
                OrgCandidateDO updateDO = new OrgCandidateDO();
                updateDO.setId(agencyStatusVO.getId());
                updateDO.setPaymentStatus(0);
                updateDO.setStatus(1);
                updateDO.setUpdateUser(userId);
                updateDO.setUpdateTime(LocalDateTime.now());
                updateDO.setRemark(null);

                int updateCount = orgCandidateMapper.update(updateDO,
                        new LambdaUpdateWrapper<OrgCandidateDO>().eq(OrgCandidateDO::getId, agencyStatusVO.getId()));

                // 更新成功后也插入培训缴费通知单
                if (updateCount > 0) {
                    OrgCandidateDO recovered = new OrgCandidateDO();
                    recovered.setId(agencyStatusVO.getId());
                    insertTrainingPaymentNotice(orgId, projectId, userId, recovered);
                }
                return updateCount;
            }
        }

        // 无关联记录，执行插入
        OrgCandidateDO insertDO = new OrgCandidateDO();
        insertDO.setOrgId(orgId);
        insertDO.setCandidateId(userId);
        insertDO.setProjectId(projectId);
        insertDO.setPaymentStatus(0);
        insertDO.setStatus(1);
        insertDO.setCreateUser(userId);
        insertDO.setCreateTime(LocalDateTime.now());
        insertDO.setUpdateTime(LocalDateTime.now());

        int insertResult = orgCandidateMapper.insert(insertDO);

        // 插入成功后生成培训缴费通知单
        if (insertResult > 0) {
            insertTrainingPaymentNotice(orgId, projectId, userId, insertDO);
            return Math.toIntExact(insertDO.getId());
        }

        return -2;
    }

    /**
     * 封装：插入培训缴费通知单
     */
    private void insertTrainingPaymentNotice(Long orgId, Long projectId, Long userId, OrgCandidateDO insertDO) {
        OrgTrainingPaymentAuditDO orgTrainingPaymentAuditDO = new OrgTrainingPaymentAuditDO();
        orgTrainingPaymentAuditDO.setOrgId(orgId);
        orgTrainingPaymentAuditDO.setCandidateId(userId);
        orgTrainingPaymentAuditDO.setProjectId(projectId);

        // 关联培训价格
        OrgTrainingPriceDO exist = orgTrainingPriceMapper.selectOne(
                new LambdaQueryWrapper<OrgTrainingPriceDO>()
                        .eq(OrgTrainingPriceDO::getOrgId, orgId)
                        .eq(OrgTrainingPriceDO::getProjectId, projectId)
                        .eq(OrgTrainingPriceDO::getIsDeleted, 0)
        );
        if (exist == null) {
            throw new BusinessException("未找到培训价格信息");
        }

        orgTrainingPaymentAuditDO.setTrainingId(exist.getId());
        orgTrainingPaymentAuditDO.setAuditStatus(0);
        orgTrainingPaymentAuditDO.setPaymentAmount(exist.getPrice());
        orgTrainingPaymentAuditDO.setEnrollId(insertDO.getId());

        // 生成通知单编号
        String prefix = String.valueOf(projectMapper.getProjectDetail(projectId).getProjectCode());
        orgTrainingPaymentAuditDO.setNoticeNo(excelUtilReactive.generateUniqueNoticeNo(prefix));

        // 插入缴费通知单
        orgTrainingPaymentAuditMapper.insert(orgTrainingPaymentAuditDO);
    }

    // 学生退出机构
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer studentQuitAgency(Long orgId) {
        if (orgId == null) {
            throw new BusinessException("请选择机构");
        }
        Long userId = TokenLocalThreadUtil.get().getUserId();

        // 查询该考生在当前机构下的所有未完成预报名记录（多个考试计划）
        LambdaQueryWrapper<EnrollPreDO> enrollPreQueryWrapper = new LambdaQueryWrapper<>();
        enrollPreQueryWrapper.eq(EnrollPreDO::getCandidateId, userId)
                .eq(EnrollPreDO::getOrgId, orgId)
                .eq(EnrollPreDO::getIsDeleted, false);
        List<EnrollPreDO> enrollPreList = enrollPreMapper.selectList(enrollPreQueryWrapper);

        // 检查所有预报名记录关联的考试计划
        if (!enrollPreList.isEmpty()) {
            for (EnrollPreDO enrollPre : enrollPreList) {
                // 查询当前预报名记录关联的考试计划
                LambdaQueryWrapper<ExamPlanDO> examPlanQueryWrapper = new LambdaQueryWrapper<>();
                examPlanQueryWrapper.eq(ExamPlanDO::getId, enrollPre.getPlanId())
                        .eq(ExamPlanDO::getIsDeleted, false);
                ExamPlanDO examPlan = examPlanMapper.selectOne(examPlanQueryWrapper);

                // 仅当“计划存在且未结束（status≠6）”时，才禁止退出
                if (examPlan != null && examPlan.getStatus() != 6) {
                    throw new BusinessException("存在通过该机构申请的未结束的考试计划申请或考试，无法退出机构");
                }
            }
        }

        // 退出机构班级
        int classAffectedRows = orgMapper.studentQuitAgencyClass(orgId, userId);
        if (classAffectedRows <= 0) {
            throw new BusinessException("退出班级失败");
        }

        // 退出机构
        int orgAffectedRows = orgMapper.studentQuitAgency(orgId, userId);
        if (orgAffectedRows <= 0) {
            throw new BusinessException("退出机构失败");
        }
        return orgAffectedRows;
    }

    // 机构移除学生
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer agencyRemoveStudent(Long orgId, Long candidateId) {
        if (orgId == null) {
            throw new BusinessException("机构ID不能为空");
        }
        if (candidateId == null) {
            throw new BusinessException("学生ID不能为空");
        }

        // 检查学生在该机构下是否存在未结束的考试计划
        LambdaQueryWrapper<EnrollPreDO> enrollPreQueryWrapper = new LambdaQueryWrapper<>();
        enrollPreQueryWrapper.eq(EnrollPreDO::getCandidateId, candidateId)
                .eq(EnrollPreDO::getOrgId, orgId)
                .eq(EnrollPreDO::getIsDeleted, false);
        List<EnrollPreDO> enrollPreList = enrollPreMapper.selectList(enrollPreQueryWrapper);

        if (!enrollPreList.isEmpty()) {
            for (EnrollPreDO enrollPre : enrollPreList) {
                LambdaQueryWrapper<ExamPlanDO> examPlanQueryWrapper = new LambdaQueryWrapper<>();
                examPlanQueryWrapper.eq(ExamPlanDO::getId, enrollPre.getPlanId())
                        .eq(ExamPlanDO::getIsDeleted, false);
                ExamPlanDO examPlan = examPlanMapper.selectOne(examPlanQueryWrapper);

                // 如果考试计划未结束，则禁止移除
                if (examPlan != null && examPlan.getStatus() != 6) {
                    throw new BusinessException("该学生存在未结束的考试计划，无法从机构中移除");
                }
            }
        }

        // 退出机构班级
        int classAffectedRows = orgMapper.studentQuitAgencyClass(orgId, candidateId);
        if (classAffectedRows < 0) {
            throw new BusinessException("移除学生班级失败");
        }

        // 退出机构
        int orgAffectedRows = orgMapper.studentQuitAgency(orgId, candidateId);
        if (orgAffectedRows <= 0) {
            throw new BusinessException("移除学生失败");
        }

        return orgAffectedRows;
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
    public List<ProjectCategoryVO> getSelectProjectClassCandidate(Long projectId,Integer planType,Long planId) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDTO orgDTO = orgMapper.getOrgId(userTokenDo.getUserId());
        List<OrgProjectClassCandidateVO> flatList = baseMapper.getSelectProjectClassCandidate(orgDTO.getId(), projectId,planType,planId);

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
     * 机构给作业人员报名
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean apply(OrgApplyReq orgApplyPreReq) {
        Long examPlanId = orgApplyPreReq.getExamPlanId();
        ExamPlanDO examPlanDO = examPlanMapper.selectById(examPlanId);
        ValidationUtils.throwIf(Objects.isNull(examPlanDO), "考试计划不存在");

        // 报名时间校验
        LocalDateTime enrollEndTime = examPlanDO.getEnrollEndTime();
        ValidationUtils.throwIf(
                !ExamPlanStatusEnum.IN_FORCE.getValue().equals(examPlanDO.getStatus())
                        || LocalDateTime.now().isAfter(enrollEndTime),
                "报名时间已截至，无法继续报名"
        );

        // 空检查
        List<List<Long>> projectClassCandidateList = orgApplyPreReq.getCandidateIds();
        ValidationUtils.throwIfEmpty(projectClassCandidateList,"未选择报考考生！");

        Set<Long> seen = new HashSet<>();
        for (int i = 0; i < projectClassCandidateList.size(); i++) {
            List<Long> innerList = projectClassCandidateList.get(i);
            ValidationUtils.throwIf(innerList.size() < 2,"未选择报考考生！");
            Long value = innerList.get(2);
            UserDO userDO = userMapper.selectById(value);
            ValidationUtils.throwIf(!seen.add(value),userDO.getNickname()+ "存在与两个班级，只能由一个班级进行报考");
        }

        // 计算最大人数
        ExamPlanVO examPlanVO = new ExamPlanVO();
        BeanUtils.copyProperties(examPlanDO, examPlanVO);
        examPlanVO.setClassroomList(examPlanMapper.getPlanExamClassroom(examPlanId));

        int maxNumber = 0;
        List<ClassroomDO> classroomDOS = classroomMapper.selectList(
                new LambdaQueryWrapper<ClassroomDO>().in(ClassroomDO::getId, examPlanVO.getClassroomList())
        );
        for (ClassroomDO classroomDO : classroomDOS) {
            maxNumber += classroomDO.getMaxCandidates();
        }

        Long actualCount = enrollMapper.getEnrollCount(examPlanId);
        ValidationUtils.throwIf(
                maxNumber - actualCount - projectClassCandidateList.size() < 0,
                "报名人数大于考试计划剩余报名名额"
        );


        // 检查是否重复报名
        List<Long> candidateIds = projectClassCandidateList.stream().map(item -> {
            return item.get(2);
        }).toList();

        List<Long> existedCandidateIds = enrollMapper.selectList(
                new LambdaQueryWrapper<EnrollDO>()
                        .eq(EnrollDO::getExamPlanId, examPlanId)
                        .in(EnrollDO::getUserId, candidateIds)
        ).stream().map(EnrollDO::getUserId).toList();

        if (CollUtil.isNotEmpty(existedCandidateIds)) {
            String existedNames = String.join("、",
                    userMapper.selectByIds(existedCandidateIds)
                            .stream().map(UserDO::getNickname).toList());
            throw new BusinessException("以下考生已报名该考试计划：" + existedNames);
        }

        // 插入报名表
        List<EnrollDO> insertEnrollList = projectClassCandidateList.stream().map(item -> {
            EnrollDO enrollDO = new EnrollDO();
            enrollDO.setExamPlanId(examPlanId);
            enrollDO.setClassId(item.get(1));
            enrollDO.setUserId(item.get(2));
            enrollDO.setEnrollStatus(EnrollStatusConstant.SIGNED_UP);
            return enrollDO;
        }).toList();
        enrollMapper.insertBatch(insertEnrollList);

        // 生成通知单
        examineePaymentAuditService.generatePaymentAudit(insertEnrollList);

        return true;
    }



    /**
     * 获取所有的机构作为选择器返回
     *
     * @return
     */
    @Override
    public List<SelectOrgVO> getOrgSelect() {
        return baseMapper.getOrgSelect();
    }

    /**
     * 根据班级类型获取机构对应的项目-班级级联选择
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getSelectProjectClassByType(Integer type) {
        List<OrgProjectClassTypeVO> list = baseMapper.getSelectProjectClassByType(type);
        Map<Long, Map<String, Object>> orgMap = new LinkedHashMap<>();
        for (OrgProjectClassTypeVO row : list) {
            Map<String, Object> orgNode = orgMap.computeIfAbsent(row.getOrgId(), k -> {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("value", row.getOrgId());
                node.put("label", row.getOrgName());
                node.put("children", new LinkedHashMap<Long, Map<String, Object>>());
                return node;
            });

            Map<Long, Map<String, Object>> projectMap =
                    (Map<Long, Map<String, Object>>) orgNode.get("children");

            Map<String, Object> projectNode = projectMap.computeIfAbsent(row.getProjectId(), k -> {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("value", row.getProjectId());
                node.put("label", row.getProjectName());
                node.put("children", new ArrayList<Map<String, Object>>());
                return node;
            });

            List<Map<String, Object>> classList =
                    (List<Map<String, Object>>) projectNode.get("children");

            if (row.getClassId() != null) {
                Map<String, Object> classNode = new LinkedHashMap<>();
                classNode.put("value", row.getClassId());
                classNode.put("label", row.getClassName());
                classList.add(classNode);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> orgNode : orgMap.values()) {
            Map<Long, Map<String, Object>> projectMap =
                    (Map<Long, Map<String, Object>>) orgNode.remove("children");
            orgNode.put("children", new ArrayList<>(projectMap.values()));
            result.add(orgNode);
        }

        return result;
    }

    /**
     * 获取班级类型机构对应的项目-班级级联选择
     *
     * @return
     */
    @Override
    public List<ProjectCategoryVO> getSelectOrgProjectClassByType(Integer type) {
        // 查询原始数据（项目 + 班级）
        Long orgId = orgMapper.getOrgId(TokenLocalThreadUtil.get().getUserId()).getId();
        List<OrgProjectClassVO> flatList = baseMapper.getSelectOrgProjectClassByType(orgId, type);

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
     * 根据班级id下载导入作业人员模板
     *
     * @param classId
     * @return
     */
    @Override
    public ResponseEntity<byte[]> downloadImportWorkerTemplate(Long classId) {
        OrgClassDO orgClassDO = orgClassMapper.selectById(classId);
        ValidationUtils.throwIfNull(orgClassDO, "班级已被删除");

        List<String> headers = getExcelHeader(classId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ============ 1. 创建工作表 ============
            Sheet sheet = workbook.createSheet(String.valueOf(classId));

            // ============ 2. 表头样式 ============
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // ============ 3. 温馨提示样式 ============
            CellStyle tipStyle = workbook.createCellStyle();
            Font tipFont = workbook.createFont();
            tipFont.setColor(IndexedColors.DARK_RED.getIndex());
            tipFont.setItalic(true);
            tipStyle.setFont(tipFont);
            tipStyle.setWrapText(true);
            tipStyle.setVerticalAlignment(VerticalAlignment.TOP);

            // ============ 4. 表头行 ============
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(28);
            for (int i = 0; i < headers.size(); i++) {
                sheet.setColumnWidth(i, 20 * 256);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // ============ 5. 温馨提示行 ============
            Row tipRow = sheet.createRow(1);
            tipRow.setHeightInPoints(100);

            String tipText = """
                    温馨提示：
                    1. 请完整填写所有必填项，表头对应的内容不得为空；
                    2. 上传的图片（身份证正反面、一寸照等）请确保大小适配单元格，不可超出边界；
                    3. 报名资格申请表请以 PDF 格式插入，并选择“文件附件”方式；
                    4. 请从第 3 行开始填写数据，确保中间无空行或空白记录；
                    5. 建议每次导入数据不超过 50 条，以提升导入效率。
                    """;


            // 合并提示单元格（例如 A2 ~ 最后一列）
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.size() - 1));
            Cell tipCell = tipRow.createCell(0);
            tipCell.setCellValue(tipText);
            tipCell.setCellStyle(tipStyle);

            // ============ 6. 设置行高、列宽 ============
            for (int i = 2; i <= 51; i++) {
                Row row = sheet.createRow(i);
                row.setHeightInPoints(80);
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.createCell(j);
                    // 设置居中样式
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    cell.setCellStyle(cellStyle);
                }
            }

            if (!headers.isEmpty()) {
                int lastColIndex = headers.size() - 1;
                sheet.setColumnWidth(lastColIndex, 30 * 256);
            }

            // ============ 7. 设置前两列格式为文本 ============
            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setDataFormat(dataFormat.getFormat("@"));
            sheet.setDefaultColumnStyle(0, textStyle); // 第一列
            sheet.setDefaultColumnStyle(1, textStyle); // 第二列

            // ============ 8. 写出 ============
            workbook.write(out);

            String fileName = URLEncoder.encode(
                    orgClassDO.getClassName() + "-导入作业人员信息模板.xlsx",
                    StandardCharsets.UTF_8
            );

            HttpHeaders headersHttp = new HttpHeaders();
            headersHttp.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headersHttp.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(out.toByteArray(), headersHttp, HttpStatus.OK);

        } catch (IOException e) {
            throw new RuntimeException("导出导入模板失败", e);
        }
    }


    private @NotNull List<String> getExcelHeader(Long classId) {
        // 1. 获取数据库中的需上传资料名称
        List<String> docNames = baseMapper.getNeedUploadDoc(classId);

        // 2. 复制默认表头
        List<String> headers = new ArrayList<>(ImportWorkerTemplateConstant.DEFAULT_HEAD);
        if (ObjectUtil.isNotEmpty(docNames)) {
            headers.addAll(docNames);
        }
        return headers;
    }

    /**
     * 批量导入作业人员
     *
     * @param file
     * @return
     */
    @Override
    public ParsedExcelResultVO importWorker(MultipartFile file, Long classId) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new BusinessException("仅支持.xlsx文件");
        }

        try (InputStream is = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException("Excel中未找到有效工作表");
            }
            List<String> expectedHeaders = getExcelHeader(classId);

            // ============ 阶段1：模板与表头校验 ============
            validateTemplate(sheet, classId,expectedHeaders);

            // ============ 阶段2：行级校验（仅检查存在性） ============
            validateRowsBeforeUpload(workbook, sheet,expectedHeaders);

            // ============ 阶段3：上传校验============
            ParsedExcelResultVO parsedExcelResultVO = parse(workbook, sheet, classId,expectedHeaders);

            List<ParsedSuccessVO> successList = parsedExcelResultVO.getSuccessList();
            List<ParsedErrorVO> failedList = parsedExcelResultVO.getFailedList();

            // 检查成功列表中的重复身份证号，将重复条目移到失败列表，并记录重复行号
            removeDuplicateIdCards(successList, failedList);

            // 删除数据库已存在身份证，将已存在的移到失败列表
            removeExistingIdCards(successList, failedList, classId);

            // 对列表的身份证和手机号进行脱敏
            if (ObjectUtil.isNotEmpty(successList)) {
                successList.forEach(item -> {
                    String phone = item.getPhone();
                    item.setEncFieldA(aesWithHMAC.encryptAndSign(phone));
                    item.setPhone(CharSequenceUtil.replaceByCodePoint(phone, 3, phone.length() - 4, '*'));

                    String idCardNumber = item.getIdCardNumber();
                    item.setEncFieldB(aesWithHMAC.encryptAndSign(idCardNumber));
                    item.setIdCardNumber(CharSequenceUtil.replaceByCodePoint(idCardNumber, 2, idCardNumber.length() - 5, '*'));

                    item.setIsUpload(true);
                });
            }

            return parsedExcelResultVO;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("导入过程发生错误，请检查模板是否被修改");
        }
    }

    /**
     * 返回要插入的数据
     *
     * @param workbook
     * @param sheet
     * @param classId
     * @return
     */
    private ParsedExcelResultVO parse(XSSFWorkbook workbook, XSSFSheet sheet, Long classId,List<String> expectedHeaders) {
        ParsedExcelResultVO result = new ParsedExcelResultVO();
        List<ParsedSuccessVO> successList = new ArrayList<>();
        List<ParsedErrorVO> failedList = new ArrayList<>();

        int rowCount = sheet.getPhysicalNumberOfRows();

        for (int rowIndex = 2; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (ExcelMediaUtils.isRowEmpty(row)) break;

            String excelName = getCellString(row, 0);
            String phone = getCellString(row, 1);
            try {
                ParsedSuccessVO worker = new ParsedSuccessVO();
                worker.setExcelName(excelName);
                worker.setPhone(phone);
                // 上传身份证正面
                ExcelUploadFileResultDTO idFront = ExcelMediaUtils.excelUploadFile(
                        workbook, sheet, rowIndex, 2, uploadService, WorkerPictureTypeEnum.ID_CARD_FRONT.getValue());
                String realName = idFront.getRealName();
                if (!realName.equals(excelName)) {
                    throw new BusinessException("上传的身份证与Excel填写的姓名不一致");
                }
                worker.setRowNum(rowIndex + 1);
                worker.setCandidateName(realName);
                worker.setIdCardPhotoFront(idFront.getIdCardPhotoFront());
                worker.setIdCardNumber(idFront.getIdCardNumber());
                worker.setGender(idFront.getGender());

                // 上传身份证反面
                ExcelUploadFileResultDTO idBack = ExcelMediaUtils.excelUploadFile(
                        workbook, sheet, rowIndex, 3, uploadService, WorkerPictureTypeEnum.ID_CARD_BACK.getValue());
                if (LocalDateTime.now().isAfter(idBack.getValidEndDate().atTime(LocalTime.MAX))) {
                    throw new BusinessException("身份证已过期");
                }
                worker.setIdCardPhotoBack(idBack.getIdCardPhotoBack());
                // 上传一寸免冠照
                ExcelUploadFileResultDTO face = ExcelMediaUtils.excelUploadFile(
                        workbook, sheet, rowIndex, 4, uploadService, WorkerPictureTypeEnum.PASSPORT_PHOTO.getValue());
                worker.setFacePhoto(face.getFacePhoto());

                // 报名申请资格表附件
                Map<String, List<String>> oleMap = ExcelMediaUtils.getOleAttachmentMapAndUpload(workbook, rowIndex, uploadService, true);
                List<String> oleMapVal = oleMap.get(rowIndex + "_5");
                worker.setQualificationName(oleMapVal.get(0));
                worker.setQualificationPath(oleMapVal.get(1));
                worker.setStatus(WorkerApplyReviewStatusEnum.PENDING_REVIEW.getValue());
                worker.setClassId(classId);
                worker.setApplyType(WorkerApplyTypeEnum.ORG_IMPORT.getValue());

                Map<String, String> docMap = new HashMap<>();
                for (int col = 6; col < expectedHeaders.size(); col++) {
                    String header = expectedHeaders.get(col);
                    ExcelUploadFileResultDTO pic = ExcelMediaUtils.excelUploadFile(
                            workbook, sheet, rowIndex, col, uploadService, WorkerPictureTypeEnum.GENERAL_PHOTO.getValue());
                    docMap.put(header,pic.getDocUrl());
                }
                worker.setDocMap(docMap);
                successList.add(worker);

            } catch (Exception e) {
                String message = e.getMessage();
                if (message != null && message.contains("BadRequestException")) {
                    int idx = message.lastIndexOf(": ");
                    if (idx != -1 && idx + 2 < message.length()) {
                        message = message.substring(idx + 2);
                    }
                }

                failedList.add(new ParsedErrorVO(
                        rowIndex + 1,
                        excelName,
                        phone,
                        message
                ));
            }
        }
        result.setSuccessList(successList);
        result.setFailedList(failedList);
        return result;
    }

    /**
     * 删除数据库班级已有的报名信息
     *
     * @param successList 成功导入列表
     * @param failedList  失败导入列表
     * @param classId     班级id
     */
    private void removeExistingIdCards(
            List<ParsedSuccessVO> successList,
            List<ParsedErrorVO> failedList,
            Long classId) {
        List<WorkerApplyDO> workerApplyDOS = workerApplyMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getClassId, classId).select(WorkerApplyDO::getIdCardNumber));
        if (ObjectUtil.isNotEmpty(workerApplyDOS)) {
            Set<String> existingIdCardsFromDb = workerApplyDOS.stream()
                    .map(item -> aesWithHMAC.verifyAndDecrypt(item.getIdCardNumber()))
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toSet());
            Iterator<ParsedSuccessVO> iterator = successList.iterator();
            while (iterator.hasNext()) {
                ParsedSuccessVO worker = iterator.next();
                String idCard = worker.getIdCardNumber();
                if (StrUtil.isNotBlank(idCard) && existingIdCardsFromDb.contains(idCard)) {
                    // 移到失败列表
                    ParsedErrorVO errorDTO = new ParsedErrorVO();
                    BeanUtils.copyProperties(worker, errorDTO);
                    errorDTO.setErrorMessage("班级中已存在身份证为【" + CharSequenceUtil.replaceByCodePoint(idCard, 2, idCard.length() - 5, '*') + "】" + "的报名记录");
                    failedList.add(errorDTO);
                    // 从成功列表移除
                    iterator.remove();
                }
            }
        }
    }


    /**
     * 检查成功列表中的重复身份证号，将重复条目移到失败列表，并记录重复行号
     *
     * @param successList 成功导入列表
     * @param failedList  失败导入列表
     */
    private void removeDuplicateIdCards(
            List<ParsedSuccessVO> successList,
            List<ParsedErrorVO> failedList) {

        Map<String, List<ParsedSuccessVO>> idCardMap = new HashMap<>();
        for (ParsedSuccessVO worker : successList) {
            String idCard = worker.getIdCardNumber();
            if (StrUtil.isBlank(idCard)) continue;
            idCardMap.computeIfAbsent(idCard, k -> new ArrayList<>()).add(worker);
        }

        for (Map.Entry<String, List<ParsedSuccessVO>> entry : idCardMap.entrySet()) {
            List<ParsedSuccessVO> list = entry.getValue();
            if (list.size() > 1) {
                List<Integer> rowNums = list.stream()
                        .map(ParsedSuccessVO::getRowNum)
                        .collect(Collectors.toList());

                for (ParsedSuccessVO duplicateWorker : list) {
                    ParsedErrorVO errorDTO = new ParsedErrorVO();
                    BeanUtils.copyProperties(duplicateWorker, errorDTO);

                    List<Integer> otherRows = rowNums.stream()
                            .filter(r -> !r.equals(duplicateWorker.getRowNum()))
                            .collect(Collectors.toList());
                    errorDTO.setErrorMessage(
                            "所上传身份证与第 " + otherRows.stream().map(String::valueOf).collect(Collectors.joining("、")) + " 行一致"
                    );

                    failedList.add(errorDTO);
                }

                successList.removeAll(list);
            }
        }
    }


    /**
     * 校验表头
     *
     * @param sheet
     * @param classId
     */
    private void validateTemplate(XSSFSheet sheet, Long classId,List<String> expectedHeaders) {
        String sheetName = sheet.getSheetName();
        if (!String.valueOf(classId).equals(sheetName)) {
            throw new BusinessException("导入模板与所选班级不匹配或模板已被修改");
        }


        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new BusinessException("Excel表头行为空，请确认模板未被修改");
        }

        List<String> actualHeaders = new ArrayList<>();
        for (Cell cell : headerRow) {
            cell.setCellType(CellType.STRING);
            actualHeaders.add(cell.getStringCellValue().trim());
        }

        if (expectedHeaders.size() != actualHeaders.size()) {
            throw new BusinessException("模板表头数量不匹配，请重新下载最新模板");
        }
        for (int i = 0; i < expectedHeaders.size(); i++) {
            if (!expectedHeaders.get(i).equals(actualHeaders.get(i))) {
                throw new BusinessException(String.format(
                        "模板表头与系统要求不符（第 %d 列应为「%s」，实际为「%s」）",
                        i + 1, expectedHeaders.get(i), actualHeaders.get(i)
                ));
            }
        }
    }

    /**
     * 校验数据有没有空的
     *
     * @param workbook
     * @param sheet
     */
    private void validateRowsBeforeUpload(XSSFWorkbook workbook, XSSFSheet sheet,List<String> expectedHeaders) {

        Set<String> phoneSet = new HashSet<>();
        int rowCount = sheet.getPhysicalNumberOfRows();

        for (int rowIndex = 2; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (ExcelMediaUtils.isRowEmpty(row)) break;
            String workerName = getCellString(row, 0);
            if (StrUtil.isBlank(workerName)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(0)));
            }

            // 手机号唯一性校验
            String phone = getCellString(row, 1);
            if (StrUtil.isBlank(phone)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(1)));
            }
            if (!phoneSet.add(phone)) {
                throw new BusinessException(String.format("第 %d 行【%s】与前面行重复", rowIndex + 1, expectedHeaders.get(1)));
            }

            if (!phone.matches("^1[3-9]\\d{9}$")) {
                throw new BusinessException(String.format("第 %d 行【%s】格式不正确", rowIndex + 1, expectedHeaders.get(1)));
            }

            // 校验必填图片存在
            if (!ExcelMediaUtils.hasPicture(workbook, sheet, rowIndex, 2)) {
                throw new BusinessException(String.format("第 %d 行【身份证人像面】不能为空", rowIndex + 1));
            }
            if (!ExcelMediaUtils.hasPicture(workbook, sheet, rowIndex, 3)) {
                throw new BusinessException(String.format("第 %d 行【身份证国徽面】不能为空", rowIndex + 1));
            }
            if (!ExcelMediaUtils.hasPicture(workbook, sheet, rowIndex, 4)) {
                throw new BusinessException(String.format("第 %d 行【一寸免冠照】不能为空", rowIndex + 1));
            }

            for (int col = 5; col < expectedHeaders.size(); col++) {
                String header = expectedHeaders.get(col);
                if (col == 5) {
                    Map<String, List<String>> oleMap = ExcelMediaUtils.getOleAttachmentMapAndUpload(workbook, rowIndex, uploadService, false);
                    if (!oleMap.containsKey(rowIndex + "_" + col)) {
                        throw new BusinessException(String.format("第 %d 行【%s】请上传 PDF 格式文件", rowIndex + 1, expectedHeaders.get(col)));
                    }
                } else {
                    boolean hasPicture = ExcelMediaUtils.hasPicture(workbook, sheet, rowIndex, col);
                    if (!hasPicture) {
                        throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, header));
                    }
                }
            }
        }
    }

    /**
     * 安全读取单元格文本
     */
    private String getCellString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return StrUtil.trimToEmpty(cell.getStringCellValue());
    }


    /**
     * 封装创建单个 EnrollPreDO 的逻辑
     */
    private EnrollPreDO createEnrollPre(Long candidateId, Long examPlanId, Long orgId) {
        EnrollPreDO preDO = new EnrollPreDO();
        preDO.setCandidateId(candidateId);
        preDO.setPlanId(examPlanId);
        preDO.setOrgId(orgId);
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