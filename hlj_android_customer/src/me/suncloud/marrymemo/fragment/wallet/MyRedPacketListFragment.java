package me.suncloud.marrymemo.fragment.wallet;

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
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.BuyWorkActivity;
import me.suncloud.marrymemo.view.BuyWorkListActivity;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.merchant.WeddingCompereChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingDressChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingDressPhotoChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingMakeupChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingPhotoChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingPlanChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingShootingChannelActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的红包列表
 * Created by chen_bin on 2016/10/15 0015.
 */
public class MyRedPacketListFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<RedPacket>, PullToRefreshBase.OnRefreshListener<ListView>, AdapterView
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
    private ObjectBindAdapter<RedPacket> adapter;
    private ArrayList<RedPacket> packets;
    private Unbinder unbinder;
    private int status;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private OnTabTextChangeListener onTabTextChangeListener;

    public final static int STATUS_UNUSED = 1; //未使用
    public final static int STATUS_USED = 3; //已使用
    public final static int STATUS_EXPIRED = 4; //已过期

    public static MyRedPacketListFragment newInstance(int status) {
        MyRedPacketListFragment fragment = new MyRedPacketListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getInt("status");
        }
        packets = new ArrayList<>();
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ObjectBindAdapter<>(getContext(),
                packets,
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
        if (status == STATUS_UNUSED) {
            emptyView.setHintId(R.string.label_no_available_red_packet);
        } else if (status == STATUS_EXPIRED) {
            emptyView.setHintId(R.string.label_no_expired_red_packet);
        } else if (status == STATUS_USED) {
            emptyView.setHintId(R.string.label_no_used_red_packet);
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
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RedPacket>>>
                            () {
                        @Override
                        public void onNext(HljHttpData<List<RedPacket>> listHljHttpData) {
                            packets.clear();
                            packets.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                            if (onTabTextChangeListener != null) {
                                onTabTextChangeListener.onTabTextChange(listHljHttpData
                                        .getTotalCount());
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(listView)
                    .setListView(listView.getRefreshableView())
                    .setProgressBar(listView.isRefreshing() ? null : progressBar)
                    .build();
            WalletApi.getMyRedPacketListObb(status, 1, 20)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<RedPacket>>> observable = PaginationTool.buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<RedPacket>>>() {
                    @Override
                    public Observable<HljHttpData<List<RedPacket>>> onNextPage(
                            int page) {
                        return WalletApi.getMyRedPacketListObb(status, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RedPacket>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<RedPacket>> listHljHttpData) {
                        packets.addAll(listHljHttpData.getData());
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
        @BindView(R.id.tv_rmb)
        TextView tvRmb;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_money_sill)
        TextView tvMoneySill;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_useful_life)
        TextView tvUsefulLife;
        @BindView(R.id.img_status)
        ImageView imgStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            tvType.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setViewValue(View view, RedPacket redPacket, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (status == STATUS_UNUSED) {
            holder.imgStatus.setVisibility(View.GONE);
            holder.imgEdge.setBackgroundResource(R.drawable.bg_red_packet_edge_red);
            holder.tvRmb.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            holder.tvValue.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            holder.tvMoneySill.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorBlack3));
            holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
            holder.tvType.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack3));
            holder.tvUsefulLife.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorGray));
        } else {
            holder.imgEdge.setBackgroundResource(R.drawable.bg_red_packet_edge_gray);
            holder.imgStatus.setVisibility(View.VISIBLE);
            holder.imgStatus.setImageResource(status == STATUS_USED ? R.drawable
                    .icon_red_packet_used_gray : R.drawable.icon_red_packet_expired);
            holder.tvRmb.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray3));
            holder.tvValue.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray3));
            holder.tvMoneySill.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorGray3));
            holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray3));
            holder.tvType.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray3));
            holder.tvUsefulLife.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorGray3));
        }
        holder.tvTitle.setText(redPacket.getRedPacketName());
        holder.tvValue.setText(Util.formatDouble2String(redPacket.getAmount()));
        holder.tvType.setText(redPacket.getCategoryType());
        holder.tvMoneySill.setText(redPacket.getMoneySillText());
        if (redPacket.getStartAt() == null || redPacket.getEndAt() == null) {
            holder.tvUsefulLife.setText("");
        } else {
            holder.tvUsefulLife.setText(getString(R.string.label_useful_life_date,
                    redPacket.getStartAt()
                            .toString(getString(R.string.format_date_type4)),
                    redPacket.getEndAt()
                            .toString(getString(R.string.format_date_type4))));
        }
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        RedPacket redPacket = (RedPacket) parent.getAdapter()
                .getItem(position);
        if (redPacket == null || redPacket.getId() == 0) {
            return;
        }
        Intent intent = null;
        int enterAnim = R.anim.slide_in_right;
        //本地服务
        if (redPacket.getRedPacketType() == 1) {
            //全部通用
            if (redPacket.getCategoryId() == 0) {
                intent = new Intent(getContext(), BuyWorkActivity.class);
                intent.putExtra("id", redPacket.getCategoryId());
            } else {
                switch ((int) redPacket.getCategoryId()) {
                    //婚礼策划
                    case Merchant.PROPERTY_WEDDING_PLAN:
                        intent = new Intent(getContext(), WeddingPlanChannelActivity.class);
                        break;
                    //婚纱摄影
                    case Merchant.PROPERTY_WEDDING_DRESS_PHOTO:
                        intent = new Intent(getContext(), WeddingDressPhotoChannelActivity.class);
                        break;
                    //婚纱礼服
                    case Merchant.PROPERTY_WEDDING_DRESS:
                        intent = new Intent(getContext(), WeddingDressChannelActivity.class);
                        break;
                    //婚礼司仪
                    case Merchant.PROPERTY_WEDDING_COMPERE:
                        intent = new Intent(getContext(), WeddingCompereChannelActivity.class);
                        break;
                    //婚礼跟妆
                    case Merchant.PROPERTY_WEDDING_MAKEUP:
                        intent = new Intent(getContext(), WeddingMakeupChannelActivity.class);
                        break;
                    //婚礼摄影
                    case Merchant.PROPERTY_WEDDING_PHOTO:
                        intent = new Intent(getContext(), WeddingPhotoChannelActivity.class);
                        break;
                    //婚礼摄像
                    case Merchant.PROPERTY_WEDDING_SHOOTING:
                        intent = new Intent(getContext(), WeddingShootingChannelActivity.class);
                        break;
                    default:
                        intent = new Intent(getContext(), BuyWorkListActivity.class);
                        intent.putExtra("id", redPacket.getCategoryId());
                        break;
                }
            }
        }
        //婚品频道页
        else if (redPacket.getRedPacketType() == 100) {
            intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra(MainActivity.ARG_MAIN_ACTION, MainActivity.MAIN_ACTION_PRODUCT);
        }
        //婚车
        else if (redPacket.getRedPacketType() == 101) {
            DataConfig.gotoWeddingCarActivity(getContext(), null);
        }
        //定制套餐不跳
        else if (redPacket.getRedPacketType() == 102) {
            return;
        }
        if (intent != null) {
            startActivity(intent);
            getActivity().overridePendingTransition(enterAnim, R.anim.activity_anim_default);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    public void setOnTabTextChangeListener(OnTabTextChangeListener onTabTextChangeListener) {
        this.onTabTextChangeListener = onTabTextChangeListener;
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