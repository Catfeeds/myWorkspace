package com.example.wheelpickerlibrary.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.wheelpickerlibrary.R;
import com.example.wheelpickerlibrary.WheelView;

import java.util.List;

/**
 * Created by werther on 16/3/16.
 */
public class SingleWheelPicker extends FrameLayout {

    private Context mContext;

    private WheelView wheelView1;

    private List<String> l1;

    public SingleWheelPicker(Context context) {
        super(context);
        init(context);
    }

    public SingleWheelPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SingleWheelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.single_level_picker, this, true);

        wheelView1 = (WheelView) findViewById(R.id.wheel1);

        wheelView1.setTextSize(16);
        wheelView1.setLoop(false);
        wheelView1.setZoomable(false);
    }

    /**
     * 设置菜单的数据
     *
     * @param l1
     */
    public void setItems(List<String> l1) {
        this.l1 = l1;
        wheelView1.setItems(l1);
    }

    /**
     * 设置单中的数据和默认选中项目index
     *
     * @param l1
     * @param i1
     */
    public void setItems(List<String> l1, int i1) {
        this.l1 = l1;
        wheelView1.setItems(l1, i1);
    }

    /**
     * 设置菜单选中的index
     *
     * @param i1
     */
    public void setCurrentItems(int i1) {
        wheelView1.setInitPosition(i1);
    }

    /**
     * 获取当前菜单下选中的项目的index
     *
     * @return
     */
    public int getCurrentItem() {
        return wheelView1.getSelectedItem();
    }
}
