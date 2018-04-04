package me.suncloud.marrymemo.view.marry;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.MarryTaskAdapter;
import me.suncloud.marrymemo.adpter.marry.viewholder.OnMarryTaskClickListener;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.marry.MarryTask;
import me.suncloud.marrymemo.model.marry.TaskSort;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;

/**
 * Created by hua_rong on 2017/11/7
 * 结婚任务
 */

public class MarryTaskActivity extends HljBaseNoBarActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView>,
        OnMarryTaskClickListener {

    @Override
    public String pageTrackTagName() {
        return "结婚任务";
    }

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.action_layout_holder)
    LinearLayout actionLayoutHolder;

    private View headerView;
    private HeaderViewHolder viewHolder;
    private static final int REQUEST_CODE = 0x200;
    private HljHttpSubscriber httpSubscriber;
    private MarryTaskAdapter adapter;
    private int headerHeight;
    private HljHttpSubscriber completeSubscriber;
    private boolean taskBind;
    private User user;
    private int totalCount;
    private int completeCount;
    private static final String TASK_KEY = "marry_task_bind";
    private boolean isInit;
    private HljHttpData<List<MarryTask>> httpData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marry_task);
        ButterKnife.bind(this);
        initValue();
        initError();
        initHeader();
        initView();
        onRefresh(recyclerView);
    }

    private void initValue() {
        user = Session.getInstance()
                .getCurrentUser(this);
        taskBind = user.getPartnerUid() > 0;
    }


    private void initView() {
        setActionBarPadding(actionLayoutHolder, viewHolder.llMarryTask);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        actionLayoutHolder.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        actionLayoutHolder.getBackground()
                .setAlpha(0);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setPadding(0, 0, 0, CommonUtil.dp2px(this, 20));
        adapter = new MarryTaskAdapter(this);
        adapter.setHeaderView(headerView);
        adapter.setOnMarryTaskListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(linearLayoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                                .getLayoutManager();
                        int position = layoutManager.findFirstVisibleItemPosition();
                        View firstVisibleChildView = layoutManager.findViewByPosition(position);
                        int itemHeight = firstVisibleChildView.getHeight();
                        int distance;
                        if (position == 0) {
                            headerHeight = itemHeight;
                            distance = -firstVisibleChildView.getTop();
                        } else {
                            distance = headerHeight + (position - 1) * itemHeight -
                                    firstVisibleChildView.getTop();
                        }
                        int height = CommonUtil.dp2px(getApplicationContext(), 84);
                        if (distance <= height) {
                            tvTitle.setAlpha(0);
                            actionLayoutHolder.getBackground()
                                    .setAlpha((int) ((distance * 1.0 / height) * 255));
                        } else {
                            tvTitle.setAlpha(Math.min((distance - height) / 30.0f, 1));
                            actionLayoutHolder.getBackground()
                                    .setAlpha(255);
                        }
                    }
                });
    }

    @OnClick(R.id.btn_record_task)
    void onRecordTask(View view) {
        Intent intent = new Intent(this, MarryTaskEditActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_up_to_top, R.anim.activity_anim_default);
    }

    private void initHeader() {
        headerView = View.inflate(this, R.layout.marry_task_header, null);
        viewHolder = new HeaderViewHolder(headerView);
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    @OnClick(R.id.iv_back)
    void onBackPress() {
        super.onBackPressed();
    }


    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(httpSubscriber);
        httpSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<MarryTask>>>() {
                    @Override
                    public void onNext(HljHttpData<List<MarryTask>> listHljHttpData) {
                        httpData = listHljHttpData;
                        completeCount = 0;
                        totalCount = listHljHttpData.getTotalCount();
                        List<MarryTask> marryTasks = getMarryTasks(listHljHttpData.getData());
                        for (MarryTask marryTask : marryTasks) {
                            if (marryTask.getStatus() == 1) {
                                completeCount++;
                            }
                        }
                        viewHolder.setHeaderView();
                        adapter.setMarryTasks(marryTasks);
                    }
                })
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .build();
        MarryApi.getMarryTasks(null)
                .subscribe(httpSubscriber);
    }

    private List<MarryTask> getMarryTasks(List<MarryTask> marryTasks) {
        List<TaskSort> systemTasks = new ArrayList<>();
        List<TaskSort> taskSorts = new ArrayList<>();
        TaskSort completed = new TaskSort(getString(R.string.label_task_finished));
        TaskSort recent = new TaskSort(getString(R.string.label_task_recent));
        TaskSort remote = new TaskSort(getString(R.string.label_task_remote));
        TaskSort unexpected = new TaskSort(getString(R.string.label_task_remote));
        for (MarryTask marryTask : marryTasks) {
            //0未完成 1已完成
            if (marryTask.getStatus() == 1) {
                marryTask.setType(MarryTask.TYPE_COMPLETED);
                completed.getMarryTasks()
                        .add(marryTask);
            } else {
                if (marryTask.getTemplateId() != null) {
                    //系统任务
                    marryTask.setType(MarryTask.TYPE_SYSTEM);
                    boolean isContained = false;
                    for (TaskSort sort : systemTasks) {
                        if (!TextUtils.isEmpty(sort.getTitle()) && sort.getTitle()
                                .equals(marryTask.getCategory()
                                        .getTitle())) {
                            sort.getMarryTasks()
                                    .add(marryTask);
                            isContained = true;
                            break;
                        }
                    }
                    if (!isContained) {
                        systemTasks.add(getMarrySort(marryTask));
                    }
                } else {
                    if (marryTask.getExpireAt() != null) {
                        DateTime dateTime = marryTask.getExpireAt();
                        long expireAt = dateTime.getMillis();
                        long currentMillis = Calendar.getInstance(Locale.getDefault())
                                .getTimeInMillis();
                        if ((dateTime.minusDays(7)
                                .getMillis() < currentMillis)) {
                            //七天内任务
                            marryTask.setType(expireAt < currentMillis ? MarryTask.TYPE_EXPIRED :
                                    MarryTask.TYPE_RECENT);
                            recent.getMarryTasks()
                                    .add(marryTask);
                        } else {
                            marryTask.setType(MarryTask.TYPE_REMOTE);
                            remote.getMarryTasks()
                                    .add(marryTask);
                        }
                    } else {
                        marryTask.setType(MarryTask.TYPE_REMOTE);
                        unexpected.getMarryTasks()
                                .add(marryTask);
                    }
                }
            }
        }
        sortMarryTask(recent);
        sortMarryTask(remote);
        Collections.reverse(systemTasks);
        sortSystemsTask(systemTasks);
        sortCompleteTask(completed);
        taskSorts.add(recent);
        if (!CommonUtil.isCollectionEmpty(unexpected.getMarryTasks())) {
            remote.getMarryTasks()
                    .addAll(unexpected.getMarryTasks());
        }
        taskSorts.add(remote);
        taskSorts.addAll(systemTasks);
        taskSorts.add(completed);
        return reDealMarryTask(taskSorts);
    }

    private List<MarryTask> reDealMarryTask(List<TaskSort> taskSorts) {
        List<MarryTask> marryTasks = new ArrayList<>();
        for (TaskSort taskSort : taskSorts) {
            if (!CommonUtil.isCollectionEmpty(taskSort.getMarryTasks())) {
                MarryTask marryTask = new MarryTask();
                marryTask.setTitle(taskSort.getTitle());
                marryTask.setGroup(true);
                marryTasks.add(marryTask);
                List<MarryTask> list = taskSort.getMarryTasks();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i)
                            .setLastLine(false);
                    if (i == list.size() - 1) {
                        list.get(i)
                                .setLastLine(true);
                        break;
                    }
                }
                marryTasks.addAll(list);
            }
        }
        return marryTasks;
    }

    private void sortCompleteTask(TaskSort completed) {
        //按任务完成时间倒序排序
        Collections.sort(completed.getMarryTasks(), new Comparator<MarryTask>() {
            @Override
            public int compare(MarryTask o1, MarryTask o2) {
                if (o1.getCompletedAt() == null) {
                    return -1;
                }
                if (o2.getCompletedAt() == null) {
                    return 1;
                }
                return o2.getCompletedAt()
                        .compareTo(o1.getCompletedAt());
            }
        });
    }

    private void sortSystemsTask(List<TaskSort> systemTasks) {
        for (TaskSort taskSort : systemTasks) {
            Collections.sort(taskSort.getMarryTasks(), new Comparator<MarryTask>() {
                @Override
                public int compare(MarryTask o1, MarryTask o2) {
                    if (o1.getTemplateId() == null || o2.getTemplateId() == null) {
                        return 0;
                    }
                    return o1.getTemplateId()
                            .compareTo(o2.getTemplateId());
                }
            });
        }
    }

    private void sortMarryTask(TaskSort taskSort) {
        Collections.sort(taskSort.getMarryTasks(), new Comparator<MarryTask>() {
            @Override
            public int compare(MarryTask o1, MarryTask o2) {
                if (o1.getExpireAt() == null || o1.getExpireAt() == null) {
                    return 0;
                }
                return o1.getExpireAt()
                        .compareTo(o2.getExpireAt());
            }
        });
    }


    private TaskSort getMarrySort(MarryTask marryTask) {
        TaskSort taskSort = new TaskSort();
        taskSort.setTitle(marryTask.getCategory()
                .getTitle());
        List<MarryTask> marryBooks = taskSort.getMarryTasks();
        marryBooks.add(marryTask);
        taskSort.setMarryTasks(marryBooks);
        return taskSort;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            onRefresh(recyclerView);
        }
    }


    @Override
    public void onMarryTaskCheck(MarryTask marryTask) {
        int status = marryTask.getStatus() == 1 ? 0 : 1;
        completeSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .build();
        MarryApi.checkTask(marryTask.getId(), status)
                .subscribe(completeSubscriber);
        if (httpData != null) {
            List<MarryTask> marryTasks = httpData.getData();
            if (!CommonUtil.isCollectionEmpty(marryTasks)) {
                for (MarryTask task : marryTasks) {
                    if (task.getId() == marryTask.getId()) {
                        if (marryTask.getStatus() == 0) {
                            task.setStatus(1);
                            task.setCompletedAt(new DateTime());
                            completeCount++;
                        } else if (marryTask.getStatus() == 1) {
                            task.setStatus(0);
                            completeCount--;
                        }
                        viewHolder.setHeaderView();
                        adapter.setMarryTasks(getMarryTasks(marryTasks));
                    }
                }
            }
        }
    }

    @Override
    public void onMarryTaskItemClick(MarryTask marryTask) {
        Intent intent = new Intent(this, MarryTaskEditActivity.class);
        intent.putExtra(MarryTaskEditActivity.ARG_MARRY_TASK, marryTask);
        startActivityForResult(intent, REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_up_to_top, R.anim.activity_anim_default);
    }

    public class HeaderViewHolder {
        @BindView(R.id.tv_progress)
        TextView tvProgress;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.ll_marry_task)
        LinearLayout llMarryTask;
        @BindView(R.id.tv_tip_content)
        TextView tvTipContent;
        @BindView(R.id.tv_get)
        TextView tvGet;
        @BindView(R.id.rl_tips)
        RelativeLayout rlTips;
        @BindView(R.id.rl_header)
        RelativeLayout rlHeader;
        @BindView(R.id.progress_view)
        View progressView;

        private Context context;
        private ValueAnimator weddingDateCountAnimator;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            context = view.getContext();
            if (SPUtils.getBoolean(context, TASK_KEY + user.getId(), false)) {
                rlTips.setVisibility(View.GONE);
            } else {
                rlTips.setVisibility(View.VISIBLE);
            }
        }


        public void setHeaderView() {
            //任务已与对方实时同步，快更新结婚进度吧～ GET
            rlHeader.setVisibility(View.VISIBLE);
            int x = CommonUtil.getDeviceSize(context).x;
            int width = CommonUtil.dp2px(context, 50);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressView
                    .getLayoutParams();
            params.width = Math.round((x - width) * completeCount / totalCount);
            progressView.setLayoutParams(params);
            if (totalCount == completeCount) {
                tvProgress.setText(R.string.label_congratulations_on_all_the_tasks);
            } else {
                tvProgress.setText(String.format("已完成%1$s/%2$s", completeCount, totalCount));
            }
            if (taskBind) {//已经绑定
                tvTipContent.setText(R.string.hint_task_bond_with_spouse);
                tvGet.setText(R.string.label_get);
            } else {
                tvTipContent.setText(R.string.hint_to_bond_with_spouse_task);
                tvGet.setText(R.string.label_to_bind_partner);
            }
            if (!isInit) {
                isInit = true;
                showWeddingDateCount();
            }
        }

        private void showWeddingDateCount() {
            if (user != null && user.getId() > 0) {
                int days = 0;
                Date date = user.getWeddingDay();
                if (date != null) {
                    DateTime nowTime = new DateTime();
                    DateTime weddingDate = new DateTime(date.getTime());
                    days = Days.daysBetween(nowTime.toLocalDate(), weddingDate.toLocalDate())
                            .getDays();
                }
                days = Math.min(Math.max(days, 0), 999);
                weddingDateCountAnimator = ObjectAnimator.ofInt(0, days);
                weddingDateCountAnimator.setDuration(3 * days);
                weddingDateCountAnimator.addUpdateListener(new ValueAnimator
                        .AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        tvContent.setText(getString(R.string.label_days_away_from_the_wedding,
                                value));
                    }
                });
                weddingDateCountAnimator.start();
            }
        }

        @OnClick(R.id.tv_get)
        void onBindGet(View view) {
            if (taskBind) {
                SPUtils.put(context, TASK_KEY + user.getId(), true);
                rlTips.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            } else {
                Intent intent = new Intent(view.getContext(), BindingPartnerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                rlTips.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(httpSubscriber, completeSubscriber);
    }

}
