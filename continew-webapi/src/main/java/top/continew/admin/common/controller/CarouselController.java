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

package top.continew.admin.common.controller;

import top.continew.admin.common.model.resp.CarouselIndexResp;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.model.query.CarouselQuery;
import top.continew.admin.common.model.req.CarouselReq;
import top.continew.admin.common.model.resp.CarouselDetailResp;
import top.continew.admin.common.model.resp.CarouselResp;
import top.continew.admin.common.service.CarouselService;

import java.util.List;

/**
 * 轮播图管理管理 API
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
@Tag(name = "轮播图管理管理 API")
@RestController
@CrudRequestMapping(value = "/common/carousel", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class CarouselController extends BaseController<CarouselService, CarouselResp, CarouselDetailResp, CarouselQuery, CarouselReq> {

    /**
     * 考生首页获取轮播图
     */
    @GetMapping("/index")
    public List<CarouselIndexResp> index() {
        return baseService.index();
    }

}