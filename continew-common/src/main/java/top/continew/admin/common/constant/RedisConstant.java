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

package top.continew.admin.common.constant;

/**
 * @author Anton
 * @date 2025/3/6-15:21
 */

public class RedisConstant {

    public static final Long MILLISECONDS_DAY = 86400L * 1000L;
    public static final Long MINIMUM_NUMBER_OF_DAYS = 1L;
    public static final Long MAXIMUM_NUMBER_OF_DAYS = 15L;

    /**
     * 生成准考证号序列号
     */
    public static final String EXAM_NUMBER_KEY = "exam:number:";

    /**
     * 记录当天同一项目有几次确认考试计划
     */
    public static final String EXAM_PLAN_COUNT_KEY = "exam:plan:count:";

    /**
     * 准考证号 Redis Key 过期时间（单位：天）
     */
    public static final long EXAM_NUMBER_KEY_EXPIRE_DAYS = 2L;

    /**
     * 考试试卷
     */
    public static final String EXAM_PAPER_KEY = "exam:paper:";

    /**
     * 每个用户的培训记录的Hash前缀
     */
    public static final String STUDY_TIME_RECOED_TOTAL = "study:time:total";

    /**
     * 考试训练计划的总时长
     */
    public static final String TRAIN_STUDY_TIME_TOTAL = "train:study:time:total";

    /**
     * 用户基本信息
     */
    public static final String USER_TOKEN = "user_token:";

    /**
     * 分隔符
     */
    public static final String DELIMITER = ":";

    /**
     * 十五天到期时间
     */
    public static final Long FIFTEEN_DAYS = (86400L * 15L);

    /**
     * 省份列表前缀
     */
    public static final String PROVINCES_KEY = "city:provinces:key";

    /**
     * 所有城市列表前缀
     */
    public static final String ALL_ADDRESS_KEY = "all:city:key";

    public static final String ALL_DOCUMENT_TYPE_KEY = "all:document:type:key";

    /**
     * 省市区树形
     */
    public static final String ADDRESS_TREE = "address:tree:key";

    //记录学习时长
    //+用户名+视频id+学习时长
    public static final String STUDY_TIME_RECOED = "study:time";

    /*
      考试项目相关
     */
    /**
     * 八大类下拉框
     */
    public static final String EXAM_CATEGORY_SELECT = "exam:select:options:category";
    /**
     * 考试项目下拉框
     */
    public static final String EXAM_PROJECT_SELECT = "exam:select:options:project";

    /**
     * 专家查询目录
     */
    public static final String EXAM_EXPERT_QUERY = "training:page:expert";
    /**
     * 机构查询目录
     */
    public static final String EXAM_ORGANIZATION_QUERY = "training:page:organization";

    /*
           代理机相关
     */
    public static final String AGENT_ONLINE_KEY = "exam:agent:online";

    /**
     * 考生注册信息
     */
    public static final String EXAM_STUDENTS_REGISTER = "training:register:";

    public static Long randomTTL() {
        long days = MINIMUM_NUMBER_OF_DAYS + (long)(Math
            .random() * (MAXIMUM_NUMBER_OF_DAYS - MINIMUM_NUMBER_OF_DAYS + 1));
        return days * MILLISECONDS_DAY;
    }

}
