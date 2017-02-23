package com.cjy.flb.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;

import com.cjy.flb.activity.AddFlbActivity;
import com.cjy.flb.activity.CompleteActivity;
import com.cjy.flb.activity.LoginActivity;
import com.cjy.flb.activity.MainActivity;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.activity.PCSOfNetworkTypeActivity;
import com.cjy.flb.bean.DateQueryMedic;
import com.cjy.flb.bean.GetBoxUserInfo;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.EatDao;
import com.cjy.flb.dao.IsNotifDao;
import com.cjy.flb.db.Eat;
import com.cjy.flb.db.IsNotif;
import com.cjy.flb.domain.BindedFlb;
import com.cjy.flb.domain.Cdc12320ApiTest;
import com.cjy.flb.domain.Flb;
import com.cjy.flb.domain.TokenBean;
import com.cjy.flb.manager.AppManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.QueryBuilder;

public class MHttpUtils {
  /*  private static String API_URL = "https://api.ccd12320.com";
    private static String CCD_URL = "https://ccd12320.com";*/

    private static String API_URL = "https://api.caijiyun.cn";
    private static String CCD_URL = "https://caijiyun.cn";

    private static String TEST_URL = "http://120.24.158.78";

 /*   private static String API_URL = TEST_URL;
    private static String CCD_URL = TEST_URL;*/

    //极光推送
    public static final String AURORA_PUSH = API_URL + "/api/1/medicine_box/client_apps";
    //获取邮箱
    public static final String FIND_EMAIL = API_URL +
            "/api/1/medicine_box/user_infos?find_by=token";
    //app更新地址
    public static final String url = CCD_URL + "/api/1/apps/14";
    //user注册的地址
    public static final String REGISTER_BASIC =
            "NDU3YmRlMWIwYjc5MDI3ODE0ODNhNmM0NDUyODBhOTcxZTExZjllYjA4MDY3ZDA5NWJlODZlY2QwZjhhZWU4Mzo5MGE4Y2RmNDQ5MjE5MTBmZjkwNjM2ZGRmM2I2NzBiZDM0YjQyNzI5MThhMzMxMmU2ODhiZmI1ODE5NTk1Yzk4";
    static String registUrl = CCD_URL + "/users";
    //
    // "MzFiZGVhYjExMjU0MmZjNzRhNjA5YTIxMzkyMmIwYTZiYzIwOWExNDFkNGFjN2I5NDZlZjVmOGYxNmYxMzczZTpiMjIzZmRmMzM3YWJhZGFhOTJlMDEwZTM1NjhmOTYwNzJhMmZiZDVkY2UxNDE3N2MxYTA3MzE5MjQzZTIwY2Q5";
    static final String AUTO_TEST_URL = API_URL + "/api/1/develop_testers";//自动登陆的URL
    //是否绑定了药盒
    public static final String isBindFlbUrl = API_URL +
            "/api/1/medicine_box/client_apps/is_bound_device";
    public static final String addFlbUrl = API_URL + "/api/1/medicine_box/devices";//绑定福乐宝盒
    static String addFromFlbUrl = API_URL + "/api/1/medicine_box/devices/app_bound_device";//获取福乐宝权限
    //设置药盒用户信息
    static String completeUrl = API_URL + "/api/3/medicine_box/device_infos";

