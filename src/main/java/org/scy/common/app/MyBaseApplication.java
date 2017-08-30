package org.scy.common.app;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationListener;

/**
 * 应用程序基类
 * @author shicy Created on 2017-08-30
 */
public class MyBaseApplication {

    // 当前应用程序实例
    private static SpringApplication application;

    /**
     * 构造方法
     */
    public MyBaseApplication() {
        application = new SpringApplication(this.getClass());
    }

    /**
     * 开始运行
     * @param args
     */
    public void run(String[] args) {
        this.setListeners(application);
        application.run(args);
        System.out.println("启动成功！");
    }

    /**
     * 设置监听器
     * @param application
     */
    protected void setListeners(SpringApplication application) {
        ApplicationListener startListener = this.getStartListener();
        if (startListener != null)
            application.addListeners(startListener);

        ApplicationListener environmentListener = this.getEnvironmentListener();
        if (environmentListener != null)
            application.addListeners(environmentListener);

        ApplicationListener contextListener = this.getContextListener();
        if (contextListener != null)
            application.addListeners(contextListener);

        ApplicationListener failedListener = this.getFailedListener();
        if (failedListener != null)
            application.addListeners(failedListener);
    }

    /**
     * 获取启动监听器
     * @return
     */
    protected ApplicationListener getStartListener() {
        return new AppStartListener();
    }

    /**
     * 获取环境监听器
     * @return
     */
    protected ApplicationListener getEnvironmentListener() {
        return new AppEnvironmentListener();
    }

    /**
     * 获取上下文监听器
     * @return
     */
    protected ApplicationListener getContextListener() {
        return new AppContextListener();
    }

    /**
     * 获取异常监听器
     * @return
     */
    protected ApplicationListener getFailedListener() {
        return new AppFailedListener();
    }

}
