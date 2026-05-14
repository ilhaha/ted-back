package top.continew.admin.exam.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ilhaha
 * @Create 2026/5/14 16:31
 */
@Data
public class CandidateOfPlanAndRoomDTO {

    private Long sort;

    private Long planId;

    private Integer examAttemptType;

    private Long classroomId;

    private String projectCode;

    private LocalDateTime startTime;
}
