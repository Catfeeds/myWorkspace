package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
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
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;


/**
 * 旅拍全部商家
 * Created by jinxin on 2016/9/26.
 */

public class GuideMerchantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int FOOTER_TYPE = 4;
    public static final int ITEM = 3;
    private Context mContext;
    private ArrayList<Merchant> merchants;
    private View footView;
    private OnItemClickListener onItemClickListener;
    private int imgWidth;


    public GuideMerchantAdapter(
            Context mContext, ArrayList<Merchant> merchants) {
        this.mContext = mContext;
        this.merchants = merchants;
        Point point = JSONUtil.getDeviceSize(mContext);
        DisplayMetrics dm = mContext.getResources()
                .getDisplayMetrics();
        imgWidth = Math.round((point.x - dm.density * (24 + 12)) * 1.0f / 3);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
            View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.common_merchant_item, parent, false);
            return new MerchantViewHolder(itemView);
        } else if (viewType == FOOTER_TYPE && footView != null) {
            return new ExtraViewHolder(footView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MerchantViewHolder && merchants != null) {
            setMerchantView((MerchantViewHolder) holder, merchants.get(position), position, true);
        }
    }

    public void setMerchantView(
            final MerchantViewHolder holder,
            final Merchant merchant,
            final int position,
            boolean showLine) {
        if (merchant == null) {
            return;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(holder.itemView, merchant, position);
                }
            }
        });
        holder.itemView.setVisibility(View.VISIBLE);
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

    @Override
    public int getItemCount() {
        return merchants.size() + (footView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        if (position == getItemCount() - 1 && footView != null) {
            type = FOOTER_TYPE;
        } else {
            type = ITEM;
        }
        return type;
    }

    public void setFootView(View view) {
        this.footView = view;
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, Merchant merchant, int position);
    }

    public class MerchantViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.img_layout)
        LinearLayout imgLayout;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.merchant_layout)
        View merchantLayout;
        View itemView;

        MerchantViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView = view;
        }
    }

    public class ExtraViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public ExtraViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

}
