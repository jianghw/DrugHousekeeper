package com.cjy.flb.utils;

import com.google.gson.Gson;

/**
 * @author lvzhongyi
 *         <p>
 *         description 类型安全转换类
 *         date 16/3/1
 *         email lvzhongyiforchrome@gmail.com
 *         </p>
 */
public class ConvertUtils {

    public final static int obj2Int(Object obj, int defaultValue) {
        if (obj == null || obj.toString().trim().equals("")) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(obj.toString());
        } catch (Exception e) {
            try {
                return Double.valueOf(obj.toString()).intValue();
            } catch (Exception e1) {
                return defaultValue;
            }
        }
    }

    /**
     * obj转json转实体类
     *
     * @param obj
     * @param c
     * @return
     */
    public final static <T> T obj2Bean(Object obj, Class<T> c) {
        if (obj == null || obj.toString().trim().equals("")) {
            return null;
        }
        return json2Bean(obj.toString().trim(), c);
    }

    /**
     * json转实体类
     *
     * @param json
     * @param c
     * @param <T>
     * @return
     */
    public final static <T> T json2Bean(String json, Class<T> c) {
        if (json == null || json.toString().trim().equals(""))
            return null;
        Gson gson = new Gson();
        try {
            T tBean = gson.fromJson(json.trim(), c);
            return tBean;
        } catch (Exception e) {
            return null;
        }
    }

}
