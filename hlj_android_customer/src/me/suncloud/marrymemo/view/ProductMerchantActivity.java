package me.suncloud.marrymemo.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonviewlibrary.widgets.ProductFilterMenuView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.ProductMerchantCouponsAdapter;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.fragment.product.ProductListFragment;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.task.HttpDeleteTask;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * Created by mo_yu on 2016/11/15.新版婚品
 */
@Route(path = RouterPath.IntentPath.Customer.PRODUCT_MERCHANT_HOME)
public class ProductMerchantActivity extends HljBaseNoBarActivity implements View
        .OnClickListener, ProductFilterMenuView.OnFilterResultListener, PullToRefreshBase
        .OnRefreshListener<ScrollableLayout>, ProductMerchantCouponsAdapter
        .OnReceiveCouponListener {
    @BindView(R.id.notice)
    View notice;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.merchant_logo)
    RoundedImageView merchantLogo;
    @BindView(R.id.merchant_name)
    TextView merchantName;
    @BindView(R.id.bond_icon)
    ImageView bondIcon;
    @BindView(R.id.collect_btn)
    Button collectBtn;
    @BindView(R.id.contact_btn)
    Button contactBtn;
    @BindView(R.id.merchant_layout)
    LinearLayout merchantLayout;
    @BindView(R.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.filter_menu_view)
    ProductFilterMenuView filterMenuView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.merchant_image)
    ImageView merchantImage;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.coupon_recycler)
    RecyclerView couponRecycler;

    private NoticeUtil noticeUtil;
    private Merchant merchant;
    private long merchantId;
    private int logoWidth;
    private ProductListFragment productFragment;
    private HljHttpSubscriber getMerchantInfoSub;
    private ArrayList<CouponInfo> couponInfos;
    private ProductMerchantCouponsAdapter couponsAdapter;
    private HljHttpSubscriber couponSub;
    private HljHttpSubscriber receiveSub;
    private boolean isFirstReceiveCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_merchant_home);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();

        merchantId = getIntent().getLongExtra("id", 0);
        logoWidth = CommonUtil.dp2px(this, 46);
        collectBtn.setOnClickListener(this);
        contactBtn.setOnClickListener(this);
        scrollableLayout.setOnRefreshListener(this);
        filterMenuView.setOnFilterResultListener(this);
        filterMenuView.setFilterType(ProductFilterMenuView.FILTER_TYPE_PRODUCT_MERCHANT);
        filterMenuView.setScrollableLayout(scrollableLayout.getRefreshableView());
        filterMenuView.initLoad(merchantId);
        notice.setVisibility(Session.getInstance()
                .isNewCart() ? View.VISIBLE : View.GONE);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        isFirstReceiveCoupon = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).getBoolean(Constants.PREF_FIRST_RECEIVE_COUPON, true);
        couponInfos = new ArrayList<>();
        couponsAdapter = new ProductMerchantCouponsAdapter(this, couponInfos);
        couponsAdapter.setOnReceiveCouponListener(this);
        couponRecycler.setHasFixedSize(true);
        couponRecycler.setLayoutManager(new LinearLayoutManager(this, HORIZONTAL, false));
        couponRecycler.setFocusable(false);
        couponRecycler.setAdapter(couponsAdapter);

        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        if (getMerchantInfoSub == null || getMerchantInfoSub.isUnsubscribed()) {
            final boolean isRefreshing = scrollableLayout.isRefreshing();
            getMerchantInfoSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<Merchant>() {
                        @Override
                        public void onNext(Merchant data) {
                            scrollableLayout.getRefreshableView()
                                    .setVisibility(View.VISIBLE);
                            merchant = data;
                            setMerchantView();
                            if (isRefreshing && productFragment != null) {
                                productFragment.setShowProgressBar(false);
                                productFragment.refresh();
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(scrollableLayout)
                    .setPullToRefreshBase(scrollableLayout)
                    .setProgressBar(isRefreshing ? null : progressBar)
                    .build();
            ProductApi.getDetailMerchant(merchantId)
                    .subscribe(getMerchantInfoSub);
        }

        loadCouponInfos();
    }

    private void loadCouponInfos() {
        CommonUtil.unSubscribeSubs(couponSub);
        couponSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CouponInfo>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CouponInfo>> listHljHttpData) {
                        if (listHljHttpData != null && !CommonUtil.isCollectionEmpty
                                (listHljHttpData.getData())) {
                            couponInfos.clear();
                            couponInfos.addAll(listHljHttpData.getData());
                            couponRecycler.setVisibility(View.VISIBLE);
                            couponsAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .build();

        ProductApi.getProductMerchantCoupons(merchantId)
                .subscribe(couponSub);
    }

    private void setMerchantView() {
        merchantLayout.setVisibility(View.VISIBLE);
        bondIcon.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        merchantName.setText(merchant.getName());
        if (merchant.isCollected()) {
            collectBtn.setText(R.string.btn_thread_has_collected);
        } else {
            collectBtn.setText(R.string.btn_thread_collect);
        }
        Glide.with(this)
                .load(ImageUtil.getImagePath2(merchant.getLogoPath(), logoWidth))
                .apply(new RequestOptions().dontAnimate()
                        .centerCrop())
                .into(merchantLogo);
        //特权图片
        String url = null;
        if (merchant.getPrivilegeContent() != null) {
            if (merchant.getPrivilegeContent()
                    .getWidth() != 0) {
                merchantImage.getLayoutParams().height = Math.round(CommonUtil.getDeviceSize
                        (this).x * merchant.getPrivilegeContent()
                        .getHeight() / merchant.getPrivilegeContent()
                        .getWidth());
            }
            url = ImageUtil.getImagePath(merchant.getPrivilegeContent()
                    .getImagePath(), CommonUtil.getDeviceSize(this).x);
        }
        if (!TextUtils.isEmpty(url)) {
            merchantImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions().fitCenter())
                    .into(merchantImage);
        } else {
            merchantImage.setVisibility(View.GONE);
            Glide.with(this)
                    .clear(merchantImage);
            merchantImage.setImageBitmap(null);
        }
    }

    //购物车
    public void onShoppingCart(View view) {
        if (Util.loginBindChecked(this, Constants.Login.SHOP_CART_LOGIN)) {
            new HljTracker.Builder(this).eventableType("Cart")
                    .action("hit")
                    .sid("AB1/A1")
                    .pos(3)
                    .desc("购物车")
                    .build()
                    .send();
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            if (notice != null) {
                notice.setVisibility(View.GONE);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    //分享
    public void onShare(View view) {
        if (merchant != null && merchant.getShareInfo() != null) {
            new HljTracker.Builder(this).eventableId(merchant.getId())
                    .eventableType("Merchant")
                    .action("share")
                    .sid("AB1/A1")
                    .pos(2)
                    .desc("分享")
                    .build()
                    .send();
            ShareDialogUtil.onCommonShare(this, merchant.getShareInfo());
        }
    }

    //收藏
    public void onCollect() {
        if (merchant == null) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.LIKE_LOGIN)) {
            if (merchant.isCollected()) {
                new HljTracker.Builder(this).eventableId(merchant.getId())
                        .eventableType("Merchant")
                        .action("del_collect")
                        .sid("AB1/A1")
                        .pos(1)
                        .desc("取消关注")
                        .build()
                        .send();
                new HttpDeleteTask(ProductMerchantActivity.this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestFailed(Object obj) {

                    }

                    @Override
                    public void onRequestCompleted(Object obj) {
                        JSONObject object = (JSONObject) obj;
                        ReturnStatus status = new ReturnStatus(object.optJSONObject("status"));
                        if (status.getRetCode() == 0) {
                            merchant.setCollected(!merchant.isCollected());
                            if (merchant.isCollected()) {
                                //首次关注商家判断
                                if (Util.isNewFirstCollect(ProductMerchantActivity.this, 6)) {
                                    Util.showFirstCollectNoticeDialog(ProductMerchantActivity
                                            .this, 6);
                                } else {
                                    Util.showToast(R.string.hint_merchant_collect_complete,
                                            ProductMerchantActivity.this);
                                }
                            } else {
                                Util.showToast(R.string.hint_discollect_complete2,
                                        ProductMerchantActivity.this);
                            }
                        }
                    }
                }).execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                                .FOCUS_MERCHANT_URL + "?merchant_id=%s",
                        merchant.getId())));
                collectBtn.setText(R.string.btn_thread_collect);
            } else {
                new HljTracker.Builder(this).eventableId(merchant.getId())
                        .eventableType("Merchant")
                        .action("collect")
                        .sid("AB1/A1")
                        .pos(1)
                        .desc("关注")
                        .build()
                        .send();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("merchant_id", merchant.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new NewHttpPostTask(ProductMerchantActivity.this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestFailed(Object obj) {}

                    @Override
                    public void onRequestCompleted(Object obj) {
                        JSONObject object = (JSONObject) obj;
                        ReturnStatus status = new ReturnStatus(object.optJSONObject("status"));
                        if (status.getRetCode() == 0) {
                            merchant.setCollected(!merchant.isCollected());
                            if (merchant.isCollected()) {
                                //首次关注商家判断
                                if (Util.isNewFirstCollect(ProductMerchantActivity.this, 6)) {
                                    Util.showFirstCollectNoticeDialog(ProductMerchantActivity
                                            .this, 6);
                                } else {
                                    Util.showToast(R.string.hint_merchant_collect_complete,
                                            ProductMerchantActivity.this);
                                }
                            } else {
                                Util.showToast(R.string.hint_discollect_complete2,
                                        ProductMerchantActivity.this);
                            }
                        }
                    }
                }).execute(Constants.getAbsUrl(Constants.HttpPath.FOCUS_MERCHANT_URL),
                        jsonObject.toString());
                collectBtn.setText(R.string.btn_thread_has_collected);
            }
        }

    }

    //私信
    public void onContact() {
        if (merchant == null || merchant.getUserId() <= 0) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.CONTACT_LOGIN)) {
            Intent intent = new Intent(this, WSCustomerChatActivity.class);
            intent.putExtra("user", merchant.toUser(1));
            intent.putExtra("ws_track", ModuleUtils.getWSTrack(merchant));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);

        }
    }

    public void onMsg(View view) {
        if (Util.loginBindChecked(ProductMerchantActivity.this,
                Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(ProductMerchantActivity.this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.bond_icon)
    public void onClick() {
        String url = merchant.getBondSignUrl();
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(ProductMerchantActivity.this, HljWebViewActivity.class);
            intent.putExtra("path", url);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onFilterResult(String order, long categoryId) {
        if (productFragment != null) {
            scrollableLayout.getRefreshableView()
                    .scrollToBottom();
            productFragment.refresh(getUrl(order, categoryId));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_btn:
                onContact();
                break;
            case R.id.collect_btn:
                onCollect();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.Login.LIKE_LOGIN:
                    onCollect();
                    break;
                case Constants.Login.CONTACT_LOGIN:
                    onContact();
                    break;
                case Constants.Login.SHOP_CART_LOGIN:
                    onShoppingCart(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onReceiveCoupon(
            int position, final CouponInfo couponInfo) {
        final List<CouponInfo> coupons = couponsAdapter.getCoupons();
        CommonUtil.unSubscribeSubs(receiveSub);
        receiveSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        ToastUtil.showCustomToast(ProductMerchantActivity.this,
                                R.string.msg_get_succeed);
                        if (isFirstReceiveCoupon) {
                            isFirstReceiveCoupon = false;
                            getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                                    .putBoolean(Constants.PREF_FIRST_RECEIVE_COUPON, false)
                                    .apply();
                            DialogUtil.createSingleButtonDialog(ProductMerchantActivity.this,
                                    ProductMerchantActivity.this.getString(R.string
                                            .msg_get_succeed2),
                                    null,
                                    null)
                                    .show();
                        }
                        if (couponInfo != null) {
                            couponInfo.setUsed(true);
                            couponsAdapter.notifyDataSetChanged();
                        } else {
                            for (CouponInfo coupon : coupons) {
                                if (!coupon.isUsed()) {
                                    coupon.setUsed(true);
                                }
                            }
                            couponsAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(ProductMerchantActivity.this))
                .build();
        StringBuilder sb = new StringBuilder();
        if (couponInfo != null) {
            sb.append(couponInfo.getId());
        } else {
            for (CouponInfo info : coupons) {
                if (!info.isUsed()) {
                    sb.append(info.getId())
                            .append(",");
                }
            }
            if (!TextUtils.isEmpty(sb) && sb.lastIndexOf(",") > 0) {
                sb.deleteCharAt(sb.length() - 1); //移除最后的逗号
            }
        }
        WalletApi.receiveCouponObb(sb.toString())
                .subscribe(receiveSub);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (productFragment == null) {
                productFragment = ProductListFragment.newInstance(getUrl(null, 0));
            }
            scrollableLayout.getRefreshableView()
                    .getHelper()
                    .setCurrentScrollableContainer(productFragment);
            return productFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(R.string.title_merchant).toUpperCase();
        }
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        if (filterMenuView.isShowMenu()) {
            filterMenuView.onHideMenu();
            return;
        }
        super.onBackPressed();
    }

    private String getUrl(String order, long categoryId) {
        StringBuilder url = new StringBuilder(String.format(Constants.HttpPath.GET_MERCHANT_GOODS,
                merchantId));
        if (!TextUtils.isEmpty(order)) {
            url.append("&order=")
                    .append(order);
        }
        if (categoryId > 0) {
            url.append("&category_id=")
                    .append(categoryId);
        }
        return url.toString();
    }

    public void scrollToTop() {
        if (scrollableLayout != null && scrollableLayout.getRefreshableView() != null) {
            scrollableLayout.getRefreshableView()
                    .scrollToTop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notice != null) {
            notice.setVisibility(Session.getInstance()
                    .isNewCart() ? View.VISIBLE : View.GONE);
        }
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(getMerchantInfoSub);
    }

}
