package org.scy.common.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 基于 Jdbc 的服务基类
 * Created by shicy on 2017/5/12.
 */
public class JdbcBaseService extends BaseService {

    /**
     * 数据库 Jdbc 操作类
     */
    @Autowired(required = false)
    protected JdbcTemplate jdbcTemplate;

}
