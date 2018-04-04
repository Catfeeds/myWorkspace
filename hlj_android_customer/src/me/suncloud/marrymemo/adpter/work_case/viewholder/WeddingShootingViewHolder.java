package me.suncloud.marrymemo.adpter.work_case.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * Created by hua_rong on 2017/8/3.
 * 婚礼摄像
 */

public class WeddingShootingViewHolder extends TrackerWorkViewHolder {


    @BindView(R.id.iv_cover)
    ImageView ivCover;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.rl_cover)
    RelativeLayout rlCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.ll_show_price)
    LinearLayout llShowPrice;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.img_level)
    ImageView imgLevel;
    @BindView(R.id.img_bond)
    ImageView imgBond;
    @BindView(R.id.img_refund)
    ImageView imgRefund;
    @BindView(R.id.img_promise)
    ImageView imgPromise;
    @BindView(R.id.img_coupon)
    ImageView imgCoupon;
    @BindView(R.id.li_merchant)
    LinearLayout liMerchant;
    @BindView(R.id.ll_item)
    LinearLayout llItem;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.img_badge)
    ImageView imgBadge;

    private int width;
    private int height;
    private int ruleWidth;
    private int ruleHeight;

    @Override
    public String cpmSource() {
        return "merchant_serve_channel";
    }

    public WeddingShootingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        Point point = CommonUtil.getDeviceSize(context);
        width = (point.x - CommonUtil.dp2px(context, 28)) / 2;
        height = Math.round(width * 5 / 8);
        llItem.getLayoutParams().width = width;
        rlCover.getLayoutParams().height = height;
        this.ruleWidth = CommonUtil.dp2px(itemView.getContext(), 60);
        this.ruleHeight = CommonUtil.dp2px(itemView.getContext(), 50);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Work work = getItem();
                Context context = v.getContext();
                if (work != null && work.getId() > 0) {
                    Intent intent = new Intent(context, WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(Context mContext, Work work, int position, int viewType) {
        if (work != null) {
            itemView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(ImagePath.buildPath(work.getCoverPath())
                            .width(width)
                            .height(height)
                            .cropPath())
                    .apply(new RequestOptions().override(width, height)
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivCover);
            tvTitle.setText(work.getTitle());
            if (work.getCommodityType() != 0) {
                llShowPrice.setVisibility(View.GONE);
            } else {
                llShowPrice.setVisibility(View.VISIBLE);
                tvPrice.setText(CommonUtil.formatDouble2String(work.getShowPrice()));
            }

            ivPlay.setVisibility(work.getMediaVideosCount() == 0 ? View.GONE : View.VISIBLE);
            Merchant merchant = work.getMerchant();
            if (merchant != null) {
                tvName.setText(merchant.getName());
                tvCommentCount.setText(String.format("%s人评价", merchant.getCommentCount()));
                int levelId = 0;
                if (merchant.getGrade() == 2) {
                    levelId = R.mipmap.icon_merchant_level2_30_30;
                } else if (merchant.getGrade() == 3) {
                    levelId = R.mipmap.icon_merchant_level3_30_30;
                } else if (merchant.getGrade() == 4) {

                    levelId = R.mipmap.icon_merchant_level4_30_30;
                }
                if (levelId == 0) {
                    imgLevel.setVisibility(View.GONE);
                } else {
                    imgLevel.setVisibility(View.VISIBLE);
                    imgLevel.setImageResource(levelId);
                }
                imgBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
                imgRefund.setVisibility(!CommonUtil.isCollectionEmpty(merchant.getChargeBack()) ?
                        View.VISIBLE : View.GONE);
                imgPromise.setVisibility(!CommonUtil.isCollectionEmpty(merchant
                        .getMerchantPromise()) ? View.VISIBLE : View.GONE);
                imgCoupon.setVisibility(merchant.isCoupon() ? View.VISIBLE : View.GONE);
            }
            WorkRule rule = work.getRule();
            if (rule == null || TextUtils.isEmpty(rule.getShowImg())) {
                imgBadge.setVisibility(View.GONE);
            } else {
                imgBadge.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(ImagePath.buildPath(rule.getShowImg())
                                .width(ruleWidth)
                                .height(ruleHeight)
                                .cropPath())
                        .into(imgBadge);
            }
        }
    }
}
