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

package top.continew.admin.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import top.continew.admin.auth.AddressConst;
import top.continew.admin.auth.mapper.RcDistrictMapper;
import top.continew.admin.auth.model.entity.RcDistrictDO;
import top.continew.admin.auth.service.RcDistrictService;
import top.continew.admin.common.constant.RedisConstant;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 地区 Mapper
 */
@Service
public class RcDistrictServiceImpl implements RcDistrictService {

    @Autowired
    private RcDistrictMapper rcDistrictMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存所有省份列表
     */
    @Override
    public void redisInstallLevel1Data() {
        // 通过level等级查询
        List<RcDistrictDO> level1 = rcDistrictMapper.selectAllDistricts(1L);

        redisTemplate.opsForValue()
            .set(RedisConstant.PROVINCES_KEY, level1, RedisConstant.FIFTEEN_DAYS, TimeUnit.SECONDS);
    }

    /**
     * 缓存所有地址列表
     */
    @Override
    public void redisInstallAllData() {
        // 通过level等级查询
        List<RcDistrictDO> rDo = rcDistrictMapper.selectList(null);

        redisTemplate.opsForValue()
            .set(RedisConstant.ALL_ADDRESS_KEY, rDo, RedisConstant.FIFTEEN_DAYS, TimeUnit.SECONDS);
    }

    /**
     * 获取省市区树形
     * 
     * @return
     */
    @Override
    public List<RcDistrictDO> ilhahaProvinces() {
        // 1. 首先检查 Redis 是否缓存了数据
        List<RcDistrictDO> result = (List<RcDistrictDO>)redisTemplate.opsForValue().get(RedisConstant.ADDRESS_TREE);
        if (!ObjectUtils.isEmpty(result)) {
            return result;
        }

        // 2. 如果 Redis 中没有数据，从数据库查询省市区数据
        List<RcDistrictDO> ilhahaProvincesDB = rcDistrictMapper.ilhahaProvinces();

        // 3. 构建树形结构
        if (!ObjectUtils.isEmpty(ilhahaProvincesDB)) {
            List<RcDistrictDO> tree = buildTree(ilhahaProvincesDB);
            // 4. 将构建好的树形结构缓存到 Redis 中
            redisTemplate.opsForValue().set(RedisConstant.ADDRESS_TREE, tree);
            return tree;
        }

        // 如果数据库和 Redis 都没有数据，返回空列表
        return Collections.emptyList();
    }

    /**
     * 构建树形
     * 
     * @param districts
     * @return
     */
    public List<RcDistrictDO> buildTree(List<RcDistrictDO> districts) {
        // 创建一个 Map 用来存储所有地区，键是 districtId，值是 AddressTreeVO 对象
        Map<Long, RcDistrictDO> map = new HashMap<>();
        // 存储顶级地区的列表（省级）
        List<RcDistrictDO> rootDistricts = new ArrayList<>();

        // 先将所有地区存入 Map，键为 districtId
        for (RcDistrictDO district : districts) {
            map.put(district.getDistrictId(), district);
        }

        // 遍历所有地区，构建父子关系
        for (RcDistrictDO district : districts) {
            if (district.getPid() == AddressConst.DEFAULT_PID) {
                // 顶级地区，添加到根列表中
                rootDistricts.add(district);
            } else {
                // 子级地区，添加到对应父级地区的 child 列表中
                RcDistrictDO parent = map.get(district.getPid());
                if (parent != null) {
                    parent.addChild(district);
                }
            }
        }

        return rootDistricts;
    }
}
