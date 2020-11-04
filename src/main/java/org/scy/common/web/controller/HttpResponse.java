package org.scy.common.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Http请求响应结果
 * Created by shicy 2020/11/1
 */
public class HttpResponse {

    private CloseableHttpResponse response;
    private HttpResult result;
    private Exception error;
    private int status;
    private String body;

    public HttpResponse(CloseableHttpResponse response) {
        this.setResponse(response);
    }

    public HttpResponse(Exception error) {
        this.setError(error);
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
        if (response != null) {
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                try {
                    HttpEntity httpEntity = response.getEntity();
                    body = StringUtils.trimToEmpty(EntityUtils.toString(httpEntity));
                    String contentType = httpEntity.getContentType().getValue();
                    if (StringUtils.containsIgnoreCase(contentType, "json")) {
                        result = HttpResult.parse(body);
                    }
                } catch (IOException e) {
                    this.setError(e);
                }
            }
        }
    }

    public boolean hasError() {
        if (error != null || status != 200)
            return true;
        return result != null && result.getCode() != HttpResult.OK;
    }

    public String getErrorMessage() {
        if (error != null) {
            if (error instanceof HttpHostConnectException)
                return "服务连接失败！";
            return error.getMessage();
        }
        else if (result != null && result.getCode() != HttpResult.OK) {
            return result.getMsg();
        }
        else if (response != null) {
            if (status == 404)
                return "URL地址不存在！";
            else if (status != 200)
                return "服务错误！";
        }
        return "";
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public HttpResult getResult() {
        return result;
    }

    public String getBody() {
        return body;
    }
}
