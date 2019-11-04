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

    // 应用编号
    @Value("${app.code:#{null}}")
    private String appId;

    // 应用密钥
    @Value("${app.secret:#{null}}")
    private String appSecret;

    // 登录地址
    @Value("${app.loginUrl:#{null}}")
    private String loginUrl;

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

}
