package top.continew.admin.exam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamPlanExcelRowDTO {
    private String planName;
    private Long projectId;
    private List<Long> classroomIds;
    private LocalDateTime signupStartTime, signupEndTime, examStartTime, examEndTime;
    private int rowIndex;
}