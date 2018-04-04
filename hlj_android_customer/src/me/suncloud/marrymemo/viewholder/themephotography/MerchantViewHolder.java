package me.suncloud.marrymemo.viewholder.themephotography;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeMerchantListActivity;

/**
 * Created by jinxin on 2016/10/13.
 */

public class MerchantViewHolder extends RecyclerView.ViewHolder {
    public View itemView;
    public int imgWidth;
    public Context mContext;
    public Point point;
    public DisplayMetrics dm;
    public JourneyTheme theme;
    @BindView(R.id.tv_all_info)
    TextView tvAllInfo;
    @BindView(R.id.iv_arrow_right)
    ImageView ivArrowRight;
    @BindView(R.id.layout_title)
    LinearLayout layoutTitle;
    @BindView(R.id.merchant_layout)
    LinearLayout merchantLayout;
    @BindView(R.id.line)
    View line;

    public MerchantViewHolder(
            View itemView, Context context, JourneyTheme journeyTheme) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
        mContext = context;
        point = JSONUtil.getDeviceSize(mContext);
        dm = mContext.getResources()
                .getDisplayMetrics();
        theme = journeyTheme;
        imgWidth = Math.round((point.x - dm.density * (24 + 12)) * 1.0f / 3);
        List<Merchant> merchants = theme.getMerchants();
        if (merchants.size() < 6) {
            tvAllInfo.setVisibility(View.GONE);
            ivArrowRight.setVisibility(View.GONE);
        }
    }

    public void setMerchant(Merchant merchant, int collectPosition, int size, boolean showHeader) {
        layoutTitle.setVisibility(showHeader ? View.VISIBLE : View.GONE);
        line.setVisibility(showHeader ? View.VISIBLE : View.GONE);
        if (merchant == null || merchant.getId() == 0) {
            merchantLayout.setVisibility(View.GONE);
            return;
        } else {
            merchantLayout.setVisibility(View.VISIBLE);
        }
        boolean showLine = (collectPosition != size - 1);
        setMerchantView(merchantLayout.getChildAt(0), merchant, showLine);
    }

    private void setMerchantView(
            View view, final Merchant merchant, boolean showLine) {
        if (view == null || merchant == null || merchant.getId() == 0) {
            return;
        }

        view.setPadding(0, view.getPaddingTop(), 0, view.getPaddingBottom());
        view.setVisibility(View.VISIBLE);

        MerchantItemViewHolder holder = (MerchantItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new MerchantItemViewHolder(view);
            view.setTag(holder);
        }

        holder.tvName.setText(merchant.getName());
        holder.tvWorkCount.setText(String.valueOf(merchant.getActiveWorkCount()));
        holder.tvCaseCount.setText(String.valueOf(merchant.getActiveCaseCount()));
        holder.tvFansCount.setText(String.valueOf(merchant.getFansCount()));
        holder.line.setVisibility(showLine ? View.VISIBLE : View.GONE);
        initImgLayout(holder.imgLayout);
        List<Work> works = merchant.getPackages();
        if (works != null && !works.isEmpty()) {
            holder.imgLayout.setVisibility(View.VISIBLE);
            for (int i = 0, size = works.size(); i < size; i++) {
                final Work work = works.get(i);
                RelativeLayout workView = (RelativeLayout) holder.imgLayout.getChildAt(i);
                workView.setVisibility(View.VISIBLE);
                workView.getChildAt(1)
                        .setVisibility(View.GONE);
                ImageView img = (ImageView) workView.getChildAt(0);
                String url = JSONUtil.getImagePath(work.getCoverPath(), imgWidth);
                if (!JSONUtil.isEmpty(url)) {
                    Glide.with(mContext)
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                            .into(img);
                } else {
                    Glide.with(mContext)
                            .clear(img);
                }
                workView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (work == null || work.getId() == 0) {
                            return;
                        }
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
        } else {
            holder.imgLayout.setVisibility(View.GONE);
        }

        int res = 0;
        switch (merchant.getGrade()) {
            case 2:
                res = R.mipmap.icon_merchant_level2_36_36;
                break;
            case 3:
                res = R.mipmap.icon_merchant_level3_36_36;
                break;
            case 4:
                res = R.mipmap.icon_merchant_level4_36_36;
                break;
            default:
                break;
        }

        if (res != 0) {
            holder.iconLevel.setVisibility(View.VISIBLE);
            holder.iconLevel.setImageResource(res);
        } else {
            holder.iconLevel.setVisibility(View.GONE);
        }
        holder.iconBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        holder.iconFree.setVisibility(merchant.getActiveWorkCount() > 0 ? View.VISIBLE : View.GONE);
        holder.iconPromise.setVisibility(merchant.getMerchantPromise() != null && merchant
                .getMerchantPromise()
                .size() > 0 ? View.VISIBLE : View.GONE);
        holder.iconRefund.setVisibility(merchant.getChargeBack() != null && merchant.getChargeBack()
                .size() > 0 ? View.VISIBLE : View.GONE);
        holder.iconGift.setVisibility(JSONUtil.isEmpty(merchant.getPlatformGift()) ? View.GONE :
                View.VISIBLE);
        holder.iconExclusiveOffer.setVisibility(JSONUtil.isEmpty(merchant.getExclusiveOffer()) ?
                View.GONE : View.VISIBLE);
        holder.merchantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (merchant == null || merchant.getId() == 0) {
                    return;
                }
                Intent intent = new Intent(mContext, MerchantDetailActivity.class);
                intent.putExtra("id", merchant.getId());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });
    }

    private void initImgLayout(LinearLayout imgLayout) {
        if (imgLayout == null) {
            return;
        }
        for (int i = 0, size = imgLayout.getChildCount(); i < size; i++) {
            RelativeLayout workView = (RelativeLayout) imgLayout.getChildAt(i);
            workView.setVisibility(View.INVISIBLE);
            workView.getChildAt(1)
                    .setVisibility(View.GONE);
            ImageView img = (ImageView) workView.getChildAt(0);
            Glide.with(mContext)
                    .clear(img);
        }
    }


    @OnClick({R.id.tv_all_info, R.id.iv_arrow_right})
    public void onClick() {
        Intent intent = new Intent(mContext, ThemeMerchantListActivity.class);
        intent.putExtra("id", theme.getId());
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    class MerchantItemViewHolder {
        //        TextView tvName;
        //        ImageView iconLevel;
        //        ImageView iconBond;
        //        ImageView iconRefund;
        //        ImageView iconPromise;
        //        ImageView iconFree;
        //        ImageView iconGift;
        //        ImageView iconExclusiveOffer;
        //        TextView tvWorkCount;
        //        TextView tvCaseCount;
        //        TextView tvFansCount;
        //        LinearLayout imgLayout;
        //        View line;
        //        LinearLayout iconLayout;
        //        View merchantLayout;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.icon_level)
        ImageView iconLevel;
        @BindView(R.id.icon_bond)
        ImageView iconBond;
        @BindView(R.id.icon_refund)
        ImageView iconRefund;
        @BindView(R.id.icon_promise)
        ImageView iconPromise;
        @BindView(R.id.icon_free)
        ImageView iconFree;
        @BindView(R.id.icon_gift)
        ImageView iconGift;
        @BindView(R.id.icon_exclusive_offer)
        ImageView iconExclusiveOffer;
        @BindView(R.id.icons_layout)
        LinearLayout iconsLayout;
        @BindView(R.id.tv_work_count)
        TextView tvWorkCount;
        @BindView(R.id.tv_case_count)
        TextView tvCaseCount;
        @BindView(R.id.tv_fans_count)
        TextView tvFansCount;
        @BindView(R.id.merchant_layout)
        LinearLayout merchantLayout;
        @BindView(R.id.img_layout)
        LinearLayout imgLayout;
        @BindView(R.id.line)
        View line;

        public MerchantItemViewHolder(View view) {
            ButterKnife.bind(this, view);
            //            tvName = (TextView) view.findViewById(R.id.tv_name);
            //            iconLevel = (ImageView) view.findViewById(R.id.icon_level);
            //            iconBond = (ImageView) view.findViewById(R.id.icon_bond);
            //            iconRefund = (ImageView) view.findViewById(R.id.icon_refund);
            //            iconPromise = (ImageView) view.findViewById(R.id.icon_promise);
            //            iconFree = (ImageView) view.findViewById(R.id.icon_free);
            //            iconGift = (ImageView) view.findViewById(R.id.icon_gift);
            //            iconExclusiveOffer = (ImageView) view.findViewById(R.id
            // .icon_exclusive_offer);
            //            tvWorkCount = (TextView) view.findViewById(R.id.tv_work_count);
            //            tvCaseCount = (TextView) view.findViewById(R.id.tv_case_count);
            //            tvFansCount = (TextView) view.findViewById(R.id.tv_fans_count);
            //            imgLayout = (LinearLayout) view.findViewById(R.id.img_layout);
            //            line = view.findViewById(R.id.line);
            //            iconLayout = (LinearLayout) view.findViewById(R.id.icons_layout);
            //            merchantLayout = view.findViewById(R.id.merchant_layout);
        }
    }
}
