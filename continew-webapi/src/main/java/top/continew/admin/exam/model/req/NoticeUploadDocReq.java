package top.continew.admin.exam.model.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class NoticeUploadDocReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @NotNull(message = "请选择报考通知")
    private Long noticeId;

    /**
     * 资料文件列表
     */
    @Valid
    @NotEmpty(message = "请上传资料文件")
    private List<DocumentFileReq> docFileList;
}