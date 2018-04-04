package com.hunliji.marrybiz.view.shop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.shoptheme.ShopThemeApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.shoptheme.ShopTheme;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by hua_rong on 2017/5/19.
 * 店铺主题模板
 */

public class ShopThemeActivity extends HljBaseActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {

    private static final int REQUEST_CODE = 100;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private Context context;
    private List<ShopTheme> shopThemeList;
    private HljHttpSubscriber refreshSubscriber;
    private ShopThemeAdapter adapter;
    private MerchantUser user;
    private long currentTheme = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        context = this;
        shopThemeList = new ArrayList<>();
        recyclerView.setBackgroundColor(Color.WHITE);
        initView();
        user = Session.getInstance()
                .getCurrentUser(this);
        initErrorView();
        onRefresh(recyclerView);
    }

    private void initErrorView() {
        //网络异常,可点击屏幕重新加载
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initView() {
        adapter = new ShopThemeAdapter(context, shopThemeList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(gridLayoutManager);
        int padding = CommonUtil.dp2px(context, 9);
        recyclerView.getRefreshableView()
                .setPadding(padding, padding / 2, padding, padding);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpData<List<ShopTheme>>> aObservable = ShopThemeApi.getThemeList();
            Observable<ShopTheme> bObservable = ShopThemeApi.getDecoration(user.getMerchantId()).onErrorReturn(

                    new Func1<Throwable, ShopTheme>() {
                        @Override
                        public ShopTheme call(Throwable throwable) {
                            return null;
                        }
                    });
            Observable observable = Observable.zip(aObservable,
                    bObservable,
                    new Func2<HljHttpData<List<ShopTheme>>, ShopTheme, ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpData<List<ShopTheme>> hljHttpData, ShopTheme object) {
                            return new ResultZip(hljHttpData, object);
                        }
                    });

            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {

                        @Override
                        public void onNext(ResultZip resultZip) {
                            if (resultZip != null) {
                                HljHttpData<List<ShopTheme>> hljHttpData = resultZip.hljHttpData;
                                if (hljHttpData != null) {
                                    List<ShopTheme> shopThemes = hljHttpData.getData();
                                    if (shopThemes != null && !shopThemes.isEmpty()) {
                                        shopThemeList.clear();
                                        shopThemeList.addAll(shopThemes);
                                        adapter.setShopThemeList(shopThemes);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                ShopTheme shopTheme = resultZip.shopTheme;
                                if (shopTheme != null) {
                                    currentTheme = shopTheme.getTheme();
                                }
                            }
                        }
                    })
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(refreshView)
                    .setPullToRefreshBase(refreshView)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    class ResultZip {
        HljHttpData<List<ShopTheme>> hljHttpData;
        ShopTheme shopTheme;

        public ResultZip(HljHttpData<List<ShopTheme>> hljHttpData, ShopTheme shopTheme) {
            this.hljHttpData = hljHttpData;
            this.shopTheme = shopTheme;
        }
    }


    class ShopThemeAdapter extends RecyclerView.Adapter<BaseViewHolder<ShopTheme>> {

        private Context context;
        private List<ShopTheme> shopThemeList;

        public void setShopThemeList(List<ShopTheme> shopThemeList) {
            this.shopThemeList = shopThemeList;
        }

        public ShopThemeAdapter(Context context, List<ShopTheme> shopThemeList) {
            this.context = context;
            this.shopThemeList = shopThemeList;
        }

        @Override
        public BaseViewHolder<ShopTheme> onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.shop_theme_item, parent, false);
            return new ShopThemeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder<ShopTheme> holder, int position) {
            holder.setView(context, shopThemeList.get(position), position, 0);
        }

        @Override
        public int getItemCount() {
            return shopThemeList == null ? 0 : shopThemeList.size();
        }

        class ShopThemeViewHolder extends BaseViewHolder<ShopTheme> {

            @BindView(R.id.iv_theme)
            ImageView ivTheme;
            @BindView(R.id.tv_title_theme)
            TextView tvTitleTheme;
            @BindView(R.id.rl_item_pic)
            RelativeLayout rlItemPic;
            @BindView(R.id.ll_use)
            LinearLayout llUse;
            private int distance;

            ShopThemeViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                Context context = itemView.getContext();
                Point point = CommonUtil.getDeviceSize(context);
                distance = (point.x - CommonUtil.dp2px(context, 36)) / 3;
                rlItemPic.getLayoutParams().width = distance;
                rlItemPic.getLayoutParams().height = distance * 3 / 2;
            }

            @Override
            protected void setViewData(
                    Context mContext, final ShopTheme shopTheme, int position, int viewType) {
                if (shopTheme != null) {
                    tvTitleTheme.setText(shopTheme.getTitle());
                    final long id = shopTheme.getId();
                    if (id == currentTheme) {
                        llUse.setVisibility(View.VISIBLE);
                    } else {
                        llUse.setVisibility(View.GONE);
                    }
                    String path = ImagePath.buildPath(shopTheme.getPath())
                            .width(distance)
                            .height(distance * 3 / 2)
                            .cropPath();
                    if (!TextUtils.isEmpty(path)) {
                        Glide.with(context)
                                .load(path)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                                        .RESOURCE))
                                .into(ivTheme);
                    } else {
                        Glide.with(context)
                                .clear(ivTheme);
                        ivTheme.setImageBitmap(null);
                    }
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ShopWebViewActivity.class);
                            intent.putExtra("title", shopTheme.getTitle());
                            intent.putExtra("path",
                                    Constants.WEB_HOST + String.format(Constants.HttpPath
                                                    .GET_THEME_PREVIEW,
                                            user.getMerchantId(),
                                            1,
                                            id,
                                            1));
                            intent.putExtra("theme", id);
                            intent.putExtra("is_select_theme", currentTheme == id);
                            intent.putExtra("type", 2);
                            startActivityForResult(intent, REQUEST_CODE);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            long theme = data.getLongExtra("theme", -1);
            if (theme != -1) {
                currentTheme = theme;
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
