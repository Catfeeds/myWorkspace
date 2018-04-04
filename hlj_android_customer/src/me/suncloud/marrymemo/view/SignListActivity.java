package me.suncloud.marrymemo.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.SignListFragment;
import me.suncloud.marrymemo.model.Sign;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;

public class SignListActivity extends HljBaseNoBarActivity {

    private View progressBar;
    private View emptyLayout;
    private ArrayList<Sign> agreeSigns;
    private ArrayList<Sign> undeterminedSigns;
    private ArrayList<Sign> refuseSigns;
    private TabPageIndicator menu;
    private ViewPager pager;
    private SignListFragment agreeSignListFragment;
    private SignListFragment undeterminedSignListFragment;
    private SignListFragment refuseSignListFragment;

    private int agreeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_list);
        setDefaultStatusBarPadding();
        progressBar = findViewById(R.id.progressBar);
        emptyLayout = findViewById(R.id.empty_hint_layout);
        agreeSigns = new ArrayList<>();
        undeterminedSigns = new ArrayList<>();
        refuseSigns = new ArrayList<>();
        menu = (TabPageIndicator) findViewById(R.id.menu);
        pager = (ViewPager) findViewById(R.id.sign_pager);
        menu.setOnTabChangeListener(new TabPageIndicator.OnTabChangeListener() {
            @Override
            public void onTabChanged(int position) {
                pager.setCurrentItem(position);
            }
        });
        menu.setTabViewId(R.layout.menu_tab_view___cm);
        pager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                menu.setCurrentItem(position);
            }
        });
        User user = Session.getInstance()
                .getCurrentUser(this);
        new SignListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.NEW_CARD_REPLIES_LIST,
                        user.getId())),
                Constants.getAbsUrl(Constants.HttpPath.CARD_V2_REPLY_LIST));
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }

    private class SignListTask extends AsyncTask<String, Object, ArrayList<Sign>> {

        @Override
        protected ArrayList<Sign> doInBackground(String... params) {
            ArrayList<Sign> signs = null;
            try {
                String jsonStr = JSONUtil.getStringFromUrl(params[0]);
                if (!JSONUtil.isEmpty(jsonStr)) {
                    JSONArray array = new JSONObject(jsonStr).optJSONArray("data");
                    if (array != null) {
                        signs = new ArrayList<>();
                        if (array.length() > 0) {
                            int size = array.length();
                            for (int i = 0; i < size; i++) {
                                Sign sign = new Sign(array.optJSONObject(i));
                                signs.add(sign);
                            }
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            try {
                String jsonStr = JSONUtil.getStringFromUrl(params[1]);
                if (!JSONUtil.isEmpty(jsonStr)) {
                    JSONArray array = new JSONObject(jsonStr).optJSONArray("data");
                    if (array != null) {
                        if (signs == null) {
                            signs = new ArrayList<>();
                        }
                        if (array.length() > 0) {
                            int size = array.length();
                            for (int i = 0; i < size; i++) {
                                Sign sign = new Sign(array.optJSONObject(i));
                                sign.setV2(true);
                                signs.add(sign);
                            }
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return signs;
        }

        @Override
        protected void onPostExecute(ArrayList<Sign> result) {
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                menu.setVisibility(View.VISIBLE);
                agreeSigns.clear();
                undeterminedSigns.clear();
                refuseSigns.clear();
                agreeCount = 0;
                for (Sign sign : result) {
                    switch (sign.getState()) {
                        case 0:
                            agreeCount += Math.max(1, sign.getCount());
                            agreeSigns.add(sign);
                            break;
                        case 1:
                            undeterminedSigns.add(sign);
                            break;
                        case 2:
                            refuseSigns.add(sign);
                            break;
                        default:
                            break;
                    }
                }
                SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager
                        ());
                pager.setAdapter(adapter);
                menu.setPagerAdapter(adapter);
            } else {
                Util.setEmptyView(SignListActivity.this,
                        emptyLayout,
                        R.string.net_disconnected,
                        R.mipmap.icon_no_network,
                        0,
                        0);
            }
            super.onPostExecute(result);
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (agreeSignListFragment == null) {
                        agreeSignListFragment = new SignListFragment();
                        if (!agreeSigns.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("signs", agreeSigns);
                            agreeSignListFragment.setArguments(bundle);
                        }
                    }
                    return agreeSignListFragment;
                case 1:
                    if (undeterminedSignListFragment == null) {
                        undeterminedSignListFragment = new SignListFragment();
                        if (!undeterminedSigns.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("signs", undeterminedSigns);
                            undeterminedSignListFragment.setArguments(bundle);
                        }
                    }
                    return undeterminedSignListFragment;
                default:
                    if (refuseSignListFragment == null) {
                        refuseSignListFragment = new SignListFragment();
                        if (!refuseSigns.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("signs", refuseSigns);
                            refuseSignListFragment.setArguments(bundle);
                        }
                    }
                    return refuseSignListFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_sign_state1_count,
                            agreeCount + "").toUpperCase();
                case 1:
                    return getString(R.string.label_sign_state2_count,
                            undeterminedSigns.size() + "").toUpperCase();
                default:
                    return getString(R.string.label_sign_state3_count,
                            refuseSigns.size() + "").toUpperCase();
            }
        }
    }
}
