package top.continew.admin.training.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/20 9:37
 */
@Data
public class ProjectCategoryVO {

    private Long parentId;
    private Long value;
    private String label;
    private List<ProjectCategoryVO> children;
}
