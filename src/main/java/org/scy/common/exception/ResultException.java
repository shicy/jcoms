package org.scy.common.exception;

import org.scy.common.web.controller.HttpResult;

/**
 * 结果异常
 * Created by shicy on 2017/10/1.
 */
public class ResultException extends RuntimeException {

    private int code = 500;

    public ResultException() {
        this(HttpResult.SERVERERROR);
    }

    public ResultException(HttpResult httpResult) {
        this(httpResult.getCode(), httpResult.getMsg());
    }

    public ResultException(int code) {
        this(code, HttpResult.getResourceMessage(code));
    }

    public ResultException(String msg) {
        this(HttpResult.SERVERERROR, msg);
    }

    public ResultException(int code, String msg) {
        super(msg);
        this.setCode(code);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
