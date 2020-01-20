package org.scy.common.web.controller;

import org.scy.common.BaseApplication;
import org.scy.common.configs.AppConfigs;

/**
 * 控制器基类，实现控制器公用方法
 * Created by shicy on 2017/5/11.
 */
public abstract class BaseController {

    /**
     * 获取版本号
     */
    protected String getAppVersion() {
        AppConfigs appConfigs = BaseApplication.getAppConfigs();
        return appConfigs.getVersion();
    }

}
