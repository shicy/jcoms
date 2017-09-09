package org.scy.common.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 应用程序配置信息
 * Created by shicy on 2017/9/3
 */
@Component
//@ConfigurationProperties(prefix = "app")
public class AppConfigs {

    // 登录地址
    @Value("${app.loginUrl:null}")
    private String loginUrl;

    // Session 服务器地址
    @Value("${app.session-service.url:null}")
    private String sessionServiceUrl;


    /**
     * 获取登录地址
     * @return
     */
    public String getLoginUrl() {
        return loginUrl;
    }

    /**
     * 获取 Session 服务器地址
     * @return
     */
    public String getSessionServiceUrl() {
        return sessionServiceUrl;
    }

}
