package me.suncloud.marrymemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CarSkuItem;
import me.suncloud.marrymemo.widget.FlowLayout;

/**
 * CarSkuItemView 婚车SkuItem的view
 * include TextView and FlowLayout
 * Created by jinxin on 2015/10/9.
 */
public class CarSkuItemView extends LinearLayout {
    private Context mContext;
    private TextView property;
    private FlowLayout layout;
    private int height;
    private LayoutInflater inflater;
    private String propertyValue;
    private List<CarSkuItem> data;
    private OnCarSkuItemOnChildChangeListener onCarSkuItemOnChildChangeListener;

    private FlowLayout.OnChildCheckedChangeListener onChildCheckedChangeListener = new FlowLayout
            .OnChildCheckedChangeListener() {
        @Override
        public void onCheckedChange(View childView, int index) {
            if (onCarSkuItemOnChildChangeListener != null) {
                onCarSkuItemOnChildChangeListener.onCarSkuItemChildChanged(propertyValue,
                        CarSkuItemView.this,
                        layout,
                        childView,
                        index);
            }
        }
    };

    public CarSkuItemView(Context context) {
        this(context, null);
    }

    public CarSkuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarSkuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        height = Math.round(mContext.getResources()
                .getDisplayMetrics().density * 30);
        init();
    }

    private void init() {
        View rootView = inflater.inflate(R.layout.car_sku_item_view, null);
        property = (TextView) rootView.findViewById(R.id.car_sku_item_property);
        layout = (FlowLayout) rootView.findViewById(R.id.car_sku_item_flowlayout);
        layout.setOnChildCheckedChangeListener(onChildCheckedChangeListener);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(rootView, params);
    }

    public void setProperty(String property) {
        this.propertyValue = property;
        this.property.setText(property + ":");
    }


    public void setLayout(List<CarSkuItem> items) {
        for (CarSkuItem item : items) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.sku_choice_view, null);
            checkBox.setText(item.getValue());
            checkBox.setTag(item);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT,
                    height);
            layout.addView2(checkBox, params);
        }
    }

    public int getLayoutChildCount() {
        return layout.getChildCount();
    }

    /**
     * 设置每一个的选中状态 当只有一个规格时候使用这个方法
     *
     * @param items
     * @param checkAble
     */
    public void setLayout(List<CarSkuItem> items, boolean checkAble) {
        for (CarSkuItem item : items) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.sku_choice_view, null);
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

    public void setData(List<CarSkuItem> data) {
        this.data = data;
        setLayoutState(data);
    }

    private void setLayoutState(List<CarSkuItem> data) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            CheckBox box = (CheckBox) layout.getChildAt(i);
            CarSkuItem boxItem = (CarSkuItem) box.getTag();
            if (data == null) {
                box.setEnabled(false);
                continue;
            }
            if (hasCarItem(data, boxItem)) {
                box.setEnabled(true);
            } else {
                box.setEnabled(false);
            }
        }
    }

    private boolean hasCarItem(List<CarSkuItem> items, CarSkuItem item) {
        boolean result = false;
        if (items == null || items.size() <= 0) {
            return false;
        }
        for (CarSkuItem it : items) {
            if (it.getValue_id() == item.getValue_id()) {
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
                String property,
                CarSkuItemView gtoup,
                FlowLayout layout,
                View childView,
                int index);
    }

    public void setOnCarSkuItemOnChildChangeListener(
            OnCarSkuItemOnChildChangeListener onCarSkuItemOnChildChangeListener) {
        this.onCarSkuItemOnChildChangeListener = onCarSkuItemOnChildChangeListener;
    }
}
