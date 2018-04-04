package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Story;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;

public class StoryListActivity extends BaseStoryListActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    private RadioGroup rankMenu;
    private View menuBg;
    private String sort;
    private View progressBar;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sort ="pop";
        footerView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_list);
        userId = getIntent().getLongExtra("userId", 0);
        if (userId > 0) {
            findViewById(R.id.rank).setVisibility(View.GONE);
            findViewById(R.id.my_story).setVisibility(View.GONE);
        }
        progressBar = findViewById(R.id.progressBar);
        menuBg = findViewById(R.id.menu_bg);
        rankMenu = (RadioGroup) findViewById(R.id.rank_menu);
        findViewById(R.id.rank).setOnClickListener(this);
        findViewById(R.id.my_story).setOnClickListener(this);
        menuBg.setOnClickListener(this);
        rankMenu.setOnCheckedChangeListener(this);
        progressBar.setVisibility(View.VISIBLE);
        onRefresh(listView);
    }

    @Override
    public void onDataLoad(int page) {
        new GetStoriesTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(page));
    }

    private String getUrl(int page) {
        if (userId > 0) {
            return Constants.getAbsUrl(String.format(Constants.HttpPath.USER_STORIES_URL, userId));
        }
        return Constants.getAbsUrl(String.format(Constants.HttpPath.STORIES_URL + Constants.PAGE_COUNT, JSONUtil.isEmpty(sort)?"pop": sort,
                page));
    }

    public void onBackPressed(View v) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_bg:
                if (rankMenu.getVisibility() == View.VISIBLE) {
                    AnimUtil.hideMenu2Animation(menuBg, rankMenu);
                }
                break;
            case R.id.rank:
                if (rankMenu.getVisibility() == View.VISIBLE) {
                    AnimUtil.hideMenu2Animation(menuBg, rankMenu);
                } else {
                    AnimUtil.showMenu2Animation(menuBg, rankMenu);
                }
                break;
            case R.id.my_story:
                if (Util.loginBindChecked(StoryListActivity.this, Constants.Login.MY_STORY_LOGIN)) {
                    Intent intent = new Intent(this, MyStoryListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        AnimUtil.hideMenu2Animation(menuBg, rankMenu);
        switch (checkedId) {
            case R.id.label_default:
                sort = "pop";
                break;
            case R.id.label_like_most:
                sort = "story_collects_count";
                break;
            case R.id.label_new:
                sort = "created_at";
                break;
        }
        progressBar.setVisibility(View.VISIBLE);
        refresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && requestCode == Constants.Login.MY_STORY_LOGIN) {
            User user = Session.getInstance().getCurrentUser(this);
            if (user != null && user.getId() != 0) {
                Intent intent = new Intent(this, MyStoryListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class GetStoriesTask extends AsyncTask<String, Object, JSONObject> {
        String url;

        private GetStoriesTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (url.equals(getUrl(currentPage))) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (result != null) {
                    if (currentPage == 1) {
                        stories.clear();
                    }
                    int count=result.optInt("page_count");
                    JSONArray array=result.optJSONArray("list");
                    if (array!=null&&array.length()>0) {
                        for (int i = 0,size=array.length(); i < size; i++) {
                            stories.add(new Story(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    onLoadCompleted(userId > 0 || count<=currentPage);
                } else if (!stories.isEmpty()) {
                    onLoadFailed();
                }
                if (stories.isEmpty()) {
                    View emptyView = listView.getRefreshableView().getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView().setEmptyView(emptyView);
                    }
                    Util.setEmptyView(StoryListActivity.this, emptyView, R.string.no_item, R.drawable
                            .icon_common_empty, 0, 0);

                }
            }
            super.onPostExecute(result);
        }
    }
}
