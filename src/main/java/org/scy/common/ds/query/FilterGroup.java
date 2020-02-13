package org.scy.common.ds.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 查询过滤条件
 * Created by shicy on 2020/02/12.
 */
public class FilterGroup {

    Logic logic = null;
    List<Object> filters = new ArrayList<Object>();

    public FilterGroup() {
        // .
    }

    public FilterGroup(Filter filter) {
        this.filters.add(filter);
    }

    public FilterGroup(Filter[] filters, Logic logic) {
        if (filters != null) {
            Collections.addAll(this.filters, filters);
        }
        this.logic = logic;
    }

    public void add(Filter filter) {
        this.filters.add(filter);
    }

    public void add(String field, Object value) {
        this.filters.add(new Filter(field, value));
    }

    public void add(String field, Object value, Oper oper) {
        this.filters.add(new Filter(field, value, oper));
    }

    public void add(FilterGroup filterGroup) {
        this.filters.add(filterGroup);
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (filters.size() == 1) {
            if (filters.get(0) != null)
                result.append(filters.get(0).toString());
        }
        else if (filters.size() > 0) {
            if (this.logic == null)
                this.logic = Logic.AND;

            result.append("(");
            boolean hasAppend = false;
            for (Object filter: filters) {
                String sql = filter != null ? filter.toString() : null;
                if (sql != null && sql.length() > 0) {
                    if (hasAppend)
                        result.append(" ").append(this.logic.toString()).append(" ");
                    result.append(sql);
                    hasAppend = true;
                }
            }
            result.append(")");
        }
        return result.toString();
    }
}
