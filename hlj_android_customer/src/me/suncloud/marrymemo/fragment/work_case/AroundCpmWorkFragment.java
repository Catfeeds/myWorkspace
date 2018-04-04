package me.suncloud.marrymemo.fragment.work_case;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.AroundCpmWorkAdapter;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import me.suncloud.marrymemo.view.lvpai.AroundLvPaiActivity;


/**
 * Created by luohanlin on 2017/12/12.
 */

public class AroundCpmWorkFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener {

    public static final String CPM_SOURCE = "micro_travel";

    // 1精选2短途2远方
    public static final int TRAVEL_TYPE_FINE = 1;
    public static final int TRAVEL_TYPE_AROUND = 2;
    public static final int TRAVEL_TYPE_FARAWAY = 3;

    public static final int BG_HEIGHT = 362;
    public static final int ACTION_HOLDER_HEIGHT = 110 + 56;
    public static final int GAP_HEIGHT = 32;
    @BindView(R.id.rotation_view)
    View rotationView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.ptr_view)
    PullToRefreshVerticalRecyclerView ptrView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    private int type;

    public static final String ARG_TYPE = "type";
    private ArrayList<Work> works;
    private AroundCpmWorkAdapter adapter;
    private int rHeight;
    private int rightMargin;
    private int rWidth;
    private int leftMargin;
    private int bgWidth;
    private HljHttpSubscriber initSub;

    private Unbinder unbinder;

    public static AroundCpmWorkFragment newInstance(int type) {
        Bundle args = new Bundle();
        AroundCpmWorkFragment fragment = new AroundCpmWorkFragment();
        args.putInt(ARG_TYPE, type);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    private void initValues() {
        works = new ArrayList<>();
        adapter = new AroundCpmWorkAdapter(getContext(), works);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE, TRAVEL_TYPE_FINE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_around_cpm_works, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initTracker();
        return view;
    }

    private void initTracker() {
        switch (type) {
            case TRAVEL_TYPE_FINE:
                HljVTTagger.tagViewParentName(ptrView, "daily_choice_list");
                break;
            case TRAVEL_TYPE_AROUND:
                HljVTTagger.tagViewParentName(ptrView, "short_tour_list");
                break;
            case TRAVEL_TYPE_FARAWAY:
                HljVTTagger.tagViewParentName(ptrView, "long_tour_list");
                break;
        }
    }

    private void initViews() {
        initRotationViews();
        ptrView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        ptrView.getRefreshableView()
                .setAdapter(adapter);
        ptrView.setOnRefreshListener(this);
        ptrView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        rotateViews(Math.abs(recyclerView.computeVerticalScrollOffset()));
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
    }

    private void initRotationViews() {
        bgWidth = CommonUtil.getDeviceSize(getContext()).x;
        int bgHeight = CommonUtil.dp2px(getContext(), BG_HEIGHT);
        int actionLayoutHolderHeight = CommonUtil.dp2px(getContext(),
                ACTION_HOLDER_HEIGHT) + getAroundCpmActivity().getStatusBarHeight();
        int gapHeight = CommonUtil.dp2px(getContext(), GAP_HEIGHT);

        rHeight = bgHeight - actionLayoutHolderHeight;
        rightMargin = bgWidth * gapHeight / (rHeight - gapHeight);
        rWidth = (int) Math.hypot(bgWidth + rightMargin, rHeight);
        leftMargin = rWidth - bgWidth - rightMargin;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rotationView
                .getLayoutParams();
        params.width = rWidth + 100;
        params.height = rHeight + 300;
        params.setMargins(-(leftMargin + 100), 0, -(rightMargin), 0);
        rotationView.setLayoutParams(params);

        rotateViews(0);
    }

    private void rotateViews(int offsetHeight) {
        offsetHeight = Math.min(rHeight, offsetHeight);
        rotationView.setPivotY(0);
        rotationView.setPivotX(rWidth);

        double b = (double) (rHeight - offsetHeight) / (double) (rWidth - leftMargin);
        double r = Math.atan(b);
        //        double a = (double) (rHeight - offsetHeight) / rWidth;
        //        double d = Math.asin(a) * 180 / Math.PI;
        double d = Math.toDegrees(r);

        rotationView.setRotation(-(float) d);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        refresh();
    }

    @Override
    public void refresh(Object... params) {
        CommonUtil.unSubscribeSubs(initSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(ptrView.isRefreshing() ? null : progressBar)
                .setEmptyView(emptyView)
                .setPullToRefreshBase(ptrView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        works.clear();
                        works.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        WorkApi.getMicroTravelList(type)
                .subscribe(initSub);
    }

    @Override
    public View getScrollableView() {
        return ptrView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(initSub);
    }

    private AroundLvPaiActivity getAroundCpmActivity() {
        AroundLvPaiActivity activity = null;
        if (getActivity() instanceof AroundLvPaiActivity) {
            activity = (AroundLvPaiActivity) getActivity();
        }

        return activity;
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        refresh();
    }
}
