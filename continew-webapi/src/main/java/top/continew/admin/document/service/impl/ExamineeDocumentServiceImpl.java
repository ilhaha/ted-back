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

package top.continew.admin.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.DocumentConstant;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.document.mapper.DocumentMapper;
import top.continew.admin.document.mapper.ExamineeDocumentMapper;
import top.continew.admin.document.model.entity.DocumentDO;
import top.continew.admin.document.model.entity.ExamineeDocumentDO;
import top.continew.admin.document.model.query.ExamineeDocumentQuery;
import top.continew.admin.document.model.req.ExamineeDocumentReq;
import top.continew.admin.document.model.req.StudentUploadDocumentsReq;
import top.continew.admin.document.model.resp.ExamineeDocumentDetailResp;
import top.continew.admin.document.model.resp.ExamineeDocumentResp;
import top.continew.admin.document.service.ExamineeDocumentService;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 考生资料关系业务实现
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Service
@RequiredArgsConstructor
public class ExamineeDocumentServiceImpl extends BaseServiceImpl<ExamineeDocumentMapper, ExamineeDocumentDO, ExamineeDocumentResp, ExamineeDocumentDetailResp, ExamineeDocumentQuery, ExamineeDocumentReq> implements ExamineeDocumentService {

    @Resource
    private DocumentMapper documentMapper;

    @Resource
    private ExamineeDocumentMapper examineeDocumentMapper;

    @Resource
    private EnrollMapper enrollMapper;

    /**
     * 考生上传资料
     * 当未通过重新上传时，就把未通过的隐藏起来
     *
     * @param studentUploadDocumentsReq
     * @return
     */
    @Transactional
    @Override
    public R studentUploadDocuments(StudentUploadDocumentsReq studentUploadDocumentsReq) {
        LambdaQueryWrapper<DocumentDO> documentDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long certificateId = 0L;
        if (studentUploadDocumentsReq.getCandidateId() != null) {
            certificateId = studentUploadDocumentsReq.getCandidateId();
            documentDOLambdaQueryWrapper.eq(DocumentDO::getCreateUser, studentUploadDocumentsReq.getCandidateId());
        } else {
            certificateId = TokenLocalThreadUtil.get().getUserId();
            documentDOLambdaQueryWrapper.eq(DocumentDO::getCreateUser, TokenLocalThreadUtil.get().getUserId());
        }
        documentDOLambdaQueryWrapper.eq(DocumentDO::getTypeId, studentUploadDocumentsReq.getTypeId())
            .ne(DocumentDO::getStatus, DocumentConstant.REJECTED);
        List<DocumentDO> documentDOS = documentMapper.selectList(documentDOLambdaQueryWrapper);
        ValidationUtils.throwIfNotEmpty(documentDOS, "该类型资料已存在，请勿重复上传");

        // 删除该类型资料(不显示出来)
        LambdaQueryWrapper<DocumentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentDO::getTypeId, studentUploadDocumentsReq.getTypeId())
            .eq(DocumentDO::getStatus, DocumentConstant.REJECTED)
            .eq(DocumentDO::getCreateUser, certificateId);
        documentMapper.delete(wrapper);

        Long documentId = documentMapper.selectId(certificateId, studentUploadDocumentsReq.getTypeId());
        //关联表也要删除
        LambdaQueryWrapper<ExamineeDocumentDO> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(ExamineeDocumentDO::getDocumentId, documentId).eq(ExamineeDocumentDO::getExamineeId, certificateId);

        examineeDocumentMapper.delete(wrapper1);

        // 把资料路基存储到资料表中
        DocumentDO documentDO = new DocumentDO();
        documentDO.setDocPath(studentUploadDocumentsReq.getDocPath());
        documentDO.setTypeId(studentUploadDocumentsReq.getTypeId());
        documentDO.setCreateUser(certificateId);
        documentDO.setUpdateUser(certificateId);
        documentDO.setStatus(DocumentConstant.PENDING_REVIEW);
        documentMapper.insert(documentDO);
        // 关联考生和资料表
        ExamineeDocumentDO examineeDocumentDO = new ExamineeDocumentDO();
        examineeDocumentDO.setDocumentId(documentDO.getId());
        if (studentUploadDocumentsReq.getCandidateId() != null) {
            examineeDocumentDO.setExamineeId(studentUploadDocumentsReq.getCandidateId());
        } else {
            examineeDocumentDO.setExamineeId(TokenLocalThreadUtil.get().getUserId());
        }

        examineeDocumentMapper.insert(examineeDocumentDO);
        return R.ok("成功");
    }

    /**
     * 学生重新上传资料
     */
    @Override
    public R studentReUploadDocument(StudentUploadDocumentsReq studentUploadDocumentsReq) {

        // 参数校验
        if (studentUploadDocumentsReq.getId() == null || studentUploadDocumentsReq.getDocPath() == null) {
            throw new BusinessException("资料ID或资料路径不能为空");
        }
        Long userId = TokenLocalThreadUtil.get().getUserId();
        // 查询旧资料
        DocumentDO oldDoc = documentMapper.selectById(studentUploadDocumentsReq.getId());
        if (oldDoc == null) {
            throw new RuntimeException("未找到对应资料记录");
        }
        // 权限校验
        if (!oldDoc.getCreateUser().equals(userId)) {
            throw new BusinessException("无权限重新上传该资料");
        }
        // 根据前端传入状态进行逻辑处理
        Integer status = studentUploadDocumentsReq.getStatus();
        if (status != null) {
            if (status == 1) {
                // 报名检查
                EnrollDO enrollDO = enrollMapper.getByCandidateId(userId);
                if (enrollDO != null) {
                    throw new BusinessException("您已报名考试，不能重新上传资料！请等到考试结束再操作");
                }
                status = 0;
            } else if (status == 2) {
                // 将状态改为3
                status = 3;
            }
        } else {
            oldDoc.setStatus(DocumentConstant.PENDING_REVIEW);
        }
        // 更新资料路径与状态
        oldDoc.setStatus(status);
        oldDoc.setDocPath(studentUploadDocumentsReq.getDocPath());
        oldDoc.setUpdateUser(userId);
        int updated = documentMapper.updateById(oldDoc);
        if (updated <= 0) {
            throw new RuntimeException("资料更新失败");
        }
        return R.ok("重新上传成功");
    }
}