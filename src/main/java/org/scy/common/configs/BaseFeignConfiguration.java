package org.scy.common.configs;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.scy.common.utils.HttpUtilsEx;
import org.scy.common.web.session.SessionManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * FeignClient配置
 * Created by shicy 2019/11/05
 */
public class BaseFeignConfiguration {

    /**
     * 添加用户代理、IP
     */
    protected void setUserAgent(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        if (request != null) {
            requestTemplate.header("User-Agent", request.getHeader("User-Agent"));
            requestTemplate.header("X-Forwarded-For", HttpUtilsEx.getIP(request));
        }
    }

    /**
     * 添加 AccessToken 请求头信息
     */
    protected void setAccessToken(RequestTemplate requestTemplate, String[] excludePatterns) {
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

    /**
     * AccessToken 拦截器，自动添加 AccessToken 请求头信息
     * @param excludePatterns 不需要添加 AccessToken 的请求地址（正则表达式字符串）
     */
    protected RequestInterceptor getAccessTokenInterceptor(final String[] excludePatterns) {
        return new RequestInterceptor() {
            public void apply(RequestTemplate requestTemplate) {
                setUserAgent(requestTemplate);
                setAccessToken(requestTemplate, excludePatterns);
            }
        };
    }

}
