package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by werther on 16/12/8.
 */

public class CommonTopicViewHolder extends BaseViewHolder<TopicUrl> {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_category)
    ImageView imgCategory;
    @BindView(R2.id.cover_layout)
    RelativeLayout coverLayout;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_read_count)
    TextView tvReadCount;
    @BindView(R2.id.img_thumb_up)
    ImageView imgThumbUp;
    @BindView(R2.id.tv_praise_count)
    TextView tvPraiseCount;
    @BindView(R2.id.check_praised)
    CheckableLinearButton checkPraised;
    @BindView(R2.id.praise_layout)
    LinearLayout praiseLayout;
    @BindView(R2.id.comments_count)
    TextView commentsCount;
    @BindView(R2.id.comments_layout)
    LinearLayout commentsLayout;
    @BindView(R2.id.bottom_thread_view)
    RelativeLayout bottomThreadView;
    @BindView(R2.id.line_layout)
    View lineLayout;

    private OnPraisedCheckListener onPraisedCheckListener;
    private OnItemClickListener onItemClickListener;
    private LongSparseArray<String> categories;
    private final int categoryHeight;
    private int coverWidth;
    private int coverHeight;

    public void setOnPraisedCheckListener(OnPraisedCheckListener onPraisedCheckListener) {
        this.onPraisedCheckListener = onPraisedCheckListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CommonTopicViewHolder(View itemView, LongSparseArray<String> categories) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.categories = categories;
        Point point = CommonUtil.getDeviceSize(itemView.getContext());
        coverWidth = point.x * 73 / 160;
        coverHeight = coverWidth * 3 / 4;
        imgCover.getLayoutParams().width = coverWidth;
        imgCover.getLayoutParams().height = coverHeight;
        itemView.getLayoutParams().height = coverHeight;
        categoryHeight = CommonUtil.dp2px(itemView.getContext(), 20);
    }

    @Override
    protected void setViewData(
            Context mContext, final TopicUrl item, final int position, int viewType) {
        if (item == null) {
            return;
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, item);
                }
            }
        });
        Glide.with(mContext)
                .load(ImageUtil.getImagePath(item.getListImg(), coverWidth))
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgCover);
        String path = ImageUtil.getImagePath(categories.get(item.getCategoryId()), coverWidth);
        if (!TextUtils.isEmpty(path)) {
            Glide.with(mContext)
                    .load(path)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_empty_common))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(
                                Drawable resource, Transition<? super Drawable> transition) {
                            if (resource != null) {
                                float width = resource.getIntrinsicWidth() * 1.0f;
                                float height = resource.getIntrinsicHeight() * 1.0f;
                                if (width != 0 && height != 0) {
                                    imgCategory.getLayoutParams().width = Math.round
                                            (categoryHeight * width / height);
                                    imgCategory.setImageDrawable(resource);
                                }
                            }
                        }
                    });
        } else {
            Glide.with(mContext)
                    .clear(imgCategory);
            imgCategory.setImageBitmap(null);
        }

        tvTitle.setText(item.getGoodTitle());
        tvReadCount.setText(String.valueOf(item.getWatchCount()) + "阅读");

        commentsCount.setText(String.valueOf(item.getCommentCount()));
        tvPraiseCount.setText(item.getPraiseCount() > 0 ? String.valueOf(item.getPraiseCount()) :
                "赞");
        checkPraised.setChecked(item.isPraised());
        checkPraised.setOnCheckedChangeListener(new CheckableLinearLayout.OnCheckedChangeListener
                () {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                if (onPraisedCheckListener != null) {
                    onPraisedCheckListener.onPraiseCheck(checkPraised, tvPraiseCount);
                }
            }
        });
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public interface OnPraisedCheckListener {
        void onPraiseCheck(CheckableLinearButton checkPraised, TextView tvPraiseCount);
    }

}
