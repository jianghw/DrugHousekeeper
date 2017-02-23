package com.cjy.flb.wxapi;

import android.app.Activity;
import android.widget.Toast;

import com.cjy.flb.utils.ToastUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        ToastUtil.show(">xx<", Toast.LENGTH_LONG);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) baseResp).code; //即为所需的code
                ToastUtil.show(">" + code + "<", Toast.LENGTH_LONG);
                break;
            default:
                ToastUtil.show(">特么错了<", Toast.LENGTH_LONG);
        }
    }
}
