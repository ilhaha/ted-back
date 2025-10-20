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

package top.continew.admin.expert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.expert.model.*;
import top.continew.admin.expert.service.ExpertFreeService;
import top.continew.admin.util.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Anton
 * @date 2025/4/27-11:32
 */
@RestController
@RequestMapping("/expertFree")
@RequiredArgsConstructor
public class ExpertFreeController {
    private final ExpertFreeService expertFreeService;

    /**
     * 查询专家费用
     * 
     * @return
     */
    @GetMapping("/org")
    public ExpertFreeRespTotalResp getExpertFree(@RequestParam("current") Integer current, // 使用标准命名
                                                 @RequestParam("size") Integer size) {
        return expertFreeService.queryExpertFree(size, current);
    }

    /**
     * 查询某段时间内应该支付的专家费用
     * 
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/Between")
    public List<ExpertFreeShouldResp> queryExpertShouldPay(@RequestParam LocalDateTime begin,
                                                           @RequestParam LocalDateTime end) {
        return expertFreeService.queryExpertShouldPay(begin, end);
    }

    @GetMapping("/{id}")
    public List<ExpertFreelistResp> getExpertFreeById(@PathVariable("id") Long id) {
        return expertFreeService.getExpertFreeById(id);
    }

    //机构新增一个专家
    @PostMapping("/addexpert")
    public Result addExpert(@RequestBody Expert expert) {
        return expertFreeService.addExpert(expert);
    }

    //查询专家
    @GetMapping("/list/{id}")
    public ExpertFreeList getExpert(@PathVariable("id") Long id) {
        return expertFreeService.getExpert(id);
    }

    //修改专家
    @PutMapping("/update/{id}")
    public Result updateExpert(@PathVariable Long id, @RequestBody @Validated Expert expert) {
        return expertFreeService.updateExpert(id, expert);
    }

    //修改专家费用
    @PutMapping("/updateFree/{id}")
    public Result updateExpertFree(@PathVariable Long id, @RequestBody @Validated ExpertFreelistResp expert) {
        return expertFreeService.updateExpertFree(id, expert);
    }

    @PostMapping("/export/selected")
    public List<ExpertFreeShouldResp> exportSelectedExperts(@RequestBody ExpertFreeShouldReq request) {

        return expertFreeService.exportSelectedExperts(request);
    }

}
