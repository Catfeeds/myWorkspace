package me.suncloud.marrymemo.adpter.themephotography;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.themephotography.Guide;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;

/**
 * Created by luohanlin on 2017/10/24.
 */

public class UnitCityGuidesAdapter extends RecyclerView.Adapter<BaseViewHolder<Guide>> {

    private Context context;
    private List<Guide> guides;

    private int imgWidth;
    private int imgHeight;
    private int bigImgWidth;
    private int bigImgHeight;

    public UnitCityGuidesAdapter(
            Context context, List<Guide> guideList) {
        this.context = context;
        this.guides = guideList;
        this.imgWidth = CommonUtil.dp2px(context, 210);
        this.imgHeight = CommonUtil.dp2px(context, 130);
        this.bigImgWidth = CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 32);
        this.bigImgHeight = bigImgWidth * 130 / 210;
    }

    @Override
    public BaseViewHolder<Guide> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.unit_city_guide_h_item, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Guide> holder, int position) {
        if (holder instanceof GuideViewHolder) {
            holder.setView(context, guides.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        if (guides == null) {
            return 0;
        } else if (guides.size() > 3) {
            return 3;
        } else {
            return guides.size();
        }
    }

    class GuideViewHolder extends BaseViewHolder<Guide> {
        private final int width;
        private final int height;
        @BindView(R.id.start_padding)
        View startPadding;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_watch_count)
        TextView tvWatchCount;
        @BindView(R.id.tv_comment_count)
        TextView tvCommentCount;
        @BindView(R.id.tv_praise_count)
        TextView tvPraiseCount;
        @BindView(R.id.end_padding)
        View endPadding;

        View view;

        GuideViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            width = getItemCount() == 1 ? bigImgWidth : imgWidth;
            height = getItemCount() == 1 ? bigImgHeight : imgHeight;
            imgCover.getLayoutParams().width = width;
            imgCover.getLayoutParams().height = height;
        }

        @Override
        protected void setViewData(
                Context mContext, final Guide guide, int position, int viewType) {
            startPadding.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            endPadding.setVisibility(position == guides.size() - 1 ? View.VISIBLE : View.GONE);


            HljVTTagger.buildTagger(view)
                    .tagName(HljTaggerName.GUIDE)
                    .atPosition(position)
                    .dataId(guide.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_GUIDE)
                    .tag();

            MultiTransformation transformation = new MultiTransformation(new CenterCrop(),
                    new RoundedCorners(CommonUtil.dp2px(context, 4)));
            String entityType = guide.getEntityType();
            String imgPath = null;
            String title = null;
            int watchCount = 0;
            int commentCount = 0;
            int likeCount = 0;
            switch (entityType) {
                case "SubPage":
                    TopicUrl topicUrl = (TopicUrl) guide.getEntity();
                    if (topicUrl != null) {
                        imgPath = topicUrl.getListImg();
                        title = topicUrl.getGoodTitle();
                        watchCount = topicUrl.getWatchCount();
                        commentCount = topicUrl.getCommentCount();
                        likeCount = topicUrl.getPraiseCount();
                    }
                    break;
                case "CommunityThread":
                    CommunityThread thread = (CommunityThread) guide.getEntity();
                    if (thread != null) {
                        title = thread.getShowTitle();
                        if (!CommonUtil.isCollectionEmpty(thread.getShowPhotos())) {
                            imgPath = thread.getShowPhotos()
                                    .get(0)
                                    .getImagePath();
                        }
                        watchCount = thread.getClickCount();
                        commentCount = thread.getPostCount();
                        likeCount = thread.getPraisedSum();
                    }
                    break;
                default:
                    break;
            }
            tvTitle.setText(title);
            tvWatchCount.setText(String.valueOf(watchCount));
            tvCommentCount.setText(String.valueOf(commentCount));
            tvPraiseCount.setText(String.valueOf(likeCount));
            Glide.with(mContext)
                    .load(ImagePath.buildPath(imgPath)
                            .width(width)
                            .height(height)
                            .ignoreFormat(true)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .transform(transformation))
                    .into(imgCover);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    String type = guide.getEntityType();
                    switch (type) {
                        case "SubPage":
                            TopicUrl topicUrl = (TopicUrl) guide.getEntity();
                            intent = new Intent(context, SubPageDetailActivity.class);
                            intent.putExtra("id", topicUrl.getId());
                            break;
                        case "CommunityThread":
                            CommunityThread thread = (CommunityThread) guide.getEntity();
                            intent = new Intent(context, CommunityThreadDetailActivity.class);
                            intent.putExtra("id", thread.getId());
                            break;
                    }
                    if (intent != null) {
                        v.getContext()
                                .startActivity(intent);
                    }
                }
            });

        }
    }
}
