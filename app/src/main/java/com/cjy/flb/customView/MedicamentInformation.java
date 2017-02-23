package com.cjy.flb.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cjy.flb.R;


/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class MedicamentInformation extends LinearLayout {


    private Context context;
    private ImageView timeQuantumImageView;
    private ImageView setMedicamentImageView;
    private TextView timeQTextView;
    private TextView timeTextView;

    public MedicamentInformation(Context context) {
        this(context, null, 0);
    }

    public MedicamentInformation(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MedicamentInformation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .MedicamentInformation, defStyleAttr, 0);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.medicament_information, this);

        timeQuantumImageView = (ImageView) view.findViewById(R.id.iv_time_quantum);
        setMedicamentImageView = (ImageView) view.findViewById(R.id.iv_add_medicine);

        timeQTextView = (TextView) view.findViewById(R.id.tv_time_quantum);
        timeTextView = (TextView) view.findViewById(R.id.tv_time);

        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.MedicamentInformation_image_time_quantum:
                    String string = typedArray.getString(attr);
                        switch (string != null ? string : "MORN") {
                            case "MORN":
                                timeQuantumImageView.setImageResource(R.drawable.setf_morning_no);
                                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                                timeQTextView.setText(context.getString(R.string.A_M));
                                break;
                            case "NOON":
                                timeQuantumImageView.setImageResource(R.drawable.setf_nooing_no);
                                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                                timeQTextView.setText(context.getString(R.string.Noon));
                                break;
                            case "AFTERNOON":
                                timeQuantumImageView.setImageResource(R.drawable.setf_afternoon_no);
                                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                                timeQTextView.setText(context.getString(R.string.P_M));
                                break;
                            case "NIGHT":
                                timeQuantumImageView.setImageResource(R.drawable.setf_eveing_no);
                                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                                timeQTextView.setText(context.getString(R.string.bed_time));
                                break;
                        }
                    break;
            }
        }
        typedArray.recycle();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public ImageView getMedicamentImageView() {
        return setMedicamentImageView;
    }

    public void onLongListenerShowPopu() {
        if (setMedicamentImageView.getVisibility() == View.VISIBLE) {
            View contentView = LayoutInflater.from(context).inflate(R.layout
                    .popupwin_listview_item, null);
            TextView textView = (TextView) contentView.findViewById(R.id.textView);
            textView.setText(context.getString(R.string.no_add_medic));
            final PopupWindow popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT
                    , LayoutParams.WRAP_CONTENT, true);
            popupWindow.setTouchable(true);
            popupWindow.setTouchInterceptor(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable
                    .pupowin_list_top));

            int[] location = new int[2];
            setMedicamentImageView.getLocationOnScreen(location);
            //[0]--left,[1]--top
            Rect anchorRect = new Rect(location[0], location[1],
                    location[0] + setMedicamentImageView.getWidth(),
                    location[1] + setMedicamentImageView.getHeight());

      /*      popupWindow.showAtLocation(setMedicamentImageView, Gravity.NO_GRAVITY,
                    anchorRect.left-80,
                    anchorRect.top - (setMedicamentImageView.getHeight() *
                    2+setMedicamentImageView.getHeight() /2));*/

            popupWindow.showAsDropDown(setMedicamentImageView,
                    -setMedicamentImageView.getWidth()*2 ,
                    -setMedicamentImageView.getHeight()*2);

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });
            //刷新状态
            popupWindow.update();
        }
    }

    /**
     * 设置是否有药物
     *
     * @param flag
     */
    public void setTimeQuantumImageView(boolean flag) {
        if (flag) {
            setMedicamentImageView.setVisibility(View.INVISIBLE);
        } else {
            setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
        }
    }

    /**
     * 设置是否有时间，有时间或没设置
     *
     * @param flag
     */
    public void setSetMedicamentImageView(int flag, String time) {
        switch (flag) {
            case 1://上午没设置时间时,但有药物
                timeQuantumImageView.setImageResource(R.drawable.setf_morning_no);
                timeTextView.setText(time);
                setMedicamentImageView.setVisibility(View.GONE);
                break;
            case 2://上午设置时间时
                timeQuantumImageView.setImageResource(R.drawable.setf_morning);
                setMedicamentImageView.setVisibility(View.GONE);
                timeTextView.setText(time);
                break;
            case 3://中午
                timeQuantumImageView.setImageResource(R.drawable.setf_nooing_no);
                timeTextView.setText(time);
                setMedicamentImageView.setVisibility(View.GONE);
                break;
            case 4:
                timeQuantumImageView.setImageResource(R.drawable.setf_nooing);
                setMedicamentImageView.setVisibility(View.GONE);
                timeTextView.setText(time);
                break;
            case 5://下午
                timeQuantumImageView.setImageResource(R.drawable.setf_afternoon_no);
                timeTextView.setText(time);
                setMedicamentImageView.setVisibility(View.GONE);
                break;
            case 6:
                timeQuantumImageView.setImageResource(R.drawable.setf_afternoon);
                setMedicamentImageView.setVisibility(View.GONE);
                timeTextView.setText(time);
                break;
            case 7://晚上
                timeQuantumImageView.setImageResource(R.drawable.setf_eveing_no);
                timeTextView.setText(time);
                setMedicamentImageView.setVisibility(View.GONE);
                break;
            case 8:
                timeQuantumImageView.setImageResource(R.drawable.setf_eveing);
                setMedicamentImageView.setVisibility(View.GONE);
                timeTextView.setText(time);
                break;
        }
    }

    /**
     * 设置吃药时间
     *
     * @param time
     */
    public void setTimeTextView(String time) {
        timeTextView.setText(time);
    }

    /**
     * 初始化没设置时间时
     *
     * @param string 时间段
     */
    public void setNeverAddMedicament(String string) {
        switch (string) {
            case "MORN":
                setMedicamentImageView.setVisibility(View.VISIBLE);
                timeQuantumImageView.setImageResource(R.drawable.setf_morning_no);
                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                timeTextView.setText(R.string.set_time);
                break;
            case "NOON":
                setMedicamentImageView.setVisibility(View.VISIBLE);
                timeQuantumImageView.setImageResource(R.drawable.setf_nooing_no);
                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                timeTextView.setText(R.string.set_time);
                break;
            case "AFTERNOON":
                setMedicamentImageView.setVisibility(View.VISIBLE);
                timeQuantumImageView.setImageResource(R.drawable.setf_afternoon_no);
                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                timeTextView.setText(R.string.set_time);
                break;
            case "NIGHT":
                setMedicamentImageView.setVisibility(View.VISIBLE);
                timeQuantumImageView.setImageResource(R.drawable.setf_eveing_no);
                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                timeTextView.setText(R.string.set_time);
                break;
        }
    }
}
