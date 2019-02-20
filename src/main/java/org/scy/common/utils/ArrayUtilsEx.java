package org.scy.common.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.scy.common.web.model.BaseModel;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 数组工具类，是{@link org.apache.commons.lang3.ArrayUtils}的扩展
 * Created by shicy on 2017/5/9.
 */
@SuppressWarnings("unused")
public abstract class ArrayUtilsEx {

    /**
     * 查找列表中的某一元素，通过ID进行比较
     */
    public static <T extends BaseModel> T findObject(List<T> list, int id) {
        if (list == null || list.size() == 0)
            return null;

        for (T model: list) {
            if (model.getId() == id) {
                return model;
            }
        }

        return null;
    }

    /**
     * 查找列表中的一个对象 add by shicy 2012-3-11
     */
    public static <T> T findObject(List<T> list, String name, Object value) {
        if (list == null || list.size() == 0)
            return null;

        for (T obj: list) {
            if (obj == null)
                continue;
            try {
                Object val = PropertyUtils.getSimpleProperty(obj, name);
                if (value == null && val == null)
                    return obj;
                if (value != null && value.equals(val))
                    return obj;
            }
            catch (Exception e) {
                //
            }
        }

        return null;
    }

    /**
     * 获取对象编号集
     */
    public static int[] getObjectIds(List<? extends BaseModel> list) {
        if (list == null || list.size() == 0)
            return ArrayUtils.EMPTY_INT_ARRAY;

        int[] ids = new int[list.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = list.get(i).getId();
        }

        return ids;
    }

    public static String join(int[] array, String sep) {
        StringBuilder result = new StringBuilder();
        if (array != null) {
            if (sep == null)
                sep = "";
            for (int i = 0; i < array.length; i++) {
                if (i > 0)
                    result.append(sep);
                result.append(array[i]);
            }
        }
        return result.toString();
    }

    public static String join(Object[] array, String sep) {
        StringBuilder result = new StringBuilder();
        if (array != null) {
            if (sep == null)
                sep = "";
            for (Object obj: array) {
                String value = obj != null ? obj.toString() : null;
                if (value != null && value.length() > 0) {
                    if (result.length() > 0)
                        result.append(sep);
                    result.append(value);
                }
            }
        }
        return result.toString();
    }

    public static String join(Collection collection, String sep) {
        if (collection != null)
            return join(collection.toArray(), sep);
        return "";
    }

    /**
     * 输出列表
     */
    public static void print(List list) {
        if (list != null && list.size() > 0) {
            for (Object obj: list) {
                System.out.println(obj.toString());
            }
        }
    }

    /**
     * 查找并返回arr1不在arr2中的元素，即返回arr1中有而arr2中没有的元素
     */
    public static int[] removeElements(int[] arr1, int[] arr2) {
        if (arr1 == null || arr1.length == 0)
            return new int[0];
        if (arr2 == null || arr2.length == 0)
            return ArrayUtils.subarray(arr1, 0, arr1.length-1);

        StringBuilder strVals = new StringBuilder();
        for (int val1: arr1) {
            if (ArrayUtils.indexOf(arr2, val1) < 0)
                strVals.append(",").append(val1);
        }
        return transStrToInt(StringUtils.split(strVals.toString(), ","));
    }

    /**
     * 移除空项
     */
    public static <T> T[] removeNull(T[] array) {
        if (array == null || array.length == 0)
            return array;

        List<T> list = new ArrayList<T>();
        for (T obj: array) {
            if (obj != null)
                list.add(obj);
        }

        return list.toArray(array);
    }

