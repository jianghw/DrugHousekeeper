package com.cjy.flb.domain;

import java.util.List;

/**
 * Created by Administrator on 2015/12/16 0016.
 */
public class BindedFlb {

    /**
     * response : ["FLB001-000009"]
     * count : 1
     */

    private int count;
    private List<String> response;

    public void setCount(int count) {
        this.count = count;
    }

    public void setResponse(List<String> response) {
        this.response = response;
    }

    public int getCount() {
        return count;
    }

    public List<String> getResponse() {
        return response;
    }
}
