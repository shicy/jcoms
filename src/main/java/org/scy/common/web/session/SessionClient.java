package org.scy.common.web.session;

import org.scy.common.web.controller.HttpResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Session管理客户端
 * Created by shicy on 2017/9/30.
 */
@FeignClient(name = "session-service", url = "${app.session-service.url:/}")
public interface SessionClient {

    /**
     * 验证 AccessToken 是否过期
     * @return “1” - 未过期 “0” - “过期”
     */
    @RequestMapping(value = "valid/access/{token}", method = RequestMethod.GET)
    HttpResult isAccessEnabled(@PathVariable("token") String token);

    /**
     * 验证用户 session 是否过期，并激活用户一次
     * @return “1” - 未过期 “0” - “过期”
     */
    @RequestMapping(value = "valid/session/{token}", method = RequestMethod.GET)
    HttpResult isSessionValidate(@PathVariable("token") String token);

    /**
     * 获取帐户信息
     */
    @RequestMapping(value = "session/account/{token}", method = RequestMethod.GET)
    HttpResult getAccount(@PathVariable("token") String token);

    /**
     * 获取用户信息
     */
    @RequestMapping(value = "session/info/{token}", method = RequestMethod.GET)
    HttpResult getUser(@PathVariable("token") String token);

    /**
     * 登录
     * -param username 用户名称、手机号或邮箱
     * -param password 登录密码
     * -param expires 有效期（秒），大于零时有效，否则无限期
     * -param loginType 登录方式，默认所有登录方式
     * -param validCode 验证码，使用“/login/code”获取登录验证码
     * -param validCodeId 验证码编号，获取验证码时一起返回
     * @return 登录成功将返回用户token信息
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    HttpResult login(@RequestBody Map<String, Object> params);

    /**
     * 登出/退出登录
     * -param token 用户登录得到的token信息
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    HttpResult logout(@RequestBody String token);

}
