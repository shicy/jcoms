package org.scy.common.web.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Session 管理类
 * Created by shicy on 2017/9/3
 */
public class SessionManager {

    private final static String TOKEN_KEY = "token";

    /**
     * 验证当前请求用户是否是登录状态
     * @param request
     * @return
     */
    public static boolean isSessionValidate(HttpServletRequest request) {
        return false;
    }

    /**
     * 获取用户 token 信息
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (TOKEN_KEY.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
