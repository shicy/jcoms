package org.scy.common.web.session;

import org.scy.common.web.controller.HttpResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Session管理客户端
 * Created by shicy on 2017/9/30.
 */
@FeignClient(name = "session-service", url = "${app.session-service.url:/}")
public interface SessionClient {

    @RequestMapping(value = "valid/access/{token}", method = RequestMethod.GET)
    HttpResult isAccessEnabled(@PathVariable("token") String token);

    @RequestMapping(value = "valid/session/{token}", method = RequestMethod.GET)
    HttpResult isSessionValidate(@PathVariable("token") String token);

    @RequestMapping(value = "session/account/{token}", method = RequestMethod.GET)
    HttpResult getAccount(@PathVariable("token") String token);

    @RequestMapping(value = "session/info/{token}", method = RequestMethod.GET)
    HttpResult getUser(@PathVariable("token") String token);

}
