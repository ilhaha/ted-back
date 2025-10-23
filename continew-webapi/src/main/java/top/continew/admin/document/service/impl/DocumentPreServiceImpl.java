package top.continew.admin.document.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.document.mapper.DocumentPreMapper;
import top.continew.admin.document.model.entity.DocumentPreDO;
import top.continew.admin.document.model.query.DocumentPreQuery;
import top.continew.admin.document.model.req.DocumentPreReq;
import top.continew.admin.document.model.resp.DocumentPreDetailResp;
import top.continew.admin.document.model.resp.DocumentPreResp;
import top.continew.admin.document.service.DocumentPreService;

/**
 * 机构报考-考生上传资料业务实现
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Service
@RequiredArgsConstructor
public class DocumentPreServiceImpl extends BaseServiceImpl<DocumentPreMapper, DocumentPreDO, DocumentPreResp, DocumentPreDetailResp, DocumentPreQuery, DocumentPreReq> implements DocumentPreService {}