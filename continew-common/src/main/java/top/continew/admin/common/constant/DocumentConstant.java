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

package top.continew.admin.common.constant;

/**
 * @Author ilhaha
 * @Create 2025/3/13 16:56
 * @Version 1.0
 */
public class DocumentConstant {

    /**
     * 待审批
     */
    public final static Integer PENDING_REVIEW = 0;

    /**
     * 已生效
     */
    public final static Integer APPROVED = 1;

    /**
     * 已拒绝
     */
    public final static Integer REJECTED = 2;

    /** 全部人员都需要上传 */
    public static final int ALL = 0;

    /** 仅京籍人员需要上传 */
    public static final int BEIJING_ONLY = 1;

    /** 仅非京籍人员需要上传 */
    public static final int NON_BEIJING_ONLY = 2;

}
