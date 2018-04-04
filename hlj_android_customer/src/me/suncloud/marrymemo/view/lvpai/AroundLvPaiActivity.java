package me.suncloud.marrymemo.view.lvpai;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.work_case.AroundCpmWorkFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.CityListActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Subscription;

public class AroundLvPaiActivity extends HljBaseNoBarActivity implements RadioGroup
        .OnCheckedChangeListener {

    @Override
    public String getFragmentPageTrackTagName() {
        return "微旅拍";
    }

    @BindView(R.id.img_bg)
    ImageView imgBg;
    @BindView(R.id.img_msg_w)
    ImageButton imgMsg;
    @BindView(R.id.msg_notice_view_w)
    View msgNoticeView;
    @BindView(R.id.tv_msg_count_w)
    TextView tvMsgCount;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.rb_day_selected)
    RadioButton rbDaySelected;
    @BindView(R.id.rb_around)
    RadioButton rbAround;
    @BindView(R.id.rb_faraway)
    RadioButton rbFaraway;
    @BindView(R.id.rg_menu)
    RadioGroup rgMenu;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.msg_layout_holder)
    RelativeLayout msgLayoutHolder;

    private int oldCheckedId;

    protected SparseArray<ScrollAbleFragment> fragments;
    private AroundCpmFragmentAdapter fragmentAdapter;

    private City city;

    private NoticeUtil noticeUtil;
    private int type;
    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_cpm);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(rbAround)
                .tagName("short_tour")
                .hitTag();
        HljVTTagger.buildTagger(rbDaySelected)
                .tagName("daily_choice")
                .hitTag();
        HljVTTagger.buildTagger(rbFaraway)
                .tagName("long_tour")
                .hitTag();
    }

    private void initValues() {
        city = Session.getInstance()
                .getMyCity(this);
        fragmentAdapter = new AroundCpmFragmentAdapter(getSupportFragmentManager());
        registerRxBusEvent();
    }

    private void initViews() {
        setActionBarPadding(appBar, msgLayoutHolder);
        rgMenu.setOnCheckedChangeListener(this);
        appBar.getLayoutParams().height = CommonUtil.dp2px(this, 110) + getStatusBarHeight();
        setSupportActionBar(toolbar);
        toolbarLayout.setCollapsedTitleGravity(Gravity.CENTER_HORIZONTAL);

        setTitle(city.getName() + "出发");

        rbDaySelected.setChecked(true);
        oldCheckedId = R.id.rb_day_selected;

        noticeUtil = new NoticeUtil(this, tvMsgCount, msgNoticeView);
        noticeUtil.onResume();

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ScrollAbleFragment fragment = (ScrollAbleFragment) fragmentAdapter.getItem
                        (viewPager.getCurrentItem());
                PullToRefreshVerticalRecyclerView ptrView = (PullToRefreshVerticalRecyclerView)
                        fragment.getScrollableView();
                if (ptrView != null) {
                    ptrView.setMode(verticalOffset == 0 ? PullToRefreshBase.Mode.PULL_FROM_START
                            : PullToRefreshBase.Mode.DISABLED);
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rgMenu.check(R.id.rb_day_selected);
                        break;
                    case 1:
                        rgMenu.check(R.id.rb_around);
                        break;
                    case 2:
                        rgMenu.check(R.id.rb_faraway);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initLoad() {
        viewPager.setAdapter(fragmentAdapter);
    }

    private void setCheckedTab() {
        int position = 0;
        switch (oldCheckedId) {
            case R.id.rb_day_selected:
                position = 0;
                break;
            case R.id.rb_around:
                position = 1;
                break;
            case R.id.rb_faraway:
                position = 2;
                break;
        }
        viewPager.setCurrentItem(position);
    }

    private void refresh() {
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i)
                    .refresh();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (oldCheckedId == checkedId) {
            return;
        }
        HljViewTracker.fireViewClickEvent(group.findViewById(checkedId));
        switch (checkedId) {
            case R.id.rb_day_selected:
                type = AroundCpmWorkFragment.TRAVEL_TYPE_FINE;
                break;
            case R.id.rb_around:
                type = AroundCpmWorkFragment.TRAVEL_TYPE_AROUND;
                break;
            case R.id.rb_faraway:
                type = AroundCpmWorkFragment.TRAVEL_TYPE_FARAWAY;
                break;
        }

        if (type == AroundCpmWorkFragment.TRAVEL_TYPE_AROUND && (city == null || city.getId() ==
                0)) {
            showCityDlg();
            rgMenu.check(oldCheckedId);
        } else {
            oldCheckedId = checkedId;
            setCheckedTab();
        }
    }

    private void showCityDlg() {
        Toast toast = Toast.makeText(this, "请先选择城市，享更精准推荐", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Intent intent = new Intent(this, CityListActivity.class);
        intent.putExtra(CityListActivity.ARG_NON_NULL, true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up_to_top, R.anim.activity_anim_default);
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        CommonUtil.unSubscribeSubs(rxBusEventSub);
        rxBusEventSub = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .subscribe(new RxBusSubscriber<RxEvent>() {
                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        switch (rxEvent.getType()) {
                            case CITY_CHANGE:
                                city = Session.getInstance()
                                        .getMyCity(AroundLvPaiActivity.this);
                                toolbarLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        toolbarLayout.setTitle(city.getName() + "出发");
                                    }
                                });
                                oldCheckedId = R.id.rb_around;
                                setCheckedTab();
                                refresh();
                                break;
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(rxBusEventSub);
        super.onFinish();
    }

    @OnClick(R.id.img_msg_w)
    void onMsgLayout() {
        if (Util.loginBindChecked(this)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
        }
    }

    private class AroundCpmFragmentAdapter extends FragmentStatePagerAdapter {

        public AroundCpmFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (fragments == null) {
                fragments = new SparseArray<>();
            }
            switch (position) {
                case 1:
                    if (fragments.get(position) == null) {
                        fragments.put(position,
                                AroundCpmWorkFragment.newInstance(AroundCpmWorkFragment
                                        .TRAVEL_TYPE_AROUND));
                    }
                    break;
                case 2:
                    if (fragments.get(position) == null) {
                        fragments.put(position,
                                AroundCpmWorkFragment.newInstance(AroundCpmWorkFragment
                                        .TRAVEL_TYPE_FARAWAY));
                    }
                    break;
                default:
                    if (fragments.get(position) == null) {
                        fragments.put(position,
                                AroundCpmWorkFragment.newInstance(AroundCpmWorkFragment
                                        .TRAVEL_TYPE_FINE));
                    }
                    break;
            }

            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "每日精选";
                case 1:
                    return "短途周边";
                case 2:
                    return "去远方";
            }
            return "";
        }
    }
}
