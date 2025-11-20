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

package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 培训签到记录详情信息
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "培训签到记录详情信息")
public class TrainingCheckinDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 培训ID
     */
    @Schema(description = "培训ID")
    @ExcelProperty(value = "培训ID")
    private Long trainingId;

    /**
     * 考生ID（对应 sys_user.id）
     */
    @Schema(description = "考生ID（对应 sys_user.id）")
    @ExcelProperty(value = "考生ID（对应 sys_user.id）")
    private Long candidateId;

    /**
     * 机构ID（冗余）
     */
    @Schema(description = "机构ID（冗余）")
    @ExcelProperty(value = "机构ID（冗余）")
    private Long orgId;

    /**
     * 签到时间
     */
    @Schema(description = "签到时间")
    @ExcelProperty(value = "签到时间")
    private LocalDateTime checkinTime;

    /**
     * 扫码时二维码的时间戳
     */
    @Schema(description = "扫码时二维码的时间戳")
    @ExcelProperty(value = "扫码时二维码的时间戳")
    private Long qrTimestamp;

    /**
     * 二维码签名（校验）
     */
    @Schema(description = "二维码签名（校验）")
    @ExcelProperty(value = "二维码签名（校验）")
    private String qrSign;

    /**
     * 1：正常签到 2: 过期二维码 3：伪造二维码
     */
    @Schema(description = "1：正常签到 2: 过期二维码 3：伪造二维码")
    @ExcelProperty(value = "1：正常签到 2: 过期二维码 3：伪造二维码")
    private Boolean status;
}