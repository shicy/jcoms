package org.scy.common.configs;

import org.scy.common.web.interceptor.ErrorInterceptor;
import org.scy.common.web.interceptor.LoginInterceptor;
import org.scy.common.web.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * MVC 框架配置
 */
@Configuration
@SuppressWarnings("unused")
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private AppConfigs configs;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);

        // 异常拦截器(包含 404 500 等状态异常)
        registry.addInterceptor(new ErrorInterceptor(configs)).addPathPatterns("/**");

        // Session拦截器
        registry.addInterceptor(new SessionInterceptor(configs)).addPathPatterns("/**");

        // 登录验证拦截器
        registry.addInterceptor(new LoginInterceptor(configs)).addPathPatterns("/**");
    }

}
