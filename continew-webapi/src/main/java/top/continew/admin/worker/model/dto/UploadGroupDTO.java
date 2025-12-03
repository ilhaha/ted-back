package top.continew.admin.worker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ilhaha
 * @Create 2025/12/3 9:56
 */
@Data
public class UploadGroupDTO {
    private MultipartFile idCardFront;
    private MultipartFile idCardBack;
    private MultipartFile photoOneInch;
    private MultipartFile applyForm;

    // 每个 docId 对应一个文件 + 名称
    private Map<Long, ProjectDocItem> projectDocs = new HashMap<>();

    @Data
    @AllArgsConstructor
    public static class ProjectDocItem {
        private MultipartFile file;
        private String name;   // 资料名称
    }
}

