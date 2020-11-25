package org.scy.common.configs;

import org.scy.common.BaseApplication;
import org.scy.common.web.interceptor.AccessTokenInterceptor;
import org.scy.common.web.interceptor.ErrorInterceptor;
import org.scy.common.web.interceptor.AuthInterceptor;
import org.scy.common.web.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * MVC 框架配置
 */
@Configuration
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);

        AppConfigs configs = BaseApplication.getAppConfigs();

        // 异常拦截器(包含 404 500 等状态异常)
        registry.addInterceptor(new ErrorInterceptor(configs)).addPathPatterns("/**");

        // Session拦截器
        registry.addInterceptor(new SessionInterceptor(configs)).addPathPatterns("/**");

        registry.addInterceptor(new AccessTokenInterceptor(configs)).addPathPatterns("/**");

        // 登录验证拦截器
        registry.addInterceptor(new AuthInterceptor(configs)).addPathPatterns("/**");
    }

}
