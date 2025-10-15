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

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.result.R;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.common.enums.SpecialCertificationApplicantEnum;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;

import top.continew.admin.config.ali.AliYunConfig;
import top.continew.admin.constant.SmsConstants;
import top.continew.admin.exam.mapper.ClassroomMapper;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.mapper.PlanClassroomMapper;
import top.continew.admin.exam.model.dto.BatchAuditSpecialCertificationApplicantDTO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.req.EnrollReq;
import top.continew.admin.exam.model.req.SpecialCertificationApplicantListReq;
import top.continew.admin.exam.service.EnrollService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.SpecialCertificationApplicantMapper;
import top.continew.admin.exam.model.entity.SpecialCertificationApplicantDO;
import top.continew.admin.exam.model.query.SpecialCertificationApplicantQuery;
import top.continew.admin.exam.model.req.SpecialCertificationApplicantReq;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantDetailResp;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantResp;
import top.continew.admin.exam.service.SpecialCertificationApplicantService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 特种设备人员资格申请业务实现
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SpecialCertificationApplicantServiceImpl extends BaseServiceImpl<SpecialCertificationApplicantMapper, SpecialCertificationApplicantDO, SpecialCertificationApplicantResp, SpecialCertificationApplicantDetailResp, SpecialCertificationApplicantQuery, SpecialCertificationApplicantReq> implements SpecialCertificationApplicantService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EnrollService enrollService;

    @Resource
    private AsyncClient smsAsyncClient;

    @Autowired
    private AliYunConfig aliYunConfig;

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private PlanClassroomMapper planClassroomMapper;

    @Resource
    private SpecialCertificationApplicantMapper scMapper;

    @Override
    public SpecialCertificationApplicantResp getByCandidates(Long planId) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        LambdaQueryWrapper<SpecialCertificationApplicantDO> specialCertificationApplicantDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        specialCertificationApplicantDOLambdaQueryWrapper
            .eq(SpecialCertificationApplicantDO::getCandidatesId, userTokenDo.getUserId());
        specialCertificationApplicantDOLambdaQueryWrapper.eq(SpecialCertificationApplicantDO::getPlanId, planId);
        specialCertificationApplicantDOLambdaQueryWrapper.eq(SpecialCertificationApplicantDO::getStatus, 0);
        SpecialCertificationApplicantDO specialCertificationApplicantDO = baseMapper
            .selectOne(specialCertificationApplicantDOLambdaQueryWrapper);
        SpecialCertificationApplicantResp specialCertificationApplicantResp = new SpecialCertificationApplicantResp();
        if (ObjectUtils.isEmpty(specialCertificationApplicantDO)) {
            return null;
        }
        BeanUtils.copyProperties(specialCertificationApplicantDO, specialCertificationApplicantResp);
        return specialCertificationApplicantResp;
    }

    @Transactional
    @Override
    public Boolean candidatesUpload(SpecialCertificationApplicantReq specialCertificationApplicantReq) {
        SpecialCertificationApplicantDO specialCertificationApplicantDO = new SpecialCertificationApplicantDO();
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        specialCertificationApplicantDO.setCandidatesId(userTokenDo.getUserId());
        BeanUtils.copyProperties(specialCertificationApplicantReq, specialCertificationApplicantDO);
        return this.baseMapper.insert(specialCertificationApplicantDO) > 0;
    }

    @Override
    public Boolean candidatesUploads(SpecialCertificationApplicantListReq scar) {
        Map<String, String> studentMap;
        List<SpecialCertificationApplicantDO> scaList = new ArrayList<>();
        try {
            ObjectMapper om = new ObjectMapper();
            studentMap = om.readValue(scar.getStudentMapStr(), Map.class);
            ValidationUtils.throwIfNull(studentMap, "请上传申请表");
            scaList = getSpecialCertificationApplicantDOS(scar, studentMap);
        } catch (Exception e) {
            log.error("错误", e);
        }
        List<UserDO> userDO = scMapper.selectLog(scaList, scar.getPlanId(), SpecialCertificationApplicantEnum.UNAUDITED
            .getValue());
        for (UserDO u : userDO) {
            ValidationUtils.throwIfNotNull(userDO, "申请提交失败，" + u.getNickname() + " 已提交申请表，请等待审批");
        }
        return scMapper.insertStudentImage(scaList, TokenLocalThreadUtil.get().getUserId()) != 0;
    }

    private static @NotNull List<SpecialCertificationApplicantDO> getSpecialCertificationApplicantDOS(SpecialCertificationApplicantListReq scar,
                                                                                                      Map<String, String> studentMap) {
        List<SpecialCertificationApplicantDO> scaList = new ArrayList<>();
        for (Map.Entry<String, String> entry : studentMap.entrySet()) {
            SpecialCertificationApplicantDO scaDB = new SpecialCertificationApplicantDO();
            scaDB.setCandidatesId(Long.parseLong(entry.getKey()));
            scaDB.setImageUrl(entry.getValue());
            scaDB.setPlanId(Long.parseLong(scar.getPlanId()));
            scaDB.setStatus(SpecialCertificationApplicantEnum.UNAUDITED.getValue());
            scaList.add(scaDB);
        }
        return scaList;
    }

    /**
     * 批量审核
     *
     * @param dto
     */
    @Override
    //TODO 优化批量审核
    public R batchAudit(BatchAuditSpecialCertificationApplicantDTO dto) {
        //1.从dto获取ids和status
        //2.判断成功还是失败
        EnrollReq enrollReq = new EnrollReq();
        enrollReq.setEnrollStatus(1L);
        boolean success = false;
        if (dto.getStatus() == 2)//不通过
        {
            //2.1修改状态
            LambdaUpdateWrapper<SpecialCertificationApplicantDO> specialCertificationApplicantDOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            specialCertificationApplicantDOLambdaUpdateWrapper.in(SpecialCertificationApplicantDO::getId, dto.getIds())
                .set(SpecialCertificationApplicantDO::getStatus, dto.getStatus());
            super.update(specialCertificationApplicantDOLambdaUpdateWrapper);

            //2.2先发送短信，循环发送
            List<String> ids = dto.getIds();
            QueryWrapper<SpecialCertificationApplicantDO> idWrapper = new QueryWrapper<SpecialCertificationApplicantDO>()
                .in("id", ids);
            List<SpecialCertificationApplicantDO> scaList = baseMapper.selectList(idWrapper);
            for (SpecialCertificationApplicantDO sca : scaList) {
                // 2.1.1构建计划名称
                ExamPlanDO examPlanDO = examPlanMapper.selectById(sca.getPlanId());
                String examPlanName = examPlanDO.getExamPlanName();
                //  2.1.2构建手机号
                String phone = userMapper.selectById(sca.getCandidatesId()).getPhone();
                sms(examPlanName, phone, "审核不通过，原因是:" + dto.getReason());
            }
        } else if (dto.getStatus() == 1) {

            List<String> ids = dto.getIds();
            QueryWrapper<SpecialCertificationApplicantDO> idWrapper = new QueryWrapper<SpecialCertificationApplicantDO>()
                .in("id", ids);
            List<SpecialCertificationApplicantDO> scaList = baseMapper.selectList(idWrapper);
            //1.检查有没有达到最大人数循环检查
            for (SpecialCertificationApplicantDO sca : scaList) {
                // 2.1.1构建计划名称
                ExamPlanDO examPlanDO = examPlanMapper.selectById(sca.getPlanId());
                String examPlanName = examPlanDO.getExamPlanName();
                enrollReq.setExamPlanId(sca.getPlanId());
                //  2.1.2构建手机号
                String phone = userMapper.selectById(sca.getCandidatesId()).getPhone();
                ExamPlanDO examPlanDO1 = examPlanMapper.selectById(sca.getPlanId());//获取计划信息，提取最大人数
                long enrolledCount = classroomMapper.getPlanCount(sca.getPlanId());//获取当前计划已经报名人数

                if (enrolledCount >= examPlanDO1.getMaxCandidates()) {

                    String reason = "审核不通过,原因是:该考试计划报名人数已满!";
                    //发送短信+赋值失败
                    //检验是否还需要跟新状态
                    LambdaQueryWrapper<SpecialCertificationApplicantDO> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(SpecialCertificationApplicantDO::getStatus, 0)
                        .eq(SpecialCertificationApplicantDO::getPlanId, sca.getPlanId());
                    if (baseMapper.selectCount(wrapper) > 0) {
                        success = true;
                        smsList(examPlanName, String.valueOf(sca.getPlanId()), reason);
                        batchRejectApplications(sca.getPlanId());
                    }
                } else {
                    //2.插入报名表
                    if (enrollService.singUp(enrollReq, sca.getCandidatesId(), sca.getStatus())) {
                        //3.修改状态
                        SpecialCertificationApplicantReq specialCertificationApplicantReq = new SpecialCertificationApplicantReq();
                        BeanUtils.copyProperties(sca, specialCertificationApplicantReq);
                        specialCertificationApplicantReq.setStatus(dto.getStatus());
                        super.update(specialCertificationApplicantReq, sca.getId());
                        //4.发送短信
                        sms(examPlanName, phone, "已经审核通过!");
                    }
                }

            }
        }
        if (success)
            return R.status(false, "批量审核成功，部分考试计划报名人数已满，已自动改为审核不通过!");
        return R.status(true, "批量审核成功");
    }

    @Override
    //重写page
    public PageResp<SpecialCertificationApplicantResp> page(SpecialCertificationApplicantQuery query,
                                                            PageQuery pageQuery) {
        //根据mapper查出考生名
        //封装返回结果
        QueryWrapper<SpecialCertificationApplicantDO> queryWrapper = this.buildQueryWrapper(query);
        if (query.getCandidatesName() != null) {
            queryWrapper.like("su.nickname", query.getCandidatesName());
        }
        if (query.getExamPlanName() != null) {
            queryWrapper.like("tep.exam_plan_name", query.getExamPlanName());
        }

        queryWrapper.eq("tsca.is_deleted", 0);
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);
        // 执行分页查询
        IPage<SpecialCertificationApplicantDO> page = baseMapper.getSpecialCertification(new Page<>(pageQuery
            .getPage(), pageQuery.getSize()), queryWrapper);
        // 将查询结果转换成 PageResp 对象
        PageResp<SpecialCertificationApplicantResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    /**
     * @param req 修改参数
     * @param id  ID
     */
    @Override
    public R updateResult(SpecialCertificationApplicantReq req, Long id) {

        SpecialCertificationApplicantDO specialCertificationApplicantDO = baseMapper.selectById(id);
        EnrollReq enrollReq = new EnrollReq();
        enrollReq.setEnrollStatus(1L);
        enrollReq.setExamPlanId(specialCertificationApplicantDO.getPlanId());
        UserDO userDO = userMapper.selectById(specialCertificationApplicantDO.getCandidatesId());
        //3.2 获取手机号
        String phone = userDO.getPhone();
        //2.1获取考试名称和通过（默认）
        ExamPlanDO examPlanDO = examPlanMapper.selectById(specialCertificationApplicantDO.getPlanId());
        String examPlanName = examPlanDO.getExamPlanName();

        if (req.getStatus() == 1)//如果审核通过
        {
            //进入这个函数前确保不超过最大人数
            //            PlancalssroomDO seatNumber = planClassroomMapper.getSeatNumber(req.getPlanId());
            //            Long seatNumber = planClassroomMapper.getSeatNumber(req.getPlanId());
            //            ClassroomDO classroomDO = classroomMapper.selectById(seatNumber.getClassroomId());
            // 获取当前计划clasroom考场最大座位数
            long enrolledCount = classroomMapper.getPlanCount(req.getPlanId());//获取当前计划已经报名人数

            if (enrolledCount >= examPlanDO.getMaxCandidates()) {
                String reason = "审核不通过,原因是:该考试计划报名人数已满!";
                //发送短信+赋值失败
                smsList(examPlanName, String.valueOf(req.getPlanId()), reason);
                batchRejectApplications(req.getPlanId());
                return R.status(false, "该考试计划报名人数已满，已自动改为审核不通过!");

            } else {

                if (enrollService.singUp(enrollReq, specialCertificationApplicantDO
                    .getCandidatesId(), specialCertificationApplicantDO.getStatus())) {
                    // 更新报名表状态,发送短信显示报名成功
                    super.update(req, id);
                    sms(examPlanName, phone, "已经审核通过!");
                    return R.status(true, "报名成功");
                }
            }
        } else//审核未通过（直接把原因转发过去）
        {
            //发送短信
            String reason = req.getReason();

            sms(examPlanName, phone, "审核不通过,原因是:" + reason + "!");
            //更新报名表状态
            super.update(req, id);
            return R.status(true, "审核成功");
        }
        return R.status(true, "审核成功");

    }

    public void batchRejectApplications(Long planId) {
        LambdaUpdateWrapper<SpecialCertificationApplicantDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SpecialCertificationApplicantDO::getStatus, 0)
            .eq(SpecialCertificationApplicantDO::getPlanId, planId)
            .set(SpecialCertificationApplicantDO::getStatus, 2);
        baseMapper.update(null, wrapper);
    }

    /**
     * 批量处理超限人数的短信
     *
     * @param examPlanName
     * @param planId
     * @param reason
     */
    public void smsList(String examPlanName, String planId, String reason) {
        //3.捕获调用的异常后，直接剩下的未审核的同学发送失败短信
        //3.1 通过这个查询条件，获取所有未审核的考生id
        LambdaQueryWrapper<SpecialCertificationApplicantDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpecialCertificationApplicantDO::getStatus, 0);
        queryWrapper.eq(SpecialCertificationApplicantDO::getPlanId, planId);

        List<SpecialCertificationApplicantDO> specialCertificationApplicantDOList = baseMapper.selectList(queryWrapper);
        List<Long> candidatesIds = specialCertificationApplicantDOList.stream().map(item -> {
            return item.getCandidatesId();
        }).collect(Collectors.toList());
        //3.2 获取手机号
        QueryWrapper wrapper = new QueryWrapper<UserDO>().in("id", candidatesIds);
        List<UserDO> userDOList = userMapper.selectListByIds(wrapper);
        List<String> phones = userDOList.stream().map(item -> item.getPhone()).collect(Collectors.toList());//获取他们手机号

        //3.3构建短信

        Map<String, String> templates = new LinkedHashMap<>();
        templates.put("name", examPlanName);
        templates.put("flag", reason);
        String params = JSON.toJSONString(templates);
        String phoneList = String.join(",", phones);
        log.info("手机号：{}", phoneList);

        SendSmsRequest request = SendSmsRequest.builder()
            .phoneNumbers(phoneList)//手机号
            .signName(aliYunConfig.getSignName())
            .templateCode(aliYunConfig.getTemplateCodes().get(SmsConstants.ENROLLMENT_CONFIRMATION_TEMPLATE))
            .templateParam(params)//验证码
            .build();
        CompletableFuture<SendSmsResponse> future = smsAsyncClient.sendSms(request);
        future.thenAccept(response -> {
            if ("OK".equals(response.getBody().getCode())) {
                log.info("短信发送成功 | phone: {} | code: {}", phoneList, response.getBody().getCode());

            } else {
                log.error("短信发送失败 | phone: {} | code: {}", phoneList, response.getBody().getCode());
            }
        }).exceptionally(ex -> {
            log.error("短信发送异常 | phone: {}", phoneList, ex);
            return null;
        });
    }

    /**
     * 单个发送短信
     *
     * @param examPlanName
     * @param phone
     * @param reason
     */
    public void sms(String examPlanName, String phone, String reason) {
        //1.获取用户电话号码(默认是必须的)

        //3.3构建短信
        Map<String, String> templateParams = new LinkedHashMap<>();
        templateParams.put("name", examPlanName);
        templateParams.put("flag", reason);
        String params = JSON.toJSONString(templateParams);
        //2.2  构建短信
        SendSmsRequest request = SendSmsRequest.builder()
            .phoneNumbers(phone)//手机号
            .signName(aliYunConfig.getSignName())
            .templateCode(aliYunConfig.getTemplateCodes().get(SmsConstants.ENROLLMENT_CONFIRMATION_TEMPLATE))
            .templateParam(params)//验证码
            .build();
        //2.3异步发送
        CompletableFuture<SendSmsResponse> future = smsAsyncClient.sendSms(request);
        future.thenAccept(response -> {
            if ("OK".equals(response.getBody().getCode())) {
                log.info("短信发送成功 | phone: {} | code: {}", phone, response.getBody().getCode());

            } else {
                log.error("短信发送失败 | phone: {} | code: {}", phone, response.getBody().getCode());
            }
        }).exceptionally(ex -> {
            log.error("短信发送异常 | phone: {}", phone, ex);
            return null;
        });

    }

    @Override
    //重写查询get
    public SpecialCertificationApplicantDetailResp get(Long id) {
        SpecialCertificationApplicantDO entity = super.getById(id, false);
        SpecialCertificationApplicantDetailResp detail = BeanUtil.toBean(entity, this.getDetailClass());
        detail.setCandidatesName(userMapper.selectNicknameById(entity.getCandidatesId()));
        this.fill(detail);
        return detail;
    }

}