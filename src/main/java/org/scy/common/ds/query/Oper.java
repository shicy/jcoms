package org.scy.common.ds.query;

import org.apache.commons.lang3.StringUtils;

/**
 * 操作符定义
 * Created by shicy on 2017/10/9.
 */
@SuppressWarnings("unused")
public enum Oper {

    /** 等于"=" */
    EQ("="),

    /** 不等于"<>" */
    NEQ("<>"),

    NEQ1("!="),

    /** 大于">"*/
    GT(">"),

    /** 小于"<"*/
    LT("<"),

    /** 大于等于">=" */
    EGT(">="),

    /** 小于等于"<=" */
    ELT("<="),

    /** 模糊匹配"like" */
    LIKE("like"),

    /** is判断，通常跟null或not null*/
    IS("is"),

    ISN("is null"),

    ISNN("is not null"),

    /** 数据范围标志，value可以是一个字符串（如select...）或一个数组（数字或字符串数组）*/
    IN("in"),

    /** 不包括 */
    NIN("not in");

    private String value;

    Oper(String value){
        this.value = value;
    }

    public static Oper get(String value) {
        value = StringUtils.trimToEmpty(value).toLowerCase();
        for (Oper oper: Oper.values()) {
            if (StringUtils.equals(value, oper.value))
                return oper;
        }
        return null;
    }

    public String toString(){
        return this.value;
    }

}
