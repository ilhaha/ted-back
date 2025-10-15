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

package top.continew.admin.exam.model.req;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考试地点参数
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
@Data
@ToString
@Schema(description = "创建或修改考试地点参数")
public class ExamLocationReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地址id
     */
    private Long id;
    /**
     * 地址名称
     */
    private String locationName;
    /**
     * 省份id
     */
    private Long provinceId;
    /**
     * 城市id
     */
    private Long cityId;
    /**
     * 区域id
     */
    private Long streetId;
    /**
     * 详细地址
     */
    private String detailedAddress;
    /**
     * 运营状态
     */
    private Integer operationalStatus;

}