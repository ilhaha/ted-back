package top.continew.admin.exam.model.req.dto;

import lombok.Data;

import java.util.List;

// 项目报名DTO
@Data
public class ProjectApplyDTO {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 考试类型：1-初考，2-补考
     */
    private Integer examAttemptType;

    /**
     * 报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷
     */
    private Integer practicalType;
}