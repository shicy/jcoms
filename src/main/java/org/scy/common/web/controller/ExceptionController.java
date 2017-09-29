package org.scy.common.web.controller;

import feign.RetryableException;
import org.apache.commons.lang3.StringUtils;
import org.scy.common.Const;
import org.scy.common.exception.WebPageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理
 * Created by shicy on 2017/9/29.
 */
@ControllerAdvice
@SuppressWarnings("unused")
public class ExceptionController {

    private Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    /**
     * Feign 远程服务连接异常
     */
    @ResponseBody
    @ExceptionHandler(RetryableException.class)
    public HttpResult handlerRetryableException(RetryableException e) {
        String message = e.getMessage();
        logger.error(message);

        if (StringUtils.contains(message, "Connection refused"))
            return HttpResult.error(Const.MSG_CODE_SERVERREFUSED);

        return HttpResult.error(e);
    }

    /**
     * 全局通用异常，给客户端返回异常信息，状态码 500
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public HttpResult handlerException(Exception e) {
        logger.error(e.getMessage());
        return HttpResult.error(e);
    }

    /**
     * 网页请求异常
     */
    @ExceptionHandler(WebPageException.class)
    public String handlerWebPageException(Exception e) {
        logger.error(e.getMessage());
        return "forward:/error/500";
    }

}
