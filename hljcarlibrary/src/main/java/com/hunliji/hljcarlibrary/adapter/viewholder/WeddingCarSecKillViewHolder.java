package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.viewholder.tracker.TrackerSecKillViewHolder;
import com.hunliji.hljcarlibrary.models.SecKill;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2018/1/3 0003.
 */

public class WeddingCarSecKillViewHolder extends TrackerSecKillViewHolder {

    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_lowest)
    ImageView imgLowest;
    @BindView(R2.id.tv_lead_car)
    TextView tvLeadCar;
    @BindView(R2.id.layout_main_car)
    LinearLayout layoutMainCar;
    @BindView(R2.id.tv_follow_car)
    TextView tvFollowCar;
    @BindView(R2.id.layout_follow_car)
    LinearLayout layoutFollowCar;
    @BindView(R2.id.tv_price_hint)
    TextView tvPriceHint;
    @BindView(R2.id.tv_actual_price)
    TextView tvActualPrice;
    @BindView(R2.id.tv_market_price)
    TextView tvMarketPrice;
    @BindView(R2.id.layout_price)
    LinearLayout layoutPrice;
    @BindView(R2.id.btn_now)
    Button btnNow;
    @BindView(R2.id.tv_title)
    TextView tvTitle;

    private Context mContext;
    private int height;
    private int width;
    private OnItemClickListener<SecKill> onItemClickListener;
    private onWeddingCarSecKillClickListener onWeddingCarSecKillClickListener;

    public WeddingCarSecKillViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        width = CommonUtil.getDeviceSize(mContext).x;
        height = Math.round(width * 9.0F / 16);
        tvMarketPrice.getPaint()
                .setAntiAlias(true);
        tvMarketPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    public View trackerChatBtnView() {
        return btnNow;
    }

    public void setOnItemClickListener(OnItemClickListener<SecKill> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnWeddingCarSecKillClickListener(
            WeddingCarSecKillViewHolder.onWeddingCarSecKillClickListener
                    onWeddingCarSecKillClickListener) {
        this.onWeddingCarSecKillClickListener = onWeddingCarSecKillClickListener;
    }


    @Override
    protected void setViewData(
            final Context mContext, final SecKill secKill, int position, int viewType) {
        if (secKill == null) {
            return;
        }
        imgCover.getLayoutParams().width = width;
        imgCover.getLayoutParams().height = height;
        Glide.with(mContext)
                .load(ImagePath.buildPath(secKill.getImg())
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgCover);

        imgLowest.setVisibility((secKill.getParams() != null && secKill.getParams()
                .isLowest()) ? View.VISIBLE : View.GONE);
        switch (secKill.getExtraData()
                .getType()) {
            case WeddingCarProduct.TYPE_WORK:
                tvTitle.setVisibility(View.GONE);
                layoutMainCar.setVisibility(View.VISIBLE);
                layoutFollowCar.setVisibility(View.VISIBLE);
                if (secKill.getExtraData() != null) {
                    tvLeadCar.setText(secKill.getExtraData()
                            .getMainCar());
                    tvFollowCar.setText(secKill.getExtraData()
                            .getShowSubCarTitle());
                }
                break;
            case WeddingCarProduct.TYPE_SELF:
                tvTitle.setText(secKill.getTitle());
                tvTitle.setVisibility(View.VISIBLE);
                layoutMainCar.setVisibility(View.GONE);
                layoutFollowCar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        tvActualPrice.setText(NumberFormatUtil.formatDouble2String(secKill.getExtraData()
                .getShowPrice()));
        tvMarketPrice.setText(mContext.getString(R.string.label_market_price2___car,
                NumberFormatUtil.formatDouble2String(secKill.getExtraData()
                        .getMarketPrice())));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getItemPosition(), secKill);
                }
            }
        });
        btnNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onWeddingCarSecKillClickListener != null) {
                    onWeddingCarSecKillClickListener.onBtnNow(secKill);
                }
            }
        });
    }


    public interface onWeddingCarSecKillClickListener {
        void onBtnNow(SecKill secKill);
    }
}
