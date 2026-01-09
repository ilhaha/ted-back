package top.continew.admin.common.util;

import jodd.util.StringUtil;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author ilhaha
 * @Create 2026/1/9 12:02
 *
 * 下载阿里云OSS文件
 */
public class DownloadOSSFileUtil {

    public static byte[] downloadUrlToBytes(String fileUrl) {
        ValidationUtils.throwIf(StringUtil.isBlank(fileUrl), "文件地址不能为空");
        try {
            URL url = new URL(fileUrl);
            try (InputStream inputStream = url.openStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new BusinessException("文件下载失败", e);
        }
    }

}
