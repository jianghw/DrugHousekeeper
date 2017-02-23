package com.cjy.flb.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.activity.SetRepeatDrug;
import com.cjy.flb.adapter.DatabaseAdapter;
import com.cjy.flb.adapter.MyAdapter;
import com.cjy.flb.bean.PullDownMedicine;
import com.cjy.flb.dao.BoxDao;
import com.cjy.flb.dao.SetMedicTimeDao;
import com.cjy.flb.db.Box;
import com.cjy.flb.db.SetMedicTime;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.PinYin;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.google.gson.Gson;
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
import com.socks.library.KLog;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2015/12/11 0011.
 * 添加药物窗口
 */
public class SetMedicPopWindow extends AlertDialog implements CompoundButton
        .OnCheckedChangeListener {

    private Context context;
    private String pointInDay;//当前时间段

    private Button okBtn;
    private Button cancelBtn;
    private TextView measureTv;
    private ImageView upMimg;
    private ImageView downMimg;
    private OsiEditText addEdit;

    private int count = 0;//计量数
    private AlertDialog mDialog;
    /**
     * 模糊提示药物
     */
    private List<String> testArray = new ArrayList<>();
    private MyAdapter adapter;
    private ListView listView;
    private LinearLayout ly;
    private CheckBox mornBox;
    private CheckBox nonBox;
    private CheckBox afterBox;
    private CheckBox evenBox;
    //设置药物所传表id
    private String curID = "0";

    public SetMedicPopWindow(Context context, String pointInDay,//时间段
                             boolean[] cWeeks, //重复的星期
                             String time, //设置时间
                             Handler handler,
                             String cWeek) {//当前天
        super(context);
        this.context = context;
        this.pointInDay = pointInDay;
        List<Integer> numbers = new ArrayList<>();//要上传的number[0,4...]
        for (int i = 1; i < cWeeks.length; i++) {
            if (cWeeks[i]) {
                Box box = MyApplication.getDaoSession().getBoxDao().queryBuilder()
                        .where(BoxDao.Properties.Point_in_time.eq(pointInDay),
                                BoxDao.Properties.Day_in_week.eq(i)).build().forCurrentThread().unique();
                numbers.add(box.getNumber());
            }
        }
        //number 对应的是每天的一个时间段 week~当前天
        initalize(numbers, time, handler, weekToDatabaseId(cWeek));
    }

    private String weekToDatabaseId(String week) {
        String day = null;
        if (week.equals(context.getString(R.string.MON))) {
            day = "1";
        } else if (week.equals(context.getString(R.string.TUE))) {
            day = "2";
        } else if (week.equals(context.getString(R.string.WED))) {
            day = "3";
        } else if (week.equals(context.getString(R.string.THU))) {
            day = "4";
        } else if (week.equals(context.getString(R.string.FRI))) {
            day = "5";
        } else if (week.equals(context.getString(R.string.SAT))) {
            day = "6";
        } else if (week.equals(context.getString(R.string.SUN))) {
            day = "7";
        }
        return day;
    }

    /**
     * @param numbers number 对应的是每天的一个时间段
     * @param time    时间
     * @param handler
     * @param week    week~当前天
     */
    private void initalize(final List<Integer> numbers, final String time, final Handler handler, final String week) {
        Builder builder = new Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_medic_dialog, null);
        builder.setView(view);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);

        ly = (LinearLayout) view.findViewById(R.id.lr);
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new MyAdapter(MyApplication.getInstance(), testArray);
        listView.setAdapter(adapter);

        measureTv = (TextView) view.findViewById(R.id.tv_measure);
        addEdit = (OsiEditText) view.findViewById(R.id.edit_add);
        //隐藏自定义中的第一个控件
        addEdit.setTvTitleToGone();
        addEdit.setImagViewVisib();
        addEdit.setContentHint(context.getString(R.string.fill_medicine_name));

        upMimg = (ImageView) view.findViewById(R.id.imgv_up_measure);
        downMimg = (ImageView) view.findViewById(R.id.imgv_donw_measure);
        downMimg.setEnabled(false);
        //重复选择框
        mornBox = (CheckBox) view.findViewById(R.id.cbox_morn);
        nonBox = (CheckBox) view.findViewById(R.id.cbox_non);
        afterBox = (CheckBox) view.findViewById(R.id.cbox_after);
        evenBox = (CheckBox) view.findViewById(R.id.cbox_even);
        mornBox.setOnCheckedChangeListener(this);
        nonBox.setOnCheckedChangeListener(this);
        afterBox.setOnCheckedChangeListener(this);
        evenBox.setOnCheckedChangeListener(this);

        switch (pointInDay) {
            case "MORN":
                mornBox.setChecked(true);
                mornBox.setEnabled(false);
                break;
            case "NOON":
                nonBox.setChecked(true);
                nonBox.setEnabled(false);
                break;
            case "AFTERNOON":
                afterBox.setChecked(true);
                afterBox.setEnabled(false);
                break;
            case "NIGHT":
                evenBox.setChecked(true);
                evenBox.setEnabled(false);
                break;
        }

        //计量“+”加数
        upMimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count + 1;
                measureTv.setText(String.valueOf(count));
                if (count != 0) {
                    downMimg.setEnabled(true);
                }
            }
        });
        //计量“——”加数
        downMimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count != 0) {
                    count = count - 1;
                    measureTv.setText(String.valueOf(count));
                }
                if (count == 0) {
                    downMimg.setEnabled(false);
                }
            }
        });
        okBtn = (Button) view.findViewById(R.id.btn_ok);
        cancelBtn = (Button) view.findViewById(R.id.btn_ignore);
        //确认
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (measureTv.getText().equals("0")) {
                    ToastUtil.showShort(context.getString(R.string.set_medic_count));
                } else {
                    if (!TextUtils.isEmpty(addEdit.getEditText())) {
                        if (determineIfData()) {
                            ToastUtil.showShort(context.getString(R.string.set_medic_exist));
                        } else {
                            //上传药物~
                            uploadDrugsEveryDay(numbers, time, handler);
                            //重复药物
                            everydayPointRepeatDrugs(week);
                        }
                    }
                    mDialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        addEdit.getEtContentOnly().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            //正在输入
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 每次输入的时候，重新初始化数据列表
                //                testArray = new ArrayList<>();
                if (!TextUtils.isEmpty(addEdit.getEditText())) {// 判断输入内容是否为空，为空则跳过
                    // 查询相似数据--传入一个转换为拼音的字符串
                    char[] charArr = null;
                    charArr = addEdit.getEditText().toCharArray();
                    int t0 = charArr.length;
                    boolean isChinese = false;
                    for (int i = 0; i < t0; i++) {
                        // 判断是否为汉字字符
                        if (Character.toString(charArr[i]).matches("[\\u4E00-\\u9FA5]+")) {
                            isChinese = true;
                        } else {
                            // 如果不是汉字字符，直接取出字符并连接到字符串t4后
                            isChinese = false;
                        }
                    }
                    if (isChinese) {
                        testArray = DatabaseAdapter.getIntance(context).queryInfoChinese(addEdit.getEditText());
                    } else {
                        testArray = DatabaseAdapter.getIntance(context).queryInfo(PinYin
                                .getPinYin(addEdit.getEditText()));
                    }
                }
                adapter.refreshData(testArray);// Adapter刷新数据
                listView.setVisibility(View.VISIBLE);
                ly.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (addEdit.getEditText().length() == 0) {
                    listView.setVisibility(View.GONE);
                    ly.setVisibility(View.VISIBLE);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {// listView点击事件

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String[] string = adapter.getItem(position).split("/");
                addEdit.getEtContentOnly().setText(string[0]);
                curID = string[2];
                listView.setVisibility(View.GONE);
                ly.setVisibility(View.VISIBLE);
            }
        });
    }

    private void everydayPointRepeatDrugs(String week) {
        for (String key : SetRepeatDrug.checkMap.keySet()) {
            //不是当前时段，且选择了~
            if (!key.equals(pointInDay) && SetRepeatDrug.checkMap.get(key)) {
                //唯一number
                Box box = MyApplication.getDaoSession().getBoxDao().queryBuilder()
                        .where(BoxDao.Properties.Point_in_time.eq(key), BoxDao.Properties.Day_in_week.eq(week))
                        .build().forCurrentThread().unique();
                QueryBuilder<SetMedicTime> setBuilder = MyApplication.getDaoSession().getSetMedicTimeDao()
                        .queryBuilder()
                        .where(SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                                SetMedicTimeDao.Properties.Number.eq(box.getNumber()));
                //查询对应box.getNumber()中有无
                if (setBuilder.buildCount().count() > 0) {//例如,当中午有药物(可能是以前有药，或前一步刚刚加上去的)
                    List<SetMedicTime> list = setBuilder.build().forCurrentThread().list();
                    QueryBuilder<SetMedicTime> yesTime = MyApplication.getDaoSession()
                            .getSetMedicTimeDao().queryBuilder()
                            .where(SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                                    SetMedicTimeDao.Properties.Medicine.eq(addEdit.getEditText()),//药物名称~
                                    SetMedicTimeDao.Properties.Number.eq(box.getNumber()));

                    if (list.get(0).getEat_time() != null) {//有时间时
                        if (yesTime.buildCount().count() == 1) {//药物名重复时，更新
                            SetMedicTime smt = yesTime.build().forCurrentThread().unique();
                            smt.setQuantity(Integer.valueOf(measureTv.getText().toString()));
                            smt.setMedicine_id(Integer.valueOf(curID));
                            MyApplication.getDaoSession().getSetMedicTimeDao().updateInTx(smt);
                        } else if (yesTime.buildCount().count() == 0) {//药物不存在时，提交服务器，更新数据库
                            uploadDrugsOneDay(box.getNumber(), list.get(0).getEat_time());
                        }
                    } else {//没时间时，只修改数据库
                        if (yesTime.buildCount().count() == 1) {//药物重复时，更新
                            SetMedicTime smt = yesTime.build().forCurrentThread().unique();
                            smt.setQuantity(Integer.valueOf(measureTv.getText().toString()));
                            MyApplication.getDaoSession().getSetMedicTimeDao().updateInTx(smt);
                        } else if (yesTime.buildCount().count() == 0) {//药物不存在时，更新数据库
                              /* SetMedicTime(Long id, String number, String device_uid, String medicine, Integer
                              quantity,
                                    Integer medicine_id, String unit, String eat_time)*/
                            SetMedicTime smt = new SetMedicTime(
                                    null,
                                    String.valueOf(box.getNumber()),
                                    MyApplication.flbId,
                                    addEdit.getEditText(),
                                    Integer.valueOf(measureTv.getText().toString()),
                                    Integer.valueOf(curID), null, null);
                            MyApplication.getDaoSession().getSetMedicTimeDao().insertInTx(smt);
                        }
                    }
                } else if (setBuilder.buildCount().count() == 0) {//对应无药物时
                    SetMedicTime smt = new SetMedicTime(
                            null,
                            String.valueOf(box.getNumber()),
                            MyApplication.flbId,
                            addEdit.getEditText(),
                            Integer.valueOf(measureTv.getText().toString()),
                            Integer.valueOf(curID), null, null);
                    MyApplication.getDaoSession().getSetMedicTimeDao().insertInTx(smt);
                }
            }
        }
    }

    /**
     * 上传每天设置的药物
     *
     * @param numbers [0,4,8...]
     * @param time
     * @param handler
     */
    private void uploadDrugsEveryDay(final List<Integer> numbers, String time, final Handler handler) {
        String accessToken = SharedPreUtil.getString("access_token");
        PullDownMedicine pullDownMedicine = new PullDownMedicine();
        pullDownMedicine.setDevice_uid(MyApplication.flbId);
        pullDownMedicine.setNumber(numbers);
        pullDownMedicine.setEat_time(time);
        pullDownMedicine.setUnit("粒");
        ArrayList<PullDownMedicine.MedicineListEntity> medicine_list = new ArrayList<>();
        //添加显示药物
        for (int i = 0; i < SetRepeatDrug.mList.size(); i++) {
            String string = SetRepeatDrug.mList.get(i);
            String[] medicines = string.split("/");
            PullDownMedicine.MedicineListEntity medicineListEntity = new PullDownMedicine.MedicineListEntity();
            //药~剂量~id
            medicineListEntity.setMedicine_name(Integer.valueOf(medicines[2]));//id
            medicineListEntity.setQuantity(Integer.valueOf(medicines[1]));
            medicine_list.add(medicineListEntity);
        }
        //添加准备设置药物
        PullDownMedicine.MedicineListEntity medicineListEntity = new PullDownMedicine.MedicineListEntity();
        medicineListEntity.setMedicine_name(Integer.valueOf(curID));
        medicineListEntity.setQuantity(Integer.valueOf(measureTv.getText().toString()));
        medicine_list.add(medicineListEntity);

        pullDownMedicine.setMedicine_list(medicine_list);
        RequestParams requestParams = new RequestParams();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(pullDownMedicine);
            requestParams.addHeader("Authorization", "Bearer " + accessToken);
            requestParams.addHeader("Content-Type", "application/json");
            requestParams.setBodyEntity(new StringEntity(json, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.LOADING_EVERY_DAY,
                requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            JsonElement element = jsonObject.get("response");
                            JsonObject jObject = element.getAsJsonObject();
                            JsonElement codeElement = jObject.get("code");
                            int code = codeElement.getAsInt();
                            if (code == 1) {
                                //避免有重复数据，先删除
                                deletesDatabaseUploaded(numbers, handler);
                            } else {
                                ToastUtil.showShort(context.getString(R.string.submit_error));
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtil.showShort(context.getString(R.string.submit_error));
                    }
                });
    }

    //删除数据库已经上传的数据
    private void deletesDatabaseUploaded(List<Integer> numbers, Handler handler) {
        for (int number : numbers) {//已经上传的数据
            List<SetMedicTime> smtList = MyApplication.getDaoSession().getSetMedicTimeDao()
                    .queryBuilder()
                    .where(SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                            SetMedicTimeDao.Properties.Number.eq(number))
                    .build().forCurrentThread().list();
            MyApplication.getDaoSession().getSetMedicTimeDao().deleteInTx(smtList);
        }
        if (handler != null) {
            SetRepeatDrug.mList.add(addEdit.getEditText() + "/" + measureTv.getText().toString() + "/" + curID);
            Message message = Message.obtain();
            message.what = 100;
            handler.sendMessage(message);
        }
    }

    /**
     * 上传单个number的药物
     *
     * @param number
     * @param eat_time
     */
    private void uploadDrugsOneDay(final Integer number, final String eat_time) {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addBodyParameter("device_uid", MyApplication.flbId);
        requestParams.addBodyParameter("number", String.valueOf(number));
        requestParams.addBodyParameter("medicine_id", curID);
        requestParams.addBodyParameter("unit", "粒");
        requestParams.addBodyParameter("eat_time", eat_time);
        requestParams.addBodyParameter("quantity", measureTv.getText().toString());
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.LOADING_SIMPLE_MED, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            JsonElement element = jsonObject.get("response");
                            JsonObject jObject = element.getAsJsonObject();
                            JsonElement codeElement = jObject.get("code");
                            int code = codeElement.getAsInt();
                            if (code == 1) {
                                SetMedicTime smt = new SetMedicTime(
                                        null,
                                        String.valueOf(number),
                                        MyApplication.flbId,
                                        addEdit.getEditText(),
                                        Integer.valueOf(measureTv.getText().toString()),
                                        Integer.valueOf(curID), null, eat_time);
                                MyApplication.getDaoSession().getSetMedicTimeDao().insertInTx(smt);
                            }
                        } catch (JsonSyntaxException | NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtil.showShort(context.getString(R.string.submit_error));
                    }
                });
    }

    /**
     * 药物是否存在过
     *
     * @return
     */
    private boolean determineIfData() {
        boolean isTrue = false;
        for (String string : SetRepeatDrug.mList) {
            if (string.split("/")[0].equalsIgnoreCase(addEdit.getEditText())) {
                isTrue = true;
            }
        }
        return isTrue;
    }

    public void showAtBottom(View view) {
        mDialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbox_morn:
                if (isChecked) {
                    SetRepeatDrug.checkMap.put("MORN", true);
                } else {
                    SetRepeatDrug.checkMap.put("MORN", false);
                }
                break;
            case R.id.cbox_non:
                if (isChecked) {
                    SetRepeatDrug.checkMap.put("NOON", true);
                } else {
                    SetRepeatDrug.checkMap.put("NOON", false);
                }
                break;
            case R.id.cbox_after:
                if (isChecked) {
                    SetRepeatDrug.checkMap.put("AFTERNOON", true);
                } else {
                    SetRepeatDrug.checkMap.put("AFTERNOON", false);
                }
                break;
            case R.id.cbox_even:
                if (isChecked) {
                    SetRepeatDrug.checkMap.put("NIGHT", true);
                } else {
                    SetRepeatDrug.checkMap.put("NIGHT", false);
                }
                break;
        }
    }

    public interface OnDialogClickListener {
        void onOKClick();

        void onCancelClick();

        void onUpMeasureClick();

        void onDownMeasureClick();
    }
}
