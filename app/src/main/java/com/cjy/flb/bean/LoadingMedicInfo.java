package com.cjy.flb.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/15 0015.
 * 用于上传药物信息
 */
public class LoadingMedicInfo {

    private String device_uid;
    private String medicine_name;
    private String unit = "粒";
    private String expire_date="2015-12-12";
    private String[] day_in_week;
    private ArrayList<MedicInfo> list;

    public LoadingMedicInfo() {
    }

    public LoadingMedicInfo(String device_uid, String medicine_name, String unit, String expire_date,
                            String[] day_in_week, ArrayList<MedicInfo> list) {
        this.device_uid = device_uid;
        this.medicine_name = medicine_name;
        this.unit = unit;
        this.expire_date = expire_date;
        this.day_in_week = day_in_week;
        this.list = list;
    }

    public String getDevice_uid() {
        return device_uid;
    }

    public void setDevice_uid(String device_uid) {
        this.device_uid = device_uid;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String[] getDay_in_week() {
        return day_in_week;
    }

    public void setDay_in_week(String[] day_in_week) {
        this.day_in_week = day_in_week;
    }

    public ArrayList<MedicInfo> getList() {
        return list;
    }

    public void setList(ArrayList<MedicInfo> list) {
        this.list = list;
    }
}
