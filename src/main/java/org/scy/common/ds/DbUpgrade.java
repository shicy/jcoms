package org.scy.common.ds;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.scy.common.utils.DateUtilsEx;
import org.scy.common.utils.FileUtilsEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

/**
 * 数据库更新，解析数据库脚本文件，比较数据库版本，自动增量更新数据库
 * 在脚本文件中，脚本的更新时间必需写在一行注释中（以"--"开头的行），并以“<>”包围
 * 如：-- 表 User 添加字段 address，日期：<2017-6-20>
 * 日期可选格式："yyyy-M-d", "yyyy-MM-dd", "yyyy-M-d H:m:s", "yyyy-MM-dd HH:mm:ss"
 * Created by shicy on 2017/6/8.
 */
public class DbUpgrade /*extends Thread*/ {

    private Logger logger = LoggerFactory.getLogger(DbUpgrade.class);

    private final static String versionTableName = "db_version";

    // 数据库操作
    private JdbcTemplate jdbcTemplate;
    // 脚本所在资源文件目录
    private String resourceFilePath;
    // 脚本文件集，与 resourceFilePath 是2种不同的处理方案
    private File[] scriptFiles;

    // 数据库更新监听
    private DbUpgradeListener listener;

    private String currentLine = null;
    private Date currentScriptDate = null;
    private String[] patterns = new String[]{"yyyy-M-d", "yyyy-MM-dd",
            "yyyy-M-d H:m:s", "yyyy-MM-dd HH:mm:ss", "yyyy-M-d HH:mm:ss"};

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

//    @Override
    public void run() {
        if (listener == null)
            listener = new DbUpgradeListener();

        if (jdbcTemplate == null)
            listener.fail("未知数据库连接");

        try {
            listener.before();
            initDatabase();
            doUpgrade();
            listener.after();
        }
        catch (Exception e) {
            listener.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 设置监听
     * @param listener 数据库更新的监听对象，或 null
     */
    public void setListener(DbUpgradeListener listener) {
        this.listener = listener;
    }

    /**
     * 初始化数据库，添加版本控制相关表、数据
     */
    private void initDatabase() {
        checkIfVersionTableExists();
    }

    /**
     * 执行数据库更新
     */
    private void doUpgrade() {
        URL[] scripts = getScriptResources();
        if (scripts != null && scripts.length > 0) {
            // 开启事务管理
            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
            transactionManager.setDataSource(jdbcTemplate.getDataSource());
            // DefaultTransactionDefinition 是TransactionDefinition的一个实现类
            // TransactionDefinition是事务的一些属性 如隔离级别等
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            // 首先获得当前的事务，需要一个事务定义类（规定隔离级别等属性的一个类）
            // 根据指定的属性创造一个新事务实例.
            TransactionStatus status = transactionManager.getTransaction(def);

            for (URL script: scripts) {
                String fileName = script.getFile();
                try {
                    fileName = URLDecoder.decode(fileName, "utf-8");
                }
                catch (UnsupportedEncodingException e) {
//                    e.printStackTrace(); // 忽略错误
                }
                listener.doing(fileName);

                String scriptName = StringUtils.substringAfterLast(fileName, "/");
                Date scriptDate = this.getLastVersionDate(scriptName);

                try {
                    Date lastUpdateDate = this.executeScriptFile(script, scriptDate);
                    if (DateUtilsEx.compareDate(lastUpdateDate, scriptDate) > 0)
                        this.setLastVersionDate(scriptName, lastUpdateDate);
                }
                catch (Exception e) {
                    transactionManager.rollback(status); // 更新失败，事务回滚
                    throw new RuntimeException(e);
                }
            }

            transactionManager.commit(status);
        }
        else {
            logger.warn("没有更新脚本文件。");
        }
    }

    /**
     * 检验版本数据库表是否存在，如果不存在则创建表
     */
    private void checkIfVersionTableExists() {
        if (!isTableExists(versionTableName)) {
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE `").append(versionTableName).append("` (");
            sql.append("`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,");
            sql.append("`scriptFile` VARCHAR(50) COMMENT '脚本文件名称',");
            sql.append("`scriptDate` BIGINT COMMENT '脚本更新时间',");
            sql.append("`createDate` BIGINT NULL, updateDate BIGINT NULL");
            sql.append(");");
            jdbcTemplate.execute(sql.toString());
            logger.info("创建表：" + versionTableName);
        }
    }

    /**
     * 判断数据库中是否存在某个表
     * @param tableName 数据库表名称
     * @return
     */
    private boolean isTableExists(String tableName) {
        List tables = jdbcTemplate.queryForList("show tables like '" + tableName + "';");
        return tables != null && tables.size() > 0;
    }

    /**
     * 获取当前的脚本文件信息
     * @return 返回所以脚本资源文件集
     */
    private URL[] getScriptResources() {
        List<URL> scripts = new ArrayList<URL>();

        if (resourceFilePath != null) {
            URL[] resources = FileUtilsEx.getResources(resourceFilePath, "sql", true);
            for (URL resource: resources) {
                scripts.add(resource);
            }
        }

        sortScripts(scripts);

        // 手动指定的脚本文件不排序，不建议使用
        if (scriptFiles != null) {
            try {
                for (File scriptFile: scriptFiles) {
                    scripts.add(scriptFile.toURI().toURL());
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return scripts.toArray(new URL[0]);
    }

    /**
     * 按照文件命名规则排序
     * 命名规则：[类型编号.大版本.小版本]文件名称.sql，其中文件名称不可以使用'['或']'字符
     * 文件按“类型编号 > 大版本 > 小版本”进行排序，如果没有版本信息，默认按文件名称排序
     * @param scripts 需要排序的脚本集
     */
    private void sortScripts (List<URL> scripts) {
        Collections.sort(scripts, new Comparator<URL>() {
            public int compare(URL o1, URL o2) {
                String name1 = o1.getFile();
                String name2 = o2.getFile();

                try {
                    name1 = URLDecoder.decode(name1, "utf-8");
                    name2 = URLDecoder.decode(name2, "utf-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String version1 = StringUtils.substringBetween(name1, "[", "]");
                String version2 = StringUtils.substringBetween(name2, "[", "]");

                if (version1 != null && version2 != null) {
                    String[] nums1 = StringUtils.split(version1, ".");
                    String[] nums2 = StringUtils.split(version2, ".");
                    for (int i = 0; i < 3; i++) {
                        int num1 = nums1.length > i ? Integer.parseInt(nums1[i]) : -1;
                        int num2 = nums2.length > i ? Integer.parseInt(nums2[i]) : -1;
                        if (num1 != num2)
                            return num1 < num2 ? -1 : 1;
                    }
                }
                else if (version1 != null) {
                    return 1;
                }
                else if (version2 != null) {
                    return -1;
                }

                name1 = StringUtils.substringAfter(name1, "]");
                name2 = StringUtils.substringAfter(name2, "]");

                return name1.compareToIgnoreCase(name2);
            }
        });
    }

    /**
     * 获取当前数据库脚本相应更新时间
     * @param scriptFileName 脚本文件名称
     * @return
     */
    private Date getLastVersionDate(String scriptFileName) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT scriptDate FROM ").append(versionTableName);
        query.append(" WHERE scriptFile='").append(scriptFileName).append("'");
        query.append(" ORDER BY id DESC");
        query.append(" LIMIT 1;");
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query.toString());
        if (result != null && result.size() > 0) {
            long time = (Long)result.get(0).get("scriptDate");
            return new Date(time);
        }
        return null;
    }

    /**
     * 更新数据库脚本时间
     * @param scriptFileName 脚本文件名称
     * @param scriptDate 设置脚本的更新时间
     */
    private void setLastVersionDate(String scriptFileName, Date scriptDate) {
        if (scriptDate == null)
            return ;

        StringBuilder insert = new StringBuilder();
        insert.append("INSERT INTO `").append(versionTableName).append("`");
        insert.append("(scriptFile, scriptDate, createDate)");
        insert.append(" VALUES ('")
                .append(scriptFileName).append("', ")
                .append(scriptDate.getTime()).append(", ")
                .append(new Date().getTime())
                .append(")");

        jdbcTemplate.execute(insert.toString());
    }

    /**
     * 执行单个脚本文件，从 scriptDate 之后的脚本开始顺序执行
     * @param scriptFile 当前需要执行的脚本文件
     * @param scriptDate 当前脚本的最后更新时间（上一次更新时间）
     * @return 返回当前脚本的最后更新时间
     * @throws Exception
     */
    private Date executeScriptFile(URL scriptFile, Date scriptDate) throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(FileUtilsEx.getResourceStream(scriptFile)));

            currentScriptDate = null;
            if (scriptDate != null)
                this.readAfterDate(reader, scriptDate);

            List<String> scripts = new ArrayList<String>();
            while (true) {
                String script = readNextStatement(reader);
                if (StringUtils.isNotBlank(script))
                    scripts.add(script);

                if (currentLine == null || "go".equalsIgnoreCase(currentLine) || scripts.size() > 800) {
                    if (scripts.size() > 0) {
                        jdbcTemplate.batchUpdate(scripts.toArray(new String[0]));
                        for (int i = 0, l = scripts.size(); i < l; i++) {
                            logger.debug(scripts.get(i));
                        }
                        scripts.clear();
                    }
                    if (currentLine == null)
                        break;
                }
            }

            return currentScriptDate;
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * 读取文件直到脚本的时间大小date，这样可以过滤那些已经更新的脚本，保证本次更新的是最新脚本。
     * 脚本更新时间必需在注释行，以“--”开头，时间以“<>”包围
     * @param reader
     * @param date
     * @throws IOException
     */
    private void readAfterDate(BufferedReader reader, Date date) throws IOException {
        while (this.readLine(reader)) {
            if (currentScriptDate != null && DateUtilsEx.compareDate(currentScriptDate, date) > 0)
                return ;
        }
    }

    /**
     * 获取脚本文件的下一条执行语句，如果读取到空行或者“GO”行或者以分号(;)结尾的行，
     * 则认为是一条SQL语句的结束
     * @param reader
     * @return
     * @throws IOException
     */
    private String readNextStatement(BufferedReader reader) throws IOException {
        StringBuilder query = new StringBuilder();
        while (this.readLine(reader)) {
            // 空行或者“GO”行都代表一条SQL语句的结束
            if ("".equals(currentLine) || "go".equalsIgnoreCase(currentLine))
                break;

            if (StringUtils.startsWith(currentLine, "--"))
                continue;
            if (StringUtils.startsWith(currentLine, "/*") &&
                    StringUtils.endsWith(currentLine, "*/"))
                continue;

            // 以分号(;)结尾的行也代表一条SQL语句的结束
            if (StringUtils.endsWith(currentLine, ";")) {
                // 去除结尾分号
                currentLine = currentLine.substring(0, currentLine.length() - 1);
                query.append(currentLine).append('\n');
                break;
            }
            query.append(currentLine).append('\n');
        }
        return query.toString();
    }

    /**
     * 读取文件一行内容，如果读到有标记脚本时间的注释行，则更新脚本时间值
     * @param reader
     * @return 返回是否成功读取行，往往读到文件尾时返回false
     * @throws IOException
     */
    private boolean readLine(BufferedReader reader) throws IOException {
        currentLine = reader.readLine();
        currentLine = StringUtils.trim(currentLine);

        if (StringUtils.startsWith(currentLine, "--")) {
            String strDate = StringUtils.substringBetween(currentLine, "<", ">");
            if (StringUtils.isNotBlank((strDate))) {
                try {
                    currentScriptDate = DateUtils.parseDate(strDate, patterns);
                }
                catch (ParseException e) {
                    // do nothing
                }
            }
        }

        return currentLine != null;
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
