package me.suncloud.marrymemo.adpter.event.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.event.EventDetailActivity;

/**
 * 发现页首页活动小图
 * Created by chen_bin on 2017/6/24 0024.
 */
public class FinderSmallEventViewHolder extends BaseViewHolder<EventInfo> {
    @BindView(R.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    private int imageWidth;

    public FinderSmallEventViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                28) / 2;
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageWidth;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventInfo eventInfo = getItem();
                if (eventInfo != null && eventInfo.getId() > 0) {
                    Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                    intent.putExtra("id", eventInfo.getId());
                    v.getContext()
                            .startActivity(intent);
                    if (v.getContext() instanceof Activity) {
                        ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, EventInfo eventInfo, int position, int viewType) {
        if (eventInfo == null) {
            return;
        }
        String imagePath;
        if (!TextUtils.isEmpty(eventInfo.getFindImg())) {
            imagePath = eventInfo.getFindImg();
        } else {
            imagePath = eventInfo.getSurfaceImg();
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(imagePath)
                        .width(imageWidth)
                        .height(imageWidth)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        if (!eventInfo.isNeedSignUp() || eventInfo.isSignUpEnd()) {
            tvTitle.setText(eventInfo.getTitle());
        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder(" " + eventInfo.getTitle());
            Drawable drawable = ContextCompat.getDrawable(mContext,
                    R.mipmap.icon_sign_up_tag_132_32);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            builder.setSpan(new HljImageSpan(drawable), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvTitle.setText(builder);
        }
        tvWatchCount.setText(mContext.getString(R.string.label_be_interested_in__count__cv,
                eventInfo.getWatchCount()));
    }
}