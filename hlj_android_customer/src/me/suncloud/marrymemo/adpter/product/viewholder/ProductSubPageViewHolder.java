package me.suncloud.marrymemo.adpter.product.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;

/**
 * 婚品专题通用的viewHolder
 * Created by chen_bin on 2016/11/28 0028.
 */
public class ProductSubPageViewHolder extends BaseViewHolder<ProductTopic> {
    @BindView(R.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    private int imageWidth;
    private int imageHeight;

    public ProductSubPageViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                32);
        this.imageHeight = Math.round(imageWidth / 2.0f);
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductTopic topic = getItem();
                if (topic == null) {
                    return;
                }
                if (topic.getType() == 3) {
                    Intent intent = new Intent(itemView.getContext(), SubPageDetailActivity.class);
                    intent.putExtra("id", topic.getEntityId());
                    intent.putExtra("productSubPageId", topic.getId());
                    itemView.getContext()
                            .startActivity(intent);
                    ((Activity) itemView.getContext()).overridePendingTransition(R.anim
                                    .slide_in_right,
                            R.anim.activity_anim_default);
                } else if (!TextUtils.isEmpty(topic.getGotoUrl())) {
                    HljWeb.startWebView((Activity) itemView.getContext(), topic.getGotoUrl());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            final Context context, final ProductTopic topic, final int position, int viewType) {
        if (topic == null) {
            return;
        }
        Glide.with(context)
                .load(ImagePath.buildPath(topic.getImgTitle())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate())
                .into(imgCover);
        tvTitle.setText(topic.getTitle());
        if (!TextUtils.isEmpty(topic.getDesc())) {
            tvDesc.setText(topic.getDesc());
            tvDesc.setVisibility(View.VISIBLE);
        } else {
            tvDesc.setVisibility(View.GONE);
        }
        tvPrice.setText(CommonUtil.formatDouble2String(topic.getPrice()) + "元起");
        tvPrice.setVisibility(topic.getPrice() > 0 ? View.VISIBLE : View.GONE);
    }
}
