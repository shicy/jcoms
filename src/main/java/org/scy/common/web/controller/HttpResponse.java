package org.scy.common.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.OutputStream;

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
    private OutputStream output;

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
        if (response == null)
            return;

        status = response.getStatusLine().getStatusCode();
        if (status != 200)
            return;

        try {
            HttpEntity httpEntity = response.getEntity();
            String contentType = httpEntity.getContentType().getValue();
            if (output != null/*StringUtils.containsIgnoreCase(contentType, "octet") */) {
                httpEntity.writeTo(output);
            }
            else {
                body = StringUtils.trimToEmpty(EntityUtils.toString(httpEntity));
                if (StringUtils.containsIgnoreCase(contentType, "json")) {
                    result = HttpResult.parse(body);
                }
            }
        } catch (IOException e) {
            this.setError(e);
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

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public HttpResult getResult() {
        return result;
    }

    public String getBody() {
        return body;
    }

}
