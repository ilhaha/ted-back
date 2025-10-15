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

package top.continew.admin.examconnect.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.QuestionConstant;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.CategoryMapper;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.entity.CategoryDO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.exam.model.req.dto.OptionDTO;
import top.continew.admin.exam.model.vo.CascadeOptionsVo;
import top.continew.admin.examconnect.mapper.KnowledgeTypeMapper;
import top.continew.admin.examconnect.mapper.QuestionOptionMapper;
import top.continew.admin.examconnect.mapper.StepMapper;
import top.continew.admin.examconnect.model.entity.KnowledgeTypeDO;
import top.continew.admin.examconnect.model.entity.StepDO;
import top.continew.admin.examconnect.model.resp.*;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.examconnect.mapper.QuestionBankMapper;
import top.continew.admin.examconnect.model.entity.QuestionBankDO;
import top.continew.admin.examconnect.model.query.QuestionBankQuery;
import top.continew.admin.examconnect.model.req.QuestionBankReq;
import top.continew.admin.examconnect.service.QuestionBankService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题库，存储各类题目及其分类信息业务实现
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
@Service
@RequiredArgsConstructor
public class QuestionBankServiceImpl extends BaseServiceImpl<QuestionBankMapper, QuestionBankDO, QuestionBankResp, QuestionBankDetailResp, QuestionBankQuery, QuestionBankReq> implements QuestionBankService {

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private KnowledgeTypeMapper knowledgeTypeMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private StepMapper stepMapper;

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private QuestionBankMapper questionBankMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private QuestionOptionMapper questionOptionMapper;

