package com.cjy.flb.bean;

/**
 * Created by Administrator on 2015/12/21 0021.
 * 按日期查询服药记录
 */
public class DateQueryMedic {

    /**
     * device_id : 1
     * id : 1
     * medicine_id : 1
     * number : 2
     * taken : true
     * setting_time : 08:00
     * eat_medicine_time : 2015-12-18T09:50:38.000Z
     * name : 阿莫西林1
     * unit : 粒
     * quantity : 1
     * created_at : 2015-12-18T07:02:20.966Z
     * updated_at : 2015-12-18T07:02:20.966Z
     */

    private int device_id;
    private int id;
    private int medicine_id;
    private int number;
    private boolean taken;
    private String setting_time;
    private String eat_medicine_time;
    private String name;
    private String unit;
    private int quantity;
    private String created_at;
    private String updated_at;

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMedicine_id(int medicine_id) {
        this.medicine_id = medicine_id;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public void setSetting_time(String setting_time) {
        this.setting_time = setting_time;
    }

    public void setEat_medicine_time(String eat_medicine_time) {
        this.eat_medicine_time = eat_medicine_time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getDevice_id() {
        return device_id;
    }

    public int getId() {
        return id;
    }

    public int getMedicine_id() {
        return medicine_id;
    }

    public int getNumber() {
        return number;
    }

    public boolean isTaken() {
        return taken;
    }

    public String getSetting_time() {
        return setting_time;
    }

    public String getEat_medicine_time() {
        return eat_medicine_time;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
