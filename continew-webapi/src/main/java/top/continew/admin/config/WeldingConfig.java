package top.continew.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "welding")
@Data
public class WeldingConfig {

    private Long categoryId;

    private List<Long> projectIdList;

    private List<Long> metalProjectIdList;

    private List<Long> noMetalProjectIdList;

}