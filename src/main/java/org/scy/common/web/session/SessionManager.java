package org.scy.common.web.session;

import javax.servlet.http.HttpServletRequest;

/**
 * Session 管理类
 * Created by shicy on 2017/9/3
 */
public abstract class SessionManager {

    // 用户唯一编号，一个客户端一个编号
    public final static ThreadLocal<String> uuid = new ThreadLocal<String>();

    // 用户登录信息
    public final static ThreadLocal<String> token = new ThreadLocal<String>();

    public final static String TOKEN_KEY = "token";

    /**
     * 验证当前请求用户是否是登录状态
     * @param request
     * @return
     */
    public abstract boolean isSessionValidate(HttpServletRequest request);

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

}
