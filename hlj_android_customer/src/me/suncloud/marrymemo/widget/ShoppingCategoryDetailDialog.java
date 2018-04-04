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

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.CategoryMarkFilterAdapter;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * 买套餐用到的dialog
 * Created by jinxin on 2016/12/7 0007.
 */

public class ShoppingCategoryDetailDialog extends Dialog implements MyScrollView
        .OnScrollChangedListener {

    @BindView(R.id.et_price_min)
    EditText etPriceMin;
    @BindView(R.id.et_price_max)
    EditText etPriceMax;
    @BindView(R.id.li_content)
    LinearLayout liContent;
    @BindView(R.id.scroll_view)
    MyScrollView scrollView;
    @BindView(R.id.price_filter_layout)
    LinearLayout priceFilterLayout;

    private Context mContext;
    private List<CategoryMark> categoryMarks;
    private List<ViewHolder> holders;
    private OnConfirmListener onConfirmListener;
    private InputMethodManager imm;


    public ShoppingCategoryDetailDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        categoryMarks = new ArrayList<>();
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

    public void hidePriceLayout() {
        priceFilterLayout.setVisibility(View.GONE);
    }

    public void hideClip() {
        for (ViewHolder h : holders) {
            h.liAll.setVisibility(View.GONE);
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
            List<CategoryMark> selects = new ArrayList<>();
            for (ViewHolder h : holders) {
                if (h.getSelected() != null && !h.getSelected()
                        .isEmpty()) {
                    selects.addAll(h.getSelected());
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

    public void setCategoryMarks(List<CategoryMark> categoryMarks) {
        if (categoryMarks == null) {
            return;
        }

        Iterator<CategoryMark> iterator = categoryMarks.iterator();
        while (iterator.hasNext()) {
            CategoryMark categoryMark = iterator.next();
            if (categoryMark.getChildren() == null || categoryMark.getChildren()
                    .isEmpty()) {
                iterator.remove();
            }
        }

        this.categoryMarks.clear();
        this.categoryMarks.addAll(categoryMarks);

        holders.clear();
        int count = liContent.getChildCount();
        int size = this.categoryMarks.size();
        if (count > size) {
            liContent.removeViews(size, count - size);
        }
        for (int i = 0; i < size; i++) {
            View itemView = liContent.getChildAt(i);
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.buy_work_list_item, null);
                ViewHolder holder = new ViewHolder(itemView);
                itemView.setTag(holder);
                liContent.addView(itemView);
            }
            CategoryMark categoryMark = this.categoryMarks.get(i);
            ViewHolder holder = (ViewHolder) itemView.getTag();
            holder.line.setVisibility(i < size - 1 ? View.VISIBLE : View.GONE);
            holder.setCategoryMark(categoryMark);
            holder.liAll.setOnClickListener(holder);
            holders.add(holder);
        }
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        hideSoftMethod();
    }

    class ViewHolder implements View.OnClickListener, CategoryMarkFilterAdapter
            .OnKeyWordClickListener {
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
        CategoryMarkFilterAdapter adapter;
        int filterWidth;
        ArrayList<CategoryMark> categoryMarks;
        CategoryMark categoryMark;
        boolean clipEd;//展开全部 收起全部
        DisplayMetrics dm;

        boolean isInit = false;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            Point point = JSONUtil.getDeviceSize(mContext);
            clipEd = false;
            dm = mContext.getResources()
                    .getDisplayMetrics();
            filterWidth = Math.round(point.x / 3 - Util.dp2px(mContext, 16));
            int filterDlgWidth = point.x / 5 * 4;
            filterWidth = (filterDlgWidth - CommonUtil.dp2px(mContext, 28 + 16)) / 3;
            categoryMarks = new ArrayList<>();
            adapter = new CategoryMarkFilterAdapter(categoryMarks, mContext, filterWidth);
            adapter.setHasDefault(true);
            adapter.setOnKeyWordClickListener(this);
            gridView.setAdapter(adapter);
        }

        /**
         * @param clip true 展开全部 clip收起全部
         */
        public void setFilter(List<CategoryMark> categoryMarks, boolean clip) {
            if (categoryMarks != null) {
                int rawRow = (int) Math.ceil(categoryMarks.size() * 1.0d / 3);//判断 全部按钮 显示与否
                if (rawRow > 2) {
                    liAll.setVisibility(View.VISIBLE);
                } else {
                    liAll.setVisibility(View.GONE);
                }

                this.categoryMarks.clear();
                if (categoryMarks.size() <= 6) {
                    //没有展开
                    this.categoryMarks.addAll(categoryMarks);
                } else {
                    if (clip) {
                        this.categoryMarks.addAll(categoryMarks);
                    } else {
                        this.categoryMarks.addAll(categoryMarks.subList(0, 6));
                    }
                }
                int row = (int) Math.ceil(this.categoryMarks.size() * 1.0d / 3);
                gridView.getLayoutParams().height = Math.round(row * dm.density * 27) + Math
                        .round((row - 1) * dm.density * 10 + dm.density * 10);
                if (!isInit) {
                    isInit = !isInit;
                    adapter.resetDefault();
                } else {
                    adapter.resetHashMap();
                }
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

        public void setCategoryMark(CategoryMark categoryMark) {
            if (categoryMark == null && categoryMark.getId() <= 0) {
                return;
            }
            this.categoryMark = categoryMark;
            if (categoryMark.getMark() != null) {
                tvProperty.setText(categoryMark.getMark()
                        .getName());
            }
            setFilter(categoryMark.getChildren(), true);
        }

        public List<CategoryMark> getSelected() {
            if (adapter != null) {
                return adapter.getChecked();
            }
            return null;
        }

        public CategoryMark getCategoryMark() {
            return categoryMark;
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
                setFilter(categoryMark.getChildren(), !clipEd);
                clipEd = !clipEd;
            }
        }

        @Override
        public void onKeyWorkClick(List<CategoryMark> categoryMarks) {
            int size = categoryMarks.size();
        }
    }

    public interface OnConfirmListener {
        void onConfirm(
                String priceMin, String priceMax, List<CategoryMark> select);
    }

}
