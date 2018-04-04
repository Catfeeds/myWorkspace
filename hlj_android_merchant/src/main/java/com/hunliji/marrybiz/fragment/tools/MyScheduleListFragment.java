package com.hunliji.marrybiz.fragment.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.tools.ToolsApi;
import com.hunliji.marrybiz.model.NewOrder;
import com.hunliji.marrybiz.model.tools.Schedule;
import com.hunliji.marrybiz.model.wrapper.HljHttpSchedulesData;
import com.hunliji.marrybiz.util.TimeUtil;
import com.hunliji.marrybiz.view.tools.CreateScheduleActivity;
import com.hunliji.marrybiz.view.tools.MyScheduleListActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的全部档期列表
 * Created by chen_bin on 2016/10/24 0024.
 */

public class MyScheduleListFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<Schedule>, PullToRefreshBase.OnRefreshListener<ListView>, AdapterView
        .OnItemClickListener {
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View footerView;
    private View endView;
    private View loadView;
    private ObjectBindAdapter<Schedule> adapter;
    private ArrayList<Schedule> schedules;
    private Unbinder unbinder;
    private String[] times;
    private boolean isNeedRefresh;
    private int todo;
    private int property;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    public static MyScheduleListFragment getInstance(int todo) {
        MyScheduleListFragment fragment = new MyScheduleListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("todo", todo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            todo = getArguments().getInt("todo");
        }
        schedules = new ArrayList<>();
        times = new String[]{getString(R.string.label_morning), getString(R.string
                .label_luncheon), getString(
                R.string.label_afternoon), getString(R.string.label_dinner), getString(R.string
                .label_night)};
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ObjectBindAdapter<>(getContext(),
                schedules,
                R.layout.my_schedule_list_item,
                this);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_list_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpSchedulesData>() {
                        @Override
                        public void onNext(HljHttpSchedulesData listHljHttpData) {
                            listView.getRefreshableView()
                                    .setSelection(0);
                            property = listHljHttpData.getProperty();
                            schedules.clear();
                            schedules.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(listView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(listView)
                    .setListView(listView.getRefreshableView())
                    .build();
            ToolsApi.getScheduleListByToDoObb(todo, 1, 20)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpSchedulesData> observable = PaginationTool.buildPagingObservable
                (listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpSchedulesData>() {
                    @Override
                    public Observable<HljHttpSchedulesData> onNextPage(int page) {
                        return ToolsApi.getScheduleListByToDoObb(todo, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSchedulesData>() {
                    @Override
                    public void onNext(HljHttpSchedulesData listHljHttpData) {
                        schedules.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    class ViewHolder {
        @BindView(R.id.top_line_layout)
        View topLineLayout;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_phone)
        TextView tvPhone;
        @BindView(R.id.message_layout)
        LinearLayout messageLayout;
        @BindView(R.id.tv_message)
        TextView tvMessage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void setViewValue(View view, Schedule schedule, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.topLineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.tvDate.setVisibility(View.GONE);
        if (schedule.getDate() != null && (position == 0 || !TimeUtil.isSameDay(schedules.get(
                position - 1)
                .getDate(), schedule.getDate()))) {
            holder.topLineLayout.setVisibility(View.GONE);
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(schedule.getDate()
                    .toString(getString(R.string.format_date_type14)));
        }
        String userName;
        String phone;
        //类型 ： 0 自定义日程
        if (schedule.getType() == Schedule.TYPE_SCHEDULE) {
            holder.tvType.setBackgroundResource(R.drawable.sp_r2_color_ffa73c);
            holder.tvType.setText(R.string.label_schedule);
            userName = schedule.getUserName();
            phone = schedule.getPhone();
        }
        //1 订单
        else if (schedule.getType() == Schedule.TYPE_ORDER) {
            holder.tvType.setBackgroundResource(R.drawable.sp_r2_color_d2a1f7);
            holder.tvType.setText(R.string.label_order);
            NewOrder order = schedule.getOrder();
            userName = order.getBuyerRealName();
            phone = order.getBuyerPhone();
        }
        //2 自定义套餐
        else {
            holder.tvType.setBackgroundResource(R.drawable.sp_r2_primary);
            holder.tvType.setText(R.string.label_reservation2);
            userName = schedule.getUserName();
            phone = schedule.getPhone();
        }
        //姓名
        if (TextUtils.isEmpty(userName)) {
            holder.tvUserName.setText(R.string.label_unfilled);
            holder.tvUserName.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        } else {
            holder.tvUserName.setText(userName);
            holder.tvUserName.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorBlack2));
        }
        //电话
        if (TextUtils.isEmpty(phone)) {
            holder.tvPhone.setText(R.string.label_unfilled);
            holder.tvPhone.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        } else {
            holder.tvPhone.setText(phone);
            holder.tvPhone.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
        }
        //时间
        String time = null;
        if (schedule.getType() == 2 || property == 0) {
            time = schedule.getTime() == null ? "" : schedule.getTime()
                    .toString(getString(R.string.format_date_type13));
        } else if (!TextUtils.isEmpty(schedule.getTimeStr())) {
            StringBuilder sb = new StringBuilder();
            String[] strings = schedule.getTimeStr()
                    .split(",");
            for (int i = 0, size = strings.length; i < size; i++) {
                if (TextUtils.isEmpty(strings[i])) {
                    continue;
                }
                int index = Integer.valueOf(strings[i]) - 1;
                if (index >= times.length) {
                    continue;
                }
                sb.append(times[index]);
                if (i != size - 1) {
                    sb.append(".");
                }
            }
            time = sb.toString();
        }
        if (TextUtils.isEmpty(time)) {
            holder.tvTime.setText(R.string.label_unfilled);
            holder.tvTime.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        } else {
            holder.tvTime.setText(time);
            holder.tvTime.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
        }
        //备注
        if (!TextUtils.isEmpty(schedule.getMessage())) {
            holder.messageLayout.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(schedule.getMessage());
        } else {
            holder.messageLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Schedule schedule = (Schedule) parent.getAdapter()
                .getItem(position);
        if (schedule != null && schedule.getId() > 0) {
            Intent intent = new Intent(getContext(), CreateScheduleActivity.class);
            intent.putExtra("position", schedules.indexOf(schedule));
            intent.putExtra("type", schedule.getType());
            intent.putExtra("property", property);
            intent.putExtra("schedule", schedule);
            startActivityForResult(intent, Constants.RequestCode.SCHEDULE_DETAIL);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            if (position == -1 || position >= schedules.size() || schedules.size() == 0) {
                return;
            }
            switch (requestCode) {
                case Constants.RequestCode.SCHEDULE_DETAIL:
                    String action = data.getStringExtra("action");
                    if ("update".equals(action)) {
                        Schedule schedule = data.getParcelableExtra("schedule");
                        if (schedule == null) {
                            return;
                        }
                        if (schedules.get(position)
                                .getDate()
                                .isEqual(schedule.getDate())) {
                            schedules.set(position, schedule);
                            adapter.notifyDataSetChanged();
                        } else if (getContext() instanceof MyScheduleListActivity) {
                            ((MyScheduleListActivity) getContext()).refreshData();
                        }
                    } else if ("delete".equals(action)) {
                        schedules.remove(position);
                        if (schedules.isEmpty()) {
                            emptyView.showEmptyView();
                            listView.setEmptyView(emptyView);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.isNeedRefresh = needRefresh;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isNeedRefresh) {
            isNeedRefresh = false;
            refresh();
        }
    }

    @Override
    public void refresh(Object... params) {
        if (listView != null) {
            onRefresh(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }
}