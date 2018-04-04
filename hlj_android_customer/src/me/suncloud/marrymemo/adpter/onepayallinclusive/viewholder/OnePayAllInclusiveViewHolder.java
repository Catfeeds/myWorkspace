package me.suncloud.marrymemo.adpter.onepayallinclusive.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by jinxin on 2018/3/14 0014.
 */

public class OnePayAllInclusiveViewHolder extends TrackerWorkViewHolder {
    @BindView(R.id.line)
    View line;
    @BindView(R.id.img_work_cover)
    ImageView imgWorkCover;
    @BindView(R.id.img_installment)
    ImageView imgInstallment;
    @BindView(R.id.img_work_play_button)
    ImageView imgWorkPlayButton;
    @BindView(R.id.img_work_badge)
    ImageView imgWorkBadge;
    @BindView(R.id.layout_img)
    RelativeLayout layoutImg;
    @BindView(R.id.tv_property)
    TextView tvProperty;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_title)
    LinearLayout layoutTitle;
    @BindView(R.id.tv_area_name)
    TextView tvAreaName;
    @BindView(R.id.address_layout)
    LinearLayout addressLayout;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.img_one_all)
    ImageView imgOneAll;
    @BindView(R.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R.id.show_price_layout)
    LinearLayout showPriceLayout;

    private Context mContext;
    private int width;
    private int coverHeight;
    private int badgeWidth;
    private int badgeHeight;

    public OnePayAllInclusiveViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        width = CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext, 32);
        coverHeight = Math.round(width * 10.0F / 16.0F);
        badgeWidth = Math.round(itemView.getResources()
                .getDisplayMetrics().density * 120);
        badgeHeight = CommonUtil.dp2px(itemView.getContext(), 100);
        layoutImg.getLayoutParams().height = coverHeight;
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            Context mContext, final Work work, final int position, int viewType) {
        setWork(mContext, work);
        setMerchant(mContext, work);
        setAddress(mContext, work);
    }

    private void setWork(Context context, Work work) {
        if (work == null) {
            return;
        }
        Glide.with(context)
                .load(ImagePath.buildPath(work.getCoverPath())
                        .width(width)
                        .height(coverHeight)
                        .cropPath())
                .into(imgWorkCover);
        if (work.getMediaVideosCount() > 0) {
            imgWorkPlayButton.setVisibility(View.VISIBLE);
        } else {
            imgWorkPlayButton.setVisibility(View.GONE);
        }
        tvTitle.setText(work.getTitle());
        tvShowPrice.setText(Util.formatDouble2String(work.getShowPrice()));
        WorkRule rule = work.getRule();
        if (rule != null && !TextUtils.isEmpty(rule.getBigImg())) {
            imgWorkBadge.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(ImagePath.buildPath(rule.getBigImg())
                            .width(badgeWidth)
                            .height(badgeHeight)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgWorkBadge);
        } else {
            imgWorkBadge.setVisibility(View.GONE);
        }
        imgInstallment.setVisibility(View.GONE);
    }

    private void setMerchant(Context mContext, Work work) {
        if (work == null || work.getMerchant() == null) {
            return;
        }
        Merchant merchant = work.getMerchant();
        String propertyName = merchant.getPropertyName();
        tvProperty.setText(propertyName);
        tvProperty.setVisibility(View.GONE);
        tvMerchantName.setText(merchant.getName());
    }

    private void setAddress(Context mContext, Work work) {
        if (work == null) {
            return;
        }
        String address = getAreaName(work);
        if (TextUtils.isEmpty(address)) {
            addressLayout.setVisibility(View.GONE);
        } else {
            tvAreaName.setText(address);
            addressLayout.setVisibility(View.VISIBLE);
        }
    }

    private String getAreaName(Work work) {
        String areaName = null;
        ParentArea parentArea = work.getMerchant()
                .getShopArea();
        if (parentArea != null) {
            areaName = parentArea.getName();
            if (work.isLvPai()) {
                parentArea = parentArea.getParentArea();
                if (parentArea != null) {
                    areaName = parentArea.getName();
                }
            }
        }
        return areaName;
    }
}
