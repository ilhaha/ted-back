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

package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.ImportQuestionConstant;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.dto.ProjectInfoDTO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.exam.model.resp.AllPathVo;
import top.continew.admin.exam.model.req.dto.OptionDTO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.exam.model.req.dto.QuestionDTO;
import top.continew.admin.exam.model.req.dto.SheetInfoDTO;
import top.continew.admin.examconnect.mapper.KnowledgeTypeMapper;
import top.continew.admin.examconnect.mapper.QuestionBankMapper;
import top.continew.admin.examconnect.mapper.StepMapper;
import top.continew.admin.examconnect.model.entity.KnowledgeTypeDO;
import top.continew.admin.examconnect.model.entity.QuestionBankDO;
import top.continew.admin.examconnect.model.entity.StepDO;
import top.continew.admin.util.RedisUtil;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.CategoryMapper;
import top.continew.admin.exam.model.entity.CategoryDO;
import top.continew.admin.exam.model.query.CategoryQuery;
import top.continew.admin.exam.model.req.CategoryReq;
import top.continew.admin.exam.model.resp.CategoryDetailResp;
import top.continew.admin.exam.model.resp.CategoryResp;
import top.continew.admin.exam.service.CategoryService;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.poi.excel.cell.CellUtil.getCellValue;

/**
 * 八大类，存储题目分类信息业务实现
 *
 * @author Anton
 * @since 2025/04/07 10:43
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends BaseServiceImpl<CategoryMapper, CategoryDO, CategoryResp, CategoryDetailResp, CategoryQuery, CategoryReq> implements CategoryService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final KnowledgeTypeMapper knowledgeTypeMapper;
    private final ProjectMapper projectMapper;
    private final CategoryMapper categoryMapper;
    @Resource
    private QuestionBankMapper questionBankMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private StepMapper stepMapper;

    @Override

    public List<ProjectVo> getSelectOptions(List<Integer> categoryTypeList) {
        return baseMapper.getSelectOptions(categoryTypeList);
    }

    @Override
    public AllPathVo getAllPath(Long id) {
        AllPathVo allPathVo = new AllPathVo();
        try {
            //1.查询知识id
            KnowledgeTypeDO knowledgeTypeDO = knowledgeTypeMapper.selectById(id);
            allPathVo.setKnowledgeTypeId(String.valueOf(knowledgeTypeDO.getId()));
            allPathVo.setKnowledgeTypeName(knowledgeTypeDO.getName());

            //2.查询项目id
            ProjectDO projectDO = projectMapper.selectById(knowledgeTypeDO.getProjectId());
            allPathVo.setProjectId(String.valueOf(projectDO.getId()));
            allPathVo.setProjectName(projectDO.getProjectName());
            //3.查询八大类id
            allPathVo.setCategoryId(String.valueOf(projectDO.getCategoryId()));
            //查询项目名称
            CategoryDO categoryDO = categoryMapper.selectById(projectDO.getCategoryId());
            allPathVo.setCategoryName(categoryDO.getName());
        } catch (Exception e) {
            throw new BusinessException("类别有误，请重试");
        }
        return allPathVo;
    }

    /**
     * 校验导入excel
     *
     * @param file
     * @return
     */
    @Override
    @Transactional
    public Boolean verifyExcel(MultipartFile file) {
        // 1. 文件非空校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 2. 文件格式校验（仅限 .xlsx / .xls）
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException("仅支持Excel文件(.xlsx/.xls)");
        }

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);

            // 5. 校验表头
            Sheet sheet = workbook.getSheetAt(0);
            validateHeader(sheet.getRow(0));

            // 6. 解析题目信息
            List<QuestionDTO> questions = parseQuestions(sheet);

            ValidationUtils.throwIfEmpty(questions, "题目不能为空");

            // 8. 校验数据库中是否已有相同题目（去重）
            List<QuestionBankDO> filteredQuestions = validateAndPrepareQuestions(questions);

            // 9. 写入数据库
            setQuestionBankMapper(filteredQuestions);

            // 10. 返回结果（包含导入题目）
            return Boolean.TRUE;

        } catch (EncryptedDocumentException e) {
            throw new BusinessException("文件加密无法读取");
        } catch (IOException e) {
            throw new BusinessException("文件读取失败");
        } catch (NumberFormatException e) {
            throw new BusinessException("工作表名称中数据格式不正确，请检查模板格式");
        }
    }

    /**
     * 项目是否存在
     *
     * @param questions
     */
    /**
     * 校验 Excel 导入题目中的项目和知识类型是否存在，并过滤数据库已存在的题目
     */
    private List<QuestionBankDO> validateAndPrepareQuestions(List<QuestionDTO> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new BusinessException("Excel中未解析到题目信息");
        }

        /* ================== 1. 校验项目是否存在 ================== */
        Set<String> projectCodes = questions.stream()
            .map(QuestionDTO::getProjectCode)
            .filter(code -> code != null && !code.trim().isEmpty())
            .map(String::trim)
            .collect(Collectors.toSet());

        if (projectCodes.isEmpty()) {
            throw new BusinessException("Excel中未填写项目代码");
        }

        // 查询项目，拿到 projectId 和 categoryId
        List<ProjectDO> projectList = projectMapper.selectList(new LambdaQueryWrapper<ProjectDO>()
            .in(ProjectDO::getProjectCode, projectCodes)
            .eq(ProjectDO::getProjectStatus, 2)
            .select(ProjectDO::getId, ProjectDO::getProjectCode, ProjectDO::getCategoryId));

        if (projectList.isEmpty()) {
            throw new BusinessException("Excel中的项目代码均不存在");
        }

        // Map<projectCode, ProjectInfo>
        Map<String, ProjectInfoDTO> projectCodeInfoMap = projectList.stream()
            .collect(Collectors.toMap(ProjectDO::getProjectCode, p -> new ProjectInfoDTO(p.getId(), p
                .getCategoryId())));

        // 不存在的项目代码
        List<String> notExistProjects = projectCodes.stream()
            .filter(code -> !projectCodeInfoMap.containsKey(code))
            .collect(Collectors.toList());

        if (!notExistProjects.isEmpty()) {
            throw new BusinessException("以下项目代码不存在：" + String.join("、", notExistProjects));
        }

        /* ================== 2. 校验知识类型是否存在 ================== */
        Set<String> excelProjectKnowledgeKeys = questions.stream()
            .filter(q -> q.getKnowledgeName() != null && !q.getKnowledgeName().trim().isEmpty())
            .map(q -> {
                Long projectId = projectCodeInfoMap.get(q.getProjectCode().trim()).getProjectId();
                return projectId + "_" + q.getKnowledgeName().trim();
            })
            .collect(Collectors.toSet());

        if (excelProjectKnowledgeKeys.isEmpty()) {
            throw new BusinessException("Excel中未填写知识类型名称");
        }

        List<KnowledgeTypeDO> knowledgeList = knowledgeTypeMapper.selectList(new LambdaQueryWrapper<KnowledgeTypeDO>()
            .in(KnowledgeTypeDO::getProjectId, projectCodeInfoMap.values()
                .stream()
                .map(ProjectInfoDTO::getProjectId)
                .collect(Collectors.toSet()))
            .select(KnowledgeTypeDO::getId, KnowledgeTypeDO::getProjectId, KnowledgeTypeDO::getName));

        // Map<"projectId_knowledgeName", knowledgeTypeId>
        Map<String, Long> knowledgeNameIdMap = knowledgeList.stream()
            .collect(Collectors.toMap(k -> k.getProjectId() + "_" + k.getName().trim(), KnowledgeTypeDO::getId));

        List<String> notExistKnowledge = excelProjectKnowledgeKeys.stream()
            .filter(key -> !knowledgeNameIdMap.containsKey(key))
            .map(key -> {
                String[] arr = key.split("_", 2);
                Long projectId = Long.valueOf(arr[0]);
                String knowledgeName = arr[1];

                String projectCode = projectCodeInfoMap.entrySet()
                    .stream()
                    .filter(e -> e.getValue().getProjectId().equals(projectId))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("未知项目");

                return "项目【" + projectCode + "】的知识类型【" + knowledgeName + "】";
            })
            .collect(Collectors.toList());

        if (!notExistKnowledge.isEmpty()) {
            throw new BusinessException("以下知识类型不存在：" + String.join("、", notExistKnowledge));
        }

        /* ================== 3. 过滤数据库已存在的题目 ================== */
        List<QuestionDTO> newQuestions = filterExistingQuestions(questions, projectCodeInfoMap, knowledgeNameIdMap);

        /* ================== 4. 构建 QuestionBankDO 列表 ================== */
        List<QuestionBankDO> questionBankDOS = newQuestions.stream().map(item -> {
            ProjectInfoDTO info = projectCodeInfoMap.get(item.getProjectCode().trim());
            Long knowledgeTypeId = knowledgeNameIdMap.get(info.getProjectId() + "_" + item.getKnowledgeName().trim());

            QuestionBankDO questionBankDO = new QuestionBankDO();
            questionBankDO.setCategoryId(info.getCategoryId());
            questionBankDO.setSubCategoryId(info.getProjectId());
            questionBankDO.setKnowledgeTypeId(knowledgeTypeId);
            questionBankDO.setQuestionType(item.getQuestionType());
            questionBankDO.setQuestion(item.getTitle());
            questionBankDO.setOptions(item.getOptions());
            questionBankDO.setExamType(item.getExamType());
            return questionBankDO;
        }).collect(Collectors.toList());

        return questionBankDOS;
    }

    /**
     * 过滤数据库中已存在的题目（题干相同且选项完全一致的视为重复）
     */
    private List<QuestionDTO> filterExistingQuestions(List<QuestionDTO> questions,
                                                      Map<String, ProjectInfoDTO> projectCodeInfoMap,
                                                      Map<String, Long> knowledgeNameIdMap) {
        List<String> questionTexts = questions.stream()
            .map(QuestionDTO::getTitle)
            .filter(Objects::nonNull)
            .map(q -> q.trim().replaceAll("\\s+", ""))
            .distinct()
            .collect(Collectors.toList());

        Set<Long> projectIds = questions.stream()
            .map(q -> projectCodeInfoMap.get(q.getProjectCode().trim()).getProjectId())
            .collect(Collectors.toSet());

        Set<Long> knowledgeTypeIds = questions.stream()
            .map(q -> knowledgeNameIdMap.get(projectCodeInfoMap.get(q.getProjectCode().trim()).getProjectId() + "_" + q
                .getKnowledgeName()
                .trim()))
            .collect(Collectors.toSet());

        List<Map<String, Object>> existingList = questionBankMapper
            .selectExistingQuestionsByProjectAndKnowledge(questionTexts, projectIds, knowledgeTypeIds);

        final Map<String, List<String>> existingMap = existingList.stream()
            .collect(Collectors.groupingBy(m -> ((String)m.get("question")).trim().replaceAll("\\s+", ""), Collectors
                .mapping(m -> {
                    String ans = (String)m.get("allOptions");
                    return ans == null ? "" : ans.trim().replaceAll("\\s+", "");
                }, Collectors.toList())));

        List<QuestionDTO> newQuestions = questions.stream().filter(q -> {
            String qTitle = q.getTitle() == null ? "" : q.getTitle().trim().replaceAll("\\s+", "");

            String qOptions;
            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                qOptions = q.getOptions().stream().map(opt -> {
                    String content = opt.getQuestion() == null ? "" : opt.getQuestion().trim().replaceAll("\\s+", "");
                    int correct = Boolean.TRUE.equals(opt.getIsCorrect()) ? 1 : 0;
                    return content + "[" + correct + "]";
                }).sorted().collect(Collectors.joining("、"));
            } else {
                qOptions = "";
            }

            List<String> existingOptionsList = existingMap.get(qTitle);
            if (existingOptionsList == null || existingOptionsList.isEmpty()) {
                return true;
            }

            boolean duplicate = existingOptionsList.stream()
                .anyMatch(dbOptions -> compareOptionSets(dbOptions, qOptions));
            return !duplicate;
        }).collect(Collectors.toList());

        if (newQuestions.isEmpty()) {
            throw new BusinessException("所有题目（含选项）均已存在，无需重复导入");
        }

        return newQuestions;
    }

    /**
     * 比较两个选项集合是否完全一致（忽略顺序、空格、大小写）
     */
    private boolean compareOptionSets(String dbOptions, String excelOptions) {
        if (dbOptions == null && excelOptions == null)
            return true;
        if (dbOptions == null || excelOptions == null)
            return false;

        Set<String> dbSet = Arrays.stream(dbOptions.split("[、,，;；]"))
            .map(s -> s.trim().toUpperCase())
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Set<String> excelSet = Arrays.stream(excelOptions.split("[、,，;；]"))
            .map(s -> s.trim().toUpperCase())
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        return dbSet.equals(excelSet);
    }

    private void setQuestionBankMapper(List<QuestionBankDO> questions) {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        // 批量插入题目
        questionBankMapper.insertBatch(questions);

        List<StepDO> stepDOS = new ArrayList<>();
        for (QuestionBankDO questionBankDO : questions) {
            for (OptionDTO option : questionBankDO.getOptions()) {
                StepDO stepDO = new StepDO();
                stepDO.setQuestionBankId(questionBankDO.getId());
                stepDO.setQuestion(option.getQuestion());
                stepDO.setIsCorrectAnswer(option.getIsCorrect());
                stepDOS.add(stepDO);
            }
        }

        stepMapper.insertBatch(stepDOS);
    }

    public static String extractBeforeBracket(String input) {
        int index = input.indexOf('（');
        if (index == -1)
            index = input.indexOf('('); // 支持中英文括号
        return index > 0 ? input.substring(0, index) : input;
    }

    private void validateHeader(Row headerRow) {
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != ImportQuestionConstant.HEADERS.length) {
            throw new BusinessException("表头缺失或列数不足，应至少包含 " + ImportQuestionConstant.HEADERS.length + " 列");
        }

        for (int i = 0; i < ImportQuestionConstant.HEADERS.length; i++) {
            String cellValue = getCellValue(headerRow.getCell(i));
            if (!ImportQuestionConstant.HEADERS[i].equals(cellValue)) {
                throw new BusinessException(String
                    .format("第 %d 列表头不正确，应为「%s」，实际为「%s」", i + 1, ImportQuestionConstant.HEADERS[i], cellValue));
            }
        }
    }

    // 改进后的数据行解析
    private List<QuestionDTO> parseQuestions(Sheet sheet) {
        List<QuestionDTO> questions = new ArrayList<>();

        // 从第 2 行开始（假设 0=说明，1=表头）
        for (int rowIdx = 2; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row == null) {
                break;
            }

            // 问题列在第 2 列
            String title = getCellValue(row.getCell(2));
            if (title == null || title.trim().isEmpty()) {
                break;
            }

            QuestionDTO question = new QuestionDTO();
            try {
                // 项目代码
                question.setProjectCode(parseRequiredString(row.getCell(0), "项目代码", rowIdx));
                // 知识类型名称
                question.setKnowledgeName(parseRequiredString(row.getCell(1), "知识类型名称", rowIdx));
                // 问题
                question.setTitle(parseRequiredString(row.getCell(2), "问题", rowIdx));
                // 题型（第 8 列）
                question.setQuestionType(parseQuestionType(row.getCell(8), rowIdx));
                // 选项从第 4 列开始（A、B、C、D...）
                parseDynamicOptions(row, question, rowIdx);

                // 考试人员类型（第 9 列）
                question.setExamType(parseExamType(row.getCell(9), rowIdx));

                questions.add(question);
            } catch (BusinessException e) {
                throw new BusinessException(e.getMessage());
            }
        }
        return questions;
    }

    private void parseDynamicOptions(Row row, QuestionDTO question, int rowIdx) {
        List<OptionDTO> options = new ArrayList<>();

        // ---------------- 1. 解析正确答案列（第 7 列） ----------------
        Cell answerCell = row.getCell(7);
        String answerRaw = getCellValue(answerCell).trim();
        if (answerRaw.isEmpty()) {
            throw new BusinessException("第" + (rowIdx + 1) + "行：未填写正确答案");
        }

        // 先强制校验格式必须是 A/B/C/D（多选可多个）
        Set<String> answerSet = parseAnswerLetters(answerRaw, rowIdx);

        // ---------------- 2. 解析选项 A~D ----------------
        char optionChar = 'A';

        for (int col = 3; col <= 6; col++) {
            Cell cell = row.getCell(col);
            String optionText = getCellValue(cell).trim();

            if (optionText.isEmpty()) {
                break;
            }

            String opt = String.valueOf(optionChar);

            OptionDTO option = new OptionDTO();
            option.setOption(opt);        // A/B/C/D
            option.setQuestion(optionText);
            option.setIsCorrect(answerSet.contains(opt));

            options.add(option);
            optionChar++;
        }

        if (options.isEmpty()) {
            throw new BusinessException("第" + (rowIdx + 1) + "行：至少需要一个选项");
        }

        // ---------------- 3. 校验选项与答案是否匹配题型 ----------------
        validateOptionsByType(question.getQuestionType(), options, rowIdx);

        // ---------------- 4. 校验答案是否存在于选项中 ----------------
        validateAnswerInOptions(answerSet, options, rowIdx);

        question.setOptions(options);
    }

    private Set<String> parseAnswerLetters(String answerRaw, int rowIdx) {
        Set<String> set = Arrays.stream(answerRaw.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
        // 校验答案必须是 A/B/C/D
        for (String s : set) {
            if (!s.matches("^[A-D]$")) {
                throw new BusinessException("第" + (rowIdx + 1) + "行：答案只能填写 A、B、C、D");
            }
        }

        return set;
    }

    private void validateAnswerInOptions(Set<String> answerSet, List<OptionDTO> options, int rowIdx) {
        Set<String> optionLabels = options.stream().map(OptionDTO::getOption).collect(Collectors.toSet());

        for (String ans : answerSet) {
            if (!optionLabels.contains(ans)) {
                throw new BusinessException("第" + (rowIdx + 1) + "行：答案【" + ans + "】没有对应的选项内容");
            }
        }
    }

    //解析考试类型
    public Integer parseExamType(Cell cell, int rowIdx) {
        String value = parseRequiredString(cell, "考试人员类型", rowIdx);

        // 去掉空格
        value = value.trim();

        // 中文题型映射到数字
        switch (value) {
            case "作业人员":
                return 1;
            case "检验人员":
                return 2;
            default:
                throw new BusinessException(String.format("第%d行：考试人员类型必须填写作业人员、或检验人员", rowIdx + 1));
        }
    }

    /**
     * 辅助方法：获取单元格原始值（用于报错提示）
     */
    private static String getCellOriginalValue(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue().trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "不支持的单元格类型（仅支持数字/文字）";
        }
    }

    // 答案标记解析
    private boolean parseAnswerFlag(String flag, int rowIdx, int colIdx) {
        if ("是".equalsIgnoreCase(flag))
            return true;
        if ("否".equalsIgnoreCase(flag))
            return false;
        throw new BusinessException(String.format("第%d行第%d列：是否正确答案必须为「是」或「否」", rowIdx + 1, colIdx + 1));
    }

    // 题型相关验证
    private void validateOptionsByType(int questionType, List<OptionDTO> options, int rowIdx) {
        long correctCount = options.stream().filter(OptionDTO::getIsCorrect).count();
        switch (questionType) {
            case 0: // 单选题
                if (options.size() < 2) {
                    throw new BusinessException("第" + (rowIdx + 1) + "行：单选题至少需要两个选项");
                }
                if (correctCount != 1) {
                    throw new BusinessException("第" + (rowIdx + 1) + "行：单选题必须有且只有一个正确答案");
                }
                break;
            case 1: // 判断题
                if (options.size() != 2) {
                    throw new BusinessException("第" + (rowIdx + 1) + "行：判断题必须有且只有两个选项");
                }
                if (correctCount != 1) {
                    throw new BusinessException("第" + (rowIdx + 1) + "行：判断题必须有且只有一个正确答案");
                }
                break;
            case 2: // 多选题
                if (options.size() < 2) {
                    throw new BusinessException("第" + (rowIdx + 1) + "行：多选题至少需要两个选项");
                }
                if (correctCount < 2) {
                    throw new BusinessException("第" + (rowIdx + 1) + "行：多选题至少需要两个正确答案");
                }
                break;

            default:
                throw new BusinessException("第" + (rowIdx + 1) + "行：题型不合法");
        }
    }

    // 改进的单元格读取方法
    private String getCellValue(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                return String.valueOf((int)cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private Boolean verifySheet(String sheetName) {
        String[] result = sheetName.split("\\s*,\\s*");
        // 遍历每个部分，检查是否为数字
        for (String str : result) {
            // 使用正则表达式判断是否为非负整数
            if (!str.matches("\\d+")) {
                return false;
            }
        }
        return true;
    }

    // 解析Sheet信息
    private SheetInfoDTO parseSheetInfo(Workbook workbook) {
        String sheetName = workbook.getSheetName(0);
        String[] parts = sheetName.split(",");
        if (parts.length != 3) {
            throw new BusinessException("导入文件模板不符合要求，或所选题目分类与文件内容不匹配");
        }
        return new SheetInfoDTO(parts[0], parts[1], parts[2]);
    }

    // 解析必填字段
    private String parseRequiredString(Cell cell, String fieldName, int rowIdx) {
        String value = getCellValue(cell);
        value = value.trim();
        if (value.isEmpty()) {
            throw new BusinessException(String.format("第%d行：%s不能为空", rowIdx + 1, fieldName));
        }
        return value;
    }

    // 解析题目类型
    private Integer parseQuestionType(Cell cell, int rowIdx) {
        String value = parseRequiredString(cell, "题型", rowIdx);

        // 去掉空格
        value = value.trim();

        // 中文题型映射到数字
        switch (value) {
            case "单选题":
                return 0;
            case "判断题":
                return 1;
            case "多选题":
                return 2;
            default:
                throw new BusinessException(String.format("第%d行：题型必须填写单选题、判断题或多选题", rowIdx + 1));
        }
    }

    @Override
    public Long add(@Valid CategoryReq req) {
        Integer categoryType = req.getCategoryType();
        if (categoryType == null || categoryType < 1 || categoryType > 4) {
            throw new BusinessException("种类类型无效，仅支持1(普通八大类)、2(焊接)、3(无损检测)、4(检验人员)");
        }
        Long addId = super.add(req);
        stringRedisTemplate.delete(RedisConstant.EXAM_CATEGORY_SELECT);
        return addId;
    }

    @Override
    public void update(CategoryReq req, Long id) {
        super.update(req, id);
        stringRedisTemplate.delete(RedisConstant.EXAM_CATEGORY_SELECT);
    }

    @Override
    public void delete(List<Long> ids) {
        super.delete(ids);
        stringRedisTemplate.delete(RedisConstant.EXAM_CATEGORY_SELECT);
    }

    @Override
    public CategoryDetailResp get(Long id) {
        return super.get(id);
    }
}