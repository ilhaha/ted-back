package top.continew.admin.document.model.resp;

import lombok.Data;

import java.util.List;

@Data
public class CandidateDocumentResp {
    private Long candidateId;
    private String userName;
    private String nickName;
    private List<DocumentResp> documents;  // 该考生的所有资料
}
