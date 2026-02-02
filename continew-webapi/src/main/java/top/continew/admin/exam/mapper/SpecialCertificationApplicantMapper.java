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

package top.continew.admin.exam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantResp;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.SpecialCertificationApplicantDO;

import java.util.List;

/**
 * 特种设备人员资格申请 Mapper
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
public interface SpecialCertificationApplicantMapper extends BaseMapper<SpecialCertificationApplicantDO> {
    IPage<SpecialCertificationApplicantResp> getSpecialCertification(@Param("page") Page<SpecialCertificationApplicantResp> page,
                                                                     @Param(Constants.WRAPPER) QueryWrapper<SpecialCertificationApplicantDO> queryWrapper);

    Integer insertStudentImage(@Param("scaList") List<SpecialCertificationApplicantDO> scaList,
                               @Param("createdUserId") Long createdUserId);

    List<UserDO> selectLog(@Param("scaList") List<SpecialCertificationApplicantDO> scaList,
                           @Param("planId") String planId,
                           @Param("status") Integer status);
}