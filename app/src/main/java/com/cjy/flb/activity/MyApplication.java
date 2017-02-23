package com.cjy.flb.activity;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.cjy.flb.bean.BindingID;
import com.cjy.flb.bean.GetBoxUserInfo;
import com.cjy.flb.dao.BoxDao;
import com.cjy.flb.dao.DaoMaster;
import com.cjy.flb.dao.DaoSession;
import com.cjy.flb.db.Box;
import com.cjy.flb.manager.DBManager;
import com.cjy.flb.utils.CrashHandler;
import com.cjy.flb.utils.DataCleanManager;
import com.cjy.flb.utils.SharedPreUtil;
import com.lidroid.xutils.HttpUtils;
import com.socks.library.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    private static HttpUtils mHttp;
    public static String accessToken;

    private static final boolean isDebug = false;

    public static Vector<BindingID> list = new Vector<>();
    public static ConcurrentHashMap<String, GetBoxUserInfo> hashMap = new ConcurrentHashMap<>();
    public static byte[] head = null;
    public static String Email = null;

    /**
     * 数据库操作
     */
    private static final String DEFAULT_DATABASE_NAME = "ygj.db";
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    //当前的福乐宝ID
    public static String flbId = "10000001";
    public static String DEVICE_USER_NAME = "---";
    public static String DEVICE_USER_PHONE = "";
    public static File imgHead;//头像

    //获取到主线程的上下文
    private static MyApplication sInstance;
    //获取到主线程的handler
    private static Handler mMainThreadHandler = null;
    //获取到主线程的looper
    private static Looper mMainThreadLooper = null;
    //获取到主线程
    private static Thread mMainThead = null;
    //获取到主线程的id
    private static int mMainTheadId;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sInstance = this;

        mMainThreadHandler = new Handler();
        mMainThreadLooper = getMainLooper();
        mMainThead = Thread.currentThread();
        mMainTheadId = android.os.Process.myTid();//主線程id
        //android.os.Process.myUid()获取到用户id
        //android.os.Process.myPid();//获取到进程id
        //android.os.Process.myTid()获取到调用线程的id

        if (isDebug) {
            initLogger();
        } else {
            //关闭本地debug本地调试模式，用于测试人员反馈日志
            CrashHandler crashHandler = CrashHandler.getInstance();
            //注册crashHandler
            crashHandler.init(getApplicationContext());
            //发送以前没发送的报告(可选)
            crashHandler.sendPreviousReportsToServer(false);
        }

        //初始化数据库
        initTwoDatabase();
        // 设置开启日志,发布时请关闭日志
        JPushInterface.setDebugMode(isDebug);
        // 初始化 JPush
        JPushInterface.init(this);
    }

    private void initTwoDatabase() {
        (new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {//注意版本更新时，要变更！！！
                    String vString = SharedPreUtil.getString("ygj_code", "1.3.6");
                    if (vString.equals("1.3.6")) {
                        DataCleanManager.cleanDatabaseByName(sInstance, DEFAULT_DATABASE_NAME);
                        DataCleanManager.cleanDatabaseByName(sInstance, DBManager.DB_NAME);
                        SharedPreUtil.setString("ygj_code", "ygj_code_1.3.6");
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean flag) {
                new Thread() {
                    @Override
                    public void run() {
                        //首次执行导入.db文件
                        DBManager dbHelper = new DBManager(MyApplication.this);
                        dbHelper.openDatabase();
                    }
                }.start();
                //初始化数据库
                try {
                    initTableBoxDao();
                } catch (Exception e) {
                    e.printStackTrace();
                    DataCleanManager.cleanDatabaseByName(sInstance, DEFAULT_DATABASE_NAME);
                }
            }
        }).execute();
    }

    private void initTableBoxDao() {
        long count = MyApplication.getDaoSession().getBoxDao().queryBuilder()
                .where(BoxDao.Properties.Point_in_time.eq("NIGHT")).buildCount().count();
        if (count != 7) {
            ArrayList<Box> list = addTableBoxData();
            MyApplication.getDaoSession().getBoxDao().insertInTx(list);
        }
    }

    /**
     * 初始化box table数据
     *
     * @return
     */
    private ArrayList<Box> addTableBoxData() {
        ArrayList<Box> list = new ArrayList<>();
        int count = -1;
        for (int i = 1; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                String str = null;
                switch (j) {
                    case 0:
                        str = "MORN";
                        break;
                    case 1:
                        str = "NOON";
                        break;
                    case 2:
                        str = "AFTERNOON";
                        break;
                    case 3:
                        str = "NIGHT";
                        break;
                }
                count += 1;
                list.add(new Box(null, String.valueOf(i), str, count));
            }
        }
        return list;
    }

    /**
     * 配置默认HTTP缓存过期时长（同时更新当前缓存过期时间）
     * 多次请求注意框架中的缓存！！！
     *
     * @return
     */
    public static HttpUtils getHttpUtils() {
        if (mHttp == null) {
            mHttp = new HttpUtils();
            mHttp.configRequestThreadPoolSize(1);
            mHttp.configDefaultHttpCacheExpiry(0);
        }
        return mHttp;
    }


    /**
     * 取得DaoSession
     *
     * @return
     */
    public static DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    protected static DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getInstance(),
                    DEFAULT_DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }


    /**
     * 初始化配置kLog
     * KLog.d(Tag,String)
     * KLog.json(Tag,String)
     * KLog.file(TAG, Environment.getExternalStorageDirectory(), "test.txt", JSON_LONG);
     * KLog.xml(XML);
     */
    private void initLogger() {
        KLog.init(isDebug);
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static Looper getMainThreadLooper() {
        return mMainThreadLooper;
    }

    public static Thread getMainThread() {
        return mMainThead;
    }

    public static int getMainThreadId() {
        return mMainTheadId;
    }

}
