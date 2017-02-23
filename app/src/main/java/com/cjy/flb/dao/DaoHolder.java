package com.cjy.flb.dao;

import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.db.Box;
import com.cjy.flb.db.Eat;
import com.cjy.flb.db.EatTime;
import com.cjy.flb.db.IsNotif;
import com.cjy.flb.db.SetMedicTime;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by jianghw on 2016/4/7 0007.
 * Description 统一数据库
 */
public class DaoHolder {

    /**
     * Long id, String today, String medic_id, String medic_name, String medic_phone,
     * Boolean isMOut,Integer isM, Boolean isNOut, Integer isN,
     * Boolean isAOut, Integer isA, Boolean isEOut, Integer isE
     *
     * @param cond     通过一个字段查询
     * @param condMore 通过多个字段查询
     * @return builder
     */
    public static QueryBuilder<IsNotif> getIsNotifDao(WhereCondition cond, WhereCondition... condMore)
    {
        QueryBuilder<IsNotif> builder = MyApplication.getDaoSession().getIsNotifDao()
                .queryBuilder()
                .where(cond, condMore);
        return builder;
    }

    /**
     * 插入一个新数据
     *
     * @param today
     * @param medic_id
     * @param medic_name
     * @param medic_phone
     */
    public static void insertInTxIsNotif(String today, String medic_id, String medic_name, String medic_phone)
    {
        IsNotif isNotif = new IsNotif(null, today, medic_id, medic_name, medic_phone,
                true, 0, true, 0, true, 0, true, 0);
        MyApplication.getDaoSession().getIsNotifDao().insertInTx(isNotif);
    }

    public static void updateInTxIsNotif(IsNotif is)
    {
        MyApplication.getDaoSession().getIsNotifDao().updateInTx(is);
    }

    public static void deleteAllIsNotif()
    {
        MyApplication.getDaoSession().getIsNotifDao().deleteAll();
    }

    /**
     * Long id, String device_uid, String eat_medicine_time, Integer number, Boolean taken
     *
     * @param cond
     * @param condMore
     * @return
     */
    public static QueryBuilder<Eat> getEatDaoBy(WhereCondition cond, WhereCondition... condMore)
    {
        QueryBuilder<Eat> builder = MyApplication.getDaoSession().getEatDao()
                .queryBuilder()
                .where(cond, condMore);
        return builder;
    }

    /**
     * 更新数据
     *
     * @param eat
     */
    public static void updateInTxEatDao(Eat eat)
    {
        MyApplication.getDaoSession().getEatDao().updateInTx(eat);
    }

    /**
     * 插入数据
     *
     * @param eat
     */
    public static void insertInTxEatDao(Eat eat)
    {
        MyApplication.getDaoSession().getEatDao().insertInTx(eat);
    }

    public static void deleteAllEatDao()
    {
        MyApplication.getDaoSession().getEatDao().deleteAll();
    }

    /**
     * Long id, String number, String device_uid,
     * String medicine, Integer quantity, 药物名，数量
     * Integer medicine_id, String unit, String eat_time
     *
     * @param cond
     * @param condMore
     * @return
     */
    public static QueryBuilder<SetMedicTime> getSetMedicTimeDao(WhereCondition cond, WhereCondition... condMore)
    {
        QueryBuilder<SetMedicTime> builder = MyApplication.getDaoSession().getSetMedicTimeDao()
                .queryBuilder()
                .where(cond, condMore);
        return builder;
    }

    public static void updateInTxSetMedicTime(SetMedicTime setMedicTime)
    {
        MyApplication.getDaoSession().getSetMedicTimeDao().updateInTx(setMedicTime);
    }

    public static void insertInTxSetMedicTime(SetMedicTime setMedicTime)
    {
        MyApplication.getDaoSession().getSetMedicTimeDao().insertInTx(setMedicTime);
    }

    /**
     * Long boxId, String day_in_week, String point_in_time, Integer number
     *
     * @param cond
     * @param condMore
     * @return
     */
    public static QueryBuilder<Box> getBoxDao(WhereCondition cond, WhereCondition... condMore)
    {
        QueryBuilder<Box> builder = MyApplication.getDaoSession().getBoxDao()
                .queryBuilder()
                .where(cond, condMore);
        return builder;
    }

    /**
     * Long id, String medic_id, Long morn, Long non, Long after, Long even,
     * String mornS, String nonS, String afterS, String evenS,
     * String name, String phone
     * @param cond
     * @param condMore
     * @return
     */
    public static QueryBuilder<EatTime> getEatTimeDao(WhereCondition cond, WhereCondition... condMore)
    {
        QueryBuilder<EatTime> builder = MyApplication.getDaoSession().getEatTimeDao()
                .queryBuilder()
                .where(cond, condMore);
        return builder;
    }
}
