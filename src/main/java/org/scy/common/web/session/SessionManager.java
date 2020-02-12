package org.scy.common.web.session;

import org.apache.commons.lang3.StringUtils;
import org.scy.common.BaseApplication;
import org.scy.common.Const;
import org.scy.common.configs.AppConfigs;
import org.scy.common.web.controller.HttpResult;
import org.scy.common.web.model.ValidInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Session 管理类
 * Created by shicy on 2017/9/3
 */
@Component
@SuppressWarnings("unused")
public final class SessionManager {

    public final static String TOKEN_KEY = "token";
    public final static String ACCESS_TOKEN_KEY = "X-Access-Token";

    // 用户唯一编号，一个客户端一个编号
    public final static ThreadLocal<String> uuid = new ThreadLocal<String>();
    public final static ThreadLocal<String> token = new ThreadLocal<String>();
    public final static ThreadLocal<String> accessToken = new ThreadLocal<String>();

    private final static ThreadLocal<Account> accountInfo = new ThreadLocal<Account>();
    private final static ThreadLocal<User> userInfo = new ThreadLocal<User>();

    private static Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private static SessionClient sessionClient;

    // 当前应用程序获得的 AccessToken，用于访问平台账户系统
    // 需要注册一个平台账户，配置好${app.code}和${app.secret}
    private static String myAccessToken = null;
    private static long myAccessTokenTime = 0L;

    @Autowired(required = false)
    private SessionClient sessionClientTemp;

    @PostConstruct
    public void init() {
        sessionClient = sessionClientTemp;
    }

