/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.common.util;

import com.coremedia.iso.IsoFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class VideoUtil {

    /**
     * 获取视频文件的播放长度(mp4、mov格式)
     *
     * @param videoUrl
     * @return 单位为毫秒
     */
    public static long getVideoDuration(String videoUrl) {
        File tempFile = null;
        try (InputStream in = new URL(videoUrl).openStream()) {
            tempFile = File.createTempFile("video", ".mp4");
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            IsoFile isoFile = new IsoFile(tempFile.getAbsolutePath());
            long duration = isoFile.getMovieBox().getMovieHeaderBox().getDuration();
            long timescale = isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
            return duration / timescale;
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (tempFile != null)
                tempFile.delete();
        }
    }

    /**
     * 得到语音或视频文件时长,单位秒
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static long getDuration(String filePath) {
        String format = getVideoFormat(filePath);
        long result = 0;
        if ("mp4".equals(format)) {
            result = VideoUtil.getVideoDuration(filePath);
        } else if ("mov".equals(format)) {
            result = VideoUtil.getVideoDuration(filePath);
        } else if ("m4a".equals(format)) {
            result = VideoUtil.getVideoDuration(filePath);
        } else if ("wav".equals(format)) {
            result = AudioUtil.getDuration(filePath).intValue();
        } else if ("mp3".equals(format)) {
            result = AudioUtil.getMp3Duration(filePath).intValue();
        }

        return result;
    }

    /**
     * 得到文件格式
     *
     * @param path
     * @return
     */
    public static String getVideoFormat(String path) {
        return path.toLowerCase().substring(path.toLowerCase().lastIndexOf(".") + 1);
    }

}
