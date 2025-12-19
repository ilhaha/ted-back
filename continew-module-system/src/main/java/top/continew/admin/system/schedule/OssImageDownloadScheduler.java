package top.continew.admin.system.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.continew.admin.system.service.OssImageDownloadService;

@Slf4j
@Component
public class OssImageDownloadScheduler {

    private final OssImageDownloadService downloadService;

    public OssImageDownloadScheduler(OssImageDownloadService downloadService) {
        this.downloadService = downloadService;
    }

    /** 每天凌晨 1 点执行下载整个 Bucket */
    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() {
        log.info("OSS 全量下载任务开始");
        downloadService.downloadAllFiles();
    }
}
