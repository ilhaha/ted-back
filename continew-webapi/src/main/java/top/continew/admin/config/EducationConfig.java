package top.continew.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "education")
public class EducationConfig {

    /**
     * 不需要学历认证的学历列表
     */
    private List<String> noVerifyList;
}