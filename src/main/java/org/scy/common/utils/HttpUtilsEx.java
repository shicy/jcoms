package org.scy.common.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * Http 相关工具类
 * Created by hykj on 2017/8/16.
 */
public abstract class HttpUtilsEx {

    /**
     * 将一个文件输出到Response，即下载文件
     * @param rep
     * @param file
     * @param fileName 文件在客户端显示的名称，为空时使用原文件名
     * @throws IOException
     */
    public static void writeFileToResponse(HttpServletResponse rep, File file, String fileName) throws IOException {
        if (fileName == null)
            fileName = file.getName();

        //rep.reset();
        rep.setContentType("application/x-msdownload");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        rep.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        rep.setContentLength((int)file.length());

        InputStream in = null;
        OutputStream out = null;
        try {
            // 创建文件输入流
            in = new BufferedInputStream(new FileInputStream(file));
            // 获取输出流
            out = rep.getOutputStream();
            int len, bufSize = 1024 * 4; // 4K 缓存
            byte[] buf = new byte[bufSize];
            while ((len = in.read(buf, 0, bufSize)) != -1){
                out.write(buf, 0, len);
            }
            out.flush();
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 将一个文本字符串输出到Response
     * @param rep
     * @param text
     * @throws IOException
     */
    public static void writeTextToResponse(HttpServletResponse rep, String text) throws IOException {
        rep.setContentType("text/html;charset=UTF-8");
        rep.setHeader("Cache-Contol", "no-cache");
        rep.setCharacterEncoding("GBK");
        PrintWriter pw = rep.getWriter();
        pw.write(text);
        pw.flush();
    }

    /**
     * 将一个JSON字符串输出到Response
     * @param rep
     * @param json 如：“[{id: "id1", data: [{val: "value"}]},{id: "id2"}]”，根据版本的不同
     * 		json属性名可能也要回双引号。
     * @throws IOException
     */
    public static void writeJsonToResponse(HttpServletResponse rep, String json) throws IOException {
        rep.setContentType("application/json;charset=UTF-8"); // 说明以JSON方式输出
        rep.setHeader("Cache-Control", "no-cache");
        rep.setCharacterEncoding("GBK");
        PrintWriter pw = rep.getWriter();
        pw.write(json);
        pw.flush();
    }

    /**
     * 将一个基于DOM4J的Doc文档输出到Response
     * @param rep
     * @param doc dom4j
     * @throws IOException
     */
    public static void writeDomToResponse(HttpServletResponse rep, Document doc) throws IOException {
        rep.setContentType("text/xml");
        rep.setHeader("Cache-Control", "no-cache");
        XmlUtilsEx.writeDomToStream(doc, rep.getOutputStream());
    }

    /**
     * 获取一个字符串型的请求参数值，先Parameter后Attribute。
     * @param req Http请求对象
     * @param key 参数名
     * @return
     */
    public static String getRequestString(HttpServletRequest req, String key) {
        return getRequestString(req, key, null);
    }

    /**
     * 获取一个字符串型的请求参数值。
     * @param req Http请求对象
     * @param key 参数名
     * @param defaultVal 默认值，当参数不存在或值为null时返回该值
     * @return
     */
    public static String getRequestString(HttpServletRequest req, String key, String defaultVal) {
        String val = req.getParameter(key);
        return val == null ? defaultVal : val;
    }

    /**
     * 获取一个非空白字符串型的请求参数值
     * @param req Http请求对象
     * @param key 参数名
     * @param defaultVal 默认值，当参数不存在或值为null时返回该值
     * @return
     */
    public static String getRequestStringNoBlank(HttpServletRequest req, String key, String defaultVal) {
        String val = req.getParameter(key);
        return StringUtils.isBlank(val) ? defaultVal : val;
    }

    /**
     * 获取一个整数值，如果该值不存在或无法解析将抛出异常
     * @param req Http请求对象
     * @param key 参数名
     * @return
     */
    public static Integer getRequestInt(HttpServletRequest req, String key) {
        return getRequestInt(req, key, null);
    }

    /**
     * 获取一个整数值
     * @param req Http请求对象
     * @param key 参数名
     * @param defaultVal 默认值，当参数不存在或无法解析为整数时返回该值
     * @return
     */
    public static Integer getRequestInt(HttpServletRequest req, String key, Integer defaultVal) {
        String val = req.getParameter(key);
        if (StringUtils.isBlank(val))
            return defaultVal;
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            // do nothing
        }
        return null;
    }

    /**
     * 获取一个长整型数值
     * @param req Http请求对象
     * @param key 参数名
     * @return
     */
    public static Long getRequestLong(HttpServletRequest req, String key) {
        return getRequestLong(req, key, null);
    }

    /**
     * 获取一个长整型数值
     * @param req Http请求对象
     * @param key 参数名
     * @param defaultVal 默认值
     * @return
     */
    public static Long getRequestLong(HttpServletRequest req, String key, Long defaultVal) {
        String val = req.getParameter(key);
        if (StringUtils.isBlank(val))
            return defaultVal;
        try {
            return Long.parseLong(val);
        }
        catch (NumberFormatException e) {
            // do nothing
        }
        return null;
    }

    /**
     * 获取一个精度数值
     * @param req Http请求对象
     * @param key 参数名
     * @return
     */
    public static Double getRequestDouble(HttpServletRequest req, String key) {
        return getRequestDouble(req, key, null);
    }

    /**
     * 获取一个精度数值
     * @param req Http请求对象
     * @param key 参数名
     * @param defaultVal 默认值
     * @return
     */
    public static Double getRequestDouble(HttpServletRequest req, String key, Double defaultVal) {
        String val = req.getParameter(key);
        if (StringUtils.isBlank(val))
            return defaultVal;
        try {
            return Double.parseDouble(val);
        }
        catch (NumberFormatException e) {
            // do nothing
        }
        return null;
    }

}
