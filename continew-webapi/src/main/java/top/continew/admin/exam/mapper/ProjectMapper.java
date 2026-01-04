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
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.document.model.resp.DocumentTypeResp;
import top.continew.admin.exam.model.resp.LocationClassroomVO;
import top.continew.admin.exam.model.resp.ProjectDetailResp;
import top.continew.admin.exam.model.resp.ProjectResp;
import top.continew.admin.exam.model.resp.StudentProjectDetailResp;
import top.continew.admin.exam.model.vo.ProjectCategoryProjectFlatVo;
import top.continew.admin.exam.model.vo.ProjectClassroomFlatVO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.training.model.vo.ProjectCategoryVO;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.ProjectDO;

import java.util.List;

/**
 * 项目 Mapper
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Mapper
public interface ProjectMapper extends BaseMapper<ProjectDO> {
    IPage<ProjectDetailResp> selectProjectPage(@Param("page") Page<Object> objectPage,
                                               @Param(Constants.WRAPPER) QueryWrapper<ProjectDO> queryWrapper);

    /**
     * 通过部门id获取所有项目信息
     *
     * @param deptId
     * @return
     */
    List<ProjectCategoryProjectFlatVo> getDeptProjectTree(@Param("deptId") Long deptId,
                                                          @Param("planType") Integer planType);

    /**
     * 查询已经绑定了的地址id集合
     */
    List<Long> selectBindingLocationByIds(@Param("id") Long id);

    List<Long> selectBindingDocumentByIds(@Param("id") Long id);

    /**
     * 通过项目id获取绑定的资料类型信息
     *
     * @param projectId
     * @return
     */
    List<DocumentTypeResp> getBindingDocumentByIds(@Param("projectId") Long projectId);

    Page<ProjectResp> getAllProject(@Param("page") Page<ProjectDO> page,
                                    @Param(Constants.WRAPPER) QueryWrapper<ProjectDO> queryWrapper);

    @Select("SELECT parent_id FROM sys_dept WHERE id = #{id} ")
    Long getParentDeptId(@Param("id") Long id);

    //获取项目所需资料列表
    List<String> getDocumentList(@Param("projectId") Long projectId);

    //获取项目地点列表
    List<String> getLocationList(@Param("projectId") Long projectId);

    //获取项目详细信息
    StudentProjectDetailResp getProjectDetail(@Param("projectId") Long projectId);

    //    @Select("SELECT tp.id AS `value`, CONCAT(tp.`project_name`, '（', tp.`project_code`, '）') AS label FROM ted_project as tp")
    //    List<ProjectVo> selectOptions();

    //    @Select("SELECT tp.id AS `value`, tp.`project_name` AS label FROM ted_project as tp left join ted_exam_plan tep on tep.project_id=tp.id")
    List<ProjectVo> selectOptions();
    //    @Select("SELECT tp.id, CONCAT(tc.`name`, '（', tc.`code`, '）') as category_id, tp.image_url, tp.project_name, tp.project_code, tp.exam_duration, tp.redeme, tp.create_user, tp.update_user, tp.create_time, tp.update_time, tp.dept_id, tp.exam_fee, tp.project_status  FROM ted_project as tp LEFT JOIN ted_category as tc ON tp.category_id = tc.id WHERE tp.id = #{id} ")
    //    ProjectDetailResp get(Long id);

    @Select("select id from ted_project where project_name = #{projectName}")
    Long selectIdByProjectName(@Param("projectName") String projectName);

    List<ProjectClassroomFlatVO> getProjectsWithClassrooms();

    List<Long> getProjectRoomByProjectId(@Param("projectId") Long projectId);

    IPage<ProjectResp> orgGetAllProject(@Param("page") Page<ProjectDO> page,
                                        @Param(Constants.WRAPPER) QueryWrapper<ProjectDO> queryWrapper,
                                        @Param("userId") Long userId);

    List<ProjectCategoryVO> getSelectCategoryProject(@Param("parentIds") List<Long> parentIds);

    List<LocationClassroomVO> getLocationClassroomList(@Param("examType") Integer examType,
                                                       @Param("isOperation") Integer isOperation);
}