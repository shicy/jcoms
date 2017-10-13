package org.scy.common.ds.mybatis;

import org.scy.common.ds.query.Selector;

import java.util.List;

/**
 * Mybatis 映射文件配置基类
 * Created by shicy on 2017/5/15.
 */
public interface BaseMapper<T> {

    T getById(int id);

    int add(T model);

    int update(T model);

    int delete(T model);
    int deleteById(int id);

    List<T> find(Selector selector);
    int countFind(Selector selector);

}
