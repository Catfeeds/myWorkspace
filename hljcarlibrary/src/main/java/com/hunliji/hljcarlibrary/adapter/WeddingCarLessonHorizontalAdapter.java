package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.viewholder.tracker.TrackerCarLessonViewHolder;
import com.hunliji.hljcarlibrary.models.CarLesson;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/12/28.婚车必修课水平列表
 */

public class WeddingCarLessonHorizontalAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<CarLesson> carLessons;
    private Context mContext;
    private OnItemClickListener<CarLesson> onItemClickListener;

    public static final int ITEM = 1;

    public WeddingCarLessonHorizontalAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener<CarLesson> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCarLessons(List<CarLesson> carLessons) {
        this.carLessons = carLessons;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                return new CarLessonViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.car_lesson_horizontal_list_item___car, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case ITEM:
                holder.setView(mContext, getItem(position), position, type);
                break;
        }
    }

    private CarLesson getItem(int position) {
        if (carLessons != null && position < carLessons.size()) {
            return carLessons.get(position);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    @Override
    public int getItemCount() {
        return carLessons == null ? 0 : carLessons.size() > 3 ? 3 : carLessons.size();
    }

    class CarLessonViewHolder extends TrackerCarLessonViewHolder {
        @BindView(R2.id.img_car_lesson)
        RoundedImageView imgCarLesson;
        @BindView(R2.id.tv_car_lesson_type)
        TextView tvCarLessonType;
        @BindView(R2.id.tv_car_lesson_intro)
        TextView tvCarLessonIntro;
        @BindView(R2.id.tv_view_count)
        TextView tvViewCount;
        @BindView(R2.id.car_lesson_layout)
        View carLessonLayout;
        private int width;
        public int faceSize;

        CarLessonViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            width = (CommonUtil.getDeviceSize(view.getContext()).x - CommonUtil.dp2px(itemView
                            .getContext(),
                    60)) / 3;
            faceSize = CommonUtil.dp2px(view.getContext(), 14);
            carLessonLayout.getLayoutParams().width = width;
            imgCarLesson.getLayoutParams().width = width;
            imgCarLesson.getLayoutParams().height = width;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        public View trackerView() {
            return itemView;
        }

        @Override
        protected void setViewData(Context mContext, CarLesson item, int position, int viewType) {
            String imagePath = null;
            String intro;
            int viewCount;
            if (item.getSourceType() == CarLesson.THREAD_SOURCE) {
                CommunityThread communityThread = (CommunityThread) item.getEntityJson();
                if (!CommonUtil.isCollectionEmpty(communityThread.getShowPhotos())) {
                    imagePath = communityThread.getShowPhotos()
                            .get(0)
                            .getImagePath();
                }
                intro = communityThread.getShowTitle();
                viewCount = communityThread.getClickCount();
            } else {
                TopicUrl topicUrl = (TopicUrl) item.getEntityJson();
                imagePath = topicUrl.getListImg();
                intro = topicUrl.getGoodTitle();
                viewCount = topicUrl.getWatchCount();
            }
            Glide.with(imgCarLesson.getContext())
                    .load(ImagePath.buildPath(imagePath)
                            .width(width)
                            .height(width)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgCarLesson);
            tvCarLessonType.setText(item.getTypeStr());
            tvCarLessonIntro.setText(EmojiUtil.parseEmojiByText2(mContext,intro,
                    faceSize));
            tvViewCount.setText(String.valueOf(viewCount) + "人看过");
        }
    }
}
