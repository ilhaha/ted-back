package top.continew.admin.training.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TrainingCheckinExportDTO {
    private Integer index;          // 序号
    private String trainingName;       // 培训课程名称
    private String candidateName;  // 考生姓名
    private LocalDateTime checkinTime; // 签到时间
}
