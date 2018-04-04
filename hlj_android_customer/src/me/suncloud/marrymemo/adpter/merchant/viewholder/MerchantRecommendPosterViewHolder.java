package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantRecommendPosterItem;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;

/**
 * 大图模式列表
 * Created by chen_bin on 2017/5/19 0019.
 */
public class MerchantRecommendPosterViewHolder extends BaseViewHolder<MerchantRecommendPosterItem> {
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.img_cover2)
    ImageView imgCover2;
    @BindView(R.id.line_layout)
    View lineLayout;
    private int imageWidth;
    private int imageHeight;

    private int showType;
    private Merchant merchant;

    public MerchantRecommendPosterViewHolder(ViewGroup parent, int showType, Merchant merchant) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_recommend_poster_list_item, parent, false), showType);
        this.merchant = merchant;
    }

    public MerchantRecommendPosterViewHolder(View itemView, int showType) {
        super(itemView);
        this.showType = showType;
        ButterKnife.bind(this, itemView);
        Point point = CommonUtil.getDeviceSize(itemView.getContext());
        switch (showType) {
            case MerchantRecommendPosterItem.DOUBLE_IMAGE:
                imageWidth = (point.x - CommonUtil.dp2px(itemView.getContext(), 2)) / 2;
                imageHeight = Math.round(imageWidth * 342.0f / 318.0f);
                imgCover.getLayoutParams().width = imageWidth;
                imgCover.getLayoutParams().height = imageHeight;
                imgCover2.getLayoutParams().width = imageWidth;
                imgCover2.getLayoutParams().height = imageHeight;
                break;
            case MerchantRecommendPosterItem.SIMPLE_VERTICAL_IMAGE:
                imageWidth = point.x;
                imageHeight = Math.round(imageWidth * 614.0f / 640.0f);
                imgCover.getLayoutParams().width = imageWidth;
                imgCover.getLayoutParams().height = imageHeight;
                break;
            default:
                imageWidth = point.x;
                imageHeight = Math.round(imageWidth * 5.0f / 8.0f);
                imgCover.getLayoutParams().width = imageWidth;
                imgCover.getLayoutParams().height = imageHeight;
                break;
        }
        imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Poster poster = getItem().getPosters()
                        .get(0);
                if (poster.getTargetType() == 99 && merchant != null) {
                    Intent intent = new Intent(v.getContext(), WSCustomerChatActivity.class);
                    intent.putExtra("user", merchant.toUser());
                    intent.putExtra("ws_track", ModuleUtils.getWSTrack(merchant, poster.getPath()));
                    if (merchant.getContactPhone() != null && !merchant.getContactPhone()
                            .isEmpty()) {
                        intent.putStringArrayListExtra("contact_phones",
                                merchant.getContactPhone());
                    }
                    v.getContext()
                            .startActivity(intent);
                    return;
                }
                BannerUtil.bannerJump(v.getContext(), poster, null);
            }
        });
        imgCover2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Poster poster = getItem().getPosters()
                        .get(1);
                if (poster.getTargetType() == 99 && merchant != null) {
                    Intent intent = new Intent(v.getContext(), WSCustomerChatActivity.class);
                    intent.putExtra("user", merchant.toUser());
                    intent.putExtra("ws_track", ModuleUtils.getWSTrack(merchant, poster.getPath()));
                    if (merchant.getContactPhone() != null && !merchant.getContactPhone()
                            .isEmpty()) {
                        intent.putStringArrayListExtra("contact_phones",
                                merchant.getContactPhone());
                    }
                    v.getContext()
                            .startActivity(intent);
                    return;
                }
                BannerUtil.bannerJump(v.getContext(), poster, null);
            }
        });
    }


    @Override
    protected void setViewData(
            Context mContext,
            MerchantRecommendPosterItem posterItem,
            final int position,
            int viewType) {
        lineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        if (posterItem == null) {
            return;
        }
        Poster poster;
        Poster poster2;
        if (CommonUtil.isCollectionEmpty(posterItem.getPosters())) {
            poster = null;
            poster2 = null;
        } else {
            poster = posterItem.getPosters()
                    .get(0);
            if (showType != MerchantRecommendPosterItem.DOUBLE_IMAGE || posterItem.getPosters()
                    .size() < 2) {
                poster2 = null;
            } else {
                poster2 = posterItem.getPosters()
                        .get(1);
            }
        }
        if (poster == null) {
            imgCover.setVisibility(View.GONE);
        } else {
            imgCover.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(ImagePath.buildPath(poster.getPath())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
        }
        if (poster2 == null) {
            imgCover2.setVisibility(View.GONE);
        } else {
            imgCover2.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(ImagePath.buildPath(poster2.getPath())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover2);
        }
    }
}
