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

package top.continew.admin.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.continew.admin.exam.model.dto.CandidateTicketDTO;

/**
 * 准考证数据访问层
 */
public interface ExamTicketMapper extends BaseMapper<CandidateTicketDTO> {

    /**
     * 根据用户ID和准考证号查询考生信息
     */
    CandidateTicketDTO findTicketByUserAndExamNumber(Long userId, String examNumber);
}