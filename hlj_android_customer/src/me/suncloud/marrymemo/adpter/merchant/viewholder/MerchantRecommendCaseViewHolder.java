package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.app.Activity;
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
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerCaseViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.CaseDetailActivity;

/**
 * 商家主页推荐案例viewHolder
 * Created by chen_bin on 2017/5/22 0022.
 */
public class MerchantRecommendCaseViewHolder extends TrackerCaseViewHolder {
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private int imageWidth;
    private int imageHeight;

    public MerchantRecommendCaseViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 110);
        this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 68);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Work work = getItem();
                if (work != null && work.getId() > 0) {
                    Intent intent = new Intent(itemView.getContext(), CaseDetailActivity.class);
                    intent.putExtra("id", work.getId());
                    itemView.getContext()
                            .startActivity(intent);
                    ((Activity) itemView.getContext()).overridePendingTransition(R.anim
                                    .slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return itemView;
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
        tvTitle.setText(work.getTitle());
    }
}
