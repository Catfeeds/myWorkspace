package me.suncloud.marrymemo.adpter.themephotography;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeAmorousCityActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeGuideListActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeHotCityActivity;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * 旅拍热城和单城市 adapter
 * Created by jinxin on 2016/9/19.
 */
public class UnitCityThemeAdapter extends RecyclerView.Adapter<BaseViewHolder<Work>> {
    private Context mContext;
    private JourneyTheme theme;

    public static final int TYPE_TOP_IMAGE = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_GUIDE = 2;

    private int topImgWidth;
    private int topImgHeight;
    private int coverImgWidth;
    private int coverImgHeight;
    private LayoutInflater inflater;

    public UnitCityThemeAdapter(
            Context mContext) {
        this.mContext = mContext;
        topImgWidth = CommonUtil.getDeviceSize(mContext).x;
        topImgHeight = topImgWidth * 516 / 1080;
        coverImgWidth = topImgWidth - CommonUtil.dp2px(mContext, 32);
        coverImgHeight = coverImgWidth * 9 / 16;
        inflater = LayoutInflater.from(mContext);
    }

    public void setTheme(JourneyTheme theme) {
        this.theme = theme;
    }

    @Override
    public BaseViewHolder<Work> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TOP_IMAGE:
                return new ImgViewHolder(inflater.inflate(R.layout.journey_image_item,
                        parent,
                        false));
            case TYPE_GUIDE:
                return new GuidesViewHolder(inflater.inflate(R.layout.unit_city_guide_item,
                        parent,
                        false));
            case TYPE_ITEM:
                return new UnitWorkViewHolder(inflater.inflate(R.layout.unit_city_work_item,
                        parent,
                        false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Work> holder, int position) {
        if (position == 0) {
            holder.setView(mContext, null, position, TYPE_TOP_IMAGE);
        } else if (!CommonUtil.isCollectionEmpty(theme.getGuides())) {
            // 固定第三个套餐之后显示攻略
            if ((getItemCount() > 4 && position == 4) || (getItemCount() < 5 && position ==
                    getItemCount() - 1)) {
                holder.setView(mContext, null, position, TYPE_GUIDE);
            } else {
                if (position > 4) {
                    holder.setView(mContext,
                            theme.getWorks()
                                    .get(position - 2),
                            position,
                            TYPE_ITEM);
                } else {
                    holder.setView(mContext,
                            theme.getWorks()
                                    .get(position - 1),
                            position,
                            TYPE_ITEM);
                }
            }
        } else {
            holder.setView(mContext,
                    theme.getWorks()
                            .get(position - 1),
                    position,
                    TYPE_ITEM);
        }
    }


    @Override
    public int getItemCount() {
        if (theme == null) {
            return 0;
        } else {
            // 顶图和旅拍攻略 + works
            return theme.getWorks()
                    .size() + (CommonUtil.isCollectionEmpty(theme.getGuides()) ? 0 : 1) + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP_IMAGE;
        } else if (!CommonUtil.isCollectionEmpty(theme.getGuides())) {
            // 固定第三个套餐之后显示攻略
            if ((getItemCount() > 4 && position == 4) || (getItemCount() < 5 && position ==
                    getItemCount() - 1)) {
                return TYPE_GUIDE;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    class ImgViewHolder extends BaseViewHolder<Work> {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_count)
        TextView tvCount;
        View view;

        ImgViewHolder(View view) {
            super(view);
            this.view = view;
            view.getLayoutParams().width = topImgWidth;
            view.getLayoutParams().height = topImgHeight;
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(Context mContext, Work item, int position, int viewType) {
            HljVTTagger.buildTagger(tvTitle)
                    .tagName(HljTaggerName.BTN_CHANGE_CITY)
                    .tag();

            Glide.with(mContext)
                    .load(ImagePath.buildPath(theme.getCoverPath())
                            .width(topImgWidth)
                            .height(topImgHeight)
                            .path())
                    .into(image);

            tvCount.setText(String.valueOf(theme.getWatchCount()));
            tvTitle.setText(theme.getTitle());
        }

        @OnClick(R.id.tv_title)
        void onTitle() {
            Intent intent = new Intent(mContext, ThemeHotCityActivity.class);
            mContext.startActivity(intent);
        }
    }

    class GuidesViewHolder extends BaseViewHolder<Work> {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_more_guides)
        TextView tvMoreGuides;
        @BindView(R.id.recycler_view)
        RecyclerView recyclerView;

        GuidesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                    HORIZONTAL,
                    false);
            recyclerView.setFocusable(false);
            recyclerView.setLayoutManager(layoutManager);
        }

        @Override
        protected void setViewData(Context mContext, Work item, int position, int viewType) {
            UnitCityGuidesAdapter adapter = new UnitCityGuidesAdapter(mContext, theme.getGuides());
            recyclerView.setAdapter(adapter);
        }

        @OnClick(R.id.tv_more_guides)
        void onMoreGuides() {
            Intent intent = new Intent();
            intent.setClass(mContext, ThemeGuideListActivity.class);
            intent.putExtra("id", theme.getId());
            mContext.startActivity(intent);
        }
    }

    class UnitWorkViewHolder extends TrackerWorkViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.img_installment)
        ImageView imgInstallment;
        @BindView(R.id.badge)
        ImageView badge;
        @BindView(R.id.re_cover)
        RelativeLayout reCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_achievement)
        TextView tvAchievement;
        @BindView(R.id.merchant_achievement_layout)
        CardView merchantAchievementLayout;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.end_padding)
        View endPadding;
        View view;

        UnitWorkViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            this.imgCover.getLayoutParams().width = coverImgWidth;
            this.imgCover.getLayoutParams().height = coverImgHeight;
        }

        @Override
        public String cpmSource() {
            return ThemeAmorousCityActivity.CPM_SOURCE;
        }

        @Override
        public View trackerView() {
            return view;
        }

        @Override
        protected void setViewData(
                final Context mContext, final Work item, int position, int viewType) {
            MultiTransformation transformation = new MultiTransformation(new CenterCrop(),
                    new RoundedCorners(CommonUtil.dp2px(mContext, 4)));
            if (position == getItemCount() - 1) {
                endPadding.setVisibility(View.VISIBLE);
            } else {
                endPadding.setVisibility(View.GONE);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = item.getLink();
                    if (JSONUtil.isEmpty(link)) {
                        Intent intent = new Intent(mContext, WorkActivity.class);
                        intent.putExtra("id", item.getId());
                        mContext.startActivity(intent);
                    } else {
                        HljWeb.startWebView(mContext, link);
                    }
                }
            });
            Glide.with(mContext)
                    .load(ImagePath.buildPath(item.getCoverPath())
                            .width(coverImgWidth)
                            .height(coverImgHeight)
                            .path())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .transform(transformation))
                    .into(imgCover);
            tvTitle.setText(item.getTitle());
            tvPrice.setText(mContext.getString(R.string.label_price,
                    CommonUtil.formatDouble2String(item.getShowPrice())));
            tvMerchantName.setText(item.getMerchant()
                    .getName());
            if (!CommonUtil.isCollectionEmpty(item.getMerchant()
                    .getMerchantAchievement()) && item.getMerchant()
                    .getMerchantAchievement()
                    .get(0)
                    .getTitle()
                    .equals("千万新娘推荐 2017年度优质商家")) {
                merchantAchievementLayout.setVisibility(View.VISIBLE);
                tvAchievement.setText("年度优选");
            } else {
                merchantAchievementLayout.setVisibility(View.GONE);
            }
        }
    }
}
