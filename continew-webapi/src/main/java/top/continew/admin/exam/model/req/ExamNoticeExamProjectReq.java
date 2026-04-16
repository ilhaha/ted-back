package top.continew.admin.exam.model.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ilhaha
 * @Create 2026/4/16 15:36
 */
@Data
public class ExamNoticeExamProjectReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long projectId;

    private LocalDateTime examTime;

    private String projectCode;

}
