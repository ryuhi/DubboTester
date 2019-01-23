package com.ryuhi.util.dubbo.test.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	private static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)");

    public static Map<String, String> parseQueryString(String qs) {
        return (qs != null && qs.length() != 0 ? parseKeyValuePair(qs, "\\&") : new ConcurrentHashMap<>());
    }
    
    private static Map<String, String> parseKeyValuePair(String str, String itemSeparator) {
        String[] tmp = str.split(itemSeparator);
        Map<String, String> map = new ConcurrentHashMap<>(tmp.length);

        for(int i = 0; i < tmp.length; ++i) {
            Matcher matcher = KVP_PATTERN.matcher(tmp[i]);
            if (matcher.matches()) {
                map.put(matcher.group(1), matcher.group(2));
            }
        }

        return map;
    }
}
