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

package top.continew.admin.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.continew.admin.training.mapper.WatchRecordMapper;

@Component
@Slf4j
public class save {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    WatchRecordMapper watchRecordMapper;

    //    @Scheduled(cron = "*/10 * * * * ?") // 每 10 秒执行一次
    //    // TODO （可以）重构代码
    //    public void saveStudyRecode() {
    //        String redisKey = RedisConstant.STUDY_TIME_RECOED;
    //        Map<String, Object> entries = redisTemplate.opsForHash().entries(redisKey);//拿到所有的键值对
    //
    //        if (CollectionUtils.isEmpty(entries)) {
    //            return;
    //        }
    //
    //        //获取所有记录
    //        List<WatchRecordDO> keys = entries.entrySet().stream().map((item) -> {
    //            String key = item.getKey();
    //            String[] split = key.split(":");
    //            Long videoId = Long.parseLong(split[0]);
    //            Long userId = Long.parseLong(split[1]);
    //            WatchRecordDO watchRecordDO = new WatchRecordDO();
    //            watchRecordDO.setVideoId(videoId);
    //            watchRecordDO.setStudentId(userId);
    //            return watchRecordDO;
    //        }).collect(Collectors.toList());
    //
    //        //已经存在的集合
    //        List<WatchRecordDO> existList = watchRecordMapper.findExistingRecords(keys);
    //
    //        // 将数据库已存在的记录转换为一个 Set，以便快速查找
    //        Set<String> existSet = existList.stream()
    //            .map(record -> record.getVideoId() + ":" + record.getStudentId()) // 转换为 "videoId:userId" 的格式
    //            .collect(Collectors.toSet());
    //
    //        // **过滤出 Redis 中存在但数据库中没有的记录**
    //        List<WatchRecordDO> notExistList = keys.stream()
    //            .filter(record -> !existSet.contains(record.getVideoId() + ":" + record.getStudentId())) // 过滤掉存在于数据库中的
    //            .collect(Collectors.toList());
    //
    //        //更新记录
    //        List<WatchRecordDO> updateList = existList.stream().map((item) -> {
    //            StringBuilder sb = new StringBuilder();
    //            Long videoId = item.getVideoId();
    //            Long studentId = item.getStudentId();
    //            String key = sb.append(videoId).append(":").append(studentId).toString();
    //            Integer studyTime = (Integer)entries.get(key);
    //            Integer oldStudyTime = item.getWatchedDuration();
    //            if (Objects.equals(oldStudyTime, studyTime)) {
    //                return null; // 新旧值相同，过滤
    //            }
    //            if (oldStudyTime > studyTime) {
    //                //进度条往回拉,按照数据库为准，更新缓存，不更新数据库
    //                redisTemplate.opsForHash().put(redisKey, key, oldStudyTime);
    //                return null;
    //            }
    //            if (studyTime - oldStudyTime > 900) {
    //                //大于15分钟
    //                throw new RuntimeException("学习时长错误");
    //            }
    //            item.setWatchedDuration(studyTime);
    //            return item;
    //        })
    //            .filter(Objects::nonNull)  // 过滤掉null值
    //            .collect(Collectors.toList());
    //        //        log.debug("Save定时任务:updateList:{},notExistList:{}",updateList,notExistList);
    //        if (!updateList.isEmpty()) {
    //            watchRecordMapper.batchUpdateByUniqueKey(updateList);
    //        }
    //        if (!notExistList.isEmpty()) {
    //            //剩下的是不存在的进行插入
    //            watchRecordMapper.insertBatch(notExistList);
    //        }
    //    }

}
