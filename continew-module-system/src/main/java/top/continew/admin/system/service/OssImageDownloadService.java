package top.continew.admin.system.service;

public interface OssImageDownloadService {

    /** 下载当天整个 OSS Bucket 下的所有文件 */
    void downloadAllFiles();
}
