package com.example.wheelpickerlibrary.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.wheelpickerlibrary.OnItemSelectedListener;
import com.example.wheelpickerlibrary.R;
import com.example.wheelpickerlibrary.WheelView;

import java.util.List;

/**
 * Created by werther on 16/3/14.
 * 模仿iOS滚筒选择的二级级联菜单选择控件
 */
public class TwoLevelPicker extends FrameLayout {
    private Context mContext;

    private WheelView wheelView1;
    private WheelView wheelView2;

    private List<String> l1;
    private List<List<String>> l2;

    public TwoLevelPicker(Context context) {
        super(context);
        init(context);
    }

    public TwoLevelPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TwoLevelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.two_level_picker, this, true);

        wheelView1 = (WheelView) findViewById(R.id.wheel1);
        wheelView2 = (WheelView) findViewById(R.id.wheel2);

        wheelView1.setTextSize(16);
        wheelView2.setTextSize(16);
        wheelView1.setLoop(false);
        wheelView2.setLoop(false);

        setListeners();
    }

    /**
     * 设置二级级联菜单的数据
     *
     * @param l1
     * @param l2
     */
    public void setItems(List<String> l1, List<List<String>> l2) {
        this.l1 = l1;
        this.l2 = l2;
        wheelView1.setItems(l1);
        wheelView2.setItems(l2.get(0));
    }

    /**
     * 设置二级菜单中的数据和默认选中项目index
     * @param l1
     * @param l2
     * @param i1
     * @param i2
     */
    public void setItems(List<String> l1, List<List<String>> l2, int i1, int i2) {
        this.l1 = l1;
        this.l2 = l2;
        wheelView1.setItems(l1, i1);
        wheelView2.setItems(l2.get(i1), i2);
    }

    /**
     * 设置二级菜单选中的index
     * @param i1
     * @param i2
     */
    public void setCurrentItems(int i1, int i2) {
        wheelView1.setInitPosition(i1);
        wheelView2.setItems(l2.get(i1));
        wheelView2.setInitPosition(i2);
    }

    /**
     * 获取当前二级菜单下选中的项目的index
     * @return
     */
    public int[] getCurrentItems() {
        int i[] = new int[2];

        i[0] = wheelView1.getSelectedItem();
        i[1] = wheelView2.getSelectedItem();

        return i;
    }

    private void setListeners() {
        wheelView1.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (!l2.isEmpty()) {
                    // 先移出监听器
                    wheelView2.setItems(l2.get(index));
                    wheelView2.setInitPosition(0);
                }
            }
        });
    }
}
