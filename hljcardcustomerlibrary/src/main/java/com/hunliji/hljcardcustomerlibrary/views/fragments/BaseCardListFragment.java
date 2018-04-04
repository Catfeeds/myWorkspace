package com.hunliji.hljcardcustomerlibrary.views.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardReplyActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardTutorialActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.ReceiveGiftCashActivity;
import com.hunliji.hljcardcustomerlibrary.views.delegates.CardListBarDelegate;
import com.hunliji.hljcardlibrary.adapter.CardAdapter;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.wrappers.CardListData;
import com.hunliji.hljcardlibrary.models.wrappers.CardNotice;
import com.hunliji.hljcardlibrary.views.activities.CardThemeActivity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.BannerJumpService;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by wangtao on 2017/11/27.
 */

public abstract class BaseCardListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.tv_sign_count)
    TextView tvSignCount;
    @BindView(R2.id.tv_gift_count)
    TextView tvGiftCount;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.poster_layout)
    RelativeLayout posterLayout;
    @BindView(R2.id.rc_cards)
    PullToRefreshVerticalRecyclerView rcCards;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.course_layout)
    RelativeLayout courseLayout;
    @BindView(R2.id.guest_layout)
    RelativeLayout guestLayout;
    @BindView(R2.id.cash_layout)
    RelativeLayout cashLayout;
    @BindView(R2.id.btn_create)
    Button btnCreate;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.appbar)
    AppBarLayout appbar;
    @BindView(R2.id.menu_layout)
    LinearLayout menuLayout;
    @BindView(R2.id.iv_card_view)
    RoundedImageView ivCardView;
    @BindView(R2.id.notice_layout)
    RelativeLayout noticeLayout;
    @BindView(R2.id.tv_news)
    TextView tvNews;
    @BindView(R2.id.btn_close)
    ImageButton btnClose;
    @BindView(R2.id.top_layout)
    LinearLayout topLayout;
    Unbinder unbinder;

    private CardAdapter cardAdapter;
    private Subscription cardsSubscription;
    private Subscription rxSubscription;
    private Subscription posterSubscription;
    private Poster poster;
    private long copyCardId;
    private Realm realm;
    private CardNotice cardNotice;

    private CardListBarDelegate actionBarDelegate;

    private Subscription signCountAnimSubscription;
    private Subscription giftCountAnimSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        cardAdapter = new CardAdapter(getContext());
        registerRxBusEvent();
    }

    private void registerRxBusEvent() {
        rxSubscription = RxBus.getDefault()
                .toObservable(CardRxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<CardRxEvent>() {
                    @Override
                    protected void onEvent(CardRxEvent cardRxEvent) {
                        switch (cardRxEvent.getType()) {
                            case CREATE_CARD:
                            case CARD_INFO_EDIT:
                            case CARD_CLOSE_CHANGE:
                                onRefresh(null);
                                break;
                            case CARD_DELETE:
                                cardAdapter.deleteCard((Card) cardRxEvent.getObject());
                                break;
                            case CARD_COPY:
                                copyCardId = (long) cardRxEvent.getObject();
                                onRefresh(null);
                                break;
                            case CARD_THUMB_UPDATE:
                                cardAdapter.updateCardCover((long) cardRxEvent.getObject());
                                break;
                        }
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_list___card, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        initTracker();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initView() {
        actionBarDelegate = getActionBarDelegate();
        actionBarDelegate.inflateActionBar(toolbar);

        HljBaseActivity.setActionBarPadding(getContext(), toolbar);
        ((ViewGroup.MarginLayoutParams) menuLayout.getLayoutParams()).topMargin +=
                HljBaseActivity.getStatusBarHeight(
                getContext());
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_wedding_card);
        emptyView.setHintId(R.string.hint_empty_wedding_card___card);

        rcCards.getRefreshableView()
                .setLayoutManager(new GridLayoutManager(getContext(), 2));
        rcCards.getRefreshableView()
                .setAdapter(cardAdapter);
        rcCards.getRefreshableView()
                .addItemDecoration(new CardItemDecoration(getContext()));
        rcCards.setOnRefreshListener(this);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (rcCards == null) {
                    return;
                }
                rcCards.setMode(verticalOffset == 0 ? PullToRefreshBase.Mode.PULL_FROM_START :
                        PullToRefreshBase.Mode.DISABLED);
                final float scale = 1 + ((float) verticalOffset) / menuLayout.getHeight();
                for (int i = 0, size = menuLayout.getChildCount(); i < size; i++) {
                    final View childView = menuLayout.getChildAt(i);
                    childView.setPivotX(childView.getWidth() / 2);
                    childView.setPivotY(0);
                    childView.setScaleX(scale);
                    childView.setScaleY(scale);
                    if (childView instanceof ViewGroup && ((ViewGroup) childView).getChildCount()
                            > 0) {
                        Observable.range(0, ((ViewGroup) childView).getChildCount())
                                .map(new Func1<Integer, View>() {
                                    @Override
                                    public View call(Integer integer) {
                                        return ((ViewGroup) childView).getChildAt(integer);
                                    }
                                })
                                .filter(new Func1<View, Boolean>() {
                                    @Override
                                    public Boolean call(View view) {
                                        return view instanceof TextView && view.getId() == View
                                                .NO_ID;
                                    }
                                })
                                .subscribe(new Action1<View>() {
                                    @Override
                                    public void call(View view) {
                                        view.setVisibility(scale >= 0.9 ? View.VISIBLE : View
                                                .INVISIBLE);
                                    }
                                });
                    }
                }
            }
        });
    }

    private void initTracker() {
        HljVTTagger.buildTagger(btnCreate)
                .tagName("create_button")
                .hitTag();
        HljVTTagger.buildTagger(courseLayout)
                .tagName("tutorial_button")
                .hitTag();
        HljVTTagger.buildTagger(guestLayout)
                .tagName("guest_button")
                .hitTag();
        HljVTTagger.buildTagger(cashLayout)
                .tagName("cash_button")
                .hitTag();
    }

    @SuppressWarnings("unchecked")
    private void initLoad() {
        if (cardsSubscription != null && !cardsSubscription.isUnsubscribed()) {
            return;
        }
        cardsSubscription = CardApi.getCardList()
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                        .setProgressBar(rcCards.isRefreshing() ? null : progressBar)
                        .setContentView(rcCards)
                        .setPullToRefreshBase(rcCards)
                        .setEmptyView(emptyView)
                        .setOnNextListener(new SubscriberOnNextListener<CardListData>() {
                            @Override
                            public void onNext(CardListData cardListData) {
                                if (CommonUtil.isCollectionEmpty(cardListData.getCards())) {
                                    rcCards.setVisibility(View.GONE);
                                    emptyView.showEmptyView();
                                } else {
                                    if (copyCardId > 0) {
                                        for (Card card : cardListData.getCards()) {
                                            if (card.getId() == copyCardId) {
                                                copyCardId = 0;
                                                card.setCopyCard(true);
                                                break;
                                            }
                                        }
                                    }
                                    rcCards.setVisibility(View.VISIBLE);
                                    emptyView.hideEmptyView();
                                }
                                cardNotice = cardListData.getCardNotice();
                                actionBarDelegate.isHasOld(cardListData.isHasOld());
                                cardAdapter.setCards(cardListData.getCards());
                                rcCards.getRefreshableView()
                                        .scrollToPosition(0);
                                setNoticeView();
                            }
                        })
                        .build());
        if (posterSubscription != null && !posterSubscription.isUnsubscribed()) {
            return;
        }
        posterSubscription = getPosterObb().filter(new Func1<Poster, Boolean>() {
            @Override
            public Boolean call(Poster poster) {
                return poster != null && poster.getId() > 0;
            }
        })
                .subscribe(new Subscriber<Poster>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        posterLayout.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Poster poster) {
                        BaseCardListFragment.this.poster = poster;
                        posterLayout.setVisibility(View.VISIBLE);
                        setPostView(poster);
                        setNoticeView();
                    }
                });
    }

    private void setPostView(Poster poster) {
        if (poster != null) {
            posterLayout.setVisibility(View.VISIBLE);
            String path = poster.getPath();
            Point point = CommonUtil.getDeviceSize(getContext());
            int width = point.x - CommonUtil.dp2px(getContext(), 20);
            int height = Math.round(width * 7 / 36);
            ivCardView.getLayoutParams().width = width;
            ivCardView.getLayoutParams().height = height;
            if (!TextUtils.isEmpty(path)) {
                Glide.with(getContext())
                        .load(ImagePath.buildPath(path)
                                .width(width)
                                .height(height)
                                .cropPath())
                        .apply(new RequestOptions().override(width, height)
                                .placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(ivCardView);
            }
        }
    }

    private void setNoticeView() {
        if (cardNotice != null && !TextUtils.isEmpty(cardNotice.getMsg()) && !OncePrefUtil
                .hasDoneThisById(
                getContext(),
                HljCommon.SharedPreferencesNames.CLOSED_CARD_NOTICE_IDS,
                cardNotice.getId())) {
            posterLayout.setVisibility(View.GONE);
            noticeLayout.setVisibility(View.VISIBLE);
            topLayout.setVisibility(View.VISIBLE);
            tvNews.setVisibility(View.VISIBLE);
            tvNews.setText(cardNotice.getMsg());
        } else {
            noticeLayout.setVisibility(View.GONE);
            if (poster != null) {
                posterLayout.setVisibility(View.VISIBLE);
                topLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R2.id.btn_close)
    void onCloseNotice() {
        noticeLayout.setVisibility(View.GONE);
        topLayout.setVisibility(View.GONE);
        OncePrefUtil.doneThisById(getContext(),
                HljCommon.SharedPreferencesNames.CLOSED_CARD_NOTICE_IDS,
                cardNotice.getId());
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (unbinder == null) {
            return;
        }
        initLoad();
    }


    @OnClick(R2.id.iv_card_view)
    public void onNoticeClicked() {
        if (poster == null) {
            return;
        }
        // 使用Name索引寻找ARouter中已注册的对应服务
        BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                .build(RouterPath.ServicePath.BANNER_JUMP)
                .navigation(getContext());
        if (bannerJumpService != null) {
            bannerJumpService.bannerJump(getContext(), poster, null);
        }
    }

    @OnClick(R2.id.btn_create)
    public void onCreateCard() {
        Intent intent = new Intent(getContext(), CardThemeActivity.class);
        startActivity(intent);
    }

    @OnClick(R2.id.course_layout)
    public void onCourse() {
        startActivity(new Intent(getContext(), CardTutorialActivity.class));
    }

    @OnClick(R2.id.guest_layout)
    public void onGuest() {
        startActivity(new Intent(getContext(), CardReplyActivity.class));
    }

    @OnClick(R2.id.cash_layout)
    public void onCash() {
        startActivity(new Intent(getContext(), ReceiveGiftCashActivity.class));
    }

    protected void onCountRefresh() {
        User user = UserSession.getInstance()
                .getUser(getContext());
        long signCount = 0;
        long giftCount = 0;
        if (user != null && user.getId() > 0) {
            signCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .equalTo("notifyType", Notification.NotificationType.SIGN)
                    .count();
            giftCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .equalTo("notifyType", Notification.NotificationType.GIFT)
                    .count();
        }
        if (signCount == 0) {
            tvSignCount.setVisibility(View.GONE);
            CommonUtil.unSubscribeSubs(signCountAnimSubscription);
        } else {
            tvSignCount.setVisibility(View.VISIBLE);
            if (signCount > 99) {
                tvSignCount.setText("99+");
            } else {
                tvSignCount.setText(String.valueOf(signCount));
            }
            if (CommonUtil.isUnsubscribed(signCountAnimSubscription)) {
                signCountAnimSubscription = startNoticeCountAnimation(tvSignCount);
            }
        }
        if (giftCount == 0) {
            tvGiftCount.setVisibility(View.GONE);
            CommonUtil.unSubscribeSubs(giftCountAnimSubscription);
        } else {
            tvGiftCount.setVisibility(View.VISIBLE);
            if (giftCount > 99) {
                tvGiftCount.setText("99+");
            } else {
                tvGiftCount.setText(String.valueOf(giftCount));
            }
            if (CommonUtil.isUnsubscribed(giftCountAnimSubscription)) {
                giftCountAnimSubscription = startNoticeCountAnimation(tvGiftCount);
            }
        }
    }

    private Subscription startNoticeCountAnimation(final View view) {
        return Observable.interval(0, 10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        ObjectAnimator.ofFloat(view,
                                "translationY",
                                0,
                                -8,
                                0,
                                8,
                                0,
                                -6,
                                0,
                                6,
                                0,
                                -4,
                                0,
                                4,
                                0)
                                .setDuration(3000)
                                .start();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cardAdapter != null) {
            cardAdapter.notifyDataSetChanged();
        }
        onCountRefresh();
    }

    @Override
    public void onPause() {
        CommonUtil.unSubscribeSubs(signCountAnimSubscription, giftCountAnimSubscription);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        CommonUtil.unSubscribeSubs(cardsSubscription, rxSubscription, posterSubscription);
        rcCards.getRefreshableView()
                .setAdapter(null);
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (actionBarDelegate != null) {
            actionBarDelegate.unbind();
            actionBarDelegate = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (realm != null) {
            realm.close();
        }
        super.onDestroy();
    }

    @Override
    public void refresh(Object... params) {

    }

    protected abstract CardListBarDelegate getActionBarDelegate();

    protected abstract Observable<Poster> getPosterObb();


    private class CardItemDecoration extends RecyclerView.ItemDecoration {

        private int itemSpace;

        private CardItemDecoration(Context context) {
            itemSpace = CommonUtil.dp2px(context, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                outRect.left = itemSpace;
                outRect.right = itemSpace / 2;
            } else {
                outRect.left = itemSpace / 2;
                outRect.right = itemSpace;
            }
            if (parent.getChildAdapterPosition(view) / 2 == 0) {
                outRect.top = itemSpace;
            } else {
                outRect.top = 0;
            }
            outRect.bottom = itemSpace;
        }
    }

}
