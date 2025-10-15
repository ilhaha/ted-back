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

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AudioUtil {

    /**
     * 从 URL 下载文件到临时文件
     */
    private static File downloadToTempFile(String urlStr, String suffix) throws IOException {
        URL url = new URL(urlStr);
        File tempFile = File.createTempFile("audio", suffix);
        try (InputStream in = url.openStream()) {
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }

    /**
     * 获取 WAV 文件播放时长（秒） 支持网络 URL
     */
    public static Float getDuration(String filePath) {
        File tempFile = null;
        try {
            if (filePath.startsWith("http")) {
                tempFile = downloadToTempFile(filePath, ".wav");
            } else {
                tempFile = new File(filePath);
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(tempFile);
            AudioFormat format = audioInputStream.getFormat();
            long audioFileLength = tempFile.length();
            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();
            return audioFileLength / (frameSize * frameRate);
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        } finally {
            if (filePath.startsWith("http") && tempFile != null)
                tempFile.delete();
        }
    }

    /**
     * 获取 MP3 文件播放时长（秒） 支持网络 URL
     */
    public static Float getMp3Duration(String filePath) {
        File tempFile = null;
        try {
            if (filePath.startsWith("http")) {
                tempFile = downloadToTempFile(filePath, ".mp3");
            } else {
                tempFile = new File(filePath);
            }
            MP3File f = (MP3File)AudioFileIO.read(tempFile);
            MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();
            return Float.parseFloat(audioHeader.getTrackLength() + "");
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        } finally {
            if (filePath.startsWith("http") && tempFile != null)
                tempFile.delete();
        }
    }
}