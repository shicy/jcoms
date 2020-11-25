package org.scy.common.annotation;

import java.lang.annotation.*;

/**
 * 在类或方法上添加“@Auth”，即该类或方法需要登录验证
 * Created by shicy on 2017/9/3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
    // 用户类型
    int[] type() default {};
    // 用户角色
    int[] role() default {};
}
