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

package top.continew.admin.invigilate.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "考试分数提交请求")
public class ExamScoreSubmitReq {

    @NotBlank(message = "考试计划ID不能为空")
    @Schema(description = "考试计划ID", example = "123456")
    private Long examPlanId;

    @NotEmpty(message = "考生成绩列表不能为空")
    @Schema(description = "考生成绩列表")
    private List<ScoreItem> scores;

    @Data
    @Schema(description = "考生成绩项")
    public static class ScoreItem {

        @NotBlank(message = "考生ID不能为空")
        @Schema(description = "考生ID", example = "1001")
        private Long studentId;

        @NotNull(message = "考试成绩不能为空")
        @Min(value = 0, message = "成绩最小值为0")
        @Max(value = 100, message = "成绩最大值为100")
        @Schema(description = "考试成绩", example = "85.5")
        private Double score;

        @Schema(description = "答卷URL（可选）", example = "https://oss.example.com/answer-sheet.pdf")
        private String answerSheetUrl;
    }
}
