package org.scy.common.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;

/**
 * 数据库更新，解析数据库脚本文件，比较数据库版本，自动增量更新数据库
 * 在脚本文件中，脚本的更新时间必需写在一行注释中（以"--"开头的行），并以“<>”包围
 * 如：-- 表 User 添加字段 address，日期：<2017-6-20>
 * 日期可选格式："yyyy-M-d", "yyyy-MM-dd", "yyyy-M-d H:m:s", "yyyy-MM-dd HH:mm:ss"
 * Created by shicy on 2017/6/8.
 */
public class DbUpgrade extends Thread {

    private Logger logger = LoggerFactory.getLogger(DbUpgrade.class);

    // 数据库操作
    private JdbcTemplate jdbcTemplate;
    // 脚本所在资源文件目录
    private String resourceFilePath;
    // 脚本文件集，与 resourceFilePath 是2种不同的处理方案
    private File[] scriptFiles;

    // 数据库更新监听
    private DbUpgradeListener listener;

    /**
     * 构造方法，根据资源文件夹更新数据库
     * 遍历资源脚本文件（“.sql”文件，包含子目录），按照文件版本规则排序，顺序执行
     * 其中，版本规则是：[类型编号.大版本.小版本]，后面跟文件名称，如果没有版本信息默认按文件名称排序
     * @param jdbcTemplate 数据库链接方法
     * @param resourceFilePath 脚本所在资源文件目录
     */
    public DbUpgrade(JdbcTemplate jdbcTemplate, String resourceFilePath) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceFilePath = resourceFilePath;
    }

    /**
     * 构造方法，指定脚本文件对象集，按顺序执行脚本
     * @param jdbcTemplate 数据库链接方法
     * @param scriptFiles 脚本文件集
     */
    public DbUpgrade(JdbcTemplate jdbcTemplate, File[] scriptFiles) {
        this.jdbcTemplate = jdbcTemplate;
        this.scriptFiles = scriptFiles;
    }

    @Override
    public void run() {
        if (listener == null)
            listener = new DbUpgradeListener();

        if (jdbcTemplate == null)
            listener.fail("未知数据库连接");

        listener.before();
    }

    private void initDatabase() {

    }

    /**
     * 设置监听
     * @param listener
     */
    public void setListener(DbUpgradeListener listener) {
        this.listener = listener;
    }

    /**
     * 数据库更新监听类
     */
    public static class DbUpgradeListener {

        protected Logger logger = LoggerFactory.getLogger(DbUpgradeListener.class);

        public void before() {
            logger.info("开始数据库更新...");
            // do something before upgrade
        }

        public void doing(String fileName) {
            logger.info("正在执行脚本：" + fileName);
            // do something when execute scripts
        }

        public void after() {
            logger.info("数据库更新已完成！");
            // do something after upgrade
        }

        public void fail(String errorMsg) {
            logger.error("数据库更新失败：" + errorMsg);
            throw new RuntimeException(errorMsg);
        }

    }
}
