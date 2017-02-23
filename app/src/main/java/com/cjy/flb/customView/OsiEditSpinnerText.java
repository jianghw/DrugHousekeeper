package com.cjy.flb.customView;

import android.content.Context;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.utils.CommonAdapter;
import com.cjy.flb.utils.ViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义的EditText布局
 * 用于登陆注册等
 * Created by Administrator on 2015/11/26 0026.
 */
public class OsiEditSpinnerText extends RelativeLayout implements View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {
    private Context context;
    private Spinner spiTitle;
    private EditText etContent;
    private ImageView imgLine;
    private CheckBox cbTail;
    private ImageView imgSearch;
    private Button btnClock;

    public OsiEditSpinnerText(Context context) {
        this(context, null, 0);
    }

    public OsiEditSpinnerText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OsiEditSpinnerText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initListener();
    }

    public void setSpinnerDatas(Context context, List<String> spinnerDatas) {
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(context, spinnerDatas, R.layout.edittext_spinner_osi_item);
        spiTitle.setAdapter(spinnerAdapter);
    }

    public class SpinnerAdapter extends CommonAdapter<String> {

        public SpinnerAdapter(Context context, List<String> mDatas, int itemLayoutId) {
            super(mDatas, context, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, String item, int positon) {
            helper.getTextView(R.id.tv_title_item).setText(mDatas.get(positon) + "");
        }
    }


    /**
     * 初始化布局
     */
    public void initView() {
        View.inflate(getContext(), R.layout.edittext_spinner_osi, this);
        spiTitle = (Spinner) findViewById(R.id.spi_title);
        etContent = (EditText) findViewById(R.id.et_content);
        cbTail = (CheckBox) findViewById(R.id.cb_tail);
        imgLine = (ImageView) findViewById(R.id.img_focus);
        imgSearch = (ImageView) findViewById(R.id.imag_search);
        btnClock = (Button) findViewById(R.id.btn_clock);
        cbTail.setVisibility(View.GONE);
    }

    private void initListener() {
        etContent.setOnFocusChangeListener(this);
        cbTail.setOnCheckedChangeListener(this);
    }

    /**
     * 设置Title的值
     *
     * @param title
     */
    public void setTitle(String title) {
//        spiTitle.setText(title);
    }

    public void setImagViewVisib() {
        imgSearch.setVisibility(View.VISIBLE);
    }

    public void setEtContent(String content) {
        etContent.setText(content);
    }

    /**
     * 设置EditText的hint
     *
     * @param hint
     */
    public void setContentHint(String hint) {
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(hint);
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        etContent.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
        //        etContent.setHint(hint);
    }

    public String getTitleValue() {
        return spiTitle.getSelectedItem().toString();
    }

    /**
     * 得到EditText输入的文字
     *
     * @return
     */
    public String getEditText() {
        return etContent.getText().toString().trim();
    }

    public EditText getEtContentOnly() {
        return etContent;
    }

    /**
     * 初始化Tail的图标,同时设置输入框为密码隐藏状态
     */
    public void initTail() {
        cbTail.setVisibility(View.VISIBLE);
        etContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            imgLine.setBackgroundColor(context.getResources().getColor(R.color.btn_green_normal));
            setLine(1);
        } else {
            imgLine.setBackgroundColor(context.getResources().getColor(R.color.et_Line));
            setLine(1);
        }
    }

    /**
     * 设置底部的线条颜色大小变化
     *
     * @param line
     */
    private void setLine(int line) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imgLine.getLayoutParams();
        layoutParams.height = line;
        imgLine.setLayoutParams(layoutParams);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            etContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        } else {
            etContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    /**
     * 添加TvTitle隐藏
     */
    public void setTvTitleToGone() {
//        tvTitle.setVisibility(View.GONE);
    }

    /**
     * 显示错误
     *
     * @param err
     */
    public void showError(String err) {
        etContent.setError(err);
    }
}
