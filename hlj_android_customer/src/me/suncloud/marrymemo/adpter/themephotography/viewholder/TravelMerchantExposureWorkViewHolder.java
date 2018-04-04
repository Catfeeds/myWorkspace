package me.suncloud.marrymemo.adpter.themephotography.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * 旅拍专场套餐列表
 * Created by chen_bin on 2017/5/15 0015.
 */
public class TravelMerchantExposureWorkViewHolder extends BaseViewHolder<Work> {
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_discount)
    TextView tvDiscount;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private int imageWidth;
    private int imageHeight;

    public TravelMerchantExposureWorkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 120);
        this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 75);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Work work = getItem();
                if (work != null && work.getId() > 0) {
                    Intent intent = new Intent(view.getContext(), WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    view.getContext()
                            .startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, Work work, int position, int viewType) {
        if (work == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(work.getCoverPath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        double ratio = 1.0;
        if (work.getMarketPrice() > 0) {
            ratio = work.getShowPrice() / work.getMarketPrice();
        } else if (work.getActualPrice() > 0) {
            ratio = work.getShowPrice() / work.getActualPrice();
        }
        if (ratio <= 0 || ratio == 1) {
            tvDiscount.setVisibility(View.GONE);
        } else {
            tvDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText(Math.floor(ratio * 100) / 10 + "折");
        }
        tvTitle.setText(work.getTitle());
    }
}