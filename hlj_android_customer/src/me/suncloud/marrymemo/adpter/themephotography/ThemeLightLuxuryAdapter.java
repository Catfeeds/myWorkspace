package me.suncloud.marrymemo.adpter.themephotography;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeLightLuxuryActivity;
import me.suncloud.marrymemo.widget.FlowLayout;

/**
 * Created by jinxin on 2016/10/10.
 */

public class ThemeLightLuxuryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER = 1;
    private static final int FOOTER = 2;
    private static final int ITEM = 3;
    private View headerView;
    private View footerView;
    private List<Work> works;
    private Context mContext;
    private int coverHeight;
    private int coverWidth;
    private int logoSize;
    private int normalTagCount = 6;
    private int strokeWidth;

    public ThemeLightLuxuryAdapter(Context mContext, List<Work> works) {
        this.mContext = mContext;
        this.works = works;
        Point point = JSONUtil.getDeviceSize(this.mContext);
        DisplayMetrics dm = this.mContext.getResources()
                .getDisplayMetrics();
        coverWidth = point.x;
        coverHeight = Math.round(coverWidth * 10 / 16);
        logoSize = Math.round(dm.density * 22);
        strokeWidth = (int) Math.min(1, Math.round(dm.density * 0.5));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new ExtraViewHolder(headerView);
            case FOOTER:
                return new ExtraViewHolder(footerView);
            case ITEM:
                View itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.theme_light_luxury_item, parent, false);
                return new LuxuryViewHolder(itemView);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM) {
            int workPosition = headerView == null ? position : position - 1;
            Work work = works.get(workPosition);
            LuxuryViewHolder luxuryViewHolder = (LuxuryViewHolder) holder;
            setWork(luxuryViewHolder, work, position);
        }
    }

    private void initTracker(View view, Work work, int position) {
        long propertyId = 0;
        long merchantId = 0;
        try {
            merchantId = work.getMerchant()
                    .getId();
            propertyId = work.getMerchant()
                    .getProperty()
                    .getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HljVTTagger.buildTagger(view)
                .tagName(HljTaggerName.WORK)
                .atPosition(position)
                .dataId(work.getId())
                .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_PACKAGE)
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_PROPERTY_ID,
                        propertyId > 0 ? propertyId : null)
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_MID, merchantId)
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_FLAG, work.getCpm())
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE,
                        ThemeLightLuxuryActivity.CPM_SOURCE)
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND, work.getDtExtend())
                .tag();
    }

    private void setWork(LuxuryViewHolder holder, final Work work, int position) {
        if (work == null || work.getId() == 0) {
            return;
        }

        //===================================
        initTracker(holder.itemView, work, position);
        //===================================

        holder.reImgLayout.getLayoutParams().width = coverWidth;
        holder.reImgLayout.getLayoutParams().height = coverHeight;
        if (!JSONUtil.isEmpty(work.getCity())) {
            holder.tvCity.setText(work.getCity());
            holder.tvCity.setVisibility(View.VISIBLE);
        } else {
            holder.tvCity.setVisibility(View.GONE);
        }
        String coverPath = JSONUtil.getImagePath(work.getCoverPath(), coverWidth);
        if (!JSONUtil.isEmpty(coverPath)) {
            Glide.with(mContext)
                    .load(coverPath)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(holder.imgCover);
        } else {
            Glide.with(mContext)
                    .clear(holder.imgCover);
        }

        Merchant merchant = work.getMerchant();
        if (merchant.getId() > 0) {
            final long merchantId = merchant.getId();
            holder.liMerchant.setVisibility(View.VISIBLE);
            String logoPath = JSONUtil.getImagePath(merchant.getLogoPath(), logoSize);
            if (!JSONUtil.isEmpty(logoPath)) {
                Glide.with(mContext)
                        .load(ImageUtil.getAvatar(logoPath, logoSize))
                        .apply(new RequestOptions().dontAnimate())
                        .into(holder.imgLogo);
            } else {
                Glide.with(mContext)
                        .clear(holder.imgLogo);
            }

            holder.tvName.setText(merchant.getName());
            holder.tvFansCount.setText(mContext.getString(R.string.merchant_collect_count,
                    merchant.getFansCount()));
            holder.liMerchant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MerchantDetailActivity.class);
                    intent.putExtra("id", merchantId);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }

            });
        } else {
            holder.liMerchant.setVisibility(View.GONE);
        }
        holder.tvTitle.setText(work.getTitle());
        holder.tvTitle.setVisibility(JSONUtil.isEmpty(work.getTitle()) ? View.GONE : View.VISIBLE);
        holder.tvDesc.setText(work.getSubTitle());
        holder.tvDesc.setVisibility(JSONUtil.isEmpty(work.getSubTitle()) ? View.GONE : View
                .VISIBLE);
        holder.tvPrice.setText(Util.formatDouble2String(work.getShowPrice()));
        addTag(holder.tagsLayout, work.getMarks());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = work.getLink();
                if (JSONUtil.isEmpty(link)) {
                    Intent intent = new Intent(mContext, WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                } else {
                    HljWeb.startWebView((Activity) mContext, link);
                }
            }
        });
    }

    private void addTag(FlowLayout layout, List<Mark> marks) {
        if (marks != null && !marks.isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            int count = layout.getChildCount();
            int size = marks.size();
            size = size > normalTagCount ? normalTagCount : size;
            if (count > size) {
                layout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                View view = null;
                TextView tv = null;
                Mark mark = marks.get(i);
                if (i < count) {
                    view = layout.getChildAt(i);
                    tv = (TextView) view.findViewById(R.id.mark);
                }
                if (view == null) {
                    view = View.inflate(mContext, R.layout.mark_item, null);
                    tv = (TextView) view.findViewById(R.id.mark);
                    //  view.setOnClickListener(new OnMarkClickListener());
                    layout.addView(view);
                }
                view.setTag(mark);
                tv.setText(mark.getName());
                tv.setBackgroundResource(R.drawable.sp_r2_stroke_gray3);
                tv.setTextColor(mContext.getResources()
                        .getColor(R.color.colorBlack2));
                GradientDrawable drawable = (GradientDrawable) tv.getBackground();
                if (drawable != null) {
                    try {
                        if (mark.isHighLight()) {
                            int color = Color.parseColor(mark.getHighLightColor());
                            drawable.setStroke(strokeWidth, color);
                            tv.setTextColor(color);
                        } else {
                            drawable.setStroke(strokeWidth,
                                    mContext.getResources()
                                            .getColor(R.color.colorGray3));
                            tv.setTextColor(mContext.getResources()
                                    .getColor(R.color.colorBlack3));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            layout.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemViewType(int position) {
        int size = getItemCount();
        int type;
        if (position == 0 && headerView != null) {
            type = HEADER;
        } else if (position == size - 1 && footerView != null) {
            type = FOOTER;
        } else {
            type = ITEM;
        }
        return type;
    }

    public View getHeaderView() {
        return headerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return works.size() + (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1);
    }

    class LuxuryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_city)
        TextView tvCity;
        @BindView(R.id.re_img_layout)
        RelativeLayout reImgLayout;
        @BindView(R.id.img_logo)
        RoundedImageView imgLogo;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_fans_count)
        TextView tvFansCount;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_desc)
        TextView tvDesc;
        @BindView(R.id.tags_layout)
        FlowLayout tagsLayout;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.li_money)
        LinearLayout liMoney;
        @BindView(R.id.li_merchant)
        LinearLayout liMerchant;
        @BindView(R.id.li_tags)
        LinearLayout liTags;

        public LuxuryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ExtraViewHolder extends RecyclerView.ViewHolder {

        public ExtraViewHolder(View itemView) {
            super(itemView);
        }
    }

}
