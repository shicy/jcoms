package org.scy.common.configs;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.scy.common.web.session.SessionManager;

/**
 * FeignClient配置
 * Created by shicy 2019/11/05
 */
public class BaseFeignConfiguration {

    protected RequestInterceptor getAccessTokenInterceptor(final String[] excludePatterns) {
        return new RequestInterceptor() {
            public void apply(RequestTemplate requestTemplate) {
                if (excludePatterns != null && excludePatterns.length > 0) {
                    String url = requestTemplate.url();
                    for (String pattern: excludePatterns) {
                        if (url.matches(pattern))
                            return ;
                    }
                }
                String accessToken = SessionManager.getMyAccessToken();
                if (StringUtils.isNotBlank(accessToken))
                    requestTemplate.header(SessionManager.ACCESS_TOKEN_KEY, accessToken);
            }
        };
    }

}
