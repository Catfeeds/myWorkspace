package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.ShopProduct;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusHttpDeleteTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;
import me.suncloud.marrymemo.widget.MarkHeaderView;
import me.suncloud.marrymemo.widget.MarkOrderView;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by jinxin on 2016/6/24.
 */
public class MoreMarkProductFragment extends RefreshFragment implements MarkHeaderView
        .OnItemClickListener, PullToRefreshBase.OnRefreshListener, AbsListView.OnScrollListener,
        ObjectBindAdapter.ViewBinder<ShopProduct>, AbsListView.OnItemClickListener {
    @Override
    public void refresh(Object... params) {

    }

    private final int FOLLOW_LOGIN = 10;
    private PullToRefreshStaggeredGridView listView;
    private MarkHeaderView headerLabelView;
    private MarkOrderView headerOrderView;
    private DisplayMetrics dm;

    private ObjectBindAdapter<ShopProduct> adapter;
    private ArrayList<ShopProduct> mData;
    private View progressBar;
    private MarkOrderView tabOrderView;

    private long markId;
    private int markType;
    private int imageWidth;
    private int itemMargin;
    private OnOrderCheckChangeListener onOrderCheckChangeListener;
    private OnOrderClickListener onOrderClickListener;
    //是否是更多婚品页
    private boolean isMore;

    private String sortType;
    private String order;
    protected boolean isLoad;
    private boolean isEnd;
    protected int currentPage;
    protected View footView;
    private View loadView;
    private TextView endView;
    private String currentUrl;
    private Button btnFollow;
    //1已关注 0未关注
    private int follow;
    private Handler mHandler;
    private SuspendRunnable mRunnable;
    private int headerLabelHeight;
    private int headerOrderHeight;
    private View emptyView;
    private DecimalFormat decimalFormat;
    private int badgeSize;
    private int signSize;
    private View rootView;
    private String markTitle;


    public static MoreMarkProductFragment newInstance(Bundle data) {
        MoreMarkProductFragment fragment = new MoreMarkProductFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mark_product_particular, container, false);
        TextView title = (TextView) rootView.findViewById(R.id.tv_title);
        title.setText(JSONUtil.isEmpty(markTitle) ? getString(R.string.label_subject_wedding) :
                markTitle);
        initTabView();
        initHeader();
        initFoot();
        initRunnable();
        mData = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getContext(),
                mData,
                R.layout.shopping_list_single_item2,
                this);
        progressBar = rootView.findViewById(R.id.progressBar);
        btnFollow = (Button) rootView.findViewById(R.id.btn_filter);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFollow(v);
            }
        });
        btnFollow.setVisibility(isMore ? View.GONE : View.VISIBLE);
        listView = (PullToRefreshStaggeredGridView) rootView.findViewById(R.id.list);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        if (!isMore) {
            listView.getRefreshableView()
                    .addHeaderView(headerLabelView);
            listView.getRefreshableView()
                    .addHeaderView(headerOrderView);
            headerOrderView.setContentVisible(false);
        }
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.getRefreshableView()
                .setItemMargin(itemMargin);
        listView.getRefreshableView()
                .setAdapter(adapter);
        listView.getRefreshableView()
                .setOnItemClickListener(this);

        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        endView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        getData(currentPage);
        rootView.findViewById(R.id.back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed(v);
                    }
                });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle data = getArguments();
        if (data != null) {
            markId = data.getLong("markId", 0);
            markType = data.getInt("markType", 0);
            isMore = data.getBoolean("isMore", false);
            markTitle = data.getString("markTitle");
            if (isMore) {
                if (!JSONUtil.isEmpty(markTitle) && !markTitle.contains("-婚品")) {
                    markTitle += "-婚品";
                }
            }
        }
        dm = getResources().getDisplayMetrics();
        itemMargin = Math.round(8 * dm.density);
        imageWidth = Math.round((JSONUtil.getDeviceSize(getContext()).x - itemMargin * 3) / 2);
        super.onCreate(savedInstanceState);
    }

    private void initRunnable() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MarkHeaderView.MARK) {
                    headerLabelHeight = (int) msg.obj;
                } else if (msg.what == MarkOrderView.ORDER) {
                    headerOrderHeight = (int) msg.obj;
                }
            }
        };
        mRunnable = new SuspendRunnable();
        badgeSize = Math.round(40 * dm.density);
        signSize = Math.round(24 * dm.density);
    }


    private void initFoot() {
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing just cost event
            }
        });
        endView = (TextView) footView.findViewById(R.id.no_more_hint);
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }
        });
        loadView = footView.findViewById(R.id.loading);
    }

    private void initHeader() {
        if (!isMore) {
            headerLabelView = new MarkHeaderView(getContext());
            headerLabelView.setActivity(getActivity());
            headerLabelView.setRelativeId(markId);
            headerLabelView.setOnItemClickListener(this);
            headerLabelView.setPaddingVisible(true);
            headerLabelView.setHeightHandler(mHandler);
            headerLabelView.setOnDataChangeListener(new MarkHeaderView.OnDataChangeListener() {
                @Override
                public void onDataChanged(
                        ArrayList<JSONObject> data,
                        MarkHeaderView.MarkHeaderAdapter adapter,
                        ViewGroup parent) {
                    if (data != null && data.size() <= 0) {
                        headerLabelView.setContentVisible(false, false);
                    }
                }
            });
            headerOrderView = new MarkOrderView(getContext());
            RecyclerView.LayoutParams orderParams = new RecyclerView.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            headerOrderView.setLayoutParams(orderParams);
            headerOrderView.setOrderTextVisible(true, true, true, false, false);
            onOrderCheckChangeListener = new OnOrderCheckChangeListener();
            onOrderClickListener = new OnOrderClickListener();
            headerOrderView.setOnCheckedChangeListener(onOrderCheckChangeListener);
            headerOrderView.setOnTextClickListener(onOrderClickListener);
        }
    }

    private void initTabView() {
        if (!isMore) {
            tabOrderView = (MarkOrderView) rootView.findViewById(R.id.mark_order_layout);
        } else {
            tabOrderView = (MarkOrderView) rootView.findViewById(R.id.more_order_layout);
            tabOrderView.setVisibility(View.VISIBLE);
        }
        tabOrderView.setOrderTextVisible(true, true, true, false, false);
        if (onOrderCheckChangeListener == null) {
            onOrderCheckChangeListener = new OnOrderCheckChangeListener();
        }
        if (onOrderClickListener == null) {
            onOrderClickListener = new OnOrderClickListener();
        }
        tabOrderView.setOnCheckedChangeListener(onOrderCheckChangeListener);
        tabOrderView.setOnTextClickListener(onOrderClickListener);
    }

    private void getOrderType(int id) {
        switch (id) {
            case R.id.result_detail_all:
                //综合
                sortType = null;
                order = null;
                break;
            case R.id.result_detail_hot:
                //热门
                sortType = "view_count";
                order = "desc";
                break;
        }
        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        getData(currentPage);
    }

    public void onBackPressed(View view) {
        getActivity().onBackPressed();
    }

    public void onFollow(View view) {
        if (!Util.loginBindChecked(getActivity(), FOLLOW_LOGIN)) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", markId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //1已关注 0未关注
        if (follow == 1) {
            //取消关注
            progressBar.setVisibility(View.VISIBLE);
            new StatusHttpDeleteTask(getContext(), new StatusRequestListener() {
                @Override
                public void onRequestCompleted(
                        Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    follow = 0;
                    btnFollow.setText(getString(R.string.label_follow));
                    Util.showToast(R.string.hint_discollect_complete2, getContext());
                }

                @Override
                public void onRequestFailed(
                        ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(getActivity(),
                            returnStatus,
                            R.string.msg_fail_to_cancel_follow,
                            network);
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.MARK_CANCEL_FOLLOW, markId));
        } else if (follow == 0) {
            //添加关注
            progressBar.setVisibility(View.VISIBLE);
            new StatusHttpPostTask(getContext(), new StatusRequestListener() {
                @Override
                public void onRequestCompleted(
                        Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    follow = 1;
                    btnFollow.setText(getString(R.string.label_followed));
                    Util.showToast(R.string.label_mark_followed, getContext());

                }

                @Override
                public void onRequestFailed(
                        ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(getActivity(),
                            returnStatus,
                            R.string.msg_fail_to_follow,
                            network);
                }

            }).execute(Constants.getAbsUrl(Constants.HttpPath.MARK_ADD_FOLLOW),
                    jsonObject.toString());
        }
    }

    private void addSuspendView() {
        if (!isMore) {
            mRunnable.setVisible(View.VISIBLE);
            mHandler.post(mRunnable);
        }
    }

    private void removeSuspendView() {
        if (!isMore) {
            mRunnable.setVisible(View.INVISIBLE);
            mHandler.post(mRunnable);
        }
    }


    private void setEmptyView() {
        if (mData.isEmpty()) {
            footView.setVisibility(View.GONE);
            if (emptyView == null) {
                emptyView = rootView.findViewById(R.id.empty_hint_layout);
            }
            ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
            ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
            TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

            imgEmptyHint.setVisibility(View.VISIBLE);
            imgNetHint.setVisibility(View.VISIBLE);
            textEmptyHint.setVisibility(View.VISIBLE);

            if (JSONUtil.isNetworkConnected(getContext())) {
                imgNetHint.setVisibility(View.GONE);
                textEmptyHint.setText(R.string.no_item);
            } else {
                imgEmptyHint.setVisibility(View.GONE);
                textEmptyHint.setText(R.string.net_disconnected);
            }
            if (!isMore) {
                headerOrderView.setContentVisible(false);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) emptyView
                        .getLayoutParams();
                if (params != null) {
                    if (headerLabelView.getData()
                            .size() > 0) {
                        params.height = Math.round(JSONUtil.getDeviceSize(getContext()).y -
                                headerLabelHeight - Math.round(
                                dm.density * (45 + 10 + 10 + 2)));
                    } else {
                        params.height = JSONUtil.getDeviceSize(getContext()).y;
                    }
                }
            }

            emptyView.invalidate();
            emptyView.setVisibility(View.VISIBLE);
        } else {
            if (!isMore && headerOrderView != null) {
                headerOrderView.setContentVisible(true);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FOLLOW_LOGIN && resultCode == Activity.RESULT_OK) {
            //关注重新登录回来
            onFollow(null);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(View view, JSONObject data, int position) {
        if (data != null) {
            long id = data.optLong("id");
            String markTitle = data.optString("name");
            int type = data.optInt("marked_type");
            Util.markActionActivity(getContext(), type, markTitle, id, false);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isLoad) {
            currentPage = 1;
            getData(currentPage);
        }
    }


    private void getData(int currentPage) {
        String tempUrl = Constants.HttpPath.GET_MARK_LIST;
        tempUrl = tempUrl.replace("&city_code=%s", "");
        currentUrl = Constants.getAbsUrl(tempUrl, markId, markType, order, sortType, currentPage);
        if (JSONUtil.isEmpty(sortType) && JSONUtil.isEmpty(order)) {
            currentUrl = currentUrl.replace("order=null&", "")
                    .replace("sort=null&", "");
        }
        new GetMarkListTask().executeOnExecutor(Constants.LISTTHEADPOOL, currentUrl);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(getContext())) {
                        loadView.setVisibility(View.VISIBLE);
                        endView.setVisibility(View.GONE);
                        currentPage++;
                        getData(currentPage);
                    } else {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.hint_net_disconnected);
                    }
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem <= 0) {
            removeSuspendView();
        } else if (firstVisibleItem > 0) {
            addSuspendView();
        }
    }


    @Override
    public void setViewValue(View view, ShopProduct shopProduct, int position) {
        ViewHolder holder = null;
        if (view.getTag() != null) {
            holder = (ViewHolder) view.getTag();
        }
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        ShopProduct product = mData.get(position);
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("###.00");
        }
        if (product != null) {
            String priceStr = decimalFormat.format(product.getPrice());
            holder.price2.setText(priceStr);

            holder.collectCount.setText(String.valueOf(product.getLikeCount()));
            holder.title.setText(product.getTitle());
            holder.collectCount.setText(String.valueOf(shopProduct.getLikeCount()));
            String imagePath = JSONUtil.getImagePath(shopProduct.getPhoto(), imageWidth);
            if (!JSONUtil.isEmpty(imagePath)) {
                ImageLoadTask task = new ImageLoadTask(holder.image);
                holder.image.setTag(imagePath);
                task.loadImage(imagePath,
                        imageWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(imageWidth,
                    imageWidth);
            holder.image.setLayoutParams(imageParams);
            RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            holder.contentLayout.setLayoutParams(itemParams);
        }

        if (product.getRule() != null && !JSONUtil.isEmpty(product.getRule()
                .getShowimg2())) {
            holder.badge.setVisibility(View.VISIBLE);
            String badgeUrl = JSONUtil.getImagePath2(product.getRule()
                    .getShowimg2(), badgeSize);
            if (!badgeUrl.equals(holder.badge.getTag())) {
                holder.badge.setTag(badgeUrl);
                ImageLoadTask task = new ImageLoadTask(holder.badge, null, null, 0, true, true);
                task.loadImage(badgeUrl,
                        badgeSize,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
        } else {
            holder.badge.setVisibility(View.GONE);
        }
        String signUrl = JSONUtil.getImagePath2(product.getShopImg(), signSize);
        if (!JSONUtil.isEmpty(signUrl)) {
            holder.sign.setVisibility(View.VISIBLE);
            if (!signUrl.equals(holder.sign.getTag())) {
                holder.sign.setTag(signUrl);
                ImageLoadTask task = new ImageLoadTask(holder.sign, null, null, 0, true, true);
                task.loadImage(signUrl,
                        signSize,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
        } else {
            holder.sign.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        ShopProduct product = (ShopProduct) parent.getAdapter()
                .getItem(position);
        if (product != null) {
            Intent intent = new Intent(getContext(), ShopProductDetailActivity.class);
            intent.putExtra("id", product.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    class OnOrderCheckChangeListener implements CheckableLinearLayout2.OnCheckedChangeListener {

        @Override
        public void onCheckedChange(View view, boolean checked) {
            if (checked) {
                sortType = "actual_price";
                order = "desc";
            } else {
                sortType = "actual_price";
                order = "asc";
            }
            progressBar.setVisibility(View.VISIBLE);
            currentPage = 1;
            getData(currentPage);
        }
    }

    class SuspendRunnable implements Runnable {
        private int visible;

        public void setVisible(int visible) {
            this.visible = visible;
        }

        @Override
        public void run() {
            if (!isMore) {
                if (tabOrderView != null) {
                    tabOrderView.setVisibility(visible);
                }
            }
        }
    }

    class OnOrderClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getOrderType(v.getId());
        }
    }


    class GetMarkListTask extends AsyncTask<String, Void, JSONObject> {
        private String url;

        private GetMarkListTask() {
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
                return new JSONObject(json);
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
            if (url.equalsIgnoreCase(currentUrl)) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    if (currentPage == 1) {
                        mData.clear();
                    }

                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {
                        int pageCount = data.optInt("page_count", 0);
                        isEnd = pageCount <= currentPage;
                        if (isEnd) {
                            endView.setVisibility(View.VISIBLE);
                            loadView.setVisibility(View.GONE);
                            endView.setText(R.string.no_more);
                        } else {
                            endView.setVisibility(View.GONE);
                            loadView.setVisibility(View.INVISIBLE);
                        }

                        follow = data.optInt("is_follow", 0);
                        //1已关注 0未关注
                        if (follow == 0) {
                            btnFollow.setText(getString(R.string.label_follow));
                        } else if (follow == 1) {
                            btnFollow.setText(getString(R.string.label_followed));
                        }
                        JSONArray list = data.optJSONArray("list");
                        if (list != null) {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject item = list.optJSONObject(i);
                                ShopProduct product = new ShopProduct(item);
                                if (product.getId() > 0) {
                                    mData.add(product);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            setEmptyView();
            super.onPostExecute(jsonObject);
        }
    }

    class ViewHolder {
        @BindView(R.id.image)
        RecyclingImageView image;
        @BindView(R.id.badge)
        ImageView badge;
        @BindView(R.id.sign)
        ImageView sign;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.price2)
        TextView price2;
        @BindView(R.id.collect_count)
        TextView collectCount;
        @BindView(R.id.content_layout)
        RelativeLayout contentLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
