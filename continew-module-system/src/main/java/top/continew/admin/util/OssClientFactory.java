package top.continew.admin.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

/**
 * 阿里云 OSS 客户端工厂
 * <p>
 * 统一创建 OSS 客户端
 */
public class OssClientFactory {

    private OssClientFactory() {}

    /**
     * 创建 OSS 客户端
     *
     * @param endpoint    OSS Endpoint，例如 oss-cn-shenzhen.aliyuncs.com
     * @param accessKeyId AccessKeyId
     * @param secretKey   AccessKeySecret
     * @return OSS 客户端对象
     */
    public static OSS build(String endpoint, String accessKeyId, String secretKey) {
        if (endpoint == null || endpoint.isBlank()) {
            throw new RuntimeException("OSS endpoint 不能为空");
        }
        if (accessKeyId == null || accessKeyId.isBlank()) {
            throw new RuntimeException("AccessKeyId 不能为空");
        }
        if (secretKey == null || secretKey.isBlank()) {
            throw new RuntimeException("AccessKeySecret 不能为空");
        }
        return new OSSClientBuilder().build(endpoint, accessKeyId, secretKey);
    }
}
