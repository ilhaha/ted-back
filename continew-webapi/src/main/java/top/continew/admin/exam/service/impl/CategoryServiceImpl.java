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

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.ExcelParseResult;
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
import java.util.concurrent.TimeUnit;
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
    public List<ProjectVo> getSelectOptions() {
        String json = stringRedisTemplate.opsForValue().get(RedisConstant.EXAM_CATEGORY_SELECT);
        if (StringUtils.isNotBlank(json)) {
            return com.alibaba.fastjson2.JSON.parseArray(json, ProjectVo.class);
        } else {
            List<ProjectVo> selectOptions = baseMapper.getSelectOptions();
            stringRedisTemplate.opsForValue()
                .set(RedisConstant.EXAM_CATEGORY_SELECT, JSON.toJSONString(selectOptions), RedisConstant
                    .randomTTL(), TimeUnit.MILLISECONDS);
            return selectOptions;
        }
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
    public ExcelParseResult verifyExcel(MultipartFile file) {
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

            // 3. 校验 sheet 名称格式（示例：分类名称[1],项目名称[2],知识类型名称[3]）
            String sheetName = workbook.getSheetName(0);
            String[] categoryInfo = sheetName.split(",");
            if (categoryInfo.length != 3) {
                throw new BusinessException("工作表名称格式应为：分类名称,项目名称,知识类型名称");
            }

            // 4. 校验 sheet 是否符合模板定义
            SheetInfoDTO sheetInfo = parseSheetInfo(workbook);
            if (!verifySheet(sheetName)) {
                throw new BusinessException("工作表名称错误，请勿修改模板");
            }

            // 5. 校验表头
            Sheet sheet = workbook.getSheetAt(0);
            validateHeader(sheet.getRow(0));

            // 6. 解析题目信息
            List<QuestionDTO> questions = parseQuestions(sheet);
            ValidationUtils.throwIfEmpty(questions, "题目不能为空");

            // 7. 提取ID信息（名称中括号格式：名称[id]）
            Long categoryId = Long.valueOf(extractBeforeBracket(categoryInfo[0]));
            Long projectId = Long.valueOf(extractBeforeBracket(categoryInfo[1]));
            Long knowledgeTypeId = Long.valueOf(extractBeforeBracket(categoryInfo[2]));

            // 8. 校验数据库中是否已有相同题目（去重）
            List<QuestionDTO> filteredQuestions = filterExistingQuestions(questions, categoryId, projectId, knowledgeTypeId);

            // 9. 写入数据库
            setQuestionBankMapper(knowledgeTypeId, filteredQuestions, categoryId, projectId);

            // 10. 返回结果（包含导入题目）
            return new ExcelParseResult(sheetInfo, filteredQuestions);

        } catch (EncryptedDocumentException e) {
            throw new BusinessException("文件加密无法读取");
        } catch (IOException e) {
            throw new BusinessException("文件读取失败");
        } catch (NumberFormatException e) {
            throw new BusinessException("工作表名称中ID格式不正确，请检查模板格式");
        }
    }

    /**
     * 过滤数据库中已存在的题目，避免重复导入
     */
    /**
     * 过滤数据库中已存在的题目（题干相同且选项完全一致的视为重复）
     */
    private List<QuestionDTO> filterExistingQuestions(List<QuestionDTO> questions,
                                                      Long categoryId,
                                                      Long projectId,
                                                      Long knowledgeTypeId) {
        if (questions == null || questions.isEmpty()) {
            throw new BusinessException("Excel中未解析到题目信息");
        }

        // 收集题目文本（去空格、换行）
        List<String> questionTexts = questions.stream()
            .map(QuestionDTO::getTitle)
            .filter(Objects::nonNull)
            .map(q -> q.trim().replaceAll("\\s+", ""))
            .distinct()
            .collect(Collectors.toList());

        // 查询数据库中同分类、同项目、同知识类型下的题目及其选项集合
        List<Map<String, Object>> existingList = questionBankMapper
            .selectExistingQuestions(questionTexts, categoryId, projectId, knowledgeTypeId);

        if (existingList == null || existingList.isEmpty()) {
            return questions; // 数据库无重复，全部导入
        }

        // 构建数据库已有题目对应的选项集合映射 Map<题目, List<选项组合字符串>>
        final Map<String, List<String>> existingMap = existingList.stream()
            .collect(Collectors.groupingBy(m -> ((String)m.get("question")).trim().replaceAll("\\s+", ""), Collectors
                .mapping(m -> {
                    String ans = (String)m.get("allOptions");
                    return ans == null ? "" : ans.trim().replaceAll("\\s+", "");
                }, Collectors.toList())));

        // 过滤出未重复的新题
        List<QuestionDTO> newQuestions = questions.stream().filter(q -> {
            String qTitle = q.getTitle() == null ? "" : q.getTitle().trim().replaceAll("\\s+", "");

            // 组装 Excel 当前题目的选项集合（包括是否正确）
            String qOptions;
            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                qOptions = q.getOptions().stream().map(opt -> {
                    String content = opt.getQuestion() == null ? "" : opt.getQuestion().trim().replaceAll("\\s+", "");
                    int correct = Boolean.TRUE.equals(opt.getIsCorrect()) ? 1 : 0;
                    return content + "[" + correct + "]";
                })
                    .sorted() // 排序避免顺序差异
                    .collect(Collectors.joining("、"));
            } else {
                qOptions = "";
            }

            List<String> existingOptionsList = existingMap.get(qTitle);
            if (existingOptionsList == null || existingOptionsList.isEmpty()) {
                return true; // 数据库中无此题目，直接导入
            }

            // 判断是否存在完全相同的选项集合（任意一条一致则视为重复）
            boolean duplicate = existingOptionsList.stream()
                .anyMatch(dbOptions -> compareOptionSets(dbOptions, qOptions));

            return !duplicate; // 不重复则保留导入
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

    private void setQuestionBankMapper(Long knowledgeTypeId,
                                       List<QuestionDTO> questions,
                                       Long categoryId,
                                       Long projectId) {
        List<QuestionBankDO> questionBankDOS = questions.stream().map(item -> {
            QuestionBankDO questionBankDO = new QuestionBankDO();
            questionBankDO.setCategoryId(categoryId);
            questionBankDO.setSubCategoryId(projectId);
            questionBankDO.setKnowledgeTypeId(knowledgeTypeId);
            questionBankDO.setQuestionType(item.getQuestionType());
            questionBankDO.setQuestion(item.getTitle());
            questionBankDO.setOptions(item.getOptions());
            questionBankDO.setExamType(item.getExamType());
            return questionBankDO;
        }).collect(Collectors.toList());

        questionBankMapper.insertBatch(questionBankDOS);

        List<StepDO> stepDOS = new ArrayList<>();
        for (QuestionBankDO questionBankDO : questionBankDOS) {
            for (OptionDTO option : questionBankDO.getOptions()) {
                StepDO stepDO = new StepDO();
                stepDO.setQuestionBankId(questionBankDO.getId());
                stepDO.setQuestion(option.getQuestion());
                stepDO.setIsCorrectAnswer(option.getIsCorrect());
                stepDOS.add(stepDO);
            }
        }

        boolean b = stepMapper.insertBatch(stepDOS);
        //添加成功提示

    }

    public static String extractBeforeBracket(String input) {
        int index = input.indexOf('（');
        if (index == -1)
            index = input.indexOf('('); // 支持中英文括号
        return index > 0 ? input.substring(0, index) : input;
    }

    private void validateHeader(Row headerRow) {
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() < 2) {
            throw new BusinessException("表头缺失或列数不足");
        }

        if (!"题目标题".equals(getCellValue(headerRow.getCell(0))) || !"题目类型（0单选，1判断，2多选）".equals(getCellValue(headerRow
            .getCell(1)))) {
            throw new BusinessException("前两列格式不符合要求");
        }
    }

    // 改进后的数据行解析
    private List<QuestionDTO> parseQuestions(Sheet sheet) {
        List<QuestionDTO> questions = new ArrayList<>();

        for (int rowIdx = 2; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row == null)
                break; // 遇到空行停止

            // 检查题目标题是否为空
            String title = getCellValue(row.getCell(0));
            if (title == null || title.trim().isEmpty())
                break;

            QuestionDTO question = new QuestionDTO();
            try {
                // 解析基础信息
                question.setTitle(parseRequiredString(row.getCell(0), "题目标题", rowIdx));
                question.setQuestionType(parseQuestionType(row.getCell(1), rowIdx));
                Cell examTypeCell = row.getCell(2);
                question.setExamType(parseExamType(examTypeCell, rowIdx));

                // 动态解析选项和答案
                parseDynamicOptions(row, question, rowIdx);

                questions.add(question);
            } catch (BusinessException e) {
                throw new BusinessException("第" + (rowIdx + 1) + "列数据错误：" + e.getMessage());
            }
        }
        return questions;
    }

    // 动态解析选项方法
    private void parseDynamicOptions(Row row, QuestionDTO question, int rowIdx) {
        List<OptionDTO> options = new ArrayList<>();
        int colIdx = 3; // 从第四列开始解析选项

        while (colIdx < row.getLastCellNum()) {
            Cell optionCell = row.getCell(colIdx);
            Cell answerCell = row.getCell(colIdx + 1);

            // 选项列结束判断
            if (optionCell == null && answerCell == null)
                break;

            // 解析选项内容
            String option = getCellValue(optionCell);
            if (option.isEmpty()) {
                if (question.getQuestionType() != 1) { // 非判断题必须明确选项
                    throw new BusinessException("选项内容不能为空");
                }
                break;
            }

            // 解析正确答案标记
            String answerFlag = getCellValue(answerCell);
            if (answerFlag.isEmpty()) {
                throw new BusinessException("选项[" + option + "]必须指定是否正确答案");
            }

            // 转换答案标记
            boolean isCorrect = parseAnswerFlag(answerFlag, rowIdx, colIdx + 1);
            options.add(new OptionDTO(option, isCorrect));

            colIdx += 2; // 移动到下一组选项
        }

        // 根据题型验证选项
        validateOptionsByType(question.getQuestionType(), options, rowIdx);
        question.setOptions(options);
    }

    //解析考试类型
    public static Long parseExamType(Cell cell, int rowIdx) {
        // 1. 校验单元格是否为空
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new BusinessException(String.format("第%d列【考试类型】字段不能为空", rowIdx));
        }

        Integer examTypeCode = null;
        try {
            // 2. 按数字解析（优先：Excel填0/1/2直接识别）
            if (cell.getCellType() == CellType.NUMERIC) {
                // 数字转int（避免小数，比如填2.0会转为2）
                int code = (int)Math.round(cell.getNumericCellValue());
                // 校验数字是否在合法范围内（0/1/2）
                if (code == 0 || code == 1 || code == 2) {
                    examTypeCode = code;
                }
            }
            // 3. 按文字解析（兼容Excel填中文的场景）
            else if (cell.getCellType() == CellType.STRING) {
                String desc = cell.getStringCellValue().trim();
                // 文字与编码的映射（匹配规则：包含关键词即可）
                if (desc.contains("未指定")) {
                    examTypeCode = 0;
                } else if (desc.contains("作业人员")) {
                    examTypeCode = 1;
                } else if (desc.contains("无损") || desc.contains("有损") || desc.contains("检验")) {
                    examTypeCode = 2;
                }
            }

            // 4. 校验解析结果是否合法
            if (examTypeCode == null) {
                String cellValue = getCellOriginalValue(cell);
                throw new BusinessException(String
                    .format("第%d行【考试类型】值不合法：%s。允许值：\n数字：0(未指定)、1(作业人员考试)、2(无损/有损检验人员考试)\n文字：未指定、作业人员考试、无损/有损检验", rowIdx, cellValue));
            }

            // 5. 转为Long类型返回（匹配后端实体类的Long字段）
            return examTypeCode.longValue();

        } catch (Exception e) {
            // 6. 统一捕获异常，封装报错信息
            String cellValue = getCellOriginalValue(cell);
            throw new BusinessException(String.format("第%d行【考试类型】解析失败：%s。原因：%s", rowIdx, cellValue, e.getMessage()), e);
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
            case 1: // 判断题
                if (correctCount != 1) {
                    throw new BusinessException("判断题必须有且只有一个正确答案");
                }
                break;
            case 0: // 单选题
                if (correctCount != 1) {
                    throw new BusinessException("单选题必须有且只有一个正确答案");
                }
                if (options.size() < 2) {
                    throw new BusinessException("单选题至少需要两个选项");
                }
                break;
            case 2: // 多选题
                if (correctCount < 1) {
                    throw new BusinessException("多选题必须至少有一个正确答案");
                }
                if (options.size() < 2) {
                    throw new BusinessException("多选题至少需要两个选项");
                }
                break;
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
            throw new BusinessException("工作表名称格式应为：分类名称,项目名称,知识类型名称");
        }
        return new SheetInfoDTO(parts[0], parts[1], parts[2]);
    }

    // 解析必填字段
    private String parseRequiredString(Cell cell, String fieldName, int rowIdx) {
        String value = (String)getCellValue(cell);
        if (value.isEmpty()) {
            throw new BusinessException(String.format("第%d行：%s不能为空", rowIdx + 1, fieldName));
        }
        return value;
    }

    // 解析题目类型
    private int parseQuestionType(Cell cell, int rowIdx) {
        String value = parseRequiredString(cell, "题目类型", rowIdx);
        try {
            int type = Integer.parseInt(value);
            if (type < 0 || type > 2) {
                throw new BusinessException(String.format("第%d行：题目类型值非法（0、1、2）", rowIdx + 1));
            }
            return type;
        } catch (NumberFormatException e) {
            throw new BusinessException(String.format("第%d行：题目类型必须为数字", rowIdx + 1));
        }
    }

    @Override
    public Long add(CategoryReq req) {
        Long add = super.add(req);
        stringRedisTemplate.delete(RedisConstant.EXAM_CATEGORY_SELECT);
        return add;
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