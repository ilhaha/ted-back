package top.continew.admin.exam.model.vo;

import lombok.Data;

@Data
public class ProjectCategoryProjectFlatVo {

    /** 八大类 */
    private Long categoryId;
    private String categoryName;

    /** 项目 */
    private Long projectId;
    private String projectName;
    private Integer isOperation;
}

