package me.suncloud.marrymemo.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.widget.TabPageIndicator;

/**
 * Created by Suncloud on 2016/2/26.
 */
public class FollowUserFragment extends RefreshListFragment implements ObjectBindAdapter
        .ViewBinder<User>, AdapterView.OnItemClickListener {

    private ArrayList<User> users;
    private ObjectBindAdapter<User> adapter;
    private View progressBar;
    private View rootView;
    private int size;
    private DateFormat weddingDateFormat;
    private long userId;
    private int emptyId;
    private int type;

    public static FollowUserFragment newInstance(long userId, int emptyId, int type) {
        FollowUserFragment fragment = new FollowUserFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        args.putInt("emptyId", emptyId);
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        size = Util.dp2px(getActivity(), 90);
        users = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(), users, R.layout.follow_user_item, this);
        footerView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        if (getArguments() != null) {
            userId = getArguments().getLong("userId", 0);
            emptyId = getArguments().getInt("emptyId", 0);
            type = getArguments().getInt("type", 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        progressBar = rootView.findViewById(R.id.progressBar);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        if (users.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            onRefresh(listView);
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = (User) parent.getAdapter()
                .getItem(position);
        if (user != null) {
            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            intent.putExtra("id", user.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }

    }

    @Override
    public void onDataLoad(int page) {
        new GetUsersTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(page));
    }

    private String getUrl(int page) {
        if (type > 0) {
            return Constants.getAbsUrl(String.format(Constants.HttpPath.GET_USER_FANS,
                    page,
                    userId));
        }
        return Constants.getAbsUrl(String.format(Constants.HttpPath.GET_USER_FOLLOWS,
                page,
                userId));
    }

    @Override
    public void setViewValue(View view, User user, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.tvName.setText(user.getNick());
        holder.tvFansCount.setText(getString(R.string.label_fans_count2, user.getFanCount()));
        if (user.getWeddingDay() != null) {
            if (weddingDateFormat == null) {
                weddingDateFormat = new SimpleDateFormat(getString(R.string
                        .format_date_wedding_time2),
                        Locale.getDefault());
            }
            holder.tvWeddingTime.setText(weddingDateFormat.format(user.getWeddingDay()));
        } else {
            holder.tvWeddingTime.setText(R.string.label_wedding_time_none);
        }
        String url = user.getAvatar(size);
        if (!JSONUtil.isEmpty(url)) {
            holder.imgAvatar.setTag(url);
            ImageLoadTask task = new ImageLoadTask(holder.imgAvatar, null, 0);
            AsyncBitmapDrawable image = new AsyncBitmapDrawable(getResources(),
                    R.mipmap.icon_avatar_primary,
                    task);
            task.loadImage(url, size, ScaleMode.WIDTH, image);
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.icon_avatar_primary);
        }
    }

    static class ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_wedding_time)
        TextView tvWeddingTime;
        @BindView(R.id.tv_fans_count)
        TextView tvFansCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class GetUsersTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        private GetUsersTask() {
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
        protected void onPostExecute(JSONObject jsonObject) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (url.equals(getUrl(currentPage))) {
                isLoad = false;
                listView.onRefreshComplete();
                progressBar.setVisibility(View.GONE);
                if (jsonObject != null) {
                    int pageCount = jsonObject.optInt("page_count", 0);
                    onLoadCompleted(pageCount <= currentPage);
                    if (onTabTextChangeListener != null) {
                        int totalCount = jsonObject.optInt("total_count", 0);
                        onTabTextChangeListener.onTabTextChange(totalCount);
                    }
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        users.clear();
                    }
                    if (array != null && array.length() > 0) {
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            users.add(new User(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else if (!users.isEmpty()) {
                    onLoadFailed();
                }
                if (users.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = rootView.findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    Util.setEmptyView(getActivity(),
                            emptyView,
                            emptyId != 0 ? emptyId : R.string.hint_empty_followd,
                            R.mipmap.icon_empty_common,
                            0,
                            0);

                }
            }
            super.onPostExecute(jsonObject);
        }
    }
}
