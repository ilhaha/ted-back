package top.continew.admin.exam.model.resp;

import lombok.Data;

@Data
public class LocationClassroomVO {
    private Long locationId;
    private String locationName;
    private Long classroomId;
    private String classroomName;
}