    @Override
    public PageResp<QuestionBankResp> page(QuestionBankQuery query, PageQuery pageQuery) {
        QueryWrapper<QuestionBankDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("t1.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<QuestionBankResp> page = baseMapper.selectQuestionBankPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        PageResp<QuestionBankResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);

        return build;
    }
    /**
     * 导出题库 Excel
     */
    @Override
    public byte[] exportQuestionsExcel(Long categoryId, Long subCategoryId, Long knowledgeTypeId) throws IOException {
        // 查询分类信息
        CategoryDO category = categoryMapper.selectById(categoryId);
        ProjectDO project = projectMapper.selectById(subCategoryId);
        KnowledgeTypeDO knowledgeType = knowledgeTypeMapper.selectById(knowledgeTypeId);

        // 防御性编程
        if (category == null || project == null || knowledgeType == null) {
            throw new IllegalArgumentException("分类信息不完整，请检查参数！");
        }

        //  查询题目及其选项
        List<QuestionBankResp> questions = questionBankMapper.selectByCategory(categoryId, subCategoryId, knowledgeTypeId);

        for (QuestionBankResp question : questions) {
            List<OptionDTO> options = questionOptionMapper.selectByQuestionId(question.getId());
            question.setOptions(options);
        }

        //统计题目类型数量
        int singleChoiceCount = 0;   // 单选题数量
        int multipleChoiceCount = 0; // 多选题数量
        int judgmentCount = 0;       // 判断题数量

        for (QuestionBankResp q : questions) {
            if (q.getQuestionType() == 0) {
                singleChoiceCount++;
            } else if (q.getQuestionType() == 1) {
                judgmentCount++;
            } else if (q.getQuestionType() == 2) {
                multipleChoiceCount++;
            }
        }
        int totalCount = questions.size(); // 总题数

        // 创建 Excel 文件
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("题库_" + category.getName());

            // ---- 3.1 表头 ----
            String[] headers = {
                    "题目标题",
                    "题目类型（0单选，1判断，2多选）",
                    "选项A", "是否正确答案",
                    "选项B", "是否正确答案",
                    "选项C", "是否正确答案",
                    "选项D", "是否正确答案"
            };

            XSSFRow headerRow = sheet.createRow(0);
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 统计行
            XSSFRow statsRow = sheet.createRow(1); // 统计行放在表头下方（第2行）
            XSSFCellStyle statsStyle = workbook.createCellStyle();
            statsStyle.setAlignment(HorizontalAlignment.LEFT);
            statsStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 合并单元格，让统计信息跨列显示（更美观）
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1)); // 合并“题目标题”“题目类型”列
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 9)); // 合并“选项A-选项D”列

            XSSFCell statsCell = statsRow.createCell(0);
            statsCell.setCellValue(
                    String.format(
                            "统计信息：总题数 %d 道，其中单选题 %d 道，多选题 %d 道，判断题 %d 道",
                            totalCount, singleChoiceCount, multipleChoiceCount, judgmentCount
                    )
            );
            statsCell.setCellStyle(statsStyle);

            // 填充题目数据
            int rowIndex = 2;
            for (QuestionBankResp q : questions) {
                XSSFRow row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(q.getQuestion());
                row.createCell(1).setCellValue(q.getQuestionType());

                // 选项填充（最多四个）
                List<OptionDTO> opts = q.getOptions();
                if (opts != null && !opts.isEmpty()) {
                    for (int j = 0; j < opts.size() && j < 4; j++) {
                        OptionDTO opt = opts.get(j);
                        row.createCell(2 + j * 2).setCellValue(opt.getQuestion());
                        row.createCell(3 + j * 2).setCellValue(opt.getIsCorrect() ? "是" : "否");
                    }
                }
            }

            // 自动列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public QuestionBankDetailResp get(Long id) {
        QuestionBankDetailResp questionBankDetailResp = super.get(id);
        questionBankDetailResp.setCategoryId(questionBankDetailResp.getKnowledgeTypeId());

        // 获取所有的步骤
        List<StepDO> questionBankId = stepMapper.selectList(new QueryWrapper<StepDO>().eq("question_bank_id", id));

        ArrayList<String> options = new ArrayList<>();
        ArrayList<Integer> correctAnswers = new ArrayList<>();

        for (int i = 0; i < questionBankId.size(); i++) {
            StepDO stepDO = questionBankId.get(i);
            options.add(stepDO.getQuestion());
            if (stepDO.getIsCorrectAnswer()) {
                correctAnswers.add(i);
            }
        }

        questionBankDetailResp.setOptions(options);
        questionBankDetailResp.setCorrectAnswers(correctAnswers);

        return questionBankDetailResp;
    }

    @Override
    public List<CascadeOptionsVo> getOptions() {
        // 取出所有八大类 项目 知识类型
        List<CategoryDO> categoryDOList = categoryMapper.selectList(new QueryWrapper<CategoryDO>().eq("is_deleted", 0));
        List<ProjectDO> projectDOList = projectMapper.selectList(new QueryWrapper<ProjectDO>().eq("is_deleted", 0));
        List<KnowledgeTypeDO> knowledgeTypeDOList = knowledgeTypeMapper.selectList(new QueryWrapper<KnowledgeTypeDO>()
            .eq("is_deleted", 0));

        List<CascadeOptionsVo> vos = new ArrayList<>();

        // 遍历所有的类别（Category）
        for (CategoryDO categoryDO : categoryDOList) {
            // 创建一个新的CascadeOptionsVo，用于存储该类别下的项目
            CascadeOptionsVo categoryVo = new CascadeOptionsVo();
            categoryVo.setValue(categoryDO.getId().toString());
            categoryVo.setLabel(categoryDO.getName() + "(" + categoryDO.getCode() + ")");

            // 获取当前类别下的项目
            List<CascadeOptionsVo> projectVos = new ArrayList<>();
            for (ProjectDO projectDO : projectDOList) {
                if (projectDO.getCategoryId().equals(categoryDO.getId())) {
                    // 为项目创建一个VO，并填充项目的相关信息
                    CascadeOptionsVo projectVo = new CascadeOptionsVo();
                    projectVo.setValue(projectDO.getId().toString());
                    projectVo.setLabel(projectDO.getProjectName() + "(" + projectDO.getProjectCode() + ")");

                    // 获取当前项目下的知识类型
                    List<CascadeOptionsVo> knowledgeTypeVos = new ArrayList<>();
                    for (KnowledgeTypeDO knowledgeTypeDO : knowledgeTypeDOList) {
                        if (knowledgeTypeDO.getProjectId().equals(projectDO.getId())) {
                            // 为知识类型创建一个VO，并填充相关信息
                            CascadeOptionsVo knowledgeTypeVo = new CascadeOptionsVo();
                            knowledgeTypeVo.setValue(knowledgeTypeDO.getId().toString());
                            knowledgeTypeVo.setLabel(knowledgeTypeDO.getName());
                            knowledgeTypeVos.add(knowledgeTypeVo);
                        }
                    }
                    // 将所有知识类型加入到当前项目的VO中
                    projectVo.setChildren(knowledgeTypeVos);
                    projectVos.add(projectVo);
                }
            }

            // 将所有项目加入到该类别的VO中
            categoryVo.setChildren(projectVos);
            vos.add(categoryVo);
        }

        return vos;
    }

    @Override
    public ExamPaperVO getExamQuestionBank(Long planId) {
        ExamPaperVO examPaperVO = getPaperFromRedis(RedisConstant.EXAM_PAPER_KEY + planId);

        if (!ObjectUtil.isEmpty(examPaperVO) && !ObjectUtil.isEmpty(examPaperVO.getQuestions())) {
            List<QuestionBankWithOptionVO> singleChoiceList = new ArrayList<>();
            List<QuestionBankWithOptionVO> multipleChoiceList = new ArrayList<>();
            List<QuestionBankWithOptionVO> judgeList = new ArrayList<>();

            for (QuestionBankWithOptionVO question : examPaperVO.getQuestions()) {
                if (question.getQuestionType() == QuestionConstant.QUESTION_TYPE_SINGLE_CHOICE) {
                    singleChoiceList.add(question);
                } else if (question.getQuestionType() == QuestionConstant.QUESTION_TYPE_MULTIPLE_CHOICE) {
                    multipleChoiceList.add(question);
                } else if (question.getQuestionType() == QuestionConstant.QUESTION_TYPE_TRUE_FALSE) {
                    judgeList.add(question);
                }
            }

            // 分别打乱每类题目
            Collections.shuffle(singleChoiceList);
            Collections.shuffle(multipleChoiceList);
            Collections.shuffle(judgeList);

            // 合并按顺序的新列表
            List<QuestionBankWithOptionVO> orderedQuestions = new ArrayList<>();
            orderedQuestions.addAll(singleChoiceList);
            orderedQuestions.addAll(multipleChoiceList);
            orderedQuestions.addAll(judgeList);

            // 打乱每个题目的选项
            for (QuestionBankWithOptionVO question : orderedQuestions) {
                if (!ObjectUtil.isEmpty(question.getOptions())) {
                    Collections.shuffle(question.getOptions());
                }
            }

            examPaperVO.setQuestions(orderedQuestions);
        }

        return examPaperVO;
    }

    @Override
    public Boolean generateExamQuestionBank(Long planId) {
        ExamPaperVO examPaperVO = getPaperFromRedis(RedisConstant.EXAM_PAPER_KEY + planId);
        if (ObjectUtil.isEmpty(examPaperVO)) {
            // 1. 查出计划、项目、分类
            ExamPlanDO examPlanDB = examPlanMapper.selectById(planId);
            ProjectDO projectDB = projectMapper.selectById(examPlanDB.getExamProjectId());
            CategoryDO categoryDB = categoryMapper.selectById(projectDB.getCategoryId());
            Long topicNumber = categoryDB.getTopicNumber();

            // 2. 查所有题目
            List<QuestionBankDO> questionBankDBList = questionBankMapper
                .selectList(new LambdaQueryWrapper<QuestionBankDO>().eq(QuestionBankDO::getCategoryId, projectDB
                    .getCategoryId()).eq(QuestionBankDO::getSubCategoryId, projectDB.getId()));

            // 3. 查知识库
            List<KnowledgeTypeDO> knowledgeTypeDBList = knowledgeTypeMapper
                .selectList(new LambdaQueryWrapper<KnowledgeTypeDO>().eq(KnowledgeTypeDO::getProjectId, projectDB
                    .getId()));

            // 4. 抽题逻辑，分类收集
            List<QuestionBankWithOptionVO> singleList = new ArrayList<>();
            List<QuestionBankWithOptionVO> multipleList = new ArrayList<>();
            List<QuestionBankWithOptionVO> judgeList = new ArrayList<>();

            for (KnowledgeTypeDO knowledgeType : knowledgeTypeDBList) {
                long count = topicNumber * knowledgeType.getProportion() / 100;

                // 当前知识点下的题目
                List<QuestionBankDO> source = questionBankDBList.stream()
                    .filter(q -> Objects.equals(q.getKnowledgeTypeId(), knowledgeType.getId()))
                    .collect(Collectors.toList());

                Collections.shuffle(source);
                List<QuestionBankDO> selected = source.stream().limit(count).toList();

                for (QuestionBankDO question : selected) {
                    QuestionBankWithOptionVO questionVO = new QuestionBankWithOptionVO();
                    BeanUtils.copyProperties(question, questionVO);

                    // 设置知识点信息
                    questionVO.setKnowledgeTypeId(knowledgeType.getId());
                    questionVO.setKnowledgeTypeName(knowledgeType.getName());
                    questionVO.setKnowledgeTypeTopicNumber(count);

                    // 加载选项
                    List<StepDO> stepDOS = stepMapper.selectList(new LambdaQueryWrapper<StepDO>()
                        .eq(StepDO::getQuestionBankId, question.getId()));

                    List<OptionVO> options = stepDOS.stream().map(step -> {
                        OptionVO option = new OptionVO();
                        option.setQuestion(step.getQuestion());
                        option.setQuestionBankId(step.getQuestionBankId());
                        option.setIsCorrectAnswer(step.getIsCorrectAnswer());
                        option.setId(step.getId());
                        return option;
                    }).collect(Collectors.toList());

                    Collections.shuffle(options);
                    questionVO.setOptions(options);

                    // 分类收集（题目类型：1-单选，2-多选，3-判断）
                    Integer type = question.getQuestionType();
                    if (type != null) {
                        switch (type) {
                            case QuestionConstant.QUESTION_TYPE_SINGLE_CHOICE -> singleList.add(questionVO);
                            case QuestionConstant.QUESTION_TYPE_MULTIPLE_CHOICE -> multipleList.add(questionVO);
                            case QuestionConstant.QUESTION_TYPE_TRUE_FALSE -> judgeList.add(questionVO);
                        }
                    }
                }
            }
            // 5. 合并题目列表：单选 -> 多选 -> 判断
            List<QuestionBankWithOptionVO> questionVOList = new ArrayList<>();
            questionVOList.addAll(singleList);
            questionVOList.addAll(multipleList);
            questionVOList.addAll(judgeList);

            // 6. 构建试卷对象
            examPaperVO = new ExamPaperVO();
            examPaperVO.setTopicNumber(topicNumber);
            examPaperVO.setQuestions(questionVOList);

            LocalDateTime endTime = examPlanDB.getEndTime();

            // 当前时间
            LocalDateTime now = LocalDateTime.now();

            // 过期时间：考试结束时间 + 30分钟
            LocalDateTime expireAt = endTime.plusMinutes(30);

            // 计算从当前时间到 expireAt 的 Duration
            Duration ttl = Duration.between(now, expireAt);
            savePaperToRedis(RedisConstant.EXAM_PAPER_KEY + planId, examPaperVO, ttl);
        }
        return true;
    }

    /**
     * 将试卷存到redis
     * 
     * @param key
     * @param examPaper
     */
    private void savePaperToRedis(String key, ExamPaperVO examPaper, Duration ttl) {
        redisTemplate.opsForValue().set(key, examPaper, ttl);
    }

    /**
     * 从redis中取出试卷
     * 
     * @param key
     * @return
     */
    private ExamPaperVO getPaperFromRedis(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj instanceof ExamPaperVO) {
            return (ExamPaperVO)obj;
        }
        return null;
    }

    @Transactional
    @Override
    public void update(QuestionBankReq req, Long id) {
        // 获取当前用户
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();

        QuestionBankDO questionBankDO = new QuestionBankDO();
        questionBankDO.setId(id);
        questionBankDO.setQuestion(req.getQuestion());
        questionBankDO.setQuestionType(req.getQuestionType());
        List<Long> categoryIds = req.getCategoryId();
        questionBankDO.setCategoryId(categoryIds.get(0));
        questionBankDO.setSubCategoryId(categoryIds.get(1));
        questionBankDO.setKnowledgeTypeId(categoryIds.get(2));
        questionBankDO.setAttachment(req.getImageUrl());
        questionBankDO.setCreateUser(userId);
        questionBankDO.setUpdateTime(LocalDateTime.now());
        this.updateById(questionBankDO);

        // 先删除该题目所有的选项
        stepMapper.delete(new LambdaQueryWrapper<StepDO>().eq(StepDO::getQuestionBankId, id));

        // 插入选项
        List<Integer> correctAnswers = req.getCorrectAnswers();
        HashMap<Integer, Boolean> map = new HashMap<>();
        for (Integer correctAnswer : correctAnswers)
            map.put(correctAnswer, true);
        List<StepDO> stepDOs = new ArrayList<StepDO>();
        for (int i = 0; i < req.getOptions().size(); i++) {
            String option = req.getOptions().get(i);
            StepDO stepDO = new StepDO();
            stepDO.setQuestion(option);
            stepDO.setQuestionBankId(questionBankDO.getId());
            Boolean b = map.get(i);
            stepDO.setIsCorrectAnswer(b != null && b);
            stepDO.setCreateUser(userId);
            stepDO.setCreateTime(LocalDateTime.now());

            stepDOs.add(stepDO);
        }

        stepMapper.insert(stepDOs);
    }

    @Override
    @Transactional
    public Long add(QuestionBankReq req) {
        // 获取当前用户
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();

        QuestionBankDO questionBankDO = new QuestionBankDO();
        questionBankDO.setQuestion(req.getQuestion());
        questionBankDO.setQuestionType(req.getQuestionType());
        List<Long> categoryIds = req.getCategoryId();
        ValidationUtils.throwIf(categoryIds.size() != 3, "请选择正确的分类");
        questionBankDO.setCategoryId(categoryIds.get(0));
        questionBankDO.setSubCategoryId(categoryIds.get(1));
        questionBankDO.setKnowledgeTypeId(categoryIds.get(2));
        questionBankDO.setAttachment(req.getImageUrl());
        questionBankDO.setCreateUser(userId);
        questionBankDO.setCreateTime(LocalDateTime.now());
        this.save(questionBankDO);

        List<Integer> correctAnswers = req.getCorrectAnswers();
        HashMap<Integer, Boolean> map = new HashMap<>();
        for (Integer correctAnswer : correctAnswers)
            map.put(correctAnswer, true);

        // 插入步骤
        List<StepDO> stepDOs = new ArrayList<StepDO>();
        for (int i = 0; i < req.getOptions().size(); i++) {
            String option = req.getOptions().get(i);
            StepDO stepDO = new StepDO();
            stepDO.setQuestion(option);
            stepDO.setQuestionBankId(questionBankDO.getId());
            Boolean b = map.get(i);
            stepDO.setIsCorrectAnswer(b != null && b);
            stepDO.setCreateUser(userId);
            stepDO.setCreateTime(LocalDateTime.now());

            stepDOs.add(stepDO);
        }

        stepMapper.insert(stepDOs);

        return questionBankDO.getId();
    }
}