    /**
     * 获取应用程序 AccessToken，到期自动刷新
     */
    public static String getMyAccessToken() {
        AppConfigs appConfigs = BaseApplication.getAppConfigs();
        String appId = appConfigs.getAppId();
        String appSecret = appConfigs.getAppSecret();
        if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appSecret)) {
            long time = new Date().getTime();
            if (myAccessToken != null && time - myAccessTokenTime < 10 * 60 * 1000)
                return myAccessToken;

            myAccessToken = null;
            myAccessTokenTime = time;

            HttpResult result = sessionClient.getAccessToken(appId, appSecret);
            if (result.getCode() == HttpResult.OK) {
                myAccessToken = "" + result.getData();
            }
            else {
                logger.error("getMyAccessToken error: [" + result.getCode() + "]" + result.getMsg());
                throw new RuntimeException(result.getMsg());
            }
        }
        return myAccessToken;
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
     * 获取验证码信息（默认15分钟）
     */
    public static ValidInfo getValidateInfo() {
        return getValidateInfo(0);
    }

    /**
     * 获取验证码信息
     */
    public static ValidInfo getValidateInfo(int expires) {
        HttpResult result = sessionClient.getValidateInfo(expires);
        if (result.getCode() == HttpResult.OK) {
            return result.getData(ValidInfo.class);
        }
        return null;
    }

    /**
     * 验证码校验
     */
    public static boolean checkValidateCode(String codeId, String code) {
        HttpResult result = sessionClient.checkValidateCode(codeId, code);
        return "1".equals(result.getData());
    }

    /**
     * 用户登录，可以使用任何一种登录方式
     * @param username 登录名称
     * @param password 登录密码
     * @param validCodeId 验证码编号
     * @param validCode 验证码
     * @return 返回用户token信息
     */
    public static String doLogin(String username, String password, String validCodeId, String validCode) {
        return doLogin(username, password, validCodeId, validCode, 0);
    }

    /**
     * 用户登录，可以使用任何一种登录方式
     * @param username 登录名称
     * @param password 登录密码
     * @param validCodeId 验证码编号
     * @param validCode 验证码
     * @param expires 过期时间（秒）
     * @return 返回用户token信息
     */
    public static String doLogin(String username, String password, String validCodeId, String validCode, int expires) {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setPassword(password);
        loginForm.setValidCodeId(validCodeId);
        loginForm.setValidCode(validCode);
        loginForm.setLoginType(Const.LOGIN_TYPE_NAME | Const.LOGIN_TYPE_MOBILE | Const.LOGIN_TYPE_EMAIL);
        loginForm.setExpires(expires);
        return doLogin(loginForm);
    }

    /**
     * 用户登录
     * @param loginForm 登录信息
     * @return 返回用户token信息
     */
    public static String doLogin(LoginForm loginForm) {
        if (StringUtils.isNotBlank(token.get()))
            doLogout();

        if (loginForm == null)
            throw new RuntimeException("没有登录信息");

        HttpResult result = sessionClient.login(loginForm);
        if (result.getCode() == HttpResult.OK) {
            token.set("" + result.getData());
        }
        else {
            logger.error("doLogin error: [" + result.getCode() + "]" + result.getMsg());
            throw new RuntimeException(result.getMsg());
        }

        return token.get();
    }

    /**
     * 免密登录
     * @param username 登录名称
     * @return 返回用户token信息
     */
    public static String doLoginWithoutPassword(String username) {
        return doLoginWithoutPassword(username, 0);
    }

    /**
     * 免密登录
     * @param username 登录名称
     * @param expires 过期时间（秒）
     * @return 返回用户token信息
     */
    public static String doLoginWithoutPassword(String username, int expires) {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setLoginType(Const.LOGIN_TYPE_NAME | Const.LOGIN_TYPE_MOBILE | Const.LOGIN_TYPE_EMAIL);
        loginForm.setExpires(expires);
        return doLoginWithoutPassword(loginForm);
    }

    /**
     * 免密登录
     * @param loginForm 登录信息
     * @return 返回用户token信息
     */
    public static String doLoginWithoutPassword(LoginForm loginForm) {
        if (StringUtils.isNotBlank(token.get()))
            doLogout();

        if (loginForm == null)
            throw new RuntimeException("没有登录信息");

        HttpResult result = sessionClient.loginWithoutPassword(loginForm);
        if (result.getCode() == HttpResult.OK) {
            token.set("" + result.getData());
        }
        else {
            logger.error("doLoginWithoutPassword error: [" + result.getCode() + "]" + result.getMsg());
            throw new RuntimeException(result.getMsg());
        }

        return token.get();
    }

    /**
     * 用户退出登录
     */
    public static void doLogout() {
        String _token = token.get();
        if (StringUtils.isNotBlank(_token)) {
            HttpResult result = sessionClient.logout(_token);
            if (result.getCode() == HttpResult.OK) {
                token.remove();
                userInfo.remove();
            }
            else {
                logger.error("doLogout error: [" + result.getCode() + "]" + result.getMsg());
                throw new RuntimeException(result.getMsg());
            }
        }
    }

    /**
     * 获取帐户信息
     */
    public static Account getAccount() {
        Account account = accountInfo.get();
        if (account == null) {
            String _token = accessToken.get();
            if (StringUtils.isNotBlank(_token)) {
                HttpResult result = sessionClient.getAccount(_token);
                if (result.getCode() == HttpResult.OK) {
                    account = result.getData(Account.class);
                }
                accountInfo.set(account);
            }
        }
        return account != null && account.getId() > 0 ? account : null;
    }

    /**
     * 获取帐户编号
     */
    public static int getAccountId() {
        Account account = getAccount();
        return account != null ? account.getId() : 0;
    }

    /**
     * 获取帐户所有者编号
     */
    public static int getAccountOwnerId() {
        Account account = getAccount();
        return account != null ? account.getOwnerId() : 0;
    }

    /**
     * 是不是帐户所有者
     */
    public static boolean isAccountOwner(int accountId) {
        Account account = getAccount();
        if (account == null)
            return false;
        if (accountId > 0 && account.getId() != accountId)
            return false;
        int accountOwnerId = account.getOwnerId();
        return accountOwnerId > 0 && accountOwnerId == getUserId();
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
                }
                userInfo.set(user);
            }
        }
        return user != null && user.getId() > 0 ? user : null;
    }

    /**
     * 设置当前用户
     * @param _token 当前用户 Token 信息
     * @param _userInfo 用户信息
     * @param response Http响应对象
     */
    public static void setUser(String _token, User _userInfo, HttpServletResponse response) {
        setUser(_token, _userInfo, response, 0);
    }

    /**
     * 设置当前用户
     * @param _token 当前用户 Token 信息
     * @param _userInfo 用户信息
     * @param response Http响应对象
     * @param expires cookie 过期时间（秒）
     */
    public static void setUser(String _token, User _userInfo, HttpServletResponse response, int expires) {
        token.set(_token);
        userInfo.set(_userInfo);
        if (response != null) {
            Cookie cookie = new Cookie(SessionManager.TOKEN_KEY, _token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            if (StringUtils.isBlank(_token))
                cookie.setMaxAge(0);
            else
                cookie.setMaxAge(expires == 0 ? Integer.MAX_VALUE : expires);
            response.addCookie(cookie);
        }
    }

    /**
     * 获取用户编号
     */
    public static int getUserId() {
        User user = getUser();
        return user != null ? user.getId() : 0;
    }

    /**
     * 判断是不是平台帐户
     */
    public static boolean isPlatform() {
        Account account = getAccount();
        return account != null && Const.PLATFORM_CODE.equals(account.getCode());
    }

}
