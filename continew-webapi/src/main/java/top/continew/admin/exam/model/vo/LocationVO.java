package top.continew.admin.exam.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/13 17:11
 */
@Data
public class LocationVO {

    /** 地点名称 */
    private String locationName;

    /** 关联的考场列表 */
    private List<ClassroomVO> classrooms;
}
