package org.scy.common.ds.query;

import org.apache.commons.lang3.StringUtils;
import org.scy.common.ds.PageInfo;
import org.scy.common.utils.ArrayUtilsEx;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询器
 * Created by shicy on 2017/10/9.
 */
@SuppressWarnings("unused")
public class Selector {

    private PageInfo pageInfo;
    private List<Filter> filters = new ArrayList<Filter>();

    public static Selector build(PageInfo pageInfo) {
        Selector selector = new Selector();
        selector.setPageInfo(pageInfo);
        return selector;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public Selector addFilter(Filter filter) {
        if (filter != null)
            this.filters.add(filter);
        return this;
    }

    public Selector addFilter(String field, Object value) {
        if (StringUtils.isNotBlank(field))
            this.filters.add(new Filter(field, value));
        return this;
    }

    public Selector addFilter(String field, Object value, Oper oper) {
        if (StringUtils.isNotBlank(field))
            this.filters.add(new Filter(field, value, oper));
        return this;
    }

    public Selector addFilterNotNull(String field, Object value) {
        if (value != null) {
            this.addFilter(field, value);
        }
        return this;
    }

    public Selector addFilterNotNull(String field, Object value, Oper oper) {
        if (value != null) {
            this.addFilter(field, value, oper);
        }
        return this;
    }

    public Selector addFilterNotEmpty(String field, Object value) {
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            this.addFilter(field, value);
        }
        return this;
    }

    public Selector addFilterNotEmpty(String field, Object value, Oper oper) {
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            this.addFilter(field, value, oper);
        }
        return this;
    }

    public Selector addFilterNotBlank(String field, Object value) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            this.addFilter(field, value);
        }
        return this;
    }

    public Selector addFilterNotBlank(String field, Object value, Oper oper) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            this.addFilter(field, value, oper);
        }
        return this;
    }

    public String getWhere() {
        List<String> wheres = new ArrayList<String>();

        for (Filter filter: filters) {
            wheres.add(filter.toString());
        }

        if (wheres.size() > 0)
            return ArrayUtilsEx.join(wheres, " and ");
        return "";
    }

    public String getWhereMore() {
        List<String> wheres = new ArrayList<String>();

        for (Filter filter: filters) {
            wheres.add(filter.toString());
        }

        if (wheres.size() > 0)
            return ArrayUtilsEx.join(wheres, " and ");
        return "";
    }

    public String getOrderBy() {
        return "";
    }

    public String getGroupBy() {
        return "";
    }

    public String getOrder() {
        return "";
    }

    public String getOrderMore() {
        return "";
    }

    public String getGroup() {
        return "";
    }

    public String getGroupMore() {
        return "";
    }

    public String getLimit() {
        if (pageInfo != null) {
            return "limit " + pageInfo.getPage() + "," + pageInfo.getSize();
        }
        return "";
    }

}
