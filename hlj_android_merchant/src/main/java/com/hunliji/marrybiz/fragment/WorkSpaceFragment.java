package com.hunliji.marrybiz.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.PosterFlowAdapter;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.api.revenue.RevenueApi;
import com.hunliji.marrybiz.model.ADVHMerchant;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.merchant.ShopInfo;
import com.hunliji.marrybiz.model.revenue.Bank;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.ADVHMerchantActivity;
import com.hunliji.marrybiz.view.OrdersManagerActivity;
import com.hunliji.marrybiz.view.ReservationManagerActivity;
import com.hunliji.marrybiz.view.comment.CommentManagerActivity;
import com.hunliji.marrybiz.view.customer.CustomerManagerActivity;
import com.hunliji.marrybiz.view.experience.AdvListActivity;
import com.hunliji.marrybiz.view.login.OpenShopScheduleActivity;
import com.hunliji.marrybiz.view.merchantservice.BondPlanDetailActivity;
import com.hunliji.marrybiz.view.revenue.BondAccountActivity;
import com.hunliji.marrybiz.view.revenue.RevenueManageActivity;
import com.hunliji.marrybiz.view.shop.ShopManagerActivity;
import com.hunliji.marrybiz.view.shop.ShopWebViewActivity;
import com.hunliji.marrybiz.view.tools.CheckShopActivity;
import com.hunliji.marrybiz.view.tools.ScheduleManageActivity;
import com.hunliji.marrybiz.view.work_case.WorkManageListActivity;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

/**
 * Created by werther on 16/9/9.
 * 工作台fragment
 */
public class WorkSpaceFragment extends RefreshFragment {


    @Override
    public String fragmentPageTrackTagName() {
        return "工作台";
    }

    public static final int LOGO_SIZE_DP = 50;

