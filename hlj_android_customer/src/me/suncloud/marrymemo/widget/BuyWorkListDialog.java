package me.suncloud.marrymemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.LabelFilterAdapter;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * 买套餐用到的dialog
 * Created by jinxin on 2016/12/7 0007.
 */

public class BuyWorkListDialog extends Dialog implements MyScrollView.OnScrollChangedListener {

    @BindView(R.id.et_price_min)
    EditText etPriceMin;
    @BindView(R.id.et_price_max)
    EditText etPriceMax;
    @BindView(R.id.li_content)
    LinearLayout liContent;
    @BindView(R.id.scroll_view)
    MyScrollView scrollView;

    private Context mContext;
    private List<MerchantProperty> propertyList;
    private List<ViewHolder> holders;
    private OnConfirmListener onConfirmListener;
    private InputMethodManager imm;


    public BuyWorkListDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        propertyList = new ArrayList<>();
        holders = new ArrayList<>();
    }

    @Override
    public void setContentView(View view) {
        Point point = JSONUtil.getDeviceSize(mContext);
        ButterKnife.bind(this, view);
        addContentView(view,
                new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, point.y));
        Window win = getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = point.x / 5 * 4;
            params.height = point.y;
            win.setAttributes(params);
            win.setGravity(Gravity.RIGHT);
            win.setWindowAnimations(R.style.dialog_anim_right_in_style);
        }
        scrollView.setOnScrollChangedListener(this);
        hideSoftMethod();
    }

    private void hideSoftMethod() {
        if (etPriceMin.hasFocus()) {
            imm.hideSoftInputFromWindow(etPriceMin.getWindowToken(), 0);
        }
        if (etPriceMax.hasFocus()) {
            imm.hideSoftInputFromWindow(etPriceMax.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.btn_reset)
    public void onReset(View view) {
        etPriceMin.setText(null);
        etPriceMax.setText(null);
        for (ViewHolder h : holders) {
            if (h != null) {
                h.reset();
            }
        }
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirm(View view) {
        if (onConfirmListener != null) {
            HashMap<MerchantProperty, List<? extends Label>> selects = new HashMap<>();
            for (ViewHolder h : holders) {
                if (h.getSelected() != null && !h.getSelected()
                        .isEmpty()) {
                    selects.put(h.getMerchantProperty(), h.getSelected());
                }
            }
            hideSoftMethod();
            String priceMin = etPriceMin.getText()
                    .toString();
            String priceMax = etPriceMax.getText()
                    .toString();
            double min = 0d;
            double max = 0d;
            if (!JSONUtil.isEmpty(priceMin)) {
                min = Double.parseDouble(priceMin);
            }
            if (!JSONUtil.isEmpty(priceMax)) {
                max = Double.parseDouble(priceMax);
            }
            if ((min > max) && (!JSONUtil.isEmpty(priceMin) && !JSONUtil.isEmpty(priceMax))) {
                etPriceMax.setText(priceMin);
                etPriceMin.setText(priceMax);
            }
            onConfirmListener.onConfirm(etPriceMin.getText()
                            .toString(),
                    etPriceMax.getText()
                            .toString(),
                    selects);
            cancel();
        }
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public void setPropertyList(List<MerchantProperty> propertyList) {
        if (propertyList == null) {
            return;
        }

        Iterator<MerchantProperty> iterator = propertyList.iterator();
        while (iterator.hasNext()) {
            MerchantProperty p = iterator.next();
            if (p.getChildren() == null || p.getChildren()
                    .isEmpty()) {
                iterator.remove();
            }
        }

        if (this.propertyList.equals(propertyList)) {
            //数据没有改变
            return;
        }
        this.propertyList.clear();
        this.propertyList.addAll(propertyList);

        liContent.removeAllViews();
        holders.clear();
        for (int i = 0, size = this.propertyList.size(); i < size; i++) {
            boolean showLine = (i == this.propertyList.size() - 1);
            MerchantProperty property = this.propertyList.get(i);
            addTagView(i, property, !showLine);
        }
    }

    private void addTagView(int index, MerchantProperty p, boolean showLine) {
        if (p == null || p.getId() <= 0) {
            return;
        }
        View itemView = getLayoutInflater().inflate(R.layout.buy_work_list_item, null);
        ViewHolder holder = new ViewHolder(itemView);
        holder.line.setVisibility(showLine ? View.VISIBLE : View.GONE);
        holder.setMerchantProperty(p);
        holder.liAll.setOnClickListener(holder);
        holders.add(holder);
        liContent.addView(itemView, index);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        hideSoftMethod();
    }

    class ViewHolder implements View.OnClickListener, LabelFilterAdapter.OnKeyWordClickListener {
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_selected)
        TextView tvSelected;
        @BindView(R.id.li_all)
        LinearLayout liAll;
        @BindView(R.id.grid_view)
        GridView gridView;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.tv_all)
        TextView tvAll;
        @BindView(R.id.img_all)
        ImageView imgAll;
        LabelFilterAdapter adapter;
        int filterWidth;
        ArrayList<MerchantProperty> ps;
        MerchantProperty property;
        boolean clipEd;//展开全部 收起全部
        DisplayMetrics dm;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            Point point = JSONUtil.getDeviceSize(mContext);
            clipEd = false;
            dm = mContext.getResources()
                    .getDisplayMetrics();
            filterWidth = Math.round(point.x / 3 - Util.dp2px(mContext, 16));
            int filterDlgWidth = point.x / 5 * 4;
            filterWidth = (filterDlgWidth - CommonUtil.dp2px(mContext, 28 + 16)) / 3;
            ps = new ArrayList<>();
            adapter = new LabelFilterAdapter(ps, mContext, filterWidth);
            adapter.setMultiSelected(true);
            adapter.setOnKeyWordClickListener(this);
            gridView.setAdapter(adapter);
        }

        /**
         * @param properties
         * @param clip       true 展开全部 clip收起全部
         */
        public void setFilter(List<MerchantProperty> properties, boolean clip) {
            if (properties != null) {
                int rawRow = (int) Math.ceil(properties.size() * 1.0d / 3);//判断 全部按钮 显示与否
                if (rawRow > 2) {
                    liAll.setVisibility(View.VISIBLE);
                } else {
                    liAll.setVisibility(View.GONE);
                }

                ps.clear();
                if (properties.size() <= 6) {
                    //没有展开
                    ps.addAll(properties);
                } else {
                    if (clip) {
                        ps.addAll(properties);
                    } else {
                        ps.addAll(properties.subList(0, 6));
                    }
                }
                int row = (int) Math.ceil(ps.size() * 1.0d / 3);
                gridView.getLayoutParams().height = Math.round(row * dm.density * 27) + Math
                        .round((row - 1) * dm.density * 10 + dm.density * 10);
                adapter.resetHashMap();
                adapter.notifyDataSetChanged();
            }
        }

        public void reset() {
            if (adapter != null) {
                adapter.resetDefault();
                adapter.notifyDataSetChanged();
            }
            tvSelected.setText(null);
        }

        public void setMerchantProperty(MerchantProperty p) {
            if (p == null && p.getId() <= 0) {
                return;
            }
            property = p;
            tvProperty.setText(p.getName());
            setFilter(p.getChildren(), false);
        }

        public List<? extends Label> getSelected() {
            if (adapter != null) {
                return adapter.getChecked();
            }
            return null;
        }

        public MerchantProperty getMerchantProperty() {
            return property;
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.li_all) {
                if (!clipEd) {
                    //状态展开全部 点击收起全部
                    tvAll.setTextColor(mContext.getResources()
                            .getColor(R.color.colorPrimary));
                    imgAll.setImageResource(R.drawable.icon_arrow_up_primary_18_12);
                } else {
                    //状态 收起全部  点击展开全部
                    tvAll.setTextColor(mContext.getResources()
                            .getColor(R.color.colorGray));
                    imgAll.setImageResource(R.drawable.icon_arrow_down_gray_18_12);
                }
                setFilter(property.getChildren(), !clipEd);
                clipEd = !clipEd;
            }
        }

        @Override
        public void onKeyWorkClick(List<? extends Label> labels) {
            String spilt = "，";
            if (labels != null) {
                StringBuilder builder = new StringBuilder();
                for (Label p : labels) {
                    builder.append(p.getName())
                            .append(spilt);
                }
                if (builder.lastIndexOf(spilt) != -1) {
                    builder.deleteCharAt(builder.lastIndexOf(spilt));
                    tvSelected.setText(builder.toString());
                } else {
                    tvSelected.setText(null);
                }
            }
        }
    }

    public interface OnConfirmListener {
        void onConfirm(
                String priceMin,
                String priceMax,
                HashMap<MerchantProperty, List<? extends Label>> select);
    }

}
