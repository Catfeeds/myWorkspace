package com.hunliji.marrybiz.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.reservation.ReservationManagerAdapter;
import com.hunliji.marrybiz.api.reservation.ReservationApi;
import com.hunliji.marrybiz.model.reservation.Reservation;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.view.ReservationManagerActivity;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DateTimePickerView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 预约管理fragment
 * Created by jinxin on 2017/5/22 0022.
 */

public class ReservationManagerFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener, ReservationManagerAdapter.onViewClickListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private Unbinder unbinder;
    private ReservationManagerAdapter adapter;
    private List<Reservation> reservations;
    private int type;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber arriveSubscriber;
    private HljHttpSubscriber lookSubscriber;
    private HljHttpSubscriber deleteSubscriber;
    private HljHttpSubscriber reservationSubscriber;
    private View footerView;
    private View endView;
    private View loadView;
    private int dataType;//用来请求数据的type  tab 1待确认列表 2确认列表 3预约历史
    private Calendar tempCalendar;
    private Dialog selectTimeDialog;
    private Dialog arriveDialog;
    private Dialog deleteDialog;

    public static ReservationManagerFragment newInstance(int type) {
        ReservationManagerFragment f = new ReservationManagerFragment();
        Bundle data = new Bundle();
        data.putInt("type", type);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        if (type == ReservationManagerAdapter.RESERVATION_CONFIRM) {
            dataType = 1;
        } else if (type == ReservationManagerAdapter.RESERVATION_LIST) {
            dataType = 2;
        } else if (type == ReservationManagerAdapter.RESERVATION_HISTORY) {
            dataType = 3;
        }
        reservations = new ArrayList<>();
        footerView = LayoutInflater.from(getContext())
                .inflate(R.layout.hlj_foot_no_more___cm, null, false);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        adapter = new ReservationManagerAdapter(getContext(), type, reservations);
        adapter.setOnViewClickListener(this);
        adapter.setFooterView(footerView);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        recyclerView.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(recyclerView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (selectTimeDialog != null && selectTimeDialog.isShowing()) {
            selectTimeDialog.dismiss();
        }
        if (arriveDialog != null && arriveDialog.isShowing()) {
            arriveDialog.dismiss();
        }
        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber,
                pageSubscriber,
                deleteSubscriber,
                arriveSubscriber,
                reservationSubscriber,
                lookSubscriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setContentView(refreshView)
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Reservation>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Reservation>> listHljHttpData) {
                        adapter.setItems(listHljHttpData.getData());
                        initPagination(listHljHttpData.getPageCount());
                    }
                })
                .build();
        Observable<HljHttpData<List<Reservation>>> obb = ReservationApi.getReservationList(dataType,
                1);
        obb.subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Reservation>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Reservation>>>() {
                    @Override
                    public Observable<HljHttpData<List<Reservation>>> onNextPage(int page) {
                        return ReservationApi.getReservationList(dataType, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Reservation>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Reservation>> productSearchResultList) {
                        if (productSearchResultList != null) {
                            adapter.addItems(productSearchResultList.getData());
                        }
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onViewClick(int id, Reservation reservation, int position, int type) {
        switch (id) {
            case R.id.img_call:
                if (!TextUtils.isEmpty(reservation.getPhoneNum())) {
                    ReservationManagerActivity activity = (ReservationManagerActivity)
                            getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        String phone;
                        if (type == ReservationManagerAdapter.RESERVATION_CONFIRM) {
                            phone = reservation.isLook() ? reservation.getPhone() : reservation
                                    .getPhoneNum();
                        } else {
                            phone = reservation.getPhoneNum();
                        }
                        activity.callUp(Uri.parse("tel:" + phone.trim()));
                    }
                }
                break;
            case R.id.tv_reservation:
                if (type == ReservationManagerAdapter.RESERVATION_CONFIRM) {
                    //预约
                    showReservationTimeDialog(reservation);
                } else if (type == ReservationManagerAdapter.RESERVATION_LIST) {
                    //到店
                    showArriveDialog(reservation, true);
                }
                break;
            case R.id.tv_delete:
                if (type == ReservationManagerAdapter.RESERVATION_CONFIRM) {
                    //删除
                    showDeleteDialog(reservation, position);
                } else if (type == ReservationManagerAdapter.RESERVATION_LIST) {
                    //未到店
                    showArriveDialog(reservation, false);
                }
                break;
            case R.id.tv_look:
                //查看
                lookReservation(reservation, position);
                break;
            default:
                break;
        }
    }

    private void lookReservation(Reservation reservation, final int position) {
        if (reservation == null) {
            return;
        }
        CommonUtil.unSubscribeSubs(lookSubscriber);
        Observable<HljHttpResult> lookObb = ReservationApi.lookReservation(reservation.getId(),
                System.currentTimeMillis());
        lookSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        if (hljHttpResult.getStatus() != null && hljHttpResult.getStatus()
                                .getRetCode() == 0) {
                            //查看成功
                            adapter.notifyItemLooked(position);
                        }
                    }
                })
                .build();
        lookObb.subscribe(lookSubscriber);
    }

    private void showReservationTimeDialog(final Reservation reservation) {
        if (reservation == null || reservation.getId() <= 0) {
            return;
        }
        if (selectTimeDialog != null && selectTimeDialog.isShowing()) {
            return;
        }

        if (reservation.getGoTime() != null) {
            tempCalendar.setTimeInMillis(reservation.getGoTime()
                    .getMillis());
        } else {
            tempCalendar = Calendar.getInstance();
        }
        selectTimeDialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
        selectTimeDialog.setContentView(R.layout.dialog_date_time_picker___cm);
        selectTimeDialog.findViewById(R.id.btn_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {selectTimeDialog.dismiss();}
                });
        Window win = selectTimeDialog.getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = JSONUtil.getDeviceSize(getContext()).x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        DateTimePickerView pickerView = (DateTimePickerView) selectTimeDialog.findViewById(R.id
                .picker);
        pickerView.setCurrentCalender(tempCalendar);
        pickerView.setYearLimit(Calendar.getInstance()
                .get(Calendar.YEAR), 49);
        pickerView.setOnPickerDateTimeListener(new DTPicker.OnPickerDateTimeListener() {
            @Override
            public void onPickerDateAndTime(
                    int year, int month, int day, int hour, int minute) {
                if (tempCalendar == null) {
                    tempCalendar = Calendar.getInstance();
                }
                tempCalendar.set(year, month - 1, day, hour, minute, 0);
            }
        });
        pickerView.getLayoutParams().height = Math.round(getResources().getDisplayMetrics()
                .density * (24 * 8));
        selectTimeDialog.findViewById(R.id.btn_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectTimeDialog.dismiss();
                        DateTime dateTime = new DateTime(tempCalendar);
                        String date = dateTime.toString(Constants.DATE_FORMAT_LONG);
                        reservation(reservation, date);
                    }
                });
        selectTimeDialog.show();
    }

    private void reservation(Reservation reservation, String time) {
        if (reservation == null) {
            return;
        }
        CommonUtil.unSubscribeSubs(reservationSubscriber);
        reservationSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        HljHttpStatus status = hljHttpResult.getStatus();
                        if (status != null) {
                            if (status.getRetCode() == 0) {
                                Toast.makeText(getContext(), "预约成功", Toast.LENGTH_SHORT)
                                        .show();
                                refreshFragment();
                            } else {
                                Toast.makeText(getContext(), status.getMsg(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                })
                .build();
        Observable<HljHttpResult> obb = ReservationApi.reservation(reservation.getId(), time);
        obb.subscribe(reservationSubscriber);
    }

    private void showDeleteDialog(final Reservation reservation, final int position) {
        if (deleteDialog != null && deleteDialog.isShowing()) {
            return;
        }
        String msg = "确认删除此条记录？";
        deleteDialog = DialogUtil.createDoubleButtonDialog(getContext(),
                msg,
                "删除",
                "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                        delete(reservation, position);
                    }
                },
                null);
        deleteDialog.show();
    }

    private void delete(Reservation reservation, final int position) {
        if (reservation == null) {
            return;
        }
        CommonUtil.unSubscribeSubs(deleteSubscriber);
        deleteSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        HljHttpStatus status = hljHttpResult.getStatus();
                        if (status != null) {
                            if (status.getRetCode() == 0) {
                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT)
                                        .show();
                                adapter.removeItem(position);
                                refreshFragment();
                            } else {
                                Toast.makeText(getContext(), status.getMsg(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                })
                .build();
        Observable<HljHttpResult> obb = ReservationApi.deleteReservation(reservation.getId(), 1);
        obb.subscribe(deleteSubscriber);
    }

    private void showArriveDialog(final Reservation reservation, final boolean arrive) {
        if (arriveDialog != null && arriveDialog.isShowing()) {
            return;
        }
        String msg = arrive ? "确认到店？" : "确认未到店？";
        arriveDialog = DialogUtil.createDoubleButtonDialog(getContext(),
                msg,
                "确定",
                "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arriveDialog.dismiss();
                        arrive(reservation, arrive);
                    }
                },
                null);
        arriveDialog.show();
    }

    /**
     * 到店 未到店
     *
     * @param arrive
     */
    private void arrive(Reservation reservation, boolean arrive) {
        if (reservation == null) {
            return;
        }
        int status;
        if (arrive) {
            //到点
            status = 1;
        } else {
            //未到店
            status = 2;
        }
        CommonUtil.unSubscribeSubs(arriveSubscriber);
        arriveSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        HljHttpStatus status = hljHttpResult.getStatus();
                        if (status != null) {
                            if (status.getRetCode() == 0) {
                                Toast.makeText(getContext(), "操作成功", Toast.LENGTH_SHORT)
                                        .show();
                                refreshFragment();
                            } else {
                                Toast.makeText(getContext(), status.getMsg(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                })
                .build();
        Observable<HljHttpResult> obb = ReservationApi.arriveReservation(reservation.getId(),
                status);
        obb.subscribe(arriveSubscriber);
    }

    private void refreshFragment() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        ReservationManagerActivity activity = (ReservationManagerActivity) getActivity();
        activity.onRefresh();
    }

    @Override
    public void refresh(Object... params) {
        onRefresh(recyclerView);
    }
}
