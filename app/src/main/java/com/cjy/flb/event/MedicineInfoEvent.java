package com.cjy.flb.event;

/**
 * Created by Administrator on 2015/12/15 0015.
 */
public class MedicineInfoEvent {

    private boolean isTrue;

    public MedicineInfoEvent() {
    }

    public MedicineInfoEvent(boolean b) {
        this.isTrue = b;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setIsTrue(boolean isTrue) {
        this.isTrue = isTrue;
    }
}
