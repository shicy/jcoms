package org.scy.common.web.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;

/**
 * Session 管理类
 * Created by shicy on 2017/9/3
 */
public final class SessionManager {

    // 用户唯一编号，一个客户端一个编号
    public final static ThreadLocal<String> uuid = new ThreadLocal<String>();

    // 用户登录信息
    public final static ThreadLocal<String> token = new ThreadLocal<String>();

    // Token 属性名称
    public final static String TOKEN_KEY = "token";

    @Autowired
    private RestOperations restOperations;

    private static SessionManager sessionManager;

    /**
     * 构造方法，
     * 单例，请通过 Session.getInstance() 获取实例
     */
    private SessionManager() {
        // do nothing
    }

    /**
     * 获取 Session 管理实例
     * @return
     */
    public static SessionManager getInstance() {
        if (sessionManager == null) {
            synchronized (SessionManager.class) {
                if (sessionManager == null)
                    sessionManager = new SessionManager();
            }
        }
        return sessionManager;
    }

    /**
     * 获取用户唯一编号
     * @param request
     * @return
     */
    public String getUUID(HttpServletRequest request) {
        return uuid.get();
    }

    /**
     * 获取用户 token 信息
     * @param request
     * @return
     */
    public String getToken(HttpServletRequest request) {
        return token.get();
    }

    /**
     * 验证当前请求用户是否是登录状态
     * @param request
     * @return
     */
    public boolean isSessionValidate(HttpServletRequest request) {
        if (getToken(request) == null)
            return false;
        return false;
    }

}
