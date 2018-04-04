package com.hunliji.hljnotelibrary.views.activities;

import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.FollowBody;
import com.hunliji.hljnotelibrary.views.fragments.NoteMarkListFragment;
import com.hunliji.hljnotelibrary.views.fragments.NoteMarkProductFragment;
import com.hunliji.hljnotelibrary.views.fragments.NoteMarkWorkFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * 笔记本标签详情页
 * Created by jinxin on 2017/6/26 0026.
 */

public class NoteMarkDetailActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener, ScrollableLayout.OnScrollListener {

    @Override
    public String pageTrackTagName() {
        return "标签详情";
    }

    @Override
    public VTMetaData pageTrackData() {
        long markId = getIntent().getLongExtra(ARG_MARK_ID, 0);
        return new VTMetaData(markId,"Mark");
    }

    public static final String SORT_DEFAULT = "score";//综合
    public static final String SORT_NEWS = "create_time";//最新
    public static final String SORT_NOTE_HOT = "note_hot";//笔记最热
    public static final String SORT_NOTE_NEW = "note_create_time";//笔记最新
    public static final String SORT_PRODUCT_NEWS = "shop_product_create_time";//婚品最新
    public static final String SORT_PRICE_LOW = "price_down";//价格降序
    public static final String SORT_PRICE_HIGH = "price_up";//价格升序

    private static final String TAG_NOTE = "note";
    private static final String TAG_WORK = "work";
    private static final String TAG_PRODUCT = "product";

