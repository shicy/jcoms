package org.scy.common.web.interceptor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.scy.common.annotation.Auth;
import org.scy.common.configs.AppConfigs;
import org.scy.common.utils.HttpUtilsEx;
import org.scy.common.web.controller.HttpResult;
import org.scy.common.web.session.SessionManager;
import org.scy.common.web.session.User;
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
public class AuthInterceptor extends HandlerInterceptorAdapter {

    // 系统配置信息
    private final AppConfigs configs;

    /**
     * 构造方法
     */
    public AuthInterceptor(AppConfigs configs) {
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

            Auth auth = null;
            if (method.isAnnotationPresent(Auth.class)) {
                auth = method.getAnnotation(Auth.class);
            }
            else if (cls.isAnnotationPresent(Auth.class)) {
                auth = cls.getAnnotation(Auth.class);
            }

            if (auth != null) {
                boolean async = method.isAnnotationPresent(ResponseBody.class) ||
                        cls.isAnnotationPresent(ResponseBody.class);
                boolean isValidate = checkLogin(request, response, async);
                if (isValidate)
                    isValidate = checkUserType(response, auth);
                if (isValidate)
                    isValidate = checkUserRoles(response, auth);
                return isValidate;
            }

            return true;
        }

        return false;
    }

    /**
     * 验证是否登录，如果未登录返回登录异常
     */
    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response,
            boolean async) throws Exception {
        if (!SessionManager.isSessionValidate()) {
            if (async)
                this.writeWithNoLogin(response);
            else
                this.gotoLogin(request, response);
            return false;
        }
        return true;
    }

    /**
     * 验证用户类型是否正确
     */
    private boolean checkUserType(HttpServletResponse response, Auth auth) throws Exception {
        if (auth.type().length > 0) {
            User user = SessionManager.getUser();
            if (user == null || !ArrayUtils.contains(auth.type(), user.getType())) {
                this.writeWithNoPriv(response);
                return false;
            }
        }
        return true;
    }

    /**
     * 验证用户角色是否正确
     */
    private boolean checkUserRoles(HttpServletResponse response, Auth auth) throws Exception {
        if (auth.role().length > 0) {
            User user = SessionManager.getUser();
            if (user == null || user.getRoleIds() == null) {
                this.writeWithNoPriv(response);
                return false;
            }
            for (int roleId: user.getRoleIds()) {
                if (ArrayUtils.contains(auth.role(), roleId))
                    return true;
            }
            this.writeWithNoPriv(response);
            return false;
        }
        return true;
    }

    /**
     * 没有登录
     */
    private void writeWithNoLogin(HttpServletResponse response) throws Exception {
        HttpResult result = new HttpResult(HttpResult.NOAUTH, "请先登录");
        HttpUtilsEx.writeJsonToResponse(response, result.toJSON());
    }

    /**
     * 没有权限
     */
    private void writeWithNoPriv(HttpServletResponse response) throws Exception {
        HttpResult result = new HttpResult(HttpResult.NOAUTH, "没有权限");
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
