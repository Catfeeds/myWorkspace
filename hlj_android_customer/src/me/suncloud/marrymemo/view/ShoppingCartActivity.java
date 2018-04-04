package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.shoppingcart.ShoppingCartAdapter;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartItem;
import me.suncloud.marrymemo.task.StatusHttpDeleteTask;
import me.suncloud.marrymemo.task.StatusHttpPutTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.view.product.MerchantGoodAddOnActivity;
import me.suncloud.marrymemo.widget.merchant.MerchantCouponDialog;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

public class ShoppingCartActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, ShoppingCartAdapter.OnShoppingCartAdapterListener {

    @Override
    public String pageTrackTagName() {
        return "购物车";
    }

    private final int GO_LOOK_REQUEST = 101;

    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.cb_select_all)
    CheckBox cbSelectAll;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_saved_money)
    TextView tvSavedMoney;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_settle)
    Button btnSettle;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private ArrayList<ShoppingCartGroup> cartGroups;//购物车中对应的商家
    private Dialog dialog;
    private ShoppingCartAdapter shopCardAdapter;
    private Dialog clearDialog;
    private HljHttpSubscriber refreshSubscriber;
    private StatusHttpDeleteTask clearTask;
    private List<ShopProduct> recommendProducts;
    private boolean isShowTopBtn;
    private GridLayoutManager manager;
    private Subscription rxBusSubscription;
    private HashMap<Long, ScheduledFuture> scheduleMap;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool
            (10);
    private MerchantCouponDialog couponDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        ButterKnife.bind(this);
        setOkButton(R.drawable.icon_message_mail_primary_46_46);

        initConstant();
        cbSelectAll.setChecked(false);
    }

    private void initConstant() {
        scheduleMap = new HashMap<>();
        registerRxBusEvent();
        cartGroups = new ArrayList<>();
        recommendProducts = new ArrayList<>();
        shopCardAdapter = new ShoppingCartAdapter(this);
        shopCardAdapter.setOnShoppingCartAdapterListener(this);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addOnScrollListener(onScrollListener);
        manager = new GridLayoutManager(this, 2);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int count = findCartGroupCount();
                count++;//加上divider的位置
                int productCount = recommendProducts.size();
                if (position < count || position == (count + productCount)) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration(CommonUtil.dp2px(this, 8)));
        recyclerView.getRefreshableView()
                .setAdapter(shopCardAdapter);
    }

    private int findCartGroupCount() {
        int count = 0;
        if (cartGroups.isEmpty()) {
            count++;
        } else {
            for (ShoppingCartGroup group : cartGroups) {
                //header
                count++;
                if (group.getCartList() != null) {
                    count += group.getCartList()
                            .size();
                }
                //footer
                count++;
            }
        }
        return count;
    }

    private void initLoad() {
        onRefresh(null);
    }

    private void registerRxBusEvent() {
        if (rxBusSubscription == null || rxBusSubscription.isUnsubscribed()) {
            rxBusSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            if (rxEvent == null) {
                                return;
                            }
                            switch (rxEvent.getType()) {
                                case CITY_CHANGE:
                                case REFRESH_SHOPPING_CART:
                                    //城市切换 或者选完婚品以后 刷新列表
                                    onRefresh(null);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }

    private void setShoppingCardItem(List<ShoppingCartGroup> groupList) {
        cartGroups.clear();
        cartGroups.addAll(groupList);
    }

    private void setRecommendProduct(List<ShopProduct> products) {
        recommendProducts.clear();
        if (products == null) {
            return;
        }
        recommendProducts.addAll(products);
    }

    private void setCartCount() {
        int cartCount = 0;
        for (ShoppingCartGroup group : cartGroups) {
            cartCount += group.getCartList()
                    .size();
        }
        Session.getInstance()
                .setCartCount(this, cartCount);
    }

    private void setRecyclerViewBackground() {
        if (recommendProducts == null || recommendProducts.isEmpty()) {
            recyclerView.getRefreshableView()
                    .setBackgroundColor(getResources().getColor(R.color.colorBackground));
        } else {
            recyclerView.getRefreshableView()
                    .setBackgroundColor(getResources().getColor(R.color.colorWhite));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLoad();
    }

    @Override
    public void onOkButtonClick() {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.btn_scroll_top)
    void onScrollTop() {
        if (manager.findFirstVisibleItemPosition() > 5) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRefreshableView()
                            .smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        }
    }


    /**
     * 从内存缓存中获取已存的购物车列表,并将其按照商家分组
     */
    private void calculateShoppingCartCroup(boolean needNotifyAdapter) {
        //找出失效的婚品
        List<ShoppingCartItem> invalidList = new ArrayList<>();
        for (ShoppingCartGroup cartGroup : cartGroups) {
            List<ShoppingCartItem> cartItemList = cartGroup.getCartList();
            Iterator<ShoppingCartItem> itemIterator = cartItemList.iterator();
            while (itemIterator.hasNext()) {
                ShoppingCartItem item = itemIterator.next();
                if (!item.isValid()) {
                    invalidList.add(item);
                    itemIterator.remove();
                }
            }
        }

        //商家的婚品排序
        for (ShoppingCartGroup cartGroup : cartGroups) {
            cartGroup.sortCartItemList();
        }

        //商家 时间排序
        Collections.sort(cartGroups, new Comparator<ShoppingCartGroup>() {
            @Override
            public int compare(ShoppingCartGroup group1, ShoppingCartGroup group2) {
                if ((group1.getCartList() == null || group1.getCartList()
                        .isEmpty()) || (group2.getCartList() == null || group2.getCartList()
                        .isEmpty())) {
                    return 0;
                }
                ShoppingCartItem lhs = group1.getCartList()
                        .get(0);
                ShoppingCartItem rhs = group2.getCartList()
                        .get(0);
                if (lhs.isValid() && rhs.isValid()) {
                    return lhs.getCreatedAt()
                            .isAfter(rhs.getCreatedAt()) ? -1 : 1;
                } else if (lhs.isValid() && !rhs.isValid()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        // 排序后最后加入失效的分组
        if (!invalidList.isEmpty()) {
            ShoppingCartGroup invalidGroup = new ShoppingCartGroup();
            invalidGroup.setCartList(invalidList);
            invalidGroup.setInvalidGroup(true);
            cartGroups.add(invalidGroup);
        }
        removeNoCartItemGroup();
        if (cartGroups.isEmpty()) {
            bottomLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
        }

        setPriceInfo(null, true);
        if (needNotifyAdapter) {
            shopCardAdapter.notifyDataSetChanged();
        }
    }

    //去除婚品列表是空的商家
    private void removeNoCartItemGroup() {
        Iterator<ShoppingCartGroup> groupIterator = cartGroups.iterator();
        while (groupIterator.hasNext()) {
            ShoppingCartGroup group = groupIterator.next();
            if (group.getCartList() == null || group.getCartList()
                    .isEmpty()) {
                groupIterator.remove();
            }
        }
    }


    private void setPriceInfo() {
        double totalPrice = 0;
        double totalMarketPrice = 0;
        int count = 0;
        for (ShoppingCartGroup group : cartGroups) {
            for (ShoppingCartItem item : group.getCartList()) {
                if (item.isChecked() && item.isValid()) {
                    count++;
                    totalMarketPrice += item.getSku()
                            .getMarketPrice() * item.getQuantity();
                    totalPrice += item.getSku()
                            .getShowPrice() * item.getQuantity();
                }
            }
        }
        btnSettle.setText(getString(R.string.btn_go_settle, String.valueOf(count)));
        tvTotalPrice.setText(getString(R.string.label_price,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(totalPrice)));
        tvSavedMoney.setText(getString(R.string.label_price,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(totalMarketPrice - totalPrice)));

        // 设置是否是所有都选中
        boolean isAllChecked = true;
        boolean isAllInvalid = true;
        for (ShoppingCartGroup group : cartGroups) {
            if (!group.isAllMerchantProductChecked()) {
                isAllChecked = false;
            }
            if (!group.isAllMerchantProductInvalid()) {
                isAllInvalid = false;
            }
        }

        cbSelectAll.setChecked(isAllChecked && !isAllInvalid);
    }

    private void setPriceInfo(ShoppingCartGroup cartGroup, boolean sortAll) {
        if (sortAll) {
            //按照商品运费模板排序
            for (ShoppingCartGroup group : cartGroups) {
                group.sortCartItemByFreeShipping();
            }
        } else {
            if (cartGroup != null) {
                cartGroup.sortCartItemByFreeShipping();
            }
        }
        setPriceInfo();
    }

    @OnClick(R.id.cb_select_all)
    void selectAll() {
        new HljTracker.Builder(this).sid("AC1/D1")
                .pos(1)
                .desc("全选")
                .build()
                .send();
        for (ShoppingCartGroup group : cartGroups) {
            if (cbSelectAll.isChecked()) {
                group.selectAll();
            } else {
                group.deSelectAll();
            }
        }

        setPriceInfo(null, true);
        shopCardAdapter.notifyDataSetChanged();
    }

    /**
     * 由于加减操作可能会连续点击,造成重复提交,所以使用task线程来管理,当两个对同一个item的加减操作在一秒内被触发的话,只提交后一个
     *
     * @param item
     */
    private void scheduleTask(
            final ShoppingCartGroup cartGroup, final ShoppingCartItem item) {
        if (scheduleMap.containsKey(item.getId())) {
            // 取消之前存在的那个任务
            scheduleMap.get(item.getId())
                    .cancel(true);
            Log.d(ShoppingCartActivity.class.getSimpleName(),
                    "There was same item task exits, " + "cancel the old one");
        }
        ScheduledFuture taskHandle = scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                changeQuantity(cartGroup, item);
                scheduleMap.remove(item.getId());
            }
        }, 1, TimeUnit.SECONDS);
        scheduleMap.put(item.getId(), taskHandle);
    }

    private void changeQuantity(
            final ShoppingCartGroup cartGroup, final ShoppingCartItem item) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", item.getId());
            jsonObject.put("quantity", item.getQuantity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new StatusHttpPutTask(ShoppingCartActivity.this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                // 成功更改数量,获取新的数量,刷新列表并控制增加按钮的颜色
                Log.e(ShoppingCartActivity.class.getSimpleName(),
                        "改变数量请求：" + Thread.currentThread()
                                .getName());
                Log.d(ShoppingCartActivity.class.getSimpleName(),
                        " success, refresh " + "the" + " data");
                JSONObject dataObj = (JSONObject) object;
                if (dataObj != null) {
                    int showNum = dataObj.optInt("show_num", 0);
                    item.getSku()
                            .setShowNum(showNum);
                    // 刷新列表中的数据
                    setPriceInfo(cartGroup, false);
                    refreshData();
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                Log.d(ShoppingCartActivity.class.getSimpleName(), "failed, refresh the" + " data");
                // 失败
                // 增加或减少达到库存限制,设置为当前库存
                item.setQuantity(item.getSku()
                        .getShowNum());
                //改变数量后
                shopCardAdapter.notifyDataSetChanged();
                Util.postFailToast(ShoppingCartActivity.this,
                        returnStatus,
                        R.string.msg_fail_change_quantity,
                        network);
            }
        }).executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.CHANGE_SHOPPING_CART_ITEM_QUANTITY),
                jsonObject.toString());
    }

    private void deleteProduct(final ShoppingCartGroup group, final ShoppingCartItem item) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        dialog = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_delete_shop_cart_item);
        confirmBtn.setText(R.string.label_confirm);
        cancelBtn.setText(R.string.label_cancel);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除item
                dialog.dismiss();
                new HljTracker.Builder(ShoppingCartActivity.this).eventableId(item.getProduct()
                        .getId())
                        .eventableType("Article")
                        .action("out_cart")
                        .additional(String.valueOf(item.getSku()
                                .getId()))
                        .sid("AC1/C1")
                        .pos(5)
                        .desc("删除")
                        .build()
                        .send();
                progressBar.setVisibility(View.VISIBLE);
                new StatusHttpDeleteTask(ShoppingCartActivity.this, new StatusRequestListener() {
                    @Override
                    public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                        // 成功删除,刷新列表
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ShoppingCartActivity.this,
                                R.string.msg_success_to_delete_cart_item,
                                Toast.LENGTH_SHORT)
                                .show();
                        group.getCartList()
                                .remove(item);
                        Session.getInstance()
                                .refreshCartItemsCount(ShoppingCartActivity.this);
                        refreshData();
                    }

                    @Override
                    public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                        progressBar.setVisibility(View.GONE);
                        Util.postFailToast(ShoppingCartActivity.this,
                                returnStatus,
                                R.string.msg_fail_to_delete,
                                network);
                    }
                }).execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                                .DELETE_SHOP_CART_ITEM,
                        item.getId())));
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(v);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);

        dialog.show();
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        Observable<List<ShoppingCartGroup>> shoppingCardItemObb = ProductApi.getShoppingCartItem();
        Observable<HljHttpData<List<ShopProduct>>> productObb = ProductApi
                .getUserRecommendProduct(2);
        Observable<ResultZip> zipObb = Observable.zip(shoppingCardItemObb,
                productObb,
                new Func2<List<ShoppingCartGroup>, HljHttpData<List<ShopProduct>>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            List<ShoppingCartGroup> groups,
                            HljHttpData<List<ShopProduct>> listHljHttpData) {
                        ResultZip zip = new ResultZip();
                        zip.cartGroupList = groups;
                        if (listHljHttpData != null) {
                            zip.products = listHljHttpData.getData();
                        }
                        return zip;
                    }
                });
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setPullToRefreshBase(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        setRecommendProduct(resultZip.products);
                        setShoppingCardItem(resultZip.cartGroupList);
                        setRecyclerViewBackground();
                        setCartCount();
                        refreshData();
                    }
                })
                .build();
        zipObb.subscribe(refreshSubscriber);
    }

    private void refreshData() {
        calculateShoppingCartCroup(true);
        shopCardAdapter.setData(cartGroups, recommendProducts);
    }

    @OnClick(R.id.btn_settle)
    void goConfirm() {
        if (cartGroups == null || cartGroups.isEmpty()) {
            Toast.makeText(this, R.string.msg_empty_cart_select, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        new HljTracker.Builder(this).eventableType("Cart")
                .action("account")
                .sid("AC1/D1")
                .pos(2)
                .desc("结算")
                .build()
                .send();
        ArrayList<ShoppingCartGroup> shoppingCartGroups = new ArrayList<>();
        for (ShoppingCartGroup group : cartGroups) {
            ShoppingCartGroup g = new ShoppingCartGroup();
            g.setMerchant(group.getMerchant());
            if (g.getCartList() == null) {
                g.setCartList(new ArrayList<ShoppingCartItem>());
            }
            if (group.getCartList() == null) {
                continue;
            }
            for (int i = 0; i < group.getCartList()
                    .size(); i++) {
                ShoppingCartItem item = group.getCartList()
                        .get(i);
                if (item.isChecked() && item.isValid() && item.getQuantity() >= 1) {
                    g.addItem(item);
                }
            }

            if (g.getCartList()
                    .size() > 0) {
                shoppingCartGroups.add(g);
            }
        }
        if (shoppingCartGroups.isEmpty()) {
            Toast.makeText(this, R.string.msg_empty_cart_select, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent intent = new Intent(this, ProductOrderConfirmActivity.class);
        intent.putExtra("cart_groups", shoppingCartGroups);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void showClearDialog() {
        if (clearDialog != null && clearDialog.isShowing()) {
            return;
        }
        if (clearDialog == null) {
            clearDialog = DialogUtil.createDoubleButtonDialog(this,
                    "确认清空失效宝贝吗？",
                    getString(R.string.action_ok),
                    getString(R.string.action_cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clearInvalid();
                        }
                    },
                    null);
        }
        clearDialog.show();
    }

    private ShoppingCartGroup findInvalidGroup() {
        for (ShoppingCartGroup croup : cartGroups) {
            if (croup.isInvalidGroup()) {
                return croup;
            }
        }
        return null;
    }

    //清除失效婚品
    private void clearInvalid() {
        if (clearTask != null && clearTask.getStatus() != AsyncTask.Status.RUNNING) {
            return;
        }
        if (cartGroups.isEmpty()) {
            return;
        }
        final ShoppingCartGroup invalidGroup = findInvalidGroup();
        StringBuilder id = new StringBuilder();
        if (invalidGroup != null) {
            if (invalidGroup.isAllInvalid()) {
                List<ShoppingCartItem> items = invalidGroup.getCartList();
                for (ShoppingCartItem item : items) {
                    id.append(String.valueOf(item.getId()))
                            .append(",");
                }
            }
        }
        if (id.lastIndexOf(",") > 0) {
            id.deleteCharAt(id.lastIndexOf(","));
        }
        progressBar.setVisibility(View.VISIBLE);
        clearTask = new StatusHttpDeleteTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                if (ShoppingCartActivity.this.isFinishing()) {
                    return;
                }
                // 成功删除,刷新列表
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ShoppingCartActivity.this, "操作成功", Toast.LENGTH_SHORT)
                        .show();
                calculateShoppingCartCroup(true);
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (ShoppingCartActivity.this.isFinishing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(ShoppingCartActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_delete,
                        network);
            }
        });
        clearTask.execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                        .DELETE_SHOP_CART_ITEM,
                id.toString())));
    }

    @Override
    protected void onFinish() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (clearDialog != null && clearDialog.isShowing()) {
            clearDialog.dismiss();
        }

        if (clearTask != null) {
            clearTask.cancel(true);
        }

        if (!scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, rxBusSubscription);
        super.onFinish();
    }

    @Override
    public void onCbMerchantClick(ShoppingCartGroup cartGroup) {
        cartGroup.toggleGroup();
        shopCardAdapter.notifyDataSetChanged();
        setPriceInfo(cartGroup, false);
    }

    @Override
    public void onEmptyClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.ARG_MAIN_ACTION, MainActivity.MAIN_ACTION_PRODUCT);
        startActivity(intent);
    }

    @Override
    public void onClear() {
        if (findInvalidGroup() == null) {
            Toast.makeText(this, "购物车中没有无失效宝贝", Toast.LENGTH_SHORT)
                    .show();
        } else {
            showClearDialog();
        }
    }

    @Override
    public void onAddOn(ShoppingCartGroup cartGroup) {
        if (cartGroup == null || cartGroup.getMerchant() == null) {
            return;
        }
        Intent intent = new Intent(this, ProductMerchantActivity.class);
        intent.putExtra("id",
                cartGroup.getMerchant()
                        .getId());
        startActivity(intent);
    }

    @Override
    public void onCbItemClick(ShoppingCartGroup shoppingCartGroup) {
        setPriceInfo(shoppingCartGroup, false);
        shopCardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDelete(
            ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item) {
        deleteProduct(shoppingCartGroup, item);
    }

    @Override
    public void onPlus(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item) {
        setPriceInfo(shoppingCartGroup, false);
        scheduleTask(shoppingCartGroup, item);
    }

    @Override
    public void onSubtract(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item) {
        setPriceInfo(shoppingCartGroup, false);
        scheduleTask(shoppingCartGroup, item);
    }

    @Override
    public void addOn(long merchantId, long expressTemplateId) {
        Intent intent = new Intent(this, MerchantGoodAddOnActivity.class);
        intent.putExtra("merchantId", merchantId);
        intent.putExtra("expressTemplateId", expressTemplateId);
        startActivity(intent);
    }

    @Override
    public void onGetCoupon(ShoppingCartGroup group) {
        if (group == null || (couponDialog != null && couponDialog.isShowing())) {
            return;
        }
        if (couponDialog == null) {
            couponDialog = new MerchantCouponDialog(this,
                    group.getMerchant()
                            .getId(),
                    0);
        }
        couponDialog.getCoupons();
    }

    //隐藏回到顶部的按钮
    private void hideFiltrateAnimation() {
        if (!isShowTopBtn) {
            return;
        }
        isShowTopBtn = false;
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            btnScrollTop.startAnimation(animation);
        }
    }

    //显示回到顶部的按钮
    private void showFiltrateAnimation() {
        if (isShowTopBtn) {
            return;
        }
        isShowTopBtn = true;
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            btnScrollTop.startAnimation(animation);
            btnScrollTop.setVisibility(View.VISIBLE);
        }
    }

    private int getScrollYDistance() {
        int position = manager.findFirstVisibleItemPosition();
        View firstVisibleChildView = manager.findViewByPosition(position);
        int dis = 0;
        if (firstVisibleChildView != null) {
            int itemHeight = firstVisibleChildView.getHeight();
            dis = (position) * itemHeight - firstVisibleChildView.getTop();
        }
        return dis;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_LOOK_REQUEST) {
            onRefresh(recyclerView);
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (recyclerView != null) {
                boolean show = getScrollYDistance() < CommonUtil.getDeviceSize
                        (ShoppingCartActivity.this).y;
                if (manager.findFirstVisibleItemPosition() < 3 && show) {
                    hideFiltrateAnimation();
                } else {
                    showFiltrateAnimation();
                }
            }
        }
    };

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private GridLayoutManager.LayoutParams lp;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            int left = lp.getSpanIndex() == 0 ? 0 : space / 2;
            int right = lp.getSpanIndex() == 0 ? space / 2 : 0;
            int position = parent.getChildAdapterPosition(view);
            int count = findCartGroupCount();
            count++;//加上divider的位置
            int productCount = count + recommendProducts.size();
            if (position > count && position < productCount) {
                outRect.set(left, 0, right, 0);
            }
        }

    }

    class ResultZip {
        List<ShoppingCartGroup> cartGroupList;
        List<ShopProduct> products;
    }
}
