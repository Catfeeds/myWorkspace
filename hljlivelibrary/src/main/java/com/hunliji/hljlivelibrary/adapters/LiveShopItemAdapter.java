package com.hunliji.hljlivelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.models.LiveIntroItem;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hunliji.hljlivelibrary.models.LiveRelevantWrapper.TYPE_PRODUCT;
import static com.hunliji.hljlivelibrary.models.LiveRelevantWrapper.TYPE_WORK;

/**
 * Created by luohanlin on 2017/11/29.
 */

public class LiveShopItemAdapter extends RecyclerView.Adapter<BaseViewHolder<LiveIntroItem>> {

    private Context context;
    private List<LiveIntroItem> list;
    private int width;
    private int height;
    private int imgSize;
    private int paddingWidth;

    public static final int TYPE_ITEM = 1;

    public LiveShopItemAdapter(
            Context context, List<LiveIntroItem> list) {
        this.context = context;
        this.list = list;
        imgSize = CommonUtil.dp2px(context, 90);
        width = CommonUtil.dp2px(context, 300);
        height = CommonUtil.dp2px(context, 186);
        paddingWidth = (CommonUtil.getDeviceSize(context).x - width - CommonUtil.dp2px(context,
                10)) / 2;
    }

    @Override
    public BaseViewHolder<LiveIntroItem> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.live_shop_item___live, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<LiveIntroItem> holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).setView(context,
                    list.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ItemViewHolder extends BaseViewHolder<LiveIntroItem> {
        @BindView(R2.id.start_padding)
        View startPadding;
        @BindView(R2.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R2.id.tv_title)
        TextView tvTitle;
        @BindView(R2.id.tv_price)
        TextView tvPrice;
        @BindView(R2.id.tv_collect)
        TextView tvCollect;
        @BindView(R2.id.btn_buy)
        TextView btnBuy;
        @BindView(R2.id.shop_item_layout)
        RelativeLayout shopItemLayout;
        @BindView(R2.id.end_padding)
        View endPadding;

        View itemView;

        ItemViewHolder(View view) {
            super(view);
            itemView = view;
            ButterKnife.bind(this, view);

            startPadding.getLayoutParams().width = paddingWidth;
            endPadding.getLayoutParams().width = paddingWidth;
        }

        @Override
        public void setView(Context mContext, LiveIntroItem item, int position, int viewType) {
            try {
                switch (item.getType()) {
                    case TYPE_PRODUCT:
                        ShopProduct product = item.getProduct();
                        if (product != null) {
                            HljVTTagger.buildTagger(itemView)
                                    .tagName(HljTaggerName.PRODUCT)
                                    .atPosition(position)
                                    .dataId(product.getId())
                                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_PRODUCT)
                                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND,
                                            product.getDtExtend())
                                    .tag();
                        }
                        break;
                    case TYPE_WORK:
                        Work work = item.getWork();
                        if (work != null) {
                            long propertyId = 0;
                            try {
                                propertyId = work.getMerchant()
                                        .getProperty()
                                        .getId();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            HljVTTagger.buildTagger(itemView)
                                    .tagName(HljTaggerName.WORK)
                                    .atPosition(position)
                                    .dataId(work.getId())
                                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_PACKAGE)
                                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_PROPERTY_ID,
                                            propertyId > 0 ? propertyId : null)
                                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND,
                                            work.getDtExtend())
                                    .tag();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.setView(mContext, item, position, viewType);
        }

        @Override
        protected void setViewData(
                Context mContext, LiveIntroItem item, int position, int viewType) {
            startPadding.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            endPadding.setVisibility(position == getItemCount() - 1 ? View.VISIBLE : View.GONE);

            String title = "";
            String coverPath = null;
            int collectCount = 0;
            boolean isIntroduced = false;

            switch (item.getType()) {
                case TYPE_PRODUCT:
                    final ShopProduct product = item.getProduct();
                    if (product != null) {
                        coverPath = ImagePath.buildPath(product.getCoverPath())
                                .width(imgSize)
                                .height(imgSize)
                                .path();
                        title = product.getTitle();
                        collectCount = product.getCollectCount();
                        isIntroduced = product.isIntroduced();
                        tvPrice.setText("￥" + CommonUtil.formatDouble2StringWithTwoFloat(product
                                .getShowPrice()));
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance()
                                        .build(RouterPath.IntentPath.Customer.SHOP_PRODUCT)
                                        .withLong("id", product.getId())
                                        .navigation(context);
                            }
                        });
                    }
                    break;
                case TYPE_WORK:
                    final Work work = item.getWork();
                    if (work != null) {
                        coverPath = ImagePath.buildPath(work.getCoverPath())
                                .width(imgSize)
                                .height(imgSize)
                                .path();
                        title = work.getTitle();
                        collectCount = work.getCollectorsCount();
                        isIntroduced = work.isIntroduced();
                        tvPrice.setText("￥" + CommonUtil.formatDouble2String(work.getShowPrice()));
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance()
                                        .build(RouterPath.IntentPath.Customer.WORK_ACTIVITY)
                                        .withLong("id", work.getId())
                                        .navigation(context);
                            }
                        });
                    }
                    break;
            }

            Glide.with(context)
                    .load(coverPath)
                    .into(imgCover);
            tvCollect.setText("收藏" + collectCount);
            if (isIntroduced) {
                SpannableStringBuilder builder = new SpannableStringBuilder(" " + title);
                View view = View.inflate(mContext, R.layout.tag_introduced_layout___live, null);
                ((TextView) view.findViewById(R.id.tv_title)).setText("介绍中");
                builder.setSpan(new HljImageSpan(ImageUtil.getDrawingCache(mContext, view)),
                        0,
                        1,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvTitle.setText(builder);
            } else {
                tvTitle.setText(title);
            }
        }
    }
}
