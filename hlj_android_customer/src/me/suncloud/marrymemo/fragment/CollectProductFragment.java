package me.suncloud.marrymemo.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

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
import me.suncloud.marrymemo.model.ShopProduct;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * Created by Suncloud on 2016/2/25.
 */
public class CollectProductFragment extends RefreshListFragment implements ObjectBindAdapter
        .ViewBinder<ShopProduct>, AdapterView.OnItemClickListener {

    private int size;
    private View progressBar;
    private View rootView;
    private ArrayList<ShopProduct> products;
    private ObjectBindAdapter<ShopProduct> adapter;

    public static CollectProductFragment newInstance() {
        Bundle args = new Bundle();
        CollectProductFragment fragment = new CollectProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        size = Util.dp2px(getActivity(), 74);
        products = new ArrayList<>();
        footerView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        adapter = new ObjectBindAdapter<>(getActivity(), products, R.layout.collect_item, this);
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
        if (CommonUtil.isCollectionEmpty(products)) {
            progressBar.setVisibility(View.VISIBLE);
            onRefresh(listView);
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShopProduct product = (ShopProduct) parent.getAdapter()
                .getItem(position);
        if (product != null) {
            Intent intent = new Intent(getActivity(), ShopProductDetailActivity.class);
            intent.putExtra("id", product.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onDataLoad(int page) {
        new GetProductTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(page));
    }

    private String getUrl(int page) {
        return Constants.getAbsUrl(String.format(Constants.HttpPath.GET_COLLECT_SHOP_URL, page));
    }

    @Override
    public void setViewValue(View view, ShopProduct shopProduct, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.imgCover.getLayoutParams().width = size;
            holder.tvProperty.setVisibility(View.GONE);
            holder.tvRmb.setVisibility(View.VISIBLE);
            view.setTag(holder);
        }

        holder.tvTitle.setText(shopProduct.getTitle());
        holder.lineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.tvRmb.setVisibility(View.VISIBLE);
        holder.tvPrice.setVisibility(View.VISIBLE);
        holder.tvPrice.setText(Util.formatDouble2String(shopProduct.getPrice()));
        if (!shopProduct.isOld() && !shopProduct.isPublished()) {
            holder.tvDescribe.setVisibility(View.VISIBLE);
            holder.tvDescribe.setText(R.string.label_invalid);
        } else if (shopProduct.getPrice() < shopProduct.getOldPrice()) {
            holder.tvDescribe.setVisibility(View.VISIBLE);
            holder.tvDescribe.setText(getString(R.string.label_discount,
                    Util.formatDouble2String(shopProduct.getOldPrice() - shopProduct.getPrice())));
        } else {
            holder.tvDescribe.setVisibility(View.GONE);
        }
        String coverPath = JSONUtil.getImagePath2(shopProduct.getPhoto(), size);
        if (!JSONUtil.isEmpty(coverPath)) {
            ImageLoadTask task = new ImageLoadTask(holder.imgCover);
            holder.imgCover.setTag(coverPath);
            task.loadImage(coverPath,
                    size,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            holder.imgCover.setImageBitmap(null);
        }

    }


    private class GetProductTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        private GetProductTask() {
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
                    if (onTabTextChangeListener != null) {
                        int totalCount = jsonObject.optInt("total_count", 0);
                        onTabTextChangeListener.onTabTextChange(totalCount);
                    }
                    onLoadCompleted(pageCount <= currentPage);
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        products.clear();
                    }
                    if (array != null && array.length() > 0) {
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            products.add(new ShopProduct(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else if (!products.isEmpty()) {
                    onLoadFailed();
                }
                if (products.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = rootView.findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    Util.setEmptyView(getActivity(),
                            emptyView,
                            R.string.hint_collect_shop_empty,
                            R.mipmap.icon_empty_common,
                            0,
                            0);

                }
            }

            super.onPostExecute(jsonObject);
        }
    }

    static class ViewHolder {
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_rmb)
        TextView tvRmb;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_describe)
        TextView tvDescribe;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
