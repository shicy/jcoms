package org.scy.common;

import org.scy.common.ds.DbUpgrade;
import org.scy.common.web.listener.AppContextListener;
import org.scy.common.web.listener.AppEnvironmentListener;
import org.scy.common.web.listener.AppFailedListener;
import org.scy.common.web.listener.AppStartListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 应用程序基类
 * Created by shicy on 2017/8/30
 */
public class BaseApplication {

    private Logger logger = LoggerFactory.getLogger(BaseApplication.class);

    // 当前应用程序实例
    private static SpringApplication application;

    // 当前应用程序上下文
    private static ApplicationContext context;

    /**
     * 构造方法
     */
    public BaseApplication() {
        application = new SpringApplication(this.getClass());
    }

    /**
     * 获取应用程序实例
     * @return
     */
    public static SpringApplication getApplication() {
        return application;
    }

    /**
     * 开始运行
     * @param args
     */
    public void run(String[] args) {
        // 设置监听器
        this.setListeners(application);

        // 应用程序开始运行
        context = application.run(args);

        // 更新数据库
        this.databaseUpgrade();

        // 完成
        logger.info("ready!");
    }

    /**
     * 获取应用程序上下文
     * @return
     */
    public static ApplicationContext getContext() {
        return context;
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
    protected ApplicationListener<ApplicationStartingEvent> getStartListener() {
        return new AppStartListener();
    }

    /**
     * 获取环境监听器
     * @return
     */
    protected ApplicationListener<ApplicationEnvironmentPreparedEvent> getEnvironmentListener() {
        return new AppEnvironmentListener();
    }

    /**
     * 获取上下文监听器
     * @return
     */
    protected ApplicationListener<ApplicationPreparedEvent> getContextListener() {
        return new AppContextListener();
    }

    /**
     * 获取异常监听器
     * @return
     */
    protected ApplicationListener<ApplicationFailedEvent> getFailedListener() {
        return new AppFailedListener();
    }

    /**
     * 更新数据库
     */
    private void databaseUpgrade() {
        try {
            JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
            if (jdbcTemplate != null) {
                String scriptResource = this.getDbScriptResource();
                if (scriptResource != null) {
                    new DbUpgrade(jdbcTemplate, scriptResource).run();
                }
            }
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.warn(e.toString());
        }
    }

    /**
     * 获取数据库脚本文件
     * @return 返回脚本所在资源文件目录
     */
    protected String getDbScriptResource() {
        return null;
    }

}
