package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPraisedAuthor;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.communitythreads.CommunityThreadApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 话题点赞用户列表
 */
public class ThreadPraisedUsersActivity extends HljBaseActivity implements AdapterView
        .OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>, ObjectBindAdapter
        .ViewBinder<CommunityPraisedAuthor> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private long postId;
    private ArrayList<CommunityPraisedAuthor> authors;
    private ObjectBindAdapter<CommunityPraisedAuthor> adapter;
    private int logoSize;
    private View endView;
    private View loadView;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber refreshSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_praised_users);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        postId = getIntent().getLongExtra("id", 0);
        authors = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        logoSize = Math.round(40 * dm.density);
    }

    private void initViews() {
        View footerView = View.inflate(this,
                com.hunliji.hljcommonlibrary.R.layout.hlj_foot_no_more___cm,
                null);
        endView = footerView.findViewById(com.hunliji.hljcommonlibrary.R.id.no_more_hint);
        loadView = footerView.findViewById(com.hunliji.hljcommonlibrary.R.id.loading);
        loadView.setVisibility(View.INVISIBLE);

        adapter = new ObjectBindAdapter<>(this, authors, R.layout.thread_praised_user_item, this);

        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
    }

    private void initLoad() {
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityPraisedAuthor>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CommunityPraisedAuthor>> listHljHttpData) {
                        authors.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                        initAutoPaging(listHljHttpData.getPageCount());
                    }
                })
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setListView(listView.getRefreshableView())
                .build();

        CommunityThreadApi.getPraisedAuthorsObb(postId, 1)
                .subscribe(initSubscriber);
    }

    private void initAutoPaging(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        Observable<HljHttpData<List<CommunityPraisedAuthor>>> observable = PaginationTool
                .buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CommunityPraisedAuthor>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityPraisedAuthor>>> onNextPage(int page) {
                        return CommunityThreadApi.getPraisedAuthorsObb(postId, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityPraisedAuthor>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CommunityPraisedAuthor>> listHljHttpData) {
                        authors.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void setViewValue(
            View view, CommunityPraisedAuthor praisedAuthor, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Author author = praisedAuthor.getAuthor();
        if (author != null) {
            if (!TextUtils.isEmpty(praisedAuthor.getAuthor()
                    .getAvatar())) {
                String url = ImageUtil.getImagePath(author.getAvatar(), logoSize);
                Glide.with(ThreadPraisedUsersActivity.this)
                        .load(url)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(holder.praisedUserLogo);
            }
            holder.tvPraisedUserName.setText(author.getName());
        }

        holder.tvPraisedTime.setText(DateUtils.getRelativeTimeSpanString(praisedAuthor
                        .getCreatedAt()
                        .getMillis(),
                Calendar.getInstance()
                        .getTimeInMillis(),
                DateUtils.MINUTE_IN_MILLIS));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommunityPraisedAuthor author = (CommunityPraisedAuthor) parent.getAdapter()
                .getItem(position);
        if (author != null) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("id",
                    author.getAuthor()
                            .getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityPraisedAuthor>>>() {
                        @Override
                        public void onNext(
                                HljHttpData<List<CommunityPraisedAuthor>> listHljHttpData) {
                            listView.onRefreshComplete();
                            authors.clear();
                            authors.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initAutoPaging(listHljHttpData.getPageCount());
                        }
                    })
                    .build();

            CommunityThreadApi.getPraisedAuthorsObb(postId, 1)
                    .distinctUntilChanged()
                    .subscribe(refreshSubscriber);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            initSubscriber.unsubscribe();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
    }

    static class ViewHolder {
        @BindView(R.id.praised_user_logo)
        RoundedImageView praisedUserLogo;
        @BindView(R.id.tv_praised_user_name)
        TextView tvPraisedUserName;
        @BindView(R.id.tv_praised_time)
        TextView tvPraisedTime;
        @BindView(R.id.bottom_line)
        View bottomLine;
        @BindView(R.id.ll_praised_user_view)
        LinearLayout llPraisedUserView;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
