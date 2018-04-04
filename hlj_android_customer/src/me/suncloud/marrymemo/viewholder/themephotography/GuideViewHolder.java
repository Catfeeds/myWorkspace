package me.suncloud.marrymemo.viewholder.themephotography;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.themephotography.Guide;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeGuideListActivity;

/**
 * Created by jinxin on 2016/10/13.
 */

public class GuideViewHolder extends RecyclerView.ViewHolder {
    public View itemView;
    public View allInfo;
    public View arrow;
    public TextView tvTitle;
    public SliderLayout slider;
    public CirclePageExIndicator indicator;
    public List<Guide> guides;
    public GuideAdapter adapter;
    public int itemHeight;
    public Context mContext;
    public Point point;
    public DisplayMetrics dm;
    public JourneyTheme journeyTheme;

    public GuideViewHolder(View itemView, Context context, JourneyTheme theme) {
        super(itemView);
        this.itemView = itemView;
        mContext = context;
        point = JSONUtil.getDeviceSize(mContext);
        dm = mContext.getResources()
                .getDisplayMetrics();
        this.journeyTheme = theme;
        allInfo = itemView.findViewById(R.id.tv_all_info);
        arrow = itemView.findViewById(R.id.iv_arrow_right);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        slider = (SliderLayout) itemView.findViewById(R.id.slider);
        indicator = (CirclePageExIndicator) itemView.findViewById(R.id.flow_indicator);

        allInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ThemeGuideListActivity.class);
                intent.putExtra("id", journeyTheme.getId());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });
        guides = new ArrayList<>();
        adapter = new GuideAdapter();
        //计算 32号字体高度
        Paint paint = new Paint();
        paint.setTextSize(32);
        Paint.FontMetrics fm = paint.getFontMetrics();
        int height = (int) Math.ceil(fm.descent - fm.top) + 2;
        itemHeight = height + Math.round(dm.density * 87);
    }

    public void setGuides(List<Guide> guides) {
        if (guides == null) {
            return;
        }
        this.guides.clear();
        this.guides.addAll(guides);
        if (this.guides.isEmpty()) {
            allInfo.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
        }
        slider.setPagerAdapter(adapter);
        slider.setCustomIndicator(indicator);
        slider.setPresetTransformer(4);
        slider.getLayoutParams().height = itemHeight;
        adapter.notifyDataSetChanged();
        slider.startAutoCycle();
        indicator.notifyDataSetChanged();
        slider.requestLayout();
    }

    class GuideAdapter extends PagerAdapter {
        private int imageWidth;
        private int imageHeight;

        public GuideAdapter() {
            this.imageWidth = CommonUtil.dp2px(mContext, 116);
            this.imageHeight = CommonUtil.dp2px(mContext, 73);
        }

        @Override
        public int getCount() {
            return guides.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(
                ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.journey_guide_entity_item, container, false);
            GuideAdapter.GuideEntityViewHolder viewHolder = new GuideAdapter.GuideEntityViewHolder(
                    convertView);
            Guide guide = guides.get(position);
            if (guide != null) {
                String entityType = guide.getEntityType();
                String imgPath = null;
                String title = null;
                String workTitle = null;
                String readCount = null;
                switch (entityType) {
                    case "SubPage":
                        TopicUrl topicUrl = (TopicUrl) guide.getEntity();
                        if (topicUrl != null) {
                            imgPath = topicUrl.getListImg();
                            title = topicUrl.getGoodTitle();
                            workTitle = topicUrl.getSummary();
                            readCount = mContext.getString(R.string.label_twitter_read_count,
                                    String.valueOf(topicUrl.getWatchCount()));
                        }
                        break;
                    case "CommunityThread":
                        CommunityThread thread = (CommunityThread) guide.getEntity();
                        if (thread != null) {
                            title = thread.getShowTitle();
                            workTitle = thread.getShowSubtitle();
                            if (!CommonUtil.isCollectionEmpty(thread.getShowPhotos())) {
                                imgPath = thread.getShowPhotos()
                                        .get(0)
                                        .getImagePath();
                            }
                            readCount = mContext.getString(R.string.label_twitter_read_count,
                                    String.valueOf(thread.getClickCount()));
                        }
                        break;
                    default:
                        break;
                }
                viewHolder.tvTitle.setText(title);
                viewHolder.tvDes.setText(workTitle);
                viewHolder.tvRead.setText(readCount);
                Glide.with(mContext)
                        .load(ImagePath.buildPath(imgPath)
                                .width(imageWidth)
                                .height(imageHeight)
                                .cropPath())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                        .into(viewHolder.imgCover);
                viewHolder.itemView.setTag(guide);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Guide guide = (Guide) v.getTag();
                        if (guide == null) {
                            return;
                        }
                        Intent intent = null;
                        String type = guide.getEntityType();
                        switch (type) {
                            case "SubPage":
                                TopicUrl topicUrl = (TopicUrl) guide.getEntity();
                                intent = new Intent(mContext, SubPageDetailActivity.class);
                                intent.putExtra("id", topicUrl.getId());
                                break;
                            case "CommunityThread":
                                CommunityThread thread = (CommunityThread) guide.getEntity();
                                intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                                intent.putExtra("id", thread.getId());
                                break;
                        }
                        if (intent != null) {
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });

            }
            container.addView(convertView);
            return convertView;
        }

        class GuideEntityViewHolder {
            TextView tvTitle;
            ImageView imgCover;
            TextView tvDes;
            TextView tvRead;
            View itemView;

            public GuideEntityViewHolder(View view) {
                itemView = view;
                tvTitle = (TextView) view.findViewById(R.id.tv_title);
                imgCover = (ImageView) view.findViewById(R.id.img_cover);
                tvDes = (TextView) view.findViewById(R.id.tv_des);
                tvRead = (TextView) view.findViewById(R.id.tv_read);
            }
        }
    }
}
