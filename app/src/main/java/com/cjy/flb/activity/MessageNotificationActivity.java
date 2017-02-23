package com.cjy.flb.activity;

import android.os.Bundle;

import com.cjy.flb.R;
import com.cjy.flb.customView.SlideSwitch;
import com.cjy.flb.utils.SharedPreUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class MessageNotificationActivity extends BaseActivity {

    @Bind(R.id.switch_inform)
    SlideSwitch mSwitchInform;
    @Bind(R.id.switch_sound)
    SlideSwitch mSwitchSound;
    @Bind(R.id.switch_shock)
    SlideSwitch mSwitchShock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView()
    {
        setContentView(R.layout.activity_message_notification);
        ButterKnife.bind(this);
    }

    @Override
    public void initData()
    {
        initBar(getString(R.string.message_notification), false, false);

        if (SharedPreUtil.getBoolean("message_notification", true)) {
            mSwitchInform.setState(true);
        } else {
            mSwitchInform.setState(false);
        }
        if (SharedPreUtil.getBoolean("message_voice", true)) {
            mSwitchSound.setState(true);
        } else {
            mSwitchSound.setState(false);
        }
        if (SharedPreUtil.getBoolean("message_vibration", true)) {
            mSwitchShock.setState(true);
        } else {
            mSwitchShock.setState(false);
        }
    }

    @Override
    public void initListener()
    {
        mSwitchInform.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open()
            {
                SharedPreUtil.setBoolean("message_notification", true);
            }

            @Override
            public void close()
            {
                SharedPreUtil.setBoolean("message_notification", false);
            }
        });
        mSwitchSound.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open()
            {
                SharedPreUtil.setBoolean("message_voice", true);
            }

            @Override
            public void close()
            {
                SharedPreUtil.setBoolean("message_voicen", false);
            }
        });
        mSwitchShock.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open()
            {
                SharedPreUtil.setBoolean("message_vibration", true);
            }

            @Override
            public void close()
            {
                SharedPreUtil.setBoolean("message_vibration", false);
            }
        });


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
