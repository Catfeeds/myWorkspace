package me.suncloud.marrymemo.fragment.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.MerchantShopImageRecyclerAdapter;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantAchievementViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantShopImageViewHolder;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;

/**
 * Created by wangtao on 2017/6/2.
 */

public class MerchantDescriptionDetailFragment extends RefreshFragment {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.action_layout)
    FrameLayout actionLayout;
    private View headerView;
    Unbinder unbinder;

    private MerchantShopImageRecyclerAdapter adapter;

    public static MerchantDescriptionDetailFragment newInstance(Merchant merchant) {
        Bundle args = new Bundle();
        args.putParcelable("merchant", merchant);
        MerchantDescriptionDetailFragment fragment = new MerchantDescriptionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerView = View.inflate(getContext(), R.layout.merchant_description_detail_header, null);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_merchant_description_detail,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        HljBaseActivity.setActionBarPadding(getContext(), actionLayout);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            Merchant merchant = getArguments().getParcelable("merchant");
            if (merchant == null) {
                return;
            }
            if (adapter == null) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) headerView.getTag();
                if (headerViewHolder == null) {
                    headerViewHolder = new HeaderViewHolder(headerView, merchant);
                    headerView.setTag(headerViewHolder);
                }
                headerViewHolder.setMerchantDescribe();
                headerViewHolder.setMerchantAchievements();
                headerViewHolder.setMerchantSubPage();
                headerViewHolder.setMerchantServices();
                headerViewHolder.setMerchantShopImages();
                adapter = new MerchantShopImageRecyclerAdapter(getContext(),
                        merchant.getRealPhotos());
                adapter.setHeaderView(headerView);
            }
            recyclerView.getRefreshableView()
                    .setAdapter(adapter);
        }
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        if (getActivity().getSupportFragmentManager()
                .getBackStackEntryCount() > 0) {
            getActivity().getSupportFragmentManager()
                    .popBackStack();
        }
    }


    public class HeaderViewHolder {
        @BindView(R.id.tv_merchant_describe)
        TextView tvMerchantDescribe;
        @BindView(R.id.merchant_describe_layout)
        LinearLayout merchantDescribeLayout;
        @BindView(R.id.merchant_achievement_list_layout)
        LinearLayout merchantAchievementListLayout;
        @BindView(R.id.merchant_achievement_layout)
        LinearLayout merchantAchievementLayout;
        @BindView(R.id.img_sub_page)
        ImageView imgSubPage;
        @BindView(R.id.sub_page_layout)
        LinearLayout subPageLayout;
        @BindView(R.id.img_service_arrow)
        ImageView imgServiceArrow;
        @BindView(R.id.tv_refund_hint)
        TextView tvRefundHint;
        @BindView(R.id.tv_refund)
        TextView tvRefund;
        @BindView(R.id.refund_layout)
        RelativeLayout refundLayout;
        @BindView(R.id.refund_bottom_line_layout)
        View refundBottomLineLayout;
        @BindView(R.id.tv_promise_hint)
        TextView tvPromiseHint;
        @BindView(R.id.tv_promise)
        TextView tvPromise;
        @BindView(R.id.promise_layout)
        RelativeLayout promiseLayout;
        @BindView(R.id.promise_bottom_line_layout)
        View promiseBottomLineLayout;
        @BindView(R.id.tv_free_hint)
        TextView tvFreeHint;
        @BindView(R.id.free_layout)
        RelativeLayout freeLayout;
        @BindView(R.id.service_content_layout)
        RelativeLayout serviceContentLayout;
        @BindView(R.id.service_layout)
        LinearLayout serviceLayout;
        @BindView(R.id.shop_images_layout)
        LinearLayout shopImagesLayout;

        @BindView(R.id.server_merchant_promise)
        LinearLayout serverMerchantPromise;
        @BindView(R.id.tv_hotel_free)
        TextView tvHotelFree;
        @BindView(R.id.hotel_free_layout)
        RelativeLayout hotelFreeLayout;
        @BindView(R.id.hotel_free_bottom_line_layout)
        View hotelFreeBottomLineLayout;
        @BindView(R.id.tv_hotel_platform_gift)
        TextView tvHotelPlatformGift;
        @BindView(R.id.hotel_platform_gift_layout)
        RelativeLayout hotelPlatformGiftLayout;
        @BindView(R.id.hotel_merchant_promise)
        LinearLayout hotelMerchantPromise;
        private Merchant merchant;

        public HeaderViewHolder(View itemView, Merchant merchant) {
            ButterKnife.bind(this, itemView);
            this.merchant = merchant;
        }

        //设置商家简介
        private void setMerchantDescribe() {
            if (TextUtils.isEmpty(merchant.getDesc())) {
                merchantDescribeLayout.setVisibility(View.GONE);
            } else {
                merchantDescribeLayout.setVisibility(View.VISIBLE);
                tvMerchantDescribe.setText(merchant.getDesc());
            }
        }

        private void setMerchantAchievements() {
            if (CommonUtil.isCollectionEmpty(merchant.getMerchantAchievement())) {
                merchantAchievementLayout.setVisibility(View.GONE);
            } else {
                merchantAchievementLayout.setVisibility(View.VISIBLE);
                int count = merchantAchievementListLayout.getChildCount();
                int size = merchant.getMerchantAchievement()
                        .size();
                if (count > size) {
                    merchantAchievementListLayout.removeViews(size, count - size);
                }
                for (int i = 0; i < size; i++) {
                    View view = null;
                    MerchantAchievementViewHolder holder;
                    Poster poster = merchant.getMerchantAchievement()
                            .get(i);
                    if (count > i) {
                        view = merchantAchievementListLayout.getChildAt(i);
                    }
                    if (view == null) {
                        View.inflate(getContext(),
                                R.layout.merchant_achievement_list_item,
                                merchantAchievementListLayout);
                        view = merchantAchievementListLayout.getChildAt(
                                merchantAchievementListLayout.getChildCount() - 1);
                    }
                    holder = (MerchantAchievementViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MerchantAchievementViewHolder(view);
                        view.setTag(holder);
                    }
                    holder.setShowBottomLineView(i < size - 1);
                    holder.setView(getContext(), poster, i, 0);
                }
            }
        }

        //店铺报导
        private void setMerchantSubPage() {
            final TopicUrl topic = merchant.getTopic();
            if (topic == null || topic.getId() == 0) {
                subPageLayout.setVisibility(View.GONE);
            } else {
                subPageLayout.setVisibility(View.VISIBLE);
                int imageWidth = CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px(
                        getContext(),
                        32);
                int imageHeight = Math.round(imageWidth / 2.0f);
                imgSubPage.getLayoutParams().width = imageWidth;
                imgSubPage.getLayoutParams().height = imageHeight;
                Glide.with(getContext())
                        .load(ImagePath.buildPath(topic.getListImg())
                                .width(imageWidth)
                                .height(imageHeight)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(imgSubPage);
            }
        }

        //退、诺、免
        private void setMerchantServices() {
            if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
                serverMerchantPromise.setVisibility(View.GONE);
                hotelMerchantPromise.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(merchant.getFreeOrder()) && TextUtils.isEmpty(merchant
                        .getPlatformGift())) {
                    serviceLayout.setVisibility(View.GONE);
                } else {
                    serviceLayout.setVisibility(View.VISIBLE);
                    hotelFreeBottomLineLayout.setVisibility(View.VISIBLE);
                    imgServiceArrow.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(merchant.getFreeOrder())) {
                        hotelFreeLayout.setVisibility(View.VISIBLE);
                        tvHotelFree.setText(merchant.getFreeOrder());
                    } else {
                        hotelFreeBottomLineLayout.setVisibility(View.GONE);
                        hotelFreeLayout.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(merchant.getPlatformGift())) {
                        hotelPlatformGiftLayout.setVisibility(View.VISIBLE);
                        tvHotelPlatformGift.setText(merchant.getPlatformGift());
                    } else {
                        hotelPlatformGiftLayout.setVisibility(View.GONE);
                        hotelFreeBottomLineLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                serverMerchantPromise.setVisibility(View.VISIBLE);
                hotelMerchantPromise.setVisibility(View.GONE);
                boolean isCanClick = false;
                StringBuilder refundText = new StringBuilder();
                if (merchant.getChargeBack() != null) {
                    for (String value : merchant.getChargeBack()) {
                        String space = "";
                        if (refundText.length() > 0) {
                            while (Util.getTextViewWidth(tvRefund, space) < 24) {
                                space += " ";
                            }
                        }
                        refundText.append(space)
                                .append(value);
                    }
                }
                StringBuilder promiseText = new StringBuilder();
                if (merchant.getMerchantPromise() != null) {
                    for (String value : merchant.getMerchantPromise()) {
                        String space = "";
                        if (promiseText.length() > 0) {
                            while (Util.getTextViewWidth(tvPromise, space) < 24) {
                                space += " ";
                            }
                        }
                        promiseText.append(space)
                                .append(value);
                    }
                }
                if (refundText.length() == 0 && promiseText.length() == 0 && merchant
                        .getActiveWorkCount() <= 0) {

                    serviceLayout.setVisibility(View.GONE);
                } else {
                    serviceLayout.setVisibility(View.VISIBLE);
                    if (refundText.length() > 0) {
                        isCanClick = true;
                        refundLayout.setVisibility(View.VISIBLE);
                        tvRefund.setText(refundText);
                    } else {
                        refundLayout.setVisibility(View.GONE);
                    }
                    if (promiseText.length() > 0) {
                        isCanClick = true;
                        promiseLayout.setVisibility(View.VISIBLE);
                        tvPromise.setText(promiseText);
                    } else {
                        promiseLayout.setVisibility(View.GONE);
                    }
                    freeLayout.setVisibility(merchant.getActiveWorkCount() > 0 ? View.VISIBLE :
                            View.GONE);
                    imgServiceArrow.setVisibility(TextUtils.isEmpty(merchant.getGuidePath()) ?
                            View.GONE : View.VISIBLE);
                    if (refundText.length() > 0 && promiseText.length() > 0) {
                        refundBottomLineLayout.setVisibility(View.VISIBLE);
                    } else {
                        refundBottomLineLayout.setVisibility(View.GONE);
                    }
                    if (promiseText.length() > 0 && merchant.getActiveWorkCount() > 0) {
                        promiseBottomLineLayout.setVisibility(View.VISIBLE);
                    } else {
                        promiseBottomLineLayout.setVisibility(View.GONE);
                    }
                    if (!isCanClick) {
                        serviceContentLayout.setClickable(false);
                        serviceContentLayout.getLayoutParams().height = CommonUtil.dp2px
                                (getContext(),
                                48);
                        serviceContentLayout.setPadding(serviceContentLayout.getPaddingLeft(),
                                0,
                                serviceContentLayout.getPaddingRight(),
                                0);
                    } else {
                        if (refundText.length() > 0) {
                            refundBottomLineLayout.setVisibility(View.VISIBLE);
                        }
                        if (promiseText.length() > 0) {
                            promiseBottomLineLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }

        //店铺环境
        private void setMerchantShopImages() {
            shopImagesLayout.setVisibility(CommonUtil.isCollectionEmpty(merchant.getRealPhotos())
                    ? View.GONE : View.VISIBLE);
        }

        @OnClick(R.id.sub_page_layout)
        void onSubPageDetail() {
            TopicUrl topic = merchant.getTopic();
            if (topic != null && topic.getId() > 0) {
                Intent intent = new Intent(getContext(), SubPageDetailActivity.class);
                intent.putExtra("id", topic.getId());
                startActivity(intent);
            }
        }

        @OnClick(R.id.service_content_layout)
        void onServiceContent() {
            if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
                DialogUtil.createHotelPromiseDlg(getContext(), merchant)
                        .show();
            } else {
                HljWeb.startWebView(getActivity(), merchant.getGuidePath());
            }
        }
    }


    @Override
    public void refresh(Object... params) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
