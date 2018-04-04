package me.suncloud.marrymemo.view.community;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.WeddingBibleFeedsAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.community.PosterWatchFeed;
import me.suncloud.marrymemo.model.community.WeddingBible;
import me.suncloud.marrymemo.model.community.wrappers.HljWeddingBiblesData;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * 结婚宝典
 * Created by chen_bin on 2018/3/12 0012.
 */
public class WeddingBibleActivity extends HljBaseNoBarActivity {

    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.avatars_layout)
    LinearLayout avatarsLayout;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.tv_last_bible)
    TextView tvLastBible;
    @BindView(R.id.last_bible_layout)
    LinearLayout lastBibleLayout;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.divider_view)
    View dividerView;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;
    @BindView(R.id.create_layout)
    RelativeLayout createLayout;

    private View endView;
    private View loadView;

    private WeddingBibleFeedsAdapter adapter;

    private HljWeddingBiblesData biblesData;
    private WeddingBible lastBible;
    private City city;

    private int coverHeight;
    private int alphaHeight;
    private int avatarSize;
    private int lastVerticalOffset;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    private Subscription addWatchCountSub;

    @Override
    public String pageTrackTagName() {
        return "结婚宝典";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_bible);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, loadingLayout);
        initTracker();
        initValues();
        initViews();
        initLoad();
        addWatchCount();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(btnShare)
                .tagName(HljTaggerName.BTN_SHARE)
                .hitTag();
        HljVTTagger.buildTagger(createLayout)
                .tagName(HljTaggerName.BTN_SEND_DISCUSSION)
                .hitTag();
    }

    private void addWatchCount() {
        addWatchCountSub = CommunityApi.addPosterWatchCountObb(PosterWatchFeed.SAME_CITY_TYPE)
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .toastHidden()
                        .build());
    }

    private void initValues() {
        int coverWidth = CommonUtil.getDeviceSize(this).x;
        coverHeight = Math.round(coverWidth * 178.0f / 375.0f);
        alphaHeight = coverHeight - CommonUtil.dp2px(this, 45) - getStatusBarHeight();
        avatarSize = CommonUtil.dp2px(this, 28);

        city = Session.getInstance()
                .getMyCity(this);
    }

    private void initViews() {
        imgCover.getLayoutParams().height = coverHeight;
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });

        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WeddingBibleFeedsAdapter(this);
        adapter.setFooterView(footerView);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = adapter.getCatalogViewsCount() + adapter.getMoreCatalogViewCount()
                        + adapter.getSectionIndexViewCount();
                if (layoutManager.findFirstVisibleItemPosition() >= position) {
                    showCreateView();
                } else {
                    hideCreateView();
                }
            }
        });

        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (lastVerticalOffset == verticalOffset) {
                    return;
                }
                lastVerticalOffset = verticalOffset;
                if (-verticalOffset >= alphaHeight) {
                    setActionBarAlpha(1);
                } else {
                    setActionBarAlpha((float) -verticalOffset / alphaHeight);
                }
                hideCreateView();
            }
        });
    }

    private void setActionBarAlpha(float alpha) {
        if (btnBack.getAlpha() == alpha) {
            return;
        }
        btnBack.setAlpha(alpha);
        tvToolbarTitle.setAlpha(alpha);
        btnShare.setAlpha(alpha);
        dividerView.setAlpha(alpha);
        int red = Color.red(0xffffffff);
        int green = Color.green(0xffffffff);
        int blue = Color.blue(0xffffffff);
        int a = (int) (Color.alpha(0xffffffff) * alpha);
        actionHolderLayout.setBackgroundColor(Color.argb(a, red, green, blue));
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            Observable<HljWeddingBiblesData> biblesDataObb = CommunityApi.getWeddingBiblesObb();
            Observable<Poster> posterObb = getPosterObb();
            Observable<HljHttpData<List<CommunityFeed>>> feedsDataObb = CommunityApi
                    .getWeddingBibleFeedsObb(
                    1,
                    HljCommon.PER_PAGE);
            Observable<ResultZip> observable = Observable.zip(biblesDataObb,
                    posterObb,
                    feedsDataObb,
                    new Func3<HljWeddingBiblesData, Poster, HljHttpData<List<CommunityFeed>>,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljWeddingBiblesData biblesData,
                                Poster poster,
                                HljHttpData<List<CommunityFeed>> feedsData) {
                            return new ResultZip(biblesData, poster, feedsData);
                        }
                    });
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            setData(resultZip);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            setActionBarAlpha(1);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(headerLayout)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSub);
        }
    }

    /**
     * 结婚宝典poster
     *
     * @return
     */
    private Observable<Poster> getPosterObb() {
        return CommonApi.getBanner(this, HljCommon.BLOCK_ID.WeddingBibleActivity, city.getId())
                .map(new Func1<PosterData, Poster>() {
                    @Override
                    public Poster call(PosterData posterData) {
                        if (posterData == null) {
                            return null;
                        }
                        List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                                HljCommon.POST_SITES.WEDDING_BIBLE_BANNER,
                                false);
                        return CommonUtil.isCollectionEmpty(posters) ? null : posters.get(0);
                    }
                })
                .onErrorReturn(new Func1<Throwable, Poster>() {
                    @Override
                    public Poster call(Throwable throwable) {
                        return null;
                    }
                });
    }

    private class ResultZip {
        HljWeddingBiblesData biblesData;
        Poster poster;
        HljHttpData<List<CommunityFeed>> feedsData;

        public ResultZip(
                HljWeddingBiblesData biblesData,
                Poster poster,
                HljHttpData<List<CommunityFeed>> feedsData) {
            this.biblesData = biblesData;
            this.poster = poster;
            this.feedsData = feedsData;
        }
    }

    private void setData(ResultZip resultZip) {
        biblesData = resultZip.biblesData;
        if (biblesData == null) {
            setActionBarAlpha(1);
            emptyView.showEmptyView();
            headerLayout.setVisibility(View.GONE);
            return;
        }
        setActionBarAlpha(0);
        tvTitle.setText(biblesData.getTitle());
        tvWatchCount.setText(String.valueOf(biblesData.getWatchCount()));
        setAvatars(biblesData.getLastUsers());

        recyclerView.setVisibility(View.VISIBLE);
        adapter.setCatalogs(resultZip.biblesData.getData());
        adapter.setPoster(resultZip.poster);
        setFeeds(resultZip.feedsData);
    }

    private void setAvatars(List<Author> users) {
        if (CommonUtil.isCollectionEmpty(users)) {
            avatarsLayout.setVisibility(View.GONE);
        } else {
            avatarsLayout.setVisibility(View.VISIBLE);
            int count = avatarsLayout.getChildCount();
            int size = Math.min(5, users.size());
            if (count > size) {
                avatarsLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                Author author = users.get(i);
                View view = null;
                if (count > i) {
                    view = avatarsLayout.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(this, R.layout.wedding_bible_avatars_item, avatarsLayout);
                    view = avatarsLayout.getChildAt(avatarsLayout.getChildCount() - 1);
                }
                RoundedImageView imgAvatar = view.findViewById(R.id.img_avatar);
                Glide.with(this)
                        .load(ImagePath.buildPath(author.getAvatar())
                                .width(avatarSize)
                                .cropPath())
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary)
                                .error(R.mipmap.icon_avatar_primary))
                        .into(imgAvatar);
            }
        }
    }

    private void refreshFeeds() {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityFeed>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CommunityFeed>> listHljHttpData) {
                            setFeeds(listHljHttpData);
                        }
                    })
                    .build();
            CommunityApi.getWeddingBibleFeedsObb(1, HljCommon.PER_PAGE)
                    .subscribe(refreshSub);
        }
    }

    private void setFeeds(HljHttpData<List<CommunityFeed>> feedsData) {
        int pageCount = 0;
        List<CommunityFeed> feeds = null;
        if (feedsData != null) {
            pageCount = feedsData.getPageCount();
            feeds = feedsData.getData();
        }
        adapter.setFeeds(feeds);
        initPagination(pageCount);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<CommunityFeed>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView,
                pageCount,
                new PagingListener<HljHttpData<List<CommunityFeed>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityFeed>>> onNextPage(int page) {
                        return CommunityApi.getWeddingBibleFeedsObb(page, HljCommon.PER_PAGE);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityFeed>>>
                        () {
                    @Override
                    public void onNext(HljHttpData<List<CommunityFeed>> listHljHttpData) {
                        adapter.addFeeds(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.SEND_THREAD_COMPLETE:
                    refreshFeeds();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_share)
    void onShare() {
        if (biblesData == null) {
            return;
        }
        ShareInfo shareInfo = biblesData.getShareInfo();
        if (shareInfo == null) {
            return;
        }
        ShareDialogUtil.onCommonShare(this, shareInfo);
    }

    @OnClick(R.id.last_bible_layout)
    void onLastBible() {
        Intent intent = new Intent(this, CommunityThreadDetailActivity.class);
        intent.putExtra(CommunityThreadDetailActivity.ARG_ID, lastBible.getThreadId());
        startActivity(intent);
    }

    @OnClick(R.id.create_layout)
    void onCreate() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        Intent intent = new Intent(this, CreateThreadActivity.class);
        startActivityForResult(intent, Constants.RequestCode.SEND_THREAD_COMPLETE);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.activity_anim_default);
    }

    private void showCreateView() {
        if (createLayout.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(createLayout,
                    new Fade(Fade.IN).setDuration(300));
            createLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideCreateView() {
        if (createLayout.getVisibility() != View.INVISIBLE) {
            TransitionManager.beginDelayedTransition(createLayout,
                    new Fade(Fade.OUT).setDuration(300));
            createLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String json = SPUtils.getString(this,
                HljCommon.SharedPreferencesNames.PREF_LAST_BIBLE,
                null);
        lastBible = GsonUtil.getGsonInstance()
                .fromJson(json, WeddingBible.class);
        if (lastBible == null) {
            lastBibleLayout.setVisibility(View.GONE);
        } else {
            lastBibleLayout.setVisibility(View.VISIBLE);
            tvLastBible.setText(getString(R.string.label_last_wedding_bible, lastBible.getTitle()));
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, refreshSub, pageSub, addWatchCountSub);
    }
}