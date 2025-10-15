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

package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考试地点实体
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
@Data
@TableName("ted_exam_location")
public class ExamLocationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 省份ID
     */
    private Long provinceId;
    private String locationName;

    /**
     * 城市ID
     */
    private Long cityId;

    /**
     * 街道ID
     */
    private Long streetId;

    /**
     * 详细地址
     */
    private String detailedAddress;

    /**
     * 运营状态; 0:运营;1:休息;2:维护;3:关闭;
     */
    private Integer operationalStatus;

    /**
     * 描述
     */
    private String redeme;

    /**
     * 删除标记
     */
    private Boolean isDeleted;
}