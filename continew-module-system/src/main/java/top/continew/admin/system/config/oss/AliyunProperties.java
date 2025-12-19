package top.continew.admin.system.config.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云配置属性
 * <p>
 * 从 application.yml 或 application.properties 中读取
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProperties {

    private String accessKeyPath;
    private String region;

    private Oss oss = new Oss();

    @Data
    public static class Oss {
        private String endpoint;
        private String bucketName;
        private String localBasePath;
    }
}
