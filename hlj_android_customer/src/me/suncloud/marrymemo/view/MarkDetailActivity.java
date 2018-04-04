package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.MarkDetailFragment;
import me.suncloud.marrymemo.fragment.MoreMarkProductFragment;
import me.suncloud.marrymemo.fragment.MoreMarkThreadFragment;
import me.suncloud.marrymemo.fragment.MoreMarkWorkAndCaseFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.MarkHeaderView;

/**
 * 标签详情activity
 * Created by jinxin on 2016/4/19.
 */
public class MarkDetailActivity extends HljBaseNoBarActivity implements MarkHeaderView
        .OnItemClickListener {
    private View progressBar;
    private City city;
    private String markTitle;
    private long markId;
    private List<Integer> mData;
    private boolean isMore;
    private int markType;
    private View actionLayout;
    private TextView title;
    private MarkHeaderView header;
    private View emptyLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_detail);
        actionLayout = findViewById(R.id.action_layout);
        title = (TextView) findViewById(R.id.title);
        header = (MarkHeaderView) findViewById(R.id.header);
        emptyLayout = findViewById(R.id.empty_hint_layout);
        mData = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        markId = getIntent().getLongExtra("markId", 0);
        markTitle = getIntent().getStringExtra("markTitle");
        isMore = getIntent().getBooleanExtra("isMore", false);
        markType = getIntent().getIntExtra("markType", -1);
        city = Session.getInstance()
                .getMyCity(this);
        setDefaultStatusBarPadding();
        refresh();
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    private void refresh() {
        progressBar.setVisibility(View.VISIBLE);
        String url = Constants.getAbsUrl(Constants.HttpPath.GET_MARK_INFO,
                markId,
                city == null ? 0 : city.getId());
        new GetMarkInfoTask().executeOnExecutor(Constants.LISTTHEADPOOL, url);
    }

    private void setEmptyView() {
        if (mData.isEmpty()) {
            actionLayout.setVisibility(View.VISIBLE);
            title.setText(markTitle);
            header.setActivity(this);
            header.setRelativeId(markId);
            header.setOnItemClickListener(this);
            header.setPaddingVisible(true);
            header.setOnDataChangeListener(new MarkHeaderView.OnDataChangeListener() {
                @Override
                public void onDataChanged(
                        ArrayList<JSONObject> data,
                        MarkHeaderView.MarkHeaderAdapter adapter,
                        ViewGroup parent) {
                    if (data != null && data.size() == 0) {
                        header.setContentVisible(false, false);
                    }
                }
            });
            header.setVisibility(View.VISIBLE);
            emptyLayout.findViewById(R.id.img_empty_list_hint)
                    .setVisibility(View.VISIBLE);
            TextView emptyHint = (TextView) emptyLayout.findViewById(R.id.empty_hint);
            if (!JSONUtil.isNetworkConnected(this)) {
                emptyHint.setText(getString(R.string.hint_net_disconnected));
            } else {
                emptyHint.setText(getString(R.string.no_item));
            }
            emptyHint.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setFullView() {
        actionLayout.setVisibility(View.GONE);
        header.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /*
        markId = intent.getLongExtra("markId", 0);
        markTitle = intent.getStringExtra("markTitle");
        isMore = intent.getBooleanExtra("isMore", false);
        markType = intent.getIntExtra("markType", -1);
        refresh();
        */
        super.onNewIntent(intent);
    }

    private void switchFragment(int type) {
        Bundle data = new Bundle();
        data.putLong("markId", markId);
        data.putString("markTitle", markTitle);
        data.putBoolean("isMore", isMore);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        switch (type) {
            case Constants.MARK_TYPE.WORK:
                //套餐
                data.putInt("markType", Constants.MARK_TYPE.WORK);
                fragment = MoreMarkWorkAndCaseFragment.newInstance(data);
                break;
            case Constants.MARK_TYPE.CASE:
                //案例
                data.putInt("markType", Constants.MARK_TYPE.CASE);
                fragment = MoreMarkWorkAndCaseFragment.newInstance(data);
                break;
            case Constants.MARK_TYPE.THREAD:
                //话题
                data.putInt("markType", Constants.MARK_TYPE.THREAD);
                fragment = MoreMarkThreadFragment.newInstance(data);
                break;
            case Constants.MARK_TYPE.PRODUCT:
                //婚品
                data.putInt("markType", Constants.MARK_TYPE.PRODUCT);
                fragment = MoreMarkProductFragment.newInstance(data);
                break;
            default:
                break;
        }

        if (fragment != null) {
            Fragment oldFragment = fragmentManager.findFragmentById(R.id.content);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (oldFragment != null) {
                transaction.remove(oldFragment);
            }
            transaction.add(R.id.content, fragment)
                    .commit();
        }
    }

    private void switchFragment() {
        if (mData.isEmpty()) {
            return;
        }
        Bundle data = new Bundle();
        data.putLong("markId", markId);
        data.putString("markTitle", markTitle);
        data.putBoolean("isMore", isMore);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        if (mData.size() == 1) {
            //非聚合
            int type = mData.get(0);
            switch (type) {
                case 1:
                    fragment = MarkDetailFragment.newInstance(data);
                    break;
                case 2:
                    //套餐
                    data.putInt("markType", Constants.MARK_TYPE.WORK);
                    fragment = MoreMarkWorkAndCaseFragment.newInstance(data);
                    break;
                case 3:
                    //案例
                    data.putInt("markType", Constants.MARK_TYPE.CASE);
                    fragment = MoreMarkWorkAndCaseFragment.newInstance(data);
                    break;
                case 4:
                    //话题
                    data.putInt("markType", Constants.MARK_TYPE.THREAD);
                    fragment = MoreMarkThreadFragment.newInstance(data);
                    break;
                case 5:
                    //婚品
                    data.putInt("markType", Constants.MARK_TYPE.PRODUCT);
                    fragment = MoreMarkProductFragment.newInstance(data);
                    break;
                default:
                    break;
            }
        } else {
            //聚合
            fragment = MarkDetailFragment.newInstance(data);
        }

        if (fragment != null) {
            Fragment oldFragment = fragmentManager.findFragmentById(R.id.content);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (oldFragment != null) {
                transaction.remove(oldFragment);
            }
            transaction.add(R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    public void onItemClick(View view, JSONObject data, int position) {
        if (data != null) {
            setFullView();
            long id = data.optLong("id");
            String markTitle = data.optString("name");
            int type = data.optInt("marked_type");
            Util.markActionActivity(this, type, markTitle, id, false);
        }
    }

    class GetMarkInfoTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            if (isFinishing()) {
                return;
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

            if (object != null) {
                JSONObject data = object.optJSONObject("data");
                if (data != null) {
                    JSONArray exportList = data.optJSONArray("export_list");
                    if (exportList != null) {
                        for (int i = 0; i < exportList.length(); i++) {
                            JSONObject item = exportList.optJSONObject(i);
                            if (item != null) {
                                int type = item.optInt("type", -1);
                                if (type != -1 && !mData.contains(type)) {
                                    mData.add(type);
                                }
                            }
                        }
                    }
                }
            }
            switchFragment();
            setEmptyView();
            super.onPostExecute(object);
        }
    }
}
