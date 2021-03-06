package org.scy.common.ds.query;

import org.apache.commons.lang3.StringUtils;
import org.scy.common.ds.PageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询器
 * Created by shicy on 2017/10/9.
 */
public class Selector {

    private PageInfo pageInfo;
    private final List<FilterGroup> filterGroups = new ArrayList<FilterGroup>();
    private final List<Order> orders = new ArrayList<Order>();
    private final List<String> groups = new ArrayList<String>();

    public static Selector build(PageInfo pageInfo) {
        Selector selector = new Selector();
        selector.setPageInfo(pageInfo);
        return selector;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public void addFilter(Filter filter) {
        if (filter != null)
            this.filterGroups.add(new FilterGroup(filter));
    }

    public void addFilter(String field, Object value) {
        if (StringUtils.isNotBlank(field))
            this.filterGroups.add(new FilterGroup(new Filter(field, value)));
    }

    public void addFilter(String field, Object value, Oper oper) {
        if (StringUtils.isNotBlank(field))
            this.filterGroups.add(new FilterGroup(new Filter(field, value, oper)));
    }

    public void addFilterNotNull(String field, Object value) {
        if (value != null) {
            this.addFilter(field, value);
        }
    }

    public void addFilterNotNull(String field, Object value, Oper oper) {
        if (value != null) {
            this.addFilter(field, value, oper);
        }
    }

    public void addFilterNotEmpty(String field, Object value) {
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            this.addFilter(field, value);
        }
    }

    public void addFilterNotEmpty(String field, Object value, Oper oper) {
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            this.addFilter(field, value, oper);
        }
    }

    public void addFilterNotBlank(String field, Object value) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            this.addFilter(field, value);
        }
    }

    public void addFilterNotBlank(String field, Object value, Oper oper) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            this.addFilter(field, value, oper);
        }
    }

    public void addFilter(Filter[] filters, Logic logic) {
        this.filterGroups.add(new FilterGroup(filters, logic));
    }

    public void addFilter(FilterGroup filterGroup) {
        this.filterGroups.add(filterGroup);
    }

    public void addOrder(Order order) {
        if (order != null)
            this.orders.add(order);
    }

    public void addOrder(String field) {
        this.addOrder(field, true);
    }

    public void addOrder(String field, boolean isAsc) {
        if (StringUtils.isNotBlank(field)) {
            this.orders.add(new Order(field, isAsc));
        }
    }

    public void addGroup(String field) {
        if (StringUtils.isNotBlank(field))
            this.groups.add(field);
    }

    public String getWhere() {
        String where = getWhereMore();
        if (StringUtils.isNotBlank(where))
            return "where " + where.substring(5);
        return "";
    }

    public String getWhereMore() {
        StringBuilder builder = new StringBuilder();
        for (FilterGroup filterGroup: filterGroups) {
            String sql = filterGroup != null ? filterGroup.toString() : null;
            if (sql != null && sql.length() > 0)
                builder.append(" and ").append(sql);
        }
        return builder.toString();
    }

    public String getOrder() {
        String order = getOrderMore();
        if (StringUtils.isNotBlank(order))
            return "order by " + order.substring(2);
        return "";
    }

    public String getOrderMore() {
        StringBuilder builder = new StringBuilder();
        for (Order order: orders) {
            builder.append(", ").append(order.toString());
        }
        return builder.toString();
    }

    public String getGroup() {
        String group = getGroupMore();
        if (StringUtils.isNotBlank(group))
            return "group by " + group.substring(2);
        return "";
    }

    public String getGroupMore() {
        StringBuilder builder = new StringBuilder();
        for (String field: groups) {
            builder.append(", ").append(field);
        }
        return builder.toString();
    }

    public String getLimit() {
        if (pageInfo != null) {
            return "limit " + pageInfo.getPageStart() + "," + pageInfo.getSize();
        }
        return "";
    }

}
