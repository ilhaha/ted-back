package top.continew.admin.exam.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/11 10:18
 */
@Data
public class ProjectWithClassroomVO {

    /** 项目ID */
    private Long projectId;
    /** 项目名称 */
    private String projectName;
    /** 项目代码 */
    private String projectCode;
    /** 分类名称 */
    private String categoryName;
    /** 项目类型 */
    private Integer projectType;
    /** 关联的考场地点列表 */
    private List<LocationVO> locations;
}
