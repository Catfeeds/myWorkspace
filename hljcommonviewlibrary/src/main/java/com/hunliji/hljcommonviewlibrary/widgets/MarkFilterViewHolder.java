package com.hunliji.hljcommonviewlibrary.widgets;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.GestureScrollView;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.WorkLabelFilterAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 套餐筛选
 * Created by mo_yu on 2017/8/2 0007.
 */

public class MarkFilterViewHolder implements GestureScrollView.OnScrollChangedListener {

    public static final int WORK_FILTER = 0;
    public static final int PRODUCT_FILTER = 1;

    @BindView(R2.id.et_price_min)
    EditText etPriceMin;
    @BindView(R2.id.et_price_max)
    EditText etPriceMax;
    @BindView(R2.id.li_content)
    LinearLayout liContent;
    @BindView(R2.id.scroll_view)
    GestureScrollView scrollView;
    @BindView(R2.id.menu_bg_layout)
    FrameLayout menuBgLayout;
    @BindView(R2.id.menu_info_layout)
    RelativeLayout menuInfoLayout;
    @BindView(R2.id.price_filter_layout)
    LinearLayout priceFilterLayout;
    @BindView(R2.id.root_layout)
    View rootLayout;
    @BindView(R2.id.edit_confirm_view)
    RelativeLayout editConfirmView;
    @BindView(R2.id.bottom_action_layout)
    LinearLayout bottomActionLayout;

    private Context mContext;
    private View rootView;
    private int type;
    private boolean isShow;
    private List<CategoryMark> properties;
    private List<ViewHolder> holders;
    private OnConfirmListener onConfirmListener;
    private InputMethodManager imm;

    public static MarkFilterViewHolder newInstance(
            Context context, int type, OnConfirmListener listener) {
        View view = View.inflate(context, R.layout.service_mark_filter_view___cv, null);
        MarkFilterViewHolder holder = new MarkFilterViewHolder(context, view, type, listener);
        holder.init();
        return holder;
    }

    public MarkFilterViewHolder(Context context, View view, int type, OnConfirmListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.type = type;
        this.onConfirmListener = listener;
        ButterKnife.bind(this, view);
    }

