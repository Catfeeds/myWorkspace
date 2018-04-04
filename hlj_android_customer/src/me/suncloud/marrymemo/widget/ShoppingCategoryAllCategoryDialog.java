package me.suncloud.marrymemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljimagelibrary.adapters.viewholders.ExtraViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * 买套餐用到的dialog
 * Created by jinxin on 2016/12/7 0007.
 */

public class ShoppingCategoryAllCategoryDialog extends Dialog {

    @BindView(R.id.btn_reset)
    Button btnReset;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;

    private Context mContext;
    private List<ShopCategory> shopCategories;
    private CategoryAdapter categoryAdapter;
    private GridLayoutManager gridLayoutManager;
    private MeasureSize measureSize;
    private OnItemClickListener onItemClickListener;
    private View footerView;


    public ShoppingCategoryAllCategoryDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        shopCategories = new ArrayList<>();
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
        ButterKnife.bind(this, view);
        btnReset.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
        measureSize = new MeasureSize(mContext);
        footerView = LayoutInflater.from(mContext)
                .inflate(R.layout.hlj_foot_no_more___cm, null, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT,
                CommonUtil.dp2px(mContext, 50));
        footerView.setLayoutParams(params);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration();
        categoryAdapter = new CategoryAdapter();
        gridLayoutManager = new GridLayoutManager(mContext, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position != categoryAdapter.getItemCount() - 1) {
                    return 1;
                }
                return 3;
            }

        });
        recyclerView.getRefreshableView()
                .setLayoutManager(gridLayoutManager);
        recyclerView.getRefreshableView()
                .addItemDecoration(itemDecoration);
        recyclerView.getRefreshableView()
                .setAdapter(categoryAdapter);
        recyclerView.getRefreshableView()
                .setPadding(measureSize.recyclePadding,
                        recyclerView.getPaddingTop(),
                        measureSize.recyclePadding,
                        recyclerView.getPaddingBottom());
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    public void setShopCategories(List<ShopCategory> shopCategories) {
        if (shopCategories != null) {
            this.shopCategories.clear();
            this.shopCategories.addAll(shopCategories);
            categoryAdapter.notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int ITEM_FOOTER = 101;
        private final int ITEM_ITEM = 102;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_ITEM:
                    View itemView = getLayoutInflater().inflate(R.layout
                                    .right_shopping_category_item,
                            parent,
                            false);
                    return new RightContentViewHolder(itemView);
                case ITEM_FOOTER:
                    return new ExtraViewHolder<R>(footerView);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
            int viewType = getItemViewType(position);
            if (viewType != ITEM_ITEM) {
                return;
            }
            RightContentViewHolder holder = (RightContentViewHolder) h;
            ShopCategory shopCategory = shopCategories.get(position);
            holder.setContent(shopCategory, position);
        }

        @Override
        public int getItemCount() {
            return shopCategories.isEmpty() ? 0 : shopCategories.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            int type;
            if (position != getItemCount() - 1) {
                type = ITEM_ITEM;
            } else {
                type = ITEM_FOOTER;
            }
            return type;
        }
    }

    class RightContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_category)
        ImageView imgCategory;
        @BindView(R.id.tv_category)
        TextView tvCategory;

        private ShopCategory shopCategory;

        public RightContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            imgCategory.getLayoutParams().width = measureSize.imgCategoryWidth;
            imgCategory.getLayoutParams().height = measureSize.imgCategoryWidth;
        }

        private void initTracker(View view, Object object, int position) {
            if (object instanceof ShopCategory) {
                HljVTTagger.buildTagger(view)
                        .tagName("bottom_section_item_")
                        .atPosition(position)
                        .dataId(((ShopCategory) object).getId())
                        .dataType("ProductCategory")
                        .hitTag();
            } else {
                HljVTTagger.buildTagger(view)
                        .clear();
            }
        }

        public void setContent(ShopCategory shopCategory, int position) {
            initTracker(itemView, shopCategory, position);
            if (shopCategory == null || shopCategory.getId() <= 0) {
                return;
            }
            this.shopCategory = shopCategory;
            setViewContent(shopCategory.getName(), shopCategory.getCoverImage());
        }

        private void setViewContent(String title, String path) {
            tvCategory.setText(title);
            Glide.with(mContext)
                    .load(ImagePath.buildPath(path)
                            .width(measureSize.imgCategoryWidth)
                            .height(measureSize.imgCategoryWidth)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgCategory);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onViewClick(shopCategory);
            }
        }
    }

    class MeasureSize {
        public int leftSpace;
        public int middleSpace;
        public int imgCategoryWidth;
        public int bannerLeft;
        public int bannerTop;
        public int recyclePadding;

        public MeasureSize(Context context) {
            int screenWidth = CommonUtil.getDeviceSize(context).x;
            int right = Math.round(screenWidth * 1.0f * 59 / (59 + 21));
            bannerLeft = Math.round(screenWidth * 1.0f * 1 / 320);
            bannerTop = CommonUtil.dp2px(context, 16);
            leftSpace = Math.round(screenWidth * 1.0f * 3 / 40);
            middleSpace = Math.round(screenWidth * 1.0f * 7 / 160);
            imgCategoryWidth = Math.round((right - middleSpace * 4 - leftSpace * 2) * 1.0f / 3);
            recyclePadding = Math.round(screenWidth * 1.0f * 1 / 32);
        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = gridLayoutManager.getPosition(view);
            switch (position % 3) {
                case 0:
                    outRect.left = measureSize.middleSpace;
                    outRect.right = measureSize.middleSpace;
                    outRect.top = measureSize.leftSpace;
                    break;
                case 1:
                    outRect.left = measureSize.middleSpace;
                    outRect.right = measureSize.middleSpace;
                    outRect.top = measureSize.leftSpace;
                    break;
                case 2:
                    outRect.left = measureSize.middleSpace;
                    outRect.right = measureSize.middleSpace;
                    outRect.top = measureSize.leftSpace;
                    break;
            }
        }
    }

    public interface OnItemClickListener {
        void onViewClick(ShopCategory shopCategory);
    }
}

