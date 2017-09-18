package org.scy.common.web.listener;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用程序环境准备完成
 * Created by shicy on 2017/8/30
 */
public class AppEnvironmentListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
//        System.out.println("环境准备完成。");
    }

}
