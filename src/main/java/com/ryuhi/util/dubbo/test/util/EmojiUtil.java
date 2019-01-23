package com.ryuhi.util.dubbo.test.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public class EmojiUtil {

    private static final Pattern EMOJI_PATTERN = Pattern.compile("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]|[\\ud83e\\udd00-\\ud83e\\uddff]|[\\u2300-\\u23ff]|[\\u2500-\\u25ff]|[\\u2100-\\u21ff]|[\\u0000-\\u00ff]|[\\u2b00-\\u2bff]|[\\u2d06]|[\\u3030]");

    private EmojiUtil() {
    }

    public static boolean containsEmoji(String string) {
        return privateContainsEmoji(string);
    }

/*    public static int getRealCharacterQuantiy(String str) {
        String[] result = EMOJI_PATTERN.split(str);
        int count = 0;
        Matcher matcher = EMOJI_PATTERN.matcher(str);
        if (privateContainsEmoji(str)) {
            AtomicInteger i = new AtomicInteger(0);
            while(matcher.find()){
                i.incrementAndGet();
            }
            AtomicInteger ai = null;
            if (ai = new AtomicInteger(0); ai < str) {
                count = count + result[i].length();
            }
            return count+i.get();
        }
        return str.length();
    }*/

    private static boolean privateContainsEmoji(String str) {
        Matcher m = EMOJI_PATTERN.matcher(str);
        return m.find();
    }

    public static String emojiConvert1(String str)
            throws UnsupportedEncodingException {
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(
                        sb,
                        "[["
                                + URLEncoder.encode(matcher.group(1),
                                "UTF-8") + "]]");
            } catch (UnsupportedEncodingException e) {
                throw e;
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param str 转换后的字符串
     * @return 转换前的字符串
     * @throws UnsupportedEncodingException exception
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     */
    public static String emojiRecovery2(String str)
            throws UnsupportedEncodingException {
        String patternString = "\\[\\[(.*?)\\]\\]";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(sb,
                        URLDecoder.decode(matcher.group(1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw e;
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) {
        String str = "fada😂🙃😝";
        String newStr = StringEscapeUtils.escapeJava(str);
        System.out.println("newStr = " + newStr);
        System.out.println("str.length() = " + str.length());
    }
}