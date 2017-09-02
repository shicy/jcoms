package org.scy.common.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用程序启动异常
 * Created by shicy on 2017/8/30
 */
public class AppFailedListener implements ApplicationListener<ApplicationFailedEvent> {

    public void onApplicationEvent(ApplicationFailedEvent applicationFailedEvent) {
//        System.out.println("启动失败。");
    }

}
