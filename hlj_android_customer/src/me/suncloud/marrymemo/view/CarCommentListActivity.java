package me.suncloud.marrymemo.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.CarCommentPicsAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.CarComment;
import me.suncloud.marrymemo.model.CarProduct;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2015/10/19.
 */
public class CarCommentListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<CarComment>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView> {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ObjectBindAdapter<CarComment> adapter;
    private ArrayList<CarComment> carComments;
    private int avaterSize;
    private int coverSize;
    private int orderItemHeight;
    private int orderSpace;
    private boolean isEnd;
    private boolean isLoad;
    private TextView endView;
    private View loadView;
    private int lastPage;
    private int currentPage;
    private String currentUrl;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long cityId = getIntent().getLongExtra("cityId", 0);
        currentUrl = Constants.getAbsUrl(String.format(Constants.HttpPath.CAR_COMMENT_URL,
                cityId,
                20)) + Constants.PAGE_COUNT;
        currentPage = 1;
        lastPage = 1;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        avaterSize = Math.round(dm.density * 30);
        coverSize = Math.round(dm.density * 60);
        orderItemHeight = Math.round(dm.density * 80);
        orderSpace = Math.round(dm.density * 6);
        carComments = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, carComments, R.layout.car_comment_list_item, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        ButterKnife.bind(this);
        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setDividerHeight(Math.round(dm.density * 10));
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }
        });
        new GetCommentsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                String.format(currentUrl, currentPage));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(this)) {
                        loadView.setVisibility(View.VISIBLE);
                        endView.setVisibility(View.GONE);
                        currentPage++;
                        new GetCommentsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                                String.format(currentUrl, currentPage));
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

    }

    @Override
    public void setViewValue(View view, final CarComment carComment, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder(view);
            holder.picsAdapter = new CarCommentPicsAdapter(CarCommentListActivity.this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.imagesView.setLayoutManager(layoutManager);
            holder.imagesView.addItemDecoration(new SpacesItemDecoration(CarCommentListActivity
                    .this));
            holder.imagesView.setAdapter(holder.picsAdapter);
            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        if (carComment.getUser() != null) {
            String path = carComment.getUser()
                    .getAvatar(avaterSize);
            holder.name.setText(carComment.getUser()
                    .getNick());
            if (!JSONUtil.isEmpty(path)) {
                holder.userIcon.setTag(path);
                ImageLoadTask task = new ImageLoadTask(holder.userIcon, null, 0);
                task.loadImage(path,
                        avaterSize,
                        ScaleMode.ALL,
                        new AsyncBitmapDrawable(getResources(),
                                R.mipmap.icon_avatar_primary,
                                task));

            }
        }
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type4),
                    Locale.getDefault());
        }
        holder.time.setText(simpleDateFormat.format(carComment.getTime()));
        holder.content.setVisibility(JSONUtil.isEmpty(carComment.getContent()) ? View.GONE : View
                .VISIBLE);
        holder.content.setText(carComment.getContent());
        if (carComment.getPhotos() != null && !carComment.getPhotos()
                .isEmpty()) {
            holder.imagesView.setVisibility(View.VISIBLE);
            holder.picsAdapter.setPhotos(carComment.getPhotos());
            holder.imagesView.getLayoutManager()
                    .scrollToPosition(0);
        } else {
            holder.imagesView.setVisibility(View.GONE);
        }
        if (carComment.getCarProducts()
                .isEmpty()) {
            holder.orderInfo.setVisibility(View.GONE);
            holder.ordersLayout.setVisibility(View.GONE);
        } else {
            holder.imgArrow.clearAnimation();
            holder.orderList.clearAnimation();
            holder.orderInfo.setVisibility(View.VISIBLE);
            holder.ordersLayout.setVisibility(View.VISIBLE);
            int size = carComment.getCarProducts()
                    .size();
            int childSize = holder.orderList.getChildCount();
            //            holder.expandHeight = //(size > 3 ? size - 3 : 0) *
            // (orderItemHeight+orderSpace);
            holder.orderList.getLayoutParams().height = size * (orderItemHeight + orderSpace);//
            // (carComment.isExpand() ? size : Math.min(size, 3)) * (orderItemHeight + orderSpace);
            if (childSize > size) {
                holder.orderList.removeViews(size, childSize - size);
            }
            StringBuffer stringBuffer = new StringBuffer(getString(R.string.label_order));
            for (int i = 0; i < size; i++) {
                View childView = null;
                if (childSize > i) {
                    childView = holder.orderList.getChildAt(i);
                }
                final CarProduct product = carComment.getCarProducts()
                        .get(i);
                stringBuffer.append(i == 0 ? ": " : " ;")
                        .append(product.getTitle());
                if (childView == null) {
                    childView = View.inflate(CarCommentListActivity.this,
                            R.layout.comment_car_product_item,
                            null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup
                            .LayoutParams.MATCH_PARENT,
                            orderItemHeight);
                    params.setMargins(0, orderSpace, 0, 0);
                    holder.orderList.addView(childView, params);
                    CarViewHolder carHolder = new CarViewHolder(childView);
                    childView.setTag(carHolder);
                }
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CarCommentListActivity.this,
                                WeddingCarProductDetailActivity.class);
                        intent.putExtra(WeddingCarProductDetailActivity.ARG_ID, product.getId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                CarViewHolder carViewHolder = (CarViewHolder) childView.getTag();
                carViewHolder.price.setText(getString(R.string.label_price5,
                        Util.formatDouble2String(product.getShowPrice())));
                if (product.getMarketPrice() > 0) {
                    carViewHolder.originalPrice.setVisibility(View.VISIBLE);
                    carViewHolder.originalPrice.setText(getString(R.string.label_price5,
                            Util.formatDouble2String(product.getMarketPrice())));
                } else {
                    carViewHolder.originalPrice.setVisibility(View.GONE);
                }
                carViewHolder.count.setText("x" + carComment.getCount(product.getId()));
                String path = JSONUtil.getImagePath2(product.getCover(), coverSize);
                carViewHolder.title.setText(product.getTitle());
                if (!JSONUtil.isEmpty(path)) {
                    carViewHolder.cover.setTag(path);
                    ImageLoadTask task = new ImageLoadTask(carViewHolder.cover);
                    task.loadImage(path,
                            coverSize,
                            ScaleMode.ALL,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_avatar_primary,
                                    task));
                }
            }
            holder.orderInfo.setText(stringBuffer);
            //            if (size > 3) {
            //                holder.collapseButtonLayout.setVisibility(View.VISIBLE);
            //                holder.imgArrow.setRotation(carComment.isExpand() ? 180 : 0);
            //                holder.tvRestLabel.setText(getString(carComment.isExpand() ? R
            // .string.label_hide_rest_product : R.string.label_show_rest_product, size - 3));
            //                holder.collapseButtonLayout.setOnClickListener(new View
            // .OnClickListener() {
            //                    @Override
            //                    public void onClick(View v) {
            //                        if (holder.orderList.getAnimation() != null && !holder
            // .orderList
            //                                .getAnimation().hasEnded()) {
            //                            return;
            //                        }
            //                        if (holder.imgArrow.getAnimation() != null && !holder.imgArrow
            //                                .getAnimation().hasEnded()) {
            //                            return;
            //                        }
            //                        holder.imgArrow.setRotation(0);
            //                        if (carComment.isExpand()) {
            //                            carComment.setIsExpand(false);
            //                            AnimUtil.hideResizeAnimation(holder.orderList, holder
            // .orderList.getHeight(), holder.expandHeight);
            //                            holder.imgArrow.startAnimation(AnimUtil
            // .getAnimArrowDown(CarCommentListActivity.this));
            //                        } else {
            //                            carComment.setIsExpand(true);
            //                            AnimUtil.showResizeAnimation(holder.orderList, holder
            // .orderList.getHeight(), holder.expandHeight);
            //                            holder.imgArrow.startAnimation(AnimUtil.getAnimArrowUp
            // (CarCommentListActivity.this));
            //                        }
            //                        holder.tvRestLabel.setText(getString(carComment.isExpand()
            // ? R.string.label_hide_rest_product : R.string.label_show_rest_product, carComment
            // .getCarProducts().size() - 3));
            //                    }
            //                });
            //            } else {
            //                holder.collapseButtonLayout.setVisibility(View.GONE);
            //            }

        }

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            new GetCommentsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
    }

    private class GetCommentsTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        private GetCommentsTask() {
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
            if (url.equals(String.format(currentUrl, currentPage))) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    lastPage = currentPage;
                    int pageCount = jsonObject.optInt("page_count", 0);
                    isEnd = pageCount <= currentPage;
                    if (isEnd) {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.no_more);
                    } else {
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    }
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        carComments.clear();
                    }
                    if (array != null && array.length() > 0) {
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            carComments.add(new CarComment(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    currentPage = lastPage;
                    endView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    endView.setText(R.string.hint_net_disconnected);
                }
                if (carComments.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        emptyView.findViewById(R.id.text_empty_hint)
                                .setVisibility(View.VISIBLE);
                        listView.setEmptyView(emptyView);
                    }
                    emptyView.setVisibility(View.VISIBLE);
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);
                    if (JSONUtil.isNetworkConnected(CarCommentListActivity.this)) {
                        imgNetHint.setVisibility(View.GONE);
                        imgEmptyHint.setVisibility(View.VISIBLE);
                        textEmptyHint.setText(R.string.no_item);
                    } else {
                        imgNetHint.setVisibility(View.VISIBLE);
                        imgEmptyHint.setVisibility(View.GONE);
                        textEmptyHint.setText(R.string.net_disconnected);
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }


    static class CarViewHolder {
        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.original_price)
        TextView originalPrice;
        @BindView(R.id.count)
        TextView count;

        CarViewHolder(View view) {
            ButterKnife.bind(this, view);
            originalPrice.getPaint()
                    .setAntiAlias(true);
            originalPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'car_comment_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        //        int expandHeight;
        CarCommentPicsAdapter picsAdapter;
        @BindView(R.id.user_icon)
        RoundedImageView userIcon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.order_info)
        TextView orderInfo;
        @BindView(R.id.images_view)
        RecyclerView imagesView;
        @BindView(R.id.order_list)
        LinearLayout orderList;
        @BindView(R.id.tv_rest_label)
        TextView tvRestLabel;
        @BindView(R.id.img_arrow)
        ImageView imgArrow;
        @BindView(R.id.collapse_button_layout)
        LinearLayout collapseButtonLayout;
        @BindView(R.id.orders_layout)
        RelativeLayout ordersLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(Context context) {
            this.space = Math.round(context.getResources()
                    .getDisplayMetrics().density * 8);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != 0)
                outRect.left = space;
        }
    }
}
