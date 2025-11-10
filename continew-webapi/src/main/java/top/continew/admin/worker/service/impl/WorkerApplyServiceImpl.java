package top.continew.admin.worker.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import top.continew.admin.common.constant.enums.WorkerApplyReviewStatusEnum;
import top.continew.admin.common.constant.enums.WorkerApplyTypeEnum;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.common.enums.GenderEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.document.mapper.DocumentTypeMapper;
import top.continew.admin.document.model.dto.DocFileDTO;
import top.continew.admin.document.model.dto.DocumentTypeDTO;
import top.continew.admin.document.model.entity.DocumentTypeDO;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.training.mapper.CandidateTypeMapper;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.model.entity.CandidateTypeDO;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.worker.mapper.WorkerApplyDocumentMapper;
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

    /**
     * 根据身份证后六位、和班级id查询当前身份证报名信息
     *
     * @param verifyReq
     * @return
     */
    @Override
    public WorkerApplyVO verify(VerifyReq verifyReq) {
        WorkerApplyVO workerApplyVO = new WorkerApplyVO();
        String aseClassId = aesWithHMAC.verifyAndDecrypt(verifyReq.getClassId());
        ValidationUtils.throwIfNull(aseClassId, "二维码已被篡改");
        Long classId = Long.valueOf(aseClassId);
        // 初始化项目报名所需资料
        workerApplyVO.setProjectNeedUploadDocs(baseMapper.selectProjectNeedUploadDoc(classId));

        // 如果有报名信息，可以在这里处理额外逻辑
        String idCard = findIdCardIfExists(classId, verifyReq.getIdLast6());
        if (ObjectUtil.isNotEmpty(idCard)) {
            WorkerUploadedDocsVO workerUploadedDocsVO =
                    baseMapper.selectWorkerUploadedDocs(classId, aesWithHMAC.encryptAndSign(idCard));
            if (ObjectUtil.isNotNull(workerUploadedDocsVO)
                    && ObjectUtil.isNotEmpty(workerUploadedDocsVO.getDocuments())) {

                // 使用 Hutool 解析 JSON 数组
                JSONArray array = JSONUtil.parseArray(workerUploadedDocsVO.getDocuments());
                List<WorkerApplyDocumentVO> docs = new ArrayList<>();

                for (Object obj : array) {
                    JSONObject jsonObj = (JSONObject) obj;
                    WorkerApplyDocumentVO vo = new WorkerApplyDocumentVO();
                    vo.setTypeId(jsonObj.getLong("typeId"));
                    vo.setTypeName(jsonObj.getStr("typeName"));
                    vo.setDocPaths(jsonObj.getStr("docPaths"));
                    docs.add(vo);
                }
                workerUploadedDocsVO.setWorkerApplyDocuments(docs);
            }

            workerApplyVO.setWorkerUploadedDocs(workerUploadedDocsVO);
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
        String aseClassId = aesWithHMAC.verifyAndDecrypt(workerQrcodeUploadReq.getClassId());
        ValidationUtils.throwIfNull(aseClassId, "二维码已被篡改");
        Long classId = Long.valueOf(aseClassId);
        String idCardNumber = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(workerQrcodeUploadReq.getIdCardNumber()));
        ValidationUtils.throwIfBlank(idCardNumber, "身份证未上传");
        String phone = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(workerQrcodeUploadReq.getPhone()));
        ValidationUtils.throwIfBlank(phone, "身份信息未通过验证");
        // 先查是否重复上传
        String idCardNumberLast6 = idCardNumber.substring(idCardNumber.length() - 6);
        ValidationUtils.throwIfNotNull(findIdCardIfExists(classId, idCardNumberLast6), "您已提交过报名，请勿重复提交！");
        // 插入作业人员报名表
        WorkerApplyDO workerApplyDO = new WorkerApplyDO();
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
        workerApplyDO.setApplyType(WorkerApplyTypeEnum.SCAN_APPLY.getValue());
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
                (WorkerApplyReviewStatusEnum.REJECTED.getValue().equals(status)
                        || WorkerApplyReviewStatusEnum.FAKE_MATERIAL.getValue().equals(status))
                        && ObjectUtil.isEmpty(req.getRemark()),
                "请填写审核原因"
        );

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

            // 8. 批量插入用户与类型
            orgClassCandidateMapper.insertBatch(classCandidates);
            candidateTypeMapper.insertBatch(candidateTypes);
        }
        // 9. 批量更新审核状态
        String remark = req.getRemark();
        baseMapper.update(new LambdaUpdateWrapper<WorkerApplyDO>()
                .set(!ObjectUtils.isEmpty(remark), WorkerApplyDO::getRemark, remark)
                .set(WorkerApplyDO::getStatus, req.getStatus())
                .in(WorkerApplyDO::getId, req.getReviewIds()));
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
        ValidationUtils.throwIfNull(aseClassId, "二维码已被篡改");
        Long classId = Long.valueOf(aseClassId);
        String idCardNumber = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(workerQrcodeUploadReq.getIdCardNumber()));
        ValidationUtils.throwIfBlank(idCardNumber, "身份证未上传");
        String phone = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(workerQrcodeUploadReq.getPhone()));
        ValidationUtils.throwIfBlank(phone, "身份信息未通过验证");
        // 修改作业人员报名表
        // 先查出原来的资料
        LambdaQueryWrapper<WorkerApplyDO> workerApplyDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        workerApplyDOLambdaQueryWrapper.eq(WorkerApplyDO::getIdCardNumber, aesWithHMAC.encryptAndSign(idCardNumber))
                .eq(WorkerApplyDO::getClassId, classId);
        WorkerApplyDO workerApplyDO = baseMapper.selectOne(workerApplyDOLambdaQueryWrapper);
        ValidationUtils.throwIfNull(workerApplyDO, "二维码已被篡改");
        // 先删除掉原来的所有资料
        workerApplyDocumentMapper.delete(new LambdaQueryWrapper<WorkerApplyDocumentDO>().eq(WorkerApplyDocumentDO::getWorkerApplyId, workerApplyDO.getId()));
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
        // 1. 校验是否选择数据
        ValidationUtils.throwIfEmpty(workerOrgImportReqs, "未选择导入的数据");

        Long classId = workerOrgImportReqs.get(0).getClassId();

        // 2. 获取所有提交的身份证
        List<String> idCards = workerOrgImportReqs.stream()
                .map(WorkerOrgImportReq::getEncFieldB)
                .collect(Collectors.toList());

        // 3. 查询数据库中已存在的身份证
        List<WorkerApplyDO> existingWorkers = baseMapper.selectList(
                new LambdaQueryWrapper<WorkerApplyDO>()
                        .eq(WorkerApplyDO::getClassId, classId)
                        .in(WorkerApplyDO::getIdCardNumber, idCards)
        );

        // 4. 如果存在重复，直接抛出异常
        ValidationUtils.throwIf(!existingWorkers.isEmpty(),
                "以下作业人员已存在：" + existingWorkers.stream()
                        .map(WorkerApplyDO::getCandidateName)
                        .collect(Collectors.joining(", "))
        );

        // 5. 构造待导入数据
        List<WorkerApplyDO> toImport = workerOrgImportReqs.stream().map(item -> {
            WorkerApplyDO workerApplyDO = new WorkerApplyDO();
            BeanUtil.copyProperties(item, workerApplyDO);
            workerApplyDO.setPhone(item.getEncFieldA());
            workerApplyDO.setIdCardNumber(item.getEncFieldB());
            return workerApplyDO;
        }).toList();

        // 6. 批量插入
        if (!toImport.isEmpty()) {
            baseMapper.insertBatch(toImport);
        }

        // 7. 插入文档资料
        List<WorkerApplyDocumentDO> workerApplyDocumentDOS = new ArrayList<>();
        for (WorkerOrgImportReq req : workerOrgImportReqs) {
            Map<String, String> docMap = req.getDocMap();
            if (ObjectUtil.isEmpty(docMap)) continue;

            List<String> docNames = new ArrayList<>(docMap.keySet());

            // 查询数据库中对应的文档类型
            List<DocumentTypeDO> documentTypeDOS = documentTypeMapper.selectList(
                    new LambdaQueryWrapper<DocumentTypeDO>()
                            .in(DocumentTypeDO::getTypeName, docNames)
                            .select(DocumentTypeDO::getId, DocumentTypeDO::getTypeName)
            );

            // 构造 name -> id 映射，保持顺序
            Map<String, Long> docNameIdMap = documentTypeDOS.stream()
                    .sorted(Comparator.comparingInt(d -> docNames.indexOf(d.getTypeName())))
                    .collect(Collectors.toMap(DocumentTypeDO::getTypeName, DocumentTypeDO::getId, (a, b) -> a, LinkedHashMap::new));

            // 找到对应 WorkerApplyDO
            WorkerApplyDO workerApplyDO = toImport.stream()
                    .filter(w -> w.getIdCardNumber().equals(req.getEncFieldB()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("未找到对应的导入记录：" + req.getEncFieldB()));

            // 构造 WorkerApplyDocumentDO
            docMap.forEach((docName, path) -> {
                WorkerApplyDocumentDO docDO = new WorkerApplyDocumentDO();
                docDO.setWorkerApplyId(workerApplyDO.getId());
                docDO.setTypeId(docNameIdMap.get(docName));
                docDO.setDocPath(path);
                workerApplyDocumentDOS.add(docDO);
            });
        }

        // 批量插入文档
        if (!workerApplyDocumentDOS.isEmpty()) {
            workerApplyDocumentMapper.insertBatch(workerApplyDocumentDOS);
        }
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

        IPage<WorkerApplyDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
        List<WorkerApplyDetailResp> records = page.getRecords();

        if (CollUtil.isNotEmpty(records)) {
            // 解密身份证号、手机号
            page.setRecords(records.stream().map(item -> {
                String idCardNumberDB = item.getIdCardNumber();
                String idCardNumber = aesWithHMAC.verifyAndDecrypt(idCardNumberDB);
                item.setIdCardNumber(CharSequenceUtil.replaceByCodePoint(idCardNumber, 2, idCardNumber.length() - 5, '*'));
                item.setPhone(aesWithHMAC.verifyAndDecrypt(item.getPhone()));
                return item;
            }).toList());

            // 机构报名附带资料映射
            if (query.getIsOrgQuery()) {
                List<Long> workerApplyIds = records.stream()
                        .map(WorkerApplyDetailResp::getId)
                        .filter(Objects::nonNull)
                        .toList();

                if (CollUtil.isNotEmpty(workerApplyIds)) {
                    List<WorkerApplyDocAndNameDTO> docList = workerApplyDocumentMapper.selectDocAndName(workerApplyIds);

                    // 报名ID → (资料名称 → URL)
                    Map<Long, Map<String, String>> workerDocMap = docList.stream()
                            .collect(Collectors.groupingBy(
                                    WorkerApplyDocAndNameDTO::getWorkerApplyId, // 按 worker_apply_id 分组
                                    LinkedHashMap::new, // 保持顺序
                                    Collectors.toMap(
                                            WorkerApplyDocAndNameDTO::getTypeName,
                                            WorkerApplyDocAndNameDTO::getDocPath,
                                            (a, b) -> a + "," + b,
                                            LinkedHashMap::new
                                    )
                            ));

                    // 注入到响应对象
                    records.forEach(item -> {
                        Map<String, String> docMap = workerDocMap.getOrDefault(item.getId(), Collections.emptyMap());
                        item.setDocMap(docMap);
                    });
                }
            }
        }

        PageResp<WorkerApplyResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }


    /**
     * 判断是否已报名，并返回匹配到的身份证号
     *
     * @param classId           班级ID
     * @param uploadIdCardLast6 上传的身份证后六位
     * @return 匹配到的身份证号，如果没匹配到则返回 null
     */
    private String findIdCardIfExists(Long classId, String uploadIdCardLast6) {
        // 查出该班级所有已有考生
        LambdaQueryWrapper<WorkerApplyDO> workerApplyDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        workerApplyDOLambdaQueryWrapper.eq(WorkerApplyDO::getClassId, classId);
        List<WorkerApplyDO> workerApplyDOS = baseMapper.selectList(workerApplyDOLambdaQueryWrapper);

        if (CollUtil.isEmpty(workerApplyDOS)) {
            return null;
        }

        // 提取该班级的所有考试身份证号
        List<String> candidateIdCards = workerApplyDOS.stream()
                .map(WorkerApplyDO::getIdCardNumber)
                .toList();
        // 比对身份后六位看是否已存在
        for (String candidateIdCard : candidateIdCards) {
            String decryptedIdCard = aesWithHMAC.verifyAndDecrypt(candidateIdCard);
            String last6 = decryptedIdCard.substring(decryptedIdCard.length() - 6);
            if (uploadIdCardLast6.equalsIgnoreCase(last6)) {
                return decryptedIdCard;
            }
        }
        return null;
    }



    /**
     * 重写删除
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 1. 查出待删除数据
        List<WorkerApplyDO> workerApplyDOS = baseMapper.selectByIds(ids);
        if (CollUtil.isEmpty(workerApplyDOS)) {
            return;
        }

        // 2. 删除关联资料
        workerApplyDocumentMapper.delete(
                new LambdaQueryWrapper<WorkerApplyDocumentDO>()
                        .in(WorkerApplyDocumentDO::getWorkerApplyId, ids)
        );

        // 3. 构建班级 -> 身份证号映射
        Map<Long, List<String>> classIdAndIdCardMap = workerApplyDOS.stream()
                .collect(Collectors.groupingBy(
                        WorkerApplyDO::getClassId,
                        Collectors.mapping(WorkerApplyDO::getIdCardNumber, Collectors.toList())
                ));

        // 4. 删除班级与考生关联表（逐班精确删除）
        for (Map.Entry<Long, List<String>> entry : classIdAndIdCardMap.entrySet()) {
            Long classId = entry.getKey();
            List<String> idCards = entry.getValue();
            List<Long> candidateIds = userMapper.selectList(
                            new LambdaQueryWrapper<UserDO>().in(UserDO::getUsername, idCards))
                    .stream()
                    .map(UserDO::getId)
                    .toList();
            if (CollUtil.isNotEmpty(candidateIds)) {
                orgClassCandidateMapper.delete(
                        new LambdaQueryWrapper<OrgClassCandidateDO>()
                                .eq(OrgClassCandidateDO::getClassId, classId)
                                .in(OrgClassCandidateDO::getCandidateId, candidateIds)
                );
                // 删除对应的报名记录
                enrollMapper.delete( new LambdaQueryWrapper<EnrollDO>()
                        .eq(EnrollDO::getClassId, classId)
                        .in(EnrollDO::getUserId, candidateIds)
                );
            }
        }

        // 5. 删除主表
        super.delete(ids);
    }
}