    @BindView(R.id.logo_layout)
    RelativeLayout logoLayout;
    @BindView(R.id.pro_layout)
    LinearLayout proLayout;
    @BindView(R.id.merchant_layout)
    RelativeLayout merchantLayout;
    @BindView(R.id.un_audit_alert)
    LinearLayout unAuditAlert;
    @BindView(R.id.todo_order_layout)
    LinearLayout todoOrderLayout;
    @BindView(R.id.todo_reservation_layout)
    LinearLayout todoReservationLayout;
    @BindView(R.id.comment_item_layout)
    LinearLayout commentItemLayout;
    @BindView(R.id.shop_manage_layout)
    LinearLayout shopManageLayout;
    @BindView(R.id.order_manage_layout)
    LinearLayout orderManageLayout;
    @BindView(R.id.customer_manage_layout)
    LinearLayout customerManageLayout;
    @BindView(R.id.works_manage_layout)
    LinearLayout worksManageLayout;
    @BindView(R.id.case_manage_layout)
    LinearLayout caseManageLayout;
    @BindView(R.id.comment_manage_layout)
    LinearLayout commentManageLayout;
    @BindView(R.id.income_manage_layout)
    LinearLayout incomeManageLayout;
    @BindView(R.id.appointment_manage_layout)
    LinearLayout appointmentManageLayout;
    @BindView(R.id.schedule_manage_layout)
    LinearLayout scheduleManageLayout;
    @BindView(R.id.shop_check_layout)
    LinearLayout shopCheckLayout;
    @BindView(R.id.ll_adv_inside)
    LinearLayout llAdvInside;
    @BindView(R.id.merchant_adv_layout)
    RelativeLayout merchantAdvLayout;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.img_merchant_pro)
    ImageView imgMerchantPro;
    @BindView(R.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.img_level)
    ImageView imgLevel;
    @BindView(R.id.img_bond_icon)
    ImageView imgBondIcon;
    @BindView(R.id.star_rating_bar)
    RatingBar starRatingBar;
    @BindView(R.id.merchant_comment_count)
    TextView merchantCommentCount;
    @BindView(R.id.tv_property)
    TextView tvProperty;
    @BindView(R.id.tv_follower_count)
    TextView tvFollowerCount;
    @BindView(R.id.merchant_name_layout)
    LinearLayout merchantNameLayout;
    @BindView(R.id.tv_pro)
    TextView tvPro;
    @BindView(R.id.img_ultimate_tags)
    ImageView imgUltimateTags;
    @BindView(R.id.tv_alert_1)
    TextView tvAlert1;
    @BindView(R.id.tv_alert_2)
    TextView tvAlert2;
    @BindView(R.id.img_alert_arrow)
    ImageView imgAlertArrow;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.advh_notice_layout)
    LinearLayout advhNoticeLayout;
    @BindView(R.id.tv_todo_orders_count)
    TextView tvTodoOrdersCount;
    @BindView(R.id.tv_todo_reservations_count)
    TextView tvTodoReservationsCount;
    @BindView(R.id.tv_comment_item_count)
    TextView tvCommentItemCount;
    @BindView(R.id.posters_view)
    SliderLayout postersView;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R.id.banner_layout)
    RelativeLayout bannerLayout;
    @BindView(R.id.grid_layout)
    GridLayout gridLayout;
    @BindView(R.id.service_merchant_layout)
    LinearLayout serviceMerchantLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.un_audit_view)
    LinearLayout unAuditView;
    @BindView(R.id.merchant_info_layout)
    RelativeLayout merchantInfoLayout;
    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.experience_store_layout)
    RelativeLayout experienceStoreLayout;

    private Unbinder unbinder;
    private MerchantUser merchantUser;
    private RxBusSubscriber<MerchantUser> userInfoUpdateSub;
    private PosterFlowAdapter flowAdapter;
    private HljHttpSubscriber zipSub;
    private GetNewADVHMerchantsTask getNewADVHMerchantsTask;
    private Subscription rxEventSubscription;
    private int infoTextColor;
    private HljHttpSubscriber bankSubscriber;

    public static WorkSpaceFragment newInstance() {
        Bundle args = new Bundle();
        WorkSpaceFragment fragment = new WorkSpaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_space, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        registerUserInfoUpdate();
        registerAdvHelperUpdate();
        initTracker();
        return rootView;
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(postersView, "merchant_banner_list");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initLoad(false);
        getBindBankInfo();
    }

    /**
     * 获取绑定的银行卡失败原因信息
     */
    private void getBindBankInfo() {
        if (bankSubscriber == null || bankSubscriber.isUnsubscribed()) {
            Observable<Bank> observable = RevenueApi.getAppBindBank();
            bankSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<Bank>() {
                        @Override
                        public void onNext(Bank bank) {
                            //1成功 2失败
                            int status = bank.getStatus();
                            if (status == 2 && !TextUtils.isEmpty(bank.getReason())) {
                                bondBankCardVerifyFail(bank.getReason(), bank.getChangeProtocol());
                            }

                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribe(bankSubscriber);
        }
    }

    /**
     * 绑定银行卡信息失败弹窗
     *
     * @param reason
     */
    private void bondBankCardVerifyFail(String reason, String changeProtocol) {
        reason = "原因：" + reason;
        if (TextUtils.isEmpty(changeProtocol)) {
            DialogUtil.createDoubleButtonDialog(getContext(),
                    getString(R.string.label_billing_account_bind_fail),
                    reason,
                    getString(R.string.label_rebind_bank_info),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), BondAccountActivity.class);
                            startActivity(intent);
                            if (getActivity() != null) {
                                getActivity().overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        }
                    },
                    null)
                    .show();
        } else {
            DialogUtil.createSingleButtonDialog(getContext(),
                    getString(R.string.hint_billing_account_modify_bond_failed),
                    getString(R.string.hint_billing_account_modify_bond_failed_reason, reason),
                    getString(R.string.label_confirm),
                    null)
                    .show();
        }
    }


    /**
     * 注册用户信息更新监听,任何时候有用户信息更新回调都会受到更新事件,进而更新界面
     */
    private void registerUserInfoUpdate() {
        if (userInfoUpdateSub == null || userInfoUpdateSub.isUnsubscribed()) {
            userInfoUpdateSub = new RxBusSubscriber<MerchantUser>() {
                @Override
                protected void onEvent(MerchantUser user) {
                    merchantUser = user;
                    setMerchantInfo();
                }
            };
            RxBus.getDefault()
                    .toObservable(MerchantUser.class)
                    .subscribe(userInfoUpdateSub);
        }
    }

    private void registerAdvHelperUpdate() {
        if (rxEventSubscription == null || rxEventSubscription.isUnsubscribed()) {
            rxEventSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case ADV_HELPER:
                                    loadNewAdvhMerchants();
                                    break;
                            }
                        }
                    });
        }
    }

    private void initValues() {
        merchantUser = Session.getInstance()
                .getCurrentUser(getContext());
    }

    private void initViews() {
        // 婚品商家只显示头部，不显示其他视图
        if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_PRODUCT) {
            serviceMerchantLayout.setVisibility(View.GONE);
        } else {
            serviceMerchantLayout.setVisibility(View.VISIBLE);
        }
        bannerLayout.getLayoutParams().height = Math.round(JSONUtil.getDeviceSize(getContext()).x
                / 5);
        setMerchantInfo();
    }

    private void initLoad(boolean isReload) {
        Observable<PosterData> bannerOb = CommonApi.getBanner(getContext(),
                HljCommon.BLOCK_ID.HomeFragment,
                merchantUser.getCity_code());
        Observable<ShopInfo> shopInfoOb = MerchantApi.getShopInfoObb();

        Observable<ResultZip> zipObservable = Observable.zip(bannerOb,
                shopInfoOb,
                new Func2<PosterData, ShopInfo, ResultZip>() {
                    @Override
                    public ResultZip call(PosterData posterData, ShopInfo shopInfo) {
                        ResultZip resultZip = new ResultZip();
                        resultZip.posterData = posterData;
                        resultZip.shopInfo = shopInfo;
                        return resultZip;
                    }
                });

        zipSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(isReload ? null : progressBar)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip zip) {
                        List<Poster> posterList = PosterUtil.getPosterList(zip.posterData
                                        .getFloors(),
                                Constants.POST_SITES.SERVICE_MERCHANT_BANNER,
                                false);
                        setBannerView(posterList);
                        setShopInfoView(zip.shopInfo);
                    }
                })
                .build();

        zipObservable.subscribe(zipSub);
    }

    private void setShopInfoView(ShopInfo shopInfo) {
        tvTodoOrdersCount.setText(String.valueOf(shopInfo.getTodoOrderCount()));
        tvTodoReservationsCount.setText(String.valueOf(shopInfo.getAppointCountTodo()));
    }

    private void setBannerView(List<Poster> posters) {
        if (getContext() == null) {
            return;
        }
        flowAdapter = new PosterFlowAdapter(getContext(), posters, R.layout.poster_flow_item);
        postersView.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(postersView);
        postersView.setCustomIndicator(flowIndicator);
        postersView.setPresetTransformer(4);
        if (flowAdapter.getCount() > 0) {
            bannerLayout.setVisibility(View.VISIBLE);
            if (flowAdapter.getCount() > 1) {
                postersView.startAutoCycle();
            } else {
                postersView.stopAutoCycle();
            }
        } else {
            bannerLayout.setVisibility(View.GONE);
        }
    }

    protected void setMerchantInfo() {
        if (merchantUser.getExperienceStore() == null) {
            experienceStoreLayout.setVisibility(View.GONE);
            gridLayout.removeView(experienceStoreLayout);
        } else if (experienceStoreLayout != null && experienceStoreLayout.getVisibility() == View
                .GONE) {
            experienceStoreLayout.setVisibility(View.VISIBLE);
            gridLayout.addView(experienceStoreLayout);
        }

        if (!merchantUser.isAdv()) {
            merchantAdvLayout.setVisibility(View.GONE);
            gridLayout.removeView(merchantAdvLayout);
        } else if (merchantAdvLayout != null && merchantAdvLayout.getVisibility() == View.GONE) {
            merchantAdvLayout.setVisibility(View.VISIBLE);
            gridLayout.addView(merchantAdvLayout);
        }

        if (!merchantUser.isOpenedTrade() && (merchantUser.getExamine() != 1 || (merchantUser
                .getKind() != 1 && merchantUser.getCertifyStatus() != 3)) && merchantUser
                .getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
            // 店铺资料审核不通过或者实名认证未审核通过
            merchantInfoLayout.setVisibility(View.GONE);
            unAuditView.setVisibility(View.VISIBLE);
            StringBuilder msg = new StringBuilder();
            msg.append("开店进度：");
            if (merchantUser.getExamine() == -1) {
                if (merchantUser.getCertifyStatus() == 0) {
                    msg.append("店铺信息");
                } else {
                    msg.append("店铺信息未提交");
                }
            } else if (merchantUser.getExamine() == 0 && merchantUser.getStatus() == 0) {
                if (merchantUser.getCertifyStatus() == 1) {
                    msg.append("店铺信息");
                } else {
                    msg.append("店铺信息审核中");
                }
            } else if (merchantUser.getExamine() == 0 && merchantUser.getStatus() == 1) {
                if (merchantUser.getCertifyStatus() == 2) {
                    msg.append("店铺信息");
                } else {
                    msg.append("店铺信息审核未通过");
                }
            }
            if (merchantUser.getCertifyStatus() != 3 && merchantUser.getExamine() != 1) {
                msg.append("、");
            }
            if (merchantUser.getCertifyStatus() == 0) {
                msg.append("实名认证未提交");
            } else if (merchantUser.getCertifyStatus() == 1) {
                msg.append("实名认证审核中");
            } else if (merchantUser.getCertifyStatus() == 2) {
                msg.append("实名认证审核未通过");
            }
            tvAlertMsg.setText(msg);
        } else {
            merchantInfoLayout.setVisibility(View.VISIBLE);
            unAuditView.setVisibility(View.GONE);
            Drawable starDrawable = ContextCompat.getDrawable(getContext(),
                    R.drawable.star_rating_bar_normal);
            // 商家信息
            if (merchantUser.isPro() && merchantUser.getShopType() == MerchantUser
                    .SHOP_TYPE_SERVICE) {
                imgUltimateTags.setVisibility(View.GONE);
                tvPro.setVisibility(View.VISIBLE);
                imgMerchantPro.setVisibility(View.VISIBLE);
                if (merchantUser.getIsPro() == 1) {
                    infoTextColor = Color.parseColor("#faf7f1");
                    tvPro.setText(getString(R.string.label_merchant_pro2));
                    imgMerchantPro.setImageResource(R.drawable.sp_pro_top_bg);
                } else if (merchantUser.getIsPro() == 2) {
                    starDrawable = ContextCompat.getDrawable(getContext(),
                            R.drawable.star_rating_bar_ultimate);
                    infoTextColor = ContextCompat.getColor(getContext(), R.color.colorWhite);
                    tvPro.setText(getString(R.string.label_merchant_ultimate));
                    imgMerchantPro.setImageResource(R.drawable.sp_ultimate_top_bg);
                }
            } else {
                infoTextColor = ContextCompat.getColor(getContext(), R.color.colorWhite);
                tvPro.setVisibility(View.GONE);
                imgMerchantPro.setVisibility(View.GONE);
                if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
                    imgUltimateTags.setVisibility(View.VISIBLE);
                } else {
                    imgUltimateTags.setVisibility(View.GONE);
                }
            }
            merchantCommentCount.setTextColor(infoTextColor);
            tvMerchantName.setTextColor(infoTextColor);
            tvFollowerCount.setTextColor(infoTextColor);
            tvProperty.setTextColor(infoTextColor);
            if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_PRODUCT) {
                tvProperty.setText("婚品");
            } else {
                tvProperty.setText(merchantUser.getProperty() != null ? merchantUser.getProperty()
                        .getName() : null);
            }
            tvProperty.setVisibility(View.GONE);
            tvFollowerCount.setText(getString(R.string.label_followers_counts2,
                    String.valueOf(merchantUser.getFansCount())));
            switch (merchantUser.getGradeLevel()) {
                case 0:
                    imgLevel.setImageResource(R.mipmap.icon_merchant_level0_36_36);
                    break;
                case 1:
                    imgLevel.setImageResource(R.mipmap.icon_merchant_level1_36_36);
                    break;
                case 2:
                    imgLevel.setImageResource(R.mipmap.icon_merchant_level2_36_36);
                    break;
                case 3:
                    imgLevel.setImageResource(R.mipmap.icon_merchant_level3_36_36);
                    break;
                case 4:
                    imgLevel.setImageResource(R.mipmap.icon_merchant_level4_36_36);
                    break;
            }
            if (merchantUser.isBondSign()) {
                imgBondIcon.setVisibility(View.VISIBLE);
            } else {
                imgBondIcon.setVisibility(View.GONE);
            }
            tvMerchantName.setText(merchantUser.getName());
            String path = ImageUtil.getAvatar(merchantUser.getLogoPath(),
                    Util.dp2px(getContext(), LOGO_SIZE_DP));
            Glide.with(getContext())
                    .load(path)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary)
                            .dontAnimate())
                    .into(imgLogo);

            setAlertInfo();

            if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
                imgLevel.setVisibility(View.VISIBLE);
            } else {
                imgLevel.setVisibility(View.GONE);
            }

            //评价
            starRatingBar.setRating((float) merchantUser.getScore());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //21版本下需要通过反射区修改ratingbar的私有方法
                Drawable d = getMethod("tileify", starRatingBar, new Object[]{starDrawable, false});
                starRatingBar.setProgressDrawable(d);
            } else {
                starRatingBar.setProgressDrawableTiled(starDrawable);
            }
            merchantCommentCount.setText(getString(R.string.label_comment_count,
                    String.valueOf(merchantUser.getCommentCount())));
            tvCommentItemCount.setText(String.valueOf(merchantUser.getThirtyCommentCount()));
        }
    }

    private static Drawable getMethod(String MethodName, Object o, Object[] paras) {
        Drawable newDrawable = null;
        try {
            Class c[] = new Class[2];
            c[0] = Drawable.class;
            c[1] = boolean.class;
            Method method = ProgressBar.class.getDeclaredMethod(MethodName, c);
            method.setAccessible(true);
            newDrawable = (Drawable) method.invoke(o, paras);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return newDrawable;
    }

    /**
     * 设置黄色提醒条
     */
    private void setAlertInfo() {
        int alertBgColor;
        int alertTextColor;
        switch (merchantUser.getIsPro()) {
            case Constants.MERCHANT_PRO:
                alertBgColor = Color.parseColor("#5b5249");
                alertTextColor = Color.parseColor("#dfcb90");
                imgAlertArrow.setImageResource(R.drawable.icon_arrow_right_gold_14_22);
                break;
            case Constants.MERCHANT_ULTIMATE:
                alertBgColor = Color.parseColor("#4b4a48");
                alertTextColor = Color.parseColor("#d6c7b4");
                Drawable drawable = ContextCompat.getDrawable(getContext(),
                        R.drawable.icon_arrow_right_white_14_26);
                Drawable drawableUltimate = tintDrawable(drawable, alertTextColor);
                imgAlertArrow.setImageDrawable(drawableUltimate);
                break;
            default:
                alertBgColor = Color.parseColor("#6298f0");
                alertTextColor = Color.parseColor("#ffffff");
                imgAlertArrow.setImageResource(R.drawable.icon_arrow_right_white_14_26);
                break;
        }
        alertLayout.setBackgroundColor(alertBgColor);
        tvAlert1.setTextColor(alertTextColor);
        tvAlert2.setTextColor(alertTextColor);
        int alertCount = 0;
        if (merchantUser.isPro() && !merchantUser.isBondSign()) {
            alertLayout.setVisibility(View.VISIBLE);
            if (!merchantUser.isBondPaid()) {
                tvAlert1.setText(R.string.hint_un_bond_pay);
                tvAlert2.setText(R.string.action_bond_pay);
            } else {
                tvAlert1.setText(R.string.hint_un_bond_sign);
                tvAlert2.setText(R.string.btn_bond_pay);
            }
            alertCount++;
        } else {
            alertLayout.setVisibility(View.GONE);
        }
        if (advhNoticeLayout.getChildCount() > 0) {
            View view = advhNoticeLayout.getChildAt(0);
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder != null) {
                holder.line.setVisibility(alertCount > 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    @OnClick({R.id.merchant_layout, R.id.img_bond_icon, R.id.img_level, R.id.order_manage_layout,
            R.id.works_manage_layout, R.id.income_manage_layout, R.id.schedule_manage_layout, R
            .id.shop_check_layout, R.id.alert_layout, R.id.img_ultimate_tags, R.id.pro_layout, R
            .id.todo_order_layout, R.id.todo_reservation_layout, R.id.comment_item_layout, R.id
            .case_manage_layout, R.id.comment_manage_layout, R.id.shop_manage_layout, R.id
            .appointment_manage_layout, R.id.customer_manage_layout, R.id
            .experience_store_layout, R.id.merchant_adv_layout})
    void onClick(View view) {
        Intent intent = null;
        PopupRule popupRule = PopupRule.getDefault();
        if (popupRule.showShopReview(getActivity(), merchantUser)) {
            return;
        }
        switch (view.getId()) {
            case R.id.merchant_layout:
                if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
                    //0 服务商家，1 婚品商家 2.婚车
                    intent = new Intent(getContext(), ShopWebViewActivity.class);
                    intent.putExtra("title", getString(R.string.label_preview_merchant));
                    intent.putExtra("type", 3);
                    intent.putExtra("path",
                            String.format(Constants.WEB_HOST + Constants.HttpPath.GET_SHOP_PREVIEW,
                                    merchantUser.getMerchantId(),
                                    1));
                }
                break;
            case R.id.img_bond_icon:
                intent = new Intent(getActivity(), BondPlanDetailActivity.class);
                break;
            case R.id.comment_manage_layout:
                intent = new Intent(getActivity(), CommentManagerActivity.class);
                break;
            case R.id.shop_manage_layout:
                intent = new Intent(getActivity(), ShopManagerActivity.class);
                break;
            case R.id.img_ultimate_tags:
                goPro();
                break;
            case R.id.pro_layout:
                goPro();
                break;
            case R.id.order_manage_layout:
                intent = new Intent(getActivity(), OrdersManagerActivity.class);
                break;
            case R.id.works_manage_layout:
                if (merchantUser.isSpecialChildMerchant()) {
                    popupRule.showSpecialDisableDlg(getContext());
                } else {
                    intent = new Intent(getActivity(), WorkManageListActivity.class);
                }
                break;
            case R.id.income_manage_layout:
                intent = new Intent(getActivity(), RevenueManageActivity.class);
                break;
            case R.id.schedule_manage_layout:
                intent = new Intent(getActivity(), ScheduleManageActivity.class);
                break;
            case R.id.shop_check_layout:
                intent = new Intent(getActivity(), CheckShopActivity.class);
                break;
            case R.id.alert_layout:
                if (!merchantUser.isBondSign()) {
                    intent = new Intent(getActivity(), BondPlanDetailActivity.class);
                }
                break;
            case R.id.todo_order_layout:
                intent = new Intent(getContext(), OrdersManagerActivity.class);
                break;
            case R.id.todo_reservation_layout:
                intent = new Intent(getContext(), ReservationManagerActivity.class);
                break;
            case R.id.comment_item_layout:
                //评价
                intent = new Intent(getActivity(), CommentManagerActivity.class);
                break;
            case R.id.case_manage_layout:
                //案例管理
                if (merchantUser.isSpecialChildMerchant()) {
                    popupRule.showSpecialDisableDlg(getContext());
                } else {
                    intent = new Intent(getActivity(), WorkManageListActivity.class);
                    intent.putExtra("type", 1);
                }
                break;
            case R.id.appointment_manage_layout:
                //预约管理
                intent = new Intent(getActivity(), ReservationManagerActivity.class);
                break;
            case R.id.customer_manage_layout:
                //客资管理
                intent = new Intent(getActivity(), CustomerManagerActivity.class);
                break;
            case R.id.experience_store_layout:
                //体验店推荐
                intent = new Intent(getContext(), AdvListActivity.class);
                intent.putExtra(AdvListActivity.ARG_ADV_TYPE, AdvListActivity.ADV_FOR_EXPERIENCE);
                break;
            case R.id.merchant_adv_layout:
                //客资推荐管理
                intent = new Intent(getContext(), AdvListActivity.class);
                intent.putExtra(AdvListActivity.ARG_ADV_TYPE, AdvListActivity.ADV_FOR_OTHERS);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

    @OnClick(R.id.un_audit_alert)
    public void onUnAuditAlertClicked() {
        //跳转到开店进度页面
        Intent intent = new Intent(getContext(), OpenShopScheduleActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    private void goPro() {
        PopupRule popupRule = PopupRule.getDefault();
        popupRule.goMerchantProAdsWebActivity(getActivity());
    }

    private void loadNewAdvhMerchants() {
        MerchantUser user = Session.getInstance()
                .getCurrentUser(getActivity());
        if (user == null || user.getMerchantId() == 0) {
            return;
        }
        if (getNewADVHMerchantsTask == null) {
            getNewADVHMerchantsTask = new GetNewADVHMerchantsTask();
            getNewADVHMerchantsTask.executeOnExecutor(Constants.LISTTHEADPOOL,
                    Constants.getAbsUrl(String.format(Constants.HttpPath.ADVH_MERCHANT_URL +
                                    Constants.PAGE_COUNT,
                            9999,
                            1) + "&status=0"));
        }
    }

    private class GetNewADVHMerchantsTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(), params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (getActivity() == null || getActivity().isFinishing() || isDetached()) {
                return;
            }
            if (jsonObject != null && jsonObject.optJSONArray("list") != null) {
                JSONArray array = jsonObject.optJSONArray("list");
                ArrayList<ADVHMerchant> advhMerchants = new ArrayList<>();
                if (array.length() > 0) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        ADVHMerchant advhMerchant = new ADVHMerchant(array.optJSONObject(i));
                        if (advhMerchant.getStatus() == 0) {
                            advhMerchants.add(advhMerchant);
                        }
                    }
                }
                int count = advhNoticeLayout.getChildCount();
                int size = advhMerchants.size();
                if (count > size) {
                    advhNoticeLayout.removeViews(size, count - size);
                }
                if (size > 0) {
                    Collections.sort(advhMerchants, new Comparator<ADVHMerchant>() {
                        @Override
                        public int compare(ADVHMerchant advhMerchant1, ADVHMerchant advhMerchant2) {
                            return Math.round(advhMerchant1.getExpiredAt() -
                                    advhMerchant2.getExpiredAt());
                        }
                    });
                    for (int i = 0; i < size; i++) {
                        View view = advhNoticeLayout.getChildAt(i);
                        ADVHMerchant advhMerchant = advhMerchants.get(i);
                        if (view == null) {
                            view = View.inflate(getActivity(), R.layout.advh_notice_item, null);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ViewHolder holder = (ViewHolder) v.getTag();
                                    if (holder != null && holder.advhMerchant != null) {
                                        Intent intent = new Intent(getActivity(),
                                                ADVHMerchantActivity.class);
                                        intent.putExtra("advhMerchant", holder.advhMerchant);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim
                                                        .slide_in_right,
                                                R.anim.activity_anim_default);
                                    }
                                }
                            });
                            advhNoticeLayout.addView(view);
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder(view);
                            view.setTag(holder);
                        }
                        holder.setAdvhMerchant(advhMerchant);
                        holder.setBg(merchantUser);
                        if (i == 0) {
                            holder.line.setVisibility(alertLayout.getVisibility() == View.VISIBLE
                                    ? View.VISIBLE : View.GONE);
                        } else {
                            holder.line.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            getNewADVHMerchantsTask = null;
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    public void refresh(Object... params) {
        loadNewAdvhMerchants();
    }

    /**
     * 直客宝提醒视图holder
     */
    static class ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.tv_notice_msg)
        TextView tvNoticeMsg;
        @BindView(R.id.notice_layout)
        LinearLayout noticeLayout;
        @BindView(R.id.img_arrow)
        ImageView imgArrow;

        private ADVHMerchant advhMerchant;
        private int proAdvBgColor;
        private int unProAdvBgColor;
        private int proAdvTextColor;
        private int unProAdvTextColor;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            unProAdvBgColor = Color.parseColor("#6298f0");
            proAdvBgColor = Color.parseColor("#5b5249");
            unProAdvTextColor = Color.parseColor("#ffffff");
            proAdvTextColor = Color.parseColor("#dfcb90");
        }

        void setAdvhMerchant(ADVHMerchant advhMerchant) {
            this.advhMerchant = advhMerchant;
            if (!advhMerchant.isSpecial()) {
                tvNoticeMsg.setText(tvNoticeMsg.getContext()
                        .getString(R.string.label_adv_notice,
                                String.valueOf(advhMerchant.getTotalNum())));
            } else {
                tvNoticeMsg.setText(R.string.label_adv_notice2);
            }
        }

        void setBg(MerchantUser user) {
            if (user != null) {
                noticeLayout.setBackgroundColor(user.isPro() ? proAdvBgColor : unProAdvBgColor);
                tvNoticeMsg.setTextColor(user.isPro() ? proAdvTextColor : unProAdvTextColor);
                imgArrow.setImageResource(user.isPro() ? R.drawable.icon_arrow_right_gold_14_22 :
                        R.drawable.icon_arrow_right_white_14_26);
            }
        }
    }

    @Override
    public void onPause() {
        if (postersView != null) {
            postersView.stopAutoCycle();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        setAlertInfo();
        if (postersView != null && flowAdapter != null && flowAdapter.getCount() > 1) {
            postersView.startAutoCycle();
        }
        initLoad(true);
        loadNewAdvhMerchants();
        startVerticalRun();
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (postersView != null && flowAdapter != null && flowAdapter.getCount() > 1) {
                postersView.startAutoCycle();
                startVerticalRun();
            }
            loadNewAdvhMerchants();
        } else {
            if (postersView != null) {
                postersView.stopAutoCycle();
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(userInfoUpdateSub, rxEventSubscription, zipSub);
    }

    private static class ResultZip {
        PosterData posterData;
        ShopInfo shopInfo;
    }

    public static Drawable tintDrawable(Drawable drawable, int colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable)
                .mutate();
        DrawableCompat.setTint(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    ObjectAnimator animator;

    public void startVerticalRun() {
        if (imgUltimateTags == null || imgUltimateTags.getVisibility() == View.GONE || (animator
                != null && animator.isRunning())) {
            return;
        }
        imgUltimateTags.post(new Runnable() {
            @Override
            public void run() {
                int startValue = CommonUtil.dp2px(getContext(), 128) - imgUltimateTags.getHeight();
                int endValue = CommonUtil.dp2px(getContext(), 134) - imgUltimateTags.getHeight();
                animator = ObjectAnimator.ofFloat(imgUltimateTags,
                        "y",
                        startValue,
                        endValue,
                        startValue);
                animator.setDuration(1500);
                animator.setRepeatCount(2);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.start();
            }
        });
    }
}
