package org.scy.common.web.session;

import org.scy.common.web.controller.HttpResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Session管理客户端
 * Created by shicy on 2017/9/30.
 */
@FeignClient(name = "session-service", url = "${app.session-service.url:/}")
public interface SessionClient {

    /**
     * 获取 AccessToken
     * @return 返回 AccessToken
     */
    @RequestMapping(value = "/token/{code}/{secret}", method = RequestMethod.GET)
    HttpResult getAccessToken(@PathVariable("code") String code, @PathVariable("secret") String secret);

    /**
     * 验证 AccessToken 是否过期
     * @return “1”-未过期 “0”-过期
     */
    @RequestMapping(value = "/valid/access/{token}", method = RequestMethod.GET)
    HttpResult isAccessEnabled(@PathVariable("token") String token);

    /**
     * 验证用户 session 是否过期，并激活用户一次
     * @return “1”-未过期 “0”-过期
     */
    @RequestMapping(value = "/valid/session/{token}", method = RequestMethod.GET)
    HttpResult isSessionValidate(@PathVariable("token") String token);

    /**
     * 获取登录验证码
     * @param expires 有效期，分钟
     */
    @RequestMapping(value = "/valid/code/{expires}", method = RequestMethod.GET)
    HttpResult getValidateInfo(@PathVariable("expires") int expires);

    /**
     * 验证码校验
     * @return “1”-有效 “0”-失效
     */
    @RequestMapping(value = "/valid/code/{codeId}/{code}", method = RequestMethod.GET)
    HttpResult checkValidateCode(@PathVariable("codeId") String codeId, @PathVariable("code") String code);

    /**
     * 获取帐户信息
     */
    @RequestMapping(value = "/session/account/{token}", method = RequestMethod.GET)
    HttpResult getAccount(@PathVariable("token") String token);

    /**
     * 获取用户信息
     */
    @RequestMapping(value = "/session/info/{token}", method = RequestMethod.GET)
    HttpResult getUser(@PathVariable("token") String token);

    /**
     * 登录
     * -param loginForm 登录信息
     * @return 登录成功将返回用户token信息
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    HttpResult login(@RequestBody LoginForm loginForm);

    /**
     * 登录
     * -param loginForm 登录信息
     * @return 登录成功将返回用户token信息
     */
    @RequestMapping(value = "/loginWithoutPassword", method = RequestMethod.POST)
    HttpResult loginWithoutPassword(@RequestBody LoginForm loginForm);

    /**
     * 登出/退出登录
     * -param token 用户登录得到的token信息
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    HttpResult logout(@RequestBody String token);

}
