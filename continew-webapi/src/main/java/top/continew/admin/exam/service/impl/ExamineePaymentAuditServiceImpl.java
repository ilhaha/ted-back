package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.UploadTypeConstants;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.mapper.ExamineePaymentAuditMapper;
import top.continew.admin.exam.mapper.ProjectMapper;
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

        // 查询考试计划
        ExamPlanDO examPlanDO = examPlanMapper.selectOne(
                new LambdaQueryWrapper<ExamPlanDO>()
                        .eq(ExamPlanDO::getId, examPlanId)
                        .eq(ExamPlanDO::getIsDeleted, false)
        );
        ValidationUtils.throwIfNull(examPlanDO, "考试计划不存在");

        Long examProjectId = examPlanDO.getExamProjectId();
        ValidationUtils.throwIfNull(examProjectId, "考试计划未关联项目ID");

        // 查询项目缴费金额
        ProjectDO projectDO = examProjectMapper.selectOne(
                new LambdaQueryWrapper<ProjectDO>()
                        .eq(ProjectDO::getId, examProjectId)
                        .eq(ProjectDO::getIsDeleted, false)
        );
        ValidationUtils.throwIfNull(projectDO, "考试项目不存在");

        BigDecimal paymentAmount = BigDecimal.valueOf(projectDO.getExamFee());
        ValidationUtils.throwIfNull(paymentAmount, "项目未设置缴费金额");

        // 构建缴费审核记录并入库
        ExamineePaymentAuditDO paymentAuditDO = new ExamineePaymentAuditDO();
        paymentAuditDO.setExamineeId(examineeId);
        paymentAuditDO.setExamPlanId(examPlanId);
        paymentAuditDO.setEnrollId(enrollId);
        paymentAuditDO.setPaymentAmount(paymentAmount);
        paymentAuditDO.setAuditStatus(0); // 待缴费状态
        paymentAuditDO.setCreateTime(LocalDateTime.now());
        paymentAuditDO.setIsDeleted(false);

        int insertCount = examineePaymentAuditMapper.insert(paymentAuditDO);
        ValidationUtils.throwIf(insertCount <= 0, "生成缴费审核记录失败");

        // 生成缴费通知单 PDF
        byte[] pdfBytes = generatePaymentNotice(examPlanId, examineeId,
                examPlanDO.getExamPlanName(), projectDO.getProjectName(),
                paymentAmount.longValue());

        // 使用 InMemoryMultipartFile 包装 PDF
        MultipartFile pdfFile = new InMemoryMultipartFile(
                "file",
                userMapper.selectById(examineeId).getNickname() + "_缴费通知单.pdf",
                "application/pdf",
                pdfBytes
        );

        // 构建上传请求对象
        GeneralFileReq fileReq = new GeneralFileReq();

        // 上传 OSS，获取 URL
        FileInfoResp fileInfoResp = fileService.upload(pdfFile, fileReq);
        String pdfUrl = fileInfoResp.getUrl();

        // 更新数据库记录，保存通知单 URL
        paymentAuditDO.setAuditNoticeUrl(pdfUrl);
        examineePaymentAuditMapper.updateById(paymentAuditDO);
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
    public byte[] generatePaymentNotice(Long examPlanId, Long examineeId,
                                        String examPlanName, String projectName,
                                        Long paymentAmount) {
        ValidationUtils.throwIfNull(examPlanId, "考试计划ID不能为空");
        ValidationUtils.throwIfNull(examineeId, "考生ID不能为空");
        ValidationUtils.throwIfNull(examPlanName, "考试名称不能为空");
        ValidationUtils.throwIfNull(projectName, "收费项目不能为空");
        ValidationUtils.throwIfNull(paymentAmount, "缴费金额不能为空");

        UserDO userDO = userMapper.selectOne(
                new LambdaQueryWrapper<UserDO>()
                        .eq(UserDO::getId, examineeId)
                        .select(UserDO::getNickname)
                        .last("LIMIT 1")
        );
        ValidationUtils.throwIfNull(userDO, "考生信息不存在（ID：" + examineeId + "）");

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("noticeNo", generateUniqueNoticeNo());
        dataMap.put("candidateName", getSafeValue(userDO.getNickname()));
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
}
