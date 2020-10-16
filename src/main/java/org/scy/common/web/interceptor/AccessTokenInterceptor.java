package org.scy.common.web.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.scy.common.annotation.AccessToken;
import org.scy.common.configs.AppConfigs;
import org.scy.common.utils.HttpUtilsEx;
import org.scy.common.web.controller.HttpResult;
import org.scy.common.web.session.SessionManager;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * AccessToken 拦截器
 * Created by shicy on 2017/9/26.
 */
public class AccessTokenInterceptor extends HandlerInterceptorAdapter {

    // 系统配置信息
//    private AppConfigs appConfigs;

    /**
     * 构造方法
     */
    public AccessTokenInterceptor(AppConfigs appConfigs) {
//        this.appConfigs = appConfigs;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean validate = super.preHandle(request, response, handler);

        SessionManager.accessToken.remove();

        if (validate) {
            if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
                return false;
            }

            String token = request.getHeader(SessionManager.ACCESS_TOKEN_KEY);
            SessionManager.accessToken.set(token);

            AccessToken accessToken = null;

            Method method = ((HandlerMethod)handler).getMethod();
            if (method.isAnnotationPresent(AccessToken.class)) {
                accessToken = method.getAnnotation(AccessToken.class);
            }
            else {
                Class<?> cls = method.getDeclaringClass();
                if (cls.isAnnotationPresent(AccessToken.class))
                    accessToken = cls.getAnnotation(AccessToken.class);
            }

            if (accessToken != null) {
                if (StringUtils.isBlank(token)) {
                    if (accessToken.required()) {
                        writeWithNoToken(response, "缺少 AccessToken 信息！");
                        return false;
                    }
                }
                else if (!SessionManager.isAccessEanbled()) {
                    writeWithNoToken(response, "无效的 AccessToken: " + token);
                    return false;
                }
            }
        }

        return validate;
    }

    /**
     * 获取 AccessToken 失败，返回相应信息
     */
    private void writeWithNoToken(HttpServletResponse response, String msg) throws Exception {
        HttpResult result = new HttpResult(HttpResult.FORBID, msg);
        HttpUtilsEx.writeJsonToResponse(response, result.toJSON());
    }

}
