package org.scy.common.annotation;

import java.lang.annotation.*;

/**
 * 获取 access token 参数
 * Created by shicy 2017/9/26.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessToken {
    // 是否必须，如果是必须的将比拦截并返回错误，默认非必须
    boolean required() default true;
}
