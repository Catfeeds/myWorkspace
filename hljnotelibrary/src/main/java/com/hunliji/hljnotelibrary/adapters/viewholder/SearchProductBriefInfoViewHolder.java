package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen_bin on 2017/6/28 0028.
 */
public class SearchProductBriefInfoViewHolder extends BaseViewHolder<ShopProduct> {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R2.id.line_layout)
    View lineLayout;
    private int imageWidth;
    private int imageHeight;
    private OnSelectProductListener onSelectProductListener;

    public SearchProductBriefInfoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 48);
        this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 48);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectProductListener != null) {
                    onSelectProductListener.onSelectProduct(getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, ShopProduct product, int position, int viewType) {
        if (product == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(product.getCoverImage()
                        .getImagePath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(product.getTitle());
        tvShowPrice.setText(CommonUtil.formatDouble2String(product.getShowPrice()));
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnSelectProductListener(OnSelectProductListener onSelectProductListener) {
        this.onSelectProductListener = onSelectProductListener;
    }

    public interface OnSelectProductListener {
        void onSelectProduct(ShopProduct product);
    }
}