    public void init() {
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        properties = new ArrayList<>();
        holders = new ArrayList<>();
        scrollView.setOnScrollChangedListener(this);
        if (type == WORK_FILTER) {
            priceFilterLayout.setVisibility(View.VISIBLE);
        } else if (type == PRODUCT_FILTER) {
            priceFilterLayout.setVisibility(View.GONE);
        }
        rootLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                Activity activity = (Activity) mContext;
                int height = activity.getWindow()
                        .getDecorView()
                        .getHeight();
                boolean immIsShow = (double) (bottom - top) / height < 0.8;
                if (immIsShow) {
                    editConfirmView.setVisibility(View.VISIBLE);
                    bottomActionLayout.setVisibility(View.GONE);
                } else {
                    editConfirmView.setVisibility(View.GONE);
                    bottomActionLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hideSoftMethod() {
        if (etPriceMin.hasFocus()) {
            imm.hideSoftInputFromWindow(etPriceMin.getWindowToken(), 0);
        }
        if (etPriceMax.hasFocus()) {
            imm.hideSoftInputFromWindow(etPriceMax.getWindowToken(), 0);
        }
    }

    public View getRootView() {
        return rootView;
    }

    public boolean isShow() {
        return isShow;
    }

    public void showMarkFilterView() {
        if (CommonUtil.isCollectionEmpty(properties)) {
            return;
        }
        showMenuAnimation();
    }

    public void resetFilter(
            double minPrice, double maxPrice, SparseArray<List<CategoryMark>> selectMarks) {
        if (CommonUtil.isCollectionEmpty(holders)) {
            return;
        }
        etPriceMin.setText(minPrice != -1 ? String.valueOf(minPrice) : null);
        etPriceMax.setText(maxPrice != -1 ? String.valueOf(maxPrice) : null);
        resetMarks(selectMarks);
    }

    public void resetMarks(SparseArray<List<CategoryMark>> selectMarks) {
        if (CommonUtil.isCollectionEmpty(holders)) {
            return;
        }
        if (selectMarks != null && selectMarks.size() > 0) {
            for (int i = 0; i < selectMarks.size(); i++) {
                try {
                    int position = selectMarks.keyAt(i);
                    List<CategoryMark> makes = selectMarks.valueAt(i);
                    holders.get(position)
                            .resetWithMarks(makes);
                } catch (Exception ignored) {
                }
            }
        } else {
            for (int i = 0; i < holders.size(); i++) {
                holders.get(i)
                        .reset();
            }
        }
    }

    @OnClick(R2.id.btn_reset)
    public void onReset() {
        etPriceMin.setText(null);
        etPriceMax.setText(null);
        for (ViewHolder h : holders) {
            if (h != null) {
                h.reset();
            }
        }
    }

    @OnClick(R2.id.tv_confirm)
    public void onPriceConfirm() {
        hideSoftMethod();
    }

    @OnClick(R2.id.btn_confirm)
    public void onConfirm() {
        if (onConfirmListener != null) {
            SparseArray<List<CategoryMark>> selects = new SparseArray<>();
            for (int i = 0; i < holders.size(); i++) {
                ViewHolder viewHolder = holders.get(i);
                if (viewHolder.getSelected() != null && !viewHolder.getSelected()
                        .isEmpty()) {
                    selects.put(i, viewHolder.getSelected());
                }
            }
            if (type == WORK_FILTER) {
                hideSoftMethod();
                String priceMin = etPriceMin.getText()
                        .toString();
                String priceMax = etPriceMax.getText()
                        .toString();
                double min = 0d;
                double max = 0d;
                if (!TextUtils.isEmpty(priceMin)) {
                    min = Double.parseDouble(priceMin);
                }
                if (!TextUtils.isEmpty(priceMax)) {
                    max = Double.parseDouble(priceMax);
                }
                if ((min > max) && (!TextUtils.isEmpty(priceMin) && !TextUtils.isEmpty(priceMax))) {
                    etPriceMax.setText(priceMin);
                    etPriceMin.setText(priceMax);
                }
                if (onConfirmListener != null) {
                    String minPriceStr = etPriceMin.getText()
                            .toString();
                    String maxPriceStr = etPriceMax.getText()
                            .toString();
                    if (!TextUtils.isEmpty(minPriceStr)) {
                        min = Double.parseDouble(minPriceStr);
                    } else {
                        min = -1;
                    }
                    if (!TextUtils.isEmpty(maxPriceStr)) {
                        max = Double.parseDouble(maxPriceStr);
                    } else {
                        max = -1;
                    }
                    hideMenuAnimation();
                    onConfirmListener.onConfirm(min, max, selects);
                }
            } else {
                hideMenuAnimation();
                onConfirmListener.onConfirm(selects);
            }
        }
    }

    @OnClick(R2.id.menu_bg_layout)
    public void onMenuBgLayout() {
        hideMenuAnimation();
    }

    public void setProperties(List<CategoryMark> properties) {
        if (properties == null) {
            return;
        }
        Iterator<CategoryMark> iterator = properties.iterator();
        while (iterator.hasNext()) {
            CategoryMark p = iterator.next();
            if (p.getChildren() == null || p.getChildren()
                    .isEmpty()) {
                iterator.remove();
            }
        }

        if (this.properties.equals(properties)) {
            //数据没有改变
            return;
        }
        this.properties.clear();
        this.properties.addAll(properties);

        liContent.removeAllViews();
        holders.clear();
        for (int i = 0, size = this.properties.size(); i < size; i++) {
            boolean showLine = (i == this.properties.size() - 1);
            CategoryMark property = this.properties.get(i);
            addTagView(i, property, !showLine);
        }
    }

    private void addTagView(int index, CategoryMark property, boolean showLine) {
        if (property == null) {
            return;
        }
        View itemView = View.inflate(mContext, R.layout.service_mark_list_item___cv, null);
        ViewHolder holder = new ViewHolder(itemView);
        holder.line.setVisibility(showLine ? View.VISIBLE : View.GONE);
        holder.imgAll.setOnClickListener(holder);
        holder.setWorkLabel(property);
        holders.add(holder);
        liContent.addView(itemView, index);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        hideSoftMethod();
    }

    class ViewHolder implements View.OnClickListener {
        @BindView(R2.id.tv_property)
        TextView tvProperty;
        @BindView(R2.id.tv_selected)
        TextView tvSelected;
        @BindView(R2.id.grid_view)
        GridView gridView;
        @BindView(R2.id.line)
        View line;
        @BindView(R2.id.img_all)
        ImageView imgAll;
        WorkLabelFilterAdapter adapter;
        int filterWidth;
        ArrayList<CategoryMark> workLabels;
        CategoryMark workLabel;
        boolean clipEd;//展开全部 收起全部
        DisplayMetrics dm;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            clipEd = false;
            dm = mContext.getResources()
                    .getDisplayMetrics();
            filterWidth = (CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext,
                    44)) / 4;
            workLabels = new ArrayList<>();
            adapter = new WorkLabelFilterAdapter(workLabels, mContext, filterWidth);
            adapter.setMultiSelected(type == WORK_FILTER);
            gridView.setAdapter(adapter);
        }

        /**
         * @param properties
         * @param clip       true 展开全部 clip收起全部
         */
        public void setFilter(List<CategoryMark> properties, boolean clip) {
            if (properties != null) {
                int rawRow = (int) Math.ceil(properties.size() * 1.0d / 4);//判断 全部按钮 显示与否
                if (rawRow > 2) {
                    imgAll.setVisibility(View.VISIBLE);
                } else {
                    imgAll.setVisibility(View.GONE);
                }

                workLabels.clear();
                if (properties.size() <= 8) {
                    //没有展开
                    workLabels.addAll(properties);
                } else {
                    if (clip) {
                        workLabels.addAll(properties);
                    } else {
                        workLabels.addAll(properties.subList(0, 8));
                    }
                }
                int row = (int) Math.ceil(workLabels.size() * 1.0d / 4);
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

        public void resetWithMarks(List<CategoryMark> marks) {
            if (adapter != null) {
                if (CommonUtil.isCollectionEmpty(marks)) {
                    adapter.resetDefault();
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.resetWithMarks(marks);
                    adapter.notifyDataSetChanged();
                }
            }
        }

        public void setWorkLabel(CategoryMark p) {
            if (p == null) {
                return;
            }
            workLabel = p;
            tvProperty.setText(p.getMark()
                    .getName());
            setFilter(p.getChildren(), false);
        }

        public List<CategoryMark> getSelected() {
            if (adapter != null) {
                return adapter.getChecked();
            }
            return null;
        }

        public CategoryMark getWorkLabel() {
            return workLabel;
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.img_all) {
                if (!clipEd) {
                    //状态展开全部 点击收起全部
                    imgAll.setImageResource(R.mipmap.icon_arrow_up_primary_26_14);
                } else {
                    //状态 收起全部  点击展开全部
                    imgAll.setImageResource(R.mipmap.icon_arrow_down_gray_26_14);
                }
                setFilter(workLabel.getChildren(), !clipEd);
                clipEd = !clipEd;
            }
        }
    }

    public interface OnConfirmListener {
        void onConfirm(double minPrice, double maxPrice, SparseArray<List<CategoryMark>> selects);

        void onConfirm(SparseArray<List<CategoryMark>> tags);
    }

    private void showMenuAnimation() {
        isShow = true;
        menuBgLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_up);
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (menuBgLayout != null) {
                    menuBgLayout.setBackgroundResource(R.color.transparent_black);
                }
            }
        });
        menuInfoLayout.startAnimation(animation);
    }

    public void hideMenuAnimation() {
        if (!isShow) {
            return;
        }
        hideSoftMethod();
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_down);
        menuInfoLayout.startAnimation(animation);
        isShow = false;
        menuBgLayout.setVisibility(View.GONE);
    }

}
