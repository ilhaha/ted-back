package top.continew.admin.document.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import top.continew.admin.document.model.dto.DocFileDTO;

import java.util.List;
import java.util.Map;

/**
 * @author ilhaha
 * @Create 2025/10/22 19:35
 *
 * 考试通过二维码上传资料
 */
@Data
public class QrcodeUploadReq {

    /**
     * 考生id
     */
    @NotBlank(message = "二维码错误")
    private String candidateId;

    /**
     * 计划id
     */
    @NotBlank(message = "二维码错误")
    private String planId;

    /**
     * 身份证后六位
     */
    @NotBlank(message = "身份证后六位未填写")
    private String idLastSix;

    /**
     * 资料申请表文件集合
     */
    @NotEmpty(message = "报考资料为空")
    private List<DocFileDTO> docFileList;

    /**
     * 资料申请表文件
     */
    @NotEmpty(message = "报名资格申请表为空")
    private String qualificationFileUrl;
}
