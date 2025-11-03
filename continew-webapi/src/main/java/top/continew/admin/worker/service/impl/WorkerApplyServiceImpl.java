package top.continew.admin.worker.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.common.enums.GenderEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.document.model.dto.DocFileDTO;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.mapper.OrgClassMapper;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.training.model.entity.OrgClassDO;
import top.continew.admin.worker.mapper.WorkerApplyDocumentMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDocumentDO;
import top.continew.admin.worker.model.req.VerifyReq;
import top.continew.admin.worker.model.req.WorkerQrcodeUploadReq;
import top.continew.admin.worker.model.resp.ProjectNeedUploadDocVO;
import top.continew.admin.worker.model.resp.WorkerApplyVO;
import top.continew.starter.core.exception.BusinessException;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private OrgClassMapper orgClassMapper;

    /**
     * 根据身份证后六位、和班级id查询当前身份证报名信息
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
     * @param workerQrcodeUploadReq
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean submit(WorkerQrcodeUploadReq workerQrcodeUploadReq) {
        // 先查是否重复上传
        Long classId = workerQrcodeUploadReq.getClassId();
        String idCardNumber = workerQrcodeUploadReq.getIdCardNumber();
        String idCardNumberLast6 = idCardNumber.substring(idCardNumber.length() - 6);
        ValidationUtils.throwIf(isUpload(classId,idCardNumberLast6),"您已提交过报名，请勿重复提交！");
        // 插入作业人员报名表
        WorkerApplyDO workerApplyDO = new WorkerApplyDO();
        BeanUtil.copyProperties(workerQrcodeUploadReq,workerApplyDO);
        workerApplyDO.setCandidateName(workerQrcodeUploadReq.getRealName());
        workerApplyDO.setQualificationName(workerQrcodeUploadReq.getQualificationName());
        workerApplyDO.setQualificationPath(workerQrcodeUploadReq.getQualificationFileUrl());
        workerApplyDO.setClassId(classId);
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
        // 插入用户表，如果存在不插入
//        String username = aesWithHMAC.encryptAndSign(workerQrcodeUploadReq.getIdCardNumber());
//        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername,username));
//        if (userCount < 0) {
//            UserDO userDO = new UserDO();
//            userDO.setUsername(username);
//            userDO.setNickname(workerQrcodeUploadReq.getRealName());
//            userDO.setPassword(initPassword);
//            userDO.setGender(workerQrcodeUploadReq.getGender().equals(GenderEnum.MALE.getDescription()) ? GenderEnum.FEMALE : GenderEnum.MALE);
//            userDO.setPhone(workerQrcodeUploadReq.getPhone());
//            userDO.setDescription("作业人员");
//            userDO.setStatus(DisEnableStatusEnum.ENABLE);
//            userDO.setIsSystem(false);
//            userDO.setDeptId(examCenterId);
//            userMapper.insert(userDO);
//        }

        return true;
    }

    /**
     * 重写page
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<WorkerApplyResp> page(WorkerApplyQuery query, PageQuery pageQuery) {
        return super.page(query, pageQuery);
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