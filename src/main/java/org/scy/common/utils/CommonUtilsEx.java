package org.scy.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * note: 定义一些公共方法，提供静态调用。
 * Created by hykj on 2017/8/15.
 */
@SuppressWarnings("unused")
public abstract class CommonUtilsEx {

    /**
     * 检查一个IP地址的格式是否正确
     * @param strIp 一个IP地址字符串
     */
    public static boolean checkIP(String strIp) {
        if (StringUtils.isEmpty(strIp)) // 如果是空串，返回false
            return false;
        if (!"localhost".equals(strIp.toLowerCase())) { // 一般localhost也是允许的
            String[] strs = StringUtils.split(strIp, ".");
            if (strs.length != 4)
                return false; // IP长度为4
            for (int i = 0; i < 4; i++) {
                try {
                    int K = Integer.parseInt(strs[i]);
                    if (K < 0 || K > 255)
                        return false; // 必须是0到255之间的数字
                }
                catch(Exception e){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查手机号码格式是否正确
     */
    public static boolean checkMobile(String mobile) {
        if (StringUtils.isNotBlank(mobile)) {
//            String regExp = "^1((3[0-9])|(4[57])|(5[0-35-9])|(7[0135678])|(8[0-9]))\\d{8}$";
            String regExp = "^1\\d{10}$";
            Pattern pattern = Pattern.compile(regExp);
            Matcher matcher = pattern.matcher(mobile);
            return matcher.matches();
        }
        return false;
    }

    /**
     * 检查邮箱地址格式是否正确
     */
    public static boolean checkEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            String regExp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
            Pattern pattern = Pattern.compile(regExp);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
        return false;
    }

    /**
     * 打印系统环境变量
     */
    public static void showSystemProperty() {
        System.out.println("java.version:" + System.getProperty("java.version"));
        System.out.println("java.vendor:" + System.getProperty("java.vendor"));
        System.out.println("java.vendor.url:" + System.getProperty("java.vendor.url"));
        System.out.println("java.home:" + System.getProperty("java.home"));
        System.out.println("java.vm.specification.version:" + System.getProperty("java.vm.specification.version"));
        System.out.println("java.vm.specification.vendor:" + System.getProperty("java.vm.specification.vendor"));
        System.out.println("java.vm.specification.name:" + System.getProperty("java.vm.specification.name"));
        System.out.println("java.vm.version:" + System.getProperty("java.vm.version"));
        System.out.println("java.vm.vendor:" + System.getProperty("java.vm.vendor"));
        System.out.println("java.vm.name:" + System.getProperty("java.vm.name"));
        System.out.println("java.specification.version:" + System.getProperty("java.specification.version"));
        System.out.println("java.specification.vendor:" + System.getProperty("java.specification.vendor"));
        System.out.println("java.specification.name:" + System.getProperty("java.specification.name"));
        System.out.println("java.class.version:" + System.getProperty("java.class.version"));
        System.out.println("java.class.path:" + System.getProperty("java.class.path"));
        System.out.println("java.library.path:" + System.getProperty("java.library.path"));
        System.out.println("java.io.tmpdir:" + System.getProperty("java.io.tmpdir"));
        System.out.println("java.compiler:" + System.getProperty("java.compiler"));
        System.out.println("java.ext.dirs:" + System.getProperty("java.ext.dirs"));
        System.out.println("os.name:" + System.getProperty("os.name"));
        System.out.println("os.arch:" + System.getProperty("os.arch"));
        System.out.println("os.version:" + System.getProperty("os.version"));
        System.out.println("file.separator:" + System.getProperty("file.separator"));
        System.out.println("path.separator:" + System.getProperty("path.separator"));
        System.out.println("line.separator:" + System.getProperty("line.separator"));
        System.out.println("user.name:" + System.getProperty("user.name"));
        System.out.println("user.home:" + System.getProperty("user.home"));
        System.out.println("user.dir:" + System.getProperty("user.dir"));
    }

}
