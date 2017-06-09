package org.scy.common.ds;

import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;

/**
 * 数据库更新，解析数据库脚本文件，比较数据库版本，自动增量更新数据库
 * 在脚本文件中，脚本的更新时间必需写在一行注释中（以"--"开头的行），并以“<>”包围
 * 如：-- 表 User 添加字段 address，日期：<2017-6-20>
 * 日期可选格式："yyyy-M-d", "yyyy-MM-dd", "yyyy-M-d H:m:s", "yyyy-MM-dd HH:mm:ss"
 * Created by shicy on 2017/6/8.
 */
public class DbUpgrade extends Thread {

    // 数据库更新监听
    private DbUpgradeListener listener = null;

    public DbUpgrade() {

    }


    public DbUpgrade(File[] files) {
    }

    public DbUpgrade(Resource[] resources) {

    }

    @Override
    public void run() {
        System.out.println("开始数据库升级...");
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
    public static interface DbUpgradeListener {

    }
}
