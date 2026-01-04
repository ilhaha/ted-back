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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/12/25 9:07
 */
@Data
public class InputScoresReq {

    /**
     * 考试记录id
     */
    @NotEmpty(message = "未选择考试记录")
    private List<Long> recordIds;

    /**
     * 分数
     */
    @NotNull(message = "分数不能为空")
    @Min(value = 0, message = "分数不能小于 0")
    @Max(value = 100, message = "分数不能大于 100")
    private Integer scores;

    /**
     * 录入成绩类型：1 实操成绩 2 道路成绩
     */
    @NotNull(message = "成绩类型不能为空")
    private Integer scoresType;
}
