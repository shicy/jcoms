package org.scy.common.ds.query;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * 查询过滤条件
 * Created by shicy on 2017/10/11.
 */
@SuppressWarnings({"unused", "unchecked"})
public class Filter {

    private String field;
    private Object value;
    private Oper oper;

    public Filter(String field, Object value) {
        this(field, value, null);
    }

    public Filter(String field, Object value, Oper oper) {
        this.setField(field);
        this.setValue(value);
        this.setOper(oper);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Oper getOper() {
        return oper;
    }

    public void setOper(Oper oper) {
        this.oper = oper;
    }

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(field)) {
            StringBuilder result = new StringBuilder();
            result.append(StringUtils.trimToEmpty(field));

            if (oper == null) {
                if (value == null)
                    oper = Oper.ISN;
                else if (value instanceof Array)
                    oper = Oper.IN;
                else if (value instanceof Collection)
                    oper = Oper.IN;
                else
                    oper = Oper.EQ;
            }
            result.append(" ").append(oper.toString()).append(" ");

            if (oper.equals(Oper.LIKE)) {
                if (value != null)
                    result.append("'%").append(value).append("%'");
                else
                    result.append("''");
            }
            else if (value != null) {
                if (value.getClass().isArray()) {
                    result.append("(").append(getArrayValue(value)).append(")");
                }
                else if (value instanceof Collection) {
                    result.append("(").append(getCollectionValue((Collection)value)).append(")");
                }
                else if (isNumberValue(value)) {
                    result.append(value);
                }
                else {
                    String val = value.toString().replaceAll("'", "''");
                    result.append("'").append(val).append("'");
                }
            }
            return result.toString();
        }
        return "";
    }

    private String getArrayValue(Object values) {
        StringBuilder str = new StringBuilder("");
        for (int i = 0, l = Array.getLength(values); i < l; i++) {
            Object obj = Array.get(values, i);
            if (obj != null) {
                if (str.length() > 0)
                    str.append(",");
                if (isNumberValue(obj))
                    str.append(obj);
                else {
                    String val = obj.toString().replaceAll("'", "''");
                    str.append("'").append(val).append("'");
                }
            }
        }
        return str.toString();
    }

    private String getCollectionValue(Collection values) {
        StringBuilder str = new StringBuilder("");
        for (Object obj: values.toArray()) {
            if (obj != null) {
                if (str.length() > 0)
                    str.append(",");
                if (isNumberValue(obj))
                    str.append(obj);
                else {
                    String val = obj.toString().replaceAll("'", "''");
                    str.append("'").append(val).append("'");
                }
            }
        }
        return str.toString();
    }

    private boolean isNumberValue(Object value) {
        return value instanceof Number;
    }

}
