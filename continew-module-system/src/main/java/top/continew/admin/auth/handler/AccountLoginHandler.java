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

package top.continew.admin.auth.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.alibaba.excel.support.cglib.beans.BeanMap;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.auth.AbstractLoginHandler;
import top.continew.admin.auth.enums.AuthTypeEnum;
import top.continew.admin.auth.mapper.AuthMapper;
import top.continew.admin.auth.model.dto.ClassroomDTO;
import top.continew.admin.auth.model.dto.InvigilatorPlanDTO;
import top.continew.admin.auth.model.req.AccountLoginReq;
import top.continew.admin.auth.model.req.CandidatesExamPlanReq;
import top.continew.admin.auth.model.resp.CandidatesExamPlanVo;
import top.continew.admin.auth.model.resp.ExamCandidateInfoVO;
import top.continew.admin.auth.model.resp.LoginResp;
import top.continew.admin.common.constant.*;
import top.continew.admin.common.constant.enums.EnrollExamStatusEnum;
import top.continew.admin.common.model.entity.UserRoleDeptDo;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.system.enums.PasswordPolicyEnum;
import top.continew.admin.system.mapper.UserRoleMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.entity.UserRoleDO;
import top.continew.admin.system.model.req.user.UserOrgDTO;
import top.continew.admin.system.model.req.user.UserReq;
import top.continew.admin.system.model.resp.ClientResp;
import top.continew.admin.system.service.UserService;
import top.continew.starter.cache.redisson.util.RedisUtils;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.CheckUtils;
import top.continew.starter.core.validation.ValidationUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 账号登录处理器
 *
 * @author KAI
 * @author Charles7c
 * @since 2024/12/22 14:58
 */
@Component
@RequiredArgsConstructor
public class AccountLoginHandler extends AbstractLoginHandler<AccountLoginReq> {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Value("${examine.userRole.candidatesId}")
    private Long candidatesId;

    @Value("${examine.userRole.invigilatorId}")
    private Long invigilatorId;

    @Value("${examine.userRole.organizationId}")
    private Long organizationId;

    @Resource
    private AuthMapper authMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private AESWithHMAC aesWithHMAC;

