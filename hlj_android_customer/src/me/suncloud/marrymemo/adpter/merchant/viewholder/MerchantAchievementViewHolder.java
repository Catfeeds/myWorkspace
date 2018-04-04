package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.BannerUtil;

/**
 * 商家成就
 * Created by chen_bin on 2017/5/22 0022.
 */
public class MerchantAchievementViewHolder extends BaseViewHolder<Poster> {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.line_layout)
    View lineLayout;

    public MerchantAchievementViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerUtil.bannerJump(v.getContext(), getItem(), null);
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, Poster poster, int position, int viewType) {
        if (poster == null) {
            return;
        }
        tvTitle.setText(poster.getTitle());
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

}
