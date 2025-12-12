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

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.training.mapper.OrgCandidateMapper;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.mapper.TrainingMapper;
import top.continew.admin.training.model.dto.TrainingCheckinExportDTO;
import top.continew.admin.training.model.entity.*;
import top.continew.admin.util.SignUtil;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.TrainingCheckinMapper;
import top.continew.admin.training.model.query.TrainingCheckinQuery;
import top.continew.admin.training.model.req.TrainingCheckinReq;
import top.continew.admin.training.model.resp.TrainingCheckinDetailResp;
import top.continew.admin.training.model.resp.TrainingCheckinResp;
import top.continew.admin.training.service.TrainingCheckinService;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 培训签到记录业务实现
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
@Service
@RequiredArgsConstructor
public class TrainingCheckinServiceImpl extends BaseServiceImpl<TrainingCheckinMapper, TrainingCheckinDO, TrainingCheckinResp, TrainingCheckinDetailResp, TrainingCheckinQuery, TrainingCheckinReq> implements TrainingCheckinService {

    @Autowired
    private TrainingCheckinMapper trainingCheckinMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrgCandidateMapper orgCandidateMapper;
    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private OrgUserMapper orgUserMapper;
    @Value("${qrcode.training.checkin.url}")
    private String trainingCheckinUrl;

    @Resource
    private TrainingMapper trainingMapper;

    @Override
    public String generateQRCode(Long trainingId) {

        Long userId = TokenLocalThreadUtil.get().getUserId();

        TedOrgUser org = orgUserMapper.selectOne(new LambdaQueryWrapper<TedOrgUser>().eq(TedOrgUser::getUserId, userId)
            .select(TedOrgUser::getOrgId)
            .last("LIMIT 1"));

        long ts = System.currentTimeMillis();
        String sign = SignUtil.sign(ts);

        String url = String.format("%s?trainingId=%s&orgId=%s&ts=%s&sign=%s", trainingCheckinUrl, trainingId, org
            .getOrgId(), ts, sign);

        return url;
    }

    @Override
    public boolean doCheckin(String realName, String idCard, Long trainingId, Long orgId, Long ts, String sign) {

        // 先校验二维码是否被伪造
        if (!SignUtil.verify(ts, sign)) {
            throw new BusinessException("二维码参数无效（疑似伪造）");
        }

        //  校验是否过期（3分钟）
        if (System.currentTimeMillis() - ts > 3 * 60 * 1000) {
            throw new BusinessException("二维码已过期，请刷新二维码");
        }

        //加密身份证号
        String encryptedIdcard = aesWithHMAC.encryptAndSign(idCard);
        UserDO user = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getNickname, realName)
            .eq(UserDO::getUsername, encryptedIdcard)
            .last("LIMIT 1"));

        if (user == null) {
            throw new BusinessException("姓名或身份证不匹配");
        }

        Long userId = user.getId();
        //判断是否属于该培训
        OrgCandidateDO cand = orgCandidateMapper.selectOne(new LambdaQueryWrapper<OrgCandidateDO>()
            .eq(OrgCandidateDO::getCandidateId, userId)
            .eq(OrgCandidateDO::getOrgId, orgId)
            .eq(OrgCandidateDO::getStatus, 2)
            .last("LIMIT 1"));

        if (cand == null) {
            throw new BusinessException("你未报名此培训机构，无法签到");
        }

        // 今天开始时间和结束时间
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // 查询当天是否已签到
        TrainingCheckinDO existed = trainingCheckinMapper.selectOne(new LambdaQueryWrapper<TrainingCheckinDO>()
            .eq(TrainingCheckinDO::getTrainingId, trainingId)
            .eq(TrainingCheckinDO::getCandidateId, userId)
            .ge(TrainingCheckinDO::getCheckinTime, startOfDay)
            .le(TrainingCheckinDO::getCheckinTime, endOfDay)
            .last("LIMIT 1"));

        if (existed != null) {
            throw new BusinessException("你今天已签到，无需重复签到");
        }

        // 插入签到记录
        TrainingCheckinDO checkin = new TrainingCheckinDO();
        checkin.setTrainingId(trainingId);
        checkin.setCandidateId(userId);
        checkin.setOrgId(orgId);
        checkin.setQrTimestamp(ts);
        checkin.setQrSign(sign);
        checkin.setStatus(1);
        checkin.setCreateUser(userId);
        checkin.setUpdateUser(userId);

        trainingCheckinMapper.insert(checkin);

        return true;
    }

    @Override
    public void exportExcel(TrainingCheckinQuery query, HttpServletResponse response) {
        // 查询签到记录
        LambdaQueryWrapper<TrainingCheckinDO> wrapper = new LambdaQueryWrapper<>();
        if (query.getTrainingId() != null) {
            wrapper.eq(TrainingCheckinDO::getTrainingId, query.getTrainingId());
        }
        if (query.getCheckinTime() != null) {
            // 前端传的是 LocalDate
            LocalDate date = query.getCheckinTime().toLocalDate();
            wrapper.ge(TrainingCheckinDO::getCheckinTime, date.atStartOfDay());
            wrapper.le(TrainingCheckinDO::getCheckinTime, date.atTime(23, 59, 59));
        }
        List<TrainingCheckinDO> checkinList = trainingCheckinMapper.selectList(wrapper);

        if (checkinList.isEmpty()) {
            throw new BusinessException("没有符合条件的签到记录");
        }

        // 查询用户信息
        Set<Long> candidateIds = checkinList.stream()
            .map(TrainingCheckinDO::getCandidateId)
            .collect(Collectors.toSet());
        List<UserDO> users = userMapper.selectBatchIds(candidateIds);
        Map<Long, UserDO> userMap = users.stream().collect(Collectors.toMap(UserDO::getId, u -> u));

        // 查询培训名称
        Set<Long> trainingIds = checkinList.stream().map(TrainingCheckinDO::getTrainingId).collect(Collectors.toSet());
        List<TrainingDO> trainings = trainingMapper.selectBatchIds(trainingIds);
        Map<Long, String> trainingMap = trainings.stream()
            .collect(Collectors.toMap(TrainingDO::getId, TrainingDO::getTitle));

        // 构造 DTO 列表，并自动生成序号
        List<TrainingCheckinExportDTO> exportList = new ArrayList<>();
        for (int i = 0; i < checkinList.size(); i++) {
            TrainingCheckinDO record = checkinList.get(i);
            UserDO user = userMap.get(record.getCandidateId());
            TrainingCheckinExportDTO dto = new TrainingCheckinExportDTO();
            dto.setIndex(i + 1); // 序号从 1 开始
            dto.setTrainingName(trainingMap.get(record.getTrainingId()));
            dto.setCandidateName(user != null ? user.getNickname() : "");
            dto.setCheckinTime(record.getCheckinTime()); // 原始 LocalDateTime
            exportList.add(dto);
        }

        // 导出 Excel
        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {

            // 设置表头别名
            writer.addHeaderAlias("index", "序号");
            writer.addHeaderAlias("trainingName", "培训课程名称");
            writer.addHeaderAlias("candidateName", "考生姓名");
            writer.addHeaderAlias("checkinTime", "签到时间");

            writer.setOnlyAlias(true);

            writer.write(exportList, true);
            String fileName = URLEncoder.encode("培训签到记录.xlsx", "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            writer.flush(response.getOutputStream(), true);
        } catch (Exception e) {
            throw new RuntimeException("导出 Excel 失败", e);
        }
    }

}