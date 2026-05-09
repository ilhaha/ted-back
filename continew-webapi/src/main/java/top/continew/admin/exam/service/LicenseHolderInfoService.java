package top.continew.admin.exam.service;

import top.continew.admin.exam.model.entity.ExamIdcardDO;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.LicenseHolderInfoQuery;
import top.continew.admin.exam.model.req.LicenseHolderInfoReq;
import top.continew.admin.exam.model.resp.LicenseHolderInfoDetailResp;
import top.continew.admin.exam.model.resp.LicenseHolderInfoResp;

import java.util.List;

/**
 * 持证信息业务接口
 *
 * @author ilhaha
 * @since 2026/05/08 15:26
 */
public interface LicenseHolderInfoService extends BaseService<LicenseHolderInfoResp, LicenseHolderInfoDetailResp, LicenseHolderInfoQuery, LicenseHolderInfoReq> {

    /**
     * 判断考生是否都上传了持证信息里面对应的证书
     * @param examIdcardDO
     * @param uploadedDocumentTypes
     */
    void checkLicenseCertificateUploaded(ExamIdcardDO examIdcardDO, List<UploadedDocumentTypeVO> uploadedDocumentTypes,Boolean isCertificateExam);

    /**
     * 获取当前用户的持证信息
     *
     * @return
     */
    List<LicenseHolderInfoResp> getInfoByUser();

    /**
     * 保存用户持证信息
     *
     * @param reqs
     * @return
     */
    Boolean saveLicenseHolderInfo(List<LicenseHolderInfoReq> reqs);
}