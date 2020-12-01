package org.scy.common.web.service;

import org.scy.common.web.session.SessionManager;

/**
 * 服务类基类
 * Created by shicy on 2017/5/12.
 */
public class BaseService {

    protected int getUserId() {
        return SessionManager.getUserId();
    }

}
