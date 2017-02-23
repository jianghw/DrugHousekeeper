package com.cjy.flb.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cjy.flb.utils.PinYin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作类
 *
 * @author zihao
 */
public class DatabaseAdapter {

    private static DatabaseManagers manager;
    private static Context mContext;

    /**
     * 获取一个操作类对象
     *
     * @param context
     * @return
     */
    public static DatabaseAdapter getIntance(Context context)
    {
        DatabaseAdapter adapter = new DatabaseAdapter();
        mContext = context;
        manager = DatabaseManagers.getInstance(new DatabaseHelper(mContext));
        return adapter;
    }

    /**
     * 插入信息
     *
     * @param titleArray
     */
    public void inserInfo(List<String> titleArray)
    {
        SQLiteDatabase database = manager.getWritableDatabase();

        try {
            for (String title : titleArray) {
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("pinyin", PinYin.getPinYin(title));// 讲内容转换为拼音
                database.insert(DatabaseHelper.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            manager.closeDatabase();
        }
    }

    /**
     * 查询信息
     *
     * @param pinyin // 字符串转换的拼音
     * @return
     */
    public List<String> queryInfo(String pinyin)
    {
        List<String> resultArray = new ArrayList<String>();
        SQLiteDatabase database = manager.getReadableDatabase();
        //		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH +
        // "/" + DBManager.DB_NAME, null);
        Cursor cursor = null;
        try {
            // 创建模糊查询的条件
            String likeStr = "'";
            for (int i = 0; i < pinyin.length(); i++) {
                if (i < pinyin.length() - 1) {
                    likeStr += pinyin.charAt(i);
                } else {
                    likeStr += pinyin.charAt(i) + "%'";
                }
            }
            cursor = database.rawQuery("select*from "
                    + DatabaseHelper.TABLE_NAME + " where name_py like "
                    + likeStr+ " ORDER BY id DESC ", null);

            if (cursor.getCount() == 0) {
                cursor = database.rawQuery("select*from "
                        + DatabaseHelper.TABLE_NAME + " where name_pinyin like "
                        + likeStr+ " ORDER BY id DESC ", null);
            }

            Map<String, String> map = new HashMap<>();
            while (cursor != null && cursor.moveToNext()) {
                map.put(
                        cursor.getString(cursor.getColumnIndex("product_name_zh"))
                                + "/" + cursor.getString(cursor.getColumnIndex("production_unit")),
                        cursor.getString(cursor.getColumnIndex("id")));
            }

            for (Object object : map.entrySet()) {
                Map.Entry entry = (Map.Entry) object;
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                resultArray.add(key + "/" + val);
            }
            if (cursor != null)
                cursor.close();
            map.clear();
        } catch (Exception e) {
            // TODO: handle exception
            e.toString();
        } finally {
            database.close();
            manager.closeDatabase();
        }
        return resultArray;
    }

    /**
     * 查询信息 添加药物为 药物名/公司名/表id
     *
     * @param pinyin // 字符串转换的拼音
     * @return
     */
    public List<String> queryInfoChinese(String pinyin)
    {
        List<String> resultArray = new ArrayList<>();

        SQLiteDatabase database = manager.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 创建模糊查询的条件
            String likeStr = "'";
            for (int i = 0; i < pinyin.length(); i++) {
                if (i < pinyin.length() - 1) {
                    likeStr += "%" + pinyin.charAt(i);
                } else {
                    likeStr += "%" + pinyin.charAt(i) + "%'";
                }
            }

            //            cursor = database.rawQuery("select distinct product_name_zh from "  唯一查找
            cursor = database.rawQuery("select*from "
                    + DatabaseHelper.TABLE_NAME + " where product_name_zh like "
                    + likeStr + " ORDER BY id DESC ", null);//降序

            Map<String, String> map = new HashMap<>();
            while (cursor != null && cursor.moveToNext()) {
                map.put(
                        cursor.getString(cursor.getColumnIndex("product_name_zh"))
                                + "/" + cursor.getString(cursor.getColumnIndex("production_unit")),
                        cursor.getString(cursor.getColumnIndex("id")));
            }

            for (Object object : map.entrySet()) {
                Map.Entry entry = (Map.Entry) object;
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                resultArray.add(key + "/" + val);
            }

          /*  while (cursor.moveToNext()) {
                resultArray.add(
                        cursor.getString(cursor.getColumnIndex("product_name_zh"))
                                + "/" + cursor.getString(cursor.getColumnIndex("production_unit"))
                                + "/" + cursor.getString(cursor.getColumnIndex("id")));
            }*/
            if (cursor != null)
                cursor.close();
            map.clear();
        } catch (Exception e) {
            // TODO: handle exception
            e.toString();
        } finally {
            database.close();
            manager.closeDatabase();
        }
        return resultArray;
    }

    public synchronized String queryInfoById(String id)
    {
        String string = null;
        SQLiteDatabase database = manager.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 创建模糊查询的条件
            cursor = database.rawQuery("select*from " + DatabaseHelper.TABLE_NAME + " where id like " + id, null);
            while (cursor != null && cursor.moveToNext()) {
                string = cursor.getString(cursor.getColumnIndex("product_name_zh"));
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
            manager.closeDatabase();
        }
        return string;
    }

    public synchronized String queryInfoByIdForProblem(String id)
    {
        String string = null;
        SQLiteDatabase database = manager.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 创建模糊查询的条件
            cursor = database.rawQuery("select*from " + DatabaseHelper.TABLE_NAME + " where id like " + id, null);
            while (cursor != null && cursor.moveToNext()) {
                string = cursor.getString(cursor.getColumnIndex("product_name_zh"))
                        + "/" + cursor.getString(cursor.getColumnIndex("production_unit"))
                        + "/" + id;
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
            manager.closeDatabase();
        }
        return string;
    }

    /**
     * 删除表中的所有数据
     */
    public void deleteAll()
    {
        SQLiteDatabase database = manager.getWritableDatabase();

        try {
            database.delete(DatabaseHelper.TABLE_NAME, null, null);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            manager.closeDatabase();
        }
    }
}