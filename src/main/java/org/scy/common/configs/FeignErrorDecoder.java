package org.scy.common.configs;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;

/**
 * FeignClient 异常处理
 * Created by shicy on 2017/9/28.
 */
//@Configuration 暂时用不到
public class FeignErrorDecoder implements ErrorDecoder {

    /**
     * Feign远程接口异常时调用该方法（当服务断开无法访问时不会执行）
     */
    public Exception decode(String methodKey, Response response) {
        return FeignException.errorStatus(methodKey, response);
    }

}
