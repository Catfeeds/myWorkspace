package com.hunliji.hljcardlibrary.views.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.CommonPagerAdapter;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.models.Template;
import com.hunliji.hljcardlibrary.models.wrappers.PageEditResult;
import com.hunliji.hljcardlibrary.utils.PrivilegeUtil;
import com.hunliji.hljcardlibrary.views.fragments.CardTemplateFragment;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by hua_rong on 2017/6/16.
 * 增加新页
 */

public class CardTemplateActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener, CardTemplateFragment.TemplateListInterface {

    @BindView(R2.id.pager)
    ViewPager pager;
    @BindView(R2.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R2.id.hint_layout)
    LinearLayout hintLayout;
    private Card card;
    private List<Fragment> fragmentList;
    private boolean hasChecked; // 是否已经获得vip模板使用权限的结果
    private boolean isVipAvailable; // vip模板使用权限的结果
    private HljHttpSubscriber checkSub;

    private Subscription createSubscription, rxSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        card = getIntent().getParcelableExtra("card");
        setContentView(R.layout.activity_card_theme_list___card);
        ButterKnife.bind(this);
        if (card != null) {
            initView();
            initLoad();
        }
        registerRxBusEvent();
    }

    private void initView() {
        fragmentList = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.card_add_new_page_title___card);
        String[] keys = getResources().getStringArray(R.array.card_add_new_page_key___card);
        List<String> titleList = Arrays.asList(titles);
        for (String key : keys) {
            fragmentList.add(CardTemplateFragment.newInstance(key, card.getThemeId()));
        }
        tabIndicator.setTabViewId(R.layout.menu_tab_widget___card);
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(getSupportFragmentManager(),
                fragmentList,
                titleList);
        pager.setAdapter(pagerAdapter);
        tabIndicator.setPagerAdapter(pagerAdapter);
        tabIndicator.setOnTabChangeListener(this);
        View tabView3 = tabIndicator.getTabView(titleList.size() - 2);
        if (tabView3 != null) {
            tabView3.setPadding(CommonUtil.dp2px(this, 7), 0, CommonUtil.dp2px(this, 2), 0);
        }
        View tabView = tabIndicator.getTabView(titleList.size() - 1);
        if (tabView != null) {
            View view = tabView.findViewById(R.id.iv_tag);
            if (view != null) {
                tabView.setPadding(0, 0, CommonUtil.dp2px(this, 8), 0);
                view.setVisibility(View.VISIBLE);
            }
        }
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setCurrentItem(position);
                if (hintLayout.getVisibility() == View.VISIBLE) {
                    hintLayout.setVisibility(View.GONE);
                }
                super.onPageSelected(position);
            }
        });
        showCardDragHint();
    }

    private void initLoad() {
        PrivilegeUtil.isVipAvailable(this, checkSub, new PrivilegeUtil.PrivilegeCallback() {
            @Override
            public void checkDone(boolean isAvailable) {
                hasChecked = true;
                isVipAvailable = isAvailable;
            }
        });
    }

    private void registerRxBusEvent() {
        rxSubscription = RxBus.getDefault()
                .toObservable(CardRxEvent.class)
                .subscribe(new RxBusSubscriber<CardRxEvent>() {
                    @Override
                    protected void onEvent(CardRxEvent cardRxEvent) {
                        switch (cardRxEvent.getType()) {
                            case CARD_APP_SHARE_SUCCESS:
                                refreshFragment();
                                break;
                        }
                    }
                });
    }

    /**
     * 刷新所有的子fragment
     */
    public void refreshFragment() {
        if (fragmentList != null) {
            for (int i = 0; i < fragmentList.size(); i++) {
                Fragment fragment = fragmentList.get(i);
                if (fragment instanceof CardTemplateFragment) {
                    CardTemplateFragment cardThemeFragment = (CardTemplateFragment) fragment;
                    cardThemeFragment.refresh();
                }
            }
        }
    }

    @Override
    public void onTabChanged(int position) {
        pager.setCurrentItem(position);
    }

    private void showCardDragHint() {
        boolean b = SPUtils.getBoolean(this, "cardTemplate", false);
        if (b) {
            hintLayout.setVisibility(View.GONE);
            return;
        }
        SPUtils.put(this, "cardTemplate", true);
        hintLayout.setVisibility(View.VISIBLE);
        hintLayout.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(hintLayout,
                        "translationY",
                        -hintLayout.getHeight() * 0.1f)
                        .setDuration(300);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setRepeatCount(3);
                animator.setStartDelay(100);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isFinishing()) {
                            return;
                        }
                        if (hintLayout.getVisibility() == View.VISIBLE) {
                            hintLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            }
        });
    }

    @Override
    public void onCreatePage(final Template template) {
        if(template.isSupportVideo()){
            for (CardPage cardPage:card.getAllPages()){
                for(ImageInfo imageInfo:cardPage.getImageInfos()){
                    if(imageInfo.isVideo()){
                        ToastUtil.showToast(this,null,R.string.msg_card_video_just_one___card);
                        return;
                    }
                }
            }
        }
        if (createSubscription != null && !createSubscription.isUnsubscribed()) {
            return;
        }
        createSubscription = Observable.merge(Observable.just(template),
                Observable.from(template.getImagePaths(this))
                        .filter(new Func1<String, Boolean>() {
                            @Override
                            public Boolean call(String path) {
                                File file = FileUtil.createThemeFile(CardTemplateActivity.this,
                                        path);
                                return file == null || !file.exists() || file.length() == 0;
                            }
                        })
                        .concatMap(new Func1<String, Observable<File>>() {
                            @Override
                            public Observable<File> call(String path) {
                                return FileApi.download(path,
                                        FileUtil.createThemeFile(CardTemplateActivity.this, path)
                                                .getAbsolutePath());
                            }
                        })
                        .map(new Func1<File, Template>() {
                            @Override
                            public Template call(File file) {
                                return template;
                            }
                        }),
                Observable.from(template.getFontPaths(this))
                        .filter(new Func1<String, Boolean>() {
                            @Override
                            public Boolean call(String path) {
                                File file = FileUtil.createFontFile(CardTemplateActivity.this,
                                        path);
                                return file == null || !file.exists() || file.length() == 0;
                            }
                        })
                        .concatMap(new Func1<String, Observable<File>>() {
                            @Override
                            public Observable<File> call(String path) {
                                return FileApi.download(path,
                                        FileUtil.createFontFile(CardTemplateActivity.this, path)
                                                .getAbsolutePath());
                            }
                        })
                        .map(new Func1<File, Template>() {
                            @Override
                            public Template call(File file) {
                                return template;
                            }
                        }))
                .last()
                .concatMap(new Func1<Template, Observable<?>>() {
                    @Override
                    public Observable<?> call(Template template) {
                        CardPage cardPage = new CardPage(template);
                        cardPage.setCardId(card.getId());
                        return CardApi.editPage(cardPage);
                    }
                })
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnNextListener(new SubscriberOnNextListener<PageEditResult>() {
                            @Override
                            public void onNext(PageEditResult pageEditResult) {
                                Intent intent = getIntent();
                                intent.putExtra("editResult", pageEditResult);
                                setResult(RESULT_OK, intent);
                                onBackPressed();
                            }
                        })
                        .build());
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(createSubscription, rxSubscription, checkSub);
        super.onFinish();
    }

    public boolean hasChecked() {
        return hasChecked;
    }

    public boolean isVipAvailable() {
        return isVipAvailable;
    }
}
