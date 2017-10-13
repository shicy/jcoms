package org.scy.common.ds.query;

import org.apache.commons.lang3.StringUtils;

/**
 * 逻辑符合
 * Created by shicy on 2017/10/9.
 */
@SuppressWarnings("unused")
public enum Logic {

    NOT("not"), // 逻辑非

    AND("and"), // 逻辑与

    OR("or"); // 逻辑或

    private String value;

    Logic(String value){
        this.value = value;
    }

    public static Logic get(String value) {
        value = StringUtils.trimToEmpty(value).toLowerCase();
        for (Logic logic: Logic.values()) {
            if (StringUtils.equals(value, logic.toString()))
                return logic;
        }
        return null;
    }

    public String toString(){
        return this.value;
    }

}
