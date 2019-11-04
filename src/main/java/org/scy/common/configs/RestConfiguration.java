package org.scy.common.configs;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.scy.common.web.session.SessionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * Rest 请求配置
 * Created by shicy on 2017/9/8.
 */
@Configuration
@SuppressWarnings("unused")
public class RestConfiguration {

    private static String[] accessTokenExcludes = {
        "/token/access/.*",
        "/valid/access/.*"
    };

    private static boolean isAccessTokenExcluded(String url) {
        for (String pattern: accessTokenExcludes) {
            if (url.matches(pattern))
                return true;
        }
        return false;
    }

    @Bean
    @ConditionalOnMissingBean({RestOperations.class, RestTemplate.class})
    public RestOperations restOperations() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);

        RestTemplate restTemplate = new RestTemplate(factory);

        // 使用 utf-8 编码集的 conver 替换默认的 conver（默认的 string conver 的编码集为 "ISO-8859-1"）
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter converter = iterator.next();
            if (converter instanceof StringHttpMessageConverter) {
                iterator.remove();
            }
        }
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }

    /**
     * 访问远程服务接口时，带上 AccessToken 头部信息
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            public void apply(RequestTemplate requestTemplate) {
                if (!isAccessTokenExcluded(requestTemplate.url()))
                    SessionManager.tryRefreshAccessToken();
                String accessToken = SessionManager.accessToken.get();
                if (StringUtils.isNotBlank(accessToken))
                    requestTemplate.header(SessionManager.ACCESS_TOKEN_KEY, accessToken);
            }
        };
    }
}
