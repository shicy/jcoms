package org.scy.common.app;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用程序的上下文创建完成，但此时 spring 中的 bean 是没有完全加载完成的。
 * 在该监听器中是无法获取自定义bean并进行操作的。
 * @author shicy Created on 2017-08-30
 */
public class AppContextListener implements ApplicationListener<ApplicationPreparedEvent> {

    public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {
        System.out.println("上下文准备完成。");
    }

}