    //解除药盒绑定
    public static final String REMOVE_DEVICE = API_URL + "/api/1/medicine_box/client_apps/remove_bound_device";
    //药物设置页拉取数据url
    public static final String PULL_DOWN_URL = API_URL +
            "/api/1/medicine_box/client_apps/get_medicine_data?device_uid=";
    //判断药盒是否在线
    public static final String IS_ONLINE = API_URL + "/api/1/medicine_box/devices/device?device_uid=";
    //删除药物url
    public static final String DELETE_MEDIC = API_URL + "/api/1/medicine_box/medicines/delete_medicine";
    //删除多个药物
    public static final String DELETE_MORE_MEDIC = API_URL + "/api/1/medicine_box/medicines/delete_many_medicine";
    //药物上传url
    public static final String LOADINT_MEDIC = API_URL + "/api/1/medicine_box/medicines";
    //每天添加的药物接口
    public static final String LOADING_EVERY_DAY = API_URL + "/api/1/medicine_box/medicines/create_day_medicine";
    //在一餐里添加单个药物
    public static final String LOADING_SIMPLE_MED = API_URL + "/api/1/medicine_box/medicines";
    //按日期查询服药记录
    public static final String DATE_QUERY_MEDIC = API_URL + "/api/1/medicine_box/medicines/find_by_day";
    //按日期区间查询服药记录
    public static final String DATE_INTERVAL_MEDIC = API_URL + "/api/1/medicine_box/medicines/find_interval_day";
    //更新吃药时间
    public static final String UPDATE_TIME = API_URL + "/api/1/medicine_box/medicines/edit_time";
    //查询药盒用户信息
    public static final String MEDIC_USER_INFO = API_URL + "/api/1/medicine_box/device_infos";
    //修改药盒用户信息
    public static final String DEVISE_INFO = API_URL + "/api/1/medicine_box/device_infos";
    //重置密码
    public static final String RESET_PASSWORD = CCD_URL + "/users/password";
    //药物反馈
    public static final String MEDIC_FEEDBACK = API_URL +
            "/api/1/medicine_box/feedbacks/medicine_feedback";
    //反馈意见
    public static final String DIEVE_FEEDBACK = API_URL + "/api/1/flb/feedbacks";
    //手机注册地址
    public static final String PHONE_REGISTER = CCD_URL + "/users";
    public static final String PHONE_BASIC =
            "NDU3YmRlMWIwYjc5MDI3ODE0ODNhNmM0NDUyODBhOTcxZTExZjllYjA4MDY3ZDA5NWJlODZlY2QwZjhhZWU4Mzo5MGE4Y2RmNDQ5MjE5MTBmZjkwNjM2ZGRmM2I2NzBiZDM0YjQyNzI5MThhMzMxMmU2ODhiZmI1ODE5NTk1Yzk4";
    //
    // "MzFiZGVhYjExMjU0MmZjNzRhNjA5YTIxMzkyMmIwYTZiYzIwOWExNDFkNGFjN2I5NDZlZjVmOGYxNmYxMzczZTpiMjIzZmRmMzM3YWJhZGFhOTJlMDEwZTM1NjhmOTYwNzJhMmZiZDVkY2UxNDE3N2MxYTA3MzE5MjQzZTIwY2Q5";
    //手机激活地址
    public static final String PHONE_ACTIVATE = API_URL + "/api/1/medicine_box/activates";

    //重新获取验证码
    public static final String RE_GET_AUTH_CODE = API_URL +
            "/api/1/medicine_box/activates/get_code";
    //重置密码，获取验证码接口
    public static final String PASSWORD_GET_AUTH_CODE = API_URL +
            "/api/1/medicine_box/passwd_activates/get_code";
    //手机注册重置密码
    public static final String RE_PASSWORD_GET_AUTH_CODE = CCD_URL + "/users/password";
    //debug上传
    public static final String DEBUG_URL = API_URL + "/api/1/bug_messages";
    //采集云地址
    public static final String CJYUN_URL = CCD_URL + "/signin?user_type=medicine_box";
    //判断药盒是否连接wifi
    public static final String VISIT_URL = API_URL + "/api/1/medicine_box/devices/wifi_device?device_uid=";
    //注册用的参数
    private static int REGIST_SUCCESS = 1;//注册成功
    private static int REGIST_FAILED_LOGIN_START = 2;//注册失败，用户名必须以字母开头
    private static int REGIST_FAILED_LOGIN_SUPPORT = 3;//注册失败，用户名只包括字母，数字和下划线
    private static int REGIST_FAILED_LOGIN_EXIST = 4;//注册失败，用户名已存在
    private static int REGIST_FAILED_LOGIN_TO_SHORT = 9;//注册失败，用户名长度不能少于4位
    private static int REGIST_FAILED_PASSWORD_SUM = 5;//注册失败，密码长度不能少于6位
    private static int REGIST_FAILED_PASSWORD_SUPPORT = 6;//注册失败，密码格式有问题
    private static int REGIST_FAILED_EMAIL = 7;//注册失败，邮箱格式不对
    private static int REGIST_FAILED_EMAIL_E = 10;//注册失败，邮箱已经存在
    private static int REGIST_FAILED_UNKNOW = 8;//注册失败，未知错误

