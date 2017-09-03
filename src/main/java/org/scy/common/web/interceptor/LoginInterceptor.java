package org.scy.common.web.interceptor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.scy.common.BaseApplication;
import org.scy.common.annotation.Auth;
import org.scy.common.configs.AppConfigs;
import org.scy.common.web.controller.HttpResult;
import org.scy.common.web.session.SessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLEncoder;

/**
 * 登录拦截器
 * Created by shicy on 2017/9/2
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    // 系统配置信息
    protected AppConfigs configs;

    // Session 管理
    protected SessionManager sessionManager;

    /**
     * 构造方法
     * @param configs
     */
    public LoginInterceptor(AppConfigs configs) {
        this.configs = configs;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean validate = super.preHandle(request, response, handler);

        if (validate) {
            if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
                return false;
            }

            Method method = ((HandlerMethod)handler).getMethod();
            Class cls = method.getDeclaringClass();
            if (method.isAnnotationPresent(Auth.class) ||  cls.isAnnotationPresent(Auth.class)) {
                if (!getSessionManager().isSessionValidate(request)) {
                    if (method.isAnnotationPresent(ResponseBody.class) || cls.isAnnotationPresent(ResponseBody.class)) {
                        this.writeWithNoAuth(response);
                    }
                    else {
                        this.gotoLogin(request, response);
                    }
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 没有权限
     * @param response
     */
    private void writeWithNoAuth(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            HttpResult result = new HttpResult(HttpResult.NOAUTH, "请先登录");
            writer.print(result.toJSON());
            writer.flush();
        }
        finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * 重定向到登录页面
     * @param request
     * @param response
     */
    private void gotoLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirect = this.configs.getLoginUrl();
        if (StringUtils.isBlank(redirect))
            redirect = "/login";

        String currentUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString))
            currentUrl += "?" + queryString;
        redirect += "?r=" + URLEncoder.encode(currentUrl);

        response.sendRedirect(redirect);
    }

    /**
     * 获取 Session 管理器
     * @return
     */
    public SessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = BaseApplication.getContext().getBean(SessionManager.class);
        }
        return sessionManager;
    }
}
