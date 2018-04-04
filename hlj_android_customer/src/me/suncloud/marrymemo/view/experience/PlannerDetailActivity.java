package me.suncloud.marrymemo.view.experience;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.PostPraiseBody;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.experienceshop.CommentAdapter;
import me.suncloud.marrymemo.api.experience.ExperienceApi;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.experience.Planner;
import me.suncloud.marrymemo.model.experience.Store;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.CommentNewWorkActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/3/22.
 * 统筹师详情页
 */

public class PlannerDetailActivity extends HljBaseNoBarActivity {

    @BindView(R.id.iv_back)
    ImageButton ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.ll_praise)
    LinearLayout llPraise;
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;
    @BindView(R.id.button_layout)
    RelativeLayout buttonLayout;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.btn_schedule)
    Button btnSchedule;
    @BindView(R.id.btn_reserve)
    Button btnReserve;
    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.iv_praise)
    ImageView ivPraise;
    @BindView(R.id.tv_praise)
    TextView tvPraise;

    private ArrayList<Comment> comments;
    private View footerView;
    private TextView endView;
    private View loadView;
    private View headerView;
    private HeaderViewHolder headerVH;

    private Unbinder unBinder;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber refreshSubscriber;
    private CommentAdapter adapter;
    private long plannerId = -1;
    private HljHttpSubscriber likeSubscriber;
    private Context context;
    private Planner planner;
    private Store store;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        plannerId = getIntent().getLongExtra("id", -1);
        store = getIntent().getParcelableExtra("store");
        super.onCreate(savedInstanceState);
        if (plannerId == -1) {
            return;
        }
        setContentView(R.layout.activity_cooedinator_detail);
        setDefaultStatusBarPadding();
        unBinder = ButterKnife.bind(this);
        EventBus.getDefault()
                .register(this);
        context = this;
        initHeaderView();
        initFooter();
        onNetError();
        comments = new ArrayList<>();
        recyclerView.addOnScrollListener(onScrollListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CommentAdapter(context, comments);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        recyclerView.setAdapter(adapter);
        onLoad();

    }

    private void onNetError() {
        //网络异常,可点击屏幕重新加载
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            int position = layoutManager.findFirstVisibleItemPosition();
            View firstVisibleChildView = layoutManager.findViewByPosition(position);
            int itemHeight = firstVisibleChildView.getHeight();
            int distance = (position) * itemHeight - firstVisibleChildView.getTop();
            int height = CommonUtil.dp2px(getApplicationContext(), 96);
            if (distance <= height) {
                tvTitle.setAlpha((float) (distance * 1.0 / height));
            } else {
                tvTitle.setAlpha(1);
            }
        }
    };

    private void onLoad() {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<Planner> mObservable = ExperienceApi.getPlannerDetail(plannerId);
            Observable<HljHttpData<List<Comment>>> aObservable = ExperienceApi.
                    getPlannerCommentList(plannerId, 1);
            Observable observable = Observable.zip(mObservable,
                    aObservable,
                    new Func2<Planner, HljHttpData<List<Comment>>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                Planner planner, HljHttpData<List<Comment>> Comments) {
                            return new ResultZip(planner, Comments);
                        }
                    });
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {

                        @Override
                        public void onNext(ResultZip resultZip) {
                            int pageCount = 0;
                            planner = resultZip.planner;
                            if (planner != null) {
                                tvTitle.setText(planner.getFullName());//标题
                                setPraiseState(ivPraise, tvPraise);
                                setHeaderView(planner);
                            }
                            bottomLayout.setVisibility(View.VISIBLE);
                            if (resultZip.comments != null && !resultZip.comments.isEmpty()) {
                                List<Comment> commentList = resultZip.comments.getData();
                                if (commentList != null && !commentList.isEmpty()) {
                                    pageCount = resultZip.comments.getPageCount();
                                    comments.clear();
                                    comments.addAll(commentList);
                                    headerVH.tvEmptyComment.setVisibility(View.GONE);
                                    adapter.setCommentList(comments);
                                } else {
                                    headerVH.tvEmptyComment.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                            }
                            initPage(pageCount);
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    public void setPraiseState(ImageView imageView, TextView tvPraise) {
        if (planner != null) {
            if (!planner.isLike()) {
                imageView.setImageResource(R.drawable.icon_praise_red_44_44);
                tvPraise.setText(R.string.label_praise2);
            } else {
                imageView.setImageResource(R.mipmap.icon_praise_primary_44_44);
                tvPraise.setText(R.string.praised);
            }
        }
    }

    /**
     * 评论成功,进行刷新
     *
     * @param event
     */
    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.COMMENT_PLANNER_ACTIVITY) {
            onLoad();
        }
    }


    private void initPage(int currentPage) {
        if (pageSubscriber == null || pageSubscriber.isUnsubscribed()) {
            Observable<HljHttpData<List<Comment>>> pageObservable = PaginationTool.
                    buildPagingObservable(recyclerView,
                            currentPage,
                            new PagingListener<HljHttpData<List<Comment>>>() {
                                @Override
                                public Observable<HljHttpData<List<Comment>>> onNextPage(int page) {
                                    return ExperienceApi.getPlannerCommentList(plannerId, page);
                                }
                            })
                    .setLoadView(loadView)
                    .setEndView(endView)
                    .build()
                    .getPagingObservable()
                    .observeOn(AndroidSchedulers.mainThread());

            pageSubscriber = HljHttpSubscriber.buildSubscriber(context)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Comment>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Comment>> hljHttpData) {
                            List<Comment> commentList = hljHttpData.getData();
                            comments.addAll(commentList);
                            adapter.setCommentList(comments);
                            adapter.notifyDataSetChanged();
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();

            pageObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pageSubscriber);
        }
    }


    @OnClick(R.id.iv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.ll_praise)
    void onPraise() {
        if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
            onPraiseLike(true);
            if (likeSubscriber == null || likeSubscriber.isUnsubscribed()) {
                likeSubscriber = HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                ToastUtil.showCustomToast(PlannerDetailActivity.this,
                                        planner.isLike() ? R.string.hint_praised_complete : R
                                                .string.hint_dispraised_complete);
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                onPraiseLike(false);
                                ToastUtil.showToast(PlannerDetailActivity.this,
                                        null,
                                        planner.isLike() ? R.string.msg_fail_to_cancel_post : R
                                                .string.msg_fail_to_praise_post);
                            }
                        })
                        .setDataNullable(true)
                        .setProgressBar(progressBar)
                        .build();
                PostPraiseBody body = new PostPraiseBody();
                body.setEntityType("TestStoreOrganizer");
                body.setId(plannerId);
                body.setType(6);
                CommonApi.postPraiseObb(body)
                        .subscribe(likeSubscriber);
            }
        }
    }

    private void onPraiseLike(boolean anim) {
        if (planner != null && headerVH != null) {
            int praiseCount = Integer.valueOf(headerVH.tvPraiseCount.getText()
                    .toString()
                    .trim());
            if (!planner.isLike()) {
                if (anim) {
                    AnimUtil.pulseAnimate(ivPraise);
                }
                planner.setLike(true);
                setPraiseState(ivPraise, tvPraise);
                headerVH.tvPraiseCount.setText(String.valueOf(praiseCount + 1));
            } else {
                planner.setLike(false);
                setPraiseState(ivPraise, tvPraise);
                headerVH.tvPraiseCount.setText(String.valueOf(praiseCount - 1));
            }
        }
    }


    @OnClick(R.id.comment_layout)
    void onComment() {
        if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
            Intent intent = new Intent(context, CommentNewWorkActivity.class);
            intent.putExtra("isPlanner", true);
            intent.putExtra("plannerId", plannerId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.btn_reserve)
    void onReserve() {
        Intent intent = new Intent(context, ExperienceShopReservationActivity.class);
        intent.putExtra("store", store);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void initHeaderView() {
        headerView = View.inflate(context, R.layout.coordinator_detail_header, null);
        headerVH = new HeaderViewHolder(headerView);
    }

    private void initFooter() {
        footerView = View.inflate(context, R.layout.list_foot_no_more_2, null);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
    }

    private void setHeaderView(Planner planner) {
        if (headerVH != null) {
            headerVH.setHeaderView(planner);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        EventBus.getDefault()
                .unregister(this);
        if (unBinder != null) {
            unBinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, likeSubscriber);
    }

    class HeaderViewHolder {

        @BindView(R.id.iv_avatar)
        RoundedImageView ivAvatar;
        @BindView(R.id.tv_comment_count)
        TextView tvCommentCount;
        @BindView(R.id.tv_praise_count)
        TextView tvPraiseCount;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_position)
        TextView tvPosition;
        @BindView(R.id.tv_self_introduction)
        TextView tvSelfIntroduction;
        @BindView(R.id.iv_pic1)
        RoundedImageView ivPic1;
        @BindView(R.id.iv_pic2)
        RoundedImageView ivPic2;
        @BindView(R.id.iv_pic3)
        RoundedImageView ivPic3;
        @BindView(R.id.iv_pic4)
        RoundedImageView ivPic4;
        @BindView(R.id.tv_more_pic_num)
        TextView tvMorePicNum;
        @BindView(R.id.rl_pic_more)
        RelativeLayout rlPicMore;
        @BindView(R.id.ll_pic)
        LinearLayout llPic;
        @BindView(R.id.ll_header)
        LinearLayout llHeader;
        @BindView(R.id.tv_empty_comment)
        TextView tvEmptyComment;
        private Context context;
        private ImageView[] viewList;
        private List<Photo> photos;

        private String[] jobTitle;
        private int height;
        private int width;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            context = view.getContext();
            viewList = new ImageView[]{ivPic1, ivPic2, ivPic3, ivPic4};
            jobTitle = context.getResources()
                    .getStringArray(R.array.jobTitle);
            Point point = CommonUtil.getDeviceSize(view.getContext());
            int distance = (point.x - CommonUtil.dp2px(view.getContext(), (16 * 2 + 8 * 3))) / 4;
            width = distance;
            height = distance;
            llPic.getLayoutParams().height = height;
            photos = new ArrayList<>();
        }

        private void setHeaderView(Planner planner) {
            if (planner != null) {
                llHeader.setVisibility(View.VISIBLE);
                tvName.setText(planner.getFullName());
                tvCommentCount.setText(String.valueOf(planner.getCommentCount()));
                tvPraiseCount.setText(String.valueOf(planner.getLikesCount()));
                tvPosition.setText(getJobTitle(planner.getTitle()));
                tvSelfIntroduction.setText(planner.getIntroduce());
                int avatarWidth = CommonUtil.dp2px(context, 60);
                int avatarHeight = CommonUtil.dp2px(context, 60);
                Glide.with(context)
                        .load(ImageUtil.getImagePath2ForWxH(planner.getHeadImg(),
                                avatarWidth,
                                avatarHeight))
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary)
                                .error(R.mipmap.icon_avatar_primary))
                        .into(ivAvatar);
                photos.clear();
                if (planner.getImg() != null) {
                    photos.addAll(planner.getImg());
                }
                if (!photos.isEmpty()) {
                    int size = photos.size();
                    tvMorePicNum.setText(String.valueOf(size));
                    if (size == 0) {
                        llPic.setVisibility(View.GONE);
                    } else {
                        llPic.setVisibility(View.VISIBLE);
                        if (size < 5) {
                            for (int i = 0; i < size; i++) {
                                viewList[i].setVisibility(View.VISIBLE);
                                Photo photo = photos.get(i);
                                if (photo != null) {
                                    glideLoad(photo.getImagePath(), viewList[i]);
                                }
                            }
                        } else {
                            rlPicMore.setVisibility(View.VISIBLE);
                            for (int i = 0; i < viewList.length; i++) {
                                Photo imgBean = photos.get(i);
                                viewList[i].setVisibility(View.VISIBLE);
                                if (imgBean != null) {
                                    glideLoad(imgBean.getImagePath(), viewList[i]);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void glideLoad(String url, ImageView imageView) {
            if (!TextUtils.isEmpty(url)) {
                if (url.endsWith(".gif")) {
                    Glide.with(context)
                            .load(url)
                            .apply(new RequestOptions().dontAnimate()
                                    .placeholder(R.mipmap.icon_empty_image)
                                    .error(R.mipmap.icon_empty_image))
                            .into(imageView);
                } else {
                    String imgPath = ImageUtil.getImagePath2ForWxH(url, width, height);
                    Glide.with(context)
                            .load(imgPath)
                            .apply(new RequestOptions().dontAnimate()
                                    .placeholder(R.mipmap.icon_empty_image)
                                    .error(R.mipmap.icon_empty_image))
                            .into(imageView);
                }
            } else {
                Glide.with(context)
                        .clear(imageView);
            }
        }

        private String getJobTitle(int title) {
            if (jobTitle != null) {
                if (title < 0) {
                    title = 0;
                } else if (title >= jobTitle.length) {
                    return "";
                }
                return jobTitle[title];
            } else {
                return "";
            }
        }

        private void previewPic(Context context, int position) {
            if (photos != null && !photos.isEmpty()) {
                Intent intent = new Intent(context, PicsPageViewActivity.class);
                intent.putParcelableArrayListExtra("photos",
                        (ArrayList<? extends Parcelable>) photos);
                intent.putExtra("position", position);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        }

        @OnClick({R.id.iv_pic1, R.id.iv_pic2, R.id.iv_pic3, R.id.iv_pic4})
        public void onClick(View view) {
            Context context = view.getContext();
            switch (view.getId()) {
                case R.id.iv_pic1:
                    previewPic(context, 0);
                    break;
                case R.id.iv_pic2:
                    previewPic(context, 1);
                    break;
                case R.id.iv_pic3:
                    previewPic(context, 2);
                    break;
                case R.id.iv_pic4:
                    if (photos != null && photos.size() > 0) {
                        if (photos.size() > 4) {
                            Intent intent = new Intent(context, ExperienceShopPhotoActivity.class);
                            intent.putParcelableArrayListExtra("photos",
                                    (ArrayList<? extends Parcelable>) photos);
                            intent.putExtra("title", planner.getFullName());
                            view.getContext()
                                    .startActivity(intent);
                            Activity activity = (Activity) context;
                            activity.overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        } else {
                            previewPic(context, 3);
                        }
                    }
                    break;
            }
        }
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        Planner planner;
        @HljRZField
        HljHttpData<List<Comment>> comments;

        public ResultZip(Planner planner, HljHttpData<List<Comment>> comments) {
            this.planner = planner;
            this.comments = comments;
        }
    }
}
