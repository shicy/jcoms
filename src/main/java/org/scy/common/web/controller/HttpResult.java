package org.scy.common.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * HTTP请求结果对象
 * Created by shicy on 2017/9/2
 */
@SuppressWarnings("unused")
public class HttpResult {

    // 成功
    public final static int OK = 200;
    // 重定向
    public final static int REDIRECT = 302;
    // 没有权限
    public final static int NOAUTH = 401;
    // 禁止访问
    public final static int FORBID = 403;
    // 地址不存在
    public final static int NOTFOUND = 404;
    // 服务器错误
    public final static int SERVERERROR = 500;
    // 请求失效
    public final static int INVALID = 502;

    private static ResourceBundle resource = null;

    // 错误码
    private int code = OK;

    // 错误或结果信息
    private String msg = "";

    // 结果数据集
    private Object data;

    public HttpResult() {
        //
    }

    public HttpResult(Object data) {
        this.setData(data);
    }

    public HttpResult(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }

    public HttpResult(int code, String msg, Object data) {
        this.setCode(code);
        this.setMsg(msg);
        this.setData(data);
    }

    public static HttpResult ok() {
        return ok(null);
    }

    public static HttpResult ok(Object data) {
        return new HttpResult(data);
    }

    public static HttpResult error() {
        return error(SERVERERROR);
    }

    public static HttpResult error(int code) {
        return error(code, getResourceMessage(code));
    }

    public static HttpResult error(String msg) {
        return error(SERVERERROR, msg);
    }

    public static HttpResult error(int code, String msg) {
        return new HttpResult(code, msg);
    }

    public static HttpResult error(Exception e) {
        return error(e.toString());
    }

    /**
     * 转换为 JSON 字符串
     */
    @JSONField(serialize = false)
    public String toJSON() {
        return JSON.toJSONString(this);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 获取配置资源文件消息
     * @param code 消息编码
     */
    public static String  getResourceMessage(int code) {
        return getResourceMessage(code, "");
    }

    /**
     * 获取配置资源文件消息
     * @param code 消息编码
     * @param defVal 默认值
     */
    public static String getResourceMessage(int code, String defVal) {
        if (resource == null) {
            Locale locale = Locale.getDefault();
            resource = ResourceBundle.getBundle("message", locale);
        }

        if (resource != null) {
            String value = resource.getString("" + code);
            return value == null ? defVal : value;
        }

        return defVal;
    }

}
