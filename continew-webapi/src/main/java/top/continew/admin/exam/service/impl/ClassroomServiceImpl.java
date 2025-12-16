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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.service.ExamPlanService;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.query.ClassroomQuery;
import top.continew.admin.exam.model.req.ClassroomReq;
import top.continew.admin.exam.service.ClassroomService;

import java.util.List;

/**
 * 考场业务实现
 *
 * @author Anton
 * @since 2025/05/14 16:34
 */
@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl extends BaseServiceImpl<ClassroomMapper, ClassroomDO, ClassroomResp, ClassroomDetailResp, ClassroomQuery, ClassroomReq> implements ClassroomService {

    private final LocationClassroomMapper locationClassroomMapper;

    private final ClassroomMapper classroomMapper;

    @Resource
    private ExamPlanClassroomMapper examPlanClassroomMapper;

    private final RedissonClient redissonClient;

    private final ExamPlanService examPlanService;

    @Resource
    private ExamPlanMapper examPlanMapper;

    private final PlanClassroomMapper planClassroomMapper;

    @Override
    public PageResp<ClassroomResp> page(ClassroomQuery query, PageQuery pageQuery) {
        QueryWrapper<ClassroomDO> wrapper = this.buildQueryWrapper(query);
        wrapper.eq("tc.is_deleted", 0);
        super.sort(wrapper, pageQuery);
        IPage<ClassroomResp> page = baseMapper.selectExamLocation(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), wrapper);
        PageResp<ClassroomResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    @Override
    public List<ExamLocationResp> getExamLocation() {
        return baseMapper.getExamLocation();
    }

    @Override
    public List<ClassroomResp> getClassroomList(Long planId) {
        return classroomMapper.getClassroomList(planId);
    }

    @Override
    public ClassroomDetailResp get(Long id) {
        return baseMapper.getClassroomDeteil(id);
    }

    @Override
    public Long add(ClassroomReq req) {
        //1.插入考试教师表
        Long classroomId = super.add(req);
        //2.还需插入考场地点和考试教室表
        LocationClassroomDO locationClassroomDO = new LocationClassroomDO();
        locationClassroomDO.setClassroomId(Math.toIntExact(classroomId));
        locationClassroomDO.setLocationId(Math.toIntExact(req.getExamLocationId()));
        locationClassroomMapper.insert(locationClassroomDO);
        return classroomId;
    }

    @Override
    public void delete(List<Long> ids) {
        super.delete(ids);
        //构建条件删除关联表
        QueryWrapper<LocationClassroomDO> wrapper = new QueryWrapper<>();
        wrapper.in("classroom_id", ids);
        locationClassroomMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ClassroomReq req, Long id) {
        // 1. 查询旧考场
        ClassroomDO oldClassroom = classroomMapper.selectById(id);
        ValidationUtils.throwIfNull(oldClassroom, "未找到对应的考场记录");

        //        Long oldMax = Optional.ofNullable(oldClassroom.getMaxCandidates()).orElse(0L);
        //        Long newMax = Optional.ofNullable(req.getMaxCandidates()).orElse(0L);
        //
        //        // 2. 如果新容量 < 旧容量，需要校验是否已被确认考试计划占用
        //        if (newMax < oldMax) {
        //
        //            // 查该考场绑定了哪些考试计划
        //            List<Long> planIds = planClassroomMapper.selectList(new LambdaQueryWrapper<PlancalssroomDO>()
        //                .select(PlancalssroomDO::getPlanId)
        //                .eq(PlancalssroomDO::getClassroomId, id)).stream().map(PlancalssroomDO::getPlanId).toList();
        //
        //            if (CollUtil.isNotEmpty(planIds)) {
        //
        //                // 查是否有已确认考试的计划
        //                boolean hasConfirmed = examPlanMapper.exists(new LambdaQueryWrapper<ExamPlanDO>()
        //                    .in(ExamPlanDO::getId, planIds)
        //                    .eq(ExamPlanDO::getStatus, ExamPlanStatusEnum.IN_FORCE.getValue()));
        //
        //                ValidationUtils.throwIf(hasConfirmed, "该考场已有已确认的考试计划，容量不能设置得小于原容量：" + oldMax);
        //            }
        //        }
        //
        //        boolean capacityChanged = !newMax.equals(oldMax);
        //
        //        //  若容量变化则批量更新 plan
        //        if (capacityChanged) {
        //            List<Long> planIdList = examPlanClassroomMapper.selectByClassroomId(id)
        //                .stream()
        //                .map(ExamPlanClassroomDO::getPlanId)
        //                .filter(Objects::nonNull)
        //                .distinct()
        //                .toList();
        //
        //            if (!planIdList.isEmpty()) {
        //                String lockKey = "lock:exam_plan_update:classroom_" + id;
        //                RLock lock = redissonClient.getLock(lockKey);
        //                try {
        //                    if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
        //                        //  构造批量更新数据
        //                        List<ExamPlanDTO> updates = planIdList.stream().map(planId -> {
        //                            ExamPlanDO plan = examPlanMapper.selectById(planId);
        //                            if (plan == null)
        //                                return null;
        //                            int currentMax = plan.getMaxCandidates() != null ? plan.getMaxCandidates() : 0;
        //                            int updatedMax = (int)Math.max(currentMax - oldMax + newMax, 0L);
        //                            return new ExamPlanDTO(planId, (long)updatedMax);
        //                        }).filter(Objects::nonNull).collect(Collectors.toList());
        //
        //                        //  一次性批量更新
        //                        if (!updates.isEmpty()) {
        //                            examPlanService.batchUpdatePlanMaxCandidates(updates);
        //                        }
        //
        //                    } else {
        //                        throw new BusinessException("系统繁忙，请稍后再试");
        //                    }
        //                } catch (InterruptedException e) {
        //                    Thread.currentThread().interrupt();
        //                    throw new BusinessException("获取分布式锁失败");
        //                } finally {
        //                    if (lock.isHeldByCurrentThread()) {
        //                        lock.unlock();
        //                    }
        //                }
        //            }
        //        }

        // 更新考场信息
        ClassroomDO update = new ClassroomDO();
        BeanUtils.copyProperties(req, update);
        update.setId(id);
        classroomMapper.updateById(update);

        //  更新考点关联
        if (req.getExamLocationId() != null) {
            UpdateWrapper<LocationClassroomDO> wrapper = new UpdateWrapper<>();
            wrapper.set("location_id", req.getExamLocationId()).eq("classroom_id", id);
            locationClassroomMapper.update(null, wrapper);
        }
    }
}
