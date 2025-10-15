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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 类说明:
 *
 * @author 丶Anton
 * @email itanton666@gmail.com
 * @date 2025/3/24 16:14
 */
public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean validateEnrollmentTime(LocalDateTime enrollEndTime, LocalDateTime startTime) {
        return !enrollEndTime.isAfter(startTime);
    }

    public static LocalDateTime parse(String time) {
        DateTimeFormatter formatter;
        if (time.length() == 16) { // yyyy-MM-dd HH:mm
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }  else  { // yyyy-MM-dd HH:mm:ss
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        return LocalDateTime.parse(time, formatter);
    }


    public static LocalDate getLocalDate(String date, String resource) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(resource, Locale.CHINA);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = LocalDate.parse(date, inputFormatter).format(outputFormatter);
        return LocalDate.parse(formattedDate, outputFormatter);
    }

    public static LocalDate getLocalDate(String date) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日", Locale.CHINA);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = LocalDate.parse(date, inputFormatter).format(outputFormatter);
        return LocalDate.parse(formattedDate, outputFormatter);
    }

}
