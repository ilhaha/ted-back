package top.continew.admin.exam.model.vo;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/10/11 10:42
 */
@Data
public class ProjectClassroomFlatVO {

    private Long projectId;
    private String projectName;
    private String projectCode;
    private String categoryName;
    private Long classroomId;
    private String classroomName;
    private Integer examType;
}
