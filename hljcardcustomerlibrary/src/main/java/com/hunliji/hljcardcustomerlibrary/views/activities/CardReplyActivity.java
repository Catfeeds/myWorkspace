package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.adapter.CardSelectAdapter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.views.fragments.CardReplyFragment;
import com.hunliji.hljcommonlibrary.adapters.CommonPagerAdapter;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import net.robinx.lib.blur.StackBlur;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by hua_rong on 2017/6/20.
 * 宾客回复
 */

public class CardReplyActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener, CardSelectAdapter.OnCardSelectListener {


    @BindView(R2.id.iv_card_bg)
    ImageView ivCardBg;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.rl_card_select)
    RelativeLayout rlCardSelect;
    @BindView(R2.id.pager)
    ViewPager pager;
    @BindView(R2.id.tab_indicator)
    TabPageIndicator tabIndicator;
    public static final int CARD_STATE_FEAST = 0;//赴宴
    public static final int CARD_STATE_WISH = 3;//祝福
    private HljHttpSubscriber subscriber;
    private List<Card> cardList;
    private CardSelectAdapter adapter;
    private List<Fragment> fragmentList;
    private Handler mHandler;
    private boolean isExpand;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_reply___card);
        ButterKnife.bind(this);
        initCardView();
        initView();
        readAll();
    }

    private void initCardView() {
        mHandler = new Handler();
        cardList = new ArrayList<>();
        adapter = new CardSelectAdapter(this, cardList);
        adapter.setOnCardSelectListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new CardSelectItemDecoration(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onOkButtonClick() {
        if (!isExpand) {
            showCardAnimation(true);
            isExpand = true;
        } else {
            showCardAnimation(false);
            isExpand = false;
        }
    }

    private void showCardAnimation(boolean show) {
        int height = CommonUtil.dp2px(this, 162);
        rlCardSelect.setVisibility(View.VISIBLE);
        ValueAnimator scaleY;
        if (show) {
            scaleY = ValueAnimator.ofInt(0, height);
        } else {
            scaleY = ValueAnimator.ofInt(height, 0);
        }
        scaleY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatorValue = Integer.valueOf(animation.getAnimatedValue() + "");
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rlCardSelect
                        .getLayoutParams();
                params.height = animatorValue;
                rlCardSelect.setLayoutParams(params);
            }
        });
        scaleY.setTarget(rlCardSelect);
        scaleY.setDuration(240);
        scaleY.start();
    }

    @Override
    public void onCardSelect(Card card, int position, String path) {
        if (fragmentList != null && tabIndicator != null && pager != null) {
            for (int i = 0, size = fragmentList.size(); i < size; i++) {
                CardReplyFragment fragment = (CardReplyFragment) fragmentList.get(i);
                if (fragment != null) {
                    if (i == 0) {
                        fragment.setRefresh(card.getId(), CARD_STATE_WISH);
                    } else if (i == 1) {
                        fragment.setRefresh(card.getId(), CARD_STATE_FEAST);
                    }
                }
            }
            tabIndicator.setCurrentItem(0);
            pager.setCurrentItem(0);
        }
        showCardBackGround(card, position, path);
    }

    private void showCardBackGround(Card card, int position, String path) {
        if (position == 0) {
            Glide.with(this)
                    .asBitmap()
                    .load(R.mipmap.icon_card_select_all___card)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                                final Bitmap resource, Transition glideAnimation) {
                            if (resource != null) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ivCardBg.setImageBitmap(StackBlur.blurNativelyPixels(
                                                resource,
                                                25,
                                                false));
                                    }
                                });
                            }
                        }
                    });
        } else {
            if (card != null) {
                if (!TextUtils.isEmpty(path)) {
                    Glide.with(this)
                            .asBitmap()
                            .load(ImagePath.buildPath(path)
                                    .width(CommonUtil.getDeviceSize(this).x)
                                    .height(CommonUtil.dp2px(this, 162))
                                    .cropPath())
                            .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(
                                        final Bitmap resource, Transition glideAnimation) {
                                    if (resource != null) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ivCardBg.setImageBitmap(StackBlur
                                                        .blurNativelyPixels(
                                                        resource,
                                                        25,
                                                        false));
                                            }
                                        });
                                    }
                                }
                            });
                } else {
                    Glide.with(this)
                            .clear(ivCardBg);
                    ivCardBg.setImageBitmap(null);
                }
            }
        }
    }

    private void initView() {
        List<String> titleList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        fragmentList.add(CardReplyFragment.newInstance(CARD_STATE_WISH));
        fragmentList.add(CardReplyFragment.newInstance(CARD_STATE_FEAST));
        titleList.add("宾客祝福");
        titleList.add("是否赴宴");
        tabIndicator.setTabViewId(R.layout.menu_tab_widget___card);
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(getSupportFragmentManager(),
                fragmentList,
                titleList);
        pager.setAdapter(pagerAdapter);
        tabIndicator.setPagerAdapter(pagerAdapter);
        tabIndicator.setOnTabChangeListener(this);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setCurrentItem(position);
                updateTabTag(position, false);
                setLastReplyId(position);
                super.onPageSelected(position);
            }
        });
        getReplyCardList();
    }

    private void getReplyCardList() {
        if (subscriber == null || subscriber.isUnsubscribed()) {
            Observable<HljHttpData<List<Card>>> observable = CustomerCardApi.getReplyCardList();
            subscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Card>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Card>> hljHttpData) {
                            if (hljHttpData != null) {
                                setOkText(R.string.label_select___card);
                                List<Card> cards = hljHttpData.getData();
                                Card card = new Card();
                                cardList.clear();
                                if (cards != null) {
                                    cardList.addAll(cards);
                                }
                                cardList.add(0, card);
                                showCardBackGround(null, 0, null);
                                adapter.setCardList(cardList);
                            }
                        }
                    })
                    .setDataNullable(true)
                    .build();
            observable.subscribe(subscriber);
        }
    }

    public void updateTabText(int count, int state) {
        switch (state) {
            case CARD_STATE_FEAST:
                tabIndicator.setTabText(getString(R.string.label_feast_count___card, count), 1);
                break;
            case CARD_STATE_WISH:
                tabIndicator.setTabText(getString(R.string.label_wish_count___card, count), 0);
                break;
        }
    }

    public void updateTabTag(int position, boolean show) {
        View tabView = tabIndicator.getTabView(position);
        if (tabView != null) {
            View view = tabView.findViewById(R.id.news);
            if (view != null && position == 1) {
                view.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }


    public int getCurrentItemPosition() {
        return pager.getCurrentItem();
    }

    @Override
    public void onTabChanged(int position) {
        pager.setCurrentItem(position);
        updateTabTag(position, false);
        setLastReplyId(position);
    }

    private void setLastReplyId(int position) {
        if (fragmentList != null) {
            Fragment fragment = fragmentList.get(position);
            if (fragment instanceof CardReplyFragment) {
                CardReplyFragment cardReplyFragment = (CardReplyFragment) fragment;
                cardReplyFragment.setLastReplyId();
            }
        }
    }

    @Override
    protected void onFinish() {
        readAll();
        realm.close();
        super.onFinish();
        CommonUtil.unSubscribeSubs(subscriber);
    }

    public class CardSelectItemDecoration extends RecyclerView.ItemDecoration {

        private Context context;

        public CardSelectItemDecoration(Context context) {
            this.context = context;
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = CommonUtil.dp2px(context, 10);
        }
    }


    private void readAll() {
        realm.beginTransaction();
        RealmResults<Notification> notifications = realm.where(Notification.class)
                .equalTo("userId",
                        UserSession.getInstance()
                                .getUser(this)
                                .getId())
                .notEqualTo("status", 2)
                .equalTo("notifyType", Notification.NotificationType.SIGN)
                .findAll();
        for (Notification notification : notifications) {
            notification.setStatus(2);
        }
        realm.commitTransaction();
    }


}
