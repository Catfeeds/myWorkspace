package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen_bin on 2017/6/28 0028.
 */
public class SearchMerchantBriefInfoViewHolder extends BaseViewHolder<Merchant> {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_address)
    TextView tvAddress;
    @BindView(R2.id.line_layout)
    View lineLayout;
    private int imageWidth;
    private int imageHeight;
    private OnSelectMerchantListener onSelectMerchantListener;


    public SearchMerchantBriefInfoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 48);
        this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 48);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectMerchantListener != null) {
                    onSelectMerchantListener.onSelectMerchant(getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, Merchant merchant, int position, int viewType) {
        if (merchant == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvName.setText(merchant.getName());
        tvAddress.setText(merchant.getAddress());
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnSelectMerchantListener(OnSelectMerchantListener onSelectMerchantListener) {
        this.onSelectMerchantListener = onSelectMerchantListener;
    }

    public interface OnSelectMerchantListener {
        void onSelectMerchant(Merchant merchant);
    }

}
