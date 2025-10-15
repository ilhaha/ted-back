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
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.certificate.model.resp.CandidateCertificateResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.certificate.model.entity.CandidateCertificateDO;

import java.util.List;

/**
 * 考生证件 Mapper
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
public interface CandidateCertificateMapper extends BaseMapper<CandidateCertificateDO> {

    IPage<CandidateCertificateResp> selectPage(@Param("page") Page<Object> objectPage,
                                               @Param(Constants.WRAPPER) QueryWrapper<CandidateCertificateDO> queryWrapper,
                                               Long id);

    IPage<CandidateCertificateResp> selectDeptPage(@Param("page") Page<Object> objectPage,
                                                   @Param(Constants.WRAPPER) QueryWrapper<CandidateCertificateDO> queryWrapper,
                                                   Long deptId);

    IPage<CandidateCertificateResp> selectAllPage(@Param("page") Page<Object> objectPage,
                                                  @Param(Constants.WRAPPER) QueryWrapper<CandidateCertificateDO> queryWrapper);

    @Select("select parent_id from ted.sys_dept where id = #{id}")
    Long selectParentDeptId(@Param("id") Long id);
    //

    /**
     * 查询所有考生证件
     *
     * @return
     */
    List<CandidateCertificateResp> selectAllList(@Param("userId") Long userId);

    /**
     * 获取考生证书列表
     *
     * @param page         分页参数
     * @param queryWrapper 查询条件
     * @return 分页结果
     */
    IPage<CandidateCertificateResp> getCandidateCertificateList(@Param("page") Page<CandidateCertificateDO> page,
                                                                @Param(Constants.WRAPPER) QueryWrapper<CandidateCertificateDO> queryWrapper);

    /**
     * 获取当前考生证书
     * 
     * @return
     */
    List<CandidateCertificateResp> getUserCertificate(@Param("userId") Long userId);

}