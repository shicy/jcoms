package org.scy.common.app;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用程序启动开始时执行
 * @author shicy Created on 2017-08-30
 */
public class AppStartListener implements ApplicationListener<ApplicationStartingEvent> {

    public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {
        System.out.println("应用程序开始启动。");
    }

}
