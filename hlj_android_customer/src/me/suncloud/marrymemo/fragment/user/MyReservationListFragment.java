package me.suncloud.marrymemo.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.moudles.Support;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Reservation;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * 我的预约列表
 * Created by chen_bin on 2017/11/6 0006.
 */
public class MyReservationListFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<Reservation>, PullToRefreshBase.OnRefreshListener<ListView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View footerView;
    private View endView;
    private ObjectBindAdapter<Reservation> adapter;
    private List<Reservation> reservations;
    private long filterId;
    private int logoSize;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSub;

    public static MyReservationListFragment newInstance() {
        Bundle args = new Bundle();
        MyReservationListFragment fragment = new MyReservationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logoSize = CommonUtil.dp2px(getContext(), 64);
        reservations = new ArrayList<>();
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        endView.setVisibility(View.INVISIBLE);
        adapter = new ObjectBindAdapter<>(getContext(),
                reservations,
                R.layout.my_reservation_list_item,
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
        initViews();
        return rootView;
    }

    private void initViews() {
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_order);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Reservation>>>() {

                    @Override
                    public void onNext(HljHttpData<List<Reservation>> listHljHttpData) {
                        endView.setVisibility(View.VISIBLE);
                        reservations.clear();
                        reservations.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                        if (onTabTextChangeListener != null) {
                            onTabTextChangeListener.onTabTextChange(listHljHttpData.getTotalCount
                                    ());
                        }
                    }
                })
                .setEmptyView(emptyView)
                .setPullToRefreshBase(listView)
                .setListView(listView.getRefreshableView())
                .setProgressBar(listView.isRefreshing() ? null : progressBar)
                .build();
        UserApi.getMyReservationsObb(filterId, 1, 9999)
                .subscribe(refreshSub);
    }

    class MyReservationViewHolder {
        @BindView(R.id.cover)
        RecyclingImageView image;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.reservation_date)
        TextView date;
        @BindView(R.id.property)
        TextView property;
        @BindView(R.id.gift_layout)
        View giftLayout;
        @BindView(R.id.shop_gift_hint)
        TextView shopGiftHint;
        @BindView(R.id.contact_merchant)
        TextView contactMerchant;

        MyReservationViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void setViewValue(View view, Reservation reservation, int position) {
        MyReservationViewHolder holder = (MyReservationViewHolder) view.getTag();
        if (holder == null) {
            holder = new MyReservationViewHolder(view);
            view.setTag(holder);
        }
        Merchant merchant = reservation.getMerchant();
        if (merchant.getHotel() != null && merchant.getHotel()
                .getId() > 0) {
            holder.contactMerchant.setText(R.string.label_contact_service2);
        } else {
            holder.contactMerchant.setText(R.string.label_contact_merchant3);
        }
        Glide.with(getContext())
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(logoSize)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(holder.image);
        holder.property.setText(merchant.getPropertyName());
        holder.title.setText(merchant.getName());
        holder.date.setText(reservation.getGoTime());
        if (TextUtils.isEmpty(reservation.getGift())) {
            holder.giftLayout.setVisibility(View.GONE);
        } else {
            holder.giftLayout.setVisibility(View.VISIBLE);
            holder.shopGiftHint.setText(reservation.getGift());
        }
        OnMerchantClickListener onMerchantClickListener = new OnMerchantClickListener(reservation);
        holder.image.setOnClickListener(onMerchantClickListener);
        holder.title.setOnClickListener(onMerchantClickListener);
        holder.contactMerchant.setOnClickListener(onMerchantClickListener);
    }

    private class OnMerchantClickListener implements View.OnClickListener {

        private Reservation reservation;

        private OnMerchantClickListener(Reservation reservation) {
            this.reservation = reservation;
        }

        @Override
        public void onClick(View v) {
            if (reservation == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.cover:
                case R.id.title:
                    gotoMerchant(reservation.getMerchant());
                    break;
                case R.id.contact_merchant:
                    contactMerchant(reservation.getMerchant());
                    break;
            }
        }
    }

    private void contactMerchant(Merchant merchant) {
        if (merchant != null) {
            if (merchant.getHotel() == null) {
                Intent intent = new Intent(getContext(), WSCustomerChatActivity.class);
                intent.putExtra("user", merchant.toUser());
                if (merchant.getContactPhone() != null && !merchant.getContactPhone()
                        .isEmpty()) {
                    intent.putStringArrayListExtra("contact_phones", merchant.getContactPhone());
                }
                if (merchant.getShopType() == Merchant.SHOP_TYPE_CAR && merchant.getCityCode() >
                        0) {
                    City city = new City();
                    city.setCid(merchant.getCityCode());
                    city.setName(merchant.getCityName());
                    intent.putExtra(RouterPath.IntentPath.Customer.BaseWsChat.ARG_CITY, city);
                }
                startActivity(intent);
            } else {
                CustomerSupportUtil.goToSupport(getContext(), Support.SUPPORT_KIND_HOTEL);
            }
        }
    }

    private void gotoMerchant(Merchant merchant) {
        if (merchant != null) {
            if (merchant.getShopType() == Merchant.SHOP_TYPE_CAR && merchant.getCityCode() > 0) {
                //婚车商家预约跳转到婚车二级页
                City city = new City();
                city.setCid(merchant.getCityCode());
                city.setName(merchant.getCityName());
                DataConfig.gotoWeddingCarActivity(getContext(), city);
            } else {
                Intent intent = new Intent(getContext(), MerchantDetailActivity.class);
                intent.putExtra("id", merchant.getId());
                startActivity(intent);
            }
        }
    }

    @Override
    public void refresh(Object... params) {
        if (listView == null) {
            return;
        }
        if (params != null && params.length > 0) {
            filterId = (long) params[0];
            onRefresh(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listView != null) {
            listView.getRefreshableView()
                    .setAdapter(null);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub);
    }
}
