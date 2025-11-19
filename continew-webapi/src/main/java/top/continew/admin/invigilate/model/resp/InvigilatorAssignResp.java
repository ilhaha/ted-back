package top.continew.admin.invigilate.model.resp;

import lombok.Data;

@Data
public class InvigilatorAssignResp {

    /**
     * id
     */
    private Long id;

    /**
     * 监考员昵称
     */
    private String nickname;

    /**
     * 监考员 ID
     */
    private Long invigilatorId;

    /**
     * 考场 ID
     */
    private Long classroomId;

    /**
     * 考场名称
     */
    private String classroomName;

    /**
     * 监考状态
     */
    private Integer invigilateStatus;
}
