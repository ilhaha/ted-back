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

package top.continew.admin.controller.system;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.auth.model.entity.RcDistrictDO;
import top.continew.admin.auth.service.RcDistrictService;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.model.resp.AddressResp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton
 * @date 2025/3/12-11:01
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RcDistrictService rcDistrictService;

    /**
     * 获取省市区树形
     * 
     * @return
     */
    @GetMapping("/ilhaha/provinces")
    @Operation(summary = "获取省份列表", description = "获取省份列表")
    public List<RcDistrictDO> ilhahaProvinces() {
        return rcDistrictService.ilhahaProvinces();
    }

    private List<RcDistrictDO> getCacheData(boolean flag) {
        List<RcDistrictDO> level1 = (List<RcDistrictDO>)redisTemplate.opsForValue().get(RedisConstant.PROVINCES_KEY);

        if (level1 == null || level1.isEmpty()) {
            if (flag)
                rcDistrictService.redisInstallLevel1Data();
            else
                rcDistrictService.redisInstallAllData();
            level1 = (List<RcDistrictDO>)redisTemplate.opsForValue().get(RedisConstant.PROVINCES_KEY);
        }
        return level1;
    }

    @GetMapping("/provinces")
    @Operation(summary = "获取省份列表", description = "获取省份列表")
    public List<AddressResp> provinces() {
        List<RcDistrictDO> level1 = getCacheData(true);

        List<AddressResp> results = new ArrayList<>();

        level1.forEach(item -> {
            AddressResp addressResp = new AddressResp();
            addressResp.setValue(item.getDistrictId());
            addressResp.setLabel(item.getDistrict());
            results.add(addressResp);
        });

        return results;
    }

    @GetMapping("/getChild")
    @Operation(summary = "通过pid获取所有下属地址", description = "通过pid获取所有下属地址")
    public List<AddressResp> getChild(@RequestParam("pid") Long pid) {
        List<RcDistrictDO> list = getCacheData(false);

        List<AddressResp> results = new ArrayList<>();

        list.forEach(item -> {
            if (item.getPid().equals(pid)) {
                AddressResp addressResp = new AddressResp();
                addressResp.setValue(item.getDistrictId());
                addressResp.setLabel(item.getDistrict());
                results.add(addressResp);
            }
        });

        return results;
    }

}
