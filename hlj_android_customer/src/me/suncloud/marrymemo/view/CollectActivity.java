package me.suncloud.marrymemo.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.views.fragments.CollectNoteInspirationListFragment;
import com.hunliji.hljquestionanswer.fragments.MyFollowAnswersFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.RankListAdapter;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.fragment.CollectProductFragment;
import me.suncloud.marrymemo.fragment.CollectWorkCaseFragment;
import me.suncloud.marrymemo.fragment.UserThreadFragment;
import me.suncloud.marrymemo.fragment.finder.CollectSubPageFragment;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.user.CountStatistics;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.PropertiesUtil;

/**
 * Created by Suncloud on 2016/2/24.
 */
@Route(path = RouterPath.IntentPath.Customer.COLLECT)
public class CollectActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener, AdapterView.OnItemClickListener {

    @BindView(R.id.btn_filter)
    Button btnFilter;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.filter_list)
    ListView filterList;
    @BindView(R.id.menu_bg)
    RelativeLayout menuBg;

    private SparseArray<RefreshFragment> fragments;
    private ArrayList<Label> properties;
    private long filterId;

    private HljHttpSubscriber getStatisticsSub;

    public final static int TAB_WORK = 0; //套餐tab
    public final static int TAB_PRODUCT = 1;//婚品tab
    public final static int TAB_CASE = 2; //案例tab
    public final static int TAB_NOTE_MEDIA = 3; //灵感tab
    public final static int TAB_THREAD = 4; //话题tab
    public final static int TAB_ANSWER = 5; //问答tab
    public final static int TAB_SUB_PAGE = 6;//专栏tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragments = new SparseArray<>();
        properties = new ArrayList<>();
        Label menuItem = new Label();
        menuItem.setName(getString(R.string.label_all_kind));
        properties.add(menuItem);
        properties.addAll(PropertiesUtil.getServerPropertiesFromFile(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        btnFilter.setVisibility(View.VISIBLE);
        filterList.setOnItemClickListener(this);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                btnFilter.setVisibility(position == TAB_WORK || position == TAB_CASE ? View
                        .VISIBLE : View.GONE);
                if (menuBg.getVisibility() == View.VISIBLE) {
                    AnimUtil.hideMenu2Animation(menuBg, filterList);
                }
                indicator.setCurrentItem(position);
            }
        });
        indicator.setTabViewId(R.layout.menu_tab_view_short_line___cm);
        indicator.setOnTabChangeListener(this);
        indicator.setPagerAdapter(pagerAdapter);
        new PropertiesUtil.PropertiesSyncTask(1, this, new PropertiesUtil.OnFinishedListener() {
            @Override
            public void onFinish(ArrayList<MerchantProperty> p) {
                properties = new ArrayList<>();
                Label menuItem = new Label();
                menuItem.setName(getString(R.string.label_all_kind));
                properties.add(menuItem);
                properties.addAll(p);
            }
        }).execute();
        getCountStatistics();
    }

    private void getCountStatistics() {
        if (getStatisticsSub == null || getStatisticsSub.isUnsubscribed()) {
            getStatisticsSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<CountStatistics>() {
                        @Override
                        public void onNext(CountStatistics countStatistics) {
                            setTabText(TAB_WORK, countStatistics.getPackageCount());
                            setTabText(TAB_PRODUCT, countStatistics.getProductCount());
                            setTabText(TAB_CASE, countStatistics.getExampleCount());
                            setTabText(TAB_NOTE_MEDIA, countStatistics.getNoteMediaCount());
                            setTabText(TAB_THREAD, countStatistics.getThreadCount());
                            setTabText(TAB_ANSWER, countStatistics.getAnswerCount());
                            setTabText(TAB_SUB_PAGE, countStatistics.getSubPageCount());
                        }
                    })
                    .build();
            UserApi.getCountStatisticsObb(CountStatistics.TYPE_COLLECTION)
                    .subscribe(getStatisticsSub);
        }
    }

    public void onBackPressed(View v) {
        onBackPressed();
    }

    @OnClick({R.id.btn_filter, R.id.menu_bg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_filter:
                if (properties.isEmpty()) {
                    return;
                }
                if (filterList.getAdapter() == null) {
                    RankListAdapter rankListAdapter = new RankListAdapter(this, properties);
                    filterList.setAdapter(rankListAdapter);
                }
                if (menuBg.getVisibility() == View.VISIBLE) {
                    AnimUtil.hideMenu2Animation(menuBg, filterList);
                } else {
                    AnimUtil.showMenu2Animation(menuBg, filterList);
                }
                break;
            case R.id.menu_bg:
                if (menuBg.getVisibility() == View.VISIBLE) {
                    AnimUtil.hideMenu2Animation(menuBg, filterList);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Label menuItem = (Label) parent.getAdapter()
                .getItem(position);
        AnimUtil.hideMenu2Animation(menuBg, filterList);
        if (menuItem != null) {
            filterId = menuItem.getId();
            CollectWorkCaseFragment workFragment = (CollectWorkCaseFragment) fragments.get
                    (TAB_WORK);
            CollectWorkCaseFragment caseFragment = (CollectWorkCaseFragment) fragments.get
                    (TAB_CASE);
            if (workFragment != null) {
                workFragment.refresh(filterId);
            }
            if (caseFragment != null) {
                caseFragment.refresh(filterId);
            }
            ((RankListAdapter) parent.getAdapter()).setSelectedItem(position);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            RefreshFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case TAB_WORK:
                    fragment = CollectWorkCaseFragment.newInstance(Work.COMMODITY_TYPE_WORK,
                            filterId);
                    break;
                case TAB_PRODUCT:
                    fragment = CollectProductFragment.newInstance();
                    break;
                case TAB_CASE:
                    fragment = CollectWorkCaseFragment.newInstance(Work.COMMODITY_TYPE_CASE,
                            filterId);
                    break;
                case TAB_NOTE_MEDIA:
                    fragment = CollectNoteInspirationListFragment.newInstance();
                    break;
                case TAB_THREAD:
                    fragment = UserThreadFragment.newInstance(UserThreadFragment
                            .TYPE_COLLECT_THREAD);
                    break;
                case TAB_ANSWER:
                    fragment = MyFollowAnswersFragment.newInstance();
                    break;
                case TAB_SUB_PAGE:
                    fragment = CollectSubPageFragment.newInstance();
                    break;
            }
            if (fragment != null) {
                fragment.setOnTabTextChangeListener(new OnTabTextChangeListener() {
                    @Override
                    public void onTabTextChange(int totalCount) {
                        setTabText(position, totalCount);
                    }
                });
            }
            fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPageTitleStr(position, 0);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private String getPageTitleStr(int position, int totalCount) {
        switch (position) {
            case TAB_WORK:
                return getString(R.string.label_collect_work_count, totalCount);
            case TAB_PRODUCT:
                return getString(R.string.label_collect_product_count, totalCount);
            case TAB_CASE:
                return getString(R.string.label_collect_case_count, totalCount);
            case TAB_NOTE_MEDIA:
                return getString(R.string.label_collect_note_media_count, totalCount);
            case TAB_THREAD:
                return getString(R.string.label_collect_thread_count, totalCount);
            case TAB_ANSWER:
                return getString(R.string.label_collect_answer_count, totalCount);
            case TAB_SUB_PAGE:
                return getString(R.string.label_collect_sub_page_count, totalCount);
            default:
                return null;
        }
    }

    public void setTabText(int position, int totalCount) {
        indicator.setTabText(getPageTitleStr(position, totalCount), position);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(getStatisticsSub);
    }
}
