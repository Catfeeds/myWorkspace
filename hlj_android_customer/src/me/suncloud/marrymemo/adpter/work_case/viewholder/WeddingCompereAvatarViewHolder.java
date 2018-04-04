package me.suncloud.marrymemo.adpter.work_case.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerMerchantViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * Created by hua_rong on 2017/8/3.
 * 婚礼司仪头部头像
 */
public class WeddingCompereAvatarViewHolder extends TrackerMerchantViewHolder {

    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    private int radius;
    private Context context;

    @Override
    public String cpmSource() {
        return "merchant_serve_channel";
    }

    public WeddingCompereAvatarViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
        radius = CommonUtil.dp2px(context, 50);
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    public boolean hitTrackOnly() {
        return true;
    }

    @Override
    protected void setViewData(Context mContext, final Merchant merchant, int position, int viewType) {
        if (merchant != null && merchant.getId() > 0) {
            itemView.setVisibility(View.VISIBLE);
            String coverPath = merchant.getLogoPath();
            Glide.with(context)
                    .load(ImagePath.buildPath(coverPath)
                            .width(radius)
                            .height(radius)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivAvatar);
            tvName.setText(merchant.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MerchantDetailActivity.class);
                    intent.putExtra("id", merchant.getId());
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }
    }
}

