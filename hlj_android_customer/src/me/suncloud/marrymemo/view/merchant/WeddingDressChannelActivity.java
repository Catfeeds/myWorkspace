package me.suncloud.marrymemo.view.merchant;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.product.WeddingDressProductHomeFragment;
import me.suncloud.marrymemo.fragment.work_case.WeddingDressWorkListFragment;

/**
 * 婚纱礼服频道页首页
 * Created by mo_yu on 2017/8/3.
 */
public class WeddingDressChannelActivity extends BaseMerchantServiceChannelActivity {

    private int[] icons = {R.drawable.sl_ic_local_buy, R.drawable.sl_ic_local_rent, R.drawable
            .sl_ic_optimization};
    private String[] titles = {"本地购买", "本地租赁", "全国优选"};
    private static final int SALE_WAY_BUY = 228;
    private static final int SALE_WAY_RENTAL = 227;
    public static final int PRODUCT_TYPE = 2;//全国优选

    @Override
    public String pageTrackTagName() {
        return "婚纱礼服二级页";
    }

    @Override
    public void initValues() {
        super.initValues();
        tabEnum = TabEnum.ICON;
        for (int i = 0; i < titles.length; i++) {
            CategoryMark categoryMark = new CategoryMark();
            Mark mark = new Mark();
            mark.setName(titles[i]);
            mark.setImagePath(String.valueOf(icons[i]));
            categoryMark.setMark(mark);
            categoryMarks.add(categoryMark);
        }
    }

    @Override
    public void initViews() {
        super.initViews();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                if (viewPager.getCurrentItem() == PRODUCT_TYPE) {
                    WeddingDressProductHomeFragment weddingDressProductHomeFragment =
                            (WeddingDressProductHomeFragment) fragments.get(
                            viewPager.getCurrentItem());
                    if (weddingDressProductHomeFragment != null) {
                        refreshBottomFilterView(weddingDressProductHomeFragment.getFilterView());
                    }
                } else if (serviceWorkFilterViewHolder != null) {
                    refreshBottomFilterView(serviceWorkFilterViewHolder.getRootView());
                }
            }
        });
    }

    @Override
    public void setBottomView() {
        if (viewPager.getCurrentItem() == PRODUCT_TYPE) {
            WeddingDressProductHomeFragment weddingDressProductHomeFragment =
                    (WeddingDressProductHomeFragment) fragments.get(
                    viewPager.getCurrentItem());
            View filterView = weddingDressProductHomeFragment.getFilterView();
            if (filterView != null) {
                serviceFilterBottomLayout.removeAllViews();
                serviceFilterBottomLayout.addView(filterView);
            }
        } else {
            super.setBottomView();
        }
    }

    public void refreshBottomFilterView(View filterView) {
        if (filterView != null) {
            serviceFilterBottomLayout.removeAllViews();
            serviceFilterBottomLayout.addView(filterView);
        }
    }

    @Override
    public ScrollAbleFragment getFragment(int position) {
        switch (position) {
            case 0:
                return WeddingDressWorkListFragment.newInstance(getPropertyId(), SALE_WAY_BUY);
            case 1:
                return WeddingDressWorkListFragment.newInstance(getPropertyId(), SALE_WAY_RENTAL);
            default:
                return WeddingDressProductHomeFragment.newInstance(1);
        }
    }

    @Override
    public long getPropertyId() {
        return Merchant.PROPERTY_WEDDING_DRESS;
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == PRODUCT_TYPE) {
            if (fragments != null && fragments.size() > 0) {
                Fragment fragment = fragments.get(viewPager.getCurrentItem());
                if (fragment != null && fragment instanceof WeddingDressProductHomeFragment && (
                        (WeddingDressProductHomeFragment) fragment).isShowFilterView()) {
                    ((WeddingDressProductHomeFragment) fragment).hideFilterView();
                    return;
                }
            }
        }
        super.onBackPressed();
    }
}
