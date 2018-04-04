package me.suncloud.marrymemo.view.community;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljcommonlibrary.views.widgets.foldingmenu.FoldingLayout;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.viewholder.SimilarWeddingTaskViewHolder;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.fragment.WeddingDateFragment;
import me.suncloud.marrymemo.fragment.community.SimilarWeddingFeedsFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.community.SimilarWeddingDetail;
import me.suncloud.marrymemo.model.marry.MarryTask;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.marry.MarryTaskActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 同婚期页
 * Created by chen_bin on 2018/3/12 0012.
 */
public class SimilarWeddingActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener, SimilarWeddingTaskViewHolder.OnCheckTaskListener {

    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_total_count)
    TextView tvTotalCount;
    @BindView(R.id.avatars_layout)
    LinearLayout avatarsLayout;
    @BindView(R.id.tv_sign_in)
    TextView tvSignIn;
    @BindView(R.id.sign_in_layout)
    LinearLayout signInLayout;
    @BindView(R.id.tv_progress_rate)
    TextView tvProgressRate;
    @BindView(R.id.tv_expand)
    TextView tvExpand;
    @BindView(R.id.tasks_layout)
    LinearLayout tasksLayout;
    @BindView(R.id.task_content_layout)
    FoldingLayout taskContentLayout;
    @BindView(R.id.task_root_layout)
    LinearLayout taskRootLayout;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout scrollableLayout;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.btn_msg)
    ImageButton btnMsg;
    @BindView(R.id.msg_notice_view)
    View msgNoticeView;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;
    @BindView(R.id.create_layout)
    RelativeLayout createLayout;

    private NoticeUtil noticeUtil;

    private SparseArray<SimilarWeddingFeedsFragment> fragments;
    private User user;
    private City city;

    private boolean isAnimEnd = true;

    private int coverHeight;
    private int alphaHeight;
    private int avatarSize;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber checkTaskSub;

    @Override
    public String pageTrackTagName() {
        return "同婚期详情页";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_wedding);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, loadingLayout);
        initTracker();
        initValues();
        initViews();
        initLoad();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(signInLayout)
                .tagName(HljTaggerName.BTN_SIGN_IN)
                .hitTag();
        HljVTTagger.buildTagger(createLayout)
                .tagName(HljTaggerName.BTN_SEND_DISCUSSION)
                .hitTag();
    }

    private void initValues() {
        fragments = new SparseArray<>();
        int coverWidth = CommonUtil.getDeviceSize(this).x;
        coverHeight = Math.round(coverWidth * 204.0f / 375.0f);
        alphaHeight = coverHeight - CommonUtil.dp2px(this, 45) - getStatusBarHeight();
        avatarSize = CommonUtil.dp2px(this, 28);

        user = Session.getInstance()
                .getCurrentUser(this);
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
        scrollableLayout.setExtraHeight(coverHeight - alphaHeight);
        scrollableLayout.setOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                if (scrollableLayout.getHelper()
                        .getScrollableView() == null) {
                    scrollableLayout.getHelper()
                            .setCurrentScrollableContainer(getCurrentScrollableContainer());
                }
                if (currentY >= alphaHeight) {
                    setActionBarAlpha(1);
                } else {
                    setActionBarAlpha(currentY * 1.0f / alphaHeight);
                }
            }
        });
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                scrollableLayout.getHelper()
                        .setCurrentScrollableContainer(getCurrentScrollableContainer());
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
        indicator.setTabViewId(R.layout.main_tab_view___cm);
        indicator.setPagerAdapter(pagerAdapter);

        noticeUtil = new NoticeUtil(this, tvMsgCount, msgNoticeView);
        noticeUtil.onResume();
    }

    private void setActionBarAlpha(float alpha) {
        if (btnBack.getAlpha() == alpha) {
            return;
        }
        btnBack.setAlpha(alpha);
        tvToolbarTitle.setAlpha(alpha);
        btnMsg.setAlpha(alpha);
        int red = Color.red(0xffffffff);
        int green = Color.green(0xffffffff);
        int blue = Color.blue(0xffffffff);
        int a = (int) (Color.alpha(0xffffffff) * alpha);
        actionHolderLayout.setBackgroundColor(Color.argb(a, red, green, blue));
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SimilarWeddingFeedsFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case 0:
                    fragment = SimilarWeddingFeedsFragment.newInstance
                            (SimilarWeddingFeedsFragment.TAB_REFINED);
                    break;
                case 1:
                    fragment = SimilarWeddingFeedsFragment.newInstance
                            (SimilarWeddingFeedsFragment.TAB_NEW);
                    break;
            }
            fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "查漏补缺";
                case 1:
                    return "讨论";
                default:
                    return null;
            }
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private ScrollableHelper.ScrollableContainer getCurrentScrollableContainer() {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof
                SectionsPagerAdapter) {
            SectionsPagerAdapter adapter = (SectionsPagerAdapter) viewPager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            if (fragment instanceof ScrollableHelper.ScrollableContainer) {
                return (ScrollableHelper.ScrollableContainer) fragment;
            }
        }
        return null;
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            Observable<ResultZip> observable = CommunityApi.getSimilarWeddingDetailObb()
                    .concatMap(new Func1<SimilarWeddingDetail, Observable<ResultZip>>() {
                        @Override
                        public Observable<ResultZip> call(SimilarWeddingDetail detail) {
                            return Observable.zip(Observable.just(detail),
                                    getTasksObb(detail),
                                    new Func2<SimilarWeddingDetail, List<MarryTask>, ResultZip>() {
                                        @Override
                                        public ResultZip call(
                                                SimilarWeddingDetail detail,
                                                List<MarryTask> tasks) {
                                            return new ResultZip(detail, tasks);
                                        }
                                    });
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
                    .setContentView(scrollableLayout)
                    .build();
            observable.subscribe(initSub);
        }
    }

    /**
     * 未完成的3项任务
     *
     * @return
     */
    private Observable<List<MarryTask>> getTasksObb(SimilarWeddingDetail detail) {
        if (detail == null || detail.getTotalCount() < 200) {
            return Observable.just(null);
        } else {
            return MarryApi.getMarryTasks(1)
                    .map(new Func1<HljHttpData<List<MarryTask>>, List<MarryTask>>() {
                        @Override
                        public List<MarryTask> call(HljHttpData<List<MarryTask>> listHljHttpData) {
                            return listHljHttpData == null ? null : listHljHttpData.getData();
                        }
                    })
                    .onErrorReturn(new Func1<Throwable, List<MarryTask>>() {
                        @Override
                        public List<MarryTask> call(Throwable throwable) {
                            return null;
                        }
                    });
        }
    }

    private class ResultZip {
        SimilarWeddingDetail detail;
        List<MarryTask> tasks;

        public ResultZip(SimilarWeddingDetail detail, List<MarryTask> tasks) {
            this.detail = detail;
            this.tasks = tasks;
        }

    }

    private void setData(ResultZip resultZip) {
        SimilarWeddingDetail detail = resultZip.detail;
        if (detail == null) {
            setActionBarAlpha(1);
            emptyView.showEmptyView();
            scrollableLayout.setVisibility(View.GONE);
            return;
        }
        setActionBarAlpha(0);
        showCreateView();
        setWeddingDate();
        setSignInStatus(detail.isSignIn());
        setAvatars(detail);
        setTasks(detail, resultZip.tasks);
    }

    private void setWeddingDate() {
        if (user.getWeddingDay() == null) {
            tvDate.setText("未设置婚期");
        } else {
            DateTime date = new DateTime(user.getWeddingDay()
                    .getTime());
            tvDate.setText(date.toString(getString(R.string.format_date_type16)));
            indicator.setTabText(String.format("%s月新娘讨论", date.getMonthOfYear()),
                    SimilarWeddingFeedsFragment.TAB_NEW);
        }
    }

    private void setSignInStatus(boolean isSignIn) {
        if (isSignIn) {
            tvSignIn.setText("已打卡");
            signInLayout.setClickable(false);
        } else {
            tvSignIn.setText("打卡加入");
            signInLayout.setClickable(true);
        }
    }

    private void setAvatars(SimilarWeddingDetail detail) {
        List<Author> users = detail.getUsers();
        if (CommonUtil.isCollectionEmpty(users)) {
            avatarsLayout.setVisibility(View.GONE);
        } else {
            avatarsLayout.setVisibility(View.VISIBLE);
            int count = avatarsLayout.getChildCount();
            int size = Math.min(6, users.size());
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
                    View.inflate(this, R.layout.similar_wedding_avatars_item, avatarsLayout);
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

    private void setTasks(SimilarWeddingDetail detail, List<MarryTask> tasks) {
        tvTotalCount.setText(String.valueOf(detail.getTotalCount()));
        if (detail.getTotalCount() < 200) {
            taskRootLayout.setVisibility(View.GONE);
        } else {
            taskRootLayout.setVisibility(View.VISIBLE);
            tvProgressRate.setText(detail.getProgressRate() + "%");
            if (CommonUtil.isCollectionEmpty(tasks)) {
                tvExpand.setText("查看我的任务");
                tvExpand.setCompoundDrawablesWithIntrinsicBounds(0,
                        0,
                        R.mipmap.icon_arrow_right_accent_14_26,
                        0);
            } else {
                tvExpand.setText("接下来完成");
                tvExpand.setCompoundDrawablesWithIntrinsicBounds(0,
                        0,
                        R.mipmap.icon_arrow_down_accent_26_14,
                        0);
                int count = tasksLayout.getChildCount();
                int size = Math.min(3, tasks.size());
                if (count > size) {
                    tasksLayout.removeViews(size, count - size);
                }
                for (int i = 0; i < size; i++) {
                    MarryTask task = tasks.get(i);
                    View view = null;
                    if (count > i) {
                        view = tasksLayout.getChildAt(i);
                    }
                    if (view == null) {
                        View.inflate(this, R.layout.simialr_wedding_tasks_item, tasksLayout);
                        view = tasksLayout.getChildAt(tasksLayout.getChildCount() - 1);
                    }
                    SimilarWeddingTaskViewHolder holder = (SimilarWeddingTaskViewHolder) view
                            .getTag();
                    if (holder == null) {
                        holder = new SimilarWeddingTaskViewHolder(view);
                        view.setTag(holder);
                    }
                    holder.setOnCheckTaskListener(this);
                    holder.setView(this, task, i, 0);
                }
            }
            taskContentLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        }
    }

    @Override
    public void onCheckTask(final CheckableLinearLayout checkLayout, final MarryTask task) {
        checkLayout.setClickable(false);
        if (task.getStatus() == MarryTask.STATUS_UNDONE) {
            task.setStatus(MarryTask.STATUS_DONE);
            checkLayout.setChecked(true);
        } else {
            task.setStatus(MarryTask.STATUS_UNDONE);
            checkLayout.setChecked(false);
        }
        checkTaskSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        ToastUtil.showCustomToast(SimilarWeddingActivity.this,
                                task.getStatus() == MarryTask.STATUS_DONE ? "勾选任务成功" : "取消勾选任务成功");
                        checkLayout.setClickable(true);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        checkLayout.setClickable(true);
                    }
                })
                .build();
        MarryApi.checkTask(task.getId(), task.getStatus())
                .subscribe(checkTaskSub);
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_msg)
    void onMsg() {
        startActivity(new Intent(this, MessageHomeActivity.class));
    }

    @OnClick(R.id.tv_date)
    void onSelectDate() {
        DateTime date;
        if (user.getWeddingDay() != null) {
            date = new DateTime(user.getWeddingDay()
                    .getTime());
        } else {
            date = new DateTime(Calendar.getInstance()
                    .getTime());
        }
        WeddingDateFragment dateFragment = WeddingDateFragment.newInstance(date);
        dateFragment.show(getSupportFragmentManager(), "WeddingDateFragment");
        dateFragment.setOnDateSelectedListener(new WeddingDateFragment.onDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar) {
                user.setWeddingDay(calendar.getTime());
                setWeddingDate();
                refreshFeeds();
            }
        });
    }

    @OnClick({R.id.sign_in_layout})
    void onSignIn() {
        DateTime date = new DateTime(user.getWeddingDay());
        String title;
        if (city.getId() == City.ID_QUANGUO) {
            title = getString(R.string.hint_similar_wedding_title,
                    date.toString(getString(R.string.format_date_type7)));
        } else {
            title = getString(R.string.hint_similar_wedding_title2,
                    city.getName(),
                    date.toString(getString(R.string.format_date_type7)));
        }
        CommunityChannel channel = new CommunityChannel();
        channel.setId(CommunityChannel.ID_SIMILAR_WEDDING);
        channel.setTitle(getString(R.string.label_similar_wedding));
        Intent intent = new Intent(this, CreateThreadActivity.class);
        intent.putExtra(CreateThreadActivity.ARG_TITLE, title);
        intent.putExtra(CreateThreadActivity.ARG_CONTENT, title);
        intent.putExtra(CreateThreadActivity.ARG_SECONDARY_CONTENT_HINT,
                getString(R.string.hint_similar_wedding_secondary_content));
        intent.putExtra(CreateThreadActivity.ARG_CHANNEL, channel);
        startActivityForResult(intent, Constants.RequestCode.SIGN_IN);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.activity_anim_default);
    }

    @OnClick(R.id.tv_expand)
    void onExpand() {
        if (tasksLayout.getChildCount() == 0) {
            onMoreTask();
            return;
        }
        if (!isAnimEnd) {
            return;
        }
        final float foldFactor = taskContentLayout.getVisibility() != View.VISIBLE ? 1 : 0;
        ObjectAnimator animator = ObjectAnimator.ofFloat(taskContentLayout,
                "foldFactor",
                foldFactor,
                foldFactor > 0 ? 0 : 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                int top = -Math.round(taskContentLayout.getMeasuredHeight() * value);
                setViewTop(indicator, top);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimEnd = false;
                taskContentLayout.setVisibility(View.VISIBLE);
                tvExpand.setCompoundDrawablesWithIntrinsicBounds(0,
                        0,
                        foldFactor > 0 ? R.mipmap.icon_arrow_up_accent_26_14 : R.mipmap
                                .icon_arrow_down_accent_26_14,
                        0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimEnd = true;
                if (foldFactor == 0) {
                    setViewTop(indicator, 0);
                    taskContentLayout.setVisibility(View.GONE);
                }
            }
        });
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void setViewTop(View view, int top) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.topMargin = top;
        view.setLayoutParams(lp);
    }

    @OnClick(R.id.tv_more_task)
    void onMoreTask() {
        startActivity(new Intent(this, MarryTaskActivity.class));
    }

    @OnClick(R.id.create_layout)
    void onCreate() {
        Intent intent = new Intent(this, CreateThreadActivity.class);
        startActivityForResult(intent, Constants.RequestCode.SEND_THREAD_COMPLETE);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.SIGN_IN:
                    setSignInStatus(true);
                    refreshFeeds();
                    break;
                case Constants.RequestCode.SEND_THREAD_COMPLETE:
                    refreshFeeds();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshFeeds() {
        for (int i = 0, size = fragments.size(); i < size; i++) {
            SimilarWeddingFeedsFragment fragment = fragments.get(i);
            if (viewPager.getCurrentItem() == i) {
                fragment.refresh();
            } else {
                fragment.setNeedRefresh(true);
            }
        }
    }

    private void showCreateView() {
        if (createLayout.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(createLayout,
                    new Fade(Fade.IN).setDuration(300));
            createLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, checkTaskSub);
    }
}