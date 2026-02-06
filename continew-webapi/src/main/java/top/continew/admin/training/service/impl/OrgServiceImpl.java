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
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.exception.AuthException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.*;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.model.dto.ExcelUploadFileResultDTO;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.DownloadOSSFileUtil;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.ExamPresenceDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.resp.EnrollResp;
import top.continew.admin.exam.service.ExamineePaymentAuditService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.req.user.UserOrgDTO;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.system.service.UserService;
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
import top.continew.admin.common.util.InMemoryMultipartFile;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 机构信息业务实现
 *
 * @author Anton
 * @since 2025/04/07 10:53
 */
@Slf4j
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

    private final UserService userService;

    @Resource
    private final ExcelUtilReactive excelUtilReactive;

    private final LicenseCertificateMapper licenseCertificateMapper;

    private static final long EXPIRE_TIME = 7;  // 过期时间（天）

    private final CandidateTypeMapper candidateTypeMapper;

    private final WeldingExamApplicationMapper weldingExamApplicationMapper;

    private final ExamRecordsMapper examRecordsMapper;

    private final AESWithHMAC aesWithHMAC;

    @Value("${welding.metal-project-id}")
    private Long metalProjectId;

    @Value("${welding.nonmetal-project-id}")
    private Long nonmetalProjectId;

    @Value("${certificate.road-exam-type-id}")
    private Long roadExamTypeId;

    @Value("${excel.template.summary-table.grade.url}")
    private String gradeTemplateUrl;

    @Value("${excel.template.summary-table.forklift_performance.url}")
    private String forkliftTemplateUrl;

    @Override
    public PageResp<OrgResp> page(OrgQuery query, PageQuery pageQuery) {
        PageResp<OrgResp> page = super.page(query, pageQuery);
        List<OrgResp> list = page.getList();

        if (list.isEmpty()) {
            return page;
        }

        // 1. 提取 orgIds
        List<Long> orgIds = list.stream().map(OrgResp::getId).toList();

        // 2. 分类
        List<Map<String, Object>> categoryRows = orgCategoryRelMapper.listCategoryInfoByOrgIds(orgIds);
        Map<Long, String> categoryMap = categoryRows.stream()
            .collect(Collectors.groupingBy(r -> ((Number)r.get("org_id")).longValue(), Collectors.mapping(r -> (String)r
                .get("name"), Collectors.joining("、"))));

        // 3. 账号（注意别名）
        List<Map<String, Object>> accountRows = orgUserMapper.listAccountNamesByOrgIds(orgIds);
        Map<Long, Map<String, Object>> accountMap = accountRows.stream()
            .collect(Collectors.groupingBy(r -> ((Number)r.get("org_id")).longValue(), Collectors
                .collectingAndThen(Collectors.toList(), rows -> rows.get(0) // 取第一条防止多条记录
                )));

        // 4. 回填数据
        for (OrgResp org : list) {
            Long orgId = org.getId();

            org.setCategoryNames(categoryMap.getOrDefault(orgId, ""));

            Map<String, Object> acc = accountMap.get(orgId);
            if (acc != null) {
                org.setUsername(aesWithHMAC.verifyAndDecrypt((String)acc.get("username")));
                org.setPhone(aesWithHMAC.verifyAndDecrypt((String)acc.get("phone")));
                org.setUserId(String.valueOf(acc.get("userId")));
            }
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
        List<OrgCategoryRelationDO> orgCategoryRelationDOS = orgCategoryRelMapper
            .selectList(new LambdaQueryWrapper<OrgCategoryRelationDO>().eq(OrgCategoryRelationDO::getOrgId, id));
        orgDetailResp.setCategoryIds(orgCategoryRelationDOS.stream()
            .map(OrgCategoryRelationDO::getCategoryId)
            .collect(Collectors.toList()));
        //
        //        List<String> categoryNames = orgCategoryRelMapper.listCategoryNamesByOrgId(id);
        //        orgDetailResp.setCategoryNames(categoryNames == null ? "" : String.join(",", categoryNames));

        return orgDetailResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务保证原子性
    public Long add(OrgReq req) {
        // 社会代号、机构名称、机构信用代码不可重复
        List<OrgDO> orgDOList = baseMapper.selectList(new LambdaQueryWrapper<OrgDO>().eq(OrgDO::getName, req.getName())
            .or()
            .eq(OrgDO::getCode, req.getCode())
            .or()
            .eq(OrgDO::getSocialCode, req.getSocialCode()));
        boolean orgNotEmpty = ObjectUtil.isNotEmpty(orgDOList);
        if (orgNotEmpty) {
            // 删除账号
            userService.deleteOrgUser(Arrays.asList(req.getUserId()));
        }
        ValidationUtils.throwIf(orgNotEmpty, "机构代号、机构名称、机构信用代码已存在");
        Long orgId = super.add(req);
        List<Long> categoryIds = req.getCategoryIds();

        if (CollectionUtil.isNotEmpty(categoryIds)) {
            List<OrgCategoryRelationDO> relations = categoryIds.stream().map(categoryId -> {
                OrgCategoryRelationDO relation = new OrgCategoryRelationDO();
                relation.setOrgId(orgId);
                relation.setCategoryId(categoryId);
                relation.setCreateUser(TokenLocalThreadUtil.get().getUserId());
                relation.setIsDeleted(false); // 设置未删除状态
                return relation;
            }).collect(Collectors.toList());

            // 插入机构与八大类管理表
            orgCategoryRelMapper.insertBatch(relations);

            // 插入机构与账户表
            bindUserToOrg(String.valueOf(orgId), String.valueOf(req.getUserId()));
        }

        redisTemplate.delete(RedisConstant.EXAM_ORGANIZATION_QUERY);
        return orgId;
    }

    @Override
    @Transactional // 确保事务性
    public void update(OrgReq req, Long id) {
        List<OrgDO> orgDOList = baseMapper.selectList(new LambdaQueryWrapper<OrgDO>().ne(OrgDO::getId, id)
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
            List<OrgCategoryRelationDO> relations = newCategoryIds.stream().map(categoryId -> {
                OrgCategoryRelationDO relation = new OrgCategoryRelationDO();
                relation.setOrgId(id);
                relation.setCategoryId(categoryId);
                relation.setCreateUser(TokenLocalThreadUtil.get().getUserId());
                relation.setIsDeleted(false); // 设置未删除状态
                return relation;
            }).collect(Collectors.toList());
            orgCategoryRelMapper.insertBatch(relations);
        }

        // 清理缓存
        redisTemplate.delete(RedisConstant.EXAM_ORGANIZATION_QUERY);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {

        // 1. 删除机构主表
        //        this.removeByIds(ids);

        // 2. 删除机构与分类的关系
        //        orgCategoryRelMapper.delete(new LambdaQueryWrapper<OrgCategoryRelationDO>()
        //                .in(OrgCategoryRelationDO::getOrgId, ids));

        // 3. 查询机构绑定的用户账号
        List<TedOrgUser> tedOrgUsers = orgUserMapper.selectList(new LambdaQueryWrapper<TedOrgUser>()
            .in(TedOrgUser::getOrgId, ids)
            .select(TedOrgUser::getUserId, TedOrgUser::getId));

        if (ObjectUtil.isNotEmpty(tedOrgUsers)) {

            // 3.1 删除机构与用户关联表（按主键 ID 删除！）
            List<Long> orgUserIds = tedOrgUsers.stream().map(TedOrgUser::getId).toList();
            //            orgUserMapper.deleteByIds(orgUserIds);

            // 3.2 删除用户（调用 userService.delete，保证校验 + 清理关联）
            List<Long> userIds = tedOrgUsers.stream().map(TedOrgUser::getUserId).toList();
            //            userService.delete(userIds);
        }

        // 4. 清除缓存
        //        redisTemplate.delete(RedisConstant.EXAM_ORGANIZATION_QUERY);
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
        ValidationUtils.throwIf(!username.matches(RegexConstants.ID_CARD_REGEX), "身份证格式错误");
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
        AgencyStatusVO agencyStatusVO = orgMapper.getAgencyStatus(orgId, userId);

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
        AgencyStatusVO agencyStatusVO = orgMapper.getAgencyStatus(orgId, userId);
        if (agencyStatusVO != null) {
            Integer status = agencyStatusVO.getStatus();
            Long existProjectId = agencyStatusVO.getProjectId();

            // 如果已存在其他项目的申请
            if (existProjectId != null && !existProjectId.equals(projectId)) {
                String projectName = projectMapper.getProjectDetail(existProjectId).getProjectName();
                throw new BusinessException("您已申请【" + projectName + "】，请撤回申请后再申请其他项目");
            }

            // 已存在有效关联（status > 0）
            if (status != null && status > 0) {
                return -1;
            }

            // 已存在无效关联（status == -1） -> 执行恢复
            if (status != null && status == -1) {
                OrgCandidateDO updateDO = new OrgCandidateDO();
                updateDO.setId(agencyStatusVO.getId());
                updateDO.setStatus(1);
                updateDO.setUpdateUser(userId);
                updateDO.setUpdateTime(LocalDateTime.now());
                updateDO.setRemark(null);

                int updateCount = orgCandidateMapper.update(updateDO, new LambdaUpdateWrapper<OrgCandidateDO>()
                    .eq(OrgCandidateDO::getId, agencyStatusVO.getId()));

                // 更新成功后插入培训缴费通知单（如不存在）
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
     * 封装：插入培训缴费通知单（防止重复）
     */
    private void insertTrainingPaymentNotice(Long orgId, Long projectId, Long userId, OrgCandidateDO insertDO) {
        // 检查是否已有通知单
        OrgTrainingPaymentAuditDO existNotice = orgTrainingPaymentAuditMapper
            .selectOne(new LambdaQueryWrapper<OrgTrainingPaymentAuditDO>()
                .eq(OrgTrainingPaymentAuditDO::getOrgId, orgId)
                .eq(OrgTrainingPaymentAuditDO::getCandidateId, userId)
                .eq(OrgTrainingPaymentAuditDO::getProjectId, projectId)
                .eq(OrgTrainingPaymentAuditDO::getEnrollId, insertDO.getId())
                .eq(OrgTrainingPaymentAuditDO::getIsDeleted, 0));

        // 已存在则跳过
        if (existNotice != null) {
            return;
        }

        // 未存在则创建新通知单
        OrgTrainingPriceDO existPrice = orgTrainingPriceMapper.selectOne(new LambdaQueryWrapper<OrgTrainingPriceDO>()
            .eq(OrgTrainingPriceDO::getOrgId, orgId)
            .eq(OrgTrainingPriceDO::getProjectId, projectId)
            .eq(OrgTrainingPriceDO::getIsDeleted, 0));
        if (existPrice == null) {
            throw new BusinessException("未找到培训价格信息");
        }

        OrgTrainingPaymentAuditDO auditDO = new OrgTrainingPaymentAuditDO();
        auditDO.setOrgId(orgId);
        auditDO.setCandidateId(userId);
        auditDO.setProjectId(projectId);
        auditDO.setTrainingId(existPrice.getId());
        auditDO.setAuditStatus(0);
        auditDO.setPaymentAmount(existPrice.getPrice());
        auditDO.setEnrollId(insertDO.getId());

        String prefix = String.valueOf(projectMapper.getProjectDetail(projectId).getProjectCode());
        auditDO.setNoticeNo(excelUtilReactive.generateUniqueNoticeNo(prefix));

        orgTrainingPaymentAuditMapper.insert(auditDO);
    }

    // 学生退出机构
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer studentQuitAgency(Long orgId) {
        if (orgId == null) {
            throw new BusinessException("请选择机构");
        }
        Long userId = TokenLocalThreadUtil.get().getUserId();
        // 查找考生提交加入机构的申请记录
        LambdaQueryWrapper<OrgCandidateDO> candidateQuery = new LambdaQueryWrapper<>();
        candidateQuery.eq(OrgCandidateDO::getCandidateId, userId)
            .eq(OrgCandidateDO::getOrgId, orgId)
            .eq(OrgCandidateDO::getStatus, 2) // 2 = 已加入
            .eq(OrgCandidateDO::getIsDeleted, false);

        OrgCandidateDO application = orgCandidateMapper.selectOne(candidateQuery);

        if (application == null) {
            throw new BusinessException("未找到考生加入机构的申请记录");
        }

        //  查询考生是否已缴费且已退费
        LambdaQueryWrapper<OrgTrainingPaymentAuditDO> paymentQuery = new LambdaQueryWrapper<>();
        paymentQuery.eq(OrgTrainingPaymentAuditDO::getCandidateId, userId)
            .eq(OrgTrainingPaymentAuditDO::getOrgId, orgId)
            .eq(OrgTrainingPaymentAuditDO::getAuditStatus, 6) // 6 = 已退费
            .eq(OrgTrainingPaymentAuditDO::getEnrollId, application.getId())
            .eq(OrgTrainingPaymentAuditDO::getIsDeleted, false);

        Long refundedCount = orgTrainingPaymentAuditMapper.selectCount(paymentQuery);

        if (refundedCount == 0) {
            throw new BusinessException("请先联系机构管理员退费后才能退出机构");
        }

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
                examPlanQueryWrapper.eq(ExamPlanDO::getId, enrollPre.getPlanId()).eq(ExamPlanDO::getIsDeleted, false);
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
        int orgAffectedRows = orgMapper.studentQuitAgency(orgId, userId, application.getId());
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

        // 查找学生提交的加入机构申请记录
        LambdaQueryWrapper<OrgCandidateDO> candidateQuery = new LambdaQueryWrapper<>();
        candidateQuery.eq(OrgCandidateDO::getCandidateId, candidateId)
            .eq(OrgCandidateDO::getOrgId, orgId)
            .eq(OrgCandidateDO::getStatus, 2) // 2 = 已加入
            .eq(OrgCandidateDO::getIsDeleted, false);

        OrgCandidateDO candidateRecord = orgCandidateMapper.selectOne(candidateQuery);
        if (candidateRecord == null) {
            throw new BusinessException("未找到该学生在机构的有效申请记录");
        }

        // 判断该学生是否已退费
        LambdaQueryWrapper<OrgTrainingPaymentAuditDO> paymentQuery = new LambdaQueryWrapper<>();
        paymentQuery.eq(OrgTrainingPaymentAuditDO::getCandidateId, candidateId)
            .eq(OrgTrainingPaymentAuditDO::getOrgId, orgId)
            .eq(OrgTrainingPaymentAuditDO::getEnrollId, candidateRecord.getId())
            .eq(OrgTrainingPaymentAuditDO::getAuditStatus, 6) // 6 = 已退费
            .eq(OrgTrainingPaymentAuditDO::getIsDeleted, false);

        Long refundedCount = orgTrainingPaymentAuditMapper.selectCount(paymentQuery);
        if (refundedCount == 0) {
            throw new BusinessException("该学生尚未退费，无法从机构中移除");
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
                examPlanQueryWrapper.eq(ExamPlanDO::getId, enrollPre.getPlanId()).eq(ExamPlanDO::getIsDeleted, false);
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
        int orgAffectedRows = orgMapper.studentQuitAgency(orgId, candidateId, candidateRecord.getId());
        if (orgAffectedRows <= 0) {
            throw new BusinessException("移除学生失败");
        }

        return orgAffectedRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer studentDelAgency(Long orgId) {
        if (orgId == null) {
            throw new BusinessException("请选择机构");
        }

        Long userId = TokenLocalThreadUtil.get().getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 查找考生提交加入机构的申请记录
        OrgCandidateDO candidateRecord = orgCandidateMapper.selectOne(new LambdaQueryWrapper<OrgCandidateDO>()
            .eq(OrgCandidateDO::getOrgId, orgId)
            .eq(OrgCandidateDO::getCandidateId, userId)
            .eq(OrgCandidateDO::getIsDeleted, false));

        if (candidateRecord == null) {
            throw new BusinessException("未找到有效的机构申请记录，无法撤回");
        }

        Long enrollId = candidateRecord.getId();

        // 查询缴费记录（同一考生 + 机构 + 申请记录）
        List<OrgTrainingPaymentAuditDO> payments = orgTrainingPaymentAuditMapper
            .selectList(new LambdaQueryWrapper<OrgTrainingPaymentAuditDO>()
                .eq(OrgTrainingPaymentAuditDO::getCandidateId, userId)
                .eq(OrgTrainingPaymentAuditDO::getOrgId, orgId)
                .eq(OrgTrainingPaymentAuditDO::getEnrollId, enrollId)
                .eq(OrgTrainingPaymentAuditDO::getIsDeleted, false));

        if (!payments.isEmpty()) {
            // 检查是否有已审核通过（2）的记录（即：已缴费但未退费）
            boolean hasApproved = payments.stream()
                .anyMatch(p -> p.getAuditStatus() != null && p.getAuditStatus() == 2);
            if (hasApproved) {
                throw new BusinessException("请先联系管理员退款后，再撤回申请");
            }

            // 删除所有未退费的缴费记录（逻辑删除）
            int delPayment = orgTrainingPaymentAuditMapper.trainingDelByEnrollId(enrollId, orgId, userId);
            if (delPayment > 0) {
                log.info("撤回申请：删除缴费记录 {} 条，enrollId={}, orgId={}, userId={}", delPayment, enrollId, orgId, userId);
            } else {
                log.info("撤回申请：未发现可删除的缴费记录（可能已退费或本就无缴费），enrollId={}, orgId={}, userId={}", enrollId, orgId, userId);
            }

        }

        // 删除机构申请（OrgCandidate）
        int delAgency = orgMapper.studentDelAgency(orgId, userId);
        if (delAgency <= 0) {
            throw new BusinessException("撤回申请失败：未找到要删除的数据");
        }

        return delAgency;
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
        List<Long> parentIds = parentList.stream().map(ProjectCategoryVO::getValue).toList();

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
        int deletedCount = orgUserMapper.delete(new LambdaQueryWrapper<TedOrgUser>().eq(TedOrgUser::getOrgId, orgId));
        return deletedCount > 0;
    }

    /**
     * 删除机构及其关联的用户信息
     *
     * @param id 机构ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOrgWithRelations(Long id) {

        // 1. 删除机构主表
        this.removeById(id);

        // 2. 删除机构与分类的关系
        orgCategoryRelMapper.delete(new LambdaQueryWrapper<OrgCategoryRelationDO>()
            .eq(OrgCategoryRelationDO::getOrgId, id));

        // 3. 查询机构绑定的用户账号
        TedOrgUser tedOrgUser = orgUserMapper.selectOne(new LambdaQueryWrapper<TedOrgUser>()
            .eq(TedOrgUser::getOrgId, id)
            .select(TedOrgUser::getUserId, TedOrgUser::getId)
            .last("limit 1"));

        if (ObjectUtil.isNotNull(tedOrgUser)) {

            // 3.1 删除机构与用户关联表（按主键 ID 删除！）
            orgUserMapper.deleteById(tedOrgUser.getId());

            // 3.2 删除用户（调用 userService.delete，保证校验 + 清理关联）
            userService.delete(Collections.singletonList(tedOrgUser.getUserId()));
        }
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
        List<Long> parentIds = parentList.stream().map(ProjectCategoryVO::getValue).toList();

        // 3️ 查询第二层（项目）
        List<ProjectCategoryVO> projectList = projectMapper.getSelectCategoryProject(parentIds);

        // 4️ 查询第三层（班级）—— 用项目ID查询
        if (!projectList.isEmpty()) {
            List<Long> projectIds = projectList.stream().map(ProjectCategoryVO::getValue).toList();

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
    public List<ProjectCategoryVO> getSelectProjectClass(Long orgId, Long projectId, Integer classType) {
        // 查询原始数据（项目 + 班级）
        List<OrgProjectClassVO> flatList = baseMapper.getSelectProjectClass(orgId, projectId, classType);

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
    public List<ProjectCategoryVO> getSelectProjectClassCandidate(Long projectId, Integer planType, Long planId) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDTO orgDTO = orgMapper.getOrgId(userTokenDo.getUserId());
        List<OrgProjectClassCandidateVO> flatList = baseMapper.getSelectProjectClassCandidate(orgDTO
            .getId(), projectId, planType, planId);
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
            ProjectCategoryVO clazz = project.getChildren()
                .stream()
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
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean apply(OrgApplyReq orgApplyPreReq) {
        Long examPlanId = orgApplyPreReq.getExamPlanId();
        ExamPlanDO examPlanDO = examPlanMapper.selectByIdForUpdate(examPlanId);
        ValidationUtils.throwIf(Objects.isNull(examPlanDO), "考试计划不存在");
        ValidationUtils.throwIf(PlanFinalConfirmedStatus.DIRECTOR_CONFIRMED.getValue()
            .equals(examPlanDO.getIsFinalConfirmed()), "考试已确认，无法报考");
        ValidationUtils.throwIf(!ExamPlanTypeEnum.WORKER.getValue().equals(examPlanDO.getPlanType()), "无法报考检验人员考试计划");
        // 报名时间校验
        LocalDateTime enrollEndTime = examPlanDO.getEnrollEndTime();
        ValidationUtils.throwIf(!ExamPlanStatusEnum.IN_FORCE.getValue().equals(examPlanDO.getStatus()) || LocalDateTime
            .now()
            .isAfter(enrollEndTime), "报名时间已截至，无法继续报名");
        // 判断机构的信誉分够不够
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());
        Integer creditScore = orgDO.getCreditScore();

        // 1. 低于 70 分直接拒绝
        ValidationUtils.throwIf(creditScore < CreditScoreConstants.PASS, "信誉分低于70分，无法报考");

        // 2. 原报名开始时间
        LocalDateTime enrollStartTime = examPlanDO.getEnrollStartTime();

        // 3. 计算顺延天数
        int delayDays = CreditScoreConstants.calcDelayDays(creditScore);

        // 4. 实际可报名时间
        LocalDateTime actualEnrollTime = enrollStartTime.plusDays(delayDays);

        // 5. 当前时间校验
        ValidationUtils.throwIf(LocalDateTime.now().isBefore(actualEnrollTime), String
            .format("信誉分为%d分，需到 %s 后方可报名", creditScore, actualEnrollTime.toLocalDate()));

        // 1. 空检查
        List<List<Long>> projectClassCandidateList = orgApplyPreReq.getCandidateIds();
        ValidationUtils.throwIfEmpty(projectClassCandidateList, "未选择报考考生！");

        // 2. 收集 candidateId
        Set<Long> allCandidateIds = projectClassCandidateList.stream()
            .filter(inner -> inner.size() > 2)
            .map(inner -> inner.get(2))
            .collect(Collectors.toSet());
        ValidationUtils.throwIf(allCandidateIds.isEmpty(), "未选择报考考生！");

        // 3. 批量查用户
        Map<Long, String> userMap = userMapper.selectBatchIds(allCandidateIds)
            .stream()
            .collect(Collectors.toMap(UserDO::getId, UserDO::getNickname));

        // 查询在黑名单中的考生
        List<CandidateTypeDO> candidateTypeDOS = candidateTypeMapper
            .selectList(new LambdaQueryWrapper<CandidateTypeDO>().in(CandidateTypeDO::getCandidateId, allCandidateIds)
                .eq(CandidateTypeDO::getIsBlacklist, BlacklistConstants.IS_BLACKLIST)
                .select(CandidateTypeDO::getCandidateId));

        if (ObjectUtil.isNotEmpty(candidateTypeDOS)) {
            // 黑名单考生 ID
            List<Long> blacklistCandidateIds = candidateTypeDOS.stream().map(CandidateTypeDO::getCandidateId).toList();

            // 转换为姓名
            String blacklistNames = blacklistCandidateIds.stream()
                .map(id -> userMap.getOrDefault(id, "未知考生"))
                .distinct()
                .collect(Collectors.joining("、"));

            ValidationUtils.throwIf(Boolean.TRUE, "以下考生已被加入黑名单，无法报考：" + blacklistNames);
        }

        // 4. 检查是否同一考生报考多个班级
        Set<Long> seen = new HashSet<>();
        for (List<Long> innerList : projectClassCandidateList) {
            ValidationUtils.throwIf(innerList.size() < 3, "未选择报考考生！");
            Long candidateId = innerList.get(2);
            String nickname = userMap.get(candidateId);
            ValidationUtils.throwIf(!seen.add(candidateId), nickname + "只能报考一个班级，请检查报名信息");
        }

        // 5. 查询是否存在未完成考试的记录
        LambdaQueryWrapper<EnrollDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EnrollDO::getUserId, allCandidateIds)
            .ne(EnrollDO::getEnrollStatus, EnrollStatusConstant.COMPLETED)
            .notIn(EnrollDO::getExamStatus, EnrollStatusConstant.SUBMITTED, EnrollStatusConstant.ABSENT)
            .select(EnrollDO::getExamPlanId, EnrollDO::getUserId);

        List<EnrollDO> enrollDOS = enrollMapper.selectList(wrapper);

        if (ObjectUtil.isNotEmpty(enrollDOS)) {
            Long examProjectId = examPlanDO.getExamProjectId();

            // 查出未完成记录对应的计划
            List<Long> planIds = enrollDOS.stream().map(EnrollDO::getExamPlanId).toList();

            // 查计划对应项目
            Map<Long, Long> planProjectMap = examPlanMapper.selectByIds(planIds)
                .stream()
                .collect(Collectors.toMap(ExamPlanDO::getId, ExamPlanDO::getExamProjectId));

            // 找出同项目未完成考试的考生
            List<Long> conflictUserIds = enrollDOS.stream()
                .filter(e -> examProjectId.equals(planProjectMap.get(e.getExamPlanId())))
                .map(EnrollDO::getUserId)
                .distinct()
                .toList();

            if (!conflictUserIds.isEmpty()) {
                // 转换为姓名
                List<String> names = conflictUserIds.stream().map(userMap::get).toList();

                String msg = "以下考生存在未完成的相同项目考试：" + String.join("、", names);
                ValidationUtils.throwIf(true, msg);
            }

        }

        Long actualCount = enrollMapper.getPlanEnrollCount(examPlanId);
        Integer maxCandidates = examPlanDO.getMaxCandidates();

        ValidationUtils.throwIf(maxCandidates - actualCount - projectClassCandidateList.size() < 0, "报名人数大于考试计划剩余报名名额");

        // 检查是否重复报名
        List<Long> candidateIds = projectClassCandidateList.stream().map(item -> {
            return item.get(2);
        }).toList();

        List<Long> existedCandidateIds = enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
            .eq(EnrollDO::getExamPlanId, examPlanId)
            .in(EnrollDO::getUserId, candidateIds)).stream().map(EnrollDO::getUserId).toList();

        if (CollUtil.isNotEmpty(existedCandidateIds)) {
            String existedNames = String.join("、", userMapper.selectByIds(existedCandidateIds)
                .stream()
                .map(UserDO::getNickname)
                .toList());
            throw new BusinessException("以下考生已报名该考试计划：" + existedNames);
        }

        // 判断考试考试记录合格但未生成证书、成绩未录入的都不能报名
        ProjectDO projectDO = projectMapper.selectById(examPlanDO.getExamProjectId());

        // 已合格但未生成证书 或 成绩未录入的考生
        List<Long> passedButUncertifiedCandidateIds = baseMapper.getPassedButUncertifiedCandidateIds(projectDO.getId());

        // 取交集
        List<Long> conflictCandidateIds = CollectionUtil.emptyIfNull(passedButUncertifiedCandidateIds)
            .stream()
            .filter(allCandidateIds::contains)
            .toList();
        // 只要有冲突，直接抛异常
        if (ObjectUtil.isNotEmpty(conflictCandidateIds)) {
            List<String> names = conflictCandidateIds.stream()
                .map(userMap::get)
                .filter(ObjectUtil::isNotEmpty)
                .toList();

            String msg = "以下考生已参加相同项目考试，但成绩未录入或证书尚未生成，暂不可报名：" + String.join("、", names);

            throw new BusinessException(msg);
        }

        // 证书未过期无法报名
        String projectCode = projectDO.getProjectCode();
        List<LicenseCertificateDO> licenseCertificateDOS = licenseCertificateMapper
            .selectList(new LambdaQueryWrapper<LicenseCertificateDO>()
                .in(LicenseCertificateDO::getCandidateId, allCandidateIds)
                .eq(LicenseCertificateDO::getPsnlcnsItemCode, projectCode));
        if (CollUtil.isNotEmpty(licenseCertificateDOS)) {
            YearMonth currentMonth = YearMonth.now();

            List<Long> blockedCandidateIds = licenseCertificateDOS.stream().filter(item -> {
                LocalDate endDate = item.getEndDate();
                if (endDate == null) {
                    return false;
                }
                YearMonth endMonth = YearMonth.from(endDate);
                return !endMonth.isBefore(currentMonth);
            }).map(LicenseCertificateDO::getCandidateId).distinct().toList();

            if (CollUtil.isNotEmpty(blockedCandidateIds)) {
                String existedNames = userMapper.selectByIds(blockedCandidateIds)
                    .stream()
                    .map(UserDO::getNickname)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.joining("、"));

                throw new BusinessException("以下考生证书仍在有效期内，暂不可报名考试：" + existedNames);
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String examDate = examPlanDO.getStartTime().format(formatter);
        String redisKey = RedisConstant.EXAM_NUMBER_KEY + projectCode + ":" + examDate + ":" + examPlanId;

        // 插入报名表
        List<EnrollDO> insertEnrollList = projectClassCandidateList.stream().map(item -> {
            EnrollDO enrollDO = new EnrollDO();
            enrollDO.setExamPlanId(examPlanId);
            enrollDO.setClassId(item.get(1));
            enrollDO.setUserId(item.get(2));
            enrollDO.setEnrollStatus(EnrollStatusConstant.SIGNED_UP);
            Long seq = redisTemplate.opsForValue().increment(redisKey);
            enrollDO.setSeatId(seq);
            return enrollDO;
        }).toList();
        enrollMapper.insertBatch(insertEnrollList);

        // 插入的报名记录，先查看有没有理论成绩合格，但是实操成绩不合格的记录，在一年内免考理论成绩
        // 通过考试计划id，找出跟他同项目的考试计划
        List<Long> planIds = baseMapper.selectPlanByPlanId(examPlanId);
        if (ObjectUtil.isEmpty(planIds)) {
            return Boolean.TRUE;
        }
        // 找出今年已经复用过理论考试成绩的考生
        List<Long> alterReusedCandidates = baseMapper.selectAlterReusedCandidates(allCandidateIds, planIds);
        Set<Long> reusedSet = new HashSet<>(alterReusedCandidates);
        allCandidateIds.removeIf(reusedSet::contains);

        if (ObjectUtil.isEmpty(allCandidateIds)) {
            return Boolean.TRUE;
        }

        // 当前报名的人和上面的计划一一比对，找出一年内有效（理论成绩合格）的考试记录
        List<ExamRecordsDO> canReuseTheoryRecords = baseMapper
            .selectExamRecordsForTheoryReuse(allCandidateIds, planIds);
        if (ObjectUtil.isEmpty(canReuseTheoryRecords)) {
            return Boolean.TRUE;
        }
        // 一年只能复用一次，Java端按 candidateId 去重，取最新的一条
        Map<Long, ExamRecordsDO> latestRecordMap = canReuseTheoryRecords.stream()
            .collect(Collectors.toMap(ExamRecordsDO::getCandidateId, Function.identity(), (r1, r2) -> {
                if (r1.getCreateTime() == null)
                    return r2;
                if (r2.getCreateTime() == null)
                    return r1;
                return r1.getCreateTime().isAfter(r2.getCreateTime()) ? r1 : r2;
            }));

        // 判断考试计划是否有道路成绩
        ExamPresenceDTO examPlanOperAndRoadDTO = examRecordsMapper.hasOperationOrRoadExam(examPlanId, roadExamTypeId);
        boolean hasRoad = ProjectHasExamTypeEnum.YES.getValue().equals(examPlanOperAndRoadDTO.getIsRoad());

        List<ExamRecordsDO> insertReuseList = latestRecordMap.values().stream().map(item -> {
            ExamRecordsDO insert = new ExamRecordsDO();
            insert.setPlanId(examPlanId);
            insert.setCandidateId(item.getCandidateId());
            insert.setExamScores(item.getExamScores());
            insert.setOperScores(0);
            insert.setOperInputStatus(ExamScoreEntryStatusEnum.NO_ENTRY.getValue());
            insert.setRoadScores(0);
            insert.setRoadInputStatus(hasRoad
                ? ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                : ExamScoreEntryStatusEnum.ENTERED.getValue());
            insert.setAttemptType(ExamRecordAttemptEnum.FIRST.getValue());
            insert.setExamPaper(item.getExamPaper());
            insert.setExamResultStatus(ExamResultStatusEnum.NOT_ENTERED.getValue());
            insert.setRegistrationProgress(ExamRecordsRegisterationProgressEnum.REVIEWED.getValue());
            insert.setIsCertificateGenerated(ExamRecprdsHasCertofocateEnum.NO.getValue());
            return insert;
        }).toList();

        if (ObjectUtil.isNotEmpty(insertReuseList)) {
            examRecordsMapper.insertBatch(insertReuseList);
            // 修改刚刚插入的报名信息，将复用信息状态改成已复用
            List<Long> reusedUserIds = insertReuseList.stream().map(ExamRecordsDO::getCandidateId).distinct().toList();
            LambdaUpdateWrapper<EnrollDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(EnrollDO::getUserId, reusedUserIds)
                .eq(EnrollDO::getExamPlanId, examPlanId)
                .set(EnrollDO::getExamStatus, EnrollStatusConstant.SUBMITTED)
                .set(EnrollDO::getEnrollStatus, EnrollStatusConstant.COMPLETED)
                .set(EnrollDO::getTheoryScoreReused, TheoryScoreReuseEnum.YES.getValue());
            enrollMapper.update(null, updateWrapper);
        }
        return Boolean.TRUE;
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

            Map<Long, Map<String, Object>> projectMap = (Map<Long, Map<String, Object>>)orgNode.get("children");

            Map<String, Object> projectNode = projectMap.computeIfAbsent(row.getProjectId(), k -> {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("value", row.getProjectId());
                node.put("label", row.getProjectName());
                node.put("children", new ArrayList<Map<String, Object>>());
                return node;
            });

            List<Map<String, Object>> classList = (List<Map<String, Object>>)projectNode.get("children");

            if (row.getClassId() != null) {
                Map<String, Object> classNode = new LinkedHashMap<>();
                classNode.put("value", row.getClassId());
                classNode.put("label", row.getClassName());
                classList.add(classNode);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> orgNode : orgMap.values()) {
            Map<Long, Map<String, Object>> projectMap = (Map<Long, Map<String, Object>>)orgNode.remove("children");
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

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ============ 1. 创建工作表 ============
            Sheet sheet = workbook.createSheet(String.valueOf(classId));

            // ============ 2. 表头样式 ============
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short)12);
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

            String fileName = URLEncoder.encode(orgClassDO.getClassName() + "-导入作业人员信息模板.xlsx", StandardCharsets.UTF_8);

            HttpHeaders headersHttp = new HttpHeaders();
            headersHttp.setContentType(MediaType
                .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
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

        try (InputStream is = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException("Excel中未找到有效工作表");
            }
            List<String> expectedHeaders = getExcelHeader(classId);

            // ============ 阶段1：模板与表头校验 ============
            validateTemplate(sheet, classId, expectedHeaders);

            // ============ 阶段2：行级校验（仅检查存在性） ============
            validateRowsBeforeUpload(workbook, sheet, expectedHeaders);

            // ============ 阶段3：上传校验============
            ParsedExcelResultVO parsedExcelResultVO = parse(workbook, sheet, classId, expectedHeaders);

            List<ParsedSuccessVO> successList = parsedExcelResultVO.getSuccessList();
            List<ParsedErrorVO> failedList = parsedExcelResultVO.getFailedList();

            // 检查成功列表中的重复身份证号，将重复条目移到失败列表，并记录重复行号
            removeDuplicateIdCards(successList, failedList);

            // 删除数据库已存在身份证，将已存在的移到失败列表
            removeExistingIdCards(successList, failedList, classId);

            // 删除数据库中电话号码与当前身份证导入的手机号不匹配的记录，将已存在的移到失败列表
            removeMismatchPhoneRecords(successList, failedList);

            // 对列表的身份证和手机号进行脱敏
            if (ObjectUtil.isNotEmpty(successList)) {
                successList.forEach(item -> {
                    String phone = item.getPhone();
                    item.setEncFieldA(aesWithHMAC.encryptAndSign(phone));
                    item.setPhone(CharSequenceUtil.replaceByCodePoint(phone, 3, phone.length() - 4, '*'));

                    String idCardNumber = item.getIdCardNumber();
                    item.setEncFieldB(aesWithHMAC.encryptAndSign(idCardNumber));
                    item.setIdCardNumber(CharSequenceUtil.replaceByCodePoint(idCardNumber, 2, idCardNumber
                        .length() - 5, '*'));

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
     * 解析导入作业人员Excel
     *
     * @param file
     * @param classId
     * @return
     */
    @Override
    public ExcelParseResultVO parsedWorkerExcel(MultipartFile file, Long classId) {
        // 先查出班级属于是否属于焊接项目
        OrgClassDO orgClassDO = orgClassMapper.selectById(classId);
        ValidationUtils.throwIfNull(orgClassDO, "所选班级信息不存在");
        Long projectId = orgClassDO.getProjectId();
        // 属于焊接项目
        Boolean isWelding = metalProjectId.equals(projectId) || nonmetalProjectId.equals(projectId);
        // 判断焊接类型
        //        Integer weldingType = null;
        //        if (Objects.equals(projectId, metalProjectId)) {
        //            weldingType = WeldingTypeEnum.METAL.getValue();
        //        } else if (Objects.equals(projectId, nonmetalProjectId)) {
        //            weldingType = WeldingTypeEnum.NON_METAL.getValue();
        //        }

        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new BusinessException("仅支持.xlsx文件");
        }

        try (InputStream is = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException("Excel中未找到有效工作表");
            }

            // ============ 阶段1：模板与表头校验 ============
            List<String> validateHeaders = isWelding
                ? ImportWorkerTemplateConstant.DEFAULT_HEAD_OLD_WELDING
                : ImportWorkerTemplateConstant.DEFAULT_HEAD_OLD;
            validateTemplate(sheet, validateHeaders);

            // ============ 阶段2：行级校验（仅检查存在性） ============
            validateRows(sheet, validateHeaders, isWelding);

            // ============ 阶段3：上传校验============
            ExcelParseResultVO excelParseResultVO = parsedExcel(sheet, classId, isWelding);

            List<ExcelRowSuccessVO> successList = excelParseResultVO.getSuccessList();
            List<ExcelRowErrorVO> failedList = excelParseResultVO.getFailedList();

            // 检查成功列表中的重复身份证号，将重复条目移到失败列表，并记录重复行号
            removeDuplicateIdCard(successList, failedList);

            // 删除数据库已存在身份证，将已存在的移到失败列表
            removeExistingIdCard(successList, failedList, classId);

            // 删除数据库中电话号码与当前身份证导入的手机号不匹配的记录，将已存在的移到失败列表
            removeMismatchPhoneRecord(successList, failedList);

            return excelParseResultVO;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("导入过程发生错误，请检查模板是否被修改");
        }
    }

    /**
     * 下载成绩汇总表
     */
    @Override
    public ResponseEntity<byte[]> downloadSummary(Long planId) {
        // 1校验考试计划
        ExamPlanDO examPlanDO = examPlanMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "考试计划信息不存在");
        ValidationUtils.throwIf(!PlanFinalConfirmedStatus.DIRECTOR_CONFIRMED.getValue()
            .equals(examPlanDO.getIsFinalConfirmed()), "考试未确认，暂无法下载");

        // 2判断是否有道路成绩
        ExamPresenceDTO examPlanOperAndRoadDTO = examRecordsMapper.hasOperationOrRoadExam(planId, roadExamTypeId);
        boolean hasRoad = ProjectHasExamTypeEnum.YES.getValue().equals(examPlanOperAndRoadDTO.getIsRoad());

        // 3查询报名信息
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDTO orgDTO = baseMapper.getOrgId(userTokenDo.getUserId());
        ValidationUtils.throwIfNull(orgDTO, "当前信息已过期");

        List<EnrollResp> enrollList = enrollMapper.downloadSummaryList(new QueryWrapper<EnrollDO>()
            .eq("te.exam_plan_id", planId)
            .eq("te.is_deleted", 0)
            .eq("toc.org_id", orgDTO.getId())

        );
        ValidationUtils.throwIfEmpty(enrollList, "暂无报考信息");

        // 4下载模板和获取写入参数
        byte[] templateBytes = DownloadOSSFileUtil.downloadUrlToBytes(hasRoad ? forkliftTemplateUrl : gradeTemplateUrl);
        ValidationUtils.throwIf(templateBytes == null || templateBytes.length == 0, "系统繁忙");

        int writeStartRow = hasRoad
            ? DownloadSummaryConstants.WRITE_START_ROW_FORKLIFT
            : DownloadSummaryConstants.WRITE_START_ROW_GRADE;
        int writeEndCell = DownloadSummaryConstants.WRITE_END_CEL;
        int writeRowNumber = DownloadSummaryConstants.WRITE_ROW_NUMBER;
        // 5填充 Excel
        byte[] resultBytes = fillExamSummary(templateBytes, enrollList, writeStartRow, writeEndCell, writeRowNumber);

        // 6构建返回 ResponseEntity
        boolean isZip = enrollList.size() > writeRowNumber;
        String fileName = isZip ? (hasRoad ? "叉车成绩汇总表.zip" : "成绩汇总表.zip") : (hasRoad ? "叉车成绩汇总表.xls" : "成绩汇总表.xls");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename(fileName, StandardCharsets.UTF_8)
            .build());
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return new ResponseEntity<>(resultBytes, headers, HttpStatus.OK);
    }

    /**
     * 填充考试汇总表
     *
     * @param templateBytes Excel 模板字节
     * @param enrollList    报名信息列表
     * @param startRow      写入开始行（0-based）
     * @param endCell       写入结束列（0-based）
     * @param rowNumber     每张表写入行数
     * @return 填充后的字节数组，如果超过 rowNumber，返回 ZIP 压缩包，否则返回 Excel 文件
     */
    public byte[] fillExamSummary(byte[] templateBytes,
                                  List<EnrollResp> enrollList,
                                  int startRow,
                                  int endCell,
                                  int rowNumber) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            if (enrollList.size() <= rowNumber) {
                // 直接生成一个 Excel 文件
                byte[] excelBytes = createSingleExcel(templateBytes, enrollList, startRow, endCell, rowNumber);
                return excelBytes;
            } else {
                // 超过行数限制 → 分成多张表，打包成 ZIP
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    int sheetCount = (int)Math.ceil((double)enrollList.size() / rowNumber);
                    for (int i = 0; i < sheetCount; i++) {
                        int fromIndex = i * rowNumber;
                        int toIndex = Math.min(fromIndex + rowNumber, enrollList.size());
                        List<EnrollResp> subList = enrollList.subList(fromIndex, toIndex);

                        byte[] excelBytes = createSingleExcel(templateBytes, subList, startRow, endCell, rowNumber);

                        String fileName = "成绩汇总表_" + (i + 1) + ".xls";
                        ZipEntry entry = new ZipEntry(fileName);
                        zos.putNextEntry(entry);
                        zos.write(excelBytes);
                        zos.closeEntry();
                    }
                    zos.finish();
                }
                return baos.toByteArray();
            }

        } catch (IOException e) {
            throw new RuntimeException("填充 Excel 汇总表失败", e);
        }
    }

    /**
     * 创建单个 Excel 文件
     */
    private byte[] createSingleExcel(byte[] templateBytes,
                                     List<EnrollResp> enrollList,
                                     int startRow,
                                     int endCell,
                                     int rowNumber) throws IOException {

        try (ByteArrayInputStream bais = new ByteArrayInputStream(templateBytes);
            Workbook workbook = new HSSFWorkbook(bais); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < enrollList.size(); i++) {
                EnrollResp enroll = enrollList.get(i);
                Row row = sheet.getRow(startRow + i);
                if (row == null)
                    row = sheet.createRow(startRow + i);

                for (int cellIndex = 0; cellIndex <= endCell; cellIndex++) {
                    Cell cell = row.getCell(cellIndex);
                    if (cell == null)
                        cell = row.createCell(cellIndex);

                    switch (cellIndex) {
                        case 0:
                            cell.setCellValue(enroll.getClassName());
                            break;
                        case 1:
                            cell.setCellValue(enroll.getSeatId());
                            break;
                        case 2:
                            cell.setCellValue(enroll.getNickName());
                            break;
                        case 3:
                            cell.setCellValue(enroll.getGender().getDescription());
                            break;
                        case 4:
                            cell.setCellValue(aesWithHMAC.verifyAndDecrypt(enroll.getUsername()));
                            break;
                        case 5:
                            cell.setCellValue("");
                            break;
                        case 6:
                            cell.setCellValue(enroll.getCategoryName());
                            break;
                        case 7:
                            cell.setCellValue(enroll.getProjectCode());
                            break;
                        default:
                            break;
                    }
                }
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    /**
     * 返回要插入的数据
     *
     * @param sheet
     * @param classId
     * @return
     */
    private ExcelParseResultVO parsedExcel(XSSFSheet sheet, Long classId, Boolean isWelding) {
        ExcelParseResultVO result = new ExcelParseResultVO();
        List<ExcelRowSuccessVO> successList = new ArrayList<>();
        List<ExcelRowErrorVO> failedList = new ArrayList<>();

        int rowCount = sheet.getPhysicalNumberOfRows();

        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (ExcelMediaUtils.isRowEmpty(row))
                break;

            String candidateName = getCellString(row, 0);
            String idCardNumber = getCellString(row, 1);
            String education = getCellString(row, 2);
            String phone = getCellString(row, 3);
            String workUnit = getCellString(row, 4);
            String address = getCellString(row, 5);
            String politicalStatus = getCellString(row, 6);
            String weldingProject = isWelding ? getCellString(row, 7) : null;
            try {
                ExcelRowSuccessVO worker = new ExcelRowSuccessVO();
                worker.setExcelName(candidateName);
                worker.setPhone(phone);
                worker.setRowNum(rowIndex + 1);
                worker.setCandidateName(candidateName);
                worker.setIdCardNumber(idCardNumber);
                worker.setGender(getGenderByIdCard(idCardNumber));
                worker.setWorkUnit(workUnit);
                worker.setEducation(education);
                worker.setAddress(address);
                worker.setPoliticalStatus(politicalStatus);
                worker.setStatus(WorkerApplyReviewStatusEnum.WAIT_UPLOAD.getValue());
                worker.setClassId(classId);
                worker.setApplyType(WorkerApplyTypeEnum.ORG_IMPORT.getValue());
                if (weldingProject != null) {
                    // 去掉首尾逗号
                    weldingProject = weldingProject.trim();
                    if (weldingProject.startsWith(",")) {
                        weldingProject = weldingProject.substring(1);
                    }
                    if (weldingProject.endsWith(",")) {
                        weldingProject = weldingProject.substring(0, weldingProject.length() - 1);
                    }

                    // 分割、去重、重新拼接
                    weldingProject = Arrays.stream(weldingProject.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .collect(Collectors.joining(","));
                    worker.setWeldingProjectCode(weldingProject);
                }
                successList.add(worker);

            } catch (Exception e) {
                String message = e.getMessage();
                if (message != null && message.contains("BadRequestException")) {
                    int idx = message.lastIndexOf(": ");
                    if (idx != -1 && idx + 2 < message.length()) {
                        message = message.substring(idx + 2);
                    }
                }
                failedList.add(new ExcelRowErrorVO(rowIndex + 1, candidateName, phone, message));
            }
        }
        result.setSuccessList(successList);
        result.setFailedList(failedList);
        return result;
    }

    /**
     * 根据身份证辨别性别
     *
     * @param idCard
     * @return
     */
    private static String getGenderByIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return null;
        }

        // 第 17 位（索引 16）判断性别：奇数男，偶数女
        char genderCode = idCard.charAt(16);

        if (!Character.isDigit(genderCode)) {
            return null;
        }

        int genderNum = genderCode - '0';
        return (genderNum % 2 == 1) ? "男" : "女";
    }

    /**
     * 返回要插入的数据
     *
     * @param workbook
     * @param sheet
     * @param classId
     * @return
     */
    private ParsedExcelResultVO parse(XSSFWorkbook workbook,
                                      XSSFSheet sheet,
                                      Long classId,
                                      List<String> expectedHeaders) {
        ParsedExcelResultVO result = new ParsedExcelResultVO();
        List<ParsedSuccessVO> successList = new ArrayList<>();
        List<ParsedErrorVO> failedList = new ArrayList<>();

        int rowCount = sheet.getPhysicalNumberOfRows();

        for (int rowIndex = 2; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (ExcelMediaUtils.isRowEmpty(row))
                break;

            String excelName = getCellString(row, 0);
            String phone = getCellString(row, 1);
            try {
                ParsedSuccessVO worker = new ParsedSuccessVO();
                worker.setExcelName(excelName);
                worker.setPhone(phone);
                // 上传身份证正面
                ExcelUploadFileResultDTO idFront = ExcelMediaUtils
                    .excelUploadFile(workbook, sheet, rowIndex, 2, uploadService, WorkerPictureTypeEnum.ID_CARD_FRONT
                        .getValue());
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
                ExcelUploadFileResultDTO idBack = ExcelMediaUtils
                    .excelUploadFile(workbook, sheet, rowIndex, 3, uploadService, WorkerPictureTypeEnum.ID_CARD_BACK
                        .getValue());
                if (LocalDateTime.now().isAfter(idBack.getValidEndDate().atTime(LocalTime.MAX))) {
                    throw new BusinessException("身份证已过期");
                }
                worker.setIdCardPhotoBack(idBack.getIdCardPhotoBack());
                // 上传一寸免冠照
                ExcelUploadFileResultDTO face = ExcelMediaUtils
                    .excelUploadFile(workbook, sheet, rowIndex, 4, uploadService, WorkerPictureTypeEnum.PASSPORT_PHOTO
                        .getValue());
                worker.setFacePhoto(face.getFacePhoto());

                // 报名申请资格表附件
                Map<String, List<String>> oleMap = ExcelMediaUtils
                    .getOleAttachmentMapAndUpload(workbook, rowIndex, uploadService, true);
                List<String> oleMapVal = oleMap.get(rowIndex + "_5");
                worker.setQualificationName(oleMapVal.get(0));
                worker.setQualificationPath(oleMapVal.get(1));
                worker.setStatus(WorkerApplyReviewStatusEnum.PENDING_REVIEW.getValue());
                worker.setClassId(classId);
                worker.setApplyType(WorkerApplyTypeEnum.ORG_IMPORT.getValue());

                Map<String, String> docMap = new HashMap<>();
                for (int col = 6; col < expectedHeaders.size(); col++) {
                    String header = expectedHeaders.get(col);
                    ExcelUploadFileResultDTO pic = ExcelMediaUtils
                        .excelUploadFile(workbook, sheet, rowIndex, col, uploadService, WorkerPictureTypeEnum.GENERAL_PHOTO
                            .getValue());
                    docMap.put(header, pic.getDocUrl());
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

                failedList.add(new ParsedErrorVO(rowIndex + 1, excelName, phone, message));
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
    private void removeExistingIdCard(List<ExcelRowSuccessVO> successList,
                                      List<ExcelRowErrorVO> failedList,
                                      Long classId) {
        List<WorkerApplyDO> workerApplyDOS = workerApplyMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
            .eq(WorkerApplyDO::getClassId, classId)
            .select(WorkerApplyDO::getIdCardNumber));
        if (ObjectUtil.isNotEmpty(workerApplyDOS)) {
            Set<String> existingIdCardsFromDb = workerApplyDOS.stream()
                .map(item -> aesWithHMAC.verifyAndDecrypt(item.getIdCardNumber()))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
            Iterator<ExcelRowSuccessVO> iterator = successList.iterator();
            while (iterator.hasNext()) {
                ExcelRowSuccessVO worker = iterator.next();
                String idCard = worker.getIdCardNumber();
                if (StrUtil.isNotBlank(idCard) && existingIdCardsFromDb.contains(idCard)) {
                    // 移到失败列表
                    ExcelRowErrorVO errorDTO = new ExcelRowErrorVO();
                    BeanUtils.copyProperties(worker, errorDTO);
                    errorDTO.setErrorMessage("班级中已存在身份证为【" + idCard + "】" + "的信息");
                    failedList.add(errorDTO);
                    // 从成功列表移除
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 删除数据库班级已有的报名信息
     *
     * @param successList 成功导入列表
     * @param failedList  失败导入列表
     * @param classId     班级id
     */
    private void removeExistingIdCards(List<ParsedSuccessVO> successList,
                                       List<ParsedErrorVO> failedList,
                                       Long classId) {
        List<WorkerApplyDO> workerApplyDOS = workerApplyMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
            .eq(WorkerApplyDO::getClassId, classId)
            .select(WorkerApplyDO::getIdCardNumber));
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
                    errorDTO.setErrorMessage("班级中已存在身份证为【" + CharSequenceUtil.replaceByCodePoint(idCard, 2, idCard
                        .length() - 5, '*') + "】" + "的报名记录");
                    failedList.add(errorDTO);
                    // 从成功列表移除
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 移除身份证与手机号绑定不一致的记录（基于 worker_apply 表）
     * <p>
     * 逻辑：
     * - worker_apply 表中若已存在某身份证
     * - 则该身份证已绑定一个手机号
     * - 若当前 Excel 上传的手机号与数据库手机号不同
     * → 判定为手机号冲突
     */

    private void removeMismatchPhoneRecord(List<ExcelRowSuccessVO> successList, List<ExcelRowErrorVO> failedList) {

        if (CollUtil.isEmpty(successList)) {
            return;
        }

        // 1. 收集所有加密身份证 + 加密手机号
        List<String> encryptedIdCards = successList.stream()
            .map(item -> aesWithHMAC.encryptAndSign(item.getIdCardNumber()))
            .toList();

        List<String> encryptedPhones = successList.stream()
            .map(item -> aesWithHMAC.encryptAndSign(item.getPhone()))
            .toList();

        // 2. 查询数据库 —— 身份证
        List<WorkerApplyDO> existByIdCard = workerApplyMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
            .in(WorkerApplyDO::getIdCardNumber, encryptedIdCards));

        // 3. 查询数据库 —— 手机号
        List<WorkerApplyDO> existByPhone = workerApplyMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
            .in(WorkerApplyDO::getPhone, encryptedPhones));

        // 4. 构建映射（允许重复 key，保留第一条）
        Map<String, String> dbIdCardToPhoneMap = existByIdCard.stream()
            .collect(Collectors.toMap(WorkerApplyDO::getIdCardNumber, WorkerApplyDO::getPhone, (v1, v2) -> v1  // 遇到重复身份证时保留第一条
            ));

        Map<String, String> dbPhoneToIdCardMap = existByPhone.stream()
            .collect(Collectors.toMap(WorkerApplyDO::getPhone, WorkerApplyDO::getIdCardNumber, (v1, v2) -> v1  // 遇到重复手机号时保留第一条
            ));

        // 5. 遍历导入数据
        Iterator<ExcelRowSuccessVO> iterator = successList.iterator();
        while (iterator.hasNext()) {
            ExcelRowSuccessVO item = iterator.next();
            String encryptedIdCard = aesWithHMAC.encryptAndSign(item.getIdCardNumber());
            String encryptedPhone = aesWithHMAC.encryptAndSign(item.getPhone());

            // 校验 1：身份证已绑定但手机号不一致
            String dbPhone = dbIdCardToPhoneMap.get(encryptedIdCard);
            if (dbPhone != null && !dbPhone.equals(encryptedPhone)) {

                moveToFailed(failedList, item, "手机号不匹配，系统已绑定手机号（" + aesWithHMAC
                    .verifyAndDecrypt(dbPhone) + "），请联系管理员处理。");

                iterator.remove();
                continue;
            }

            // 校验 2：手机号已被别人绑定
            String dbIdCard = dbPhoneToIdCardMap.get(encryptedPhone);
            if (dbIdCard != null && !dbIdCard.equals(encryptedIdCard)) {

                moveToFailed(failedList, item, "导入手机号已被其他人员绑定，不能重复使用");

                iterator.remove();
            }
        }
    }

    /**
     * 移除身份证与手机号绑定不一致的记录（基于 worker_apply 表）
     * <p>
     * 逻辑：
     * - worker_apply 表中若已存在某身份证
     * - 则该身份证已绑定一个手机号
     * - 若当前 Excel 上传的手机号与数据库手机号不同
     * → 判定为手机号冲突
     */

    private void removeMismatchPhoneRecords(List<ParsedSuccessVO> successList, List<ParsedErrorVO> failedList) {

        if (CollUtil.isEmpty(successList)) {
            return;
        }

        // 1. 收集所有加密身份证 + 加密手机号
        List<String> encryptedIdCards = successList.stream()
            .map(item -> aesWithHMAC.encryptAndSign(item.getIdCardNumber()))
            .toList();

        List<String> encryptedPhones = successList.stream()
            .map(item -> aesWithHMAC.encryptAndSign(item.getPhone()))
            .toList();

        // 2. 查询数据库 —— 身份证
        List<WorkerApplyDO> existByIdCard = workerApplyMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
            .in(WorkerApplyDO::getIdCardNumber, encryptedIdCards));

        // 3. 查询数据库 —— 手机号
        List<WorkerApplyDO> existByPhone = workerApplyMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
            .in(WorkerApplyDO::getPhone, encryptedPhones));

        // 4. 构建映射（允许重复 key，保留第一条）
        Map<String, String> dbIdCardToPhoneMap = existByIdCard.stream()
            .collect(Collectors.toMap(WorkerApplyDO::getIdCardNumber, WorkerApplyDO::getPhone, (v1, v2) -> v1  // 遇到重复身份证时保留第一条
            ));

        Map<String, String> dbPhoneToIdCardMap = existByPhone.stream()
            .collect(Collectors.toMap(WorkerApplyDO::getPhone, WorkerApplyDO::getIdCardNumber, (v1, v2) -> v1  // 遇到重复手机号时保留第一条
            ));

        // 5. 遍历导入数据
        Iterator<ParsedSuccessVO> iterator = successList.iterator();
        while (iterator.hasNext()) {
            ParsedSuccessVO item = iterator.next();

            String encryptedIdCard = aesWithHMAC.encryptAndSign(item.getIdCardNumber());
            String encryptedPhone = aesWithHMAC.encryptAndSign(item.getPhone());

            // 校验 1：身份证已绑定但手机号不一致
            String dbPhone = dbIdCardToPhoneMap.get(encryptedIdCard);
            if (dbPhone != null && !dbPhone.equals(encryptedPhone)) {

                moveToFailedList(failedList, item, "该人员已有报名已绑定手机号 " + maskPhone(aesWithHMAC
                    .verifyAndDecrypt(dbPhone)) + "，与导入手机号不一致");

                iterator.remove();
                continue;
            }

            // 校验 2：手机号已被别人绑定
            String dbIdCard = dbPhoneToIdCardMap.get(encryptedPhone);
            if (dbIdCard != null && !dbIdCard.equals(encryptedIdCard)) {

                moveToFailedList(failedList, item, "导入手机号已被其他人员绑定，不能重复使用");

                iterator.remove();
            }
        }
    }

    /**
     * 移动到失败列表的工具方法
     */
    private void moveToFailedList(List<ParsedErrorVO> failedList, ParsedSuccessVO item, String message) {
        ParsedErrorVO error = new ParsedErrorVO();
        error.setRowNum(item.getRowNum());
        error.setExcelName(item.getExcelName());
        error.setPhone(item.getPhone());
        error.setErrorMessage(message);
        failedList.add(error);
    }

    /**
     * 移动到失败列表的工具方法
     */
    private void moveToFailed(List<ExcelRowErrorVO> failedList, ExcelRowSuccessVO item, String message) {
        ExcelRowErrorVO error = new ExcelRowErrorVO();
        error.setRowNum(item.getRowNum());
        error.setExcelName(item.getExcelName());
        error.setPhone(item.getPhone());
        error.setErrorMessage(message);
        failedList.add(error);
    }

    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        return CharSequenceUtil.replaceByCodePoint(phone, 3, phone.length() - 4, '*');
    }

    //    // 2. 查询数据库中已存在的身份证 -> 手机号
    //    List<UserDO> userDOS = userMapper.selectList(
    //            new LambdaQueryWrapper<UserDO>()
    //                    .in(UserDO::getUsername, idCards)
    //    );
    //        if (CollUtil.isEmpty(userDOS)) {
    //        return; // 数据库没有匹配身份证，不做处理
    //    }
    //
    //    // 3. 构建身份证 -> 数据库手机号 map
    //    Map<String, String> dbIdCardToPhoneMap = userDOS.stream()
    //            .collect(Collectors.toMap(UserDO::getUsername, UserDO::getPhone));
    //    // 4. 检查是否手机号不一致
    //    Iterator<ParsedSuccessVO> iterator = successList.iterator();
    //        while (iterator.hasNext()) {
    //        ParsedSuccessVO item = iterator.next();
    //        String idCard = aesWithHMAC.encryptAndSign(item.getIdCardNumber());
    //        String importPhone = aesWithHMAC.encryptAndSign(item.getPhone());
    //
    //        String dbPhone = dbIdCardToPhoneMap.get(idCard);
    //        if (dbPhone != null && !dbPhone.equals(importPhone)) {
    //            // 移到失败列表
    //            ParsedErrorVO error = new ParsedErrorVO();
    //            error.setRowNum(item.getRowNum());
    //            error.setPhone(item.getPhone());
    //            String dbPhoneVerify = aesWithHMAC.verifyAndDecrypt(dbPhone);
    //            error.setErrorMessage("已绑定" + CharSequenceUtil.replaceByCodePoint(dbPhoneVerify, 3, dbPhoneVerify.length() - 4, '*') + "手机号，与导入手机号不一致");
    //            error.setExcelName(item.getExcelName());
    //            failedList.add(error);
    //            iterator.remove();
    //        }
    //    }

    /**
     * 检查成功列表中的重复身份证号，将重复条目移到失败列表，并记录重复行号
     *
     * @param successList 成功导入列表
     * @param failedList  失败导入列表
     */
    private void removeDuplicateIdCard(List<ExcelRowSuccessVO> successList, List<ExcelRowErrorVO> failedList) {

        Map<String, List<ExcelRowSuccessVO>> idCardMap = new HashMap<>();
        for (ExcelRowSuccessVO worker : successList) {
            String idCard = worker.getIdCardNumber();
            if (StrUtil.isBlank(idCard))
                continue;
            idCardMap.computeIfAbsent(idCard, k -> new ArrayList<>()).add(worker);
        }

        for (Map.Entry<String, List<ExcelRowSuccessVO>> entry : idCardMap.entrySet()) {
            List<ExcelRowSuccessVO> list = entry.getValue();
            if (list.size() > 1) {
                List<Integer> rowNums = list.stream().map(ExcelRowSuccessVO::getRowNum).collect(Collectors.toList());

                for (ExcelRowSuccessVO duplicateWorker : list) {
                    ExcelRowErrorVO errorDTO = new ExcelRowErrorVO();
                    BeanUtils.copyProperties(duplicateWorker, errorDTO);

                    List<Integer> otherRows = rowNums.stream()
                        .filter(r -> !r.equals(duplicateWorker.getRowNum()))
                        .collect(Collectors.toList());
                    errorDTO.setErrorMessage("所上传身份证与第 " + otherRows.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining("、")) + " 行一致");

                    failedList.add(errorDTO);
                }

                successList.removeAll(list);
            }
        }
    }

    /**
     * 检查成功列表中的重复身份证号，将重复条目移到失败列表，并记录重复行号
     *
     * @param successList 成功导入列表
     * @param failedList  失败导入列表
     */
    private void removeDuplicateIdCards(List<ParsedSuccessVO> successList, List<ParsedErrorVO> failedList) {

        Map<String, List<ParsedSuccessVO>> idCardMap = new HashMap<>();
        for (ParsedSuccessVO worker : successList) {
            String idCard = worker.getIdCardNumber();
            if (StrUtil.isBlank(idCard))
                continue;
            idCardMap.computeIfAbsent(idCard, k -> new ArrayList<>()).add(worker);
        }

        for (Map.Entry<String, List<ParsedSuccessVO>> entry : idCardMap.entrySet()) {
            List<ParsedSuccessVO> list = entry.getValue();
            if (list.size() > 1) {
                List<Integer> rowNums = list.stream().map(ParsedSuccessVO::getRowNum).collect(Collectors.toList());

                for (ParsedSuccessVO duplicateWorker : list) {
                    ParsedErrorVO errorDTO = new ParsedErrorVO();
                    BeanUtils.copyProperties(duplicateWorker, errorDTO);

                    List<Integer> otherRows = rowNums.stream()
                        .filter(r -> !r.equals(duplicateWorker.getRowNum()))
                        .collect(Collectors.toList());
                    errorDTO.setErrorMessage("所上传身份证与第 " + otherRows.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining("、")) + " 行一致");

                    failedList.add(errorDTO);
                }

                successList.removeAll(list);
            }
        }
    }

    /**
     * 校验表头（无资料类型）
     *
     * @param sheet
     */
    private void validateTemplate(XSSFSheet sheet, List<String> expectedHeaders) {
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
                throw new BusinessException(String.format("模板表头与系统要求不符（第 %d 列应为「%s」，实际为「%s」）", i + 1, expectedHeaders
                    .get(i), actualHeaders.get(i)));
            }
        }
    }

    /**
     * 校验表头
     *
     * @param sheet
     * @param classId
     */
    private void validateTemplate(XSSFSheet sheet, Long classId, List<String> expectedHeaders) {
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
                throw new BusinessException(String.format("模板表头与系统要求不符（第 %d 列应为「%s」，实际为「%s」）", i + 1, expectedHeaders
                    .get(i), actualHeaders.get(i)));
            }
        }
    }

    /**
     * 校验数据有没有空的（无资料上传）
     *
     * @param sheet
     */
    private void validateRows(XSSFSheet sheet, List<String> expectedHeaders, Boolean isWelding) {

        int rowCount = sheet.getPhysicalNumberOfRows();

        // 身份证 & 手机号重复校验
        Set<String> idCardSet = new HashSet<>();
        Set<String> phoneSet = new HashSet<>();

        // 如果是焊接项目先查出当前机构已申请的所有焊接资格项目
        //        List<String> orgWeldingProjectCodes = Collections.emptyList();

        //        if (isWelding) {
        //            List<WeldingExamApplicationDO> weldingExamApplicationDOS = weldingExamApplicationMapper
        //                .selectList(new LambdaQueryWrapper<WeldingExamApplicationDO>()
        //                    .eq(WeldingExamApplicationDO::getOrgId, orgId)
        //                    .eq(WeldingExamApplicationDO::getStatus, WeldingExamApplicationStatusEnum.PASS_REVIEW.getValue())
        //                    .eq(WeldingExamApplicationDO::getWeldingType, weldingType)
        //                    .select(WeldingExamApplicationDO::getProjectCode));
        //
        //            orgWeldingProjectCodes = weldingExamApplicationDOS.stream()
        //                .map(WeldingExamApplicationDO::getProjectCode)
        //                .collect(Collectors.toList());
        //        }

        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {

            Row row = sheet.getRow(rowIndex);
            if (ExcelMediaUtils.isRowEmpty(row))
                break;

            // ===== 1. 姓名 =====
            String workerName = getCellString(row, 0);
            if (StrUtil.isBlank(workerName)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(0)));
            }

            // ===== 2. 身份证号 =====
            String idCard = getCellString(row, 1);
            if (StrUtil.isBlank(idCard)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(1)));
            }

            if (!idCard.matches(RegexConstants.ID_CARD_REGEX)) {
                throw new BusinessException(String.format("第 %d 行【%s】格式不正确", rowIndex + 1, expectedHeaders.get(1)));
            }

            if (!idCardSet.add(idCard)) {
                throw new BusinessException(String.format("第 %d 行【%s】与前面行重复", rowIndex + 1, expectedHeaders.get(1)));
            }

            // ===== 3. 学历 =====
            String education = getCellString(row, 2);
            if (StrUtil.isBlank(education)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(2)));
            }

            // 限定学历范围
            Set<String> EDUCATION_SET = ImportWorkerTemplateConstant.EDUCATION_SET;
            if (!EDUCATION_SET.contains(education.trim())) {
                throw new BusinessException(String.format("第 %d 行【%s】只能为：%s", rowIndex + 1, expectedHeaders
                    .get(2), String.join(" / ", EDUCATION_SET)));
            }

            // ===== 4. 联系电话 =====
            String phone = getCellString(row, 3);
            if (StrUtil.isBlank(phone)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(3)));
            }

            if (!phone.matches("^1[3-9]\\d{9}$")) {
                throw new BusinessException(String.format("第 %d 行【%s】格式不正确", rowIndex + 1, expectedHeaders.get(3)));
            }

            if (!phoneSet.add(phone)) {
                throw new BusinessException(String.format("第 %d 行【%s】与前面行重复", rowIndex + 1, expectedHeaders.get(3)));
            }

            // ===== 5. 工作单位 =====
            String workUnit = getCellString(row, 4);
            if (StrUtil.isBlank(workUnit)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(4)));
            }

            // ===== 6. 工作区域 =====
            String address = getCellString(row, 5);
            if (StrUtil.isBlank(address)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(5)));
            }

            // 去空格，防止 Excel 里有空白
            address = address.trim();

            // 校验是否为指定北京区
            if (!ImportWorkerTemplateConstant.BEIJING_DISTRICTS.contains(address)) {
                throw new BusinessException(String.format("第 %d 行【%s】只能填写以下区之一：%s", rowIndex + 1, expectedHeaders
                    .get(5), String.join("、", ImportWorkerTemplateConstant.BEIJING_DISTRICTS)));
            }

            // ===== 7. 政治面貌 =====
            String politicalStatus = getCellString(row, 6);
            if (StrUtil.isBlank(politicalStatus)) {
                throw new BusinessException(String.format("第 %d 行【%s】不能为空", rowIndex + 1, expectedHeaders.get(6)));
            }
            Set<String> POLITICAL_STATUS_SET = ImportWorkerTemplateConstant.POLITICAL_STATUS_SET;
            // 限定政治面貌
            if (!POLITICAL_STATUS_SET.contains(politicalStatus.trim())) {
                throw new BusinessException(String.format("第 %d 行【%s】只能为：%s", rowIndex + 1, expectedHeaders
                    .get(6), String.join(" / ", POLITICAL_STATUS_SET)));
            }
            // 焊接项目还需要判断焊接资格项目是否填写
            if (isWelding) {
                String weldingProject = getCellString(row, 7);
                if (StrUtil.isNotBlank(weldingProject)) {
                    weldingProject = weldingProject.replaceAll("^,+|,+$", "");
                }

                if (StrUtil.isBlank(weldingProject)) {
                    throw new BusinessException(String.format("第 %d 行焊接资格项目不能为空", rowIndex + 1));
                }

                //                if (orgWeldingProjectCodes.isEmpty()) {
                //                    throw new BusinessException("当前机构暂无对应的已通过审核的焊接资格项目");
                //                }

                // Excel 中的焊接项目（去空格、去重）
                //                final List<String> weldingProjectExcel = Arrays.stream(weldingProject.split(","))
                //                    .map(String::trim)
                //                    .filter(StrUtil::isNotBlank)
                //                    .distinct()
                //                    .collect(Collectors.toList());

                // 使用 Set
                //                final Set<String> orgWeldingProjectCodeSet = new HashSet<>(orgWeldingProjectCodes);

                // 查出 Excel 中“机构未申请 / 未通过审核”的项目
                //                List<String> notExistProjects = weldingProjectExcel.stream()
                //                    .filter(code -> !orgWeldingProjectCodeSet.contains(code))
                //                    .collect(Collectors.toList());

                //                if (!notExistProjects.isEmpty()) {
                //                    throw new BusinessException(String
                //                        .format("第 %d 行焊接资格项目【%s】未在当前机构已通过审核的焊接资格范围内或不属于该焊接类型", rowIndex + 1, String
                //                            .join(",", notExistProjects)));
                //                }
            }
        }
    }

    /**
     * 校验数据有没有空的
     *
     * @param workbook
     * @param sheet
     */
    private void validateRowsBeforeUpload(XSSFWorkbook workbook, XSSFSheet sheet, List<String> expectedHeaders) {

        Set<String> phoneSet = new HashSet<>();
        int rowCount = sheet.getPhysicalNumberOfRows();

        for (int rowIndex = 2; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (ExcelMediaUtils.isRowEmpty(row))
                break;
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
                    Map<String, List<String>> oleMap = ExcelMediaUtils
                        .getOleAttachmentMapAndUpload(workbook, rowIndex, uploadService, false);
                    if (!oleMap.containsKey(rowIndex + "_" + col)) {
                        throw new BusinessException(String
                            .format("第 %d 行【%s】请上传 PDF 格式文件", rowIndex + 1, expectedHeaders.get(col)));
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
        if (cell == null)
            return "";
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
        String encryptedCandidateId = URLEncoder.encode(aesWithHMAC.encryptAndSign(String
            .valueOf(candidateId)), StandardCharsets.UTF_8);
        String encryptedPlanId = URLEncoder.encode(aesWithHMAC.encryptAndSign(String
            .valueOf(examPlanId)), StandardCharsets.UTF_8);
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

            MultipartFile file = new InMemoryMultipartFile("file", candidateId + ".png", "image/png", bytes);

            GeneralFileReq fileReq = new GeneralFileReq();
            fileReq.setType("pic");

            FileInfoResp fileInfo = uploadService.upload(file, fileReq);
            return fileInfo.getUrl();
        }
    }
}