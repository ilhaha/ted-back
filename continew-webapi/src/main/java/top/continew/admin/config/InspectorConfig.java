package top.continew.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;

import java.util.List;

@ConfigurationProperties(prefix = "inspector")
@Component
@Data
public class InspectorConfig {


    /**
     * 非必要上传资料
     */
    private List<UploadedDocumentTypeVO> optionalUploadDocs;

    /**
     * 不需要上传证书的类别ID（取证考试 一级）
     */
    private List<Long> noUploadCategoryIds;

}