package top.continew.admin.exam.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProjectCategoryTreeVo {

    private Long value;     // categoryId
    private String label;   // categoryName

    private List<ProjectVo> children;
}
