package com.cjy.flb.domain;

/**
 * Created by Administrator on 2015/12/9 0009.
 */
public class TokenBean {
    /**
     * access_token是以后用来访问API接口的token
     */
    private String access_token;
    /**
     *再次获取access_token
     */
    private String refresh_token;
    private String token_type;
    /**
     * expires_in是这个access_token的有效时长（秒）
     */
    private int expires_in;
    private long created_at;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