    /**
     * 排序字典顺序排序字符串
     * @param array 需要排序的字符串数组
     * @param desc 是否是降序排序
     * @return 排序后的数组
     */
    public static String[] sort(String[] array, final boolean desc) {
        List<String> list = toList(array);
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {
                if (o1 == null && o2 == null)
                    return 0;
                if (o1 == null)
                    return desc ? -1 : 1;
                if (o2 == null)
                    return desc ? 1 : -1;
                o1 = StringUtilsEx.toHanYuPinYin(o1, true);
                o2 = StringUtilsEx.toHanYuPinYin(o2, true);
                int v = o1.compareTo(o2);
                return v * (desc ? -1 : 1);
            }
        });
        return list.toArray(new String[0]);
    }

    /**
     * 根据输入的编号顺序，排序列表
     * @param list 对象列表
     * @param ids 编号
     */
    public static void sortBeansById(List<? extends BaseModel> list, final int[] ids) {
        Collections.sort(list, new Comparator<BaseModel>() {
            public int compare(BaseModel o1, BaseModel o2) {
                if (o1 == null && o2 == null)
                    return 0;
                if (o1 == null)
                    return -1;
                if (o2 == null)
                    return 1;
                int p1 = ArrayUtils.indexOf(ids, o1.getId());
                int p2 = ArrayUtils.indexOf(ids, o2.getId());
                return p1 - p2;
            }
        });
    }

    /**
     * 将对象数组封装成一个迭代器
     * @param <T> 使用泛型
     * @param objs 一个对象数组
     */
    public static <T> Iterator<T> toIterator(T[] objs) {
        return new ArrayIterator<T>(objs);
    }

    /**
     * 将数据转化成列表集合
     */
    public static <T> List<T> toList(T[] objs) {
        List<T> list = new ArrayList<T>();
        if (objs != null) {
            for (T obj: objs) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * 对象转换成整数型数组 add by shicy 2011-4-13
     * @param arrayObj 数据或集合
     */
    public static int[] toPrimitiveInt(Object arrayObj) {
        Integer[] ret = toObjectInt(arrayObj);
        return ArrayUtils.toPrimitive(ret);
    }

    /**
     * 数组对象转换成整型数组 add by shicy 2011-4-13
     * @param arrayObj 数组对象，也可以是集合对象
     */
    public static Integer[] toObjectInt(Object arrayObj) {
        if (arrayObj == null)
            return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
        List<Integer> ret = new ArrayList<Integer>();
        if (arrayObj.getClass().isArray()) {
            for (int i = 0, l = Array.getLength(arrayObj); i < l; i++) {
                ret.add(getIntValue(Array.get(arrayObj, i), 0));
            }
        }
        else if (arrayObj instanceof Collection) {
            Iterator<?> iter = ((Collection<?>)arrayObj).iterator();
            while (iter.hasNext()) {
                ret.add(getIntValue(iter.next(), 0));
            }
        }
        return ret.toArray(new Integer[0]);
    }

    /**
     * 试图获取对象的整数值
     */
    private static int getIntValue(Object obj, int defaultValue) {
        try {
            return Integer.parseInt("" + obj);
        }
        catch (Exception e) {
            //
        };
        return defaultValue;
    }

    /**
     * 试图将一个对象数组转换成short[]，忽略转换失败的对象
     */
    public static short[] toPrimitiveShort(Object[] objs) {
        if (objs == null || objs.length == 0)
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        short[] ret = new short[objs.length];
        int len = 0;
        for (Object obj: objs) {
            try {
                ret[len++] = Short.parseShort("" + obj);
            }
            catch (Exception e) {
                //
            }
        }
        return len == ret.length ? ret : ArrayUtils.subarray(ret, 0, len);
    }

    /**
     * 试图将一个对象数组转换成Short[]，忽略转换失败的对象
     */
    public static Short[] toObjectShort(Object[] objs) {
        if (objs == null || objs.length == 0)
            return ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY;
        Short[] ret = new Short[objs.length];
        int len = 0;
        for (Object obj: objs) {
            try {
                ret[len++] = Short.valueOf("" + obj);
            }
            catch (Exception e) {
                //
            }
        }
        return len == ret.length ? ret : (Short[])ArrayUtils.subarray(ret, 0, len);
    }

    /**
     * 试图将一个对象数组转换成float[]，忽略转换失败的对象
     */
    public static float[] toPrimitiveFloat(Object[] objs) {
        if (objs == null || objs.length == 0)
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        float[] ret = new float[objs.length];
        int len = 0;
        for (Object obj: objs) {
            try {
                ret[len++] = Float.parseFloat("" + obj);
            }
            catch (Exception e) {
                //
            }
        }
        return len == ret.length ? ret : ArrayUtils.subarray(ret, 0, len);
    }

    /**
     * 试图将一个对象数组转换成Float[]，忽略转换失败的对象
     */
    public static Float[] toObjectFloat(Object[] objs) {
        if (objs == null || objs.length == 0)
            return ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY;
        Float[] ret = new Float[objs.length];
        int len = 0;
        for (Object obj: objs) {
            try {
                ret[len++] = Float.valueOf("" + obj);
            }
            catch (Exception e) {
                //
            }
        }
        return len == ret.length ? ret :(Float[])ArrayUtils.subarray(ret, 0, len);
    }

    /**
     * 试图将一个对象数组转换成double[]，忽略转换失败的对象
     */
    public static double[] toPrimitiveDouble(Object[] objs) {
        if (objs == null || objs.length == 0)
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        double[] ret = new double[objs.length];
        int len = 0;
        for (Object obj: objs) {
            try {
                ret[len++] = Double.parseDouble("" + obj);
            }
            catch (Exception e) {
                //
            }
        }
        return len == ret.length ? ret : ArrayUtils.subarray(ret, 0, len);
    }

    /**
     * 试图将一个对象数组转换成Double[]，忽略转换失败的对象
     */
    public static Double[] toObjectDouble(Object[] objs) {
        if (objs == null || objs.length == 0)
            return ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY;
        Double[] ret = new Double[objs.length];
        int len = 0;
        for (Object obj: objs) {
            try {
                ret[len++] = Double.valueOf("" + obj);
            }
            catch (Exception e) {
                //
            }
        }
        return len == ret.length ? ret : (Double[])ArrayUtils.subarray(ret, 0, len);
    }

    /**
     * 试图将一个对象数组转换成long[]，忽略转换失败的对象
     */
    public static long[] toPrimitiveLong(Object[] objs) {
        if (objs == null || objs.length == 0)
            return ArrayUtils.EMPTY_LONG_ARRAY;
        long[] ret = new long[objs.length];
        int len = 0;
        for (Object obj: objs) {
            try {
                ret[len++] = Long.parseLong("" + obj);
            }
            catch (Exception e) {
                //
            }
        }
        return len == ret.length ? ret : ArrayUtils.subarray(ret, 0, len);
    }

    /**
     * 试图将一个对象数组转换成Long[]，忽略转换失败的对象
     */
    public static Long[] toObjectLong(Object[] objs) {
        if (objs == null || objs.length == 0)
            return ArrayUtils.EMPTY_LONG_OBJECT_ARRAY;
        Long[] ret = new Long[objs.length];
        int len = 0;
        for (Object obj: objs) {
            try {
                ret[len++] = Long.valueOf("" + obj);
            }
            catch (Exception e) {
                //
            }
        }
        return len == ret.length ? ret : (Long[])ArrayUtils.subarray(ret, 0, len);
    }

    /**
     * 数字类型转换
     */
    public static Object transtion(Object[] objs, Class<?> cls) {
        if (objs == null || cls == null)
            return null;
        Object obj = null;
        if (cls == int.class)
            obj = toPrimitiveInt(objs);
        else if (cls == short.class)
            obj = toPrimitiveShort(objs);
        else if (cls == double.class)
            obj = toPrimitiveDouble(objs);
        else if (cls == float.class)
            obj = toPrimitiveFloat(objs);
        else if (cls == Integer.class)
            obj = toObjectInt(objs);
        else if (cls == Short.class)
            obj = toObjectShort(objs);
        else if (cls == Double.class)
            obj = toObjectDouble(objs);
        else if (cls == Float.class)
            obj = toObjectFloat(objs);
        else
            return null;
        return obj;
    }

    /**
     * 将一个字符串数组转换成整型数组
     */
    public static int[] transStrToInt(String[] args) {
        if (args == null || args.length == 0)
            return ArrayUtils.EMPTY_INT_ARRAY;
        int[] ret = new int[args.length];
        for (int i = 0; i < args.length; i++)
            ret[i] = Integer.parseInt(args[i]);
        return ret;
    }

    /**
     * 将一个字符串数组转换成长整型数组
     */
    public static long[] transStrToLong(String[] args) {
        if (args == null || args.length == 0)
            return ArrayUtils.EMPTY_LONG_ARRAY;
        long[] ret = new long[args.length];
        for (int i = 0; i < args.length; i++)
            ret[i] = Long.parseLong(args[i]);
        return ret;
    }

    /**
     * 将一个字符串数组转换成双精度数组
     */
    public static double[] transStrToDouble(String[] args) {
        if (args == null || args.length == 0)
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        double[] ret = new double[args.length];
        for (int i = 0; i < args.length; i++)
            ret[i] = Double.parseDouble(args[i]);
        return ret;
    }

    /**
     * 数组迭代器
     */
    private static class ArrayIterator<T> implements Iterator<T> {

        private T[] objs;
        private int index;
        private int length;

        private ArrayIterator(T[] objs) {
            this.objs = objs;
            this.index = 0;
            this.length = objs.length;
        }

        public boolean hasNext() {
            return this.index < this.length;
        }

        public T next() {
            return this.objs[this.index++];
        }

        public void remove() {
            throw new RuntimeException("方法未实现! ");
        }

    }

}
