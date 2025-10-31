package top.continew.admin.document.service;

import top.continew.admin.document.model.req.QrcodeUploadReq;
import top.continew.admin.document.model.req.EnrollPreReviewReq;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.document.model.query.EnrollPreUploadQuery;
import top.continew.admin.document.model.req.EnrollPreUploadReq;
import top.continew.admin.document.model.resp.EnrollPreUploadDetailResp;
import top.continew.admin.document.model.resp.EnrollPreUploadResp;

/**
 * 机构报考-考生扫码上传文件业务接口
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
public interface EnrollPreUploadService extends BaseService<EnrollPreUploadResp, EnrollPreUploadDetailResp, EnrollPreUploadQuery, EnrollPreUploadReq> {

    /**
     * 通过二维码上传上传考生资料
     *
     * @return
     */
    Boolean qrcodeUpload(QrcodeUploadReq qrcodeUploadReq);

    /**
     * 机构报考
     * @param reviewReq
     * @return
     */
    Boolean review(EnrollPreReviewReq reviewReq);
}