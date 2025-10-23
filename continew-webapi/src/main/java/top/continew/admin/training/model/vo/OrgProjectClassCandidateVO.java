package top.continew.admin.training.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrgProjectClassCandidateVO {
    private Long projectId;
    private String projectLabel;
    private Long classId;
    private String classLabel;
    private Long candidateId;
    private String nickname;
}
