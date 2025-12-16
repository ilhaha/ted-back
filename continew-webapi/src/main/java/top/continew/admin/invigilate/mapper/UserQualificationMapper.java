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

package top.continew.admin.invigilate.mapper;

import org.springframework.data.repository.query.Param;
import top.continew.admin.invigilate.model.dto.UserQualificationDTO;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.invigilate.model.entity.UserQualificationDO;

import java.util.List;

/**
 * 监考员资质证明 Mapper
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
public interface UserQualificationMapper extends BaseMapper<UserQualificationDO> {

    List<UserQualificationDTO> listByUserId(@Param("userId") Long userId);
}