package org.scy.common.web.session;

import feign.RequestInterceptor;
import org.scy.common.configs.BaseFeignConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * SessionClient 拦截器
 * Created by shicy 2019/11/05
 */
public class SessionClientConfiguration extends BaseFeignConfiguration {

    private static String[] accessTokenExcludes = {
        "/token/access/.*",
        "/valid/access/.*"
    };

    @Bean
    public RequestInterceptor requestInterceptor() {
        return getAccessTokenInterceptor(accessTokenExcludes);
    }

}
