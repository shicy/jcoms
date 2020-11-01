package org.scy.common.web.model;

import java.util.Map;

/**
 * 发起Http请求的参数信息
 * Created by shicy 2020/11/1
 */
public class RequestModel {

    private String url;
    private Map<String, String> params;

    public RequestModel() {}

    public RequestModel(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
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

    public String getUrlParams() {
        return "";
    }

}
