package com.cjy.flb.manager;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cjy.flb.dao.DaoMaster;
import com.cjy.flb.dao.DaoSession;
import com.cjy.flb.db.Box;

import com.cjy.flb.enums.Medicine;
import com.cjy.flb.utils.ListUtils;
import com.cjy.flb.utils.StringUtils;


import java.util.Collection;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author jianghw
 */
public abstract class DatabaseManager<M, K> implements IDatabase<M, K> {

    private static final String DEFAULT_DATABASE_NAME = "ygj.db";

    /**
     * The Android Activity reference for access to DatabaseManager.
     * Android为访问DatabaseManager活动参考
     */
    protected static DaoMaster.DevOpenHelper mHelper;
    protected static DaoSession daoSession;
    protected Context context;
    protected String dbName;

    /**
     * 删除数据库时获取实例的方法
     *
     * @param context
     * @return
     */
    public static DatabaseManager getInstance(@NonNull Context context) {
        return new DatabaseManager(context) {
            @Override
            public AbstractDao getAbstractDao() {
                return null;
            }
        };
    }

    /**
     * create new DataBase
     */
    public DatabaseManager(@NonNull Context context) {
        this.context = context;
        this.dbName = DEFAULT_DATABASE_NAME;
        getOpenHelper(context, dbName);
    }

    /**
     * create new DataBase
     */
    public DatabaseManager(@NonNull Context context, @NonNull String dataBaseName) {
        this.context = context;
        this.dbName = dataBaseName;
        getOpenHelper(context, dataBaseName);
    }

    /**
     * 初始化DatabaseHelper
     */
    protected DaoMaster.DevOpenHelper getOpenHelper(@NonNull Context context, @Nullable String dataBaseName) {
        if (mHelper == null) {
            mHelper = new DaoMaster.DevOpenHelper(context, dataBaseName, null);
        }
        return mHelper;
    }

    /**
     * 查询可读数据库
     */
    protected void openReadableDb() throws SQLiteException {
        getDaoMaster(getReadableDatabase());
        getDaoSession();
    }

    /**
     * Query for writable DB
     */
    protected void openWritableDb() throws SQLiteException {
        getDaoMaster(getWritableDatabase());
        getDaoSession();
    }

    /**
     * @return
     */
    protected SQLiteDatabase getWritableDatabase() {
        return getOpenHelper(context, dbName).getWritableDatabase();
    }

    /**
     * @return
     */
    protected SQLiteDatabase getReadableDatabase() {
        return getOpenHelper(context, dbName).getReadableDatabase();
    }

    /**
     * 初始化DaoMaster
     */
    private DaoMaster getDaoMaster(SQLiteDatabase database) {
        return new DaoMaster(database);
    }

    /**
     * 初始化DaoSession
     */
    private DaoSession getDaoSession() {
        if (daoSession == null) {
            daoSession = getDaoMaster(getWritableDatabase()).newSession();
        }
        return daoSession;
    }


    /**
     * 只关闭helper就好,看源码就知道helper关闭的时候会关闭数据库
     */
    @Override
    public void closeDbConnections() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    @Override
    public void clearDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    @Override
    public boolean dropDatabase() {
        try {
            openWritableDb();
            //            DaoMaster.dropAllTables(database, true); // drops all tables
            //            mHelper.onCreate(database);              // creates the tables
            daoSession.deleteAll(Box.class);    // clear all elements from a table
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean insert(@NonNull M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().insert(m);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean insertOrReplace(@NonNull M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().insertOrReplace(m);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(@NonNull M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().delete(m);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteByKey(K key) {
        try {
            if (StringUtils.isEmpty(key.toString()))
                return false;
            openWritableDb();
            getAbstractDao().deleteByKey(key);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteByKeyInTx(K... key) {
        try {
            openWritableDb();
            getAbstractDao().deleteByKeyInTx(key);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteList(List<M> mList) {
        try {
            if (ListUtils.isEmpty(mList))
                return false;
            openWritableDb();
            getAbstractDao().deleteInTx(mList);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAll() {
        try {
            openWritableDb();
            getAbstractDao().deleteAll();
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public boolean update(@NonNull M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().update(m);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateInTx(M... m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().updateInTx(m);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateList(List<M> mList) {
        try {
            if (ListUtils.isEmpty(mList))
                return false;
            openWritableDb();
            getAbstractDao().updateInTx(mList);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public M selectByPrimaryKey(@NonNull K key) {
        try {
            openReadableDb();
            return getAbstractDao().load(key);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<M> loadAll() {
        List<M> mList = null;
        try {
            openReadableDb();
            mList = getAbstractDao().loadAll();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return mList;
    }

    @Override
    public boolean refresh(@NonNull M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().refresh(m);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void runInTx(Runnable runnable) {
        try {
            openReadableDb();
            getDaoSession().runInTx(runnable);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean insertList(@NonNull List<M> list) {
        try {
            if (ListUtils.isEmpty(list))
                return false;
            openWritableDb();
            getAbstractDao().insertInTx(list);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param list
     * @return
     */
    @Override
    public boolean insertOrReplaceList(@NonNull List<M> list) {
        try {
            if (ListUtils.isEmpty(list))
                return false;
            openWritableDb();
            getAbstractDao().insertOrReplaceInTx(list);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @return  QueryBuilder<k> qb
     */
    @Override
    public QueryBuilder<M> getQueryBuilder() {
        openReadableDb();
        return getAbstractDao().queryBuilder();
    }

    /**
     * @param where
     * @param selectionArg
     * @return
     */
    @Override
    public List<M> queryRaw(String where, String... selectionArg) {
        openReadableDb();
        return getAbstractDao().queryRaw(where, selectionArg);
    }

    @Override
    public Query<M> queryRawCreate(String where, Object... selectionArg) {
        openReadableDb();
        return getAbstractDao().queryRawCreate(where, selectionArg);
    }

    @Override
    public Query<M> queryRawCreateListArgs(String where, Collection<Object> selectionArg) {
        openReadableDb();
        return getAbstractDao().queryRawCreateListArgs(where, selectionArg);
    }


    //    @Override
    //    public synchronized void bulkInsertPhoneNumbers(Set<DBPhoneNumber> phoneNumbers) {
    //        try {
    //            if (phoneNumbers != null && phoneNumbers.size() > 0) {
    //                openWritableDb();
    //                asyncSession.insertOrReplaceInTx(DBPhoneNumber.class, phoneNumbers);
    //                assertWaitForCompletion1Sec();
    //                daoSession.clear();
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
}
