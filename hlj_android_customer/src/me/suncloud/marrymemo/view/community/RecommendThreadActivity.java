package me.suncloud.marrymemo.view.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.community.HotThreadRankHomeFragment;
import me.suncloud.marrymemo.fragment.community.RecommendThreadFragment;
import me.suncloud.marrymemo.fragment.community.RichThreadListFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;

/**
 * Created by mo_yu on 2017/1/4.看帖页面
 */

public class RecommendThreadActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener {

    private final static int RECOMMEND_THREAD = 0;
    //    private final static int HOT_THREAD_RANK = 1;
    private final static int RICH_THREAD = 1;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private int offset;
    private int totalCount;
    private User user;
    private RecommendThreadFragment recommendThreadFragment;//社区热门
    private HotThreadRankHomeFragment hotThreadRankFragment;//排行榜
    private RichThreadListFragment richThreadListFragment;//推荐（精编话题）

    @Override
    public String pageTrackTagName() {
        return "社区热帖";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_thread);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        offset = getIntent().getIntExtra("offset", 0);
        totalCount = getIntent().getIntExtra("totalCount", 0);
        user = Session.getInstance()
                .getCurrentUser(this);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        indicator.setTabViewId(R.layout.menu_tab_view_short_line___cm);
        indicator.setPagerAdapter(mSectionsPagerAdapter);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case RECOMMEND_THREAD:
                    if (recommendThreadFragment == null) {
                        recommendThreadFragment = RecommendThreadFragment.newInstance(offset,
                                totalCount);
                    }
                    return recommendThreadFragment;
                //                case 1:
                //                    if (hotThreadRankFragment == null) {
                //                        hotThreadRankFragment = HotThreadRankHomeFragment
                // .newInstance();
                //                    }
                //                    return hotThreadRankFragment;
                case RICH_THREAD:
                    if (richThreadListFragment == null) {
                        richThreadListFragment = RichThreadListFragment.newInstance();
                    }
                    return richThreadListFragment;
                default:
                    if (recommendThreadFragment == null) {
                        recommendThreadFragment = RecommendThreadFragment.newInstance(offset,
                                totalCount);
                    }
                    return recommendThreadFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case RECOMMEND_THREAD:
                    return getString(R.string.label_hot_thread_tab).toUpperCase();
                //                case 1:
                //                    return getString(R.string.label_hot_rank_tab).toUpperCase();
                case RICH_THREAD:
                    return getString(R.string.label_hot_rich_tab).toUpperCase();
                default:
                    return getString(R.string.label_hot_thread_tab).toUpperCase();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        User currentUser = Session.getInstance()
                .getCurrentUser(this);
        userRefresh(currentUser);
    }

    /**
     * 退出登录或者切换用户之后，刷新数据
     *
     * @param u
     */
    public void userRefresh(User u) {
        if (u != null) {
            if (user != null) {
                //切换用户
                if (!u.getId()
                        .equals(user.getId())) {
                    user = u;
                    onBackPressed();
                }
            } else {
                //登入
                user = u;
                onBackPressed();
            }
        } else if (user != null) {
            //登出
            user = null;
            onBackPressed();
        }
    }


    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (recommendThreadFragment != null) {
            intent.putExtra("offset", recommendThreadFragment.getOffset());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        setResult(Activity.RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);
    }
}
