package org.scy.common.ds.query;

import org.apache.commons.lang3.StringUtils;

/**
 * 排序
 * Created by shicy 2020/01/27
 */
public class Order {

    private String field;
    private boolean asc;

    public Order(String field, boolean asc) {
        this.field = field;
        this.asc = asc;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(field))
            return field + " " + (asc ? "ASC" : "DESC");
        return "";
    }
}
