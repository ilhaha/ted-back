package top.continew.admin.document.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.document.model.query.DocumentPreQuery;
import top.continew.admin.document.model.req.DocumentPreReq;
import top.continew.admin.document.model.resp.DocumentPreDetailResp;
import top.continew.admin.document.model.resp.DocumentPreResp;

/**
 * 机构报考-考生上传资料业务接口
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
public interface DocumentPreService extends BaseService<DocumentPreResp, DocumentPreDetailResp, DocumentPreQuery, DocumentPreReq> {}