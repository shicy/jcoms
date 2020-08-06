package org.scy.common.web.controller;

import org.scy.common.BaseApplication;
import org.scy.common.configs.AppConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

/**
 * 控制器基类，实现控制器公用方法
 * Created by shicy on 2017/5/11.
 */
@Controller
public class BaseController {

    @Autowired
    protected Environment env;

    protected boolean isDev() {
        String _env = env.getActiveProfiles()[0];
        return "dev".equals(_env);
    }

    /**
     * 获取版本号
     */
    protected String getAppVersion() {
        AppConfigs appConfigs = BaseApplication.getAppConfigs();
        return appConfigs.getVersion();
    }

}
