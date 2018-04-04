package me.suncloud.marrymemo.adpter.finder.viewholder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;

/**
 * 专题viewHolder
 * Created by chen_bin on 2017/1/3 0003.
 */
public class SubPageViewHolder extends BaseViewHolder<TopicUrl> {
    @BindView(R.id.begin_at_layout)
    LinearLayout beginAtLayout;
    @BindView(R.id.tv_begin_at)
    TextView tvBeginAt;
    @BindView(R.id.cover_layout)
    RelativeLayout coverLayout;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_summary)
    TextView tvSummary;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.line_layout)
    View lineLayout;
    private int imageWidth;
    private int imageHeight;

    public SubPageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x;
        this.imageHeight = Math.round(imageWidth / 2.0f);
        this.coverLayout.getLayoutParams().width = imageWidth;
        this.coverLayout.getLayoutParams().height = imageHeight;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicUrl topic = getItem();
                if (topic != null && topic.getId() > 0) {
                    Intent intent = new Intent(v.getContext(), SubPageDetailActivity.class);
                    intent.putExtra("id", topic.getId());
                    v.getContext()
                            .startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void setViewData(
            final Context context, final TopicUrl topic, final int position, int viewType) {
        if (topic == null) {
            return;
        }
        Glide.with(context)
                .load(ImagePath.buildPath(topic.getListImg())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(topic.getGoodTitle());
        if (TextUtils.isEmpty(topic.getSummary())) {
            tvSummary.setVisibility(View.GONE);
        } else {
            tvSummary.setVisibility(View.VISIBLE);
            tvSummary.setText(topic.getSummary());
        }
        tvWatchCount.setText(String.valueOf(topic.getWatchCount()));
    }

    public void setShowBeginAt(boolean showBeginAt, TopicUrl topic) {
        if (!showBeginAt) {
            beginAtLayout.setVisibility(View.GONE);
        } else {
            beginAtLayout.setVisibility(View.VISIBLE);
            tvBeginAt.setText(topic.getBeginAt()
                    .toString(itemView.getContext()
                            .getString(R.string.format_date_type15, Locale.getDefault())));
        }
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }
}