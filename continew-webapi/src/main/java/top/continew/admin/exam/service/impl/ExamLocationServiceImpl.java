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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.exam.model.vo.PlanLocationAndRoomVO;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamLocationMapper;
import top.continew.admin.exam.model.entity.ExamLocationDO;
import top.continew.admin.exam.model.query.ExamLocationQuery;
import top.continew.admin.exam.model.req.ExamLocationReq;
import top.continew.admin.exam.model.resp.ExamLocationDetailResp;
import top.continew.admin.exam.model.resp.ExamLocationResp;
import top.continew.admin.exam.service.ExamLocationService;

import java.util.List;

/**
 * 考试地点业务实现
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
@Service
@RequiredArgsConstructor
public class ExamLocationServiceImpl extends BaseServiceImpl<ExamLocationMapper, ExamLocationDO, ExamLocationResp, ExamLocationDetailResp, ExamLocationQuery, ExamLocationReq> implements ExamLocationService {

    @Resource
    private ExamLocationMapper examLocationMapper;

    @Override
    public PageResp<ExamLocationResp> page(ExamLocationQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamLocationDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("is_deleted", 0);
        //todo 部门模糊查询
        super.sort(queryWrapper, pageQuery);

        IPage<ExamLocationDetailResp> page = baseMapper.selectExamLocationPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        PageResp<ExamLocationResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 重写获取考试详情
     */
    @Override
    public ExamLocationDetailResp get(Long id) {
        return examLocationMapper.getExamLocationDetail(id);
    }

    /**
     * 根据计划id获取计划对应的考试地点和考场信息
     * 
     * @param planId
     * @return
     */
    @Override
    public List<PlanLocationAndRoomVO> getPlanLocationAndRoomByPlanId(Long planId) {
        return baseMapper.getPlanLocationAndRoom(planId);
    }

    //    @Override
    //    public Page<ExamLocationResp> page(PageQuery pageQuery) {
    //        //创建查询条件对象
    //        ExamLocationSelect select =new ExamLocationSelect(pageQuery);
    //        // 创建分页对象
    //        Page<ExamLocationResp> page = new Page<>(select.getPage(),select.getSize());
    //        // 调用Mapper层方法，查询考试地点列表
    //        Page<ExamLocationResp> allExamLocation = examLocationMapper.getAllExamLocation(page, select);
    //        allExamLocation.getRecords().forEach(
    //                examLocationResp -> {
    //                    //根据城市id查询
    //                    Map<String, String> districtFromRedis = getDistrictFromRedis(examLocationResp.getCityId());
    //                    examLocationResp.setCityName(districtFromRedis.get("cityName"));
    //                    examLocationResp.setProvinceName(districtFromRedis.get("provinceName"));
    //                }
    //        );
    //        return allExamLocation;
    //    }
    //

    /**
     * 根据省份id在redis中查询出一级城市 从而查出其子类城市对应信息
     */
    //    public Map<String, String> getDistrictFromRedis(Long CityId){
    //        List<AddressTreeVO> cityDos = (List<AddressTreeVO>) redisTemplate.opsForValue().get(RedisConstant.PROVINCES_KEY);
    //
    //        if (cityDos == null || cityDos.isEmpty()) {
    //            rcDistrictService.redisInstallData();
    //            cityDos = (List<AddressTreeVO>) redisTemplate.opsForValue().get(RedisConstant.PROVINCES_KEY);
    //        }
    //
    //        //2.处理缓存未命中
    //
    //        return result;
    //    }

}