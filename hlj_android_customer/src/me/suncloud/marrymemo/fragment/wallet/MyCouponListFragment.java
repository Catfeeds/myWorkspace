package me.suncloud.marrymemo.fragment.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.model.wallet.CouponRecord;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.wallet.MyCouponListActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的优惠券列表
 * Created by chen_bin on 2016/10/15 0015.
 */

public class MyCouponListFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<CouponRecord>, PullToRefreshBase.OnRefreshListener<ListView>, AdapterView
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
    private ObjectBindAdapter<CouponRecord> adapter;
    private List<CouponRecord> couponRecords;
    private Unbinder unbinder;
    private int type;
    private int totalCount = -1;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private OnTabTextChangeListener onTabTextChangeListener;
    private MyCouponListActivity.OnCouponUsedListener onCouponUsedListener;

    public final static int TYPE_UNUSED = 0; //未使用
    public final static int TYPE_USED = 1; //已使用
    public final static int TYPE_EXPIRED = 2; //已过期


    public static MyCouponListFragment newInstance(int type) {
        MyCouponListFragment fragment = new MyCouponListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
        couponRecords = new ArrayList<>();
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ObjectBindAdapter<>(getContext(),
                couponRecords,
                R.layout.my_red_packet_coupon_list_item,
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
        if (type == TYPE_UNUSED) {
            emptyView.setHintId(R.string.label_no_available_coupon);
        } else if (type == TYPE_EXPIRED) {
            emptyView.setHintId(R.string.label_no_expired_coupon);
        } else if (type == TYPE_USED) {
            emptyView.setHintId(R.string.label_no_used_coupon);
        }
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
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CouponRecord>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CouponRecord>> listHljHttpData) {
                            listView.getRefreshableView()
                                    .setSelection(0);
                            couponRecords.clear();
                            couponRecords.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                            totalCount = listHljHttpData.getTotalCount();
                            if (onTabTextChangeListener != null) {
                                onTabTextChangeListener.onTabTextChange(totalCount);
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(listView)
                    .setListView(listView.getRefreshableView())
                    .setProgressBar(listView.isRefreshing() ? null : progressBar)
                    .build();
            WalletApi.getMyCouponListObb(type, 1, HljCommon.PER_PAGE)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<CouponRecord>>> observable = PaginationTool
                .buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CouponRecord>>>() {
                    @Override
                    public Observable<HljHttpData<List<CouponRecord>>> onNextPage(
                            int page) {
                        return WalletApi.getMyCouponListObb(type, page, HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CouponRecord>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<CouponRecord>> listHljHttpData) {
                        couponRecords.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    class ViewHolder {
        @BindView(R.id.img_edge)
        ImageView imgEdge;
        @BindView(R.id.img_watermark)
        ImageView imgWaterMark;
        @BindView(R.id.tv_rmb)
        TextView tvRmb;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_money_sill)
        TextView tvMoneySill;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_useful_life)
        TextView tvUsefulLife;
        @BindView(R.id.img_status)
        ImageView imgStatus;
        @BindView(R.id.tv_type)
        TextView tvType;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            imgWaterMark.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setViewValue(
            View view, CouponRecord couponRecord, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (type == TYPE_UNUSED) {
            holder.imgStatus.setVisibility(View.GONE);
            holder.imgEdge.setBackgroundResource(R.drawable.bg_coupon_edge_yellow);
            holder.imgWaterMark.setImageResource(R.drawable.icon_coupon_yellow_137_151);
            holder.tvRmb.setTextColor(ContextCompat.getColor(getContext(), R.color.color_yellow));
            holder.tvValue.setTextColor(ContextCompat.getColor(getContext(), R.color.color_yellow));
            holder.tvMoneySill.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorBlack3));
            holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
            holder.tvUsefulLife.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorGray));
        } else {
            holder.imgEdge.setBackgroundResource(R.drawable.bg_red_packet_edge_gray);
            holder.imgWaterMark.setImageResource(R.drawable.icon_coupon_gray_137_151);
            holder.imgStatus.setVisibility(View.VISIBLE);
            holder.imgStatus.setImageResource(type == TYPE_USED ? R.drawable
                    .icon_red_packet_used_gray : R.drawable.icon_red_packet_expired);
            holder.tvRmb.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray3));
            holder.tvValue.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray3));
            holder.tvMoneySill.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorGray3));
            holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray3));
            holder.tvUsefulLife.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorGray3));
        }
        holder.tvValue.setText(CommonUtil.formatDouble2String(couponRecord.getValue()));
        if (couponRecord.getCoupon()
                .getType() == 1) {
            holder.tvMoneySill.setVisibility(View.VISIBLE);
            holder.tvMoneySill.setText(couponRecord.getMoneySill() == 0 ? getString(R.string
                    .label_money_sill_empty) : getString(
                    R.string.label_money_sill_available,
                    CommonUtil.formatDouble2String(couponRecord.getMoneySill())));
        } else {
            holder.tvMoneySill.setVisibility(View.GONE);
        }
        holder.tvTitle.setText(getString(R.string.label_coupon2,
                couponRecord.getCoupon()
                        .getMerchant()
                        .getName()));
        int shopType = couponRecord.getCoupon()
                .getMerchant()
                .getShopType();
        holder.tvType.setText(shopType == Merchant.SHOP_TYPE_PRODUCT ? "婚品使用" : "本地服务使用");
        if (couponRecord.getValidStart() == null || couponRecord.getValidEnd() == null) {
            holder.tvUsefulLife.setText("");
        } else {
            holder.tvUsefulLife.setText(getString(R.string.label_useful_life_date,
                    couponRecord.getValidStart()
                            .toString(getString(R.string.format_date_type4)),
                    couponRecord.getValidEnd()
                            .toString(getString(R.string.format_date_type4))));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CouponRecord couponRecord = (CouponRecord) parent.getAdapter()
                .getItem(position);
        if (couponRecord == null || couponRecord.getId() == 0) {
            return;
        }
        int shopType = couponRecord.getCoupon()
                .getMerchant()
                .getShopType();
        Intent intent = null;
        if (shopType == Merchant.SHOP_TYPE_PRODUCT) {
            intent = new Intent(getContext(), ProductMerchantActivity.class);
        } else {
            intent = new Intent(getContext(), MerchantDetailActivity.class);
        }
        intent.putExtra("id", couponRecord.getMerchantId());
        if (type != TYPE_UNUSED) {
            startActivity(intent);
        } else {
            intent.putExtra("position", couponRecords.indexOf(couponRecord));
            intent.putExtra("couponRecord", couponRecord);
            startActivityForResult(intent, Constants.RequestCode.MERCHANT_INFO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.MERCHANT_INFO:
                    int position = data.getIntExtra("position", -1);
                    if (position == -1 || position >= couponRecords.size() || couponRecords
                            .isEmpty()) {
                        return;
                    }
                    couponRecords.remove(position);
                    adapter.notifyDataSetChanged();
                    if (couponRecords.isEmpty()) {
                        emptyView.showEmptyView();
                        listView.setEmptyView(emptyView);
                    }
                    totalCount = totalCount - 1;
                    if (onTabTextChangeListener != null) {
                        onTabTextChangeListener.onTabTextChange(totalCount);
                    }
                    if (onCouponUsedListener != null) {
                        onCouponUsedListener.onCouponUsed();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public void refresh(Object... params) {
        if (listView != null) {
            onRefresh(null);
        }
    }

    public void setOnTabTextChangeListener(OnTabTextChangeListener onTabTextChangeListener) {
        this.onTabTextChangeListener = onTabTextChangeListener;
    }

    public void setOnCouponUsedListener(
            MyCouponListActivity.OnCouponUsedListener onCouponUsedListener) {
        this.onCouponUsedListener = onCouponUsedListener;
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
