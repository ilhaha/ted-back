package top.continew.admin.exam.model.dto;

import lombok.Data;

@Data
public class ExamPresenceDTO {

    /**
     * 是否有实操考试
     * 0：否  1：是
     */
    private Integer isOperation;

    /**
     * 是否有道路考试
     * 0：否  1：是
     */
    private Integer isRoad;

}
