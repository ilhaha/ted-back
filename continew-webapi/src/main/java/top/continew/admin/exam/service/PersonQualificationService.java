package top.continew.admin.exam.service;

import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.exam.model.req.PersonQualificationAuditReq;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.PersonQualificationQuery;
import top.continew.admin.exam.model.req.PersonQualificationReq;
import top.continew.admin.exam.model.resp.PersonQualificationDetailResp;
import top.continew.admin.exam.model.resp.PersonQualificationResp;

/**
 * 人员复审信息表业务接口
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
public interface PersonQualificationService extends BaseService<PersonQualificationResp, PersonQualificationDetailResp, PersonQualificationQuery, PersonQualificationReq> {


    /**
     * 批量导入复审人员信息
     *
     * @param file
     */
    void importExcel(MultipartFile file);


    /**
     * 审核人员复审信息
     */
    void audit(PersonQualificationAuditReq req);
}