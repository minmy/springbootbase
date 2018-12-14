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

import java.util.Date;

/**
 * @desc
 */
public class CodeGenerator {
    private final static String NUMBERS = "1234567890";
    private final static String NUMBER_ALPHA = NUMBERS + "abcdefghijkmnpqrstuvwxyz";
    private final static String dateFmt = "yyyyMMdd";

    /**
     * @param only_number true=则仅包含数字
     */
    public static String genValCode(final boolean only_number) {
        return CodeGenerator.genValCode0(only_number, 6);
    }

    /**
     * @param only_number true=则仅包含数字
     */
    public static String genValCode0(final boolean only_number, final int length) {
        StringBuilder sb = new StringBuilder(length);
        String strTable = only_number ? NUMBERS : NUMBER_ALPHA;
        int len = strTable.length();
        for (int i = 0; i < length; i++) {
            int dblR = (int) (Math.random() * len);
            sb.append(strTable.charAt(dblR));
        }
        return sb.toString();
    }

    public static String generateCode(String prefix, String lastCode) {
        String date = DateUtils.date(new Date(), dateFmt);
        if (StringUtils.isEmpty(lastCode) || !lastCode.startsWith(prefix)) {
            return prefix + date + "001";
        }
        String oldDate = lastCode.substring(prefix.length(),(prefix+dateFmt).length());
        if (!StringUtils.isEquals(oldDate,date)){
            return prefix + date + "001";
        }
        int prefixAndDateLength = prefix.length() + dateFmt.length();
        String numb = String.valueOf(Integer.valueOf("1" + lastCode.substring(prefixAndDateLength)) + 1).substring(1);
        return prefix + date + numb;
    }
}
