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

package top.continew.admin.expert.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.expert.model.ExpertFreeResp;
import top.continew.admin.expert.model.ExpertFreeShouldResp;
import top.continew.admin.expert.model.ExpertFreelistResp;
import top.continew.admin.expert.model.entity.TedExpertFree;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Anton
 * @date 2025/4/27-11:20
 */

@Mapper
public interface ExpertFreeMapper extends BaseMapper<TedExpertFree> {

    /**
     * 查询专家费用By机构Id
     *
     * @param organizationId
     * @param pageSize
     * @param offset
     * @return
     */

    @Select("SELECT toe.id as id, toe.expert_id as expertId, te.name as expertName, " + "te.title as expertTitle, te.avatar as imageUrl  " + "FROM ted_org_expert toe " + "LEFT JOIN ted_expert te ON toe.expert_id = te.id " + "WHERE toe.org_id = #{organizationId} and toe.is_deleted=0 " + "GROUP BY toe.id, toe.expert_id, te.name, te.title, te.avatar " + "ORDER BY expertId " + "LIMIT #{pageSize} OFFSET #{offset}")
    List<ExpertFreeResp> queryExpertFreeByProjectIdAndExpertId(Long organizationId, int pageSize, int offset);

    /**
     * 查询专家费用By机构总条数
     * 
     * @param organizationId
     * @return
     */
    //    @Select("select count(*) from " +
    //            "ted_expert_free tef " +
    //            "inner join ted_expert te on tef.expert_id = te.id " +
    //            "inner join ted_training tt on tef.project_id = tt.id " +
    //            "where tef.organization_id = #{organizationId}"
    //            )
    @Select("select count(*) from ted_org_expert toe where toe.org_id=#{organizationId} and toe.is_deleted=0 ")
    Long queryExpertFreeByProjectIdAndExpertIdTotal(Long organizationId);

    /**
     * 根据userId查询所属于的机构Id
     * 
     * @param userId
     * @return
     */
    @Select("select org_id from ted_org_user where user_id = #{userId}")
    Long queryOrganizetionId(Long userId);

    //    @Select("SELECT te.name  expertName,SUM(tef.free) free " +
    //            "FROM ted_expert_free tef " +
    //            "INNER JOIN ted_expert te ON tef.expert_id = te.id " +
    //            "INNER JOIN ted_training tt ON tef.project_id = tt.id " +
    //            "WHERE tef.organization_id = #{organizationId} and tef.pay_deadline_time between #{begin} and #{end} and tef.status=1 " +
    //            "GROUP BY te.name")
    @Select("select * ,tt.title as projectName ,te.name as expertName " + " from ted_expert_free tef " + "left JOIN ted_training tt ON tef.project_id = tt.id " + "left JOIN ted_expert te ON tef.expert_id = te.id " + "WHERE tef.organization_id = #{organizationId} and tef.pay_deadline_time between #{begin} and #{end} and tef.status=1 ")
    List<ExpertFreeShouldResp> queryExpertShouldPay(Long organizationId, LocalDateTime begin, LocalDateTime end);

    @Select("select * ,tt.title as projectName, te.id_card as idCard , te.education as education " + " from ted_expert_free tef " + "left JOIN ted_training tt ON tef.project_id = tt.id " + "left JOIN ted_expert te ON tef.expert_id = te.id " + " where tef.expert_id=#{id}")
    List<ExpertFreelistResp> getExpertFreeById(Long id);

    @Select("update ted_expert_free set " + "free=#{expert.free},status=#{expert.status},pay_completion_time=#{expert.payCompletionTime} " + "where project_id=#{id}")
    void updateExpertFree(Long id, ExpertFreelistResp expert);

    @Select("select * ,tt.title as projectName ,te.name as expertName " + " from ted_expert_free tef " + "left JOIN ted_training tt ON tef.project_id = tt.id " + "left JOIN ted_expert te ON tef.expert_id = te.id  " + "  ${ew.customSqlSegment} ")
    List<ExpertFreeShouldResp> queryExpertSelectedPay(@Param(Constants.WRAPPER) QueryWrapper<ExpertFreeShouldResp> queryWrapper);
}
