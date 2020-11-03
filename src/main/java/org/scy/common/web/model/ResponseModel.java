package org.scy.common.web.model;

import org.apache.http.HttpResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.scy.common.web.controller.HttpResult;

/**
 * Http请求响应结果
 * Created by shicy 2020/11/1
 */
public class ResponseModel {

    private HttpResponse response;
    private HttpResult result;
    private Exception error;

    public ResponseModel(HttpResponse response) {
        this.setResponse(response);
    }

    public ResponseModel(Exception error) {
        this.setError(error);
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public boolean hasError() {
        return error != null;
    }

    public String getErrorMessage() {
        if (error != null) {
            if (error instanceof HttpHostConnectException)
                return "HTTP连接失败！";
            return error.getMessage();
        }
        return "";
    }

    public void setError(Exception error) {
        this.error = error;
    }
}
