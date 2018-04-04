package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.views.activities.NoteMarkDetailActivity;
import com.hunliji.hljnotelibrary.views.widgets.SpacesItemDecoration;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.TabPageIndicator;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我关注的标签
 * Created by jinxin on 2016/2/26.
 */
public class FollowMarkFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener {

    private final int FOLLOW_REQUEST = 101;

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private Unbinder unbinder;
    private List<Mark> markList;
    private MarkListAdapter adapter;
    private int imageWidth;
    private int imageHeight;
    private View footerView;
    private View endView;
    private View loadView;

    private Long userId;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;


    public static FollowMarkFragment newInstance(Long userId) {
        FollowMarkFragment fragment = new FollowMarkFragment();
        Bundle data = new Bundle();
        data.putLong("userId", userId);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        if (data != null) {
            userId = data.getLong("userId");
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Point point = JSONUtil.getDeviceSize(getContext());
        imageWidth = Math.round((point.x - Math.round(dm.density * 36)) * 1.0f / 2);
        imageHeight = Math.round(imageWidth * 1.0f * 3 / 4);
        markList = new ArrayList<>();
        adapter = new MarkListAdapter();
        footerView = getActivity().getLayoutInflater()
                .inflate(R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        endView.setVisibility(View.VISIBLE);
        loadView = footerView.findViewById(R.id.loading);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout
                        .hlj_common_fragment_ptr_staggered_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initWidget();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    private void initWidget() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration(getContext(), 0, 1));
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        emptyView.setHintId(R.string.label_empty_mark_followed);
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_common);
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setPullToRefreshBase(recyclerView)
                .setContentView(recyclerView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Mark>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Mark>> listHljHttpData) {
                        if (onTabTextChangeListener != null) {
                            onTabTextChangeListener.onTabTextChange(listHljHttpData.getTotalCount
                                    ());
                        }
                        markList.clear();
                        markList.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                        initPagination(listHljHttpData.getPageCount());
                    }

                })
                .build();
        NoteApi.getFollowMark(1)
                .subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            return;
        }
        Observable<HljHttpData<List<Mark>>> noteObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Mark>>>() {
                    @Override
                    public Observable<HljHttpData<List<Mark>>> onNextPage(int page) {
                        return NoteApi.getFollowMark(page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Mark>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Mark>> listHljHttpData) {
                        if (listHljHttpData == null || listHljHttpData.getData() == null) {
                            return;
                        }
                        int start = adapter.getItemCount() - (footerView != null ? 1 : 0);
                        markList.addAll(listHljHttpData.getData());
                        adapter.notifyItemRangeInserted(start,
                                listHljHttpData.getData()
                                        .size());
                    }
                })
                .build();
        noteObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FOLLOW_REQUEST && resultCode == Activity.RESULT_OK) {
            onRefresh(null);
        }
    }

    class MarkListAdapter extends RecyclerView.Adapter<BaseViewHolder<Mark>> {

        private final int ITEM_FOOTER = 11;
        private final int ITEM_ITEM = 12;

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_ITEM:
                    View itemView = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_marks, parent, false);
                    return new ViewHolder(itemView);
                case ITEM_FOOTER:
                    ExtraBaseViewHolder footerViewHolder = new ExtraBaseViewHolder(footerView);
                    StaggeredGridLayoutManager.LayoutParams params = new
                            StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setFullSpan(true);
                    footerViewHolder.itemView.setLayoutParams(params);
                    return footerViewHolder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final BaseViewHolder<Mark> holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType != ITEM_ITEM) {
                return;
            }
            final Mark mark = markList.get(position);
            holder.setView(getContext(), mark, position, viewType);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mark != null && mark.getId() > 0) {
                        Intent intent = new Intent(getContext(), NoteMarkDetailActivity.class);
                        intent.putExtra(NoteMarkDetailActivity.ARG_MARK_ID, mark.getId());
                        startActivityForResult(intent, FOLLOW_REQUEST);
                    }
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            if (position != getItemCount() - 1) {
                return ITEM_ITEM;
            } else {
                return ITEM_FOOTER;
            }
        }

        @Override
        public int getItemCount() {
            return markList.size() + (markList.isEmpty() ? 0 : 1);
        }
    }

    class ViewHolder extends BaseViewHolder<Mark> {

        @BindView(R.id.mark_image)
        RoundedImageView markImage;
        @BindView(R.id.mark_des)
        TextView markDes;
        @BindView(R.id.mark_view)
        RelativeLayout markView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Mark mark, int position, int viewType) {
            markImage.getLayoutParams().width = imageWidth;
            markImage.getLayoutParams().height = imageHeight;
            if (mark == null) {
                return;
            }
            markDes.setText(mark.getName());
            Glide.with(getContext())
                    .load(ImagePath.buildPath(mark.getImagePath())
                            .height(imageHeight)
                            .width(imageWidth)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(markImage);
        }
    }
}
