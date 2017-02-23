package com.cjy.flb.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjy.flb.R;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class setMedicamentInformation extends LinearLayout {


    private ImageView timeQuantumImageView;
    private ImageView setMedicamentImageView;
    private TextView timeQTextView;
    private TextView timeTextView;

    public setMedicamentInformation(Context context) {
        this(context, null, 0);
    }

    public setMedicamentInformation(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public setMedicamentInformation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.setMedicamentInformation, defStyleAttr, 0);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.set_medicament_information, this);

        timeQuantumImageView = (ImageView) view.findViewById(R.id.imageView_time_quantum);
        setMedicamentImageView = (ImageView) view.findViewById(R.id.imageView_set_medicament);

        timeQTextView = (TextView) view.findViewById(R.id.textView_time_quantum);
        timeTextView = (TextView) view.findViewById(R.id.textView_time);

        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.setMedicamentInformation_image_time:
                    String string = typedArray.getString(attr);
                    switch (string) {
                        case "MORN":
                            timeQuantumImageView.setImageResource(R.drawable.setf_morning_no);
                            setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                            timeQTextView.setText(R.string.A_M);
                            break;
                        case "NOON":
                            timeQuantumImageView.setImageResource(R.drawable.setf_nooing_no);
                            setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                            timeQTextView.setText(R.string.Noon);
                            break;
                        case "AFTERNOON":
                            timeQuantumImageView.setImageResource(R.drawable.setf_afternoon_no);
                            setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                            timeQTextView.setText(R.string.P_M);
                            break;
                        case "NIGHT":
                            timeQuantumImageView.setImageResource(R.drawable.setf_eveing_no);
                            setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                            timeQTextView.setText(R.string.bed_time);
                            break;
                    }
                    break;
                case R.styleable.setMedicamentInformation_tv_time_quantum:
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

    /**
     * 设置是否有药物
     *
     * @param flag
     */
    public void setTimeQuantumImageView(boolean flag) {
        if (flag) {
            setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
        } else {
            setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
        }
    }

    /**
     * 设置是否有时间
     *
     * @param flag
     */
    public void setSetMedicamentImageView(int flag) {
        switch (flag) {
            case 1://上午没设置时间时
                timeQuantumImageView.setImageResource(R.drawable.setf_morning_no);
                break;
            case 2://上午设置时间时
                timeQuantumImageView.setImageResource(R.drawable.setf_morning);
                break;
            case 3://中午
                timeQuantumImageView.setImageResource(R.drawable.setf_nooing_no);
                break;
            case 4:
                timeQuantumImageView.setImageResource(R.drawable.setf_nooing);
                break;
            case 5://下午
                timeQuantumImageView.setImageResource(R.drawable.setf_afternoon_no);
                break;
            case 6:
                timeQuantumImageView.setImageResource(R.drawable.setf_afternoon);
                break;
            case 7://晚上
                timeQuantumImageView.setImageResource(R.drawable.setf_eveing_no);
                break;
            case 8:
                timeQuantumImageView.setImageResource(R.drawable.setf_eveing);
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

    public void setNeverAddMedicament(String string) {
        switch (string) {
            case "MORN":
                timeQuantumImageView.setImageResource(R.drawable.setf_morning_no);
                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                timeTextView.setText(R.string.set_time);
                break;
            case "NOON":
                timeQuantumImageView.setImageResource(R.drawable.setf_nooing_no);
                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                timeTextView.setText(R.string.set_time);
                break;
            case "AFTERNOON":
                timeQuantumImageView.setImageResource(R.drawable.setf_afternoon_no);
                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                timeTextView.setText(R.string.set_time);
                break;
            case "NIGHT":
                timeQuantumImageView.setImageResource(R.drawable.setf_eveing_no);
                setMedicamentImageView.setImageResource(R.drawable.set_medicament_no);
                timeTextView.setText(R.string.set_time);
                break;
        }
    }
}
