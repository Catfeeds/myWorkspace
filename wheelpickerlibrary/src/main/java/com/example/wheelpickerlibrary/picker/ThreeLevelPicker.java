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
 * Created by werther on 15/12/26.
 * 模仿iOS滚筒选择的三级级联菜单选择控件
 */
public class ThreeLevelPicker extends FrameLayout {

    private Context mContext;

    private WheelView wheelView1;
    private WheelView wheelView2;
    private WheelView wheelView3;

    private List<String> l1;
    private List<List<String>> l2;
    private List<List<List<String>>> l3;

    public ThreeLevelPicker(Context context) {
        super(context);
        init(context);
    }

    public ThreeLevelPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ThreeLevelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.three_level_picker, this, true);

        wheelView1 = (WheelView) findViewById(R.id.wheel1);
        wheelView2 = (WheelView) findViewById(R.id.wheel2);
        wheelView3 = (WheelView) findViewById(R.id.wheel3);

        wheelView1.setTextSize(16);
        wheelView2.setTextSize(16);
        wheelView3.setTextSize(16);
        wheelView1.setLoop(false);
        wheelView2.setLoop(false);
        wheelView3.setLoop(false);

        setListeners();
    }

    /**
     * 设置三级级联菜单的数据
     *
     * @param l1
     * @param l2
     * @param l3
     */
    public void setItems(List<String> l1, List<List<String>> l2, List<List<List<String>>> l3) {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        wheelView1.setItems(l1);
        wheelView2.setItems(l2.get(0));
        wheelView3.setItems(l3.get(0)
                .get(0));
    }

    /**
     * 设置三级菜单中的数据和默认选中项目index
     *
     * @param l1
     * @param l2
     * @param l3
     * @param i1
     * @param i2
     * @param i3
     */
    public void setItems(
            List<String> l1,
            List<List<String>> l2,
            List<List<List<String>>> l3,
            int i1,
            int i2,
            int i3) {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        wheelView1.setItems(l1, i1);
        wheelView2.setItems(l2.get(i1), i2);
        wheelView3.setItems(l3.get(i1)
                .get(i2), i3);
    }

    /**
     * 设置三级菜单中选中的index
     *
     * @param i1
     * @param i2
     * @param i3
     */
    public void setCurrentItems(int i1, int i2, int i3) {
        wheelView1.setInitPosition(i1);
        wheelView2.setItems(l2.get(i1));
        wheelView2.setInitPosition(i2);
        wheelView3.setItems(l3.get(i1)
                .get(i2));
        wheelView3.setInitPosition(i3);
    }

    /**
     * 获取当前三级菜单下选中的项目的index
     *
     * @return
     */
    public int[] getCurrentItems() {
        int i[] = new int[3];

        i[0] = wheelView1.getSelectedItem();
        i[1] = wheelView2.getSelectedItem();
        i[2] = wheelView3.getSelectedItem();

        return i;
    }

    private void setListeners() {
        final OnItemSelectedListener listener2 = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (!l3.isEmpty()) {
                    try {
                        if (l3.size() - 1 >= wheelView1.getSelectedItem()) {
                            wheelView3.setItems(l3.get(wheelView1.getSelectedItem())
                                    .get(index));
                            wheelView3.setInitPosition(0);
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        };

        wheelView1.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                index = Math.max(index, 0);
                if (!l2.isEmpty()) {
                    // 先移出监听器
                    wheelView2.removeListener();
                    wheelView2.setItems(l2.get(index));
                    wheelView2.setInitPosition(0);
                }
                if (!l3.isEmpty()) {
                    wheelView3.setItems(l3.get(index)
                            .get(0));
                    wheelView3.setInitPosition(0);
                }
                // 再加入
                wheelView2.setListener(listener2);
            }
        });
        wheelView2.setListener(listener2);
    }

}