    @Override
    public LoginResp login(AccountLoginReq req, ClientResp client, HttpServletRequest request) {
        // 解密密码
        String rawPassword = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getPassword()));
        ValidationUtils.throwIfBlank(rawPassword, "密码解密失败");
        // 验证用户名密码
        String username = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getUsername()));
        ValidationUtils.throwIfBlank(rawPassword, "用户名解密失败");
        UserDO user = userService.getByUsername(aesWithHMAC.encryptAndSign(username));
        boolean isError = ObjectUtil.isNull(user) || !passwordEncoder.matches(rawPassword, user.getPassword());
        // 检查账号锁定状态
        this.checkUserLocked(req.getUsername(), request, isError);
        ValidationUtils.throwIf(isError, "用户名或密码错误");
        // 检查用户状态
        super.checkUserStatus(user);
        // 执行认证
        String token = this.authenticate(user, client);

        UserTokenDo userTokenDto = new UserTokenDo();
        BeanUtils.copyProperties(user, userTokenDto);
        userTokenDto.setToken(token);

        // 查询用户角色、用户部门
        UserRoleDeptDo userDo = userService.getUserRoleDeptByUserId(user.getId());
        BeanUtils.copyProperties(userDo, userTokenDto);
        // 考试、监考、机构不能进入管理后台
        ValidationUtils.throwIf(candidatesId.equals(userDo.getRoleId()) || organizationId.equals(userDo
            .getRoleId()) || invigilatorId.equals(userDo.getRoleId()), "用户名或密码错误");
        // 将认证保存至Redis中
        redisTemplate.opsForValue()
            .set(RedisConstant.USER_TOKEN + token, userTokenDto, RedisConstant.FIFTEEN_DAYS, TimeUnit.SECONDS);
        return LoginResp.builder().token(token).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResp examLogin(AccountLoginReq req, ClientResp client, HttpServletRequest request) {
        // 验证用户名密码
        String username = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getUsername()));
        UserDO user = userService.getByUsername(aesWithHMAC.encryptAndSign(username));
        ValidationUtils.throwIf(ObjectUtil.isEmpty(user), "未找到对应考试");
        // 检查用户状态
        //        super.checkUserStatus(user);

        // 根据考生ID和准考证查询考试信息
        CandidatesExamPlanReq candidatesExamPlanReq = new CandidatesExamPlanReq();
        candidatesExamPlanReq.setCandidateId(user.getId());
        String examNumber = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getExamNumber()));
        String examNumberEncrypt = aesWithHMAC.encryptAndSign(examNumber);
        candidatesExamPlanReq.setExamNumber(examNumberEncrypt);
        //        candidatesExamPlanReq.setEnrollStatus(ExamEnrollStatusEnum.SIGNED_UP.getValue());
        CandidatesExamPlanVo candidatesExamPlanVo = userService.getPlanInfo(candidatesExamPlanReq);
        // 找不到对应的考试
        ValidationUtils.throwIf(ObjectUtil.isEmpty(candidatesExamPlanVo), "请核对身份证号或准考证号是否正确");
        ValidationUtils.throwIf(!PlanConstant.EXAM_BEGUN.getStatus()
            .equals(candidatesExamPlanVo.getStatus()) || (EnrollStatusConstant.SUBMITTED.equals(candidatesExamPlanVo
                .getExamStatus()) || EnrollStatusConstant.COMPLETED.equals(candidatesExamPlanVo
                    .getEnrollStatus())), "请确认考试是否已开始，或是否已参加过本场考试");
        LocalDateTime startTime = candidatesExamPlanVo.getStartTime();

        // 执行认证
        String token = this.authenticate(user, client);

        UserTokenDo userTokenDto = new UserTokenDo();
        BeanUtils.copyProperties(user, userTokenDto);
        userTokenDto.setToken(token);

        // 查询用户角色、用户部门
        UserRoleDeptDo userDo = userService.getUserRoleDeptByUserId(user.getId());
        BeanUtils.copyProperties(userDo, userTokenDto);

        // 将认证保存至Redis中
        redisTemplate.opsForValue()
            .set(RedisConstant.USER_TOKEN + token, userTokenDto, RedisConstant.FIFTEEN_DAYS, TimeUnit.SECONDS);

        // 查出考场对应的信息
        ClassroomDTO classroomDTO = userService.getClassroomInfo(candidatesExamPlanVo.getClassroomId());

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

        // 格式化开始时间和结束时间
        String formattedStartTime = startTime.format(formatter);

        ExamCandidateInfoVO examCandidateInfoVO = new ExamCandidateInfoVO();
        examCandidateInfoVO.setExamTime(formattedStartTime);
        examCandidateInfoVO.setExamNumber(examNumber);
        examCandidateInfoVO.setPlanName(candidatesExamPlanVo.getPlanName());
        examCandidateInfoVO.setPlanId(candidatesExamPlanVo.getPlanId());
        examCandidateInfoVO.setClassroomId(classroomDTO.getClassroomId());
        examCandidateInfoVO.setClassroomName(classroomDTO.getClassroomName());
        examCandidateInfoVO.setWarningShortFilm(candidatesExamPlanVo.getWarningShortFilm());
        examCandidateInfoVO.setExamDuration(candidatesExamPlanVo.getExamDuration());
        examCandidateInfoVO.setEnableProctorWarning(candidatesExamPlanVo.getEnableProctorWarning());
        // 修改考生的考试状态为已签到
        userService.updateExamStatus(user.getId(), examNumberEncrypt, candidatesExamPlanVo
            .getPlanId(), EnrollExamStatusEnum.SIGNED.getValue());
        return LoginResp.builder()
            .token(token)
            .role(UserConstant.CANDIDATES_ROLE_FLAG)
            .examCandidateInfoVO(examCandidateInfoVO)
            .build();
    }

    @Override
    public LoginResp invigilatorLogin(AccountLoginReq req, ClientResp client, HttpServletRequest request) {
        // 验证开考密码
        String examPassword = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getExamPassword()));
        List<InvigilatorPlanDTO> invigilatorPlanDTOS = userService.getPlanInfoByExamPassword(examPassword);
        ValidationUtils.throwIf(ObjectUtil.isEmpty(invigilatorPlanDTOS), "开考密码错误");
        InvigilatorPlanDTO invigilatorPlanDTO = invigilatorPlanDTOS.get(0);
        //        ValidationUtils.throwIf(invigilatorPlanDTO.getStatus().equals(PlanConstant.EXAM_BEGUN.getStatus()),"考试已开考");
        ValidationUtils.throwIf(invigilatorPlanDTO.getStatus().equals(PlanConstant.OVER.getStatus()), "考试已结束");

        LocalDateTime startTime = invigilatorPlanDTO.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        // 开始时间 - 当前时间 > 15分钟： 考前45分钟才可以进去
        ValidationUtils.throwIf(Duration.between(now, startTime).toMinutes() > 15, "请于考前15分钟内进入");

        UserDO user = userService.getById(invigilatorPlanDTO.getInvigilatorId());

        String token = this.authenticate(user, client);

        UserTokenDo userTokenDto = new UserTokenDo();
        BeanUtils.copyProperties(user, userTokenDto);
        userTokenDto.setToken(token);

        // 查询用户角色、用户部门
        UserRoleDeptDo userDo = userService.getUserRoleDeptByUserId(user.getId());
        BeanUtils.copyProperties(userDo, userTokenDto);

        // 将认证保存至Redis中
        redisTemplate.opsForValue()
            .set(RedisConstant.USER_TOKEN + token, userTokenDto, RedisConstant.FIFTEEN_DAYS, TimeUnit.SECONDS);

        // 查出考场对应的信息
        ClassroomDTO classroomDTO = userService.getClassroomInfo(invigilatorPlanDTO.getClassroomId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
        String formattedStartTime = startTime.format(formatter);
        ExamCandidateInfoVO examCandidateInfoVO = new ExamCandidateInfoVO();
        examCandidateInfoVO.setPlanId(invigilatorPlanDTO.getPlanId());
        examCandidateInfoVO.setPlanName(invigilatorPlanDTO.getExamPlanName());
        examCandidateInfoVO.setExamTime(formattedStartTime);
        examCandidateInfoVO.setClassroomId(classroomDTO.getClassroomId());
        examCandidateInfoVO.setClassroomName(classroomDTO.getClassroomName());

        // 更新考试计划状态
        userService.examBegins(PlanConstant.EXAM_BEGUN.getStatus(), invigilatorPlanDTO.getPlanId());
        return LoginResp.builder()
            .token(token)
            .role(UserConstant.INVIGILATOR_ROLE_FLAG)
            .examCandidateInfoVO(examCandidateInfoVO)
            .build();
    }



    @Override
    public void preLogin(AccountLoginReq req, ClientResp client, HttpServletRequest request) {
        super.preLogin(req, client, request);
        // 校验验证码
        int loginCaptchaEnabled = optionService.getValueByCode2Int("LOGIN_CAPTCHA_ENABLED");
        if (SysConstants.YES.equals(loginCaptchaEnabled)) {
            ValidationUtils.throwIfBlank(req.getCaptcha(), "验证码不能为空");
            ValidationUtils.throwIfBlank(req.getUuid(), "验证码标识不能为空");
            String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + req.getUuid();
            String captcha = RedisUtils.get(captchaKey);
            ValidationUtils.throwIfBlank(captcha, CAPTCHA_EXPIRED);
            RedisUtils.delete(captchaKey);
            ValidationUtils.throwIfNotEqualIgnoreCase(req.getCaptcha(), captcha, CAPTCHA_ERROR);
        }
    }

    @Override
    public AuthTypeEnum getAuthType() {
        return AuthTypeEnum.ACCOUNT;
    }

    @Override
    public LoginResp candidatesOrInviteLogin(AccountLoginReq req, ClientResp client, HttpServletRequest request) {
        String username = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getUsername()));
        ValidationUtils.throwIfBlank(username, "用户名解密失败");
        String aesUsername = aesWithHMAC.encryptAndSign(username);
        if (userService.getByUsername(aesUsername) == null) {
            // redis验证注册信息进行登录
            String key = RedisConstant.EXAM_STUDENTS_REGISTER + username;

            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
            ValidationUtils.throwIf(MapUtil.isEmpty(entries), "请先注册");

            UserOrgDTO userOrgDTO = new UserOrgDTO();
            BeanMap beanMap = BeanMap.create(userOrgDTO);

            entries.forEach((fieldObj, valueObj) -> {
                String field = (String)fieldObj;
                String strValue = (String)valueObj;

                // 跳过username字段的特殊处理
                if ("username".equals(field))
                    return;

                // 获取目标字段类型
                Class<?> targetType = beanMap.getPropertyType(field);

                // 处理空值
                if (strValue == null || strValue.isEmpty()) {
                    beanMap.put(field, null);
                    return;
                }

                // 类型转换
                Object convertedValue = convertStringToType(strValue, targetType);
                beanMap.put(field, convertedValue);

            });

            // 单独设置username
            userOrgDTO.setUsername(aesUsername);
            // 考生信息注册
            this.orgSignUp(userOrgDTO);

            // 删除redis中的考生信息
            stringRedisTemplate.delete(key);
        }

        // 解密密码
        String rawPassword = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getPassword()));
        ValidationUtils.throwIfBlank(rawPassword, "密码解密失败");
        // 验证用户名密码
        UserDO user = userService.getByUsername(aesUsername);
        // 校验角色
        LambdaQueryWrapper<UserRoleDO> userRoleDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleDOLambdaQueryWrapper.eq(UserRoleDO::getUserId, user.getId());
        List<UserRoleDO> userRoleDOS = new ArrayList<>();
        /*
        if (ExamRoleConstants.CANDIDATES.equals(req.getRole())) {
            userRoleDOLambdaQueryWrapper.eq(UserRoleDO::getRoleId, candidatesId);
            userRoleDOS = userRoleMapper.selectList(userRoleDOLambdaQueryWrapper);
        } else if (ExamRoleConstants.INVITE.equals(req.getRole())) {
            userRoleDOLambdaQueryWrapper.eq(UserRoleDO::getRoleId, invigilatorId);
            userRoleDOS = userRoleMapper.selectList(userRoleDOLambdaQueryWrapper);
        } else if (ExamRoleConstants.ORGANIZATION.equals(req.getRole())) {
            userRoleDOLambdaQueryWrapper.eq(UserRoleDO::getRoleId, organizationId);
            userRoleDOS = userRoleMapper.selectList(userRoleDOLambdaQueryWrapper);
            // 验证机构人员是否绑定机构
            UserDO orgUser = userService.getOrg(user.getId());
            ValidationUtils.throwIf(ObjectUtil.isNull(orgUser), "请联系管理员绑定机构");
        }
        */

        Long roleId = null;
        if (ExamRoleConstants.CANDIDATES.equals(req.getRole())) {
            roleId = candidatesId;
        } else if (ExamRoleConstants.INVITE.equals(req.getRole())) {
            roleId = invigilatorId;
        } else if (ExamRoleConstants.ORGANIZATION.equals(req.getRole())) {
            roleId = organizationId;
            // 验证机构人员是否绑定机构
            UserDO orgUser = userService.getOrg(user.getId());
            ValidationUtils.throwIf(ObjectUtil.isNull(orgUser), "请联系管理员绑定机构");
        }
        userRoleDOLambdaQueryWrapper.eq(UserRoleDO::getRoleId, roleId);
        userRoleDOS = userRoleMapper.selectList(userRoleDOLambdaQueryWrapper);

        boolean isError = ObjectUtil.isNull(user) || !passwordEncoder.matches(rawPassword, user
            .getPassword()) || ObjectUtil.isEmpty(userRoleDOS);
        // 检查账号锁定状态
        this.checkUserLocked(req.getUsername(), request, isError);
        ValidationUtils.throwIf(isError, "用户名或密码错误");
        // 检查用户状态
        super.checkUserStatus(user);
        // 执行认证
        String token = this.authenticate(user, client);

        UserTokenDo userTokenDto = new UserTokenDo();
        BeanUtils.copyProperties(user, userTokenDto);
        userTokenDto.setToken(token);

        // 查询用户角色、用户部门
        UserRoleDeptDo userDo = userService.getUserRoleDeptByUserId(user.getId());
        BeanUtils.copyProperties(userDo, userTokenDto);

        // 将认证保存至Redis中
        redisTemplate.opsForValue()
            .set(RedisConstant.USER_TOKEN + token, userTokenDto, RedisConstant.FIFTEEN_DAYS, TimeUnit.SECONDS);

        return LoginResp.builder().token(token).role(req.getRole()).build();
    }

    /**
     * 检测用户是否已被锁定
     *
     * @param username 用户名
     * @param request  请求对象
     * @param isError  是否登录错误
     */
    private void checkUserLocked(String username, HttpServletRequest request, boolean isError) {
        // 不锁定
        int maxErrorCount = optionService.getValueByCode2Int(PasswordPolicyEnum.PASSWORD_ERROR_LOCK_COUNT.name());
        if (maxErrorCount <= SysConstants.NO) {
            return;
        }
        // 检测是否已被锁定
        String key = CacheConstants.USER_PASSWORD_ERROR_KEY_PREFIX + RedisUtils.formatKey(username, JakartaServletUtil
            .getClientIP(request));
        int lockMinutes = optionService.getValueByCode2Int(PasswordPolicyEnum.PASSWORD_ERROR_LOCK_MINUTES.name());
        Integer currentErrorCount = ObjectUtil.defaultIfNull(RedisUtils.get(key), 0);
        CheckUtils.throwIf(currentErrorCount >= maxErrorCount, PasswordPolicyEnum.PASSWORD_ERROR_LOCK_MINUTES.getMsg()
            .formatted(lockMinutes));
        // 登录成功清除计数
        if (!isError) {
            RedisUtils.delete(key);
            return;
        }
        // 登录失败递增计数
        currentErrorCount++;
        RedisUtils.set(key, currentErrorCount, Duration.ofMinutes(lockMinutes));
        CheckUtils.throwIf(currentErrorCount >= maxErrorCount, PasswordPolicyEnum.PASSWORD_ERROR_LOCK_COUNT.getMsg()
            .formatted(maxErrorCount, lockMinutes));
    }

    public void orgSignUp(UserOrgDTO userDTO) {
        //获取机构id
        Long orgId = userDTO.getOrgId();
        //将userDTO转化为UserReq
        UserReq userReq = new UserReq();
        BeanUtils.copyProperties(userDTO, userReq);
        userService.add(userReq);//获取考生id
        Long candidateId = authMapper.selectIdByUsername(userDTO.getUsername());
        authMapper.orgUserRoleAdd(userDTO.getRoleId(), candidateId);
        Long status = 2L;
        authMapper.linkCandidateWithOrg(candidateId, orgId, status);//添加到关联表
        //添加到关联表
    }

    private Object convertStringToType(String value, Class<?> targetType) {
        if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == String.class) {
            return value;
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        // 其他类型可在此扩展
        throw new IllegalArgumentException("不支持的字段类型: " + targetType.getName());
    }
}