package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.mapper.ExamineePaymentAuditMapper;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.dto.ExamPlanProjectPaymentDTO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.exam.model.query.ExamineePaymentAuditQuery;
import top.continew.admin.exam.model.req.ExamineePaymentAuditReq;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditDetailResp;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
import top.continew.admin.exam.service.ExamineePaymentAuditService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.util.ExcelUtilReactive;
import top.continew.admin.util.InMemoryMultipartFile;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamineePaymentAuditServiceImpl extends BaseServiceImpl<
        ExamineePaymentAuditMapper, ExamineePaymentAuditDO, ExamineePaymentAuditResp,
        ExamineePaymentAuditDetailResp, ExamineePaymentAuditQuery, ExamineePaymentAuditReq>
        implements ExamineePaymentAuditService {

    @Resource
    private ExamineePaymentAuditMapper examineePaymentAuditMapper;

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ProjectMapper examProjectMapper;

    @Resource
    private UserMapper userMapper;

    private final ExcelUtilReactive excelUtilReactive;

    private final FileService fileService;

    @Value("${excel.template.url1}")
    private String excelTemplateUrl;

    @Override
    public Boolean verifyPaymentAudit(Long examineeId) {
        ValidationUtils.throwIfNull(examineeId, "考生ID不能为空");
        QueryWrapper<ExamineePaymentAuditDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("examinee_id", examineeId);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void generatePaymentAudit(Long examPlanId, Long examineeId, Long enrollId) throws Exception {
        ValidationUtils.throwIfNull(examPlanId, "考试计划ID不能为空");
        ValidationUtils.throwIfNull(examineeId, "考生ID不能为空");
        ValidationUtils.throwIfNull(enrollId, "报名ID不能为空");

        // 根据考试计划ID查询项目缴费金额
        ExamPlanProjectPaymentDTO paymentInfoDTO = getExamPlanProjectPaymentInfo(examPlanId);

        // 构建缴费审核记录并入库
        ExamineePaymentAuditDO paymentAuditDO = new ExamineePaymentAuditDO();
        paymentAuditDO.setExamineeId(examineeId);
        paymentAuditDO.setExamPlanId(examPlanId);
        paymentAuditDO.setEnrollId(enrollId);
        paymentAuditDO.setPaymentAmount(paymentInfoDTO.getPaymentAmount());
        paymentAuditDO.setAuditStatus(0); // 待缴费状态
        paymentAuditDO.setCreateTime(LocalDateTime.now());
        paymentAuditDO.setIsDeleted(false);

        int insertCount = examineePaymentAuditMapper.insert(paymentAuditDO);
        ValidationUtils.throwIf(insertCount <= 0, "生成缴费审核记录失败");
    }


    @Override
    public PageResp<ExamineePaymentAuditResp> page(ExamineePaymentAuditQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamineePaymentAuditDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tepa.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);
        IPage<ExamineePaymentAuditResp> page = baseMapper.getExamineePaymentAudits(
                new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper
        );
        PageResp<ExamineePaymentAuditResp> pageResp = PageResp.build(page, super.getListClass());
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamineePaymentAuditDO getByExamPlanIdAndExamineeId(Long examPlanId, Long examineeId) {
        // 1. 先查找当前记录
        ExamineePaymentAuditDO record = examineePaymentAuditMapper.selectOne(
                new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                        .eq(ExamineePaymentAuditDO::getExamPlanId, examPlanId)
                        .eq(ExamineePaymentAuditDO::getExamineeId, examineeId)
                        .eq(ExamineePaymentAuditDO::getIsDeleted, false)
                        .last("LIMIT 1")
        );

        if (record == null) {
            throw new IllegalStateException("未找到对应的缴费审核记录，请检查考试计划和考生信息。");
        }

        // 如果已存在通知单 URL，则直接返回该记录
        if (record.getAuditNoticeUrl() != null) {
            return record;
        }
        // 否则生成通知单 PDF
        // 获取缴费金额等信息
        ExamPlanProjectPaymentDTO paymentInfoDTO = getExamPlanProjectPaymentInfo(examPlanId);
        if (paymentInfoDTO == null) {
            throw new IllegalStateException("未找到考试计划缴费信息: " + examPlanId);
        }
        // 生成 PDF
        byte[] pdfBytes = generatePaymentNotice(
                examPlanId,
                examineeId,
                paymentInfoDTO.getExamPlanName(),
                paymentInfoDTO.getProjectName(),
                paymentInfoDTO.getPaymentAmount().longValue()
        );

        // 封装为 MultipartFile
        String nickname = userMapper.selectById(examineeId).getNickname();
        MultipartFile pdfFile = new InMemoryMultipartFile(
                "file",
                nickname + "_缴费通知单.pdf",
                "application/pdf",
                pdfBytes
        );

        // 上传 OSS
        FileInfoResp fileInfoResp = fileService.upload(pdfFile, new GeneralFileReq());
        if (fileInfoResp == null || fileInfoResp.getUrl() == null) {
            throw new IllegalStateException("文件上传失败");
        }
        String pdfUrl = fileInfoResp.getUrl();

        // 更新当前记录
        record.setAuditNoticeUrl(pdfUrl);
        examineePaymentAuditMapper.updateById(record);

        // 返回更新后的记录（此处 record 已包括新 URL）
        return record;
    }


    @Override
    public byte[] generatePaymentNotice(Long examPlanId, Long examineeId,
                                        String examPlanName, String projectName,
                                        Long paymentAmount) {
        ValidationUtils.throwIfNull(examPlanId, "考试计划ID不能为空");
        ValidationUtils.throwIfNull(examineeId, "考生ID不能为空");
        ValidationUtils.throwIfNull(examPlanName, "考试名称不能为空");
        ValidationUtils.throwIfNull(projectName, "收费项目不能为空");
        ValidationUtils.throwIfNull(paymentAmount, "缴费金额不能为空");

        String candidateName = userMapper.selectNicknameById(examineeId);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("noticeNo", generateUniqueNoticeNo());
        dataMap.put("candidateName", getSafeValue(candidateName));
        dataMap.put("examPlanName", getSafeValue(examPlanName));
        dataMap.put("projectName", getSafeValue(projectName));
        dataMap.put("paymentAmount", getSafeValue(String.valueOf(paymentAmount)));
        dataMap.putAll(splitAmountToUpper(paymentAmount.intValue()));

        // 阻塞生成 PDF
        return excelUtilReactive.generatePdfBytesSync(dataMap, excelTemplateUrl , new byte[0]);

    }

    private String generateUniqueNoticeNo() {
        String prefix = "TZSB_PAY_";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String randomNum = String.valueOf((int) (Math.random() * 1000000));
        return prefix + timestamp + "_" + randomNum;
    }

    private Map<String, String> splitAmountToUpper(int totalAmount) {
        Map<String, String> upperMap = new HashMap<>(5);
        int wan = (totalAmount / 10000) % 10;
        int qian = (totalAmount / 1000) % 10;
        int bai = (totalAmount / 100) % 10;
        int shi = (totalAmount / 10) % 10;
        int yuan = totalAmount % 10;
        upperMap.put("paymentAmountWan", digitToChineseUpper(wan));
        upperMap.put("paymentAmountQian", digitToChineseUpper(qian));
        upperMap.put("paymentAmountBai", digitToChineseUpper(bai));
        upperMap.put("paymentAmountShi", digitToChineseUpper(shi));
        upperMap.put("paymentAmountYuan", digitToChineseUpper(yuan));
        return upperMap;
    }

    private String digitToChineseUpper(int digit) {
        String[] upperNums = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        return (digit >= 0 && digit <= 9) ? upperNums[digit] : upperNums[0];
    }

    private String getSafeValue(String value) {
        return value == null ? "" : value.trim();
    }

    // 根据考试计划ID查询项目缴费金额
    private ExamPlanProjectPaymentDTO getExamPlanProjectPaymentInfo(Long examPlanId) {
        // 查询考试计划
        ExamPlanDO examPlanDO = examPlanMapper.selectOne(
                new LambdaQueryWrapper<ExamPlanDO>()
                        .eq(ExamPlanDO::getId, examPlanId)
                        .eq(ExamPlanDO::getIsDeleted, false)
        );
        ValidationUtils.throwIfNull(examPlanDO, "考试计划不存在");

        // 获取关联项目ID和考试计划名称
        Long examProjectId = examPlanDO.getExamProjectId();
        String examPlanName = examPlanDO.getExamPlanName(); // 提取考试计划名称
        ValidationUtils.throwIfNull(examProjectId, "考试计划未关联项目ID");
        ValidationUtils.throwIfBlank(examPlanName, "考试计划名称不能为空"); // 额外校验名称非空

        //  查询项目信息
        ProjectDO projectDO = examProjectMapper.selectOne(
                new LambdaQueryWrapper<ProjectDO>()
                        .eq(ProjectDO::getId, examProjectId)
                        .eq(ProjectDO::getIsDeleted, false)
        );
        ValidationUtils.throwIfNull(projectDO, "考试项目不存在");

        // 提取项目名称和缴费金额
        String projectName = projectDO.getProjectName();
        BigDecimal paymentAmount = BigDecimal.valueOf(projectDO.getExamFee());
        ValidationUtils.throwIfBlank(projectName, "项目名称不能为空");
        ValidationUtils.throwIfNull(paymentAmount, "项目未设置缴费金额");

        return new ExamPlanProjectPaymentDTO(examPlanName, projectName, paymentAmount);
    }
}
