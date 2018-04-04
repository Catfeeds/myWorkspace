package com.hunliji.hljcarlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.WeddingCarSecKillAdapter;
import com.hunliji.hljcarlibrary.api.WeddingCarApi;
import com.hunliji.hljcarlibrary.models.HljCarHttpData;
import com.hunliji.hljcarlibrary.models.SecKill;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackWeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 特价秒杀
 * Created by jinxin on 2018/1/3 0003.
 */

public class WeddingCarSecKillActivity extends HljBaseNoBarActivity implements
        OnItemClickListener<SecKill>, WeddingCarSecKillAdapter.onWeddingCarSecKillAdapterListener {

    @Override
    public String pageTrackTagName() {
        return "婚车特价秒杀";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra(CITY_ID, 0);
        return new VTMetaData(id, VTMetaData.DATA_TYPE.DATA_TYPE_CITY);
    }

    public static final String CITY_ID = "city_id";

    @BindView(R2.id.tv_limit_hour)
    TextView tvLimitHour;
    @BindView(R2.id.tv_limit_minute)
    TextView tvLimitMinute;
    @BindView(R2.id.tv_limit_second)
    TextView tvLimitSecond;
    @BindView(R2.id.layout_time)
    LinearLayout layoutTime;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private Handler handler;
    private HljHttpSubscriber loadSub;
    private long cityId;
    private long countDownTime;
    private WeddingCarSecKillAdapter carSecKillAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_car_sec_kill___car);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        if (getIntent() != null) {
            cityId = getIntent().getLongExtra(CITY_ID, 0L);
        }
        handler = new Handler();
        carSecKillAdapter = new WeddingCarSecKillAdapter(this);
        carSecKillAdapter.setOnItemClickListener(this);
        carSecKillAdapter.setOnWeddingCarSecKillAdapterListener(this);
    }

    private void initWidget() {
        setDefaultStatusBarPadding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setAdapter(carSecKillAdapter);
    }

    private void initLoad() {
        loadSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<HljCarHttpData<List<SecKill>>>() {
                    @Override
                    public void onNext(HljCarHttpData<List<SecKill>> listHljCarHttpData) {
                        setHttpData(listHljCarHttpData);
                    }
                })
                .build();
        WeddingCarApi.getSecKillsObb(cityId, Integer.MAX_VALUE, 1)
                .subscribe(loadSub);
    }

    private void setHttpData(HljCarHttpData<List<SecKill>> resultData) {
        if (resultData == null) {
            return;
        }
        countDownTime = resultData.getCountDownTime();
        handler.post(countTimeRunnable);
        carSecKillAdapter.setSecKillList(resultData.getData());
    }

    private Runnable countTimeRunnable = new Runnable() {
        public void run() {
            long timeDistance = (countDownTime - HljTimeUtils.getServerCurrentTimeMillis()) / 1000;
            if (timeDistance > 0) {
                // 还在倒计时
                int nextHour;
                int nextMinute;
                int nextSecond;
                nextHour = (int) (timeDistance / (60 * 60));
                timeDistance = timeDistance - (nextHour * 60 * 60);
                nextMinute = (int) (timeDistance / (60));
                timeDistance = timeDistance - (nextMinute * 60);
                nextSecond = (int) timeDistance;
                tvLimitHour.setText(nextHour >= 10 ? String.valueOf(nextHour) : "0" + nextHour);
                tvLimitMinute.setText(nextMinute >= 10 ? String.valueOf(nextMinute) : "0" +
                        nextMinute);
                tvLimitSecond.setText(nextSecond >= 10 ? String.valueOf(nextSecond) : "0" +
                        nextSecond);
                handler.postDelayed(this, 1000);
            } else {
                handler.removeCallbacks(this);
            }
        }
    };

    @OnClick(R2.id.btn_back)
    void onBack(){
        super.onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSub);
    }

    @Override
    public void onItemClick(int position, SecKill secKill) {
        if (secKill == null || secKill.getExtraData() == null) {
            return;
        }
        Intent intent = new Intent(this, WeddingCarProductDetailActivity.class);
        intent.putExtra(WeddingCarProductDetailActivity.ARG_ID,
                secKill.getExtraData()
                        .getId());
        startActivity(intent);
    }

    @Override
    public void onBtnNow(SecKill secKill) {
        if (secKill == null || secKill.getExtraData() == null) {
            return;
        }
        Photo photo = new Photo();
        photo.setImagePath(secKill.getImg());
        List<Photo> list = new ArrayList<>();
        list.add(photo);
        secKill.getExtraData()
                .setHeaderPhotos(list);

        WeddingCarProduct carProduct = secKill.getExtraData();
        if (carProduct == null) {
            return;
        }
        WSTrack wsTrack = new WSTrack("发起咨询页");
        TrackWeddingCarProduct trackWeddingCarProduct = new TrackWeddingCarProduct(carProduct);
        wsTrack.setAction(WSTrack.WEDDING_CAR);
        wsTrack.setCarProduct(trackWeddingCarProduct);

        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                        .WS_CUSTOMER_CHAT_ACTIVITY)
                .withLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                        carProduct.getMerchantComment()
                                .getUserId())
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_WS_TRACK, wsTrack)
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_CITY,
                        carProduct.getCity())
                .navigation(this);

    }
}
