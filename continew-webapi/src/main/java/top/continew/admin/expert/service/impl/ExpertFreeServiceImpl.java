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

package top.continew.admin.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.expert.mapper.ExpertFreeMapper;
import top.continew.admin.expert.mapper.ExpertOrgMapper;
import top.continew.admin.expert.model.*;
import top.continew.admin.expert.service.ExpertFreeService;
import top.continew.admin.training.mapper.ExpertMapper;
import top.continew.admin.training.model.entity.ExpertDO;
import top.continew.admin.util.RedisUtil;
import top.continew.admin.util.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Anton
 * @date 2025/4/27-11:29
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpertFreeServiceImpl implements ExpertFreeService {

    private final ExpertFreeMapper expertFreeMapper;
    @Resource
    private AESWithHMAC aesWithHMAC;
    @Resource
    private final ExpertMapper expertMapper;

    @Resource
    private final ExpertOrgMapper expertOrgMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public ExpertFreeRespTotalResp queryExpertFree(int pageSize, int curSize) {
        //1.查找组织id
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();
        Long organizetionId = expertFreeMapper.queryOrganizetionId(userId);
        //2.计算分页参数
        int offset = (curSize - 1) * pageSize;
        //3.查找总条数和页面数据
        Long total = expertFreeMapper.queryExpertFreeByProjectIdAndExpertIdTotal(organizetionId);
        List<ExpertFreeResp> expertFreeResps = expertFreeMapper
            .queryExpertFreeByProjectIdAndExpertId(organizetionId, pageSize, offset);
        ExpertFreeRespTotalResp expertFreeRespTotalResp = new ExpertFreeRespTotalResp();
        expertFreeRespTotalResp.setRes(expertFreeResps);
        expertFreeRespTotalResp.setTotal(total);
        return expertFreeRespTotalResp;
    }

    @Override
    public List<ExpertFreeShouldResp> queryExpertShouldPay(LocalDateTime begin, LocalDateTime end) {
        //1.查找组织id
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();
        Long organizetionId = expertFreeMapper.queryOrganizetionId(userId);
        //2.查找数据
        return expertFreeMapper.queryExpertShouldPay(organizetionId, begin, end);
    }

    /**
     * 根据id查询专家信息（包括费用）
     * 
     * @param id
     * @return
     */
    @Override
    public List<ExpertFreelistResp> getExpertFreeById(Long id) {
        return expertFreeMapper.getExpertFreeById(id);
    }

    /**
     * 新增专家信息
     * 
     * @param expert
     * @return
     */
    @Override
    public Result addExpert(Expert expert) {
        //1.判断是否为空
        if (expert == null) {
            return Result.error("参数错误");
        }
        //2.判定是否已存在(查身份证)
        if (expertMapper.isExpertExist(expert.getIdCard()) != 0) {
            return Result.error("专家已存在");
        }
        //3.对身份证格式和字数做检查
        if (!expert.getIdCard()
            .matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$")) {
            return Result.error("身份证格式错误");
        }
        expert.setIdCard(aesWithHMAC.encryptAndSign(expert.getIdCard()));
        //4.插入专家数据，并关联相关机构数据
        addExpertWithOrgRelation(expert);
        redisUtil.delete(RedisConstant.EXAM_EXPERT_QUERY);//删除缓存，后台做了这个
        return Result.success("插入成功");
    }

    /**
     * 根据id查询专家信息(返回特定数据)
     * 
     * @param id
     * @return
     */
    //查询信息
    @Override
    public ExpertFreeList getExpert(Long id) {
        ExpertDO expertDO = expertMapper.selectById(id);
        ExpertFreeList expertDO1 = new ExpertFreeList();
        //复制数据
        BeanUtils.copyProperties(expertDO, expertDO1);
        return expertDO1;
    }

    /**
     * 修改专家信息
     * 
     * @param id
     * @param expert
     * @return
     */
    @Override
    public Result updateExpert(Long id, Expert expert) {
        //直接更新
        ExpertDO expertDO = new ExpertDO();

        BeanUtils.copyProperties(expert, expertDO);
        expertDO.setId(id);
        expertDO.setUpdateUser(TokenLocalThreadUtil.get().getUserId());
        expertMapper.updateById(expertDO);
        redisUtil.delete(RedisConstant.EXAM_EXPERT_QUERY);//删除缓存，后台做了这个
        return Result.success("更新成功");
    }

    @Override
    public Result updateExpertFree(Long id, ExpertFreelistResp expert) {
        // ExpertFreelistResp expertDO = new ExpertFreelistResp();
        //BeanUtil.copyProperties(expert,expertDO);
        if (expert == null)
            return Result.error("参数错误");
        //设置时间为2025-04-29 14:52:42格式
        if (expert.getStatus() == 2)
            expert.setPayCompletionTime(LocalDateTime.now());
        expertFreeMapper.updateExpertFree(id, expert);
        return Result.success("更新成功");
    }

    /**
     * 导出选中的专家
     * 
     * @param request
     * @return
     */
    @Override
    public List<ExpertFreeShouldResp> exportSelectedExperts(ExpertFreeShouldReq request) {

        //1.查找组织id
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();
        Long organizetionId = expertFreeMapper.queryOrganizetionId(userId);
        //2.查找数据(wrapper)
        QueryWrapper<ExpertFreeShouldResp> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("tef.expert_id", request.getExpertIds()).eq("tef.organization_id", organizetionId);

        // 动态添加 status 条件
        if (request.getStatus() != 3) {
            queryWrapper.eq("tef.status", request.getStatus());
        }
        return expertFreeMapper.queryExpertSelectedPay(queryWrapper);
    }

    @Transactional
    public void addExpertWithOrgRelation(Expert expert) {
        // 插入操作
        ExpertOrg expertOrg = new ExpertOrg();
        expertMapper.insertexper(expert);
        //3.1获取当前机构id
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();
        Long organizetionId = expertFreeMapper.queryOrganizetionId(userId);
        expertOrg.setOrgId(organizetionId);
        expertOrg.setExpertId(expert.getId());
        expertOrg.setCreateUser(userId);
        expertOrg.setUpdateUser(userId);
        expertOrg.setIsDeleted(0L);
        expertOrgMapper.insert(expertOrg);

    }

}
