## 公共代码库

### 启动应用程序

继承类：org.scy.common.BaseApplication

##### 应用程序监听
* 启动：org.scy.common.web.listener.AppStartListener
* 准备：org.scy.common.web.listener.AppEnvironmentListener
* 完成：org.scy.common.web.listener.AppContextListener
* 异常：org.scy.common.web.listener.AppFailedListener

##### 数据库更新   
> 程序启动时，自动执行数据库脚本。需按约定命名数据库脚本文件，并标注脚本日期或时间。   
保存脚本文件在 resources 目录中，在方法`getDbScriptResource()`中返回脚本目录。   
涉及多个脚本执行顺序的问题，脚本文件名称必须是：`[版本号]名称.sql`，
如：`[1.0.0]base_create.sql`、 `[1.0.1]base_insert.sql`。   
详细说明见：org.scy.common.ds.DbUpgrade


### 配置项
* 登录地址：app.loginUrl (默认：`/login`)
* Session服务器：app.session-service.url



