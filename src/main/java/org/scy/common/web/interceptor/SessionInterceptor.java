package org.scy.common.web.interceptor;

import org.scy.common.configs.AppConfigs;
import org.scy.common.utils.StringUtilsEx;
import org.scy.common.web.session.SessionManager;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户 Session 相关连接器
 * Created by shicy on 2017/9/7.
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {

    // 配置参数
    protected AppConfigs configs;

    /**
     * 构造方法
     * @param configs
     */
    public SessionInterceptor(AppConfigs configs) {
        this.configs = configs;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean validate = super.preHandle(request, response, handler);

        SessionManager.uuid.remove();
        SessionManager.token.remove();

        if (validate) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie: cookies) {
                    String name = cookie.getName();
                    if ("uuid".equals(name))
                        SessionManager.uuid.set(cookie.getValue());
                    else if (SessionManager.TOKEN_KEY.equals(name))
                        SessionManager.token.set(cookie.getValue());
                }
            }

            if (SessionManager.uuid.get() == null) {
                String uuid = StringUtilsEx.getRandomString(64);
                SessionManager.uuid.set(uuid);
                Cookie cookie = new Cookie("uuid", uuid);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(Integer.MAX_VALUE); // 永久
                response.addCookie(cookie);
            }
        }

        return validate;
    }

}
