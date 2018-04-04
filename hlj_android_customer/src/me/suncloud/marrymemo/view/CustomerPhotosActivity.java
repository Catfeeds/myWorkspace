package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
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
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;


public class CustomerPhotosActivity extends HljBaseActivity implements ObjectBindAdapter

        .ViewBinder<Work>, AbsListView.OnScrollListener, AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener<ListView> {

    @BindView(R.id.list)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private int imgWidth;
    private int imgHeight;
    private int iconSize;
    private ArrayList<Work> works;
    private ObjectBindAdapter<Work> adapter;
    private int currentPage;
    private boolean isEnd;
    private boolean onLoad;
    private View loadView;
    private View endView;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_photos);
        ButterKnife.bind(this);

        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imgWidth = Math.round(point.x - 20 * dm.density);
        imgHeight = Math.round(imgWidth * 63 / 100);
        iconSize = Math.round(dm.density * 30);
        if (imgWidth > 805) {
            imgWidth = Math.round(point.x * 3 / 4);
        }
        City city = (City) getIntent().getSerializableExtra("city");
        if (city == null) {
            city = Session.getInstance()
                    .getMyCity(this);
        }
        long propertyId = getIntent().getLongExtra("property_id", 2);

        works = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, works, R.layout.customer_photo_item, this);
        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        loadView = footerView.findViewById(R.id.loading);
        endView = footerView.findViewById(R.id.no_more_hint);

        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);

        currentUrl = Constants.getAbsUrl(String.format(Constants.HttpPath.SECONDARY_PAGE_LIST_URL,
                6,
                propertyId,
                city.getId())) + Constants.PAGE_COUNT;
        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                String.format(currentUrl, currentPage));
    }

    @Override
    public void setViewValue(View view, final Work work, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder(view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.workCover
                    .getLayoutParams();
            params.height = imgHeight;
            params = (ViewGroup.MarginLayoutParams) holder.transLayout.getLayoutParams();
            params.height = imgHeight;
            params = (ViewGroup.MarginLayoutParams) holder.merchantLogo.getLayoutParams();
            params.width = iconSize;
            params.height = iconSize;

            view.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) view.getTag();

        String path = JSONUtil.getImagePath(work.getCoverPath(), imgWidth);
        if (!JSONUtil.isEmpty(path)) {
            if (!path.equals(holder.workCover.getTag())) {
                holder.workCover.setTag(path);
                ImageLoadTask task = new ImageLoadTask(holder.workCover);
                task.loadImage(path,
                        imgWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
        } else {
            holder.workCover.setImageBitmap(null);
            holder.workCover.setTag(null);
        }
        String path2 = JSONUtil.getImagePath2(work.getMerchant()
                .getLogoPath(), iconSize);

        if (!JSONUtil.isEmpty(path2)) {
            if (!path2.equals(holder.merchantLogo.getTag())) {
                holder.merchantLogo.setTag(path2);
                ImageLoadTask task = new ImageLoadTask(holder.merchantLogo, 0);
                task.loadImage(path2,
                        iconSize,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
        } else {
            holder.merchantLogo.setImageBitmap(null);
            holder.merchantLogo.setTag(null);
        }

        holder.merchantName.setText(work.getMerchant()
                .getName());
        holder.title.setText(work.getTitle());
        holder.tvPhotosCount.setText(getString(R.string.label_photos_count,
                work.getMediaItemsCount()));
        holder.tvWatchCount.setText(work.getWatchCount());
        holder.merchantLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerPhotosActivity.this, MerchantDetailActivity.class);
                intent.putExtra("id",
                        work.getMerchant()
                                .getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !onLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(currentUrl, currentPage));
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Work work = (Work) parent.getAdapter()
                .getItem(position);
        if (work != null) {
            Intent intent = new Intent(this, CaseDetailActivity.class);
            intent.putExtra("id", work.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!onLoad) {
            currentPage = 1;
            new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
    }

    private class GetWorksTask extends AsyncTask<String, Object, JSONObject> {
        private String url;

        private GetWorksTask() {
            onLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            url = strings[0];
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
            if (url.equals(String.format(currentUrl, currentPage))) {
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    int pageCount = jsonObject.optInt("page_count", 0);
                    if (currentPage == 1) {
                        works.clear();
                    }
                    JSONArray jsonArray = jsonObject.optJSONArray("list");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0, size = jsonArray.length(); i < size; i++) {
                            works.add(new Work(jsonArray.optJSONObject(i)));
                        }
                    }
                    boolean isEnd = pageCount <= currentPage;
                    if (isEnd) {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                    } else {
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
                if (works.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();

                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);

                    imgEmptyHint.setVisibility(View.VISIBLE);
                    imgNetHint.setVisibility(View.VISIBLE);
                    textEmptyHint.setVisibility(View.VISIBLE);
                    if (JSONUtil.isNetworkConnected(CustomerPhotosActivity.this)) {
                        imgNetHint.setVisibility(View.GONE);
                        textEmptyHint.setText(R.string.no_item);
                    } else {
                        imgEmptyHint.setVisibility(View.GONE);
                        textEmptyHint.setText(R.string.net_disconnected);
                    }
                }
                onLoad = false;
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'customer_photo_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.work_cover)
        ImageView workCover;
        @BindView(R.id.tv_photos_count)
        TextView tvPhotosCount;
        @BindView(R.id.tv_watch_count)
        TextView tvWatchCount;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.merchant_logo)
        RoundedImageView merchantLogo;
        @BindView(R.id.merchant_name)
        TextView merchantName;
        @BindView(R.id.trans_layout)
        View transLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
