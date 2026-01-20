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

package top.continew.admin.worker.model.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/31 11:26
 */
@Data
public class WorkerApplyVO implements Serializable {

    /**
     * 项目所需提交的报名资料
     */
    private List<ProjectNeedUploadDocVO> projectNeedUploadDocs;

    /**
     * 作业人员已上传的资料
     */
    private WorkerUploadedDocsVO workerUploadedDocs;

    /**
     * 项目信息
     */
    private ProjectInfoVO projectInfo;

    /**
     * 焊接项目代码
     */
    private List<String> weldingProjectCodes;
}
