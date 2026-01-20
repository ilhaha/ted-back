package top.continew.admin.exam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomInvigilatorDTO {

    // 考场 ID
    private Long classroomId;
    // 监考员 ID
    private Long invigilatorId;
}
