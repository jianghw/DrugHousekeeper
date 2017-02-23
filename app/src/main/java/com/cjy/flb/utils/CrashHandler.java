package com.cjy.flb.utils;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.manager.AppManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Administrator on 2016/3/14 0014.
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录 发送错误报告.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //用于单例
    private static CrashHandler instance;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //使用Properties来保存设备的信息和错误堆栈信息
    //    private Properties mDeviceCrashInfo = new Properties();
    //用来存储设备信息和异常信息
    private Map<String, String> mDeviceCrashInfo = new HashMap<>();

    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
    //错误报告文件的扩展名
    private static final String CRASH_REPORTER_EXTENSION = ".log";


    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    /**
     * 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param context 初始化,注册Context对象
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) return true;
        final String meg = ex.getLocalizedMessage();
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出现错误,我们将及时修正它~" + meg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备信息
        collectCrashDeviceInfo(mContext);
        //保存错误文件
        String fileName = saveCrashInfoToFile(ex);
        //上传文件
        sendCrashReportsToServer(mContext, true);
        return true;
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer(boolean flag) {
        sendCrashReportsToServer(mContext, flag);
    }

    private void collectCrashDeviceInfo(Context mContext) {
        mDeviceCrashInfo.put(VERSION_NAME, AppUtils.getVerName(mContext) == null ? "not set" :
                AppUtils.getVerName(mContext));
        mDeviceCrashInfo.put(VERSION_CODE, String.valueOf(AppUtils.getVerCode(mContext)));

        Field[] fieles = Build.class.getDeclaredFields();
        try {
            for (Field field : fieles) {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), String.valueOf(field.get(null)));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String saveCrashInfoToFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : mDeviceCrashInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        Throwable throwable = ex.getCause();
        while (throwable != null) {
            throwable.printStackTrace(printWriter);
            throwable = throwable.getCause();
        }
        String result = writer.toString();
        sb.append(result);
        printWriter.close();

        CharSequence timestamp = DateFormat.format("yyyyMMdd_kkmmss", System.currentTimeMillis());
        String filename = timestamp + "error" + MyApplication.flbId + CRASH_REPORTER_EXTENSION;
        try {
            FileOutputStream fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(sb.toString().getBytes());

            fos.flush();
            fos.close();
            return filename;
        } catch (Exception e) {

        }
        return "no file";
    }

    private void sendCrashReportsToServer(Context mContext, boolean flag) {
        String[] crFiles = getCrashReportFiles(mContext);

        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> treeSetFiles = new TreeSet<>();
            treeSetFiles.addAll(Arrays.asList(crFiles));
            for (String fileName : treeSetFiles) {
                Log.i("fName", fileName);
                File file = new File(mContext.getFilesDir(), fileName);
                postReport(file, flag);
                //                file.delete();
            }
        }
    }

    private void postReport(final File file, final boolean flag) {
        new Thread() {
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams();
                requestParams.addBodyParameter("app_name", "药管家");
                requestParams.addBodyParameter("app_version", String.valueOf(AppUtils.getVerCode(mContext)));
                requestParams.addBodyParameter("app_tag", String.valueOf(AppUtils.getVerName(mContext)));
                String stirng = file2String(file, "utf-8");
                requestParams.addBodyParameter("bug_message", stirng);
                HttpUtils httpUtils = new HttpUtils();
                try {
                    ResponseStream responseStream = httpUtils.sendSync(HttpRequest.HttpMethod.POST,
                            MHttpUtils.DEBUG_URL, requestParams);
                    String entity = responseStream.readString();
                    KLog.json(entity);
                    JsonElement jsonElement = new JsonParser().parse(entity);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonElement response = jsonObject.get("response");
                    JsonObject jObject = response.getAsJsonObject();
                    JsonElement code = jObject.get("code");
                    if (code.getAsInt() == 1) {
                        file.delete();
                    }
                    if (flag) {
                        AppManager.getAppManager().AppExit(mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String[] getCrashReportFiles(Context mContext) {
        File fileDir = mContext.getFilesDir();
        Log.i("APath", fileDir.getAbsolutePath());
        Log.i("Path", fileDir.getPath());
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return fileDir.list(filter);
    }

    public static String file2String(File file, String encoding) {
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            if (encoding == null || "".equals(encoding.trim())) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                reader = new InputStreamReader(new FileInputStream(file));
            }
            //将输入流写入输出流
            char[] buffer = new char[1024 * 8];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        //返回转换结果
        if (writer != null) return writer.toString();
        else return null;
    }
}
