package org.scy.common.web.session;

import org.apache.commons.lang3.StringUtils;
import org.scy.common.Const;
import org.scy.common.web.controller.HttpResult;
import org.scy.common.web.model.ValidInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

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
    private final static ThreadLocal<Long> accessTokenTime = new ThreadLocal<Long>();

    // Token 属性名称
    public final static String TOKEN_KEY = "token";

    // AccessToken 属性名称
    public final static String ACCESS_TOKEN_KEY = "X-Access-Token";

    private static Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private static SessionClient sessionClient;

    @Value("${app.code}")
    private static String AppId;

    @Value("${app.secret}")
    private static String AppSecret;

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
     * 试图刷新 AccessToken
     * AccessToken 具有15分钟有效期，调用该方法将择机刷新 AccessToken
     */
    public static void tryRefreshAccessToken() {
        if (StringUtils.isNotBlank(AppId) && StringUtils.isNotBlank(AppSecret)) {
            Long time = new Date().getTime();
            if (accessToken.get() != null) {
                Long lastTime = accessTokenTime.get();
                if (lastTime != null && time - lastTime < 15 * 60 * 1000)
                    return ;
            }

            accessToken.remove();
            accessTokenTime.set(time);

            HttpResult result = sessionClient.getAccessToken(AppId, AppSecret);
            if (result.getCode() == HttpResult.OK) {
                accessToken.set("" + result.getData());
            }
            else {
                logger.error(result.getCode() + "-" + result.getMsg());
                throw new RuntimeException(result.getMsg());
            }
        }
        else {
            logger.warn("缺少配置项：app.code 或 app.secret！");
        }
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
     * 获取验证码信息
     */
    public static ValidInfo getValidCode() {
        HttpResult result = sessionClient.getValidCode();
        if (result.getCode() == HttpResult.OK) {
            return result.getData(ValidInfo.class);
        }
        return null;
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

        tryRefreshAccessToken();
        HttpResult result = sessionClient.login(loginForm);
        if (result.getCode() == HttpResult.OK) {
            token.set("" + result.getData());
        }
        else {
            logger.error(result.getCode() + "-" + result.getMsg());
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

        tryRefreshAccessToken();
        HttpResult result = sessionClient.loginWithoutPassword(loginForm);
        if (result.getCode() == HttpResult.OK) {
            token.set("" + result.getData());
        }
        else {
            logger.error(result.getCode() + "-" + result.getMsg());
        }

        return token.get();
    }

    /**
     * 用户退出登录
     */
    public static void doLogout() {
        String _token = token.get();
        if (StringUtils.isNotBlank(_token)) {
            tryRefreshAccessToken();
            HttpResult result = sessionClient.logout(_token);
            if (result.getCode() == HttpResult.OK) {
                token.remove();
                userInfo.remove();
            }
            else {
                logger.error(result.getCode() + "-" + result.getMsg());
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
                else {
                    account = new Account();
                    logger.error(result.getCode() + "-" + result.getMsg());
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
                else {
                    user = new User();
                    logger.error(result.getCode() + "-" + result.getMsg());
                }
                userInfo.set(user);
            }
        }
        return user != null && user.getId() > 0 ? user : null;
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
