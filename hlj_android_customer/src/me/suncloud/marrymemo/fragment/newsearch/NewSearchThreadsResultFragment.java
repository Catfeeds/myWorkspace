package me.suncloud.marrymemo.fragment.newsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpSearch;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.NewSearchThreadResultAdapter;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.community.CreatePostActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/1/8.
 * 话题、帖子结果页
 */

public class NewSearchThreadsResultFragment extends NewBaseSearchResultFragment implements
        OnItemClickListener<CommunityThread> {
    private ArrayList<CommunityThread> threads = new ArrayList<>();
    private NewSearchThreadResultAdapter adapter;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private LinearLayoutManager layoutManager;

    public static NewSearchThreadsResultFragment newInstance(
            Bundle args) {
        NewSearchThreadsResultFragment fragment = new NewSearchThreadsResultFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initViews() {
        super.initViews();

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);

        adapter = new NewSearchThreadResultAdapter(getContext(), threads);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        adapter.setOnReplyItemClickListener(new NewSearchThreadResultAdapter
                .OnReplyItemClickListener() {
            @Override
            public void onReply(CommunityThread item, int position) {
                if (!AuthUtil.loginBindCheck(getContext())) {
                    return;
                }
                int realIndex = threads.indexOf(item);
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                intent.putExtra(CreatePostActivity.ARG_POST, item.getPost());
                intent.putExtra(CreatePostActivity.ARG_POSITION, realIndex);
                intent.putExtra(CreatePostActivity.ARG_IS_REPLY_THREAD, true);
                startActivityForResult(intent, 1);
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.slide_in_from_bottom,
                            R.anim.activity_anim_default);
                }
            }
        });
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    protected void initLoad() {
        super.initLoad();
        clearData(adapter);
        CommonUtil.unSubscribeSubs(initSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<CommunityThread>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<CommunityThread>> httpSearch) {
                        threads.addAll(httpSearch.getData());
                        initPage(httpSearch.getPageCount());
                        adapter.setData(threads);
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                        recyclerView.getRefreshableView()
                                .setPadding(0, CommonUtil.dp2px(getContext(), 10), 0, 0);
                    }
                })
                .build();
        NewSearchApi.getThreadsList(keyword, searchType, 1)
                .subscribe(initSub);
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpSearch<List<CommunityThread>>> pageObb = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpSearch<List<CommunityThread>>>() {
                    @Override
                    public Observable<HljHttpSearch<List<CommunityThread>>> onNextPage(int page) {
                        return NewSearchApi.getThreadsList(keyword, searchType, page);
                    }
                })
                .setLoadView(footerViewHolder.loading)
                .setEndView(footerViewHolder.noMoreHint)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<CommunityThread>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<CommunityThread>> hljHttpSearch) {
                        adapter.addData(hljHttpSearch.getData());
                    }
                })
                .build();
        pageObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(initSub, pageSub);
    }

    @Override
    protected void resetFilterView() {
        if (getSearchActivity() != null) {
            getSearchActivity().setFilterView(null);
        }
    }

    @Override
    public void onItemClick(int position, CommunityThread thread) {
        if (thread != null) {
            Intent intent = new Intent(getContext(), CommunityThreadDetailActivity.class);
            intent.putExtra(CommunityThreadDetailActivity.ARG_ID, thread.getId());
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra(CreatePostActivity.ARG_POSITION, 0);
            CommunityThread thread = threads.get(position);
            if (thread != null) {
                thread.setPostCount(thread.getPostCount() + 1);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