    public static final String ARG_MARK_ID = "mark_id";

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.btn_follow)
    TextView btnFollow;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_des)
    TextView tvDes;
    @BindView(R2.id.indicator)
    TabPageIndicator indicator;
    @BindView(R2.id.tv_sort)
    TextView tvSort;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.scroll_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;

    private MarkFragmentAdapter fragmentAdapter;
    private NoteMarkListFragment noteListFragment;
    private NoteMarkWorkFragment noteWorkFragment;
    private NoteMarkProductFragment noteProductFragment;
    private PopupWindow sortPopupWindow;
    private CheckableLinearGroup sortCheckGroup;
    private int sortCheckedId;
    private HljHttpSubscriber loadSubscriber;
    private HljHttpSubscriber followSubscriber;
    private long markId;
    private String sort;
    private boolean isFollow;
    private List<ScrollAbleFragment> fragments;
    private SparseIntArray sortMap;
    private boolean needRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_mark_detail___note);
        ButterKnife.bind(this);


        initConstant();
        initWidget();
        initLoad();
        setSwipeBackEnable(true);
        setDefaultStatusBarPadding();
    }

    private void initConstant() {
        markId = getIntent().getLongExtra(ARG_MARK_ID, 0);
        sort = SORT_DEFAULT;
        sortMap = new SparseIntArray();
        fragments = new ArrayList<>();
        fragmentAdapter = new MarkFragmentAdapter(getSupportFragmentManager());
    }

    private void initWidget() {
        scrollableLayout.setMode(PullToRefreshBase.Mode.DISABLED);
        scrollableLayout.setVisibility(View.GONE);
        scrollableLayout.getRefreshableView()
                .addOnScrollListener(this);
        indicator.setOnTabChangeListener(this);
        indicator.setPagerAdapter(fragmentAdapter);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    bottomLayout.setVisibility(View.GONE);
                } else {
                    bottomLayout.setVisibility(View.VISIBLE);
                }

                if (sortMap.get(position) <= 0) {
                    sortMap.put(position, R.id.cb_default);
                }
                sortCheckedId = sortMap.get(position);
                onSortChecked(sortCheckedId, false);

                indicator.setCurrentItem(position);
                scrollableLayout.getRefreshableView()
                        .getHelper()
                        .setCurrentScrollableContainer(getCurrentFragment());
            }
        });
    }

    private void initLoad() {
        loadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setContentView(scrollableLayout)
                .setOnNextListener(new SubscriberOnNextListener<NoteMark>() {
                    @Override
                    public void onNext(NoteMark noteMark) {
                        setNoteMark(noteMark);
                    }
                })
                .build();
        Observable<NoteMark> obb = NoteApi.markDetail(markId);
        obb.subscribe(loadSubscriber);
    }

    private void setNoteMark(NoteMark mark) {
        if (mark == null || mark.getId() == 0) {
            return;
        }
        scrollableLayout.setVisibility(View.VISIBLE);
        tvTitle.setText(mark.getName());
        if (!TextUtils.isEmpty(mark.getName())) {
            tvName.setText("#" + mark.getName());
        }
        if (CommonUtil.isEmpty(mark.getDescribe())) {
            tvDes.setVisibility(View.GONE);
        } else {
            tvDes.setVisibility(View.VISIBLE);
            tvDes.setText(mark.getDescribe());
        }
        isFollow = mark.isFollow();
        setBtnFollow();
        bottomLayout.setVisibility(View.VISIBLE);
        indicator.addTab(0,
                getString(R.string.label_note_count1___note, String.valueOf(mark.getNoteCount())));
        noteListFragment = NoteMarkListFragment.newInstance(markId);
        indicator.setCurrentItem(0);
        fragments.add(noteListFragment);
        sort = SORT_NOTE_NEW;
        sortCheckedId = R.id.cb_default;
        sortMap.put(0, sortCheckedId);
        tvSort.setText("最新");
        if (mark.getSetMealCount() > 0) {
            noteWorkFragment = NoteMarkWorkFragment.newInstance(markId);
            fragments.add(noteWorkFragment);
            indicator.addTab(1,
                    getString(R.string.label_note_work1___note,
                            String.valueOf(mark.getSetMealCount())));
        }
        if (mark.getProductCount() > 0) {
            noteProductFragment = NoteMarkProductFragment.newInstance(markId);
            fragments.add(noteProductFragment);
            indicator.addTab(2,
                    getString(R.string.label_note_product1___note,
                            String.valueOf(mark.getProductCount())));
        }
        fragmentAdapter.notifyDataSetChanged();
        scrollableLayout.getRefreshableView()
                .getHelper()
                .setCurrentScrollableContainer(fragments.get(0));
    }

    @OnClick(R2.id.tv_publish)
    void onPublish() {
        noteListFragment.onPublish();
    }

    @OnClick(R2.id.btn_back2)
    void onBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (needRefresh) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @OnClick(R2.id.btn_follow)
    void onFollow() {
        if (followSubscriber != null && !followSubscriber.isUnsubscribed()) {
            return;
        }
        FollowBody body = new FollowBody();
        body.setId(markId);
        body.setFollowable_type("Mark");
        if (isFollow) {
            //已关注
            followSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(HljHttpResult hljHttpResult) {
                            if (hljHttpResult != null) {
                                if (hljHttpResult.getStatus()
                                        .getRetCode() == 0) {
                                    needRefresh = true;
                                    isFollow = false;
                                    setBtnFollow();
                                    Toast.makeText(NoteMarkDetailActivity.this,
                                            "操作成功",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(NoteMarkDetailActivity.this,
                                            hljHttpResult.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    })
                    .build();

            Observable<HljHttpResult> obb = NoteApi.unFollowNoteMark(body);
            obb.subscribe(followSubscriber);
        } else {
            followSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(HljHttpResult hljHttpResult) {
                            if (hljHttpResult != null) {
                                if (hljHttpResult.getStatus()
                                        .getRetCode() == 0) {
                                    needRefresh = true;
                                    isFollow = true;
                                    setBtnFollow();
                                    Toast.makeText(NoteMarkDetailActivity.this,
                                            "操作成功",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(NoteMarkDetailActivity.this,
                                            hljHttpResult.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    })
                    .build();

            Observable<HljHttpResult> obb = NoteApi.followNoteMark(body);
            obb.subscribe(followSubscriber);
        }
    }

    private void setBtnFollow() {
        btnFollow.setText(isFollow ? getString(R.string.label_followed___cm) : getString(R.string
                .label_follow___cv));
        btnFollow.setBackgroundResource(isFollow ? R.drawable.sp_r4_stroke1_gray : R.drawable
                .sp_r4_stroke1_primary_solid_white);
        int color = isFollow ? getResources().getColor(R.color.colorGray) : getResources().getColor(
                R.color.colorPrimary);
        btnFollow.setTextColor(color);
    }

    @OnClick(R2.id.tv_sort)
    void onSort() {
        showSortWindow(tvSort);
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private void showSortWindow(View view) {
        if (sortPopupWindow != null && sortPopupWindow.isShowing()) {
            sortPopupWindow.dismiss();
        }

        if (sortPopupWindow == null) {
            sortPopupWindow = new PopupWindow(this);
            View contentView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_note_sort___note, null, false);
            sortPopupWindow.setContentView(contentView);
            ColorDrawable mDrawable = new ColorDrawable(ContextCompat.getColor(this,
                    android.R.color.transparent));
            sortPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            sortPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            sortPopupWindow.setBackgroundDrawable(mDrawable);
            sortPopupWindow.setOutsideTouchable(true);
            sortCheckGroup = (CheckableLinearGroup) contentView.findViewById(R.id.check_group);
            sortCheckGroup.setOnCheckedChangeListener(new CheckableLinearGroup
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                    onSortChecked(checkedId, true);
                }
            });
            sortCheckedId = R.id.cb_default;
        }
        int position = viewPager.getCurrentItem();
        TextView tvNew = (TextView) sortPopupWindow.getContentView()
                .findViewById(R.id.tv_new);
        TextView tvDefault = (TextView) sortPopupWindow.getContentView()
                .findViewById(R.id.tv_default);
        switch (position) {
            case 0:
                tvDefault.setText("最新");
                tvNew.setText("最热");
                sortPopupWindow.getContentView()
                        .findViewById(R.id.cb_price_high)
                        .setVisibility(View.GONE);
                sortPopupWindow.getContentView()
                        .findViewById(R.id.cb_price_low)
                        .setVisibility(View.GONE);
                break;
            case 1:
                tvDefault.setText("综合");
                tvNew.setText("最新");
                sortPopupWindow.getContentView()
                        .findViewById(R.id.cb_price_high)
                        .setVisibility(View.VISIBLE);
                sortPopupWindow.getContentView()
                        .findViewById(R.id.cb_price_low)
                        .setVisibility(View.VISIBLE);
                break;
            case 2:
                tvDefault.setText("综合");
                tvNew.setText("最新");
                sortPopupWindow.getContentView()
                        .findViewById(R.id.cb_price_high)
                        .setVisibility(View.VISIBLE);
                sortPopupWindow.getContentView()
                        .findViewById(R.id.cb_price_low)
                        .setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        sortCheckGroup.check(sortCheckedId);
        int[] location = getShowLocation(view);
        sortPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1]);
    }

    private int[] getShowLocation(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        Paint.FontMetrics fontMetrics = tvSort.getPaint()
                .getFontMetrics();
        int textHeight = (int) (Math.ceil(fontMetrics.descent - fontMetrics.ascent) + 2);
        location[1] += CommonUtil.dp2px(this, 20) + textHeight / 2;
        return location;
    }

    private void onSortChecked(int checkedId, boolean needRefresh) {
        String tag = null;
        ScrollAbleFragment fragment = fragments.get(viewPager.getCurrentItem());
        if (fragment instanceof NoteMarkListFragment) {
            tag = TAG_NOTE;
        } else if (fragment instanceof NoteMarkWorkFragment) {
            tag = TAG_WORK;
        } else if (fragment instanceof NoteMarkProductFragment) {
            tag = TAG_PRODUCT;
        }
        if (TextUtils.isEmpty(tag)) {
            return;
        }

        String sortValueText = null;
        sortCheckedId = checkedId;
        if (checkedId == R.id.cb_default) {
            if (tag.equals(TAG_NOTE)) {
                //笔记
                sort = SORT_NOTE_NEW;
                sortValueText = "最新";
            } else {
                sort = SORT_DEFAULT;
                sortValueText = "综合";
            }
        } else if (checkedId == R.id.cb_new) {
            if (tag.equals(TAG_NOTE)) {
                //笔记
                sort = SORT_NOTE_HOT;
                sortValueText = "最热";
            } else {
                if (tag.equals(TAG_PRODUCT)) {
                    sort = SORT_PRODUCT_NEWS;
                } else {
                    sort = SORT_NEWS;
                }
                sortValueText = "最新";
            }
        } else if (checkedId == R.id.cb_price_high) {
            //价格最高
            sort = SORT_PRICE_LOW;
            sortValueText = "价格最高";
        } else if (checkedId == R.id.cb_price_low) {
            sort = SORT_PRICE_HIGH;
            sortValueText = "价格最低";
        }
        sortMap.put(viewPager.getCurrentItem(), sortCheckedId);
        if (!TextUtils.isEmpty(sortValueText)) {
            tvSort.setText(sortValueText);
        }
        if (needRefresh) {
            sortPopupWindow.dismiss();
            getCurrentFragment().refresh(sort);
        }
    }

    public ScrollAbleFragment getCurrentFragment() {
        return (ScrollAbleFragment) fragmentAdapter.getItem(viewPager.getCurrentItem());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (sortPopupWindow != null && sortPopupWindow.isShowing()) {
            sortPopupWindow.dismiss();
        }
        CommonUtil.unSubscribeSubs(loadSubscriber, followSubscriber);
    }

    @Override
    public void onScroll(int currentY, int maxY) {
        float alpha = currentY * 1.0f / maxY;
        tvTitle.setAlpha(alpha);
    }

    class MarkFragmentAdapter extends FragmentStatePagerAdapter {

        public MarkFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = "笔记";
                    break;
                case 1:
                    title = "套餐";
                    break;
                case 2:
                    title = "婚品";
                    break;
            }
            return title;
        }
    }

}
