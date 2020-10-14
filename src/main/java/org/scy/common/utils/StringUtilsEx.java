package org.scy.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理公共方法，是{@link org.apache.commons.lang3.StringUtils}的扩展
 * Created by hykj on 2017/8/15.
 */
public abstract class StringUtilsEx {

    private static final char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * 过滤文本内容，去除标点符号以及多余空格
     */
    public static String filterText(String text) {
        if (text == null) return "";
        return text.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5\\s]", "").replaceAll("\\s+", " ");
    }

    /**
     * 获取随机字符串
     */
    public static String getRandomString(int length) {
        return getRandomString(length, chars);
    }

    public static String getRandomString(int length, char[] chars) {
        length = length > 0 ? length : 32;
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            str.append(chars[random.nextInt(chars.length)]);
        }
        return str.toString();
    }

    /**
     * 连接一个int[]为字符串
     */
    public static String join(int[] array, String separator) {
        if (array == null)
            return null;
        if (separator == null)
            separator = "";
        StringBuilder buf = new StringBuilder();
        if (array.length > 0)
            buf.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            buf.append(separator).append(array[i]);
        }
        return buf.toString();
    }

    /**
     * 连接一个short[]为字符串
     */
    public static String join(short[] array, String separator) {
        if (array == null)
            return null;
        if (separator == null)
            separator = "";
        StringBuilder buf = new StringBuilder();
        if (array.length > 0)
            buf.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            buf.append(separator).append(array[i]);
        }
        return buf.toString();
    }

    /**
     * 连接一个String[]为字符串
     */
    public static String join(String[] array, String separator) {
        if (array == null)
            return null;
        if (separator == null)
            separator = "";
        StringBuilder buf = new StringBuilder();
        if (array.length > 0)
            buf.append("'").append(array[0]).append("'");
        for (int i = 1; i < array.length; i++) {
            buf.append(separator).append("'").append(array[i]).append("'");
        }
        return buf.toString();
    }

    /**
     * 解析URL字符串，获得其中的参数信息
     * @param strURL url字符串，如：/aa/bb/cc.jsp?a=1&b=2..
     * @return 返回 [参数名][参数值]
     */
    public static Map<String, String> parseUrlParams(String strURL) {
        String strTmp = StringUtils.substringAfter(strURL, "?");
        String[] params = StringUtils.split(strTmp, "&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param: params) {
            String name = StringUtils.substringBefore(param, "=");
            String value = StringUtils.substringAfter(param, "=");
            map.put(name, value);
        }
        return map;
    }

    /**
     * 拆分一个整数字符串
     * @param str 如：12,32,43,33
     * @param separator 分隔符
     */
    public static int[] splitAsIntValue(String str, String separator) {
        if (StringUtils.isBlank(str))
            return ArrayUtils.EMPTY_INT_ARRAY;
        String[] strs = StringUtils.split(str, separator);
        return ArrayUtilsEx.transStrToInt(strs);
    }

    /**
     * 提取一个字符串中的数据信息
     */
    public static double[] splitNumbers(String str) {
        if (StringUtils.isBlank(str))
            return new double[0];
        Pattern p = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher m = p.matcher(str);
        StringBuilder result = new StringBuilder();
        while (m.find()) {
            result.append(m.group()).append(' ');
        }
        if (result.length() == 0)
            return new double[0];
        String[] strs = result.toString().split(" ");
        double[] numbers = new double[strs.length];
        for (int i = 0; i < strs.length; i++) {
            numbers[i] = Double.parseDouble(strs[i]);
        }
        return numbers;
    }

    /**
     * 将字符串的分隔符转换为标准文件分隔符
     */
    public static String tranToFileSeparator(String str) {
        if (StringUtils.isBlank(str))
            return "";
        str = StringUtils.replace(str, "\\", "/");
        str = str.replaceAll("/+", "/");
        return StringUtils.replace(str, "/", File.separator);
    }

    /** 定义汉语拼音转换的输出格式 */
    private static final HanyuPinyinOutputFormat hypyFormat = new HanyuPinyinOutputFormat();

    static {
        // 汉语发音中的 ü 用 v 代表
        hypyFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        // 使用小写字母
        hypyFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 不显示发音语调
        hypyFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    /**
     * 将字符串中的中文字符转换为汉语拼音，发音 ü 用 v 代替。
     * @param chs 想要转换的字符串
     * @param headOnly 是否只取首字母
     * @return 返回转换后的拼音字符串
     */
    public static String toHanYuPinYin(String chs, boolean headOnly) {
        // 转化为字符数组
        char[] chars = chs.toCharArray();
        StringBuilder pyStr = new StringBuilder();
        try {
            for (char c: chars) {
                String[] py = PinyinHelper.toHanyuPinyinStringArray(c, hypyFormat);
                if (py == null) // 当转换不是中文字符时，返回null
                    pyStr.append(c);
                else if (headOnly)
                    pyStr.append(py[0].charAt(0));
                else
                    pyStr.append(py[0]);
            }
        }
        catch (Exception e) {
            return null;
        }
        return pyStr.toString();
    }

    /**
     * 获取字符串 MD5 编码
     * @param value 原字符串
     * @return MD5编码
     */
    public static String toMD5(String value) {
        if (StringUtils.isEmpty(value))
            return "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return MD5Encoder.encode(md.digest(value.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return value;
    }

}
