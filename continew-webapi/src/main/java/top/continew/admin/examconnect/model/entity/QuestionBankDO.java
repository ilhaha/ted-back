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

package top.continew.admin.examconnect.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;
import top.continew.admin.exam.model.req.dto.OptionDTO;

import java.io.Serial;
import java.util.List;

/**
 * 题库，存储各类题目及其分类信息实体
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
@Data
@TableName("ted_question_bank")
public class QuestionBankDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 八大类ID
     */
    private Long categoryId;

    /**
     * 八大类子ID
     */
    private Long subCategoryId;

    /**
     * 题目类型(0: 选择; 1: 判断; 2: 多选;)
     */
    private Integer questionType;

    /**
     * 知识类型ID
     */
    private Long knowledgeTypeId;

    /**
     * 考试类型（0-未指定，1-作业人员考试，2-无损/有损检验人员考试，可后续扩展）
     */
    private Integer examType;

    /**
     * 题目内容
     */
    private String question;

    /**
     * 题目附件（图片路径）
     */
    private String attachment;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Boolean isDeleted;

    /**
     * 选项列表
     */
    @TableField(exist = false)
    private List<OptionDTO> options;
}