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

package top.continew.admin.certificate.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.continew.admin.certificate.model.entity.CertificateProjectDO;
import top.continew.admin.certificate.model.resp.CertificateTypeDetailResp;
import top.continew.admin.certificate.model.resp.CertificateTypeResp;
import top.continew.admin.common.enums.ProjectEnum;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.certificate.model.entity.CertificateTypeDO;

/**
 * 证件种类 Mapper
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
public interface CertificateTypeMapper extends BaseMapper<CertificateTypeDO> {
    public /**
            * 根据资料种类id获取项目名称
            */
    //    List<CertificateTypeDTO> getProjectName();

    IPage<CertificateTypeResp> selectPageWithProjectName(@Param("page") Page<Object> objectPage,
                                                         @Param(Constants.WRAPPER) QueryWrapper<CertificateTypeDO> queryWrapper);

    @Insert("insert into ted_certificate_project (certificate_type_id,project_id,create_user,update_user) values (#{certificateTypeId},#{projectId},#{createUser},#{updateUser})")
    void insertCertificateProject(CertificateProjectDO CertificateProjectDO);

    @Update("update ted_certificate_project set project_id=#{projectId} where certificate_type_id=#{certificateTypeId}")
    void updateCertificateProject(@Param("projectId") Long projectId,
                                  @Param("certificateTypeId") Long certificateTypeId);

    CertificateTypeDO selectType(@Param(Constants.WRAPPER) QueryWrapper<CertificateTypeDO> queryWrapper);

    CertificateTypeDetailResp getCertificateTypeDetail(@Param("id") Long id,
                                                       @Param("maintenance") ProjectEnum maintenance);
}