package me.suncloud.marrymemo.adpter.newsearch.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerMerchantViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchWorkCaseFragment;

/**
 * Created by hua_rong on 2018/1/9
 * 店铺cpm
 */

public class SearchHeaderCpmMerchantViewHolder extends TrackerMerchantViewHolder {

    @BindView(R.id.img_merchant_logo)
    RoundedImageView imgMerchantLogo;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.img_merchant_cover)
    ImageView imgMerchantCover;
    @BindView(R.id.tv_extra)
    TextView tvExtra;
    @BindView(R.id.cpm_bottom_padding)
    View cpmBottomPadding;
    @BindView(R.id.cpm_layout)
    LinearLayout cpmLayout;
    @BindView(R.id.bottom_padding)
    View bottomPadding;
    private OnItemClickListener onItemClickListener;


    private int cpmCoverWidth;
    private int cpmCoverHeight;

    public SearchHeaderCpmMerchantViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        cpmCoverWidth = CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 32);
        cpmCoverHeight = cpmCoverWidth * 7 / 16;
        imgMerchantCover.getLayoutParams().width = cpmCoverWidth;
        imgMerchantCover.getLayoutParams().height = cpmCoverHeight;
    }

    @Override
    public String cpmSource() {
        return NewSearchWorkCaseFragment.CPM_SOURCE;
    }

    @Override
    public View trackerView() {
        return cpmLayout;
    }

    @Override
    protected void setViewData(
            Context context, Merchant merchant, int position, int viewType) {
        if (merchant != null) {
            itemView.setVisibility(View.VISIBLE);
            cpmBottomPadding.setVisibility(View.GONE);
            Glide.with(context)
                    .load(ImagePath.buildPath(merchant.getLogoPath())
                            .width(CommonUtil.dp2px(context, 24))
                            .height(CommonUtil.dp2px(context, 24))
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgMerchantLogo);
            Glide.with(context)
                    .load(ImagePath.buildPath(merchant.getCoverPath())
                            .width(cpmCoverWidth)
                            .height(cpmCoverHeight)
                            .cropPath())
                    .into(imgMerchantCover);
            tvMerchantName.setText(merchant.getName());
            tvExtra.setText("广告");
            cpmLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
        }
    }

    public void hideViewBottom() {
        bottomPadding.setVisibility(View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