    //登陆用的参数
    private static String apiKey =
            "457bde1b0b7902781483a6c445280a971e11f9eb08067d095be86ecd0f8aee83";
    //                "31bdeab112542fc74a609a213922b0a6bc209a141d4ac7b946ef5f8f16f1373e";//test
    private static String apiSecret =
            "90a8cdf44921910ff90636ddf3b670bd34b4272918a3312e688bfb5819595c98";
    //                "b223fdf337abadaa92e010e3568f96072a2fbd5dce14177c1a07319243e20cd9";//test
    private static OAuthService mService = new ServiceBuilder()
            //            .provider(Cdc12320Api.class)
            .provider(Cdc12320ApiTest.class)//test
            .apiKey(apiKey)
            .apiSecret(apiSecret)
            .scope("flb_app_read flb_app_write")
            .build();
    protected static String refreshToken = "";

    private static boolean isRefresh;

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gps是否打开
     *
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = ((LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = locationManager.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }

    /**
     * wifi是否打开
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断当前网络是否是wifi网络
     * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前网络是否3G网络
     *
     * @param context
     * @return boolean
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }


    /**
     * 获取版本号(内部识别号)
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return pi.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 注册新用户
     *
     * @param userName
     * @param password
     * @param passwordConfir
     * @param email
     */
    public static void registUser(String userName, String password,
                                  String passwordConfir, String email,
                                  final Handler handler) {
        com.lidroid.xutils.HttpUtils http = new com.lidroid.xutils.HttpUtils();
        final RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Basic " + REGISTER_BASIC);

        params.addBodyParameter("login", userName);
        params.addBodyParameter("password", password);
        params.addBodyParameter("password_confirmation", passwordConfir);
        params.addBodyParameter("user_type", "sz_flb_user");
        params.addBodyParameter("key_type", "name");
        params.addBodyParameter("email", email);

        http.send(HttpMethod.POST, registUrl, params, new RequestCallBack<Object>() {
            public Message message = new Message();

            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result.toString());
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        message.what = REGIST_SUCCESS;
                        handler.sendMessage(message);
                    } else {
                        JSONObject failureIndex = jsonObject.getJSONObject("failure_index");
                        L.i(failureIndex + "");

                        if (responseInfo.result.toString().contains("login")) {
                            JSONArray login = failureIndex.getJSONArray("login");
                            if (login != null && login.optString(0).length() > 0) {
                                if (login.optString(0).equals("Name must start with alphabet")) {
                                    message.what = REGIST_FAILED_LOGIN_START;
                                    handler.sendMessage(message);
                                } else if (login.optString(0).equals("Only support alphabet," +
                                        "number and underline")) {
                                    message.what = REGIST_FAILED_LOGIN_SUPPORT;
                                    handler.sendMessage(message);
                                } else if (login.optString(0).equals("is too short (minimum is 4 " +
                                        "characters)")) {
                                    message.what = REGIST_FAILED_LOGIN_TO_SHORT;
                                    handler.sendMessage(message);
                                } else {
                                    message.what = REGIST_FAILED_LOGIN_EXIST;
                                    handler.sendMessage(message);
                                }
                            }
                        } else if (responseInfo.result.toString().contains("email")) {
                            JSONArray email = failureIndex.getJSONArray("email");
                            if (email.optString(0).equals("Exist")) {
                                message.what = REGIST_FAILED_EMAIL_E;
                                handler.sendMessage(message);
                            } else if (email.optString(0).equals("Email format error")) {
                                message.what = REGIST_FAILED_EMAIL;
                                handler.sendMessage(message);
                            }
                        } else if (responseInfo.result.toString().contains("password")) {
                            JSONArray password = failureIndex.getJSONArray("password");
                            if (password != null && password.optString(0).length() > 0) {
                                if (password.optString(0).equals("过短（最短为 6 个字符）")) {
                                    message.what = REGIST_FAILED_PASSWORD_SUM;
                                    handler.sendMessage(message);
                                } else {
                                    message.what = REGIST_FAILED_PASSWORD_SUPPORT;
                                    handler.sendMessage(message);
                                }
                            }
                        } else {
                            message.what = REGIST_FAILED_UNKNOW;
                            handler.sendMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                KLog.i(s);
                message.what = REGIST_FAILED_UNKNOW;
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 登陆操作 获取token
     *
     * @param userName
     * @param password
     * @param context
     * @param handler
     */
    public static void initLoginApp(String userName, String password, Context context, Handler
            handler) {
        startAuthorize(userName, password, context, handler);
    }

    /**
     * 开始授权,不存在token时需要登录
     *
     * @param userName
     * @param password
     * @param handler
     */
    private static void startAuthorize(final String userName, final String password, final
    Context context, final Handler handler) {
        System.out.println("=== Ccd12320's OAuth Workflow ===");
        (new AsyncTask<Void, Void, Token>() {
            @Override
            protected Token doInBackground(Void... params) {
                try {
                    Token token = mService.getAccessToken(userName, password);
                    return token;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Token accessToken) {
                try {
                    if (accessToken == null) {
                        Message message = Message.obtain();
                        message.what = 100;
                        handler.sendMessage(message);
                    } else {

                        KLog.i("accessToken:" + accessToken.getToken());
                        KLog.i("refreshToken:" + mService.extractRefreshToken(accessToken
                                .getRawResponse()).getToken());
                        refreshToken = mService.extractRefreshToken(accessToken.getRawResponse())
                                .getToken();
                        KLog.i("RawResponse:" + accessToken.getRawResponse());

                        saveToken(accessToken, context);
                        bindedFlb(context, accessToken.getToken());

                        Message message = Message.obtain();
                        message.what = 200;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    /**
     * 存储token的信息
     *
     * @param token
     */
    private static void saveToken(Token token, Context context) {
        KLog.i("保存信息");
        TokenBean tokenBean = new Gson().fromJson(token.getRawResponse(), TokenBean.class);

        SharedPreUtil.setString("access_token", tokenBean.getAccess_token());
        SharedPreUtil.setString("refresh_token", tokenBean.getRefresh_token());
        SharedPreUtil.setInteger("expires_in", tokenBean.getExpires_in());
    }

    /**
     * 刷新token
     */
    public static void refreshToken(final Context context) {
        System.out.println("=== Ccd12320's refresh Token Workflow ===");
        (new AsyncTask<Void, Void, Token>() {
            @Override
            protected Token doInBackground(Void... params) {
                Verifier refreshTokenVer = new Verifier(SharedPreUtil.getString("refresh_token"));
                try {
                    return mService.refreshAccessToken(null, refreshTokenVer);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Token accessToken) {
                try {
                    // AccessToken is passed here! Do what you want!
                    System.out.println("accessToken:" + accessToken.getToken());
                    System.out.println("refreshToken:" + mService.extractRefreshToken(accessToken
                            .getRawResponse()).getToken());
                    refreshToken = mService.extractRefreshToken(accessToken.getRawResponse())
                            .getToken();
                    System.out.println("RawResponse:" + accessToken.getRawResponse());

                    saveToken(accessToken, context);
                    isRefresh = true;
                } catch (Exception e) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        }).execute();
    }

    /**
     * 自动登陆
     *
     * @param context
     */
    public static void tokenLogin(final Context context) {
        RequestParams requestParams = new RequestParams();
        final String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        final HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        httpUtils.send(HttpRequest.HttpMethod.POST, AUTO_TEST_URL, requestParams, new RequestCallBack<String>() {
            //访问成功就不处理
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                KLog.json(responseInfo.result);
                bindedFlb(context, accessToken);
            }

            //访问失败表示token失效，则刷新token
            @Override
            public void onFailure(HttpException e, String s) {
                Intent intent = new Intent(context, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("show_token", true);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 获取accessToken是否绑定了药盒信息
     *
     * @param context
     */
    public static void bindedFlb(final Context context, String accessToken) {
        RequestParams requestParams = new RequestParams();
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configDefaultHttpCacheExpiry(10);
        httpUtils.configTimeout(1000);
        httpUtils.send(HttpMethod.GET, isBindFlbUrl, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                KLog.json(responseInfo.result);
                BindedFlb bindedFlb = null;
                try {
                    bindedFlb = new Gson().fromJson(responseInfo.result, BindedFlb.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                int count = bindedFlb != null ? bindedFlb.getCount() : 0;
                //如果当前已绑定药盒
                if (count > 0) {
                    KLog.i("已绑定所有药盒信息", bindedFlb.getResponse().toString());
                    //设置当前的药盒Id为默认的第一个
                    String strid = SharedPreUtil.getString("flbId");
                    MyApplication.flbId = bindedFlb.getResponse().get(0);
                    if (!TextUtils.isEmpty(strid)) {
                        for (String string : bindedFlb.getResponse()) {
                            if (string.equals(strid)) {
                                MyApplication.flbId = string;
                            }
                        }
                    }
                    SharedPreUtil.setString("flbId", MyApplication.flbId);
                    //存储当前所有绑定药盒信息
                    currentBindingInformation(context, bindedFlb.getResponse());
                    //拉取2个月的网络数据
                    pullTwoMonthMedinInfo(context, MyApplication.flbId);
                    //成功跳转到...面
                    updateMedicineUser(MyApplication.flbId, context);
                } else {
                    //否则去添加药盒
                    KLog.i("TAG", "当前账号未绑定药盒");
                    toAddFlb(context);
                    //结束启动页面
                    AppManager.getAppManager().finishExceptMe(AddFlbActivity.class);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                KLog.i(s);
                toAddFlb(context);
                //结束启动页面
                AppManager.getAppManager().finishExceptMe(AddFlbActivity.class);
            }
        });
    }

    private static void currentBindingInformation(final Context context, List<String> response) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String day = sdf.format(new Date());

        QueryBuilder<IsNotif> builder = DaoHolder.getIsNotifDao(IsNotifDao.Properties.Today.eq(day));
        long count = builder.buildCount().count();
        if (count == 0) {//如果查询不到，说明可能到新的一天,插入新的数据
            DaoHolder.deleteAllIsNotif();
            for (final String id : response) {
                getBoxUserInfo(context, day, id, true);
            }
        }
    }

    /**
     * 获取药盒用户信息
     *
     * @param context
     * @param day     today
     * @param id      medicine_id
     * @param flag    是否为新数据
     */
    public static void getBoxUserInfo(final Context context, final String day, final String id, final boolean flag) {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addQueryStringParameter("device_uid", id);
        MyApplication.getHttpUtils().send(HttpMethod.GET, MHttpUtils.MEDIC_USER_INFO,
                requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonElement jsonElement = null;
                            if (json.isJsonObject()) {
                                JsonObject jsonObject = json.getAsJsonObject();
                                jsonElement = jsonObject.get("response");
                            }
                            if (jsonElement != null && jsonElement.isJsonObject()) {
                                if (!jsonElement.getAsJsonObject().has("message")) {
                                    GetBoxUserInfo getBoxUserInfo = new Gson().fromJson(jsonElement,
                                            GetBoxUserInfo.class);
                                    if (flag) {
                                        DaoHolder.insertInTxIsNotif(day, id, getBoxUserInfo.getDevice_use_name(),
                                                getBoxUserInfo.getDevice_use_phone());
                                    } else {
                                        MyApplication.DEVICE_USER_NAME = getBoxUserInfo.getDevice_use_name();
                                        MyApplication.DEVICE_USER_PHONE = getBoxUserInfo.getDevice_use_phone();
                                        String str = (String) getBoxUserInfo.getAvatar();
                                        if (str != null) {
                                            byte[] bytes = Base64.decode(str, Base64.DEFAULT);
                                            MyApplication.head = bytes;
                                        }
                                    }
                                }
                            } else {//跳转用户资料页
                                if (day.equals("noDay")) {
                                    MyApplication.flbId = id;
                                    context.startActivity(new Intent(context, CompleteActivity.class), null);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d(s);
                    }
                });
    }

    public static void pullTwoMonthMedinInfo(final Context context, final String flbId) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat simpleFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        // 本月的最后一天
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        String lastDat = simpleFormate.format(calendar.getTime());
        //上月的第一天
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        String firstDat = simpleFormate.format(c.getTime());

        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");

        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addBodyParameter("device_uid", flbId);

        requestParams.addBodyParameter("begin_day", firstDat);
        requestParams.addBodyParameter("end_day", lastDat);

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(1000);
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.DATE_INTERVAL_MEDIC, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonElement jsonElement = null;
                            if (json.isJsonObject()) {
                                JsonObject jsonObject = json.getAsJsonObject();
                                jsonElement = jsonObject.get("response");
                            }
                            if (jsonElement != null && jsonElement.isJsonArray()) {
                                final JsonArray jsonArray = jsonElement.getAsJsonArray();
                                if (jsonArray.size() > 0) {
                                    DaoHolder.deleteAllEatDao();
                                    MyApplication.getDaoSession().runInTx(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveTokenToDatabase(jsonArray, flbId);
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        e.printStackTrace();
                    }
                });
    }


    private static void saveTokenToDatabase(JsonArray jsonArray, String flbId) {
        for (JsonElement jElement : jsonArray) {
            DateQueryMedic dateQueryMedic = new Gson().fromJson(jElement, DateQueryMedic.class);
            String eatTime = dateQueryMedic.getEat_medicine_time().split("T")[0];
            boolean isTaken = dateQueryMedic.isTaken();
            int number = dateQueryMedic.getNumber();

            //吃药时间和number覆盖后对应最多4个
            QueryBuilder<Eat> builder = DaoHolder.getEatDaoBy(EatDao.Properties.Eat_medicine_time.eq(eatTime),
                    EatDao.Properties.Number.eq(number), EatDao.Properties.Device_uid.eq(flbId));

            long count = builder.buildCount().count();
            if (count == 1) {
                Eat eat = builder.build().forCurrentThread().unique();
                DaoHolder.updateInTxEatDao(new Eat(eat.getId(), flbId, eatTime, number, isTaken));
            } else if (count == 0) {
                DaoHolder.insertInTxEatDao(new Eat(null, flbId, eatTime, number, isTaken));
            }
        }
    }

    //通过药盒id查找用户信息
    public static void updateMedicineUser(final String flbID, final Context context) {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addQueryStringParameter("device_uid", flbID);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(10);
        httpUtils.send(HttpRequest.HttpMethod.GET, MHttpUtils.MEDIC_USER_INFO,
                requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonElement jsonElement = null;
                            if (json.isJsonObject()) {
                                JsonObject jsonObject = json.getAsJsonObject();
                                jsonElement = jsonObject.get("response");
                            }
                            if (jsonElement != null && jsonElement.isJsonObject()) {
                                JsonObject object = jsonElement.getAsJsonObject();
                                choseFillUserInfo(jsonElement, flbID, context);
                            } else if (jsonElement != null && jsonElement.isJsonNull()) {
                                context.startActivity(new Intent(context, CompleteActivity.class));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        MyApplication.flbId = flbID;
                        context.startActivity(new Intent(context, CompleteActivity.class), null);
                        AppManager.getAppManager().finishExceptMe(CompleteActivity.class);
                    }
                });
    }

    private static void choseFillUserInfo(JsonElement jsonElement, String flbID, Context context) {
        JsonObject object = jsonElement.getAsJsonObject();
        if (object.has("message")) {
            MyApplication.flbId = flbID;
            context.startActivity(new Intent(context, CompleteActivity.class), null);
            AppManager.getAppManager().finishExceptMe(CompleteActivity.class);
        } else {
            Gson gson = new Gson();
            GetBoxUserInfo medicUser = gson.fromJson(jsonElement, GetBoxUserInfo.class);
            if (medicUser.getDevice_use_name() != null) {
                MyApplication.flbId = flbID;
                MyApplication.DEVICE_USER_NAME = medicUser.getDevice_use_name();
                MyApplication.DEVICE_USER_PHONE = medicUser.getDevice_use_phone();
                context.startActivity(new Intent(context, MainActivity.class));
                AppManager.getAppManager().finishExceptMe(MainActivity.class);
            }
        }
    }

    /**
     * 绑定药盒第二步
     *
     * @param id
     * @param sn
     * @param context
     */
    public static void addFromFlb(final String id, String sn, final Context context, final Handler handler) {
        RequestParams params = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        params.addHeader("Authorization", "Bearer " + accessToken);
        params.addBodyParameter("device_uid", id);
        params.addBodyParameter("device_sn", sn);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configDefaultHttpCacheExpiry(0);
        httpUtils.send(HttpMethod.POST, addFromFlbUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                KLog.json(responseInfo.result);
                Flb flb = null;
                try {
                    flb = new Gson().fromJson(responseInfo.result, Flb.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                int code = flb != null ? flb.getResponse().getCode() : 0;
                if (code == 1) {
                    KLog.i("第二绑定成功");
                    MyApplication.flbId = id;
                    SharedPreUtil.setString("flbId", id);
                    Message message = Message.obtain();
                    message.what = 100;
                    handler.sendMessage(message);
                    updateMedicineUser(MyApplication.flbId, context);
                } else {
                    KLog.i("第二步绑定失败");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                KLog.i(s);
                Message message = Message.obtain();
                message.what = 400;
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 到添加福乐宝药盒
     *
     * @param context
     */
    private static void toAddFlb(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, PCSOfNetworkTypeActivity.class);
        context.startActivity(intent);
    }

    /**
     * 完善福乐宝信息
     */
    public static void completeFlb(final String id, final String useName, final String usePhone,
                                   String year, String mouth,
                                   boolean gender, String contact1Name, String contact1Phone,
                                   String contact2Name, String contact2Phone, String doctorName,
                                   String doctorPhone, File avatar, final Context context) {
        RequestParams params = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        params.addHeader("Authorization", "Bearer " + accessToken);

        params.addBodyParameter("device_uid", id);
        params.addBodyParameter("device_use_name", useName);
        params.addBodyParameter("device_use_phone", usePhone);
        params.addBodyParameter("year", year);
        params.addBodyParameter("mouth", mouth);
        params.addBodyParameter("gender", String.valueOf(gender));

        if (contact1Name != null) {
            params.addBodyParameter("emergency_contact_1_name", contact1Name);
        }
        if (contact1Phone != null) {
            params.addBodyParameter("emergency_contact_1_phone", contact1Phone);
        }
        if (contact2Name != null) {
            params.addBodyParameter("emergency_contact_2_name", contact2Name);
        }
        if (contact2Phone != null) {
            params.addBodyParameter("emergency_contact_2_phone", contact2Phone);
        }
        if (doctorName != null) {
            params.addBodyParameter("visiting_staff_name", doctorName);
        }
        if (doctorPhone != null) {
            params.addBodyParameter("visiting_staff_phone", doctorPhone);
        }
        if (avatar != null) {
            try {
                String strings = MHttpUtils.toByteArray(avatar.getAbsolutePath());
                params.addBodyParameter("avatar", strings);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(100);
        httpUtils.send(HttpMethod.POST, completeUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                KLog.json(responseInfo.result);
                try {
                    MyApplication.list.clear();//删除数据，重新加载！
                    MyApplication.hashMap.clear();
                    AppManager.getAppManager().finishActivity(MainActivity.class);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    QueryBuilder<IsNotif> builder = DaoHolder.getIsNotifDao(IsNotifDao.Properties.Medic_id.eq(id),
                            IsNotifDao.Properties.Today.eq(sdf.format(new Date())));
                    long count = builder != null ? builder.buildCount().count() : 0;
                    if (count > 0) {
                        IsNotif isNotif = builder.build().forCurrentThread().unique();
                        isNotif.setMedic_name(useName);
                        isNotif.setMedic_phone(usePhone);
                        DaoHolder.updateInTxIsNotif(isNotif);
                    } else {
                        DaoHolder.insertInTxIsNotif(sdf.format(new Date()), id, useName, usePhone);
                    }
                    //成功跳转到主界面
                    Intent intent = new Intent(context, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("currentPosition", 1);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                    AppManager.getAppManager().finishExceptMe(MainActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                KLog.i(s);
                ToastUtil.showShort("提交失败，网络出错" + s);
            }
        });
    }

    /**
     * 图片文件转为byte[]
     *
     * @param pathName
     * @return
     * @throws IOException
     */
    public static String toByteArray(String pathName) throws IOException {
        FileInputStream fis = new FileInputStream(pathName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c = bis.read();
        while (c != -1) {
            baos.write(c);
            c = bis.read();
        }
        bis.close();
        byte[] rtn = baos.toByteArray();
        String string = Base64.encodeToString(rtn, Base64.DEFAULT);
        baos.close();
        return string;
    }

    /**
     * 转二进制
     *
     * @param bArr
     * @return
     */
    public static String getBinaryStrFromByteArr(byte[] bArr) {
        String result = "";
        for (byte b : bArr) {
            result += getBinaryStrFromByte(b);
        }
        return result;
    }

    private static String getBinaryStrFromByte(byte b) {
        String result = "";
        byte a = b;

        for (int i = 0; i < 8; i++) {
            byte c = a;
            a = (byte) (a >> 1);//每移一位如同将10进制数除以2并去掉余数。
            a = (byte) (a << 1);
            if (a == c) {
                result = "0" + result;
            } else {
                result = "1" + result;
            }
            a = (byte) (a >> 1);
        }
        return result;
    }

    public static byte[] hex2byte(String str) { // 字符串转二进制
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;

        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {

                b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
            }
            return b;

        } catch (Exception e) {
            return null;
        }
    }
}
