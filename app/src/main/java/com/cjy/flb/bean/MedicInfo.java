package com.cjy.flb.bean;

/**
 * Created by Administrator on 2015/12/15 0015.
 * 用于提交数据的 medicine封装
 */
public class MedicInfo {
    private String point_in_time;
    private String eat_time;
    private int quantity;

    public MedicInfo() {
    }

    public MedicInfo(String point_in_time, String eat_time, int quantity) {
        this.point_in_time = point_in_time;
        this.eat_time = eat_time;
        this.quantity = quantity;
    }

    public String getPoint_in_time() {
        return point_in_time;
    }

    public void setPoint_in_time(String point_in_time) {
        this.point_in_time = point_in_time;
    }

    public String getEat_time() {
        return eat_time;
    }

    public void setEat_time(String eat_time) {
        this.eat_time = eat_time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
