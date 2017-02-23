package com.cjy.flb.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/12/18 0018.
 */
public class PullDownMedicine {

    /**
     * number : [0,1,2,3,4,5,6,7,8,9]
     * device_uid : FLB001-000005
     * unit : 粒
     * eat_time : 08:00
     * medicine_list : [{"medicine_id":"阿莫西林1","quantity":1},
     * {"medicine_id":"阿莫西林2","quantity":1},{"medicine_id":"阿莫西林3","quantity":1}]
     */

    private String device_uid;
    private String unit;
    private String eat_time;
    private List<Integer> number;
    /**
     * medicine_id : 阿莫西林1
     * quantity : 1
     */
    private List<MedicineListEntity> medicine_list;



    public void setDevice_uid(String device_uid) {
        this.device_uid = device_uid;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setEat_time(String eat_time) {
        this.eat_time = eat_time;
    }

    public void setNumber(List<Integer> number) {
        this.number = number;
    }

    public void setMedicine_list(List<MedicineListEntity> medicine_list) {
        this.medicine_list = medicine_list;
    }

    public String getDevice_uid() {
        return device_uid;
    }

    public String getUnit() {
        return unit;
    }

    public String getEat_time() {
        return eat_time;
    }

    public List<Integer> getNumber() {
        return number;
    }

    public List<MedicineListEntity> getMedicine_list() {
        return medicine_list;
    }

    public static class MedicineListEntity {
        private int medicine_id;
        private int quantity;

        public void setMedicine_name(int medicine_name) {
            this.medicine_id = medicine_name;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getMedicine_name() {
            return medicine_id;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
