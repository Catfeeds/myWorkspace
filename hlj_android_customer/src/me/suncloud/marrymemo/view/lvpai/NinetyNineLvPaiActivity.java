package me.suncloud.marrymemo.view.lvpai;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.NinetyNineWorkAdapter;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

public class NinetyNineLvPaiActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener {

    @Override
    public String pageTrackTagName() {
        return "9.9元闪购";
    }
    public static final String CPM_SOURCE_IN_PAGE = "nine_dot_nine_lv_pai";

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_msg_p)
    ImageButton imgMsgP;
    @BindView(R.id.msg_notice_view_p)
    View msgNoticeViewP;
    @BindView(R.id.tv_msg_count_p)
    TextView tvMsgCountP;
    @BindView(R.id.msg_layout_p)
    ConstraintLayout msgLayoutP;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private City city;
    private ArrayList<Work> works;
    private HljHttpSubscriber bannerSub;
    private HljHttpSubscriber listSub;
    private NinetyNineWorkAdapter adapter;
    private NoticeUtil noticeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ninety_nine_lv_pai);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }


    private void initValues() {
        works = new ArrayList<>();
        city = Session.getInstance()
                .getMyCity(this);
        adapter = new NinetyNineWorkAdapter(this, works);
    }

    private void initViews() {
        tvTitle.setText("9.9元闪购");
        noticeUtil = new NoticeUtil(this, tvMsgCountP, msgNoticeViewP);
        noticeUtil.onResume();
        setDefaultStatusBarPadding();
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);
    }

    private void initLoad() {
        bannerSub = HljHttpSubscriber.buildSubscriber(this)
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<PosterData>() {
                    @Override
                    public void onNext(PosterData posterData) {
                        List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                                Constants.POST_SITES.SITE_GRAB_TRAVEL,
                                false);
                        setBannerImage(posters);
                    }
                })
                .build();
        CommonApi.getBanner(this, HljCommon.BLOCK_ID.NINETY_NINE_LV_PAI_BANNER, city.getId())
                .subscribe(bannerSub);
        listSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        works.clear();
                        works.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        WorkApi.getNinetyNineWorks()
                .subscribe(listSub);
    }

    private void setBannerImage(List<Poster> posterList) {
        if (CommonUtil.isCollectionEmpty(posterList)) {
            adapter.setBanner(null);
        } else {
            final Poster poster = posterList.get(0);
            adapter.setBanner(poster);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.img_msg_p)
    void onMessage() {
        Intent intent = new Intent(this, MessageHomeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(bannerSub, listSub);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initLoad();
    }
}
