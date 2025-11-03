package top.continew.admin.exam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamPlanDTO {
    private Long id;
    private Long maxCandidates;
}
