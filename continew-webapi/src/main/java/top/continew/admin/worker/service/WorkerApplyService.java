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

package top.continew.admin.worker.service;

import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.worker.model.req.*;
import top.continew.admin.worker.model.resp.UploadResulResp;
import top.continew.admin.worker.model.resp.WorkerApplyVO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.worker.model.query.WorkerApplyQuery;
import top.continew.admin.worker.model.resp.WorkerApplyDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyResp;

import java.util.List;

/**
 * 作业人员报名业务接口
 *
 * @author ilhaha
 * @since 2025/10/31 10:20
 */
public interface WorkerApplyService extends BaseService<WorkerApplyResp, WorkerApplyDetailResp, WorkerApplyQuery, WorkerApplyReq> {

    /**
     * 根据身份证后六位、和班级id查询当前身份证报名信息
     * 
     * @param verifyReq
     * @return
     */
    WorkerApplyVO verify(VerifyReq verifyReq);

    /**
     * 作业人员通过二维码上传资料
     * 
     * @param workerQrcodeUploadReq
     * @return
     */
    Boolean submit(WorkerQrcodeUploadReq workerQrcodeUploadReq);

    /**
     * 审核作业人员报考
     * 
     * @return
     */
    Boolean review(WorkerApplyReviewReq workerApplyReviewReq);

    /**
     * 作业人员通过二维码重新上传资料
     * 
     * @param workerQrcodeUploadReq
     * @return
     */
    Boolean restSubmit(WorkerQrcodeUploadReq workerQrcodeUploadReq);

    /**
     * 机构批量导入
     * 
     * @param workerOrgImportReqs
     * @return
     */
    Boolean orgImport(List<WorkerOrgImportReq> workerOrgImportReqs);

    /**
     * 机构根据作业人员报考id获取需要上传的资料信息
     *
     * @return
     */
    WorkerApplyVO getWorkerNeedUploadDoc(Long workerIdOrClassId, Boolean isBatch);

    /**
     * 机构上传某个考生的资料
     * 
     * @param workerOrgUploadReq
     * @return
     */
    Boolean orgSingleUploadDoc(WorkerOrgUploadReq workerOrgUploadReq);

    /**
     * 根据班级id获取未上上传资料的作业人员数
     * 
     * @param classId
     * @return
     */
    Long getNotUploadedCount(Integer classId);

    /**
     * 机构上传某个班级的资料
     * 
     * @param classId
     * @param idCardFiles
     * @param applyForms
     * @param projectDocs
     * @return
     */
    UploadResulResp orgBatchUploadDoc(Long classId,
                                      List<MultipartFile> idCardFiles,
                                      List<MultipartFile> applyForms,
                                      List<MultipartFile> projectDocs);

    /**
     * 机构提交作业人员资料进行审核
     * 
     * @param classId
     * @return
     */
    Boolean submitDoc(Long classId);

}