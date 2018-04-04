package me.suncloud.marrymemo.adpter.product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;

/**
 * Created by luohanlin on 2017/11/8.
 */

public class ProductHomeTopicAdapter extends RecyclerView.Adapter<BaseViewHolder<ProductTopic>> {

    private Context context;
    private List<ProductTopic> topics;
    private int width;
    private int height;

    public static final int TYPE_ITEM = 1;

    public ProductHomeTopicAdapter(
            Context context) {
        this.context = context;
    }

    public void setTopics(List<ProductTopic> topics) {
        this.topics = topics;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    @Override
    public BaseViewHolder<ProductTopic> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.product_home_topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<ProductTopic> holder, int position) {
        if (holder instanceof TopicViewHolder) {
            holder.setView(context, topics.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return topics == null ? 0 : topics.size();
    }

    class TopicViewHolder extends BaseViewHolder<ProductTopic> {
        @BindView(R.id.start_padding)
        View startPadding;
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_sub_title)
        TextView tvSubTitle;
        @BindView(R.id.end_padding)
        View endPadding;

        TopicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            imgCover.getLayoutParams().width = width;
            imgCover.getLayoutParams().height = height;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductTopic topic = getItem();
                    if (topic == null) {
                        return;
                    }
                    if (topic.getType() == 3) {
                        Intent intent = new Intent(itemView.getContext(),
                                SubPageDetailActivity.class);
                        intent.putExtra("id", topic.getEntityId());
                        intent.putExtra("productSubPageId", topic.getId());
                        itemView.getContext()
                                .startActivity(intent);
                    } else if (!TextUtils.isEmpty(topic.getGotoUrl())) {
                        HljWeb.startWebView((Activity) itemView.getContext(), topic.getGotoUrl());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, ProductTopic item, int position, int viewType) {
            startPadding.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            endPadding.setVisibility(position == topics.size() - 1 ? View.VISIBLE : View.GONE);

            Glide.with(context)
                    .load(ImagePath.buildPath(item.getImgTitle())
                            .width(width)
                            .height(height)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgCover);
            tvTitle.setText(item.getTitle());
            tvPrice.setText(CommonUtil.formatDouble2String(item.getPrice()) + "元起");
            tvPrice.setVisibility(item.getPrice() > 0 ? View.VISIBLE : View.GONE);
            tvSubTitle.setText(item.getDesc());
        }
    }
}
