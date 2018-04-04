package me.suncloud.marrymemo.view.experience;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinuscxj.pullzoom.PullZoomBaseView;
import com.dinuscxj.pullzoom.PullZoomRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.experienceshop.ShopAdapter;
import me.suncloud.marrymemo.adpter.experienceshop.ShopMeasureSize;
import me.suncloud.marrymemo.api.experience.ExperienceApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.experience.ExperienceShop;
import me.suncloud.marrymemo.model.experience.Planner;
import me.suncloud.marrymemo.model.experience.ShopResultZip;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.kefu.AdvHelperActivity;
import me.suncloud.marrymemo.view.CommentNewWorkActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

/**
 * 婚礼纪体验店入口首页
 * Created by jinxin on 2016/10/26.
 */

public class ExperienceShopActivity extends HljBaseNoBarActivity implements PullZoomBaseView
        .OnPullZoomListener {

    @BindView(R.id.zoom_recycler_view)
    PullZoomRecyclerView zoomRecyclerView;
    @BindView(R.id.shadow_view)
    ImageView shadowView;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.progressBar)
    View progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.tv_impression)
    TextView tvImpression;
    @BindView(R.id.layout_call)
    LinearLayout layoutCall;
    @BindView(R.id.layout_sms)
    LinearLayout layoutSms;
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;
    @BindView(R.id.btn_buy)
    Button btnBuy;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;

    private String TAG = "ExperienceShop";
    private ShopAdapter shopAdapter;
    private View headerView;
    private ImageView imgZoom;
    private RelativeLayout zoomLayout;

    private ShopMeasureSize shopMeasureSize;
    private City mCity;
    private long id;//杭州传1 其他的传id
    private HljHttpSubscriber subscriber;
    private LinearLayoutManager manager;
    private ShopResultZip resultZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("id", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_shop);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        initConstants();
        initHeader();
        initWidget();
        initLoad();

        initTracker();
    }


    private void initTracker() {
        HljVTTagger.buildTagger(layoutCall)
                .tagName("phone")
                .dataId(id)
                .dataType("test_store")
                .hitTag();
        HljVTTagger.buildTagger(tvImpression)
                .tagName("top_comment")
                .dataId(id)
                .dataType("test_store")
                .hitTag();
        HljVTTagger.buildTagger(commentLayout)
                .tagName("comment")
                .dataId(id)
                .dataType("test_store")
                .hitTag();
        HljVTTagger.buildTagger(layoutSms)
                .tagName("message")
                .dataId(id)
                .dataType("test_store")
                .hitTag();
        HljVTTagger.buildTagger(btnBuy)
                .tagName("appointment")
                .dataId(id)
                .dataType("test_store")
                .hitTag();
    }

    private void initConstants() {
        mCity = Session.getInstance()
                .getMyCity(this);
    }

    private void initWidget() {
        shopAdapter = new ShopAdapter(this);
        shopAdapter.setHeaderView(headerView);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        zoomRecyclerView.setLayoutManager(manager);
        zoomRecyclerView.setAdapter(shopAdapter);
        zoomRecyclerView.setIsZoomEnable(true);
        zoomRecyclerView.setModel(PullZoomBaseView.ZOOM_HEADER);
        zoomRecyclerView.setOnPullZoomListener(this);
        zoomRecyclerView.getRecyclerView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        View firstView = manager.findViewByPosition(0);
                        if (manager.findFirstVisibleItemPosition() >= 1) {
                            actionHolderLayout2.setAlpha(1);
                        } else if (firstView != null) {
                            int top = firstView.getTop();
                            int headerHeight = firstView.getHeight();
                            if (top >= headerHeight) {
                                actionHolderLayout2.setAlpha(1);
                            } else {
                                actionHolderLayout2.setAlpha(Math.min(1.0f,
                                        Math.max(0, Math.abs(top * 1.0f / headerHeight))));
                            }
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
    }

    private void initHeader() {
        shopMeasureSize = new ShopMeasureSize(this);
        headerView = getLayoutInflater().inflate(R.layout.experience_shop_item_header, null, false);
        imgZoom = (ImageView) headerView.findViewById(R.id.img_top);
        zoomLayout = (RelativeLayout) headerView.findViewById(R.id.layout_header);
        zoomLayout.getLayoutParams().height = shopMeasureSize.imgHeaderHeight;
        zoomRecyclerView.setZoomView(imgZoom);
        zoomRecyclerView.setHeaderContainer(zoomLayout);
    }


    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    @OnClick(R.id.layout_call)
    void onCall() {
        if (resultZip != null && resultZip.shop != null && resultZip.shop.getStore() != null &&
                !TextUtils.isEmpty(
                resultZip.shop.getStore()
                        .getContactPhone())) {
            callUp(Uri.parse("tel:" + resultZip.shop.getStore()
                    .getContactPhone()));
        }
    }

    @OnClick(R.id.btn_buy)
    void onBuy() {
        if (resultZip != null && resultZip.shop != null) {
            if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
                Intent intent = new Intent(this, ExperienceShopReservationActivity.class);
                intent.putExtra("store", resultZip.shop.getStore());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    @OnClick({R.id.comment_layout, R.id.tv_impression})
    void onComment() {
        if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
            Intent intent = new Intent(this, CommentNewWorkActivity.class);
            intent.putExtra("isTestStore", true);
            intent.putExtra("testStoreId", id);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.layout_sms)
    void onSms() {
        if (Util.loginBindChecked(this, Constants.Login.ADV_HELPER_LOGIN)) {
            Intent intent = new Intent(this, AdvHelperActivity.class);
            intent.putExtra(AdvHelperActivity.ARG_ADV_FROM, "xianxiadian");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onPullZooming(float newScrollValue) {
        Log.e(TAG, "onPullZooming--> " + newScrollValue);
    }

    @Override
    public void onPullStart() {
        Log.e(TAG, "onPullStart--> ");
    }

    @Override
    public void onPullZoomEnd(float newScrollValue) {
        Log.e(TAG, "onPullZoomEnd--> " + newScrollValue);
    }

    private void initLoad() {
        //banner
        Observable<PosterData> bannerObb = CommonApi.getBanner(this,
                HljCommon.BLOCK_ID.EXPERIENCE_STORE_BANNER_EXHIBITION,
                mCity.getId());
        //店铺详情
        Observable<ExperienceShop> shopObb = ExperienceApi.getStoreAtlas(id);

        //统筹师
        Observable<HljHttpData<List<Planner>>> plannerObb = ExperienceApi.getPlannerList(id, null);

        //体验店评价
        Observable<HljHttpData<List<Comment>>> comment = ExperienceApi.getComments(id);

        Observable<ShopResultZip> zipObb = Observable.zip(bannerObb,
                shopObb,
                plannerObb,
                comment,
                new Func4<PosterData, ExperienceShop, HljHttpData<List<Planner>>,
                        HljHttpData<List<Comment>>, ShopResultZip>() {


                    @Override
                    public ShopResultZip call(
                            PosterData posterData,
                            ExperienceShop experienceShop,
                            HljHttpData<List<Planner>> listPlanner,
                            HljHttpData<List<Comment>> listComment) {
                        ShopResultZip zip = new ShopResultZip();
                        zip.posterData = posterData;
                        zip.shop = experienceShop;
                        zip.planners = listPlanner;
                        zip.comments = listComment;
                        return zip;
                    }
                });

        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setContentView(zoomRecyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<ShopResultZip>() {
                    @Override
                    public void onNext(ShopResultZip resultZip) {
                        setResultZip(resultZip);
                    }
                })
                .build();

        zipObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    private void setResultZip(ShopResultZip resultZip) {
        this.resultZip = resultZip;
        shopAdapter.setResultZip(resultZip, id);
        shopAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onFinish() {
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
        super.onFinish();
    }
}
