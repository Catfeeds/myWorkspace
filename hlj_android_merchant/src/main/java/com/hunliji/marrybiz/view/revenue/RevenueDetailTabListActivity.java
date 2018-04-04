package com.hunliji.marrybiz.view.revenue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.RevenueDetailFragment;
import com.hunliji.marrybiz.model.revenue.RevenueDetail;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 收支明细
 * edited by hua_rong
 */
public class RevenueDetailTabListActivity extends HljBaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.rb_water_detail)
    RadioButton rbWaterDetail;
    @BindView(R.id.rb_order_detail)
    RadioButton rbOrderDetail;

    private RevenueDetailFragment waterDetailFragment;
    private RevenueDetailFragment orderDetailFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_detail_tab_list);
        ButterKnife.bind(this);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter
                (getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        rbWaterDetail.setChecked(false);
                        rbOrderDetail.setChecked(true);
                        break;
                    default:
                        rbWaterDetail.setChecked(true);
                        rbOrderDetail.setChecked(false);
                        break;
                }
            }
        });
        RadioButtonCheckListener radioButtonCheckListener = new RadioButtonCheckListener();
        rbWaterDetail.setOnClickListener(radioButtonCheckListener);
        rbOrderDetail.setOnClickListener(radioButtonCheckListener);
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    if (orderDetailFragment == null) {
                        orderDetailFragment = new RevenueDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(RevenueDetailFragment.ARG_NAME_TYPE,
                                RevenueDetail.TYPE_ORDER_DETAIL);
                        orderDetailFragment.setArguments(bundle);
                    }
                    return orderDetailFragment;
                default:
                    if (waterDetailFragment == null) {
                        waterDetailFragment = new RevenueDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(RevenueDetailFragment.ARG_NAME_TYPE,
                                RevenueDetail.TYPE_WATER_DETAIL);
                        waterDetailFragment.setArguments(bundle);
                    }
                    return waterDetailFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 1:
                    return getString(R.string.label_order_detail).toUpperCase(l);
                default:
                    return getString(R.string.label_water_detail).toUpperCase(l);

            }
        }
    }

    private class RadioButtonCheckListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rb_water_detail:
                    viewPager.setCurrentItem(0, true);
                    rbWaterDetail.setChecked(true);
                    rbOrderDetail.setChecked(false);
                    break;
                case R.id.rb_order_detail:
                    viewPager.setCurrentItem(1, true);
                    rbWaterDetail.setChecked(false);
                    rbOrderDetail.setChecked(true);
                    break;
            }
        }
    }

}
