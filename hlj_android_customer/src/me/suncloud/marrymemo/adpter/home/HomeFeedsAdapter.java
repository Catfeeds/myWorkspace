package me.suncloud.marrymemo.adpter.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tracker.viewholder.TrackerHomeFeedViewHolder;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.home.HomeFeed;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.SpanEllipsizeEndHelper;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;

/**
 * Created by luohanlin on 2017/4/20.
 * 首页feed流adapter
 */

public class HomeFeedsAdapter extends RecyclerView.Adapter<BaseViewHolder<HomeFeed>> {

    private final String HAND_PICK_ID = "0";//精选
    private final String SI_DA_JIN_GANG_ID = "7,8,9,11";//四大金刚

    private Context context;
    private View footerView;
    private ArrayList<HomeFeed> feeds;
    private City city;
    private int normalTagCount = 3;
    private int normalPicCount = 3;
    private String propertyIdString;

    public HomeFeedsAdapter(
            Context context, ArrayList<HomeFeed> feeds) {
        this.context = context;
        this.feeds = feeds;
    }


    public void setCity(City city) {
        this.city = city;
    }

    public void setPropertyIdString(String propertyIdString) {
        this.propertyIdString = propertyIdString;
    }

    public void setFooterView(View footerView) {
        if (this.footerView == null) {
            this.footerView = footerView;
            notifyItemInserted(getItemCount() - 1);
        } else {
            this.footerView = footerView;
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void setFeeds(ArrayList<HomeFeed> feeds) {
        this.feeds = feeds;
    }

    public boolean isEmpty() {
        return feeds.isEmpty();
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if(footerView != null && position == getItemCount() - 1) {
            type = HomeFeed.FEED_TYPE_INT_FOOTER;
        } else {
            type = getItem(position).getEntityTypeInt();
        }
        return type;
    }

    public HomeFeed getItem(int position) {
        if (position >= 0 && position < feeds.size()) {
            return feeds.get(position);
        }
        return null;
    }

    private View getView(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            //footer
            case HomeFeed.FEED_TYPE_INT_FOOTER:
                view = footerView;
                break;
            //case work
            case HomeFeed.FEED_TYPE_INT_CASE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_page_work_item, parent, false);
                break;
            case HomeFeed.FEED_TYPE_INT_WORK:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_page_work_item, parent, false);
                break;
            //thread
            case HomeFeed.FEED_TYPE_INT_THREAD:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_page_thread_item, parent, false);
                break;
            //poster
            case HomeFeed.FEED_TYPE_INT_POSTER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_page_poster_item, parent, false);
                break;
            //banner
            case HomeFeed.FEED_TYPE_INT_BANNER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_page_banner_item, parent, false);
                break;
        }
        if (view == null) {
            view = new View(parent.getContext());
        }
        return view;
    }


    @Override
    public BaseViewHolder<HomeFeed> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomeFeed.FEED_TYPE_INT_CASE:
                return new CaseViewHolder(getView(parent, viewType));
            case HomeFeed.FEED_TYPE_INT_WORK:
                return new WorkViewHolder(getView(parent, viewType));
            case HomeFeed.FEED_TYPE_INT_THREAD:
                return new ThreadViewHolder(getView(parent, viewType));
            case HomeFeed.FEED_TYPE_INT_POSTER:
                return new PosterViewHolder(getView(parent, viewType));
            case HomeFeed.FEED_TYPE_INT_BANNER:
                return new BannerViewHolder(getView(parent, viewType));
            default:
                return new ExtraViewHolder(getView(parent, viewType));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<HomeFeed> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return (footerView == null ? 0 : 1) + feeds.size();
    }

    public class ExtraViewHolder extends BaseViewHolder<HomeFeed> {

        public ExtraViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, HomeFeed item, int position, int viewType) {
        }
    }

    //套餐的处理-----------------------------------套餐--------------------------------------
    class WorkViewHolder extends WorkAndCaseBaseTrackViewHolder {

        private int width;
        private int coverHeight;
        private int badgeWidth;
        private int badgeHeight;

        WorkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            Point point = CommonUtil.getDeviceSize(context);
            width = point.x;
            coverHeight = Math.round(width * 10.0F / 16.0F);
            badgeWidth = Math.round(itemView.getResources()
                    .getDisplayMetrics().density * 120);
            badgeHeight = CommonUtil.dp2px(itemView.getContext(), 100);
            singleCoverLayout.getLayoutParams().height = coverHeight;

            caseCoverLayout.setVisibility(View.GONE);
            singleCoverLayout.setVisibility(View.VISIBLE);
            showPriceLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected void setViewData(
                Context mContext, final HomeFeed item, final int position, int viewType) {
            Work work = (Work) item.getEntityObj();
            if (work == null) {
                return;
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGotoWork(item, position);
                }
            });
            setWork(context, work);
            setMerchant(context, work);
            setAddress(context, work);
        }

        private void setWork(Context context, Work work) {
            if (work == null) {
                return;
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(work.getCoverPath())
                            .width(width)
                            .height(coverHeight)
                            .cropPath())
                    .into(imgWorkCover);
            if (work.getMediaVideosCount() > 0) {
                imgWorkPlayButton.setVisibility(View.VISIBLE);
            } else {
                imgWorkPlayButton.setVisibility(View.GONE);
            }
            tvTitle.setText(work.getTitle());
            tvShowPrice.setText(Util.formatDouble2String(work.getShowPrice()));
            WorkRule rule = work.getRule();
            if (rule != null && !TextUtils.isEmpty(rule.getBigImg())) {
                imgWorkBadge.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(ImagePath.buildPath(rule.getBigImg())
                                .width(badgeWidth)
                                .height(badgeHeight)
                                .cropPath())
                        .apply(new RequestOptions().dontAnimate())
                        .into(imgWorkBadge);
            } else {
                imgWorkBadge.setVisibility(View.GONE);
            }
            imgInstallment.setVisibility(View.GONE);
        }
    }

    //案例的处理-------------------------------------案例------------------------------------
    class CaseViewHolder extends WorkAndCaseBaseTrackViewHolder {

        private int width;
        private int coverHeight;
        private int bigCoverWidth;
        private int smallCoverSize;

        CaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            Point point = CommonUtil.getDeviceSize(context);
            width = point.x;
            coverHeight = Math.round(width * 10.0F / 16.0F);
            caseCoverLayout.getLayoutParams().height = coverHeight;
            singleCoverLayout.getLayoutParams().height = coverHeight;
            smallCoverSize = Math.round((coverHeight - CommonUtil.dp2px(itemView.getContext(),
                    2)) / 2);
            bigCoverWidth = Math.round(width - CommonUtil.dp2px(itemView.getContext(),
                    2) - smallCoverSize);
            imgCaseCover1.getLayoutParams().width = bigCoverWidth;
            imgCaseCover1.getLayoutParams().height = coverHeight;
            imgCaseCover2.getLayoutParams().width = smallCoverSize;
            imgCaseCover2.getLayoutParams().height = smallCoverSize;
            imgCaseCover3.getLayoutParams().width = smallCoverSize;
            imgCaseCover3.getLayoutParams().height = smallCoverSize;
            showPriceLayout.setVisibility(View.GONE);
        }

        @Override
        protected void setViewData(
                Context mContext, final HomeFeed item, final int position, int viewType) {
            Work work = (Work) item.getEntityObj();
            if (work == null) {
                return;
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGotoWork(item, position);
                }
            });

            setCase(context, work);
            setMerchant(context, work);
            setAddress(context, work);
        }

        private void setCase(Context mContext, Work work) {
            if (work == null) {
                return;
            }
            // 设置套餐信息
            if (work.getMediaItems() != null && work.getMediaItems()
                    .size() >= 2) {
                singleCoverLayout.setVisibility(View.GONE);
                caseCoverLayout.setVisibility(View.VISIBLE);

                // 案例封面
                Glide.with(mContext)
                        .load(ImagePath.buildPath(work.getCoverPath())
                                .width(bigCoverWidth)
                                .height(coverHeight)
                                .cropPath())
                        .into(imgCaseCover1);
                if (work.getMediaItems()
                        .size() > 0) {
                    Glide.with(mContext)
                            .load(ImagePath.buildPath(work.getMediaItems()
                                    .get(0)
                                    .getItemCover())
                                    .width(smallCoverSize)
                                    .height(smallCoverSize)
                                    .cropPath())
                            .into(imgCaseCover2);
                }
                if (work.getMediaItems()
                        .size() > 1) {
                    Glide.with(mContext)
                            .load(ImagePath.buildPath(work.getMediaItems()
                                    .get(1)
                                    .getItemCover())
                                    .width(smallCoverSize)
                                    .height(smallCoverSize)
                                    .cropPath())
                            .into(imgCaseCover3);
                }
                if (work.getMediaVideosCount() > 0) {
                    casePlayButtonLayout.setVisibility(View.VISIBLE);
                } else {
                    casePlayButtonLayout.setVisibility(View.GONE);
                }
                if (work.getMediaItemsCount() - 2 > 99) {
                    tvCaseImgCount.setVisibility(View.VISIBLE);
                    tvCaseImgCount.setText(String.valueOf(work.getMediaItemsCount() - 2));
                } else if (work.getMediaItemsCount() - 2 > 0) {
                    tvCaseImgCount.setVisibility(View.VISIBLE);
                    tvCaseImgCount.setText("" + "+" + String.valueOf(work.getMediaItemsCount() -
                            2));
                } else {
                    tvCaseImgCount.setVisibility(View.GONE);
                }
            } else {
                singleCoverLayout.setVisibility(View.VISIBLE);
                caseCoverLayout.setVisibility(View.GONE);
                Glide.with(mContext)
                        .load(ImagePath.buildPath(work.getCoverPath())
                                .width(width)
                                .height(coverHeight)
                                .cropPath())
                        .into(imgWorkCover);
                if (work.getMediaVideosCount() > 0) {
                    imgWorkPlayButton.setVisibility(View.VISIBLE);
                } else {
                    imgWorkPlayButton.setVisibility(View.GONE);
                }
            }

            tvTitle.setText(work.getTitle());
        }
    }

    private void onGotoWork(HomeFeed feed, int position) {
        Work work = (Work) feed.getEntityObj();
        Intent intent;
        if (feed.getEntityType()
                .equals(HomeFeed.FEED_TYPE_STR_WORK)) {
            intent = new Intent(context, WorkActivity.class);
            JSONObject jsonObject = TrackerUtil.getSiteJson("B1/R1",
                    position + 1,
                    "套餐" + work.getId() + work.getTitle());
            if (jsonObject != null) {
                intent.putExtra("site", jsonObject.toString());
            }
            Map<String, Object> kv = new HashMap<>();
            kv.put("套餐ID", work.getId() + "");
            me.suncloud.marrymemo.util.TrackerUtil.sendEvent(context, "套餐", "首页Feeds流", kv);
        } else {
            //案例
            intent = new Intent(context, CaseDetailActivity.class);
            JSONObject jsonObject = TrackerUtil.getSiteJson("B1/R1",
                    position + 1,
                    "案例" + work.getId() + work.getTitle());
            if (jsonObject != null) {
                intent.putExtra("site", jsonObject.toString());
            }
            Map<String, Object> kv = new HashMap<>();
            kv.put("案例ID", work.getId() + "");
            me.suncloud.marrymemo.util.TrackerUtil.sendEvent(context, "案例", "首页Feeds流", kv);
        }
        intent.putExtra("id", work.getId());
        intent.putExtra("ads", feed.getAds());
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    private class ThreadViewHolder extends HomeFeedTrackViewHolder implements View.OnClickListener {
        private ImageView cover;
        private TextView from;
        private TextView title;
        private TextView content;
        private TextView commentCount;
        private CheckableLinearLayout checkPraised;
        private TextView tvAdd;
        private ImageView imgThumb;
        private TextView praiseCount;
        private int faceSize;
        private int imageSize;

        ThreadViewHolder(View itemView) {
            super(itemView);
            DisplayMetrics dm = context.getResources()
                    .getDisplayMetrics();
            imageSize = Math.round(140 * dm.density);
            faceSize = Math.round(dm.density * 18);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            from = (TextView) itemView.findViewById(R.id.tv_from);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            commentCount = (TextView) itemView.findViewById(R.id.comments_count);
            checkPraised = (CheckableLinearLayout) itemView.findViewById(R.id.check_praised);
            imgThumb = (ImageView) itemView.findViewById(R.id.img_thumb_up);
            praiseCount = (TextView) itemView.findViewById(R.id.tv_praise_count);
            tvAdd = (TextView) itemView.findViewById(R.id.tv_add);
            itemView.setOnClickListener(this);
        }

        @Override
        protected void setViewData(
                Context mContext, HomeFeed item, int position, int viewType) {
            CommunityThread thread = (CommunityThread) item.getEntityObj();
            SpannableStringBuilder titleSpan;
            String string = null;
            if (thread.getChannel() != null) {
                string = thread.getChannel()
                        .getTitle();
            }
            if (!JSONUtil.isEmpty(string)) {
                from.setVisibility(View.VISIBLE);
                from.setText(String.format(context.getString(R.string.label_come_form_s), string));
            } else {
                from.setVisibility(View.GONE);
            }

            //如果话题存在图片的话
            String url = null;
            titleSpan = EmojiUtil.parseEmojiByText2(context, thread.getShowTitle(), faceSize);
            content.setText(EmojiUtil.parseEmojiByText2(context,
                    thread.getShowSubtitle(),
                    faceSize));
            if (!CommonUtil.isCollectionEmpty(thread.getShowPhotos())) {
                url = ImagePath.buildPath(thread.getShowPhotos()
                        .get(0)
                        .getImagePath())
                        .width(imageSize)
                        .path();
            }
            if (!JSONUtil.isEmpty(url)) {
                title.setMaxLines(2);
                title.setText(titleSpan);
                content.setVisibility(View.GONE);
                cover.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(url)
                        .into(cover);
                //如果话题不存在图片
            } else {
                cover.setTag(null);
                cover.setImageBitmap(null);
                cover.setVisibility(View.GONE);
                title.setMaxLines(1);
                title.setText(SpanEllipsizeEndHelper.matchMaxWidth(titleSpan, title));
                content.setVisibility(View.VISIBLE);
            }

            //替换点赞动画
            checkPraised.setChecked(thread.isPraised());
            //点击喜欢
            checkPraised.setOnClickListener(new OnLikeClickListener((Activity) context,
                    checkPraised,
                    imgThumb,
                    praiseCount,
                    tvAdd,
                    thread));

            commentCount.setText(thread.getPostCount() == 0 ? "回复" : String.valueOf(thread
                    .getPostCount()));
            praiseCount.setText(thread.getPraisedSum() == 0 ? "赞" : String.valueOf(thread
                    .getPraisedSum()));
        }

        @Override
        public void onClick(View v) {
            CommunityThread thread = (CommunityThread) getItem().getEntityObj();
            if (thread == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.thread_layout:
                    Map<String, Object> kv = new HashMap<>();
                    kv.put("话题ID", thread.getId() + "");
                    me.suncloud.marrymemo.util.TrackerUtil.sendEvent(context, "话题", "首页Feeds流", kv);
                    Intent intent = new Intent(context, CommunityThreadDetailActivity.class);
                    JSONObject jsonObject = TrackerUtil.getSiteJson("B1/R1",
                            getAdapterPosition() + 1,
                            "话题" + thread.getId());
                    if (jsonObject != null) {
                        intent.putExtra("site", jsonObject.toString());
                    }
                    intent.putExtra("id", thread.getId());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                    break;
            }
        }
    }

    private class BannerViewHolder extends HomeFeedTrackViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private int width;
        private int height;

        BannerViewHolder(View itemView) {
            super(itemView);
            Point point = JSONUtil.getDeviceSize(context);
            DisplayMetrics dm = context.getResources()
                    .getDisplayMetrics();
            width = Math.round(point.x - 24 * dm.density);
            height = Math.round(width / 2);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            imageView.getLayoutParams().height = height;
            itemView.setOnClickListener(this);
        }

        @Override
        protected void setViewData(
                Context mContext, HomeFeed item, int position, int viewType) {
            Poster poster = (Poster) item.getEntityObj();
            Glide.with(mContext)
                    .load(ImagePath.buildPath(poster.getPath())
                            .width(width)
                            .height(height)
                            .cropPath())
                    .into(imageView);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.poster_layout:
                    Poster poster = (Poster) getItem().getEntityObj();
                    Map<String, Object> kv = new HashMap<>();
                    kv.put("PostID", poster.getId() + "");
                    me.suncloud.marrymemo.util.TrackerUtil.sendEvent(context,
                            "Banner",
                            "首页Feeds流",
                            kv);
                    new NewHttpPostTask(context, null).execute(Constants.getAbsUrl(String.format(
                            Constants.HttpPath.POSTER_WATCH_URL,
                            poster.getId())));
                    BannerUtil.bannerAction(context,
                            (Poster) getItem().getEntityObj(),
                            city,
                            false,
                            TrackerUtil.getSiteJson("B1/R1",
                                    getAdapterPosition() + 1,
                                    "广告" + poster.getId() + poster.getTitle()));
                    break;
            }
        }
    }

    //poster流
    private class PosterViewHolder extends HomeFeedTrackViewHolder implements View.OnClickListener {

        private ImageView avatar;
        private TextView userName;
        private TextView property;
        private TextView title;
        private ImageView imageView;
        private LinearLayout tagsLayout;
        private TextView previewCount;
        private int width;
        private int height;
        private int avatarSize;
        private int d;

        PosterViewHolder(View itemView) {
            super(itemView);
            Point point = JSONUtil.getDeviceSize(context);
            DisplayMetrics dm = context.getResources()
                    .getDisplayMetrics();
            width = Math.round(point.x - 24 * dm.density);
            height = Math.round(width / 2);
            avatarSize = Math.round(30 * dm.density);
            d = Math.round(8 * dm.density);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            userName = (TextView) itemView.findViewById(R.id.name);
            property = (TextView) itemView.findViewById(R.id.property);
            title = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            previewCount = (TextView) itemView.findViewById(R.id.preview_count);
            tagsLayout = (LinearLayout) itemView.findViewById(R.id.tags_layout);
            imageView.getLayoutParams().height = height;
            itemView.setOnClickListener(this);
        }

        @Override
        public View trackerView() {
            return itemView;
        }

        @Override
        public String propertyIdString() {
            return propertyIdString;
        }

        @Override
        protected void setViewData(
                Context mContext, HomeFeed item, int position, int viewType) {
            Poster poster = (Poster) item.getEntityObj();
            User user = poster.getUser();
            if (user != null) {
                Glide.with(mContext)
                        .load(ImagePath.buildPath(user.getAvatar())
                                .width(avatarSize)
                                .height(avatarSize)
                                .cropPath())
                        .apply(new RequestOptions().dontAnimate())
                        .into(avatar);
                int width = Math.round(property.getPaint()
                        .measureText(property.getText()
                                .toString()) + d);
                userName.setPadding(0, 0, width, 0);
                userName.setText(user.getNick());
            }
            Glide.with(mContext)
                    .load(ImagePath.buildPath(poster.getPath())
                            .width(width)
                            .height(height)
                            .cropPath())
                    .into(imageView);
            title.setText(poster.getTitle());
            previewCount.setText(Util.formatCount(mContext, item.getWatchCount()));
            addTagLayouts(context, tagsLayout, poster.getMarks());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.poster_layout:
                    Poster poster = (Poster) getItem().getEntityObj();
                    Map<String, Object> kv = new HashMap<>();
                    kv.put("PostID", poster.getId() + "");
                    me.suncloud.marrymemo.util.TrackerUtil.sendEvent(context,
                            "Post",
                            "首页Feeds流",
                            kv);
                    getItem().setWatchCount(getItem().getWatchCount() + 1);
                    previewCount.setText(Util.formatCount(context, getItem().getWatchCount()));
                    new NewHttpPostTask(context, null).execute(Constants.getAbsUrl(String.format(
                            Constants.HttpPath.POSTER_WATCH_URL,
                            poster.getId())));
                    BannerUtil.bannerAction(context,
                            (Poster) getItem().getEntityObj(),
                            city,
                            false,
                            TrackerUtil.getSiteJson("B1/R1",
                                    getAdapterPosition() + 1,
                                    "海报" + poster.getId() + poster.getTitle()));
                    break;
            }
        }
    }

    private class HomeFeedTrackViewHolder extends TrackerHomeFeedViewHolder {

        public HomeFeedTrackViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public View trackerView() {
            return itemView;
        }

        @Override
        public String propertyIdString() {
            return propertyIdString;
        }

        @Override
        protected void setViewData(
                Context mContext, HomeFeed item, int position, int viewType) {

        }
    }

    class WorkAndCaseBaseTrackViewHolder extends HomeFeedTrackViewHolder {

        @BindView(R.id.img_work_cover)
        ImageView imgWorkCover;
        @BindView(R.id.img_installment)
        ImageView imgInstallment;
        @BindView(R.id.img_work_play_button)
        ImageView imgWorkPlayButton;
        @BindView(R.id.img_work_badge)
        ImageView imgWorkBadge;
        @BindView(R.id.single_cover_layout)
        RelativeLayout singleCoverLayout;
        @BindView(R.id.img_case_cover_1)
        ImageView imgCaseCover1;
        @BindView(R.id.case_play_button_layout)
        RelativeLayout casePlayButtonLayout;
        @BindView(R.id.img_case_cover_2)
        ImageView imgCaseCover2;
        @BindView(R.id.img_case_cover_3)
        ImageView imgCaseCover3;
        @BindView(R.id.tv_case_img_count)
        TextView tvCaseImgCount;
        @BindView(R.id.case_cover_layout)
        RelativeLayout caseCoverLayout;
        @BindView(R.id.layout_img)
        RelativeLayout layoutImg;
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.layout_title)
        LinearLayout layoutTitle;
        @BindView(R.id.tv_area_name)
        TextView tvAreaName;
        @BindView(R.id.address_layout)
        LinearLayout addressLayout;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_show_price)
        TextView tvShowPrice;
        @BindView(R.id.show_price_layout)
        LinearLayout showPriceLayout;

        public WorkAndCaseBaseTrackViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, final HomeFeed item, final int position, int viewType) {
        }

        protected String getAreaName(Work work) {
            String areaName = null;
            ParentArea parentArea = work.getMerchant()
                    .getShopArea();
            if (parentArea != null) {
                areaName = parentArea.getName();
                if (work.isLvPai()) {
                    parentArea = parentArea.getParentArea();
                    if (parentArea != null) {
                        areaName = parentArea.getName();
                    }
                }
            }
            return areaName;
        }

        protected void setMerchant(Context mContext, Work work) {
            if (work == null || work.getMerchant() == null) {
                return;
            }
            Merchant merchant = work.getMerchant();
            String propertyName = merchant.getPropertyName();
            tvProperty.setText(propertyName);
            if (propertyIdString.equals(SI_DA_JIN_GANG_ID) || propertyIdString.equals
                    (HAND_PICK_ID)) {
                tvProperty.setVisibility(View.VISIBLE);
            } else {
                tvProperty.setVisibility(View.GONE);
            }
            tvMerchantName.setText(merchant.getName());
        }

        protected void setAddress(Context mContext, Work work) {
            if (work == null) {
                return;
            }
            String address = getAreaName(work);
            if (TextUtils.isEmpty(address)) {
                addressLayout.setVisibility(View.GONE);
            } else {
                tvAreaName.setText(address);
                addressLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class OnMarkClickListener implements View.OnClickListener {

        private OnMarkClickListener() {
        }

        @Override
        public void onClick(View v) {
            Mark mark = (Mark) v.getTag();
            if (mark != null) {
                Util.markAction(context, mark.getMarkedType(), mark.getName(), mark.getId(), false);
            }
        }
    }

    //添加tag
    private void addTagLayouts(
            Context context, LinearLayout tagsLayout, List<Mark> marks) {
        //设置标签
        if (marks != null && !marks.isEmpty()) {
            tagsLayout.setVisibility(View.VISIBLE);
            int count = tagsLayout.getChildCount();
            int size = marks.size();
            size = size > normalTagCount ? normalTagCount : size;
            if (count > size) {
                tagsLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                View view = null;
                TextView tv = null;
                Mark mark = marks.get(i);
                if (i < count) {
                    view = tagsLayout.getChildAt(i);
                    tv = (TextView) view.findViewById(R.id.mark);
                }
                if (view == null) {
                    view = View.inflate(context, R.layout.home_feed_mark_item, null);
                    tv = (TextView) view.findViewById(R.id.mark);
                    view.setOnClickListener(new OnMarkClickListener());
                    tagsLayout.addView(view);
                }
                view.setTag(mark);
                tv.setText("#" + mark.getName());
                tv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack3));

                try {
                    if (!TextUtils.isEmpty(mark.getHighLightColor())) {
                        int color = Color.parseColor(mark.getHighLightColor());
                        tv.setTextColor(color);
                    } else {
                        tv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack3));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            tagsLayout.setVisibility(View.INVISIBLE);
        }
    }

    //点赞的操作
    private class OnLikeClickListener implements View.OnClickListener {
        private Activity activity;
        private CheckableLinearLayout checkPraised;
        private ImageView imgThumbUp;
        private TextView praiseCount;
        private TextView tvAdd;
        private CommunityThread thread;

        @Override
        public void onClick(View v) {
            if (thread != null) {
                CommunityTogglesUtil.onNewCommunityThreadPraise(activity,
                        checkPraised,
                        imgThumbUp,
                        praiseCount,
                        tvAdd,
                        thread,
                        null);
            }
        }

        OnLikeClickListener(
                Activity activity,
                CheckableLinearLayout checkPraised,
                ImageView imgThumb,
                TextView praiseCount,
                TextView tvAdd,
                CommunityThread thread) {
            this.activity = activity;
            this.checkPraised = checkPraised;
            this.imgThumbUp = imgThumb;
            this.tvAdd = tvAdd;
            this.praiseCount = praiseCount;
            this.thread = thread;
        }
    }
}
