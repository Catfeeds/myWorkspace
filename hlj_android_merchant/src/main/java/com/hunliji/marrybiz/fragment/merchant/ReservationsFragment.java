package com.hunliji.marrybiz.fragment.merchant;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.model.merchant.Reservation;
import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DateTimePickerView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 预约管理列表
 * Created by chen_bin on 2016/10/27 0013.
 */
public class ReservationsFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<Reservation>, PullToRefreshBase.OnRefreshListener<ListView> {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View footerView;
    private View endView;
    private View loadView;
    private ObjectBindAdapter<Reservation> adapter;
    private ArrayList<Reservation> reservations;
    private Unbinder unbinder;
    private Calendar tempCalendar;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber postSubscriber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reservations = new ArrayList<>();
        tempCalendar = Calendar.getInstance();
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ObjectBindAdapter<>(getContext(),
                reservations,
                R.layout.reservation_list_item,
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
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (reservations.isEmpty()) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Reservation>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Reservation>> listHljHttpData) {
                            reservations.clear();
                            reservations.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(listView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(listView)
                    .setListView(listView.getRefreshableView())
                    .build();
            MerchantApi.getReservationListObb(1, 20)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPagination(final int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Reservation>>> observable = PaginationTool
                .buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Reservation>>>() {
                    @Override
                    public Observable<HljHttpData<List<Reservation>>> onNextPage(int page) {
                        return MerchantApi.getReservationListObb(pageCount, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Reservation>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Reservation>> listHljHttpData) {
                        reservations.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    class ViewHolder {
        @BindView(R.id.tv_full_name)
        TextView tvFullName;
        @BindView(R.id.tv_contacted)
        TextView tvContacted;
        @BindView(R.id.tv_created_at)
        TextView tvCreatedAt;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.btn_contact)
        Button btnContact;
        @BindView(R.id.btn_reservation)
        Button btnReservation;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    public void setViewValue(View view, final Reservation reservation, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.tvFullName.setText(getString(R.string.label_reservation_name,
                reservation.getFullName()));
        holder.tvContacted.setVisibility(reservation.getStatus() == 0 ? View.GONE : View.VISIBLE);
        holder.tvCreatedAt.setText(reservation.getCreatedAt() == null ? "" : getString(R.string
                        .label_reservation_create_time,
                reservation.getCreatedAt()
                        .toString(getString(R.string.format_date))));
        try {
            holder.tvDate.setText(reservation.getDate()
                    .toString(getString(R.string.format_date_type11)));
            holder.tvDate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } catch (Exception e) {
            holder.tvDate.setText("-");
            holder.tvDate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack3));
        }
        final ViewHolder holders = holder;
        holder.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCallDialog(holders, reservation);
            }
        });
        holder.btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservationInStore(holders, reservation);
            }
        });
    }

    private void showCallDialog(final ViewHolder holder, final Reservation reservation) {
        if (TextUtils.isEmpty(reservation.getPhoneNum()) || reservation.getPhoneNum()
                .trim()
                .length() <= 0) {
            return;
        }
        if (reservation.getStatus() == 0) {
            holder.tvContacted.setVisibility(View.VISIBLE);
            reservation.setStatus(1);
            MerchantApi.editReservationStatusObb(reservation.getId(), 1)
                    .subscribe(new Subscriber() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Object o) {

                        }
                    });
        }
        DialogUtil.createDoubleButtonDialog(getContext(),
                reservation.getPhoneNum()
                        .trim(),
                getString(R.string.label_call),
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            callUp(Uri.parse("tel:" + reservation.getPhoneNum()
                                    .trim()));
                        } catch (Exception ignored) {
                        }
                    }
                },
                null)
                .show();
    }

    //预约到店
    private void reservationInStore(final ViewHolder holder, final Reservation reservation) {
        try {
            tempCalendar.setTimeInMillis(reservation.getDate()
                    .getMillis());
        } catch (Exception e) {
            tempCalendar = Calendar.getInstance();
        }
        final Dialog selectTimeDialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
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
        pickerView.setYearLimit(2016, 49);
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
                        CommonUtil.unSubscribeSubs(postSubscriber);
                        if (postSubscriber == null || postSubscriber.isUnsubscribed()) {
                            final DateTime dateTime = new DateTime(tempCalendar);
                            final String date = dateTime.toString(getString(R.string.format_date,
                                    Locale.getDefault()));
                            postSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                                    .setOnNextListener(new SubscriberOnNextListener() {
                                        @Override
                                        public void onNext(Object o) {
                                            reservation.setDate(date);
                                            holder.tvDate.setText(dateTime.toString(getString(R
                                                            .string.format_date_type11,
                                                    Locale.getDefault())));
                                            holder.tvDate.setTextColor(ContextCompat.getColor(
                                                    getContext(),
                                                    R.color.colorPrimary));
                                        }
                                    })
                                    .setProgressDialog(DialogUtil.createProgressDialog(getContext
                                            ()))
                                    .build();
                            MerchantApi.editAppointmentObb(reservation.getId(),
                                    date,
                                    reservation.getFullName())
                                    .subscribe(postSubscriber);
                        }
                    }
                });
        selectTimeDialog.show();
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, postSubscriber);
    }
}