package org.scy.common.web.model;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 发起Http请求的参数信息
 * Created by shicy 2020/11/1
 */
public class RequestModel {

    private Method method;
    private String url;
    private Map<String, String> params;
    private String body;

    public enum Method {
        GET,
        POST,
        JSON,
        UPLOAD,
        DOWNLOAD
    }

    public RequestModel(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getUrl() {
        return StringUtils.trimToEmpty(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String urlForGet() {
        String _url = getUrl();
        if (_url.length() > 0) {
            String params = getUrlParams();
            if (params.length() > 0)
                _url += "?" + params;
        }
        return _url;
    }

    public String getUrlParams() {
        if (params == null)
            return "";
        StringBuilder builder = new StringBuilder();
        for (String key: params.keySet()) {
            if (builder.length() > 0)
                builder.append("&");
            String value = StringUtils.trimToEmpty(params.get(key));
            if (value.length() > 0) {
                try {
                    value = URLEncoder.encode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            builder.append(key).append("=").append(value);
        }
        return builder.toString();
    }

}
