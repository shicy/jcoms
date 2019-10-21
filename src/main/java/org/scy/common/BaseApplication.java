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
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 应用程序基类
 * Created by shicy on 2017/8/30
 */
@SuppressWarnings("unused")
public class BaseApplication  {

    private Logger logger = LoggerFactory.getLogger(BaseApplication.class);

    // 当前应用程序实例
    private static SpringApplication application;

    // 当前应用程序上下文
    private static ApplicationContext context;

    private static BaseApplication instance;

    /**
     * 开始构建应用
     */
    public static void startup(Class appClass, String[] args) {
        BaseApplication.setApplication(new SpringApplication(appClass));
        BaseApplication.getApplication().run(args);
    }

    /**
     * 获取应用程序实例
     */
    public static SpringApplication getApplication() {
        return application;
    }

    /**
     * 设置应用程序实例
     */
    public static void setApplication(SpringApplication _application) {
        application = _application;
        initSpringApplication(application);
    }

    /**
     * 获取应用程序上下文
     */
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 设置应用程序上下文
     */
    public static void setContext(ApplicationContext _context) {
        context = _context;
    }

    private static void initSpringApplication(SpringApplication application) {
        application.addListeners(new AppStartListener());
        application.addListeners(new AppEnvironmentListener());
        application.addListeners(new AppContextListener());
        application.addListeners(new AppFailedListener());
    }

    public BaseApplication() {
        super();
        instance = this;
        this.run();
    }

    /**
     * 开始运行
     */
    protected void run() {
        // 更新数据库
        this.databaseUpgrade();

        logger.info("ready!");
    }

    /**
     * 更新数据库
     */
    private void databaseUpgrade() {
        try {
            JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
//            logger.info("JdbcTemplate: " + jdbcTemplate);
            if (jdbcTemplate != null) {
                String scriptResource = this.getDbScriptResource();
                logger.info("SqlScripts: " + scriptResource);
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
