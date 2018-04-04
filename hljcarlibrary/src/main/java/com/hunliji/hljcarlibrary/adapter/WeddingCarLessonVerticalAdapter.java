package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.viewholder.tracker.TrackerCarLessonViewHolder;
import com.hunliji.hljcarlibrary.models.CarLesson;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThreadPages;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2018/1/3.婚车必修课列表
 */

public class WeddingCarLessonVerticalAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<CarLesson> carLessons;
    private Context mContext;
    private OnItemClickListener<CarLesson> onItemClickListener;
    private OnPraiseClickListener<CarLesson> onPraiseClickListener;
    private View footerView;

    public static final int THREAD_TYPE = 1;
    public static final int SUB_PAGE_TYPE = 2;
    public static final int FOOTER_TYPE = 3;
    public static final int DEFAULT_TYPE = 4;

    public WeddingCarLessonVerticalAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener<CarLesson> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnPraiseClickListener<T> {
        void onPraiseClick(int position, T object, ImageView imgPraise, TextView tvPraiseCount);
    }

    public void setOnPraiseClickListener(OnPraiseClickListener<CarLesson> onPraiseClickListener) {
        this.onPraiseClickListener = onPraiseClickListener;
    }

    public void setCarLessons(List<CarLesson> carLessons) {
        this.carLessons = carLessons;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case THREAD_TYPE:
                return new CarThreadViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.car_thread_list_item___car, parent, false));
            case SUB_PAGE_TYPE:
                return new SubPageViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.car_sub_page_list_item___car, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder___cm, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case THREAD_TYPE:
                holder.setView(mContext, getItem(position), position, type);
                break;
            case SUB_PAGE_TYPE:
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
        if (footerView != null && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        }
        if (carLessons.get(position)
                .getSourceType() == CarLesson.THREAD_SOURCE) {
            return THREAD_TYPE;
        } else if (carLessons.get(position)
                .getSourceType() == CarLesson.TOPIC_SOURCE) {
            return SUB_PAGE_TYPE;
        }
        return DEFAULT_TYPE;
    }

    @Override
    public int getItemCount() {
        return (carLessons == null ? 0 : carLessons.size()) + (footerView != null ? 1 : 0);
    }

    class CarThreadViewHolder extends TrackerCarLessonViewHolder {
        @BindView(R2.id.tv_lesson_title)
        TextView tvLessonTitle;
        @BindView(R2.id.tv_lesson_content)
        TextView tvLessonContent;
        @BindView(R2.id.iv_lesson_right)
        ImageView ivLessonRight;
        @BindView(R2.id.iv_lesson_1)
        ImageView ivLesson1;
        @BindView(R2.id.iv_lesson_2)
        ImageView ivLesson2;
        @BindView(R2.id.iv_lesson_3)
        ImageView ivLesson3;
        @BindView(R2.id.lesson_img_view)
        LinearLayout lessonImgView;
        @BindView(R2.id.tv_lesson_img_count)
        TextView tvLessonImgCount;
        @BindView(R2.id.lesson_img_count_view)
        LinearLayout lessonImgCountView;
        @BindView(R2.id.lesson_cover_view)
        RelativeLayout lessonCoverView;
        @BindView(R2.id.iv_single_lesson_img)
        ImageView ivSingleLessonImg;
        @BindView(R2.id.tv_lesson_type)
        TextView tvLessonType;
        @BindView(R2.id.tv_praise_count)
        TextView tvPraiseCount;
        @BindView(R2.id.tv_watch_count)
        TextView tvWatchCount;
        @BindView(R2.id.car_lesson_view)
        LinearLayout carLessonView;
        @BindView(R2.id.img_praise)
        ImageView imgPraise;
        @BindView(R2.id.praise_layout)
        View praiseLayout;


        public int dynamicImgWidth;//动态图片宽度（3张）
        public int singleImgWidth;//富文本单张动态图片宽度
        public int singleImgHeight;//富文本单张动态图片高度
        public int rightImgWidth;//动态图片宽度(1,2张)
        public int faceSize;

        CarThreadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            faceSize = CommonUtil.dp2px(view.getContext(), 14);
            dynamicImgWidth = ((CommonUtil.getDeviceSize(view.getContext()).x - CommonUtil.dp2px(
                    view.getContext(),
                    42))) / 3;
            singleImgWidth = (CommonUtil.getDeviceSize(view.getContext()).x - CommonUtil.dp2px
                    (view.getContext(),
                    32));
            singleImgHeight = Math.round(singleImgWidth / 2);
            rightImgWidth = Math.round(CommonUtil.dp2px(view.getContext(), 108));
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
        protected void setViewData(
                Context mContext, final CarLesson item, int position, int viewType) {
            String title;
            String subTitle;
            int watchCount;
            int praiseCount;
            List<Photo> photos = null;
            CommunityThreadPages pages = null;
            if (item.getSourceType() == CarLesson.THREAD_SOURCE) {
                CommunityThread thread = (CommunityThread) item.getEntityJson();
                title = thread.getShowTitle();
                subTitle = thread.getShowSubtitle();
                watchCount = thread.getClickCount();
                praiseCount = thread.getPraisedSum();
                if (!CommonUtil.isCollectionEmpty(thread.getShowPhotos())) {
                    photos = thread.getShowPhotos();
                }
                pages = thread.getPages();
                imgPraise.setImageResource(thread.isPraised() ? R.mipmap
                        .icon_praised_soild_gold_30_24___car : R.mipmap
                        .icon_praise_gold_30_24___car);
                praiseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onPraiseClickListener != null) {
                            onPraiseClickListener.onPraiseClick(getAdapterPosition(),
                                    item,
                                    imgPraise,
                                    tvPraiseCount);
                        }
                    }
                });
            } else {
                TopicUrl topicUrl = (TopicUrl) item.getEntityJson();
                photos = new ArrayList<>();
                Photo photo = new Photo();
                photo.setImagePath(topicUrl.getListImg());
                photos.add(photo);
                title = topicUrl.getTitle();
                subTitle = topicUrl.getSummary();
                watchCount = topicUrl.getWatchCount();
                praiseCount = topicUrl.getPraiseCount();
                praiseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
            if (pages != null) {
                setRichThreadView(mContext, pages);
            } else {
                setPhotoView(mContext, photos);
            }
            tvLessonType.setText(item.getTypeStr());
            if (item.isSelect()) {
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(mContext,
                        " " + title,
                        faceSize);
                View view = View.inflate(mContext, R.layout.car_lesson_tag___car, null);
                ((TextView) view.findViewById(R.id.tv_title)).setText("精选");
                builder.setSpan(new HljImageSpan(ImageUtil.getDrawingCache(mContext, view)),
                        0,
                        1,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvLessonTitle.setText(builder);
            } else {
                tvLessonTitle.setText(EmojiUtil.parseEmojiByText2(mContext, title, faceSize));
            }

            tvLessonContent.setText(EmojiUtil.parseEmojiByText2(mContext, subTitle, faceSize));
            tvWatchCount.setText(String.valueOf(watchCount));
            tvPraiseCount.setText(String.valueOf(praiseCount));
        }

        /**
         * 设置话题图片
         */
        private void setPhotoView(
                Context context, List<Photo> photos) {
            int size = 0;
            if (photos != null) {
                size = photos.size();//图片数量
            }
            ArrayList<ImageView> imgLists = new ArrayList<ImageView>(Arrays.asList(ivLesson1,
                    ivLesson2,
                    ivLesson3));
            ivSingleLessonImg.setVisibility(View.GONE);
            //社区频道页不显示三图样式
            if (size >= 3) {
                //三图样式时，隐藏描述
                lessonCoverView.setVisibility(View.VISIBLE);
                ivLessonRight.setVisibility(View.GONE);
                lessonImgCountView.setVisibility(View.GONE);
                for (int i = 0; i < 3; i++) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgLists
                            .get(
                            i)
                            .getLayoutParams();
                    params.width = dynamicImgWidth;
                    params.height = dynamicImgWidth;

                    String url = ImageUtil.getImagePath(photos.get(i)
                            .getImagePath(), dynamicImgWidth);
                    if (!TextUtils.isEmpty(url)) {
                        imgLists.get(i)
                                .setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(url)
                                .apply(new RequestOptions().placeholder(com.hunliji
                                        .hljcommonviewlibrary.R.mipmap.icon_empty_image))
                                .into(imgLists.get(i));
                    } else {
                        imgLists.get(i)
                                .setVisibility(View.GONE);
                    }

                    if (size > 3) {
                        lessonImgCountView.setVisibility(View.VISIBLE);
                        tvLessonImgCount.setText(String.valueOf(photos.size()));
                    }
                }
            } else if (size > 0) {
                String url = ImageUtil.getImagePath(photos.get(0)
                        .getImagePath(), rightImgWidth);
                lessonCoverView.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(url)) {
                    ivLessonRight.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(url)
                            .apply(new RequestOptions().placeholder(com.hunliji
                                    .hljcommonviewlibrary.R.mipmap.icon_empty_image))
                            .into(ivLessonRight);
                } else {
                    ivLessonRight.setVisibility(View.GONE);
                }
            } else {
                lessonImgCountView.setVisibility(View.GONE);
                ivLessonRight.setVisibility(View.GONE);
                lessonCoverView.setVisibility(View.GONE);
            }
        }

        /**
         * 精编话题
         */
        private void setRichThreadView(Context context, CommunityThreadPages threadPages) {
            //富文本帖子图片
            lessonCoverView.setVisibility(View.GONE);
            ivLessonRight.setVisibility(View.GONE);
            ivSingleLessonImg.setVisibility(View.VISIBLE);
            String imgUrl = ImageUtil.getImagePath(threadPages.getImgPath(), singleImgWidth);
            if (!TextUtils.isEmpty(imgUrl)) {
                Glide.with(context)
                        .load(imgUrl)
                        .apply(new RequestOptions().placeholder(com.hunliji.hljcommonviewlibrary
                                .R.mipmap.icon_empty_image))
                        .into(ivSingleLessonImg);
            } else {
                Glide.with(context)
                        .clear(ivSingleLessonImg);
                ivSingleLessonImg.setVisibility(View.GONE);
            }
        }
    }

    class SubPageViewHolder extends TrackerCarLessonViewHolder {
        @BindView(R2.id.cover_layout)
        RelativeLayout coverLayout;
        @BindView(R2.id.img_cover)
        ImageView imgCover;
        @BindView(R2.id.tv_title)
        TextView tvTitle;
        @BindView(R2.id.tv_summary)
        TextView tvSummary;
        @BindView(R2.id.tv_watch_count)
        TextView tvWatchCount;
        private int imageWidth;
        private int imageHeight;
        public int faceSize;

        public SubPageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x;
            this.imageHeight = Math.round(imageWidth / 2.0f);
            this.coverLayout.getLayoutParams().width = imageWidth;
            this.coverLayout.getLayoutParams().height = imageHeight;
            this.faceSize = CommonUtil.dp2px(itemView.getContext(), 14);
            itemView.setOnClickListener(new View.OnClickListener() {
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
        protected void setViewData(
                final Context context, final CarLesson item, final int position, int viewType) {
            TopicUrl topic = (TopicUrl) item.getEntityJson();
            Glide.with(context)
                    .load(ImagePath.buildPath(topic.getListImg())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
            if (item.isSelect()) {
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(mContext,
                        " " + topic.getGoodTitle(),
                        faceSize);
                View view = View.inflate(mContext, R.layout.car_lesson_tag___car, null);
                ((TextView) view.findViewById(R.id.tv_title)).setText("精选");
                builder.setSpan(new HljImageSpan(ImageUtil.getDrawingCache(mContext, view)),
                        0,
                        1,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvTitle.setText(builder);
            } else {
                tvTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                        topic.getGoodTitle(),
                        faceSize));
            }
            if (TextUtils.isEmpty(topic.getSummary())) {
                tvSummary.setVisibility(View.GONE);
            } else {
                tvSummary.setVisibility(View.VISIBLE);
                tvSummary.setText(topic.getSummary());
            }
            tvWatchCount.setText(String.valueOf(topic.getWatchCount()));
        }
    }
}
