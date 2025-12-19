package top.continew.admin.oss;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.continew.admin.system.service.OssImageDownloadService;

@Slf4j
@SpringBootTest
public class OssDownloadTest {

    @Autowired
    private OssImageDownloadService downloadService;

    @Test
    void testDownloadAllFiles() {
        log.info("==== 开始测试 OSS 全量下载 ====");
        downloadService.downloadAllFiles();
        log.info("==== OSS 全量下载测试结束 ====");
    }
}
