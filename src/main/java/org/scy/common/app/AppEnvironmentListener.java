package org.scy.common.app;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用程序环境准备完成
 * @author shicy Created on 2017-08-30
 */
public class AppEnvironmentListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
        System.out.println("环境准备完成。");
    }

}
