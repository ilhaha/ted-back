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

import cn.crane4j.core.util.CollectionUtils;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.expert.mapper.ExpertFreeMapper;
import top.continew.admin.expert.model.entity.TedExpertFree;
import top.continew.admin.system.mapper.RoleMapper;
import top.continew.admin.system.mapper.UserRoleMapper;
import top.continew.admin.system.model.entity.RoleDO;
import top.continew.admin.system.model.entity.UserRoleDO;
import top.continew.admin.training.mapper.*;
import top.continew.admin.training.model.entity.*;
import top.continew.admin.training.model.req.StudyTimeRecordReq;
import top.continew.admin.training.model.req.TrainTreeReq;
import top.continew.admin.training.model.resp.StudyTimeRecordResp;
import top.continew.admin.training.model.resp.TrainTreeResp;
import top.continew.admin.training.model.vo.ExpertVO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.model.query.TrainingQuery;
import top.continew.admin.training.model.req.TrainingReq;
import top.continew.admin.training.model.resp.TrainingDetailResp;
import top.continew.admin.training.model.resp.TrainingResp;
import top.continew.admin.training.service.TrainingService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 培训主表业务实现
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl extends BaseServiceImpl<TrainingMapper, TrainingDO, TrainingResp, TrainingDetailResp, TrainingQuery, TrainingReq> implements TrainingService {

    private static final Lock watchRecordLock = new ReentrantLock();

    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private WatchRecordMapper watchRecordMapper;

    @Autowired
    private StudentTrainingMapper studentTrainingMapper;
    @Autowired
    private TrainingMapper trainingMapper;

    @Autowired
    private EnrollMapper enrollMapper;
    @Autowired
    private ExamPlanMapper examPlanMapper;
    @Autowired
    private ProjectTrainingMapper projectTrainingMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private OrgExpertMapper orgExpertMapper;

    private final RoleMapper roleMapper;
    private final ExpertMapper expertMapper;
    private final ExpertFreeMapper expertFreeMapper;
    private final OrgUserMapper orgUserMapper;
    private final OrgTrainingMapper1 orgTrainingMapper1;

    @Override
    public TrainingDetailResp get(Long id) {
        TrainingDetailResp trainingDetailResp = super.get(id);
        LambdaQueryWrapper<ProjectTrainingDO> projectTrainingDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectTrainingDOLambdaQueryWrapper.eq(ProjectTrainingDO::getTrainingId, id);
        ProjectTrainingDO projectTrainingDO = projectTrainingMapper.selectOne(projectTrainingDOLambdaQueryWrapper);
        trainingDetailResp.setProjectId(projectTrainingDO.getProjectId());
        return trainingDetailResp;
    }

    //获取树形结构
    @Override
    public TrainTreeResp getTree(TrainTreeReq trainTreeReq) {
        Long userId = UserContextHolder.getUserId();
        if (ObjectUtil.isEmpty(trainTreeReq) || ObjectUtil.isEmpty(trainTreeReq.getTedTrainingId())) {
            throw new RuntimeException("参数不能为空");
        }
        //培训id
        Long tedTrainingId = trainTreeReq.getTedTrainingId();

        // **一次性查询所有章节**
        List<ChapterDO> allChapters = chapterMapper.selectList(new LambdaQueryWrapper<ChapterDO>()
            .eq(ChapterDO::getTrainingId, tedTrainingId));

        if (CollectionUtils.isEmpty(allChapters)) {
            TrainTreeResp resp = new TrainTreeResp();
            resp.setId(tedTrainingId);
            resp.setChapters(Collections.emptyList());
            return resp;
        }
        // **一次性查询所有视频**
        List<VideoDO> allVideos = videoMapper.selectList(new LambdaQueryWrapper<VideoDO>().in(allChapters
            .isEmpty(), VideoDO::getChapterId, allChapters.stream().map(ChapterDO::getId).toList()));

        // **将视频数据映射到章节**
        Map<Long, List<VideoDO>> videoMap = allVideos.stream().collect(Collectors.groupingBy(VideoDO::getChapterId));

        // **构建章节树（改用迭代方式）**
        List<ChapterDO> chapterTree = buildChapterTree(allChapters, videoMap);

        TrainTreeResp resp = new TrainTreeResp();
        resp.setId(tedTrainingId);
        resp.setChapters(chapterTree);

        return resp;
    }

    @Override
    public TrainTreeResp getStuTree(TrainTreeReq trainTreeReq) {

        Long userId = UserContextHolder.getUserId();
        if (ObjectUtil.isEmpty(trainTreeReq) || ObjectUtil.isEmpty(trainTreeReq.getTedTrainingId())) {
            throw new RuntimeException("参数不能为空");
        }
        //培训id
        Long tedTrainingId = trainTreeReq.getTedTrainingId();

        // **一次性查询所有章节**
        List<ChapterDO> allChapters = chapterMapper.selectList(new LambdaQueryWrapper<ChapterDO>()
            .eq(ChapterDO::getTrainingId, tedTrainingId));

        if (CollectionUtils.isEmpty(allChapters)) {
            TrainTreeResp resp = new TrainTreeResp();
            resp.setId(tedTrainingId);
            resp.setChapters(Collections.emptyList());
            return resp;
        }
        //查询视频观看记录
        List<WatchRecordDO> watchRecordDOS = watchRecordMapper.selectList(new LambdaQueryWrapper<WatchRecordDO>()
            .eq(WatchRecordDO::getStudentId, userId));
        //封装map
        Map<Long, WatchRecordDO> recordDOMap = watchRecordDOS.stream()
            .collect(Collectors.toMap(WatchRecordDO::getVideoId, record -> record));

        // **一次性查询所有视频**
        List<VideoDO> allVideos = videoMapper.selectList(new LambdaQueryWrapper<VideoDO>().in(allChapters
            .isEmpty(), VideoDO::getChapterId, allChapters.stream().map(ChapterDO::getId).toList()));

        //统计每个视频的学习时长  及百分比
        allVideos = allVideos.stream().peek(item -> {
            Long videoId = item.getId();
            WatchRecordDO watchRecordDO = recordDOMap.get(videoId);
            if (ObjectUtil.isEmpty(watchRecordDO)) {
                // 如果没有学习记录，设为 0%
                item.setLearningPercentage(0.0);
                item.setLearningTime(formatDuration(0));
            } else {
                Integer watchedDuration = watchRecordDO.getWatchedDuration();

                BigDecimal totalBigDecimal = BigDecimal.valueOf(item.getDuration());
                if (BigDecimal.ZERO.equals(totalBigDecimal)) {
                    throw new RuntimeException("视频时长为0");
                }

                BigDecimal watchedDurationBigDecimal = BigDecimal.valueOf(watchedDuration);
                item.setLearningTime(formatDuration(watchedDuration));
                item.setLearningPercentage(watchedDurationBigDecimal.divide(totalBigDecimal, 2, RoundingMode.HALF_UP) // 计算百分比，保留 2 位小数
                    .multiply(BigDecimal.valueOf(100)) // 转换为百分比
                    .doubleValue() // 转回 double
                );
            }
        }).collect(Collectors.toList());

        // **将视频数据映射到章节**
        Map<Long, List<VideoDO>> videoMap = allVideos.stream().collect(Collectors.groupingBy(VideoDO::getChapterId));

        // **构建章节树（改用迭代方式）**
        List<ChapterDO> chapterTree = buildChapterTree(allChapters, videoMap);

        TrainTreeResp resp = new TrainTreeResp();
        resp.setId(tedTrainingId);
        resp.setChapters(chapterTree);

        double overallPercentage = 0;

        TrainingDO trainingDO = trainingMapper.selectById(tedTrainingId);//查询培训计划
        int total = trainingDO.getTotalDuration();//总视频时长

        StudentTrainingDO studentTrainingDO = studentTrainingMapper
            .selectOne(new LambdaQueryWrapper<StudentTrainingDO>().eq(StudentTrainingDO::getStudentId, userId)
                .eq(StudentTrainingDO::getTrainingId, trainTreeReq.getTedTrainingId()));
        if (!ObjectUtil.isEmpty(studentTrainingDO)) {
            //学生学习时长
            int studied = studentTrainingDO.getTotalDuration();
            overallPercentage = total == 0
                ? 0.0
                : BigDecimal.valueOf(studied)
                    .multiply(BigDecimal.valueOf(100)) // 转换为百分比
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP) // 保留两位小数
                    .doubleValue();

        }

        resp.setLearningTime(formatDuration(total));
        resp.setLearningPercentage(overallPercentage);
        return resp;
    }

    //获取所有培训计划
    @Override
    public PageResp<TrainingResp> getAllTraining(TrainingQuery query, PageQuery pageQuery) {

        Long userId = UserContextHolder.getUserId();

        // 构建查询条件，根据传入的 query 参数动态生成 QueryWrapper
        QueryWrapper<TrainingDO> queryWrapper = this.buildQueryWrapper(query);

        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询，获取 TrainingResp 类型的分页数据
        IPage<TrainingResp> page = baseMapper.selectCurrentKSPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), // 创建分页对象，指定当前页和每页大小
            queryWrapper, // 查询条件
            userId // 用户 ID，可能用于权限控制
        );
        // 将查询结果转换成 PageResp 对象，方便前端处理
        PageResp<TrainingResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        //查出总学习时长
        StudentTrainingDO studentTrainingDO = studentTrainingMapper
            .selectOne(new LambdaQueryWrapper<StudentTrainingDO>().eq(StudentTrainingDO::getStudentId, userId)
                .eq(StudentTrainingDO::getIsDeleted, 0));
        pageResp.getList().forEach((item) -> {
            Integer totalStudyDuration = studentTrainingDO.getTotalDuration();
            //封装学习时长
            item.setLearningTime(formatDuration(totalStudyDuration));
            //封装百分比
            Integer totalDuration = item.getTotalDuration();
            double percentage = totalStudyDuration.doubleValue() / totalDuration.doubleValue();
            item.setLearningPercentage(percentage * 100);
        });
        // 返回分页查询结果
        return pageResp;

        //数据量大用下面的进行改造
        //登录用户id
        //        Long userId = UserContextHolder.getUserId();
        //        List<TrainingDO> result=new ArrayList<>();
        //        //获取登录用户所有考试计划
        //        List<EnrollDO> enrollDOS = enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
        //                .eq(EnrollDO::getUserId, userId).eq(EnrollDO::getIsDeleted,false));
        //        if(CollectionUtils.isEmpty(enrollDOS)){
        //
        //            return result;
        //        }
        //        List<Long> examPlanIds = enrollDOS.stream()
        //                .mapToLong(EnrollDO::getExamPlanId) // 映射到 examPlanId
        //                .boxed() // 将 LongStream 转换为 Stream<Long>
        //                .collect(Collectors.toList()); // 收集为 List<Long>
        //
        //        //查询考试计划对应项目 id集合
        //        List<Long> projectIds=examPlanMapper.selectAllProjectIds(examPlanIds);
        //
        //        if(CollectionUtils.isEmpty(projectIds)){
        //            return result;
        //        }
        //        //去查询这些项目下面的培训
        //        List<Long> trainingIds=projectTrainingMapper.selectTrainingIds(projectIds);
        //        if(CollectionUtils.isEmpty(trainingIds)){
        //            return result;
        //        }
        //        //根据培训id去查 具体的培训
        //        List<TrainingDO> trainingDOS = trainingMapper.selectByIds(trainingIds);
        //
        //
        //        return trainingDOS;
    }

    //更新视频的学习时长
    public Boolean updateStudyTimeRecord(StudyTimeRecordReq studyTimeRecordReq) {
        Long userId = UserContextHolder.getUserId();
        Long videoId = studyTimeRecordReq.getVideoId();
        Integer studyTime = studyTimeRecordReq.getStudyTime();

        // Redis Key
        String redisKey = RedisConstant.STUDY_TIME_RECOED;
        // Hash Key (字段：视频ID:用户ID)
        String hashKey = videoId + ":" + userId;

        // 累加学习时长
        // 使用 increment 方法自动处理插入和累加
        Integer redisStudyTime = (Integer)redisTemplate.opsForHash().get(redisKey, hashKey);
        if (redisStudyTime == null) {
            //1.缓存过期 需要从数据库从新加载
            QueryWrapper<WatchRecordDO> watchRecordDOQueryWrapper = new QueryWrapper<>();
            watchRecordDOQueryWrapper.select("watched_duration").eq("student_id", userId).eq("video_id", videoId);
            WatchRecordDO watchRecordDO = watchRecordMapper.selectOne(watchRecordDOQueryWrapper);
            //2.数据库不存在则插入一条新纪录
            if (watchRecordDO == null) {
                if (!watchRecordLock.tryLock()) {
                    return false;
                } else {
                    try {
                        watchRecordDO = watchRecordMapper.selectOne(watchRecordDOQueryWrapper);
                        if (watchRecordDO == null) {
                            watchRecordDO = new WatchRecordDO();
                            watchRecordDO.setStudentId(userId);
                            watchRecordDO.setVideoId(studyTimeRecordReq.getVideoId());
                            watchRecordDO.setStatus(1);
                            watchRecordDO.setWatchedDuration(0);
                            watchRecordMapper.insert(watchRecordDO);
                            redisTemplate.opsForHash().put(redisKey, hashKey, watchRecordDO.getWatchedDuration());
                            redisStudyTime = 0;
                        } else {
                            redisStudyTime = watchRecordDO.getWatchedDuration();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        watchRecordLock.unlock();
                    }
                }
            } else {
                redisStudyTime = watchRecordDO.getWatchedDuration();
            }
        }
        if (redisStudyTime != null && studyTime < redisStudyTime) {
            return true;//进度条往回拉无效
        }

        redisTemplate.opsForHash().put(redisKey, hashKey, studyTime);
        //TODO 以下删除的是整个Hash 可能导致缓存雪崩（因为同时过期了）
        //        // 设置整个 Key 的过期时间（例如 24 小时）
        //        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);
        //        //2.还需更新学生的总学习时长
        //        String redisTotalKey = RedisConstant.STUDY_TIME_RECOED_TOTAL + DELIMITER + userId;
        //        String redisTotalHashKey = studyTimeRecordReq.getTrainId().toString();
        //        Long redisTrainTotalWatchTime = (Long) redisTemplate.opsForHash().get(redisTotalKey, redisTotalHashKey);
        //        //2.1如果不在缓存
        //        if(redisTrainTotalWatchTime == null || redisTrainTotalWatchTime == 0){
        //            //1.从数据库查询
        //            if(!watchRecordLock.tryLock()){
        //                return false;
        //            } else {
        //                try {
        //                    QueryWrapper<StudentTrainingDO> queryWrapper = new QueryWrapper<>();
        //                    queryWrapper.select("total_duration").eq("student_id",userId).eq("training_id",studyTimeRecordReq.getStudyTime());
        //                    StudentTrainingDO studentTrainingDO = studentTrainingMapper.selectOne(queryWrapper);
        //                    int gap = studentTrainingDO.getTotalDuration() + studyTime;
        //                    TrainingDO trainingDO = trainingMapper.selectById(studyTimeRecordReq.getTrainId());
        //                    if(gap >= trainingDO.getTotalDuration()){
        //                        gap = trainingDO.getTotalDuration();
        //                    }
        //                    redisTemplate.opsForHash().put(redisTotalKey,redisTotalHashKey,gap);
        //                    return true;
        //                } catch (Exception e) {
        //                    throw new RuntimeException(e);
        //                } finally {
        //                    watchRecordLock.unlock();
        //                }
        //            }
        //        }
        //        int incrementTotal = studyTime - redisStudyTime;
        //        //TODO 后续需要加上时间戳 防止键值膨胀
        //        redisTemplate.opsForHash().increment(redisTotalKey,redisTotalHashKey,incrementTotal);
        return true;
    }

    //视频结束时调用
    @Override
    public Boolean ending(StudyTimeRecordReq studyTimeRecordReq) {
        Integer studyTime = studyTimeRecordReq.getStudyTime();
        Long videoId = studyTimeRecordReq.getVideoId();
        Long userId = UserContextHolder.getUserId();
        if (ObjectUtil.isEmpty(videoId)) {
            throw new RuntimeException("视频id为空");
        }
        //已完成视频的才更新时长
        try {
            if (!watchRecordLock.tryLock()) {
                return false;
            } else {
                VideoDO videoDO = videoMapper.selectById(videoId);
                if (videoDO.getDuration() - studyTime > 600) {
                    //如果和视频时长相差10分钟以上
                    throw new RuntimeException("学习时长错误");
                } else {
                    WatchRecordDO watchRecordDO = watchRecordMapper.selectOne(new LambdaQueryWrapper<WatchRecordDO>()
                        .eq(WatchRecordDO::getVideoId, videoId)
                        .eq(WatchRecordDO::getStudentId, userId));
                    //判断是否第一次看完视频
                    if (watchRecordDO == null || watchRecordDO.getStatus() == 0) {
                        Integer watchTotalTime = null;
                        Long trainingId = videoMapper.selectById(videoId).getTrainingId();
                        StudentTrainingDO studentTrainingDO = studentTrainingMapper
                            .selectOne(new LambdaQueryWrapper<StudentTrainingDO>()
                                .eq(StudentTrainingDO::getTrainingId, trainingId)
                                .eq(StudentTrainingDO::getStudentId, userId));
                        if (!ObjectUtil.isEmpty(watchRecordDO)) {
                            watchTotalTime = studentTrainingDO.getTotalDuration() + videoDO.getDuration();
                            studentTrainingDO.setStatus(1);
                            studentTrainingDO.setTotalDuration(studentTrainingDO.getTotalDuration() + videoDO
                                .getDuration());
                            studentTrainingMapper.updateById(studentTrainingDO);
                        } else {
                            watchTotalTime = videoDO.getDuration();
                            StudentTrainingDO newStudentTrainingDO = new StudentTrainingDO();
                            newStudentTrainingDO.setTrainingId(trainingId);
                            newStudentTrainingDO.setStudentId(userId);
                            newStudentTrainingDO.setStatus(1);
                            newStudentTrainingDO.setTotalDuration(videoDO.getDuration());
                            studentTrainingMapper.insert(newStudentTrainingDO);
                        }

                        //3.还需要更新培训计划完成情况
                        //3.1判断是否看完了全部视频
                        Integer totalDuration = trainingMapper.selectById(trainingId).getTotalDuration();
                        //3.2如果看完则更新状态为已完成
                        if (watchTotalTime >= (totalDuration)) {
                            UpdateWrapper<StudentTrainingDO> studentTrainingDOUpdateWrapper = new UpdateWrapper<>();
                            studentTrainingDOUpdateWrapper.set("status", 2)
                                .set("total_duration", totalDuration)
                                .eq("training_id", trainingId)
                                .eq("student_id", userId);
                            studentTrainingMapper.update(studentTrainingDOUpdateWrapper);
                        }
                    }
                    if (!ObjectUtil.isEmpty(watchRecordDO)) {
                        watchRecordDO.setStatus(2);//设置已经完成
                        watchRecordDO.setWatchedDuration(videoDO.getDuration());
                        watchRecordMapper.updateById(watchRecordDO);
                    } else {
                        WatchRecordDO newWatchRecordDO = new WatchRecordDO();
                        newWatchRecordDO.setStatus(2);
                        newWatchRecordDO.setVideoId(videoId);
                        newWatchRecordDO.setStudentId(userId);
                        newWatchRecordDO.setWatchedDuration(videoDO.getDuration());
                        watchRecordMapper.insert(newWatchRecordDO);
                    }
                }

            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            watchRecordLock.unlock();
        }

        return true;

        //删除redis中对应的数据
        //        String string = new StringBuilder().append(videoId).append(":").append(userId).toString();
        //        redisTemplate.opsForHash().delete(RedisConstant.STUDY_TIME_RECOED,string);
        //2.还需更新最后的学习时长
        //        String videoTotalHashKey = RedisConstant.STUDY_TIME_RECOED;
        //        String videoTotalHashFieldKey = videoId + DELIMITER + userId;
        //        Integer preWatchTime = (Integer) redisTemplate.opsForHash().get(videoTotalHashKey, videoTotalHashFieldKey);
        //        //2.1 先查出之前观看已经观看的视频时长和总时长的时差
        //        Integer videoTime = videoMapper.selectById(videoId).getDuration();
        //        if(preWatchTime == null || preWatchTime == 0){
        //            if(!watchRecordLock.tryLock()){
        //                return null;
        //            }else {
        //                try {
        //                    //如果redis没有则从数据库里面查询
        //                    QueryWrapper<WatchRecordDO> watchRecordDOQueryWrapper = new QueryWrapper<>();
        //                    watchRecordDOQueryWrapper.select("watched_duration").eq("student_id",userId).eq("video_id",videoId);
        //                    WatchRecordDO watchRecordDO = watchRecordMapper.selectOne(watchRecordDOQueryWrapper);
        //                    preWatchTime = watchRecordDO.getWatchedDuration();
        //                } catch (Exception e) {
        //                    throw new RuntimeException(e);
        //                } finally {
        //                    watchRecordLock.unlock();
        //                }
        //            }
        //        }
        //        int gap = videoTime -preWatchTime;
        //        //2.2更新学生观看记录的总时长
        //        Long trainingId = videoMapper.selectById(videoId).getTrainingId();
        //        String totalHashKey = RedisConstant.STUDY_TIME_RECOED_TOTAL + DELIMITER + userId;
        //        String totalHashFieldKey = trainingId.toString();
        //        redisTemplate.opsForHash().increment(totalHashKey,totalHashFieldKey,gap);
        //        //3.还需要更新培训计划完成情况
        //        //3.1判断是否看完了全部视频
        //        //3.1.1 先从缓存查看已观看的视频时长 和数据库里查询培训计划总时长
        //        Integer watchTotalTime = (Integer)redisTemplate.opsForHash().get(totalHashKey, totalHashFieldKey);
        //        Integer totalDuration = trainingMapper.selectById(trainingId).getTotalDuration();
        //
        //        //3.2如果看完则更新状态为已完成
        //        if(watchTotalTime >= (totalDuration)){
        //            UpdateWrapper<StudentTrainingDO> studentTrainingDOUpdateWrapper = new UpdateWrapper<>();
        //            studentTrainingDOUpdateWrapper.set("status",2).set("total_duration",totalDuration)
        //                            .eq("training_id",trainingId).eq("student_id",userId);
        //            studentTrainingMapper.update(studentTrainingDOUpdateWrapper);
        //            redisTemplate.opsForHash().put(totalHashKey, totalHashFieldKey,totalDuration);
        //        }
        //        return true;
    }

    @Override
    public StudyTimeRecordResp startRecord(StudyTimeRecordReq studyTimeRecordReq) {
        StudyTimeRecordResp resp = new StudyTimeRecordResp();
        Long userId = UserContextHolder.getUserId();
        Long videoId = studyTimeRecordReq.getVideoId();
        // Redis Key
        String redisKey = RedisConstant.STUDY_TIME_RECOED;
        // Hash Key (字段：视频ID:用户ID)
        String hashKey = videoId + ":" + userId;
        if (ObjectUtil.isEmpty(videoId)) {
            throw new RuntimeException("视频id不能为空");
        }
        WatchRecordDO watchRecordDO = watchRecordMapper.selectOne(new LambdaQueryWrapper<WatchRecordDO>()
            .eq(WatchRecordDO::getStudentId, userId)
            .eq(WatchRecordDO::getVideoId, videoId));
        if (watchRecordDO == null) {
            if (!watchRecordLock.tryLock()) {
                resp.setStudyTime(0);
                return resp;
            } else {
                try {
                    watchRecordDO = new WatchRecordDO();
                    watchRecordDO.setStudentId(userId);
                    watchRecordDO.setVideoId(studyTimeRecordReq.getVideoId());
                    watchRecordDO.setStatus(1);
                    watchRecordDO.setWatchedDuration(0);
                    watchRecordMapper.insert(watchRecordDO);
                    redisTemplate.opsForHash().put(redisKey, hashKey, watchRecordDO.getWatchedDuration());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    watchRecordLock.unlock();
                }
            }
        }
        resp.setStudyTime(watchRecordDO.getWatchedDuration());
        //2.更新学生的培训计划关联表从未开始到学习中(如果是处于未开始才这样做，否则什么都不做)
        //2.1判断是否是第一次看这个培训视频
        Long trainingId = videoMapper.selectById(videoId).getTrainingId();
        QueryWrapper<StudentTrainingDO> studentTrainingDOQueryWrapper = new QueryWrapper<>();
        studentTrainingDOQueryWrapper.eq("student_id", userId).eq("training_id", trainingId);
        StudentTrainingDO studentTrainingDO = studentTrainingMapper.selectOne(studentTrainingDOQueryWrapper);
        //2.1.1没找到就是第一次 直接插入新数据
        if (studentTrainingDO == null) {
            if (!watchRecordLock.tryLock()) {
                // 加锁失败，立即退出方法
                return resp;
            } else {
                try {
                    if (studentTrainingDO == null) {
                        studentTrainingDO = new StudentTrainingDO();
                        studentTrainingDO.setTrainingId(trainingId);
                        studentTrainingDO.setStudentId(userId);
                        studentTrainingDO.setTotalDuration(0);
                        studentTrainingDO.setStatus(1);
                        studentTrainingMapper.insert(studentTrainingDO);
                        return resp;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    watchRecordLock.unlock();
                }
            }
        }
        UpdateWrapper<StudentTrainingDO> studentTrainingDOUpdateWrapper = new UpdateWrapper<>();
        studentTrainingDOUpdateWrapper.set("status", 1)
            .eq("status", 0)
            .eq("student_id", userId)
            .eq("training_id", trainingId);
        studentTrainingMapper.update(studentTrainingDOUpdateWrapper);
        return resp;
    }

    @Override
    public List<ExpertVO> experts() {
        //获取该机构账号，查询是否有专家
        Long userId = UserContextHolder.getUserId();
        //获取机构id
        Long orgId = orgUserMapper.selectByUserId(String.valueOf(userId));
        List<ExpertVO> expertVOList = orgExpertMapper.listExperts(orgId);
        ValidationUtils.throwIfEmpty(expertVOList, "机构下没有专家");
        return expertVOList;
    }

    /**
     * 使用 Map 结构优化章节树构建，避免递归查询
     */
    private List<ChapterDO> buildChapterTree(List<ChapterDO> allChapters, Map<Long, List<VideoDO>> videoMap) {
        // 章节映射表，Key: 章节 ID，Value: 章节对象
        Map<Long, ChapterDO> chapterMap = allChapters.stream()
            .collect(Collectors.toMap(ChapterDO::getId, chapter -> chapter));

        // 存储最终的章节树
        List<ChapterDO> rootChapters = new ArrayList<>();

        // 遍历所有章节，建立父子关系
        for (ChapterDO chapter : allChapters) {
            chapter.setChildren(new ArrayList<>());
            List<VideoDO> videoList = videoMap.getOrDefault(chapter.getId(), Collections.emptyList());

            videoList = videoList.stream().map((item) -> {
                Integer duration = item.getDuration();
                String durationStr = formatDuration(duration);
                item.setDurationStr(durationStr);
                return item;
            }).sorted(Comparator.comparingInt(VideoDO::getSort)).collect(Collectors.toList());
            // 设置章节下的视频（避免递归查询）
            chapter.setVideos(videoList);

            if (chapter.getParentId() == 0L) {
                //如果是一级章节 直接添加到集合
                rootChapters.add(chapter);
            } else {
                //如果不是获取它的父章节
                ChapterDO parentChapter = chapterMap.get(chapter.getParentId());
                if (parentChapter != null) {
                    //把自己加入到父章节的子章节集合中
                    parentChapter.getChildren().add(chapter);
                }
            }
        }
        return rootChapters;
    }

    public String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%d:%02d", minutes, secs);
        }
    }

    //TODO 优化
    @Override
    public PageResp<TrainingResp> page(TrainingQuery query, PageQuery pageQuery) {
        log.info("query:{},pageQuery:{}", query, pageQuery);
        //1.查询是用户还是机构来进行查询
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();
        QueryWrapper<UserRoleDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("role_id").eq("user_id", userId);
        UserRoleDO userRoleDO = userRoleMapper.selectOne(queryWrapper);
        QueryWrapper<RoleDO> roleDOQueryWrapper = new QueryWrapper<>();
        roleDOQueryWrapper.select("id").eq("name", "机构人员");
        RoleDO orgRoleDO = roleMapper.selectOne(roleDOQueryWrapper);
        QueryWrapper<RoleDO> roleDOQueryWrapper1 = new QueryWrapper<>();
        roleDOQueryWrapper1.select("id").eq("name", "检验人员");
        RoleDO userRoleDO1 = roleMapper.selectOne(roleDOQueryWrapper1);
        QueryWrapper<RoleDO> roleDOQueryWrapper2 = new QueryWrapper<>();
        roleDOQueryWrapper2.select("id").eq("name", "系统管理员");
        //1.1是机构
        if (userRoleDO.getRoleId().equals(orgRoleDO.getId())) {
            return getOrgTrainingRespPageResp(query, pageQuery);
            //
        } else
        //1.2是用户
        if (userRoleDO.getRoleId().equals(userRoleDO1.getId())) {
            return getUserTrainingRespPageResp(query, pageQuery);
        } else
        //1.3是超级管理员
        {
            return getAllTrainingRespPageResp(query, pageQuery);
        }

    }

    private PageResp<TrainingResp> getUserTrainingRespPageResp(TrainingQuery query, PageQuery pageQuery) {
        //1.获取考生所报名的培训id
        //1.1获取userId
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();
        //TODO 大数据的情况下可能会影响性能
        //1.2获取考生id所报名的培训计划id
        Long status = query.getStatus();
        QueryWrapper<StudentTrainingDO> studentTrainingDOQueryWrapper = new QueryWrapper<>();
        List<Long> trainingIds = null;
        if (status.equals(-1L)) {
            trainingIds = trainingMapper.selectStudentNeedTraing(userId);
        } else {
            trainingIds = trainingMapper.selectStudentNeedTraing1(userId, status);
        }
        {
            //TODO 目前先直接插入数据
            List<StudentTrainingDO> studentTrainingDOS = new ArrayList<>();
            QueryWrapper<StudentTrainingDO> studentTrainingDOQueryWrapper1 = new QueryWrapper<>();
            studentTrainingDOQueryWrapper1.select("training_id").eq("student_id", userId);
            List<StudentTrainingDO> studentTrainingDOS1 = studentTrainingMapper
                .selectList(studentTrainingDOQueryWrapper1);
            Set<Long> existingTrainingIds = studentTrainingDOS1.stream()
                .map(StudentTrainingDO::getTrainingId)   // 提取 training_id
                .collect(Collectors.toSet());
            List<Long> newTrainingIds = trainingIds.stream()
                .filter(trainId -> !existingTrainingIds.contains(trainId)) // 排除已存在的
                .distinct()                                            // 去重（避免重复插入）
                .collect(Collectors.toList());
            for (Long trainId : newTrainingIds) {
                StudentTrainingDO studentTrainingDO = new StudentTrainingDO();
                studentTrainingDO.setTrainingId(trainId);
                studentTrainingDO.setStudentId(userId);
                studentTrainingDO.setStatus(0);
                studentTrainingDO.setTotalDuration(0);
                studentTrainingDOS.add(studentTrainingDO);
            }
            studentTrainingMapper.insert(studentTrainingDOS);
        }
        query.setStatus(null);
        QueryWrapper<TrainingDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tt.is_deleted", 0);
        if (trainingIds.isEmpty()) {
            return new PageResp<TrainingResp>();
        }
        queryWrapper.in("tt.id", trainingIds);
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询
        IPage<TrainingResp> page = baseMapper.gettrainingList(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        // 将查询结果转换成 PageResp 对象
        PageResp<TrainingResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);
        //查出总学习时长
        pageResp.getList().forEach((item) -> {
            int temp = 61;
            if (item.getTotalDuration() < 60) {
                temp = item.getTotalDuration();
            }
            item.setTotalDuration(item.getTotalDuration() / 60);
            //1.查询学生的每条训练计划的已经学习的时长且计算百分比
            //1.1查询学生已经学习的时长
            QueryWrapper<StudentTrainingDO> studentTrainingDOQueryWrapper1 = new QueryWrapper<>();
            studentTrainingDOQueryWrapper1.select("total_duration")
                .eq("student_id", userId)
                .eq("training_id", item.getId());
            StudentTrainingDO studentTrainingDO = studentTrainingMapper.selectOne(studentTrainingDOQueryWrapper1);
            Integer viewVideoTime = studentTrainingDO.getTotalDuration();
            //1.2如果未学习
            Integer totalDuration = item.getTotalDuration();
            if (viewVideoTime == null) {
                item.setLearningTime(String.valueOf(0));
                if (totalDuration != null) {
                    item.setLearningPercentage((double)0);
                }
                return;
            }
            //1.3如果已经有学习时长
            item.setLearningTime(formatDuration((Integer)viewVideoTime));
            if (totalDuration != null) {
                if ((Integer)viewVideoTime > totalDuration * 60) {
                    item.setLearningPercentage(100.0);
                } else {
                    int percentage = 0;
                    if (temp <= 60) {
                        percentage = ((Integer)viewVideoTime * 100) / (temp == 0 ? 1 : temp);
                    } else {
                        percentage = ((Integer)viewVideoTime * 100) / (totalDuration.equals(0)
                            ? 1
                            : totalDuration) / 60;
                    }
                    item.setLearningPercentage((double)(percentage));
                }
            }
        });
        //        StudentTrainingDO studentTrainingDO = studentTrainingMapper.selectOne(new LambdaQueryWrapper<StudentTrainingDO>()
        //                .eq(StudentTrainingDO::getStudentId, userId)
        //                .eq(StudentTrainingDO::getIsDeleted, 0));
        //        pageResp.getList().forEach((item)->{
        //            Integer totalStudyDuration = studentTrainingDO.getTotalDuration();
        //            //封装学习时长
        //            item.setLearningTime(formatDuration(totalStudyDuration));
        //            //封装百分比
        //            Integer totalDuration = item.getTotalDuration();
        //            double percentage = totalStudyDuration.doubleValue() / totalDuration.doubleValue();
        //            item.setLearningPercentage(percentage*100);
        //        });

        return pageResp;
    }

    @NotNull
    private PageResp<TrainingResp> getOrgTrainingRespPageResp(TrainingQuery query, PageQuery pageQuery) {
        //1.获取机构id下的培训id
        //1.1获取userId
        Long orgId = getOrganizationIdByUserId();
        //TODO 大数据的情况下可能会影响性能
        //1.2获取机构id下的培训类目
        QueryWrapper<OrgTraining> orgTrainingQueryWrapper = new QueryWrapper<>();
        orgTrainingQueryWrapper.select("training_id").eq("org_id", orgId);
        List<OrgTraining> orgTrainings = orgTrainingMapper1.selectList(orgTrainingQueryWrapper);
        List<Long> trainingIdS = new ArrayList<>();
        for (OrgTraining orgTraining : orgTrainings) {
            trainingIdS.add(orgTraining.getTrainingId());
        }
        QueryWrapper<TrainingDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tt.is_deleted", 0);
        PageResp<TrainingResp> pageResp = new PageResp<>();
        if (!trainingIdS.isEmpty()) {
            queryWrapper.in("tt.id", trainingIdS);
            // 根据 pageQuery 里的排序参数，对查询结果进行排序
            super.sort(queryWrapper, pageQuery);

            // 执行分页查询
            IPage<TrainingResp> page = baseMapper.gettrainingList(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

            // 将查询结果转换成 PageResp 对象
            pageResp = PageResp.build(page, super.getListClass());
            // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
            pageResp.getList().forEach(this::fill);

        }

        return pageResp;
    }

    @NotNull
    private PageResp<TrainingResp> getAllTrainingRespPageResp(TrainingQuery query, PageQuery pageQuery) {
        //1.获取全部培训计划记录
        QueryWrapper<TrainingDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tt.is_deleted", 0);

        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询
        IPage<TrainingResp> page = baseMapper.gettrainingList(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        // 将查询结果转换成 PageResp 对象
        PageResp<TrainingResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;
    }

    @Override
    public Long add(TrainingReq req) {
        //1.需要判断专家是否存在
        Long expertId = req.getExpertId();
        ExpertDO expertDO = expertMapper.selectById(expertId);
        if (expertDO == null) {
            throw new BusinessException("专家不存在");
        }
        //2.插入培训表
        //2.1查询用户id
        Long orgId = getOrganizationIdByUserId();
        //2.3 插入培训表
        TrainingDO trainingDO = TrainingDO.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .expertId(expertDO.getId())
//            .fee(req.getFee())
            .coverPath(req.getCoverPath())
            .build();
        trainingMapper.insert(trainingDO);
        Long trainingDOId = trainingDO.getId();
        //2.4插入培训机构关联表
        OrgTraining orgTraining = new OrgTraining();
        orgTraining.setTrainingId(trainingDOId);
        orgTraining.setOrgId(orgId);
        orgTrainingMapper1.insert(orgTraining);
        //3.还需插入专家费用
        //3.1插入专家费用
        TedExpertFree tedExpertFree = new TedExpertFree();
        tedExpertFree.setOrganizationId(orgId);
        tedExpertFree.setExpertId(expertId);
        tedExpertFree.setProjectId(trainingDOId);
        tedExpertFree.setPayDeadlineTime(LocalDateTime.now().plusMonths(1));
        expertFreeMapper.insert(tedExpertFree);
        //4.插入培训和项目关联表
        ProjectTrainingDO projectTrainingDO = new ProjectTrainingDO();
        projectTrainingDO.setTrainingId(trainingDOId);
        projectTrainingDO.setProjectId(req.getProjectId());
        projectTrainingMapper.insert(projectTrainingDO);
        //5.返回
        return trainingDOId;
    }

    //    @Override
    //    public TrainingDetailResp get(Long id) {
    //        getOrganizationIdByUserId();
    //        TrainingDO trainingDO = trainingMapper.selectById(id);
    //        QueryWrapper<TedExpertFree> tedExpertFreeQueryWrapper = new QueryWrapper<>();
    //        tedExpertFreeQueryWrapper.select("free")
    //        .eq("organization_id",getOrganizationIdByUserId())
    //                .eq("expert_id",trainingDO.getExpertId());
    //        TedExpertFree tedExpertFree = expertFreeMapper.selectOne(tedExpertFreeQueryWrapper);
    //    }

    private Long getOrganizationIdByUserId() {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();
        QueryWrapper<TedOrgUser> tedOrgUserQueryWrapper = new QueryWrapper<>();
        tedOrgUserQueryWrapper.select("org_id").eq("user_id", userId);
        TedOrgUser tedOrgUser = orgUserMapper.selectOne(tedOrgUserQueryWrapper);
        return tedOrgUser.getOrgId();
    }

    @Override
    public void update(TrainingReq req, Long id) {
        //只要修改了数据 状态都需要变成待审核（？）
        //req.setStatus(0L);
        super.update(req, id);
        if (req.getExpertId() != null) {
            //1.更新专家费用表相关数据
            Long orgId = getOrganizationIdByUserId();
            UpdateWrapper<TedExpertFree> tedExpertFreeUpdateWrapper = new UpdateWrapper<>();
            tedExpertFreeUpdateWrapper.eq("organization_id", orgId)
                .eq("project_id", id)
                .set("expert_id", req.getExpertId());
            expertFreeMapper.update(tedExpertFreeUpdateWrapper);
        }
        //4.更新培训视频的审核状态为未审核
        TrainingDO trainingDO1 = new TrainingDO();
        trainingDO1.setId(id);
        trainingDO1.setStatus(0L);
        trainingMapper.updateById(trainingDO1);
    }

    @Override
    public void delete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids))
            return;

        // 1. 批量删除专家费用表数据（一次性操作）
        QueryWrapper<TedExpertFree> expertFreeWrapper = new QueryWrapper<>();
        expertFreeWrapper.in("project_id", ids); // 使用 IN 操作
        expertFreeMapper.delete(expertFreeWrapper);

        // 2. 批量删除机构培训关联表数据（一次性操作）
        Long orgId = getOrganizationIdByUserId();
        QueryWrapper<OrgTraining> orgTrainingWrapper = new QueryWrapper<>();
        orgTrainingWrapper.eq("org_id", orgId).in("training_id", ids); // 注意字段名是否准确
        orgTrainingMapper1.delete(orgTrainingWrapper);

        //3.删除培训表
        super.delete(ids);

    }

    public OrgExpertMapper getOrgExpertMapper() {
        return orgExpertMapper;
    }

    public void setOrgExpertMapper(OrgExpertMapper orgExpertMapper) {
        this.orgExpertMapper = orgExpertMapper;
    }
}