package org.scy.common.web.interceptor;

import org.scy.common.configs.AppConfigs;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常、错误拦截器
 * Created by shicy on 2017/9/29.
 */
//@Component
public class ErrorInterceptor extends HandlerInterceptorAdapter {

    // 系统配置信息
    private AppConfigs appConfigs;

    /**
     * 构造方法
     */
    public ErrorInterceptor(AppConfigs appConfigs) {
        this.appConfigs = appConfigs;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
//        System.out.println(response.getStatus());
    }

}
