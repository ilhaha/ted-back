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

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class IDPhotoConverter {
    private static final int TARGET_WIDTH = 295;
    private static final int TARGET_HEIGHT = 413;

    public static MultipartFile convertToOneInchPhoto(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new RuntimeException("无法读取一寸照图片");
        }

        BufferedImage targetImage = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = targetImage.createGraphics();

        // 等比例缩放并居中
        double scale = Math.min((double)TARGET_WIDTH / originalImage.getWidth(), (double)TARGET_HEIGHT / originalImage
            .getHeight());
        int newWidth = (int)(originalImage.getWidth() * scale);
        int newHeight = (int)(originalImage.getHeight() * scale);
        int x = (TARGET_WIDTH - newWidth) / 2;
        int y = (TARGET_HEIGHT - newHeight) / 2;
        g2d.drawImage(originalImage, x, y, newWidth, newHeight, null);
        g2d.dispose();

        // 写入到内存
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(targetImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        // 使用你已有的 InMemoryMultipartFile
        return new InMemoryMultipartFile(file.getName(), "one_inch.jpg", "image/jpeg", imageBytes);
    }
}
