package top.continew.admin.exam.model.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2026/4/14 16:57
 */
@Data
public class InspectionProjectResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long projectId;

    private String projectCode;

    /**
     * 考试场次类型（0既包含初试也包含补考，1只有初试，2只有补考，3无）
     */
    private Integer examAttemptType;

    /**
     * 实操类型（0默认实操，1拍片，2评片，3拍片+评片，4无）
     */
    private Integer practicalType;

}
