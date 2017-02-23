package com.cjy.flb.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.cjy.flb.manager.DBManager;

/**
 * 数据库助手类
 *
 * @author zihao
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;// 版本号
    // private static final String DB_NAME = "ZiHao.db";// 数据库名称
    public static final String DB_NAME = DBManager.DB_PATH + "/" + DBManager.DB_NAME;

    public static final String TABLE_NAME = "mediclists";// 表名

    /**
     * DatabaseHelper构造方法
     *
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public DatabaseHelper(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        createAutoTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    /**
     * 创建数据库表
     *
     * @param db
     */
    private void createAutoTable(SQLiteDatabase db) {
        //		db.execSQL("create table  if not exists "
        //				+ TABLE_NAME
        //				+ "(id INTEGER,MediName nvarchar(255),Dosage nvarchar(255),Spedifications nvarchar(255)," +
        //				"Packaging nvarchar(255),Matetria nvarchar(255),Enterprise nvarchar(255),Price nvarchar(255)," +
        //				"Commonly nvarchar(255),Category nvarchar(255),Care nvarchar(255))");
    }

}