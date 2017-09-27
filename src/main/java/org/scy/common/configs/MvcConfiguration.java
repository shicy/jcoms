package org.scy.common.configs;

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

        // Session拦截器
        registry.addInterceptor(new SessionInterceptor(configs)).addPathPatterns("/**");

        // 登录验证拦截器
        registry.addInterceptor(new LoginInterceptor(configs)).addPathPatterns("/**");
    }

}
