package org.scy.common.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * Created by hykj on 2017/8/15.
 */
public class MapUtilsEx {

    private static Logger logger = LoggerFactory.getLogger(MapUtilsEx.class.getName());

    /**
     * 获取Map中的一个编号集，如果属性不存在，则返回一个0长度数据
     * add by shicy 2011-12-16
     * @param map
     * @param paramName
     * @return
     */
    public static int[] getMapIds(Map<String, Object> map, String paramName) {
        Object value = map.get(paramName);
        if (value == null)
            return ArrayUtils.EMPTY_INT_ARRAY;

        int[] ids = ArrayUtilsEx.toPrimitiveInt(value);

        if (ids == null)
            return ArrayUtils.EMPTY_INT_ARRAY;

        return ids;
    }

    /**
     * 获取参数中对应的编号集
     * @param paramId 单一编号属性，优先获取
     * @param paramIds 多值编号属性
     * @return
     */
    public static int[] getMapIntValues(Map<String, Object> map, String paramId, String paramIds) {
        int id = MapUtils.getIntValue(map, paramId, 0);
        if (id != 0)
            return new int[]{id};
        return getMapIds(map, paramIds);
    }

    /**
     * 获取Map中的一个字符串数组
     * @param map
     * @param paramName
     * @return
     */
    public static String[] getMapStrings(Map<String, Object> map, String paramName) {
        Object[] value = (Object[])map.get(paramName);
        if (value == null)
            return new String[]{};
        String[] values = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            values[i] = "" + value[i];
        }
        return values;
    }

    /**
     * 将Map转化为实体对象
     * @param maps
     * @param entityCls
     * @return
     */
    public static <T> List<T> parseFromMap(List<Map<String, Object>> maps, Class<T> entityCls) {
        if (entityCls == null)
            throw new RuntimeException("试解析前端对象时出错：实例类不能为null");

        List<T> result = new ArrayList<T>();
        if (maps == null || maps.size() == 0)
            return result;

        for (int i = 0; i < maps.size(); i++) {
            result.add(parseFromMap(maps.get(i), entityCls));
        }

        return result;
    }

    /**
     * 试从Map中实例化一个对象
     * @param <T> 实例数据类型
     * @param map 数据映射集，从Flex客户端返回的对象以Map的形式存在
     * @param entityCls 实例类
     * @return
     */
    public static <T> T parseFromMap(Map<String, Object> map, Class<T> entityCls) {
        T entity = null;

        try {
            entity = entityCls.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("试解析前端对象时出错：实例化失败! " + e.getMessage(), e);
        }

        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Object val = map.get(key);
            if (key != null && val != null)
                setEntityValue(entity, key, val);
        }

        return entity;
    }

    /**
     * 批量对象实例化
     * @param maps 是待转换的前端对象数组，如果是List可以先用list.toArray()转换
     * @return
     */
    public static <T> List<T> parseFromMap(Map<String, Object>[] maps, Class<T> entityCls) {
        if (entityCls == null)
            throw new RuntimeException("试解析前端对象时出错：实例类不能为null");

        List<T> result = new ArrayList<T>();
        if (maps == null || maps.length == 0)
            return result;

        for (int i = 0; i < maps.length; i++) {
            result.add(parseFromMap(maps[i], entityCls));
        }

        return result;
    }

    /**
     * 给实例对象的属性注入相应的值，支持嵌套属性注入，即可注入子对象
     * @param entity 目标实例对象
     * @param key 注入的对象属性名
     * @param val 待注入的值
     */
    @SuppressWarnings("unchecked")
    private static void setEntityValue(Object entity, String key, Object val) {
        try {
            Class<?> type = PropertyUtils.getPropertyType(entity, key);
            if (type == null)
                return ;
            if (type.isArray()) // 数组
                setEntityArrayValue(entity, key, (Object[])val, type);
            else if (Collection.class.isAssignableFrom(type)) // 集合类
                setEntityCollectionValue(entity, key, (Object[])val, type);
            else if (Map.class.isAssignableFrom(type)) // 映射表类
                setEntityMapValue(entity, key, (Map<String, Object>)val, type);
            else if (val instanceof Map) // 保存一个对象，是一个ASObject对象
                setEntityObjectValue(entity, key, (Map<String, Object>)val, type);
            else // 保存简单类型值
                setEntitySimpleValue(entity, key, val, type);
        }
        catch (Exception e) {
            //e.printStackTrace();
            logger.debug("试解析前端对象 " + entity.getClass().getName() + " 的属性 " + key +
                    " 时出错，" + e.getMessage());
        }
    }

    /**
     * 注入一个简单型对象
     */
    private static void setEntitySimpleValue(Object entity, String key, Object val, Class<?> type) throws Exception {
        if (String.class == type)
            PropertyUtils.setProperty(entity, key, String.valueOf(val));
        else if (int.class == type || Integer.class == type)
            PropertyUtils.setProperty(entity, key, Integer.parseInt("" + val));
        else if (short.class == type || Short.class == type)
            PropertyUtils.setProperty(entity, key, Short.parseShort("" + val));
        else if (Date.class.isAssignableFrom(type)) {
            if (val instanceof Date)
                PropertyUtils.setProperty(entity, key, val);
            else
                PropertyUtils.setProperty(entity, key, DateUtilsEx.tryParseDate("" + val));
        }
        else if (double.class == type || Double.class == type)
            PropertyUtils.setProperty(entity, key, Double.parseDouble("" + val));
        else if (long.class == type || Long.class == type)
            PropertyUtils.setProperty(entity, key, Long.parseLong("" + val));
        else if (float.class == type || Float.class == type)
            PropertyUtils.setProperty(entity, key, Float.parseFloat("" + val));
        else
            PropertyUtils.setProperty(entity, key, val);
    }

    /**
     * 注入一个数组
     */
    private static void setEntityArrayValue(Object entity, String key, Object[] val, Class<?> type) throws Exception {
        if (type == int[].class)
            PropertyUtils.setProperty(entity, key, ArrayUtilsEx.toPrimitiveInt(val));
        else if (type == short[].class)
            PropertyUtils.setProperty(entity, key, ArrayUtilsEx.toPrimitiveShort(val));
        else if (type == double[].class)
            PropertyUtils.setProperty(entity, key, ArrayUtilsEx.toPrimitiveDouble(val));
        else if (type == float[].class)
            PropertyUtils.setProperty(entity, key, ArrayUtilsEx.toPrimitiveFloat(val));
        else if (type == Integer[].class)
            PropertyUtils.setProperty(entity, key, ArrayUtilsEx.toObjectInt(val));
        else if (type == Short[].class)
            PropertyUtils.setProperty(entity, key, ArrayUtilsEx.toObjectShort(val));
        else if (type == Double[].class)
            PropertyUtils.setProperty(entity, key, ArrayUtilsEx.toObjectDouble(val));
        else if (type == Float[].class)
            PropertyUtils.setProperty(entity, key, ArrayUtilsEx.toObjectFloat(val));
        else
            PropertyUtils.setProperty(entity, key, val);
    }

    /**
     * 注入一个集合类
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void setEntityCollectionValue(Object entity, String key, Object[] vals, Class<?> type)throws Exception {
        List list = null;
        if (type == List.class)
            list = new ArrayList();
        else
            list = (List)type.newInstance();

        for (int i = 0; i < vals.length; i++) {
            list.add(vals[i]);
        }

        PropertyUtils.setProperty(entity, key, list);
    }

    /**
     * 注入一个映射表
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void setEntityMapValue(Object entity, String key, Map<String, Object> val, Class<?> type)throws Exception {
        Map map = null;
        if (type == Map.class)
            map = new HashMap();
        else
            map = (Map)type.newInstance();

        Iterator<String> keys = val.keySet().iterator();
        while (keys.hasNext()) {
            String key1 = keys.next();
            map.put(key1, val.get(key1));
        }

        PropertyUtils.setProperty(entity, key, map);
    }

    /**
     * 注入一个复杂对象
     */
    private static void setEntityObjectValue(Object entity, String key, Map<String, Object> val, Class<?> type)throws Exception {
        if (type.isInterface()) // 如果是接口没办法实例化了
            PropertyUtils.setProperty(entity, key, val);
        else {
            Object obj = parseFromMap(val, type);
            PropertyUtils.setProperty(entity, key, obj);
        }
    }

    /**
     * 将一个对象数组级的查询结果转换成一个Map对象
     * @param results 查询结果集
     * @param paramNames 转换成Map的属性名，按结果集顺序指定名称
     * @param indexs 对象索引
     * @return
     */
    public static Map<String, Object> transToMap(Object[] results, String[] paramNames, int[] indexs) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (results != null && results.length > 0) {
            for (int i = 0; i < paramNames.length; i++) {
                String paramName = paramNames[i];
                if (StringUtils.isNotBlank(paramName)) {
                    int index = indexs == null ? i : indexs[i];
                    map.put(paramName, results[index]);
                }
            }
        }
        return map;
    }

    /**
     * 将一组对象数组级的查询结果转换成一个Map形列表对象
     * @param results 查询结果集
     * @param paramNames 转换成Map的属性名，按结果集顺序指定名称
     * @param indexs 对象索引
     * @return
     */
    public static List<Map<String, Object>> transToMap(List<Object[]> resultList, String[] paramNames, int[] indexs) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (resultList != null && resultList.size() > 0) {
            for (Object[] results: resultList) {
                list.add(transToMap(results, paramNames, indexs));
            }
        }
        return list;
    }

    /**
     * @see #transToMap(Object[], String[], int[])
     */
    public static List<Map<String, Object>> transToMap(List<Object[]> resultList, String[] paramNames) {
        return transToMap(resultList, paramNames, null);
    }

}
