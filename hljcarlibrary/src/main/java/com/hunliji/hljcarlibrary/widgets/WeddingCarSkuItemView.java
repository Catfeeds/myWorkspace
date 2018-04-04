package com.hunliji.hljcarlibrary.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSkuItem;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;

import java.util.List;

/**
 * CarSkuItemView 婚车SkuItem的view
 * include TextView and FlowLayout
 * Created by jinxin on 2015/10/9.
 */
public class WeddingCarSkuItemView extends LinearLayout {
    private Context mContext;
    private TextView property;
    private FlowLayout layout;
    private int height;
    private LayoutInflater inflater;
    private String propertyValue;
    private OnCarSkuItemOnChildChangeListener onCarSkuItemOnChildChangeListener;

    private FlowLayout.OnChildCheckedChangeListener onChildCheckedChangeListener = new FlowLayout
            .OnChildCheckedChangeListener() {
        @Override
        public void onCheckedChange(View childView, int index) {
            if (onCarSkuItemOnChildChangeListener != null) {
                onCarSkuItemOnChildChangeListener.onCarSkuItemChildChanged(propertyValue,
                        WeddingCarSkuItemView.this,
                        layout,
                        childView,
                        index);
            }
        }
    };

    public WeddingCarSkuItemView(Context context) {
        this(context, null);
    }

    public WeddingCarSkuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeddingCarSkuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        height = Math.round(mContext.getResources()
                .getDisplayMetrics().density * 30);
        init();
    }

    private void init() {
        View rootView = inflater.inflate(R.layout.car_sku_item_view___car, null);
        property = (TextView) rootView.findViewById(R.id.car_sku_item_property);
        layout = (FlowLayout) rootView.findViewById(R.id.flow_layout);
        layout.setOnChildCheckedChangeListener(onChildCheckedChangeListener);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(rootView,params);
    }

    public void setProperty(String property) {
        this.propertyValue = property;
        this.property.setText(property + ":");
    }


    public void setLayout(List<WeddingCarSkuItem> items) {
        for (WeddingCarSkuItem item : items) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.sku_choice_view___car, null);
            checkBox.setText(item.getValue());
            checkBox.setTag(item);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT,
                    height);
            layout.addView2(checkBox, params);
        }
    }

    /**
     * 设置每一个的选中状态 当只有一个规格时候使用这个方法
     *
     * @param items
     * @param checkAble
     */
    public void setLayout(List<WeddingCarSkuItem> items, boolean checkAble) {
        for (WeddingCarSkuItem item : items) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.sku_choice_view___car, null);
            checkBox.setText(item.getValue());
            checkBox.setTag(item);
            checkBox.setChecked(checkAble);
            //只有一个规格的时候 默认选中 不可取消选中状态
            checkBox.setEnabled(false);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT,
                    height);
            layout.addView2(checkBox, params);
        }
    }

    public void setData(List<WeddingCarSkuItem> data) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            CheckBox box = (CheckBox) layout.getChildAt(i);
            WeddingCarSkuItem boxItem = (WeddingCarSkuItem) box.getTag();
            if (hasCarItem(data, boxItem)) {
                box.setEnabled(true);
            } else {
                box.setEnabled(false);
            }
        }
    }


    private boolean hasCarItem(List<WeddingCarSkuItem> items, WeddingCarSkuItem item) {
        boolean result = false;
        if (items == null || items.size() <= 0) {
            return false;
        }
        for (WeddingCarSkuItem it : items) {
            if (it.getValueId() == item.getValueId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    public String getProperty() {
        return propertyValue;
    }

    public FlowLayout getLayout() {
        return layout;
    }

    public interface OnCarSkuItemOnChildChangeListener {
        void onCarSkuItemChildChanged(
                String property, WeddingCarSkuItemView group, FlowLayout layout, View childView, int
                index);
    }

    public void setOnCarSkuItemOnChildChangeListener(
            OnCarSkuItemOnChildChangeListener onCarSkuItemOnChildChangeListener) {
        this.onCarSkuItemOnChildChangeListener = onCarSkuItemOnChildChangeListener;
    }
}
