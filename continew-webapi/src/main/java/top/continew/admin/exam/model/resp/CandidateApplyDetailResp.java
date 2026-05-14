package top.continew.admin.exam.model.resp;

import lombok.Data;
import top.continew.admin.exam.model.entity.LicenseHolderInfoDO;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author ilhaha
 * @Create 2026/5/12 8:44
 */
@Data
public class CandidateApplyDetailResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报考的基本信息
     */
    private ExamineeNoticeApplyResp examineeNoticeApplyResp;

    /**
     * 考生基本信息
     */
    private ExamIdcardResp examIdcardResp;

    /**
     * 考生报考项目
     */
    List<CandidateApplyProjectResp> applyProjectList;

    /**
     * 考生持证老板
     */
    List<LicenseHolderInfoResp> licenseHolderList;

    /**
     * 考生已上传的资料列表
     */
    List<UploadedDocumentTypeVO> alreadyUploadDocList;

}
