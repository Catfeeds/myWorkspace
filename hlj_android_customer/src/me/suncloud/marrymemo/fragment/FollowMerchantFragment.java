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
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.NewMerchant;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * Created by Suncloud on 2016/2/26.
 */
public class FollowMerchantFragment extends RefreshListFragment implements ObjectBindAdapter
        .ViewBinder<NewMerchant>, AdapterView.OnItemClickListener {

    private ArrayList<NewMerchant> merchants;
    private ObjectBindAdapter<NewMerchant> adapter;
    private View progressBar;
    private View rootView;
    private int size;
    private int margin;
    private long userId;
    private int emptyId;

    public static FollowMerchantFragment newInstance(long userId, int emptyId) {
        FollowMerchantFragment fragment = new FollowMerchantFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        args.putInt("emptyId", emptyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        size = Util.dp2px(getActivity(), 90);
        margin = Util.dp2px(getActivity(), 4);
        merchants = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(),
                merchants,
                R.layout.follow_merchant_item,
                this);
        footerView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        if (getArguments() != null) {
            userId = getArguments().getLong("userId", 0);
            emptyId = getArguments().getInt("emptyId", 0);
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
        if (merchants.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            onRefresh(listView);
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewMerchant merchant = (NewMerchant) parent.getAdapter()
                .getItem(position);
        if (merchant != null) {
            Intent intent;
            if (merchant.getShopType() == 1) {
                intent = new Intent(getActivity(), ProductMerchantActivity.class);
            } else {
                intent = new Intent(getActivity(), MerchantDetailActivity.class);
            }
            intent.putExtra("id", merchant.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }

    }

    @Override
    public void onDataLoad(int page) {
        new GetMerchantsTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(page));
    }

    private String getUrl(int page) {
        return Constants.getAbsUrl(String.format(Constants.HttpPath.FAVORITES_MERCHANTS_URL,
                page,
                userId));
    }

    @Override
    public void setViewValue(View view, NewMerchant merchant, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        if (merchant.getShopType() == 0) {
            holder.tvProperty.setText(merchant.getPropertyName());
            holder.tvCasesCount.setVisibility(View.VISIBLE);
            holder.tvWorksCount.setText(getString(R.string.label_work_count,
                    merchant.getActiveWorkCount()));
            holder.tvCasesCount.setText(getString(R.string.label_case_count,
                    merchant.getActiveCaseCount()));
        } else {
            holder.tvProperty.setText(R.string.label_product2);
            holder.tvCasesCount.setVisibility(View.GONE);
            holder.tvWorksCount.setText(getString(R.string.label_product_count,
                    merchant.getWorkCount()));
        }
        holder.tvFansCount.setText(getString(R.string.merchant_collect_count,
                merchant.getFansCount()));
        int width = Math.round(holder.tvProperty.getPaint()
                .measureText(holder.tvProperty.getText()
                        .toString()) + holder.tvProperty.getPaddingLeft() + holder.tvProperty
                .getPaddingRight() + margin);
        holder.tvName.setText(merchant.getName());
        holder.tvName.setPadding(0, 0, width, 0);
        String url = JSONUtil.getAvatar(merchant.getLogoPath(), size);
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
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_works_count)
        TextView tvWorksCount;
        @BindView(R.id.tv_cases_count)
        TextView tvCasesCount;
        @BindView(R.id.tv_fans_count)
        TextView tvFansCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class GetMerchantsTask extends AsyncTask<Object, Integer, JSONObject> {
        String url;

        private GetMerchantsTask() {
            isLoad = true;
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
                    int pageCount = jsonObject.optInt("total_page", 0);
                    onLoadCompleted(pageCount <= currentPage);
                    if (onTabTextChangeListener != null) {
                        int totalCount = jsonObject.optInt("total_count", 0);
                        onTabTextChangeListener.onTabTextChange(totalCount);
                    }
                    JSONArray array = jsonObject.optJSONArray("merchants");
                    if (currentPage == 1) {
                        merchants.clear();
                    }
                    if (array != null && array.length() > 0) {
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            merchants.add(new NewMerchant(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else if (!merchants.isEmpty()) {
                    onLoadFailed();
                }
                if (merchants.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = rootView.findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    Util.setEmptyView(getActivity(),
                            emptyView,
                            emptyId == 0 ? R.string.hint_collect_merchant_empty : emptyId,
                            R.mipmap.icon_empty_common,
                            0,
                            0);
                }
            }
            super.onPostExecute(jsonObject);
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            url = (String) params[0];
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
    }
}
