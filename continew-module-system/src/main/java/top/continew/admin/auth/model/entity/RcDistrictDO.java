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

package top.continew.admin.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("ted_rc_district")
public class RcDistrictDO {

    private Long districtId;
    private Long pid;
    private String district;
    private Integer level;

    @TableField(exist = false)
    private List<RcDistrictDO> child;

    // 添加子级地区
    public void addChild(RcDistrictDO district) {
        if (this.child == null) {
            this.child = new ArrayList<>();
        }
        this.child.add(district);
    }
}
