package org.scy.common.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用程序配置信息
 * Created by shicy on 2017/9/3
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfigs {

    // 登录地址
    private String loginUrl;

    /**
     * 获取登录地址
     * @return
     */
    public String getLoginUrl() {
        return loginUrl;
    }

    /**
     * 设置登录地址
     * @param loginUrl
     */
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

}
