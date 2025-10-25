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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.document.mapper.DocumentTypeMapper;
// import top.continew.admin.document.model.dto.DocumentTypeDTO;
// import top.continew.admin.document.model.dto.UserDTO;
import top.continew.admin.document.model.dto.DocumentTypeDTO;
import top.continew.admin.document.model.dto.UserDTO;
import top.continew.admin.document.model.entity.DocumentTypeDO;
// import top.continew.admin.document.service.cache.DocumentTypeCache;
import top.continew.admin.document.model.req.DocumentAuditReq;
import top.continew.admin.document.model.req.QrcodeUploadReq;
import top.continew.admin.document.model.resp.*;
import top.continew.admin.document.service.cache.DocumentTypeCache;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.mapper.SpecialCertificationApplicantMapper;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.resp.EnrollStatusResp;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantResp;
import top.continew.admin.file.model.dto.Document;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.document.mapper.DocumentMapper;
import top.continew.admin.document.model.entity.DocumentDO;
import top.continew.admin.document.model.query.DocumentQuery;
import top.continew.admin.document.model.req.DocumentReq;
import top.continew.admin.document.service.DocumentService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 资料核心存储业务实现
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl extends BaseServiceImpl<DocumentMapper, DocumentDO, DocumentResp, DocumentDetailResp, DocumentQuery, DocumentReq> implements DocumentService {
    @Resource
    private DocumentMapper documentMapper;
    @Resource
    private DocumentTypeMapper documentTypeMapper;
    @Resource
    private DocumentTypeCache documentTypeCache;
    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private EnrollMapper enrollMapper;

    @Override
    public PageResp<DocumentResp> page(DocumentQuery query, PageQuery pageQuery) {
        // 1. 分页查询主数据
        QueryWrapper<DocumentDO> queryWrapper = buildQueryWrapper(query);
        super.sort(queryWrapper, pageQuery);
        IPage<DocumentDO> page = baseMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
        PageResp<DocumentResp> result = PageResp.build(page, super.getListClass());

        //2. 缓存获取所有的资料类型
        List<DocumentTypeDTO> documentTypeDOS = documentTypeCache.getDocumentTypeCache();
        //2.1 将documentTypeDOS转换为Map<ID, TypeName>，提升查找效率
        Map<Long, String> typeIdToNameMap = documentTypeDOS.stream()
                .collect(Collectors.toMap(DocumentTypeDTO::getId, DocumentTypeDTO::getTypeName));
        //2.2 获取用户信息
        List<UserDTO> userDTOList = documentMapper.getUserInfoList();

        //3. 填充资料名称字段
        result.getList().forEach(documentResp -> {
            Long typeId = documentResp.getTypeId();
            userDTOList.forEach(userDTO -> {
                if (userDTO.getDocumentId().equals(documentResp.getId())) {
                    documentResp.setUserName(userDTO.getUsername());
                    documentResp.setNickName(userDTO.getNickname());
                    documentResp.setCreateUser(userDTO.getId());
                    documentResp.setCandidateId(userDTO.getId());
                }
            });
            if (typeId != null) {
                String typeName = typeIdToNameMap.get(typeId);
                if (typeName != null) {
                    documentResp.setTypeName(typeName);
                } else {
                    // 处理未匹配的情况（如设置默认值或日志告警）
                    documentResp.setTypeName("未知分类");
                }
            }
        });

        // 5. 填充其他字段
        result.getList().forEach(this::fill);
        return result;
    }

    @Override
    public DocumentDetailResp get(Long id) {
        DocumentDO entity = super.getById(id, false);
        DocumentDetailResp detail = BeanUtil.toBean(entity, this.getDetailClass());
        // 填充其他字段
        List<DocumentTypeDTO> dtos = documentTypeCache.getDocumentTypeCache();
        UserDTO userDTO = documentMapper.getUserInfo(id);
        if (dtos != null) {
            //2.1 将documentTypeDOS转换为Map<ID, TypeName>，提升查找效率
            Map<Long, String> typeIdToNameMap = dtos.stream()
                    .collect(Collectors.toMap(DocumentTypeDTO::getId, DocumentTypeDTO::getTypeName));

            //缓存命中
            detail.setTypeName(typeIdToNameMap.get(detail.getTypeId()));
            detail.setUserName(userDTO.getUsername());
            detail.setNickName(userDTO.getNickname());
            this.fill(detail);
            return detail;
        } else {
            //缓存未命中
            this.fill(detail);
            detail.setTypeName(baseMapper.getTypeName(id));
            return detail;
        }

    }

    @Override
    public void update(DocumentReq req, Long id) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        //判断角色是否为考生
        if (Objects.equals(userTokenDo.getRoleName(), "考生")) {
            throw new RuntimeException("考生不能审核资料");
        }
        this.beforeUpdate(req, id);
        DocumentDO entity = this.getById(id);
        entity.setUpdateUser(userTokenDo.getUserId());
        BeanUtil.copyProperties(req, entity, CopyOptions.create().ignoreNullValue());
        DocumentTypeDO documentTypeDO = documentTypeMapper.selectById(entity.getTypeId());
        ValidationUtils.throwIfNull(documentTypeDO, "资料类型不存在");
        baseMapper.updateById(entity);
        this.afterUpdate(req, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(DocumentReq req) {
        this.beforeAdd(req);
        DocumentDO entity = BeanUtil.copyProperties(req, super.getEntityClass());
        DocumentTypeDO documentTypeDO = documentTypeMapper.selectById(entity.getTypeId());
        ValidationUtils.throwIfNull(documentTypeDO, "资料类型不存在");
        baseMapper.insert(entity);
        this.afterAdd(req, entity);
        return entity.getId();
    }

    @Override
    public void upload(DocumentReq req) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        DocumentDO documentDo = BeanUtil.copyProperties(req, DocumentDO.class);
        documentDo.setCreateUser(userTokenDo.getUserId());
        //根据前端传入的种类id 对应到考生资料表中  涉及到三张表的添加
        documentMapper.uploadDocument(documentDo);
        Long id = documentDo.getId();
        documentMapper.uploadCandidatesDocument(documentDo.getId(), userTokenDo.getUserId());
    }

    @Override
    public List<DocumentTypeAddResp> getDocumentType() {
        return baseMapper.getDocumentType();
    }

    /**
     * 考生端分页获取资料接口
     *
     * @param query
     * @param pageQuery
     * @return PageResp<DocumentResp>
     */
    @Override
    public PageResp<DocumentCandidatesResp> listDocument(DocumentQuery query, PageQuery pageQuery) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        QueryWrapper<DocumentDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("ed.is_deleted", 0)
                .eq("ed.examinee_id", userTokenDo.getUserId())
                .eq("td.create_user",userTokenDo.getUserId())
                .eq("td.is_deleted", 0);
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询
        IPage<DocumentCandidatesResp> page = baseMapper.getDocumentList(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
        System.out.println("page========"+page);

        // 将查询结果转换成 PageResp 对象
        PageResp<DocumentCandidatesResp> pageResp = PageResp.build(page, DocumentCandidatesResp.class);
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    /**
     * 通过二维码上传上传考生资料
     *
     * @return
     */
    @Override
    public Boolean qrcodeUpload(QrcodeUploadReq qrcodeUploadReq) {
        // 首先判断是否是本人扫码
        String aesPlanId = aesWithHMAC.verifyAndDecrypt(qrcodeUploadReq.getPlanId());
        String aesCandidateId = aesWithHMAC.verifyAndDecrypt(qrcodeUploadReq.getCandidateId());

        // 校验二维码有效性
        ValidationUtils.throwIf(
                ObjectUtil.isEmpty(aesPlanId) || ObjectUtil.isEmpty(aesCandidateId),
                "二维码信息已失效，请重新获取"
        );

        Long planId = Long.valueOf(aesPlanId);
        Long candidateId = Long.valueOf(aesCandidateId);

        // 获取用户信息并验证身份证
        UserDTO userInfo = baseMapper.getUserInfo(candidateId);
        String aesUsername = aesWithHMAC.verifyAndDecrypt(userInfo.getUsername());
        String idLastSix = aesUsername.substring(aesUsername.length() - 6);

        // 身份验证
        ValidationUtils.throwIf(
                !qrcodeUploadReq.getIdLastSix().equals(idLastSix),
                "身份证后六位验证不通过，请确认信息后重新输入"
        );
        return null;
    }

    /**
     * 审核资料
     *
     * @param request 审核请求对象
     * @return true=审核成功，false=失败
     */
    @Override
    public boolean auditDocument(DocumentAuditReq request) {
        Integer status = request.getStatus();

        if (status == null) {
            throw new IllegalArgumentException("审核状态不能为空");
        }

        // 仅支持两种操作：审核通过(1) 和 补正(2)
        if (status != 1 && status != 2) {
            throw new IllegalArgumentException("非法的审核状态：" + status);
        }

        // 查询资料是否存在
        DocumentDO document = documentMapper.selectById(request.getId());
        if (document == null) {
            throw new RuntimeException("资料不存在");
        }

        Long documentId = document.getId();
        Long userId = document.getCreateUser();

        // ===== 当前状态为1（已通过）时，禁止再次审核为1（重复通过） =====
        if (document.getStatus() == 1 && status == 1) {
            throw new BusinessException("该资料已通过审核，无需重复审核！");
        }

        // 如果目标状态是补正(2)，且当前状态是已通过(1)，需检查考生是否已报名
        if (document.getStatus() == 1 && status == 2) {
            EnrollDO enrollDO = enrollMapper.getByCandidateId(userId);
            if (enrollDO != null) {
                throw new BusinessException("该考生已报名考试，不能将状态从审核通过改为补正！");
            }
        }

        // ========== 补正备注必填 ==========
        if (status == 2 && !StringUtils.hasText(request.getAuditRemark())) {
            throw new IllegalArgumentException("补正时必须填写审核备注");
        }

        // ========== 更新状态与备注 ==========
        int rows = documentMapper.updateAuditStatus(documentId, status, request.getAuditRemark(),TokenLocalThreadUtil.get().getUserId());
        if (rows == 0) {
            throw new RuntimeException("审核失败，未更新任何记录");
        }
        return true;
    }
}