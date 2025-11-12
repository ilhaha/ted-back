package top.continew.admin.exam.model.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2025/11/12 15:55
 */
@Data
public class WorkerAuditNoticeResp implements Serializable {

    private String nickname;

    private String auditNoticeUrl;
}
