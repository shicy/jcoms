package org.scy.common.web.session;

import org.apache.commons.lang3.StringUtils;
import org.scy.common.web.controller.HttpResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Session 管理类
 * Created by shicy on 2017/9/3
 */
@Component
@SuppressWarnings("unused")
public final class SessionManager {

    // 用户唯一编号，一个客户端一个编号
    public final static ThreadLocal<String> uuid = new ThreadLocal<String>();

    // 用户登录信息
    public final static ThreadLocal<String> token = new ThreadLocal<String>();

    // 接口访问权限口令
    public final static ThreadLocal<String> accessToken = new ThreadLocal<String>();

    private final static ThreadLocal<Account> accountInfo = new ThreadLocal<Account>();
    private final static ThreadLocal<User> userInfo = new ThreadLocal<User>();

    // Token 属性名称
    public final static String TOKEN_KEY = "token";

    // AccessToken 属性名称
    public final static String ACCESS_TOKEN_KEY = "X-Access-Token";

    private static SessionClient sessionClient;

    @Autowired(required = false)
    private SessionClient sessionClientTemp;

    @PostConstruct
    public void init() {
        sessionClient = sessionClientTemp;
    }

    /**
     * 获取当前用户的 Token 信息
     */
    public static String getToken() {
        return token.get();
    }

    /**
     * 获取当前用户的 AccessToken 信息
     */
    public static String getAccessToken() {
        return accessToken.get();
    }

    /**
     * 判断 AccessToken 是否有效
     */
    public static boolean isAccessEanbled() {
        String _token = accessToken.get();
        if (StringUtils.isBlank(_token))
            return false;
        HttpResult result = sessionClient.isAccessEnabled(_token);
        return "1".equals(result.getData());
    }

    /**
     * 验证当前请求用户是否是登录状态
     */
    public static boolean isSessionValidate() {
        String _token = token.get();
        if (StringUtils.isBlank(_token))
            return false;
        HttpResult result = sessionClient.isSessionValidate(_token);
        return "1".equals(result.getData());
    }

    /**
     * 获取帐户信息
     */
    public static Account getAccount() {
        Account account = accountInfo.get();
        if (account == null) {
            String _token = accessToken.get();
            if (StringUtils.isBlank(_token)) {
                HttpResult result = sessionClient.getAccount(_token);
                if (result.getCode() == HttpResult.OK) {
                    account = result.getData(Account.class);
                    accountInfo.set(account);
                }
            }
        }
        return account;
    }

    /**
     * 获取帐户编号
     */
    public static int getAccountId() {
        Account account = getAccount();
        return account != null ? account.getId() : 0;
    }

    /**
     * 获取当前用户信息
     */
    public static User getUser() {
        User user = userInfo.get();
        if (user == null) {
            String _token = token.get();
            if (StringUtils.isNotBlank(_token)) {
                HttpResult result = sessionClient.getUser(_token);
                if (result.getCode() == HttpResult.OK) {
                    user = result.getData(User.class);
                    userInfo.set(user);
                }
            }
        }
        return user;
    }

    /**
     * 获取用户编号
     */
    public static int getUserId() {
        User user = getUser();
        return user != null ? user.getId() : 0;
    }

}
