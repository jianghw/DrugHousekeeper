package com.cjy.flb.event;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/12/25 0025.
 */
public class PerodOfDayEvent {
    private String week;
    private HashMap<String, Boolean> hasMap;

    public PerodOfDayEvent() {
    }

    public PerodOfDayEvent(String week, HashMap<String, Boolean> hasMap) {
        this.week = week;
        this.hasMap = hasMap;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public HashMap<String, Boolean> getHasMap() {
        return hasMap;
    }

    public void setHasMap(HashMap<String, Boolean> hasMap) {
        this.hasMap = hasMap;
    }
}
