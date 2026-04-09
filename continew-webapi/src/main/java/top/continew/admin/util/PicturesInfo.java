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

package top.continew.admin.util;

import java.io.Serializable;

/**
 * 图片信息
 */
public class PicturesInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 最小行
     */
    private int minRow;

    /**
     * 最大行
     */
    private int maxRow;

    /**
     * 最小列
     */
    private int minCol;

    /**
     * 最大列
     */
    private int maxCol;

    /**
     * 扩展
     */
    private String ext;

    /**
     * 图片数据
     */
    private byte[] pictureData;

    public int getMinRow() {
        return minRow;
    }

    public PicturesInfo setMinRow(int minRow) {
        this.minRow = minRow;
        return this;
    }

    public int getMaxRow() {
        return maxRow;
    }

    public PicturesInfo setMaxRow(int maxRow) {
        this.maxRow = maxRow;
        return this;
    }

    public int getMinCol() {
        return minCol;
    }

    public PicturesInfo setMinCol(int minCol) {
        this.minCol = minCol;
        return this;
    }

    public int getMaxCol() {
        return maxCol;
    }

    public PicturesInfo setMaxCol(int maxCol) {
        this.maxCol = maxCol;
        return this;
    }

    public String getExt() {
        return ext;
    }

    public PicturesInfo setExt(String ext) {
        this.ext = ext;
        return this;
    }

    public byte[] getPictureData() {
        return pictureData;
    }

    public PicturesInfo setPictureData(byte[] pictureData) {
        this.pictureData = pictureData;
        return this;
    }
}
