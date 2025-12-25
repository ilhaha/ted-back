package top.continew.admin.exam.model.resp;

import lombok.Data;
import java.util.List;

/**
 * 项目-考试计划级联选择器响应 DTO
 */
@Data
public class CascaderPlanResp {

    /**
     * 项目ID 或考试计划ID
     */
    private Long value;

    /**
     * 显示名称（项目名称或考试计划名称）
     */
    private String label;

    /**
     * 子级考试计划列表
     */
    private List<CascaderPlanResp> children;
}
