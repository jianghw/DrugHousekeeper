package com.cjy.flb.bean;

/**
 * @author lvzhongyi
 *         <p>
 *         description
 *         date 16/3/1
 *         email lvzhongyiforchrome@gmail.com
 *         </p>
 */
public class QQLoginBean {

    /**
     * ret : 0
     * pay_token : xxxxxxxxxxxxxxxx
     * pf : openmobile_android
     * expires_in : 7776000
     * openid : xxxxxxxxxxxxxxxxxxx
     * pfkey : xxxxxxxxxxxxxxxxxxx
     * msg : sucess
     * access_token : xxxxxxxxxxxxxxxxxxxxx
     */

    private int ret;
    private String pay_token;
    private String pf;
    private String expires_in;
    private String openid;
    private String pfkey;
    private String msg;
    private String access_token;

    @Override
    public String toString() {
        return "QQLoginBean{" +
                "ret=" + ret +
                ", pay_token='" + pay_token + '\'' +
                ", pf='" + pf + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", openid='" + openid + '\'' +
                ", pfkey='" + pfkey + '\'' +
                ", msg='" + msg + '\'' +
                ", access_token='" + access_token + '\'' +
                '}';
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setPay_token(String pay_token) {
        this.pay_token = pay_token;
    }

    public void setPf(String pf) {
        this.pf = pf;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setPfkey(String pfkey) {
        this.pfkey = pfkey;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getRet() {
        return ret;
    }

    public String getPay_token() {
        return pay_token;
    }

    public String getPf() {
        return pf;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getOpenid() {
        return openid;
    }

    public String getPfkey() {
        return pfkey;
    }

    public String getMsg() {
        return msg;
    }

    public String getAccess_token() {
        return access_token;
    }
}
