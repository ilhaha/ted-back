package top.continew.admin.worker.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.common.constant.enums.WorkerApplyReviewStatus;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.common.enums.GenderEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.document.model.dto.DocFileDTO;
import top.continew.admin.document.model.entity.EnrollPreUploadDO;
import top.continew.admin.document.model.resp.EnrollPreUploadDetailResp;
import top.continew.admin.document.model.resp.EnrollPreUploadResp;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.training.mapper.CandidateTypeMapper;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.mapper.OrgClassMapper;
import top.continew.admin.training.model.entity.CandidateTypeDO;
import top.continew.admin.training.model.entity.OrgCandidateDO;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.training.model.entity.OrgClassDO;
import top.continew.admin.worker.mapper.WorkerApplyDocumentMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDocumentDO;
import top.continew.admin.worker.model.req.VerifyReq;
import top.continew.admin.worker.model.req.WorkerApplyReviewReq;
import top.continew.admin.worker.model.req.WorkerQrcodeUploadReq;
import top.continew.admin.worker.model.resp.ProjectNeedUploadDocVO;
import top.continew.admin.worker.model.resp.WorkerApplyVO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;
import top.continew.admin.worker.model.query.WorkerApplyQuery;
import top.continew.admin.worker.model.req.WorkerApplyReq;
import top.continew.admin.worker.model.resp.WorkerApplyDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyResp;
import top.continew.admin.worker.service.WorkerApplyService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 作业人员报名业务实现
 *
 * @author ilhaha
 * @since 2025/10/31 10:20
 */
@Service
@RequiredArgsConstructor
public class WorkerApplyServiceImpl extends BaseServiceImpl<WorkerApplyMapper, WorkerApplyDO, WorkerApplyResp, WorkerApplyDetailResp, WorkerApplyQuery, WorkerApplyReq> implements WorkerApplyService {

    @Resource
    private OrgClassCandidateMapper classCandidateMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Value("{examine.worker.init-password}")
    private String initPassword;

    @Value("${examine.deptId.examCenterId}")
    private Long examCenterId;

    @Resource
    private WorkerApplyDocumentMapper workerApplyDocumentMapper;

    @Resource
    private OrgClassCandidateMapper orgClassCandidateMapper;

    @Resource
    private CandidateTypeMapper candidateTypeMapper;

    /**
     * 根据身份证后六位、和班级id查询当前身份证报名信息
     *
     * @param verifyReq
     * @return
     */
    @Override
    public WorkerApplyVO verify(VerifyReq verifyReq) {
        WorkerApplyVO workerApplyVO = new WorkerApplyVO();

        // 初始化项目报名所需资料
        workerApplyVO.setProjectNeedUploadDocs(baseMapper.selectProjectNeedUploadDoc(verifyReq.getClassId()));

        // 如果有报名信息，可以在这里处理额外逻辑
        if (isUpload(verifyReq.getClassId(), verifyReq.getIdLast6())) {
            // TODO: 查出已有报名信息，填充到 workerApplyVO
        }

        return workerApplyVO;
    }

