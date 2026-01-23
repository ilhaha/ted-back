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

package top.continew.admin.worker.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import net.dreamlu.mica.core.utils.StringUtil;
import net.dreamlu.mica.core.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.DocumentConstant;
import top.continew.admin.common.constant.EnrollStatusConstant;
import top.continew.admin.common.constant.WorkerApplyCheckConstants;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.common.enums.GenderEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.document.mapper.DocumentTypeMapper;
import top.continew.admin.document.model.dto.DocFileDTO;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.mapper.WeldingExamApplicationMapper;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.WeldingExamApplicationDO;
import top.continew.admin.exam.service.ExamineePaymentAuditService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.mapper.UserRoleMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.entity.UserRoleDO;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.model.resp.IdCardFileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.training.mapper.CandidateTypeMapper;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.mapper.OrgClassMapper;
import top.continew.admin.training.mapper.OrgMapper;
import top.continew.admin.training.model.entity.CandidateTypeDO;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.training.model.entity.OrgClassDO;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.worker.mapper.WorkerApplyDocumentMapper;
import top.continew.admin.worker.model.dto.UploadGroupDTO;
import top.continew.admin.worker.model.dto.WorkerApplyCheckDTO;
import top.continew.admin.worker.model.dto.WorkerApplyDocAndNameDTO;
import top.continew.admin.worker.model.entity.WorkerApplyDocumentDO;
import top.continew.admin.worker.model.req.*;
import top.continew.admin.worker.model.resp.*;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;
import top.continew.admin.worker.model.query.WorkerApplyQuery;
import top.continew.admin.worker.service.WorkerApplyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private UserMapper userMapper;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Value("{examine.worker.init-password}")
    private String initPassword;

    @Value("${examine.deptId.examCenterId}")
    private Long examCenterId;

    @Value("${examine.userRole.workerId}")
    private Long workerId;

    @Resource
    private WorkerApplyDocumentMapper workerApplyDocumentMapper;

    @Resource
    private OrgClassCandidateMapper orgClassCandidateMapper;

    @Resource
    private CandidateTypeMapper candidateTypeMapper;

    @Resource
    private DocumentTypeMapper documentTypeMapper;

    @Resource
    private EnrollMapper enrollMapper;

    @Resource
    private OrgClassMapper orgClassMapper;

    private final ExamineePaymentAuditService examineePaymentAuditService;

    @Resource
    private UserRoleMapper userRoleMapper;

    private final UploadService uploadService;

    private final OrgMapper orgMapper;

    private final WeldingExamApplicationMapper weldingExamApplicationMapper;

    @Value("${welding.metal-project-id}")
    private Long metalProjectId;

    @Value("${welding.nonmetal-project-id}")
    private Long nonmetalProjectId;


    @Value("${document.id-card-id}")
    private Long documentIdCardId;

    /**
     * 根据身份证后六位、和班级id查询当前身份证报名信息
     *
     * @param verifyReq
     * @return
     */
    @Override
    public WorkerApplyVO verify(VerifyReq verifyReq) {
        // 初始化返回对象
        WorkerApplyVO workerApplyVO = new WorkerApplyVO();

        // 1️ 校验二维码合法性并解析班级ID

        String idCardNumber = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(verifyReq.getIdCard()));
        ValidationUtils.throwIfBlank(idCardNumber, "未输入身份证号");

        String decryptedClassId = aesWithHMAC.verifyAndDecrypt(verifyReq.getClassId());
        ValidationUtils.throwIfNull(decryptedClassId, "二维码已被篡改或参数缺失，请重新获取");
        Long classId = Long.valueOf(decryptedClassId);

        // 2 查询班级并验证状态
        OrgClassDO orgClassDO = orgClassMapper.selectById(classId);
