package top.continew.admin.exam.model.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2026/5/12 9:15
 */
@Data
public class CandidateApplyProjectResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 考试场次类型：1初试，2补考
     */
    private Integer examAttemptType;

    /**
     * 报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷
     */
    private Integer practicalType;

}