    /**
     * 作业人员通过二维码上传资料
     *
     * @param workerQrcodeUploadReq
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean submit(WorkerQrcodeUploadReq workerQrcodeUploadReq) {
        String idCardNumber = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(workerQrcodeUploadReq.getIdCardNumber()));
        ValidationUtils.throwIfBlank(idCardNumber, "身份证未上传");
        String phone = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(workerQrcodeUploadReq.getPhone()));
        ValidationUtils.throwIfBlank(phone, "身份信息未通过验证");
        // 先查是否重复上传
        Long classId = workerQrcodeUploadReq.getClassId();
        String idCardNumberLast6 = idCardNumber.substring(idCardNumber.length() - 6);
        ValidationUtils.throwIf(isUpload(classId, idCardNumberLast6), "您已提交过报名，请勿重复提交！");
        // 插入作业人员报名表
        WorkerApplyDO workerApplyDO = new WorkerApplyDO();
        BeanUtil.copyProperties(workerQrcodeUploadReq, workerApplyDO);
        workerApplyDO.setCandidateName(workerQrcodeUploadReq.getRealName());
        workerApplyDO.setQualificationName(workerQrcodeUploadReq.getQualificationName());
        workerApplyDO.setQualificationPath(workerQrcodeUploadReq.getQualificationFileUrl());
        workerApplyDO.setClassId(classId);
        workerApplyDO.setIdCardNumber(aesWithHMAC.encryptAndSign(idCardNumber));
        workerApplyDO.setPhone(aesWithHMAC.encryptAndSign(phone));
        baseMapper.insert(workerApplyDO);
        // 插入报名表的资料
        List<DocFileDTO> docFileList = workerQrcodeUploadReq.getDocFileList();
        if (!ObjectUtils.isEmpty(docFileList)) {
            ArrayList<WorkerApplyDocumentDO> workerApplyDocumentDOS = new ArrayList<>();
            for (DocFileDTO docFileDTO : docFileList) {
                for (String url : docFileDTO.getUrls()) {
                    WorkerApplyDocumentDO workerApplyDocumentDO = new WorkerApplyDocumentDO();
                    workerApplyDocumentDO.setWorkerApplyId(workerApplyDO.getId());
                    workerApplyDocumentDO.setTypeId(docFileDTO.getTypeId());
                    workerApplyDocumentDO.setDocPath(url);
                    workerApplyDocumentDOS.add(workerApplyDocumentDO);
                }
            }
            workerApplyDocumentMapper.insertBatch(workerApplyDocumentDOS);
        }
        return true;
    }

    /**
     * 审核作业人员报考
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean review(WorkerApplyReviewReq req) {
        // 1. 校验
        Integer status = req.getStatus();
        ValidationUtils.throwIf(
                (WorkerApplyReviewStatus.REJECTED.getValue().equals(status)
                        || WorkerApplyReviewStatus.FAKE_MATERIAL.getValue().equals(status))
                        && ObjectUtil.isEmpty(req.getRemark()),
                "请填写审核原因"
        );

        if (WorkerApplyReviewStatus.APPROVED.getValue().equals(status)) {
            // 2. 批量查询申请
            List<WorkerApplyDO> applyList = baseMapper.selectByIds(req.getReviewIds());
            if (CollUtil.isEmpty(applyList)) {
                return true;
            }

            // 3. 收集身份证号
            List<String> idCardList = applyList.stream()
                    .map(WorkerApplyDO::getIdCardNumber)
                    .filter(StrUtil::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());

            // 4. 一次性查出已有用户（用 username 对应身份证号）
            List<UserDO> existUsers = userMapper.selectList(
                    new LambdaQueryWrapper<UserDO>().in(UserDO::getUsername, idCardList)
            );
            Map<String, UserDO> userMap = existUsers.stream()
                    .collect(Collectors.toMap(UserDO::getUsername, Function.identity()));

            // 5. 找出需要新建的用户
            List<UserDO> newUsers = new ArrayList<>();
            for (WorkerApplyDO apply : applyList) {
                if (!userMap.containsKey(apply.getIdCardNumber())) {
                    UserDO newUser = new UserDO();
                    newUser.setUsername(apply.getIdCardNumber());
                    newUser.setNickname(apply.getCandidateName());
                    newUser.setPassword(initPassword);
                    newUser.setGender(apply.getGender().equals(GenderEnum.MALE.getDescription()) ? GenderEnum.MALE : GenderEnum.FEMALE);
                    newUser.setPhone(apply.getPhone());
                    newUser.setDescription("作业人员");
                    newUser.setStatus(DisEnableStatusEnum.ENABLE);
                    newUser.setIsSystem(false);
                    newUser.setDeptId(examCenterId);
                    newUsers.add(newUser);
                }
            }

            // 6. 批量插入新用户
            if (CollUtil.isNotEmpty(newUsers)) {
                userMapper.insertBatch(newUsers);
                newUsers.forEach(u -> userMap.put(u.getUsername(), u));
            }

            // 7. 构建候选人 & 类型数据
            List<OrgClassCandidateDO> classCandidates = new ArrayList<>();
            List<CandidateTypeDO> candidateTypes = new ArrayList<>();
            for (WorkerApplyDO apply : applyList) {
                UserDO user = userMap.get(apply.getIdCardNumber());
                if (user == null) {
                    throw new IllegalStateException("用户创建失败: " + apply.getIdCardNumber());
                }

                OrgClassCandidateDO candidateDO = new OrgClassCandidateDO();
                candidateDO.setCandidateId(user.getId());
                candidateDO.setClassId(apply.getClassId());
                classCandidates.add(candidateDO);

                CandidateTypeDO typeDO = new CandidateTypeDO();
                typeDO.setCandidateId(user.getId());
                candidateTypes.add(typeDO);
            }

            // 8. 批量插入候选人与类型
            orgClassCandidateMapper.insertBatch(classCandidates);
            candidateTypeMapper.insertBatch(candidateTypes);
        }
        // 9. 批量更新审核状态
        String remark = req.getRemark();
        baseMapper.update(new LambdaUpdateWrapper<WorkerApplyDO>()
                .set(!ObjectUtils.isEmpty(remark), WorkerApplyDO::getRemark,remark)
                .set(WorkerApplyDO::getStatus, req.getStatus())
                .in(WorkerApplyDO::getId, req.getReviewIds()));
        return true;
    }


    /**
     * 重写page
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<WorkerApplyResp> page(WorkerApplyQuery query, PageQuery pageQuery) {
        QueryWrapper<WorkerApplyDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("twa.is_deleted", 0);

        IPage<WorkerApplyDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
        List<WorkerApplyDetailResp> records = page.getRecords();
        if (!ObjectUtil.isEmpty(records)) {
            page.setRecords(records.stream().map(item -> {
                String idCardNumberDB = item.getIdCardNumber();
                String idCardNumber = aesWithHMAC.verifyAndDecrypt(idCardNumberDB);
                item.setIdCardNumber(CharSequenceUtil.replaceByCodePoint(idCardNumber, 2, idCardNumber.length() - 5, '*'));
                item.setPhone(aesWithHMAC.verifyAndDecrypt(item.getPhone()));
                return item;
            }).toList());
        }
        PageResp<WorkerApplyResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    private Boolean isUpload(Long classId, String uploadIdCardLast6) {
        // 查出该班级所有已有考生
        List<OrgClassCandidateDO> orgClassCandidateDOS = classCandidateMapper.selectList(
                new LambdaQueryWrapper<OrgClassCandidateDO>()
                        .eq(OrgClassCandidateDO::getClassId, classId)
        );
        boolean isApply = false;
        if (CollUtil.isNotEmpty(orgClassCandidateDOS)) {
            // 提取所有考生ID
            List<Long> candidateIds = orgClassCandidateDOS.stream()
                    .map(OrgClassCandidateDO::getCandidateId)
                    .toList();

            // 批量查出这些考生的用户信息
            List<UserDO> userDOS = userMapper.selectByIds(candidateIds);
            // 解密并提取身份证后六位
            for (UserDO userDO : userDOS) {
                String idCard = aesWithHMAC.verifyAndDecrypt(userDO.getUsername());
                String last6 = idCard.substring(idCard.length() - 6);
                if (uploadIdCardLast6.equalsIgnoreCase(last6)) {
                    isApply = true;
                    break;
                }
            }
        }
        return isApply;
    }

}