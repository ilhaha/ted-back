package top.continew.admin.worker.model.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2025/11/13 11:40
 */
@Data
public class ProjectInfoVO implements Serializable {
    /**
     * 八大类名称
     */
    private String categoryName;


    /**
     * 项目名称
     */
    private String projectName;


    /**
     * 班级信息
     */
    private String className;
}
