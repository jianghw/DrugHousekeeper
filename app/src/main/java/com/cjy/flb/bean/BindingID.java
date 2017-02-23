package com.cjy.flb.bean;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class BindingID {
    private String url;
    private String name;
    private String MedicId;

    public BindingID() {
    }

    public BindingID(String url, String name, String medicId) {
        this.url = url;
        this.name = name;
        MedicId = medicId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedicId() {
        return MedicId;
    }

    public void setMedicId(String medicId) {
        MedicId = medicId;
    }
}
