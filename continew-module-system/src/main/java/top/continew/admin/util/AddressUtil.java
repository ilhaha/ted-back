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

package top.continew.admin.util;

import cn.crane4j.core.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.continew.admin.auth.service.RcDistrictService;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.model.resp.AddressInfoResp;
import top.continew.admin.auth.model.entity.RcDistrictDO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Anton
 * @date 2025/3/12-16:39
 */
@Component
@Slf4j
public class AddressUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RcDistrictService rcDistrictService;

    public AddressInfoResp getIds(Long provinceId, Long cityId, Long areaId) {
        // 1. 优化缓存获取逻辑
        List<RcDistrictDO> districtList = getCachedDistricts();

        // 2. 构建快速查找结构
        Map<Long, RcDistrictDO> districtMap = districtList.stream()
            .collect(Collectors.toMap(RcDistrictDO::getDistrictId, Function.identity()));

        AddressInfoResp resp = new AddressInfoResp();
        Optional.ofNullable(provinceId)
            .filter(id -> !id.equals(0L))
            .map(districtMap::get)
            .ifPresent(p -> resp.setProvinceName(p.getDistrict()));

        Optional.ofNullable(cityId).map(districtMap::get).ifPresent(c -> resp.setCityName(c.getDistrict()));

        Optional.ofNullable(areaId).map(districtMap::get).ifPresent(a -> resp.setAreaName(a.getDistrict()));

        return resp;
    }

    private List<RcDistrictDO> getCachedDistricts() {
        List<RcDistrictDO> list = (List<RcDistrictDO>)redisTemplate.opsForValue().get(RedisConstant.ALL_ADDRESS_KEY);

        if (CollectionUtils.isEmpty(list)) {
            log.warn("从Redis中保存并获取地址缓存");
            rcDistrictService.redisInstallAllData();
            list = (List<RcDistrictDO>)redisTemplate.opsForValue().get(RedisConstant.ALL_ADDRESS_KEY);

            if (CollectionUtils.isEmpty(list)) {
                throw new ServiceException("Redis缓存地址信息失败!");
            }
        }
        return list;
    }

}