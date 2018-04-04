package com.hunliji.marrybiz.fragment.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.CommentStatistics;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.TableAdapter;
import com.hunliji.marrybiz.api.comment.CommentApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/4/14.
 * 评论概述
 */

public class CommentaryFragment extends Fragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.tv_praise_percent)
    TextView tvPraisePercent;
    @BindView(R.id.ll_view)
    LinearLayout llView;
    private List<String> dataList = new ArrayList<>();
    private HljHttpSubscriber commentSubscriber;
    private TableAdapter tableAdapter;
    Unbinder unbinder;

    public static CommentaryFragment newInstance() {
        Bundle args = new Bundle();
        CommentaryFragment fragment = new CommentaryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commentary, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onLoad();
    }

    /**
     * 获取评论概述
     */
    private void onLoad() {
        if (commentSubscriber == null || commentSubscriber.isUnsubscribed()) {
            Observable<CommentStatistics> observable = CommentApi.getCommentStatistics();
            commentSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<CommentStatistics>() {
                        @Override
                        public void onNext(CommentStatistics commentStatistics) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            llView.setVisibility(View.VISIBLE);
                            showView(commentStatistics);
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(commentSubscriber);
        }
    }

    private void showView(CommentStatistics commentStatistics) {
        if (commentStatistics != null && dataList != null) {
            tvScore.setText(String.valueOf(Math.floor(commentStatistics.getScore() * 10) / 10));
            tvPraisePercent.setText(getContext().getString(R.string
                            .label_merchant_comment_good_rate,
                    Math.floor(commentStatistics.getGoodRate() * 1000) / 10));
            dataList.clear();
            dataList.add("");
            dataList.add("好评");
            dataList.add("中评");
            dataList.add("差评");
            dataList.add("总计");
            dataList.add("近30天");
            int thirtyGoodCount = commentStatistics.getThirtyGoodCount();
            int thirtyMediumCount = commentStatistics.getThirtyMediumCount();
            int thirtyBadCount = commentStatistics.getThirtyBadCount();
            int thirtyTotalCount = thirtyGoodCount + thirtyMediumCount + thirtyBadCount;
            dataList.add(String.valueOf(thirtyGoodCount));
            dataList.add(String.valueOf(thirtyMediumCount));
            dataList.add(String.valueOf(thirtyBadCount));
            dataList.add(String.valueOf(thirtyTotalCount));
            dataList.add("近90天");
            int ninetyGoodCount = commentStatistics.getNinetyGoodCount();
            int ninetyMediumCount = commentStatistics.getNinetyMediumCount();
            int ninetyBadCount = commentStatistics.getNinetyBadCount();
            int ninetyTotalCount = ninetyGoodCount + ninetyMediumCount + ninetyBadCount;
            dataList.add(String.valueOf(ninetyGoodCount));
            dataList.add(String.valueOf(ninetyMediumCount));
            dataList.add(String.valueOf(ninetyBadCount));
            dataList.add(String.valueOf(ninetyTotalCount));
            dataList.add("总计");
            int totalGoodCount = commentStatistics.getTotalGoodCount();
            int totalMediumCount = commentStatistics.getTotalMediumCount();
            int totalBadCount = commentStatistics.getTotalBadCount();
            int total = totalGoodCount + totalMediumCount + totalBadCount;
            dataList.add(String.valueOf(totalGoodCount));
            dataList.add(String.valueOf(totalMediumCount));
            dataList.add(String.valueOf(totalBadCount));
            dataList.add(String.valueOf(total));
            tableAdapter.setDataList(dataList);
            tableAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        tableAdapter = new TableAdapter(getContext(), dataList);
        recyclerView.setAdapter(tableAdapter);
        onNetError();
    }

    private void onNetError() {
        //网络异常,可点击屏幕重新加载
        emptyView.setNetworkHint2Id(com.hunliji.hljquestionanswer.R.string
                .label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(commentSubscriber);
    }


}
