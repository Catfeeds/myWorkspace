package me.suncloud.marrymemo.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.prepared.WeddingPreparedAdapter;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.model.WeddingPreparedListModelItem;
import me.suncloud.marrymemo.model.prepared.WeddingPreparedCategoryMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.viewholder.prepared.WeddingPreparedHeaderViewHolder;

/**
 * 备婚清单scrollview 版本
 * Created by kingsun on 15/12/16.
 */
public class WeddingPreparedListActivity extends HljBaseNoBarActivity implements
        WeddingPreparedAdapter.onViewHolderInflateListener {
    private static final String POSITONTAG = "position";
    private static final String OFFSETTAG = "offset";

    @Override
    public String pageTrackTagName() {
        return "备婚流程";
    }

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R.id.shadow_view)
    View shadowView;
    @BindView(R.id.btn_share)
    ImageView btnShare;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.action_layout_w)
    RelativeLayout actionLayoutW;
    @BindView(R.id.action_layout_r)
    RelativeLayout actionLayoutR;
    @BindView(R.id.layout_content)
    RelativeLayout layoutContent;

    private long mCategoryId = 1L;
    private int mSelectPosition;
    private int offset;
    private long cid;

    private WeddingPreparedAdapter weddingPreparedAdapter;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber detailSubscriber;
    private List<WeddingPreparedCategoryMode> categoryModeList;
    private List<WeddingPreparedListModelItem> strategyList;
    private List<WeddingPreparedListModelItem> recommendList;
    private LinearLayoutManager manager;
    private WeddingPreparedHeaderViewHolder headerViewHolder;
    private int viewPagerHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_parelist);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        mSelectPosition = getSharedPreferences(this.getClass()
                .getSimpleName(), Context.MODE_PRIVATE).getInt(POSITONTAG, 0);
        offset = getSharedPreferences(this.getClass()
                .getSimpleName(), Context.MODE_PRIVATE).getInt(OFFSETTAG, 0);
        viewPagerHeight = CommonUtil.dp2px(this, 200-45) + CommonUtil.getStatusBarHeight(this);
        categoryModeList = new ArrayList<>();
        strategyList = new ArrayList<>();
        recommendList = new ArrayList<>();
        mCategoryId = getCategoryId(0);
        cid = Session.getInstance()
                .getMyCity(this)
                .getId();
    }

    private void initWidget() {
        setActionBarPadding(actionLayout, layoutContent);

        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        weddingPreparedAdapter = new WeddingPreparedAdapter(this,
                mSelectPosition,
                offset,
                strategyList,
                recommendList,
                categoryModeList);
        weddingPreparedAdapter.setOnViewHolderInflateListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(weddingPreparedAdapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int position = manager.findFirstVisibleItemPosition();
                        float alpha = 0.0F;
                        if (position >= 1) {
                            alpha = 1.0F;
                        } else {
                            View firstView = manager.findViewByPosition(position);
                            if (firstView != null) {
                                int top = firstView.getTop();
                                alpha = Math.min(Math.abs(top * 1.0F / viewPagerHeight),1.0F);
                            }
                        }
                        setAlpha(alpha);
                    }
                });
    }

    private void initLoad() {
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setPullToRefreshBase(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        setCategoryList(jsonElement);
                    }
                })
                .build();
        CustomCommonApi.getWeddingPrepareCategoryList()
                .subscribe(initSubscriber);
    }

    private void loadStrategyAndRecommend() {
        CommonUtil.unSubscribeSubs(detailSubscriber);
        detailSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        setWeddingPrepareDetail(jsonElement);
                    }
                })
                .build();
        CustomCommonApi.getWeddingPrepareData(cid, mCategoryId)
                .subscribe(detailSubscriber);
    }

    private void setWeddingPrepareDetail(JsonElement jsonElement) {
        if (jsonElement == null) {
            return;
        }
        strategyList.clear();
        JsonArray strategy = jsonElement.getAsJsonObject()
                .get("strategy")
                .getAsJsonArray();
        if (strategy != null) {
            for (int i = 0, size = strategy.size(); i < size; i++) {
                JsonElement json = strategy.get(i);
                if (json != null) {
                    try {
                        strategyList.add(new WeddingPreparedListModelItem(new JSONObject(json
                                .toString())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        recommendList.clear();
        JsonArray recommend = jsonElement.getAsJsonObject()
                .get("recommend")
                .getAsJsonArray();
        if (recommend != null) {
            for (int i = 0, size = recommend.size(); i < size; i++) {
                JsonElement json = recommend.get(i);
                if (json != null) {
                    try {
                        recommendList.add(new WeddingPreparedListModelItem(new JSONObject(json
                                .toString())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        weddingPreparedAdapter.notifyDataSetChanged();
    }

    private void setAlpha(float alpha) {
        actionLayoutR.setAlpha(alpha);
        actionLayoutW.setAlpha(Math.max(1 - alpha, 0));
        shadowView.setAlpha(Math.max(1 - alpha, 0));
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    private void setCategoryList(JsonElement jsonElement) {
        if (jsonElement == null || !(jsonElement instanceof JsonArray)) {
            return;
        }
        JsonArray array = jsonElement.getAsJsonArray();
        if (array != null) {
            for (int i = 0, size = array.size(); i < size; i++) {
                JsonObject json = array.get(i)
                        .getAsJsonObject();
                if (json != null) {
                    long id = json.get("id")
                            .getAsLong();
                    String name = json.get("name")
                            .getAsString();
                    WeddingPreparedCategoryMode mode = new WeddingPreparedCategoryMode(id, name);
                    categoryModeList.add(mode);
                }
            }
        }
        if (headerViewHolder != null) {
            headerViewHolder.notifyItemCategoryChanged();
        }
    }

    private long getCategoryId(int position) {
        if (categoryModeList.isEmpty()) {
            return 1L;
        }
        return categoryModeList.get(position)
                .getId();
    }

    @Override
    public void onViewPagerHolderInflate(WeddingPreparedHeaderViewHolder holder) {
        this.headerViewHolder = holder;
        setActionBarPadding(holder.getPagerHolderLayout());
    }

    @Override
    public void onViewPagerSnapListener(int centerPosition, int selectPosition, int offset) {
        mCategoryId = categoryModeList.get(centerPosition)
                .getId();
        mSelectPosition = selectPosition;
        this.offset = offset;
        if (headerViewHolder != null) {
            headerViewHolder.setCategoryId(mCategoryId);
        }
        weddingPreparedAdapter.clearLoadState();
        loadStrategyAndRecommend();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        saveSelectPosition(mSelectPosition, offset);
        CommonUtil.unSubscribeSubs(initSubscriber, detailSubscriber);
    }

    private void saveSelectPosition(int position, int offset) {
        SharedPreferences share = this.getSharedPreferences(this.getClass()
                .getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(POSITONTAG, position);
        editor.putInt(OFFSETTAG, offset);
        editor.apply();
    }
}
