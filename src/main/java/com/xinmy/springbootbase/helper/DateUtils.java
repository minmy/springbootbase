/*
 * Copyright 2015-2020 reserved by jf61.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xinmy.springbootbase.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date start(Date now) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateFormat.format(now));
        } catch (ParseException e) {
            //
        }
        return null;
    }

    public static String date(Date date) {
        return date(date,"yyyy-MM-dd");
    }
    public static String date(Date date, String fmt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
        return dateFormat.format(date);
    }

    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Integer date2Int(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return Integer.valueOf(dateFormat.format(date));
    }

}
