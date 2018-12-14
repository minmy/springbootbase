/*
 * Copyright 2015-2020 reserved by jufeng.com.
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public final class StringUtils {

    /**
     */
    private StringUtils() {
        super();
    }

    /**
     * 
     */
    public static boolean isEmpty(final String str) {
        return null == str || str.trim().length() == 0;
    }

    /**
     * 检验source中是否包含search.
     */
    public static boolean contains(final String source, final String search) {
        boolean is_exist = false;
        if (StringUtils.isNotEmpty(source) && StringUtils.isNotEmpty(search)) {
            is_exist = source.indexOf(search) > 0;
        }
        return is_exist;
    }

    /**
     *
     */
    public static boolean isNotEmpty(final String str) {
        return !StringUtils.isEmpty(str);
    }

    /**
     * 如果first为空，则返回backup，否则返回first.
     */
    public static String getStrSafelly(final String source, final String backup) {
        final String ret;
        if (StringUtils.isEmpty(source)) {
            ret = backup;
        } else {
            ret = source;
        }
        return ret;
    }

    /**
     * 对大小写不敏感.<br>
     * 之一为空时，返回false.
     */
    public static boolean isEquals(final String s1, final String s2) {
        final boolean flag;
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) {
            flag = false;
        } else {
            flag = s1.trim().equalsIgnoreCase(s2.trim());
        }
        //
        return flag;
    }

    /**
     * @desc .
     */
    public static String getRandomString(final int seed) {
        final String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        final Random random = new Random();
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < seed; i++) {
            final int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * @desc 如果obj是null，则返回空字符串，否则返回obj.toString().
     */
    public static String stringify(final Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * @desc .
     */
    public static String formatNamedStr(final String source, final Map<String, Object> args) {
        final StringBuilder sb = new StringBuilder();
        final Pattern p = Pattern.compile("(\\{([\\w]+)\\})");
        final Matcher m = p.matcher(source);
        if (m.find()) {
            final String key = m.group(2);
            final Object value = args.get(key);
            sb.append(source.substring(0, m.start())).append(value);
            final String sub_source = source.substring(m.end());
            sb.append(StringUtils.formatNamedStr(sub_source, args));
        } else {
            sb.append(source);
        }
        return sb.toString();
    }

    /**
     * @desc 指定字符串是否匹配对应正则表达式.
     */
    public static boolean matches(final String str, final String pattern) {
        return Pattern.matches(pattern, str);
    }

    /**
     * @desc 判断是否为手机号码.
     */
    public static boolean isPhone(final String str) {
        return Pattern.matches("^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\\d{8}$", str);
    }

    /**
     * @desc 将字符串数组按指定分隔符拼接起来.
     */
    public static String concatStrs(final List<String> values, final String comma) {
    	if ( null != values) {
    		Optional<String> opt = values.stream().reduce((a, b) -> a + comma + b);
    		if (opt.isPresent()) {
    			return opt.get();
    		}
    	}
    	return null;
    }

    /**
     * @desc . .
     */
    public static String trim_str(final String value) {
        return StringUtils.isEmpty(value) ? "" : value.trim();
    }

    /**
     * @desc .
     */
    public static String wrapByPercent(final String value) {
        return "%" + value + "%";
    }

    /**
     * @desc 去掉utf-8编码无法支持的字符.
     */
    public static String trimOffUTF8(final String source) {
        if (StringUtils.isNotEmpty(source)) {
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
        }
        return null;
    }
    
}
