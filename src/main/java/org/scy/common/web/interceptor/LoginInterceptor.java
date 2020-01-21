package org.scy.common.web.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.scy.common.annotation.Auth;
import org.scy.common.configs.AppConfigs;
import org.scy.common.utils.HttpUtilsEx;
import org.scy.common.web.controller.HttpResult;
import org.scy.common.web.session.SessionManager;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URLEncoder;

/**
 * 登录拦截器
 * Created by shicy on 2017/9/2
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    // 系统配置信息
    private AppConfigs configs;

    /**
     * 构造方法
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
            Class<?> cls = method.getDeclaringClass();
            if (method.isAnnotationPresent(Auth.class) ||  cls.isAnnotationPresent(Auth.class)) {
                if (!SessionManager.isSessionValidate()) {
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
     */
    private void writeWithNoAuth(HttpServletResponse response) throws Exception {
        HttpResult result = new HttpResult(HttpResult.NOAUTH, "请先登录");
        HttpUtilsEx.writeJsonToResponse(response, result.toJSON());
    }

    /**
     * 重定向到登录页面
     */
    private void gotoLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirect = this.configs != null ? this.configs.getLoginUrl() : null;
        if (StringUtils.isBlank(redirect))
            redirect = "/login";

        String currentUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString))
            currentUrl += "?" + queryString;
        redirect += "?r=" + URLEncoder.encode(currentUrl, "utf-8");

        response.sendRedirect(redirect);
    }

}
