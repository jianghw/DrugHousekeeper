package com.cjy.flb.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.cjy.flb.R;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.adapter.WImgAdapter;
import com.cjy.flb.adapter.WeekAdapter;
import com.cjy.flb.dao.BoxDao;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.EatDao;
import com.cjy.flb.dao.SetMedicTimeDao;
import com.cjy.flb.db.Box;
import com.cjy.flb.db.Eat;
import com.cjy.flb.db.SetMedicTime;
import com.cjy.flb.enums.Medicine;
import com.cjy.flb.event.WeekDateEven;
import com.cjy.flb.utils.UIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class NextWeekFragment extends BaseFragment {
    @Bind(R.id.listView)
    ListView mListView;
    @Bind(R.id.gridView)
    GridView mGridView;
    /**
     * 加载日期 11/25
     */
    private WeekAdapter weekAdapter;
    private ArrayList<String> dateList = new ArrayList<>();
    /**
     * 加载图标
     */
    private WImgAdapter wImgAdapter;
    private ArrayList<Medicine> imgList = new ArrayList<>();

    @Override
    protected LoadingPager.LoadResult load() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 7);

        refreshData(calendar);
        return LoadingPager.LoadResult.WRITE;
    }

    private void refreshData(Calendar calendar) {
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        //判断今天是星期几
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        //今天 前面的星期数 再-1
        if (week == 0) {
            calendar.add(Calendar.DATE, -7);
        } else {
            calendar.add(Calendar.DATE, 1 - week - 1);
        }
        if (dateList.size() > 0) {
            dateList.clear();
        }
        if (imgList.size() > 0) {
            imgList.clear();
        }

        try {
            for (int i = 0; i < 7; i++) {
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
                String dateStr = sdf.format(date);
                dateList.add(dateStr);
                //一天对应4条数据
                HashMap<Integer, Medicine> hashMap = new HashMap<>();
                hashMap.put(1, Medicine.USET);
                hashMap.put(2, Medicine.USET);
                hashMap.put(3, Medicine.USET);
                hashMap.put(4, Medicine.USET);

                long time = sdf.parse(dateStr).getTime();
                long nTime = sdf.parse(sdf.format(new Date())).getTime();
                //显示的日期和今天日期比较，未来时间
                noMedicineTime(date, hashMap, time, nTime);

                QueryBuilder<Eat> builder = DaoHolder.getEatDaoBy(
                        EatDao.Properties.Eat_medicine_time.eq(dateStr),
                        EatDao.Properties.Device_uid.eq(MyApplication.flbId));
                if (builder.buildCount().count() > 0) {
                    List<Eat> eatList = builder.build().forCurrentThread().list();
                    setMedicineTime(eatList, hashMap);
                }
                for (int k = 1; k <= hashMap.size(); k++) {
                    imgList.add(hashMap.get(k));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void noMedicineTime(Date date, HashMap<Integer, Medicine> hashMap, long time, long nTime) {
        if (time >= nTime) {
            SimpleDateFormat sdfWeek = new SimpleDateFormat("E");
            String week2 = getDatabaseWeekId(sdfWeek.format(date));

            QueryBuilder<Box> b = DaoHolder.getBoxDao(BoxDao.Properties.Day_in_week.eq(week2));
            if (b.buildCount().count() > 0) {
                List<Box> boxs = b.build().forCurrentThread().list();
                for (Box box : boxs) {
                    QueryBuilder<SetMedicTime> build = DaoHolder.getSetMedicTimeDao(
                            SetMedicTimeDao.Properties.Number.eq(box.getNumber()),
                            SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId));
                    //即，已经设置过
                    if (build.buildCount().count() > 0) {
                        String poinTime = box.getPoint_in_time();
                        switch (poinTime) {
                            case "MORN":
                                hashMap.put(1, Medicine.KONG);
                                break;
                            case "NOON":
                                hashMap.put(2, Medicine.KONG);
                                break;
                            case "AFTERNOON":
                                hashMap.put(3, Medicine.KONG);
                                break;
                            case "NIGHT":
                                hashMap.put(4, Medicine.KONG);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置过服药的，记录
     *
     * @param eatList
     * @param hashMap
     */
    private void setMedicineTime(List<Eat> eatList, HashMap<Integer, Medicine> hashMap) {
        if (eatList.size() > 0) {
            for (Eat eat : eatList) {
                int number = eat.getNumber();
                if (number == 0 || number == 4 || number == 8 || number == 12 || number == 16 || number
                        == 20 ||
                        number == 24) {
                    if (eat.getTaken()) {//吃了药
                        hashMap.put(1, Medicine.NORMAL);
                    } else {
                        hashMap.put(1, Medicine.UNORMAL);
                    }
                } else if (number == 1 || number == 5 || number == 9 || number == 13 || number == 17 ||
                        number ==
                                21 || number == 25) {
                    if (eat.getTaken()) {//吃了药
                        hashMap.put(2, Medicine.NORMAL);
                    } else {
                        hashMap.put(2, Medicine.UNORMAL);
                    }
                } else if (number == 2 || number == 6 || number == 10 || number == 14 || number == 18 ||
                        number
                                == 22 || number == 26) {
                    if (eat.getTaken()) {//吃了药
                        hashMap.put(3, Medicine.NORMAL);
                    } else {
                        hashMap.put(3, Medicine.UNORMAL);
                    }
                } else if (number == 3 || number == 7 || number == 11 || number == 15 || number == 19 ||
                        number
                                == 23 || number == 27) {
                    if (eat.getTaken()) {//吃了药
                        hashMap.put(4, Medicine.NORMAL);
                    } else {
                        hashMap.put(4, Medicine.UNORMAL);
                    }
                }
            }
        }
    }

    //对应数据库中数据
    private String getDatabaseWeekId(String week) {
        String string = null;
        if (week.equals(getString(R.string.SUN)) || week.equals(getString(R.string.SUN_X))) {
            string = "7";
        } else if (week.equals(getString(R.string.MON)) || week.equals(getString(R.string.MON_X))) {
            string = "1";
        } else if (week.equals(getString(R.string.TUE)) || week.equals(getString(R.string.TUE_X))) {
            string = "2";
        } else if (week.equals(getString(R.string.WED)) || week.equals(getString(R.string.WED_X))) {
            string = "3";
        } else if (week.equals(getString(R.string.THU)) || week.equals(getString(R.string.THU_X))) {
            string = "4";
        } else if (week.equals(getString(R.string.FRI)) || week.equals(getString(R.string.FRI_X))) {
            string = "5";
        } else if (week.equals(getString(R.string.SAT)) || week.equals(getString(R.string.SAT_X))) {
            string = "6";
        }
        return string;
    }

    public void onEventMainThread(WeekDateEven event) {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat sdfor = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        Date date = null;
        try {
            date = sdfor.parse(event.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date != null ? date : new Date());
        calendar.add(Calendar.DATE, 7);

        refreshData(calendar);
        weekAdapter.notifyDataSetChanged();
        wImgAdapter.notifyDataSetChanged();
    }

    @Override
    public View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cweek, container, false);
        ButterKnife.bind(this, view);

        EventBus.getDefault().registerSticky(this);
        return view;
    }

    public LoadingPager.LoadResult readDatabase() {
        weekAdapter = new WeekAdapter(UIUtils.getContext(), dateList);
        mListView.setAdapter(weekAdapter);

        wImgAdapter = new WImgAdapter(UIUtils.getContext(), imgList);
        mGridView.setAdapter(wImgAdapter);
        return LoadingPager.LoadResult.READ;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