//        ValidationUtils.throwIf(ObjectUtil.isNull(orgClassDO) || ClassStatusEnum.STOPPED.getValue()
//                .equals(orgClassDO.getStatus()), "该班级已停止报名");
        ValidationUtils.throwIfNull(orgClassDO, "二维码已过期，请重新获取");
        Long projectId = orgClassDO.getProjectId();
        boolean isWelding = metalProjectId.equals(projectId) || nonmetalProjectId.equals(projectId);
        if (isWelding) {
            List<WeldingExamApplicationDO> weldingExamApplicationDOS =
                    weldingExamApplicationMapper.selectList(
                            new LambdaQueryWrapper<WeldingExamApplicationDO>()
                                    .eq(WeldingExamApplicationDO::getOrgId, orgClassDO.getOrgId())
                                    .eq(WeldingExamApplicationDO::getStatus,
                                            WeldingExamApplicationStatusEnum.PASS_REVIEW.getValue())
                                    .eq(WeldingExamApplicationDO::getWeldingType,
                                            metalProjectId.equals(projectId) ? WeldingTypeEnum.METAL.getValue() : WeldingTypeEnum.NON_METAL.getValue())
                                    .select(WeldingExamApplicationDO::getProjectCode)
                    );
            List<String> orgWeldingProjectCodes = weldingExamApplicationDOS.stream()
                    .map(WeldingExamApplicationDO::getProjectCode).toList();
            workerApplyVO.setWeldingProjectCodes(orgWeldingProjectCodes);
        }
        // 3 初始化项目报名所需资料
        workerApplyVO.setProjectNeedUploadDocs(baseMapper.selectProjectNeedUploadDoc(classId));

        // 3 初始化项目信息
        workerApplyVO.setProjectInfo(baseMapper.getProjectInfoByClassId(classId));

        // 判断该考试是否已存在在班级
        String encryptIdCard = aesWithHMAC.encryptAndSign(idCardNumber);
        WorkerApplyDO workerApplyDO = baseMapper.selectOne(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getClassId, classId)
                .eq(WorkerApplyDO::getIdCardNumber, encryptIdCard));
        if (ObjectUtil.isNotNull(workerApplyDO)) {
            ValidationUtils.throwIf(WorkerApplyTypeEnum.ORG_IMPORT.getValue()
                    .equals(workerApplyDO.getApplyType()), "您的信息已被机构批量导入，二维码报名功能不可使用");

            // 当前班级已存在报名记录 → 查询上传资料并组装
            WorkerUploadedDocsVO uploadedDocs = baseMapper.selectWorkerUploadedDocs(classId, encryptIdCard);

            if (ObjectUtil.isNotNull(uploadedDocs) && ObjectUtil.isNotEmpty(uploadedDocs.getDocuments())) {
                if (isWelding) {
                    uploadedDocs.setWeldingProjectCode(Arrays.asList(uploadedDocs.getWeldingProjectCodeStr().split(",")));
                }
                // 解析 JSON 数组并封装
                List<WorkerApplyDocumentVO> docList = JSONUtil.parseArray(uploadedDocs.getDocuments())
                        .stream()
                        .map(obj -> {
                            JSONObject jsonObj = (JSONObject) obj;
                            WorkerApplyDocumentVO vo = new WorkerApplyDocumentVO();
                            vo.setTypeId(jsonObj.getLong("typeId"));
                            vo.setTypeName(jsonObj.getStr("typeName"));
                            vo.setDocPaths(jsonObj.getStr("docPaths"));
                            return vo;
                        })
                        .toList();
                uploadedDocs.setWorkerApplyDocuments(docList);
            }

            workerApplyVO.setWorkerUploadedDocs(uploadedDocs);

        }

        // 5 查询该身份证是否已在当前班级报名或者同项目下的班级报名
        // 查出该项目下的所有班级
        //        List<Long> allClassIds = orgClassMapper.selectList(new LambdaQueryWrapper<OrgClassDO>()
        //                .eq(OrgClassDO::getProjectId, orgClassDO.getProjectId())
        //                .select(OrgClassDO::getId)).stream().map(OrgClassDO::getId).toList();

        //        WorkerApplyCheckDTO workerApplyCheckDTO = findIdCardIfExists(classId, verifyReq.getIdLast6(), allClassIds);
        //        ValidationUtils.throwIf(WorkerApplyCheckConstants.OTHER.equals(workerApplyCheckDTO
        //                .getStatus()), "您已在其他班级报考该项目，请勿重复报名");

        //        if (WorkerApplyCheckConstants.CURRENT.equals(workerApplyCheckDTO.getStatus())) {
        //            // 当前班级已存在报名记录 → 查询上传资料并组装
        //            WorkerUploadedDocsVO uploadedDocs = baseMapper.selectWorkerUploadedDocs(classId, aesWithHMAC
        //                    .encryptAndSign(workerApplyCheckDTO.getIdCardNumber()));
        //
        //            if (ObjectUtil.isNotNull(uploadedDocs) && ObjectUtil.isNotEmpty(uploadedDocs.getDocuments())) {
        //                // 解析 JSON 数组并封装
        //                List<WorkerApplyDocumentVO> docList = JSONUtil.parseArray(uploadedDocs.getDocuments())
        //                        .stream()
        //                        .map(obj -> {
        //                            JSONObject jsonObj = (JSONObject) obj;
        //                            WorkerApplyDocumentVO vo = new WorkerApplyDocumentVO();
        //                            vo.setTypeId(jsonObj.getLong("typeId"));
        //                            vo.setTypeName(jsonObj.getStr("typeName"));
        //                            vo.setDocPaths(jsonObj.getStr("docPaths"));
        //                            return vo;
        //                        })
        //                        .toList();
        //                uploadedDocs.setWorkerApplyDocuments(docList);
        //            }
        //
        //            workerApplyVO.setWorkerUploadedDocs(uploadedDocs);
        //        }
        return workerApplyVO;
    }

    /**
     * 作业人员通过二维码上传资料
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean submit(WorkerQrcodeUploadReq req) {
        // 1 解密与校验
        String decryptedClassId = aesWithHMAC.verifyAndDecrypt(req.getClassId());
        ValidationUtils.throwIfNull(decryptedClassId, "二维码已被篡改或参数缺失，请重新获取");
        Long classId = Long.valueOf(decryptedClassId);

        String idCardNumber = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getIdCardNumber()));
        ValidationUtils.throwIfBlank(idCardNumber, "身份证未上传");

        String phone = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getPhone()));
        ValidationUtils.throwIfBlank(phone, "身份信息未通过验证");

        // 2 校验班级状态
        OrgClassDO orgClass = orgClassMapper.selectById(classId);
        ValidationUtils.throwIf(ObjectUtil.isNull(orgClass) || ClassStatusEnum.STOPPED.getValue()
                .equals(orgClass.getStatus()), "该班级已停止接收报名人员");

        // 3 查重：当前班级和是否已有此人
        String encryptIdCard = aesWithHMAC.encryptAndSign(idCardNumber);
        WorkerApplyDO workerApplyDO = baseMapper.selectOne(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getClassId, classId)
                .eq(WorkerApplyDO::getIdCardNumber, encryptIdCard));
        if (ObjectUtil.isNotNull(workerApplyDO)) {
            ValidationUtils.throwIf(WorkerApplyTypeEnum.ORG_IMPORT.getValue()
                    .equals(workerApplyDO.getApplyType()), "您的信息已被机构批量导入，无法提交");
            ValidationUtils.throwIf(Boolean.TRUE, "您已提交过报名，请勿重复提交！");
        }
        //        List<Long> allClassIds = orgClassMapper.selectList(new LambdaQueryWrapper<OrgClassDO>()
        //                .eq(OrgClassDO::getProjectId, orgClass.getProjectId())
        //                .select(OrgClassDO::getId)).stream().map(OrgClassDO::getId).toList();

        //        String idCardLast6 = StrUtil.subSuf(idCardNumber, idCardNumber.length() - 6);
        //        WorkerApplyCheckDTO workerApplyCheckDTO = findIdCardIfExists(classId, idCardLast6, allClassIds);
        //        ValidationUtils.throwIf(WorkerApplyCheckConstants.CURRENT.equals(workerApplyCheckDTO
        //                .getStatus()), "您已提交过报名，请勿重复提交！");
        //        ValidationUtils.throwIf(WorkerApplyCheckConstants.OTHER.equals(workerApplyCheckDTO
        //                .getStatus()), "您已在其他班级报考该项目，请勿重复报名");

        // 5 插入报名信息
        WorkerApplyDO apply = new WorkerApplyDO();
        apply.setClassId(classId);
        apply.setCandidateName(req.getRealName());
        apply.setGender(req.getGender());
        apply.setPhone(aesWithHMAC.encryptAndSign(phone));
        apply.setQualificationPath(req.getQualificationFileUrl());
        apply.setQualificationName(req.getQualificationName());
        apply.setIdCardNumber(aesWithHMAC.encryptAndSign(idCardNumber));
        apply.setIdCardPhotoFront(req.getIdCardPhotoFront());
        apply.setIdCardPhotoBack(req.getIdCardPhotoBack());
        apply.setFacePhoto(req.getFacePhoto());
        apply.setApplyType(WorkerApplyTypeEnum.SCAN_APPLY.getValue());
        apply.setStatus(WorkerApplyReviewStatusEnum.DOC_UPLOADED.getValue());
        apply.setIdCardAddress(req.getIdCardAddress());
        apply.setAddress(req.getAddress());
        apply.setWorkUnit(req.getWorkUnit());
        apply.setEducation(req.getEducation());
        apply.setPoliticalStatus(req.getPoliticalStatus());
        List<String> weldingProjectCode = req.getWeldingProjectCode();
        if (ObjectUtil.isNotEmpty(weldingProjectCode)) {
            String weldingProjectCodeStr = String.join(",", weldingProjectCode);
            apply.setWeldingProjectCode(weldingProjectCodeStr);
        }
        baseMapper.insert(apply);

        // 6 插入附件信息
        if (CollUtil.isNotEmpty(req.getDocFileList())) {
            List<WorkerApplyDocumentDO> docs = req.getDocFileList()
                    .stream()
                    .flatMap(doc -> doc.getUrls().stream().map(url -> {
                        WorkerApplyDocumentDO document = new WorkerApplyDocumentDO();
                        document.setWorkerApplyId(apply.getId());
                        document.setTypeId(doc.getTypeId());
                        document.setDocPath(url);
                        return document;
                    }))
                    .toList();

            workerApplyDocumentMapper.insertBatch(docs);
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
        ValidationUtils.throwIf((WorkerApplyReviewStatusEnum.REJECTED.getValue()
                .equals(status) || WorkerApplyReviewStatusEnum.FAKE_MATERIAL.getValue().equals(status)) && ObjectUtil
                .isEmpty(req.getRemark()), "请填写审核原因");

        if (WorkerApplyReviewStatusEnum.APPROVED.getValue().equals(status)) {
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
            List<UserDO> existUsers = userMapper.selectList(new LambdaQueryWrapper<UserDO>()
                    .in(UserDO::getUsername, idCardList));
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
                    newUser.setGender(apply.getGender().equals(GenderEnum.MALE.getDescription())
                            ? GenderEnum.MALE
                            : GenderEnum.FEMALE);
                    newUser.setPhone(apply.getPhone());
                    newUser.setDescription("作业人员");
                    newUser.setStatus(DisEnableStatusEnum.ENABLE);
                    newUser.setIsSystem(false);
                    newUser.setDeptId(examCenterId);
                    newUser.setAvatar(apply.getFacePhoto());
                    newUsers.add(newUser);
                }
            }

            // 6. 批量插入新用户
            if (CollUtil.isNotEmpty(newUsers)) {
                newUsers = new ArrayList<>(newUsers.stream()
                        .collect(Collectors.toMap(UserDO::getUsername, Function.identity(), (u1, u2) -> u1))
                        .values());
                userMapper.insertBatch(newUsers);
                newUsers.forEach(u -> userMap.put(u.getUsername(), u));

                // 添加角色
                List<UserRoleDO> userRoleDOS = newUsers.stream().map(item -> {
                    UserRoleDO userRoleDO = new UserRoleDO();
                    userRoleDO.setRoleId(workerId);
                    userRoleDO.setUserId(item.getId());
                    return userRoleDO;
                }).toList();
                userRoleMapper.insertBatch(userRoleDOS);
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
                typeDO.setType(CandidateTypeEnum.WORKER.getValue());
                candidateTypes.add(typeDO);

            }

            // 8. 批量插入用户与类型
            orgClassCandidateMapper.insertBatch(classCandidates);
            candidateTypeMapper.insertBatchIgnore(candidateTypes);

            // 生成新的缴费通知单
            Long classId = req.getClassId();
            examineePaymentAuditService.generatePaymentAuditByClassId(classId);
        }
        // 9. 批量更新审核状态
        String remark = req.getRemark();
        int updateRow = baseMapper.update(new LambdaUpdateWrapper<WorkerApplyDO>().set(!ObjectUtils
                        .isEmpty(remark), WorkerApplyDO::getRemark, remark)
                .set(WorkerApplyDO::getStatus, req.getStatus())
                .in(WorkerApplyDO::getId, req.getReviewIds()));

        // 如果审核状态是虚假材料，那么给机构扣分
        if (updateRow > 0 && WorkerApplyReviewStatusEnum.FAKE_MATERIAL.getValue().equals(status)) {

            List<Long> reviewIds = req.getReviewIds();
            if (CollUtil.isEmpty(reviewIds)) {
                return true;
            }

            // 1. 查询 review 对应班级和机构
            List<Map<String, Object>> orgInfoList = baseMapper.selectOrgIdByReviewIds(reviewIds);
            // 每条 Map 包含：review_id、org_id、credit_score

            if (CollUtil.isEmpty(orgInfoList)) {
                return true;
            }

            // 2. 统计每个机构的扣分次数
            Map<Long, Long> orgIdToDeductCount = orgInfoList.stream()
                    .collect(Collectors.groupingBy(m -> (Long) m.get("org_id"), Collectors.counting()));

            // 3. 构建 OrgDO 批量更新
            List<OrgDO> updateOrgs = orgIdToDeductCount.entrySet().stream().map(entry -> {
                Long orgId = entry.getKey();
                Long count = entry.getValue();
                OrgDO orgDO = new OrgDO();
                orgDO.setId(orgId);
                orgDO.setCreditScore(orgMapper.selectById(orgId).getCreditScore() - count.intValue());
                return orgDO;
            }).toList();

            // 4. 批量更新
            orgMapper.updateBatchById(updateOrgs);
        }

        return true;
    }

    /**
     * 作业人员通过二维码重新上传资料
     *
     * @param workerQrcodeUploadReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean restSubmit(WorkerQrcodeUploadReq workerQrcodeUploadReq) {
        String aseClassId = aesWithHMAC.verifyAndDecrypt(workerQrcodeUploadReq.getClassId());
        ValidationUtils.throwIfNull(aseClassId, "二维码已被篡改或参数缺失，请重新获取");
        Long classId = Long.valueOf(aseClassId);
        String idCardNumber = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(workerQrcodeUploadReq
                .getIdCardNumber()));
        ValidationUtils.throwIfBlank(idCardNumber, "身份证未上传");
        String phone = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(workerQrcodeUploadReq
                .getPhone()));
        ValidationUtils.throwIfBlank(phone, "身份信息未通过验证");
        // 修改作业人员报名表
        // 先查出原来的资料
        LambdaQueryWrapper<WorkerApplyDO> workerApplyDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        workerApplyDOLambdaQueryWrapper.eq(WorkerApplyDO::getIdCardNumber, aesWithHMAC.encryptAndSign(idCardNumber))
                .eq(WorkerApplyDO::getClassId, classId);
        WorkerApplyDO workerApplyDO = baseMapper.selectOne(workerApplyDOLambdaQueryWrapper);
        ValidationUtils.throwIfNull(workerApplyDO, "未查询到身份证报名记录，可能已在其他班级报名");
        // 先删除掉原来的所有资料
        workerApplyDocumentMapper.delete(new LambdaQueryWrapper<WorkerApplyDocumentDO>()
                .eq(WorkerApplyDocumentDO::getWorkerApplyId, workerApplyDO.getId()));
        // 再插入新的资料
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
        // 更新报名表信息
        workerApplyDO.setClassId(classId);
        workerApplyDO.setCandidateName(workerQrcodeUploadReq.getRealName());
        workerApplyDO.setGender(workerQrcodeUploadReq.getGender());
        workerApplyDO.setPhone(aesWithHMAC.encryptAndSign(phone));
        workerApplyDO.setQualificationPath(workerQrcodeUploadReq.getQualificationFileUrl());
        workerApplyDO.setQualificationName(workerQrcodeUploadReq.getQualificationName());
        workerApplyDO.setIdCardNumber(aesWithHMAC.encryptAndSign(idCardNumber));
        workerApplyDO.setIdCardPhotoFront(workerQrcodeUploadReq.getIdCardPhotoFront());
        workerApplyDO.setIdCardPhotoBack(workerQrcodeUploadReq.getIdCardPhotoBack());
        workerApplyDO.setFacePhoto(workerQrcodeUploadReq.getFacePhoto());
        workerApplyDO.setStatus(WorkerApplyReviewStatusEnum.PENDING_REVIEW.getValue());
        workerApplyDO.setRemark("");
        workerApplyDO.setAddress(workerQrcodeUploadReq.getAddress());
        workerApplyDO.setWorkUnit(workerQrcodeUploadReq.getWorkUnit());
        workerApplyDO.setEducation(workerQrcodeUploadReq.getEducation());
        workerApplyDO.setPoliticalStatus(workerQrcodeUploadReq.getPoliticalStatus());
        List<String> weldingProjectCode = workerQrcodeUploadReq.getWeldingProjectCode();
        if (ObjectUtil.isNotEmpty(weldingProjectCode)) {
            String weldingProjectCodeStr = String.join(",", weldingProjectCode);
            workerApplyDO.setWeldingProjectCode(weldingProjectCodeStr);
        }
        return baseMapper.updateById(workerApplyDO) > 0;
    }

    /**
     * 机构批量导入
     *
     * @param workerOrgImportReqs
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean orgImport(List<WorkerOrgImportReq> workerOrgImportReqs) {
        // 1. 校验
        ValidationUtils.throwIfEmpty(workerOrgImportReqs, "未选择导入的数据");

        Long classId = workerOrgImportReqs.get(0).getClassId();

        // 2. 获取加密身份证号
        List<String> importIdCard = workerOrgImportReqs.stream()
                .map(req -> aesWithHMAC.encryptAndSign(req.getIdCardNumber()))
                .collect(Collectors.toList());

        // 3. 查询已存在的身份证
        List<WorkerApplyDO> existingWorkers = baseMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getClassId, classId)
                .in(WorkerApplyDO::getIdCardNumber, importIdCard));
        ValidationUtils.throwIf(!existingWorkers.isEmpty(), "以下作业人员已存在：" +
                existingWorkers.stream().map(WorkerApplyDO::getCandidateName).collect(Collectors.joining(", ")));

        // 4. 构造导入数据
        List<WorkerApplyDO> toImport = workerOrgImportReqs.stream().map(item -> {
            WorkerApplyDO workerApplyDO = new WorkerApplyDO();
            BeanUtil.copyProperties(item, workerApplyDO);
            workerApplyDO.setPhone(aesWithHMAC.encryptAndSign(item.getPhone()));
            String encryptIdCardNo = aesWithHMAC.encryptAndSign(item.getIdCardNumber());
            workerApplyDO.setIdCardNumber(encryptIdCardNo);
            workerApplyDO.setUpdateTime(LocalDateTime.now());
            return workerApplyDO;
        }).toList();

        if (toImport.isEmpty()) return Boolean.TRUE;

        baseMapper.insertBatch(toImport);

        // 5. 半年前时间
        LocalDateTime halfYearAgo = LocalDateTime.now().minusMonths(6);
        Long orgId = orgMapper.getOrgId(TokenLocalThreadUtil.get().getUserId()).getId();

        // 6. 查询半年内已审核通过记录
        List<WorkerApplyDO> approvedList = baseMapper.selectWorkerApplyByProjectAndIdCards(
                classId, orgId, WorkerApplyReviewStatusEnum.APPROVED.getValue(),WorkerApplyReviewStatusEnum.ALTER_EXAM.getValue(), importIdCard, halfYearAgo);
        // 7. 保留每个身份证最新记录
        Map<String, WorkerApplyDO> latestApprovedMap = approvedList.stream()
                .collect(Collectors.toMap(
                        WorkerApplyDO::getIdCardNumber,
                        Function.identity(),
                        (oldVal, newVal) -> oldVal.getCreateTime().isAfter(newVal.getCreateTime()) ? oldVal : newVal
                ));

        if (ObjectUtil.isEmpty(latestApprovedMap)) return Boolean.TRUE;

        // 8. 查询班级必填资料
        List<Long> classBingDocIds = baseMapper.selectClassBingDocIds(classId);
        classBingDocIds.remove(documentIdCardId);
        Set<Long> classBingDocIdSet = new HashSet<>(classBingDocIds);

        // 9. 批量查询所有复用文档（避免 N+1）
        List<Long> approvedIds = latestApprovedMap.values().stream().map(WorkerApplyDO::getId).toList();
        List<WorkerApplyDocumentDO> allReuseDocs = workerApplyDocumentMapper.selectList(
                new LambdaQueryWrapper<WorkerApplyDocumentDO>().in(WorkerApplyDocumentDO::getWorkerApplyId, approvedIds)
        );
        Map<Long, List<WorkerApplyDocumentDO>> reuseDocsMapByApplyId = allReuseDocs.stream()
                .collect(Collectors.groupingBy(WorkerApplyDocumentDO::getWorkerApplyId));

        // 10. 更新导入记录
        for (WorkerApplyDO item : toImport) {
            WorkerApplyDO latest = latestApprovedMap.get(item.getIdCardNumber());
            if (latest != null) {
                System.out.println(latest.getCandidateName());
                // 复用基本信息
                item.setQualificationName(latest.getQualificationName());
                item.setQualificationPath(latest.getQualificationPath());
                item.setIdCardAddress(latest.getIdCardAddress());
                item.setIdCardPhotoFront(latest.getIdCardPhotoFront());
                item.setIdCardPhotoBack(latest.getIdCardPhotoBack());
                item.setFacePhoto(latest.getFacePhoto());
                if (ObjectUtil.isEmpty(classBingDocIds)) {
                    item.setStatus(WorkerApplyReviewStatusEnum.APPROVED.getValue());
                }else {
                    // 判断文档是否齐全
                    List<WorkerApplyDocumentDO> docs = reuseDocsMapByApplyId.get(latest.getId());
                    if (ObjectUtil.isEmpty(docs)) {
                        if (ObjectUtil.isNull(latest.getIdCardPhotoFront())) {
                            item.setStatus(WorkerApplyReviewStatusEnum.WAIT_UPLOAD.getValue());
                        }else {
                            item.setStatus(WorkerApplyReviewStatusEnum.DOC_COMPLETE.getValue());
                        }
                    } else {
                        Set<Long> submittedDocIds = docs.stream().map(WorkerApplyDocumentDO::getTypeId).collect(Collectors.toSet());
                        if (submittedDocIds.containsAll(classBingDocIdSet)) {
                            item.setStatus(WorkerApplyReviewStatusEnum.APPROVED.getValue());
                        } else {
                            item.setStatus(WorkerApplyReviewStatusEnum.DOC_COMPLETE.getValue());
                        }
                        // 复用文档
                        List<WorkerApplyDocumentDO> reuseDocs = docs.stream()
                                .filter(doc -> {
                                    return classBingDocIdSet.contains(doc.getTypeId());
                                }).map(doc -> {
                            WorkerApplyDocumentDO copy = new WorkerApplyDocumentDO();
                            BeanUtil.copyProperties(doc, copy);
                            copy.setId(null);
                            copy.setWorkerApplyId(item.getId());
                            return copy;
                        }).toList();
                        if (!reuseDocs.isEmpty()) {
                            workerApplyDocumentMapper.insertBatch(reuseDocs);
                        }
                    }
                }

            } else {
                // 没有复用信息，直接是待上传状态
                item.setStatus(WorkerApplyReviewStatusEnum.WAIT_UPLOAD.getValue());
            }
        }

        // 11. 批量更新导入记录
        baseMapper.updateBatchById(toImport);

        // 12.找出直接审核通过的数据
        List<String> approvedIdNumbers = toImport.stream()
                .filter(item -> WorkerApplyReviewStatusEnum.APPROVED.getValue().equals(item.getStatus()))
                .map(WorkerApplyDO::getIdCardNumber)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();

        if (CollUtil.isNotEmpty(approvedIdNumbers)) {
            // 2. 查询用户
            LambdaQueryWrapper<UserDO> qw = new LambdaQueryWrapper<>();
            qw.in(UserDO::getUsername, approvedIdNumbers);
            List<UserDO> userDOS = userMapper.selectList(qw);

            if (CollUtil.isEmpty(userDOS)) {
                return Boolean.TRUE;
            }
            // 3. 构建 username -> userId 映射
            Map<String, Long> userMap = userDOS.stream()
                    .collect(Collectors.toMap(
                            UserDO::getUsername,
                            UserDO::getId,
                            (o1, o2) -> o1
                    ));

            // 4. 构建班级学员关系表数据
            List<OrgClassCandidateDO> orgClassCandidateDOS = approvedIdNumbers.stream()
                    .filter(userMap::containsKey)
                    .map(idCard -> {
                        OrgClassCandidateDO entity = new OrgClassCandidateDO();
                        entity.setClassId(classId);
                        entity.setCandidateId(userMap.get(idCard));
                        entity.setStatus(OrgClassCandidateStatusEnum.IN_CLASS.getValue());
                        return entity;
                    })
                    .toList();

            if (CollUtil.isNotEmpty(orgClassCandidateDOS)) {
                orgClassCandidateMapper.insertBatch(orgClassCandidateDOS);
            }
        }


        return Boolean.TRUE;
    }


    /**
     * 机构根据作业人员报考id获取需要上传的资料信息
     *
     * @return
     */
    @Override
    public WorkerApplyVO getWorkerNeedUploadDoc(Long workerIdOrClassId, Boolean isBatch) {
        // 创建返回对象
        WorkerApplyVO workerApplyVO = new WorkerApplyVO();

        Long classId;

        if (Boolean.TRUE.equals(isBatch)) {
            classId = workerIdOrClassId;
        } else {
            WorkerApplyDO workerApplyDO = baseMapper.selectById(workerIdOrClassId);
            ValidationUtils.throwIfNull(workerApplyDO, "未找到作业人员信息");
            classId = workerApplyDO.getClassId();
        }

        // 查询所需上传资料
        workerApplyVO.setProjectNeedUploadDocs(baseMapper.selectProjectNeedUploadDoc(classId));

        return workerApplyVO;
    }

    /**
     * 机构上传某个考生的资料
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean orgSingleUploadDoc(WorkerOrgUploadReq req) {
        // 1. 查询是否存在该报考记录
        WorkerApplyDO workerApply = baseMapper.selectById(req.getWorkerId());
        ValidationUtils.throwIfNull(workerApply, "未查询到报考记录");
        Integer status = workerApply.getStatus();
        ValidationUtils.throwIf(!WorkerApplyReviewStatusEnum.WAIT_UPLOAD.getValue()
                .equals(status) && !WorkerApplyReviewStatusEnum.DOC_UPLOADED.getValue()
                .equals(status) && !WorkerApplyReviewStatusEnum.REJECTED.getValue().equals(status)
                && !WorkerApplyReviewStatusEnum.DOC_COMPLETE.getValue().equals(status), "资料已提交或已审核，无法继续上传");

        // 2. 校验身份证号是否一致（数据库中是加密存储）
        String encryptedReqIdCard = aesWithHMAC.encryptAndSign(req.getIdCardNumber());
        ValidationUtils.throwIf(!encryptedReqIdCard.equals(workerApply.getIdCardNumber()), "所上传的身份证照片与报考记录对应的身份证号不一致");
        ValidationUtils.throwIf(!workerApply.getCandidateName().equals(req.getCandidateName()), "上传的身份证图片姓名与作业人员姓名不匹配");

        // 3. 更新信息
        WorkerApplyDO update = new WorkerApplyDO();
        update.setId(req.getWorkerId());
        update.setQualificationPath(req.getQualificationFileUrl());
        update.setQualificationName(req.getQualificationName());
        update.setIdCardPhotoFront(req.getIdCardPhotoFront());
        update.setIdCardPhotoBack(req.getIdCardPhotoBack());
        update.setFacePhoto(req.getFacePhoto());
        update.setIdCardAddress(req.getIdCardAddress());
        update.setStatus(WorkerApplyReviewStatusEnum.DOC_UPLOADED.getValue());
        // 4. 执行更新
        baseMapper.updateById(update);
        // 先删除资料表
        workerApplyDocumentMapper.delete(new LambdaQueryWrapper<WorkerApplyDocumentDO>()
                .eq(WorkerApplyDocumentDO::getWorkerApplyId, update.getId()));
        // 插入资料表
        if (CollUtil.isNotEmpty(req.getDocFileList())) {
            List<WorkerApplyDocumentDO> docs = req.getDocFileList()
                    .stream()
                    .flatMap(doc -> doc.getUrls().stream().map(url -> {
                        WorkerApplyDocumentDO document = new WorkerApplyDocumentDO();
                        document.setWorkerApplyId(req.getWorkerId());
                        document.setTypeId(doc.getTypeId());
                        document.setDocPath(url);
                        return document;
                    }))
                    .toList();

            workerApplyDocumentMapper.insertBatch(docs);
        }
        return true;
    }

    /**
     * 根据班级id获取未上上传资料的作业人员数
     *
     * @param classId
     * @return
     */
    @Override
    public Long getNotUploadedCount(Integer classId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<WorkerApplyDO>().eq(WorkerApplyDO::getClassId, classId)
                .in(WorkerApplyDO::getStatus, WorkerApplyReviewStatusEnum.WAIT_UPLOAD
                        .getValue(), WorkerApplyReviewStatusEnum.REJECTED.getValue()));
    }

    /**
     * 机构上传某个班级的资料
     *
     * @param classId
     * @param idCardFiles
     * @param applyForms
     * @param projectDocs
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadResulResp orgBatchUploadDoc(Long classId,
                                             List<MultipartFile> idCardFiles,
                                             List<MultipartFile> applyForms,
                                             List<MultipartFile> projectDocs) {

        UploadResulResp result = new UploadResulResp();

        // 1 查询班级所需上传资料配置
        WorkerApplyVO workerNeedUploadDoc = getWorkerNeedUploadDoc(classId, Boolean.TRUE);

        // id -> ProjectNeedUploadDocVO
        Map<Long, ProjectNeedUploadDocVO> docConfigMap = workerNeedUploadDoc.getProjectNeedUploadDocs()
                .stream()
                .collect(Collectors.toMap(ProjectNeedUploadDocVO::getId, Function.identity()));

        // 2 文件按身份证分组
        Map<String, UploadGroupDTO> grouped = groupFilesByIdCard(idCardFiles, applyForms, projectDocs, docConfigMap);

        if (grouped.isEmpty()) {
            return result;
        }

        // 3 查询班级下所有人员
        List<WorkerApplyDO> workerList = baseMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getClassId, classId));

        // 4 明文身份证 -> WorkerApplyDO
        Map<String, WorkerApplyDO> idCardToWorkerMap = new HashMap<>();
        for (WorkerApplyDO w : workerList) {
            try {
                String plainId = aesWithHMAC.verifyAndDecrypt(w.getIdCardNumber());
                idCardToWorkerMap.put(plainId, w);
            } catch (Exception ignore) {
            }
        }

        List<WorkerApplyDO> updateList = new ArrayList<>();
        List<WorkerApplyDocumentDO> insertList = new ArrayList<>();

        // 5 逐个身份证处理
        for (Map.Entry<String, UploadGroupDTO> entry : grouped.entrySet()) {

            String idCard = entry.getKey();
            UploadGroupDTO group = entry.getValue();

            WorkerApplyDO workerApplyDO = idCardToWorkerMap.get(idCard);
            if (workerApplyDO == null) {
                result.getFailedList().add(new FailedUploadResp(idCard, "未查询到班级下存在该身份证信息"));
                continue;
            }
            // 状态校验

            Set<Integer> allowedStatuses = Set.of(
                    WorkerApplyReviewStatusEnum.WAIT_UPLOAD.getValue(),
                    WorkerApplyReviewStatusEnum.REJECTED.getValue(),
                    WorkerApplyReviewStatusEnum.FAKE_MATERIAL.getValue(),
                    WorkerApplyReviewStatusEnum.DOC_UPLOADED.getValue(),
                    WorkerApplyReviewStatusEnum.DOC_COMPLETE.getValue()
            );

            if (!allowedStatuses.contains(workerApplyDO.getStatus())) {
                result.getFailedList().add(new FailedUploadResp(idCard, "该作业人员资料已提交，无法重复上传"));
                continue;
            }
            if (WorkerApplyReviewStatusEnum.FAKE_MATERIAL.getValue().equals(workerApplyDO.getStatus())) {
                result.getFailedList().add(new FailedUploadResp(idCard, "该作业人员资料存在异常，已被判定为虚假材料，无法再次上传"));
                continue;
            }

            try {
                // 6 身份证正反面识别
                IdCardFileInfoResp frontResp = uploadAndCheckIdCard(group.getIdCardFront(), 1, idCard, result, "正面");
                if (frontResp == null)
                    continue;
                if (!workerApplyDO.getCandidateName().equals(frontResp.getRealName())) {
                    result.getFailedList().add(new FailedUploadResp(idCard, "上传的身份证图片姓名与作业人员姓名不匹配"));
                    continue;
                }
                IdCardFileInfoResp backResp = uploadAndCheckIdCard(group.getIdCardBack(), 0, idCard, result, "反面");
                if (backResp == null)
                    continue;

                if (backResp.getValidEndDate() != null && backResp.getValidEndDate().isBefore(LocalDate.now())) {
                    result.getFailedList().add(new FailedUploadResp(idCard, "身份证已过期"));
                    continue;
                }

                // 7 判断是否京籍
                boolean isBeijing = isBeijingIdCard(frontResp);

                // 8 判断【必须上传】的资料
                List<ProjectNeedUploadDocVO> mustUploadDocs = docConfigMap.values()
                        .stream()
                        .filter(doc -> isUploadRequired(doc.getNeedUploadPerson(), isBeijing))
                        .collect(Collectors.toList());

                // 9 校验资料是否齐全
                List<String> missingDocs = checkMissingProjectDocs(group, mustUploadDocs);

                boolean isIncomplete = !missingDocs.isEmpty();

                // 10 一寸照上传
                MultipartFile photoOneInch = group.getPhotoOneInch();
                if (ObjectUtil.isNull(photoOneInch)) {
                    result.getFailedList().add(new FailedUploadResp(idCard, "未上传一寸照"));
                    continue;
                }
                IdCardFileInfoResp faceResp = uploadService.uploadIdCard(photoOneInch, 2);

                // 申请表上传
                MultipartFile applyForm = group.getApplyForm();
                if (ObjectUtil.isNull(applyForm)) {
                    result.getFailedList().add(new FailedUploadResp(idCard, "未上传资格申请表"));
                    continue;
                }
                FileInfoResp applyResp = uploadService.applyUpload(applyForm);

                // 更新主表
                WorkerApplyDO update = new WorkerApplyDO();
                update.setId(workerApplyDO.getId());
                update.setIdCardPhotoFront(frontResp.getUrl());
                update.setIdCardAddress(frontResp.getAddress());
                update.setIdCardPhotoBack(backResp.getUrl());
                update.setFacePhoto(faceResp.getFacePhoto());
                update.setQualificationPath(applyResp.getUrl());
                update.setQualificationName(group.getApplyForm().getOriginalFilename());
                update.setStatus(isIncomplete
                        ? WorkerApplyReviewStatusEnum.DOC_COMPLETE.getValue()
                        : WorkerApplyReviewStatusEnum.DOC_UPLOADED.getValue());
                update.setRemark("");
                updateList.add(update);

                // 项目资料上传
                if (group.getProjectDocs() != null) {
                    for (Map.Entry<Long, UploadGroupDTO.ProjectDocItem> docEntry : group.getProjectDocs().entrySet()) {

                        FileInfoResp fileInfoResp = uploadService.applyUpload(docEntry.getValue().getFile());

                        WorkerApplyDocumentDO insert = new WorkerApplyDocumentDO();
                        insert.setWorkerApplyId(workerApplyDO.getId());
                        insert.setTypeId(docEntry.getKey());
                        insert.setDocPath(fileInfoResp.getUrl());
                        insertList.add(insert);
                    }
                }

                result.getSuccessIdCards().add(idCard);

            } catch (Exception e) {
                e.printStackTrace();
                String msg = Optional.ofNullable(e.getMessage())
                        .map(m -> m.contains(":") ? m.substring(m.indexOf(":") + 1).trim() : m)
                        .orElse("上传失败");
                result.getFailedList().add(new FailedUploadResp(idCard, msg));
            }
        }

        // 1 批量数据库操作
        if (!updateList.isEmpty()) {
            baseMapper.updateBatchById(updateList);
            List<Long> workerApplyIds = updateList.stream().map(WorkerApplyDO::getId).toList();
            workerApplyDocumentMapper.delete(new LambdaQueryWrapper<WorkerApplyDocumentDO>()
                    .in(WorkerApplyDocumentDO::getWorkerApplyId, workerApplyIds));
        }


        if (!insertList.isEmpty()) {
            workerApplyDocumentMapper.insertBatch(insertList);
        }

        return result;
    }

    private boolean isBeijingIdCard(IdCardFileInfoResp resp) {
        return resp.getAddress() != null && resp.getAddress().startsWith(WorkerApplyCheckConstants.BEIJING_RESIDENT);
    }

    private boolean isUploadRequired(Integer needUploadPerson, boolean isBeijing) {
        if (needUploadPerson == null) {
            return true;
        }
        return switch (needUploadPerson) {
            case DocumentConstant.ALL -> true;
            case DocumentConstant.BEIJING_ONLY -> isBeijing;
            case DocumentConstant.NON_BEIJING_ONLY -> !isBeijing;
            default -> false;
        };
    }

    private List<String> checkMissingProjectDocs(UploadGroupDTO group, List<ProjectNeedUploadDocVO> mustUploadDocs) {

        List<String> missing = new ArrayList<>();

        Map<Long, UploadGroupDTO.ProjectDocItem> uploaded = group.getProjectDocs();

        for (ProjectNeedUploadDocVO doc : mustUploadDocs) {
            if (uploaded == null || !uploaded.containsKey(doc.getId())) {
                missing.add(doc.getTypeName());
            }
        }
        return missing;
    }

    /**
     * 机构提交作业人员资料进行审核
     *
     * @param classId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitDoc(Long classId) {
        OrgClassDO orgClassDO = orgClassMapper.selectById(classId);
        ValidationUtils.throwIfNull(orgClassDO, "未查询到班级信息");

        // 必须先结束报名
        ValidationUtils.throwIf(!ClassStatusEnum.STOPPED.getValue()
                .equals(orgClassDO.getStatus()), "当前班级报名尚未结束，请先结束报名后再提交资料");

        // 查询班级所有人员
        List<WorkerApplyDO> workerApplyDOS = baseMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getClassId, classId));
        ValidationUtils.throwIfEmpty(workerApplyDOS, "班级未查询到任何人员信息");

        // 检查是否存在未上传或审核不通过（不能提交）
        //        long invalidCount = workerApplyDOS.stream()
        //            .filter(item -> WorkerApplyReviewStatusEnum.WAIT_UPLOAD.getValue()
        //                .equals(item.getStatus()) || WorkerApplyReviewStatusEnum.REJECTED.getValue().equals(item.getStatus()))
        //            .count();
        //
        //        ValidationUtils.throwIf(invalidCount > 0, "班级中存在未上传资料或审核未通过的作业人员，请全部处理完成后再提交");

        long uploadedCount = workerApplyDOS.stream()
                .filter(item -> WorkerApplyReviewStatusEnum.DOC_UPLOADED.getValue().equals(item.getStatus()))
                .count();

        ValidationUtils.throwIf(uploadedCount == 0, "班级中没有任何人员上传资料，无法提交审核");

        OrgClassDO update = new OrgClassDO();
        update.setId(orgClassDO.getId());
        update.setDocSubmitTime(LocalDateTime.now());
        update.setDocSubmitStatus(OrgClassDocSubmitStatusEnum.SUBMITTED.getCode());
        orgClassMapper.updateById(update);

        // 更新
        baseMapper.update(new LambdaUpdateWrapper<WorkerApplyDO>().eq(WorkerApplyDO::getClassId, classId)
                .eq(WorkerApplyDO::getStatus, WorkerApplyReviewStatusEnum.DOC_UPLOADED.getValue())
                .set(WorkerApplyDO::getStatus, WorkerApplyReviewStatusEnum.PENDING_REVIEW.getValue())
                .set(WorkerApplyDO::getUpdateTime, LocalDateTime.now())
                .set(WorkerApplyDO::getRemark, null));

        return Boolean.TRUE;
    }

    /**
     * 机构获取班级人员列表
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<WorkerApplyResp> orgPage(WorkerApplyQuery query, PageQuery pageQuery) {
        return page(query, pageQuery);
    }

    /**
     * 获取作业人员的上传资料
     *
     * @param workerId
     * @return
     */
    @Override
    public DocDetailResp getDocDetailByWorkerId(Long workerId) {
        DocDetailResp docDetailResp = new DocDetailResp();
        WorkerApplyDO workerApplyDO = baseMapper.selectById(workerId);
        ValidationUtils.throwIfNull(workerApplyDO, "未查询到报考信息");
        List<WorkerApplyDocumentDO> workerApplyDocumentDOS = workerApplyDocumentMapper
                .selectList(new LambdaQueryWrapper<WorkerApplyDocumentDO>()
                        .eq(WorkerApplyDocumentDO::getWorkerApplyId, workerApplyDO.getId()));
        BeanUtil.copyProperties(workerApplyDO, docDetailResp);
        docDetailResp.setIdCardNumber(aesWithHMAC.verifyAndDecrypt(docDetailResp.getIdCardNumber()));
        if (ObjectUtil.isNotEmpty(workerApplyDocumentDOS)) {
            docDetailResp.setUploadedDocs(workerApplyDocumentDOS.stream().map(item -> {
                ProjectNeedUploadDocVO projectNeedUploadDocVO = new ProjectNeedUploadDocVO();
                projectNeedUploadDocVO.setId(item.getTypeId());
                projectNeedUploadDocVO.setDocPath(item.getDocPath());
                return projectNeedUploadDocVO;
            }).toList());
        }
        return docDetailResp;
    }

    /**
     * 撤销申请
     *
     * @param id
     * @return
     */
    @Override
    public Boolean revokeApply(Integer id) {
        baseMapper.update(new LambdaUpdateWrapper<WorkerApplyDO>().eq(WorkerApplyDO::getId, id)
                .set(WorkerApplyDO::getStatus, WorkerApplyReviewStatusEnum.DOC_UPLOADED.getValue()));
        return Boolean.TRUE;
    }

    private Map<String, UploadGroupDTO> groupFilesByIdCard(List<MultipartFile> idCardFiles,
                                                           List<MultipartFile> applyForms,
                                                           List<MultipartFile> projectDocs,
                                                           Map<Long, ProjectNeedUploadDocVO> docConfigMap) {

        Map<String, UploadGroupDTO> grouped = new HashMap<>();

        // 1 身份证 / 一寸照
        for (MultipartFile file : idCardFiles) {
            if (file == null || file.getOriginalFilename() == null) {
                continue;
            }

            String idCard = extractIdCard(file.getOriginalFilename());
            UploadGroupDTO group = grouped.computeIfAbsent(idCard, k -> new UploadGroupDTO());

            String filename = file.getOriginalFilename();
            if (isIdCardFront(filename)) {
                group.setIdCardFront(file);
            } else if (isIdCardBack(filename)) {
                group.setIdCardBack(file);
            } else if (isPhotoOneInch(filename)) {
                group.setPhotoOneInch(file);
            }
        }

        // 2 申请表
        for (MultipartFile file : applyForms) {
            if (file == null || file.getOriginalFilename() == null) {
                continue;
            }
            String idCard = extractIdCard(file.getOriginalFilename());
            grouped.computeIfAbsent(idCard, k -> new UploadGroupDTO()).setApplyForm(file);
        }

        // 3 项目资料
        if (projectDocs != null && !projectDocs.isEmpty()) {
            for (MultipartFile file : projectDocs) {
                if (file == null || file.getOriginalFilename() == null) {
                    continue;
                }

                String idCard = extractIdCard(file.getOriginalFilename());
                UploadGroupDTO group = grouped.computeIfAbsent(idCard, k -> new UploadGroupDTO());

                String filename = file.getOriginalFilename();

                for (ProjectNeedUploadDocVO docConfig : docConfigMap.values()) {
                    // 根据资料类型名称匹配文件
                    if (filename.contains(docConfig.getTypeName())) {
                        group.getProjectDocs()
                                .put(docConfig.getId(), new UploadGroupDTO.ProjectDocItem(file, docConfig.getTypeName()));
                        break; // 一个文件只属于一种资料类型
                    }
                }
            }
        }

        return grouped;
    }

    private List<String> checkMissingFiles(UploadGroupDTO g, Map<Long, String> requiredDocs) {
        List<String> missing = new ArrayList<>();
        if (g.getIdCardFront() == null)
            missing.add("身份证正面");
        if (g.getIdCardBack() == null)
            missing.add("身份证反面");
        if (g.getPhotoOneInch() == null)
            missing.add("一寸免冠照");
        if (g.getApplyForm() == null)
            missing.add("资格申请表");

        for (String docName : requiredDocs.values()) {
            boolean hasDoc = g.getProjectDocs().values().stream().anyMatch(item -> docName.equals(item.getName()));
            if (!hasDoc)
                missing.add(docName);
        }
        return missing;
    }

    private IdCardFileInfoResp uploadAndCheckIdCard(MultipartFile file,
                                                    int type,
                                                    String idCard,
                                                    UploadResulResp result,
                                                    String position) {
        if (ObjectUtil.isNull(file)) {
            result.getFailedList().add(new FailedUploadResp(idCard, "未上传的身份证" + position));
            return null;
        }
        try {
            IdCardFileInfoResp resp = uploadService.uploadIdCard(file, type);
            if (type == 1 && !resp.getIdCardNumber().equals(idCard)) {
                result.getFailedList().add(new FailedUploadResp(idCard, "上传的身份证" + position + "与身份证号不一致"));
                return null;
            }
            return resp;
        } catch (Exception e) {
            result.getFailedList().add(new FailedUploadResp(idCard, "识别身份证" + position + "失败"));
            return null;
        }
    }

    private String extractIdCard(String filename) {
        if (filename == null || filename.length() < 18) {
            return null;
        }
        return filename.substring(0, 18).toUpperCase();
    }

    private boolean isIdCardFront(String name) {
        return name != null && name.contains("正");
    }

    private boolean isIdCardBack(String name) {
        return name != null && name.contains("反");
    }

    private boolean isPhotoOneInch(String name) {
        // 不含“正”或“反”就是一寸照（默认规则）
        return name != null && !name.contains("正") && !name.contains("反");
    }

    private boolean isApplyForm(String filename) {
        return filename.contains("资格申请表");
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
        queryWrapper.eq("twa.is_deleted", 0).orderByAsc("twa.update_time");
        Boolean isOrgQuery = query.getIsOrgQuery();
        if (!isOrgQuery) {
            queryWrapper.eq("twa.status", WorkerApplyReviewStatusEnum.PENDING_REVIEW.getValue());
        }
        IPage<WorkerApplyDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
        List<WorkerApplyDetailResp> records = page.getRecords();

        if (CollUtil.isNotEmpty(records)) {
            // 解密身份证号、手机号
            page.setRecords(records.stream().map(item -> {
                item.setIdCardNumber(aesWithHMAC.verifyAndDecrypt(item.getIdCardNumber()));
                item.setPhone(aesWithHMAC.verifyAndDecrypt(item.getPhone()));
                return item;
            }).toList());

            // 机构报名附带资料映射
//            if (isOrgQuery) {

            List<Long> workerApplyIds = records.stream()
                    .map(WorkerApplyDetailResp::getId)
                    .filter(Objects::nonNull)
                    .toList();

            if (CollUtil.isNotEmpty(workerApplyIds)) {
                List<WorkerApplyDocAndNameDTO> docList = workerApplyDocumentMapper.selectDocAndName(workerApplyIds);

                // 报名ID → (资料名称 → URL)
                Map<Long, Map<String, String>> workerDocMap = docList.stream()
                        .collect(Collectors.groupingBy(WorkerApplyDocAndNameDTO::getWorkerApplyId, // 按 worker_apply_id 分组
                                LinkedHashMap::new, // 保持顺序
                                Collectors
                                        .toMap(WorkerApplyDocAndNameDTO::getTypeName, WorkerApplyDocAndNameDTO::getDocPath, (a,
                                                                                                                             b) -> a + "," + b, LinkedHashMap::new)));

                // 注入到响应对象
                records.forEach(item -> {
                    Map<String, String> docMap = workerDocMap.getOrDefault(item.getId(), Collections.emptyMap());
                    item.setDocMap(docMap);
                });
            }
//            }
        }

        PageResp<WorkerApplyResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 根据身份证后6位判断该学员是否在指定班级或班级集合中存在报名记录。
     * <p>
     * 返回结果：
     * - status = WorkerApplyCheckConstants.NONE：未报过名；
     * - status = WorkerApplyCheckConstants.CURRENT：在当前班级已报名；
     * - status = WorkerApplyCheckConstants.OTHER：在其他班级已报名。
     * - idCardNumber：仅当 CURRENT 时返回完整身份证号。
     */
    private WorkerApplyCheckDTO findIdCardIfExists(Long classId, String idCardLast6, List<Long> classIds) {
        if (CollUtil.isEmpty(classIds)) {
            return new WorkerApplyCheckDTO(WorkerApplyCheckConstants.NONE, null);
        }

        // 查询这些班级的所有报名记录
        List<WorkerApplyDO> applies = baseMapper.selectList(new LambdaQueryWrapper<WorkerApplyDO>()
                .in(WorkerApplyDO::getClassId, classIds)
                .select(WorkerApplyDO::getClassId, WorkerApplyDO::getIdCardNumber, WorkerApplyDO::getApplyType));

        if (CollUtil.isEmpty(applies)) {
            return new WorkerApplyCheckDTO(WorkerApplyCheckConstants.NONE, null);
        }

        for (WorkerApplyDO apply : applies) {
            // 解密身份证号
            String decryptedIdCard = aesWithHMAC.verifyAndDecrypt(apply.getIdCardNumber());
            if (StrUtil.isBlank(decryptedIdCard) || decryptedIdCard.length() < 6) {
                continue;
            }

            // 取出后6位进行比较
            String last6 = StrUtil.subSuf(decryptedIdCard, decryptedIdCard.length() - 6);
            if (StrUtil.equalsIgnoreCase(last6, idCardLast6)) {
                // 匹配到了
                if (apply.getClassId().equals(classId)) {
                    ValidationUtils.throwIf(WorkerApplyTypeEnum.ORG_IMPORT.getValue()
                            // 当前班级已报名
                            .equals(apply.getApplyType()), "您的信息已被机构批量导入，二维码报名功能不可使用");
                    return new WorkerApplyCheckDTO(WorkerApplyCheckConstants.CURRENT, decryptedIdCard);
                } else {
                    // 其他班级已报名
                    return new WorkerApplyCheckDTO(WorkerApplyCheckConstants.OTHER, null);
                }
            }
        }

        // 没有匹配项
        return new WorkerApplyCheckDTO(WorkerApplyCheckConstants.NONE, null);
    }

    /**
     * 重写删除
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        ValidationUtils.throwIfEmpty(ids, "未选择删除的数据");
        ValidationUtils.throwIf(ids.size() != 1, "仅支持单条删除");

        Long id = ids.get(0);

        WorkerApplyDO workerApplyDO = baseMapper.selectById(id);
        if (workerApplyDO == null) {
            return;
        }

        OrgClassDO orgClassDO = orgClassMapper.selectById(workerApplyDO.getClassId());
        ValidationUtils.throwIfNull(orgClassDO, "关联班级不存在，无法删除");
        Long classId = orgClassDO.getId();

        UserDO userDO = userMapper.selectByUsername(workerApplyDO.getIdCardNumber());
        if (userDO != null) {
            Long enrollCount = enrollMapper.selectCount(new LambdaQueryWrapper<EnrollDO>()
                    .eq(EnrollDO::getEnrollStatus, EnrollStatusConstant.COMPLETED)
                    .eq(EnrollDO::getUserId, userDO.getId())
                    .eq(EnrollDO::getClassId, classId));
            ValidationUtils.throwIf(enrollCount > 0, "考生参加了考试计划，无法删除");

            orgClassCandidateMapper.delete(new LambdaQueryWrapper<OrgClassCandidateDO>()
                    .eq(OrgClassCandidateDO::getClassId, classId)
                    .eq(OrgClassCandidateDO::getCandidateId, userDO.getId()));

        }

        // 如果班级还没缴费成功那就需要先更新缴费通知表
        if (!OrgClassPayStatusEnum.FREE.getCode().equals(orgClassDO.getPayStatus()) && !OrgClassPayStatusEnum.PAID
                .getCode()
                .equals(orgClassDO.getPayStatus())) {
            examineePaymentAuditService.generatePaymentAuditByClassId(classId);
        }

        // 先删子表
        workerApplyDocumentMapper.delete(new LambdaQueryWrapper<WorkerApplyDocumentDO>()
                .eq(WorkerApplyDocumentDO::getWorkerApplyId, id));

        // 最后删主表
        baseMapper.deleteById(id);
    }

}