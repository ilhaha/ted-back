package top.continew.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;

import java.util.List;
import java.util.Map;

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

    /**
     * 默认开考密码
     */
    private String defaultPassword;

    /**
     * 准考证生成规则映射
     */
    private Map<String, Integer> projectTypeMap;

    /**
     * 理论考试默认配置
     */
    private ExamDefaultConfig theory;

    /**
     * 实操考试默认配置
     */
    private ExamDefaultConfig oper;

    @Data
    public static class ExamDefaultConfig {

        /**
         * 默认监考员ID
         */
        private Long invigilatorId;

        /**
         * 默认考场ID
         */
        private Long roomId;
    }

}