package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.product.MerchantShippingFee;
import com.hunliji.hljcommonlibrary.models.product.wrappers.ShippingFeeList;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.orders.ProductConfirmOrderAdapter;
import me.suncloud.marrymemo.adpter.orders.RedPacketsListAdapter;
import me.suncloud.marrymemo.api.Address.AddressApi;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.ShippingAddress;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartItem;
import me.suncloud.marrymemo.model.wallet.MerchantCouponList;

import com.hunliji.hljcardcustomerlibrary.models.RedPacket;

import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.product.AfterPayProductOrderActivity;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;


public class ProductOrderConfirmActivity extends HljBaseActivity implements View.OnClickListener,
        ProductConfirmOrderAdapter.OnRefreshListener {

    @Override
    public String pageTrackTagName() {
        return "婚品填写订单页";
    }

    TextView tvReceiverName;
    TextView tvReceiverAddress;
    TextView tvReceiverPhone;
    View redPacketLayout;
    TextView tvAvailableCount;
    TextView tvRedPacketSavedMoney;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.img_empty_hint)
    ImageView imgEmptyHint;
    @BindView(R.id.img_net_hint)
    ImageView imgNetHint;
    @BindView(R.id.text_empty_hint)
    TextView textEmptyHint;
    @BindView(R.id.text_empty2_hint)
    TextView textEmpty2Hint;
    @BindView(R.id.btn_empty_hint)
    Button btnEmptyHint;
    @BindView(R.id.empty_hint_layout)
    LinearLayout emptyHintLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Subscriber<PayRxEvent> paySubscriber;

    private HashMap<Long, MerchantCouponList> merchantCouponMap;
    private ArrayList<ShoppingCartGroup> cartGroups;
    private ShoppingCartGroup cartGroup;
    private ProductConfirmOrderAdapter adapter;
    private View footView;
    private View headView;
    private ShippingAddress selectAddress;
    private LinearLayout addressLayout;
    private ArrayList<RedPacket> redPackets;
    private String productIds;
    private double totalPrice;
    private double totalCartPrice;//购物车商品总金额（不含运费）
    private double totalSaveMoney;//省钱金额（商品金额小于优惠金额，按照商品金额计算（不含运费））
    private boolean submitting;
    private boolean isShippingFeeError; // 请求运费错误
    private Dialog redPacketDialog;
    private RedPacketsListAdapter redPacketsAdapter;
    private RedPacket selectedRedPacket;
    private RedPacket pendingRedPacket;
    private HljHttpSubscriber couponSub;
    private HljHttpSubscriber shippingFeeSub;
    private HljHttpSubscriber defaultAddressSub;
    private HljHttpSubscriber initSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_order_confirm);
        ButterKnife.bind(this);

        headView = getLayoutInflater().inflate(R.layout.product_order_confirm_list_head, null);
        footView = getLayoutInflater().inflate(R.layout.product_order_confirm_list_foot, null);

        addressLayout = headView.findViewById(R.id.address_layout);
        tvReceiverAddress = headView.findViewById(R.id.tv_receiver_address);
        tvReceiverName = headView.findViewById(R.id.tv_receiver_name);
        tvReceiverPhone = headView.findViewById(R.id.tv_receiver_phone);

        redPacketLayout = footView.findViewById(R.id.red_packet_layout);
        tvAvailableCount = footView.findViewById(R.id.tv_available_count);
        tvRedPacketSavedMoney = footView.findViewById(R.id.tv_red_packet_saved_money);
        addressLayout.setOnClickListener(this);

        cartGroups = new ArrayList<>();
        redPackets = new ArrayList<>();
        merchantCouponMap = new HashMap<>();
        adapter = new ProductConfirmOrderAdapter(this, cartGroups);
        adapter.setHeaderView(headView);
        adapter.setFooterView(footView);
        adapter.setMerchantCouponMap(merchantCouponMap);
        adapter.setShippingFeeError(isShippingFeeError);
        adapter.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        // 可能传入group列表也可能传入单个group
        ArrayList<ShoppingCartGroup> groups = (ArrayList<ShoppingCartGroup>) getIntent()
                .getSerializableExtra(
                "cart_groups");
        cartGroup = getIntent().getParcelableExtra("cart_group");
        if (groups != null) {
            cartGroups.addAll(groups);
        }
        if (cartGroup != null) {
            cartGroups.add(cartGroup);
        }

        if (selectAddress == null) {
            // 请求默认地址和优惠券
            initLoad();
        } else {
            getCouponList();
        }
        setPriceInfo();
        adapter.notifyDataSetChanged();
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(initSub);
        Observable<JsonObject> aObservable = AddressApi.getDefaultAddressObb();
        Observable<List<MerchantCouponList>> oObservable = OrderApi.postAvailableProductCouponList(
                getCouponAddressMap());
        Observable<ResultZip> observable = Observable.zip(aObservable,
                oObservable,
                new Func2<JsonObject, List<MerchantCouponList>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            JsonObject JsonObject, List<MerchantCouponList> merchantCouponLists) {
                        ResultZip zip = new ResultZip();
                        zip.jsonObject = JsonObject;
                        zip.merchantCouponLists = merchantCouponLists;
                        return zip;
                    }
                });
        initSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        if (resultZip.jsonObject != null) {
                            selectAddress = new ShippingAddress(resultZip.jsonObject);
                        }
                        setShippingAddressInfo();
                        setCouponData(resultZip.merchantCouponLists);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        setShippingAddressInfo();
                    }
                })
                .setProgressBar(progressBar)
                .setDataNullable(true)
                .build();

        observable.subscribe(initSub);
    }

    private Map<String, Object> getCouponAddressMap() {
        Map<String, Object> map = new HashMap<>();
        List<Object> jsonArray = new ArrayList<>();
        for (ShoppingCartGroup group : cartGroups) {
            Map<String, Object> jsonObject = new HashMap<>();
            jsonObject.put("merchant_id",
                    group.getMerchant()
                            .getId());
            double money = group.getShippingFee();
            for (ShoppingCartItem item : group.getCartList()) {
                money += item.getSku()
                        .getShowPrice() * item.getQuantity();
            }
            jsonObject.put("money", money);
            jsonArray.add(jsonObject);
        }
        map.put("submit_orders", jsonArray);
        return map;
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        JsonObject jsonObject;
        @HljRZField
        List<MerchantCouponList> merchantCouponLists;
    }

    /**
     * 判断有没有默认地址,有的话显示默认地址,没有的话,显示选择地址信息
     */
    private void setShippingAddressInfo() {
        if (selectAddress != null) {
            // 每次设置地址都需要重新请求邮费
            postForShippingFee();

            tvReceiverName.setText(selectAddress.getBuyerName());
            tvReceiverName.setVisibility(View.VISIBLE);
            tvReceiverAddress.setText(selectAddress.toString());
            tvReceiverAddress.setVisibility(View.VISIBLE);
            tvReceiverPhone.setText(selectAddress.getMobilePhone());
            tvReceiverPhone.setVisibility(View.VISIBLE);

            headView.findViewById(R.id.empty_head)
                    .setVisibility(View.GONE);
        } else {
            tvReceiverName.setText(R.string.hint_set_shipping_address);
            tvReceiverName.setVisibility(View.VISIBLE);
            tvReceiverAddress.setVisibility(View.GONE);
            tvReceiverPhone.setVisibility(View.GONE);

            headView.findViewById(R.id.empty_head)
                    .setVisibility(View.VISIBLE);
        }
    }

    private void setPriceInfo() {
        if (cartGroups == null || cartGroups.isEmpty()) {
            bottomLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            totalCartPrice = 0;
            totalSaveMoney = 0;
            double totalFee = 0;
            totalPrice = 0;
            for (ShoppingCartGroup group : cartGroups) {
                double totalGroupPrice = 0;
                for (ShoppingCartItem item : group.getCartList()) {
                    totalGroupPrice += item.getSku()
                            .getShowPrice() * item.getQuantity();
                }
                if (group.getCouponRecord() != null) {
                    totalGroupPrice -= group.getCouponRecord()
                            .getValue();
                }
                if (totalGroupPrice < 0) {
                    totalGroupPrice = 0;
                }
                totalCartPrice += totalGroupPrice;
                totalFee += group.getShippingFee();
            }
            //红包不能抵扣邮费
            totalSaveMoney = totalCartPrice;
            totalCartPrice = totalCartPrice - (selectedRedPacket == null ? 0 : selectedRedPacket
                    .getAmount());
            if (totalCartPrice <= 0) {
                totalCartPrice = 0;
            }
            totalPrice = totalCartPrice + totalFee;
            tvTotalPrice.setText(NumberFormatUtil.formatDouble2StringWithTwoFloat(totalPrice));
        }
    }

    @OnClick(R.id.btn_submit)
    void submit() {
        // 先验证数据有效性
        if (selectAddress == null) {
            Toast.makeText(this, R.string.msg_empty_shipping_address, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (cartGroups.isEmpty()) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        final JSONObject extraObject = new JSONObject();
        try {
            JSONArray groupsArr = new JSONArray();
            JSONArray extraArray = new JSONArray();
            for (ShoppingCartGroup group : cartGroups) {
                if (!JSONUtil.isEmpty(group.getLeaveMessage()) && Util.calculateLength(group
                        .getLeaveMessage()) > 300) {
                    Toast.makeText(this, R.string.msg_too_much_message, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                JSONObject groupObj = new JSONObject();
                JSONArray itemsArr = new JSONArray();
                groupObj.put("merchant_id",
                        group.getMerchant()
                                .getId());
                if (group.getCouponRecord() != null && group.getCouponRecord()
                        .getId() > 0) {
                    groupObj.put("user_coupon_id",
                            group.getCouponRecord()
                                    .getId());
                }
                for (ShoppingCartItem item : group.getCartList()) {
                    JSONObject itemObj = new JSONObject();
                    if (item.getId() > 0) {
                        itemObj.put("cart_id", item.getId());
                    }
                    itemObj.put("sku_id",
                            item.getSku()
                                    .getId());
                    itemObj.put("product_id",
                            item.getProduct()
                                    .getId());
                    itemObj.put("quantity", item.getQuantity());
                    itemsArr.put(itemObj);

                    extraArray.put(itemObj);
                }

                groupObj.put("sub_items", itemsArr);
                groupObj.put("message", group.getLeaveMessage());

                groupsArr.put(groupObj);
            }

            jsonObject.put("items", groupsArr);
            jsonObject.put("address_id", selectAddress.getId());
            if (selectedRedPacket != null && selectedRedPacket.getId() != -1) {
                jsonObject.put("red_packet_id", selectedRedPacket.getId());
            }
            extraObject.put("sub_items", extraArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);

        // 避免重复提交
        if (submitting) {
            return;
        } else {
            submitting = true;
        }

        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                // 提交成功后更新本地购物车数据
                Session.getInstance()
                        .refreshCartItemsCount(ProductOrderConfirmActivity.this);
                submitting = false;
                JSONObject jsonObject = new JSONObject();
                JSONObject resultObject = (JSONObject) object;
                JSONArray idArray = resultObject.optJSONArray("id");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idArray.length(); i++) {
                    long id = idArray.optLong(i);
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(String.valueOf(id));
                }
                double productMoney = resultObject.optDouble("actual_money",
                        0) - resultObject.optDouble("red_packet_money") - resultObject.optDouble(
                        "aid_money");
                if (productMoney < 0) {
                    productMoney = 0;
                }
                double money = productMoney + resultObject.optDouble("shipping_fee");
                try {
                    jsonObject.put("order_ids", sb.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goPayOrder(jsonObject, money);
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                submitting = false;
                Util.postFailToast(ProductOrderConfirmActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_submit_order,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.SUBMIT_PRODUCT_ORDER),
                jsonObject.toString());

    }

    private void goPayOrder(JSONObject jsonObject, double money) {
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    Intent intent;
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            intent = new Intent(ProductOrderConfirmActivity.this,
                                    AfterPayProductOrderActivity.class);
                            intent.putExtra("order_type",
                                    Constants.OrderType.WEDDING_PRODUCT_ORDER);
                            intent.putExtra("product_ids", productIds);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                            break;
                        case PAY_CANCEL:
                            intent = new Intent(ProductOrderConfirmActivity.this,
                                    MyOrderListActivity.class);
                            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                                    RouterPath.IntentPath.Customer.MyOrder.Tab.PRODUCT_ORDER);
                            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN,
                                    true);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                            break;
                    }
                }
            };
        }
        PayConfig.Builder builder = new PayConfig.Builder(this);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null,
                DataConfig.getWalletPayAgents());
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.PRODUCT_ORDER_PAYMENT))
                .price(money > 0 ? money : 0)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address_layout:
                if (selectAddress != null) {
                    Intent intent = new Intent(this, ShippingAddressListActivity.class);
                    intent.putExtra("select", true);
                    if (selectAddress != null) {
                        intent.putExtra("selected_address", selectAddress);
                    }
                    startActivityForResult(intent, Constants.RequestCode.SELECT_SHIPPING_ADDRESS);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else {
                    Intent intent = new Intent(this, EditShippingAddressActivity.class);
                    intent.putExtra("is_first", true);
                    intent.putExtra("select", true);
                    startActivityForResult(intent, Constants.RequestCode.SELECT_SHIPPING_ADDRESS);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        getRedPacketList();
        postForShippingFee();
        setPriceInfo();
    }

    private void getDefaultAddress() {
        CommonUtil.unSubscribeSubs(defaultAddressSub);
        defaultAddressSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<JsonObject>() {
                    @Override
                    public void onNext(JsonObject jsonObject) {
                        selectAddress = new ShippingAddress(jsonObject);
                        setShippingAddressInfo();
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        setShippingAddressInfo();
                    }
                })
                .setProgressBar(progressBar)
                .build();
        AddressApi.getDefaultAddressObb()
                .subscribe(defaultAddressSub);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.SELECT_SHIPPING_ADDRESS:
                    if (data != null) {
                        ShippingAddress address = (ShippingAddress) data.getSerializableExtra(
                                "address");
                        boolean deletedSelectedAddress = data.getBooleanExtra(
                                "deleted_selected_address",
                                false);
                        if (address != null) {
                            selectAddress = address;

                            setShippingAddressInfo();
                        }
                        if (deletedSelectedAddress) {
                            selectAddress = null;
                            getDefaultAddress();
                        }
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void postForShippingFee() {
        CommonUtil.unSubscribeSubs(shippingFeeSub);
        shippingFeeSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<ShippingFeeList>() {
                    @Override
                    public void onNext(ShippingFeeList shippingFeeList) {
                        isShippingFeeError = false;
                        for (int i = 0; i < shippingFeeList.getShippingFees()
                                .size(); i++) {
                            // 更新数据中的邮费信息
                            MerchantShippingFee merchantShippingFee = shippingFeeList
                                    .getShippingFees()
                                    .get(i);
                            long merchantId = merchantShippingFee.getMerchantId();
                            double shippingFee = merchantShippingFee.getShippingFee();
                            for (int j = 0; j < cartGroups.size(); j++) {
                                if (cartGroups.get(j)
                                        .getMerchant()
                                        .getId() == merchantId) {
                                    cartGroups.get(j)
                                            .setShippingFee(shippingFee);
                                }
                            }
                        }
                        // 更新邮费后刷新列表,重新计算合计
                        adapter.notifyDataSetChanged();
                        setPriceInfo();
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        isShippingFeeError = true;
                    }
                })
                .setDataNullable(true)
                .setProgressBar(progressBar)
                .build();
        try {
            StringBuilder sb = new StringBuilder();
            Map<String, Object> map = new HashMap<>();
            List<Object> jsonArray = new ArrayList<>();
            for (ShoppingCartGroup group : cartGroups) {
                Map<String, Object> jsonObject = new HashMap<>();
                jsonObject.put("merchant_id",
                        group.getMerchant()
                                .getId());
                if (group.getCouponRecord() != null && group.getCouponRecord()
                        .getId() > 0) {
                    jsonObject.put("user_coupon_id",
                            group.getCouponRecord()
                                    .getId());
                }
                List<Object> subJsonArray = new ArrayList<>();
                for (ShoppingCartItem item : group.getCartList()) {
                    Map<String, Object> subJsonObject = new HashMap<>();
                    if (item.getId() > 0) {
                        subJsonObject.put("cart_id", item.getId());
                    }
                    subJsonObject.put("sku_id",
                            item.getSku()
                                    .getId());
                    subJsonObject.put("product_id",
                            item.getProduct()
                                    .getId());
                    subJsonObject.put("quantity", item.getQuantity());
                    subJsonArray.add(subJsonObject);
                    sb.append(item.getProduct()
                            .getId())
                            .append(",");
                }
                jsonObject.put("sub_items", subJsonArray);
                jsonArray.add(jsonObject);
            }
            if (!TextUtils.isEmpty(sb) && sb.lastIndexOf(",") > 0) {
                sb.deleteCharAt(sb.length() - 1); //移除最后的逗号
            }
            productIds = sb.toString();
            map.put("items", jsonArray);
            map.put("address_id", selectAddress.getId());
            OrderApi.postForShippingFeeObb(map)
                    .subscribe(shippingFeeSub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRedPacketList() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray groupsArr = new JSONArray();
            JSONArray extraArray = new JSONArray();
            for (ShoppingCartGroup group : cartGroups) {
                JSONObject groupObj = new JSONObject();
                JSONArray itemsArr = new JSONArray();
                groupObj.put("merchant_id",
                        group.getMerchant()
                                .getId());
                if (group.getCouponRecord() != null && group.getCouponRecord()
                        .getId() > 0) {
                    groupObj.put("user_coupon_id",
                            group.getCouponRecord()
                                    .getId());
                }
                for (ShoppingCartItem item : group.getCartList()) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("sku_id",
                            item.getSku()
                                    .getId());
                    itemObj.put("product_id",
                            item.getProduct()
                                    .getId());
                    itemObj.put("quantity", item.getQuantity());
                    itemsArr.put(itemObj);

                    extraArray.put(itemObj);
                }

                groupObj.put("sub_items", itemsArr);
                groupsArr.put(groupObj);
            }

            jsonObject.put("items", groupsArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                redPackets.clear();
                selectedRedPacket = null;
                JSONObject resultObject = (JSONObject) object;
                if (resultObject != null) {
                    JSONArray jsonArray = resultObject.optJSONArray("list");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        Gson gson = GsonUtil.getGsonInstance();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            RedPacket redPacket = gson.fromJson(jsonArray.optJSONObject(i)
                                    .toString(), RedPacket.class);
                            redPackets.add(redPacket);
                        }

                    }
                }
                // 设置红包显示
                setRedPacketsInfo();
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                redPackets.clear();
                setRedPacketsInfo();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.PRODUCT_RED_PACKET),
                jsonObject.toString());
    }

    private void setCouponData(List<MerchantCouponList> merchantCouponLists) {
        merchantCouponMap.clear();
        if (!CommonUtil.isCollectionEmpty(merchantCouponLists)) {
            for (int i = 0; i < merchantCouponLists.size(); i++) {
                merchantCouponMap.put(merchantCouponLists.get(i)
                        .getMerchantId(), merchantCouponLists.get(i));
            }
            for (ShoppingCartGroup group : cartGroups) {
                MerchantCouponList merchantCouponList = merchantCouponMap.get(group.getMerchant()
                        .getId());
                if (merchantCouponList != null && !CommonUtil.isCollectionEmpty
                        (merchantCouponList.getCouponList())) {
                    group.setCouponRecord(merchantCouponList.getCouponList()
                            .get(0));
                }
            }
        }
        adapter.notifyDataSetChanged();
        setPriceInfo();
        // 加载完优惠券后才能加载红包列表并刷新运费
        postForShippingFee();
        getRedPacketList();
    }

    private void getCouponList() {
        CommonUtil.unSubscribeSubs(couponSub);
        couponSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<List<MerchantCouponList>>() {
                    @Override
                    public void onNext(List<MerchantCouponList> merchantCouponLists) {
                        setCouponData(merchantCouponLists);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        getRedPacketList();
                    }
                })
                .setDataNullable(true)
                .build();

        OrderApi.postAvailableProductCouponList(getCouponAddressMap())
                .subscribe(couponSub);
    }

    private void setRedPacketsInfo() {
        redPacketLayout.setVisibility(View.VISIBLE);
        redPacketLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRedPacket();
            }
        });
        if (redPackets.isEmpty()) {
            tvAvailableCount.setText(R.string.label_no_avaliable_red_packet);
            tvAvailableCount.setBackgroundColor(ContextCompat.getColor(this,
                    android.R.color.transparent));
            tvAvailableCount.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        } else {
            tvAvailableCount.setText(getString(R.string.label_available_packet_count2,
                    redPackets.size()));
            tvAvailableCount.setBackgroundResource(R.drawable.sp_r15_yellow);
            tvAvailableCount.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            RedPacket useNoneRedPacket = new RedPacket();
            useNoneRedPacket.setId(-1);
            redPackets.add(useNoneRedPacket);
            selectedRedPacket = useNoneRedPacket;
            setPriceInfo();
            setPriceSavedMoney();
        }
    }

    private void setPriceSavedMoney() {
        if (selectedRedPacket == null || selectedRedPacket.getId() < 0) {
            tvRedPacketSavedMoney.setText("未使用");
            tvRedPacketSavedMoney.setTextColor(ContextCompat.getColor(ProductOrderConfirmActivity
                    .this, R.color.colorGray));
            return;
        }
        if (totalCartPrice > 0) {
            tvRedPacketSavedMoney.setText("省" + Util.formatDouble2String(selectedRedPacket
                    .getAmount()) + "元");
        } else {
            tvRedPacketSavedMoney.setText("省" + Util.formatDouble2String(totalSaveMoney) + "元");

        }
        tvRedPacketSavedMoney.setTextColor(ContextCompat.getColor(ProductOrderConfirmActivity.this,
                R.color.colorPrimary));
    }

    void onRedPacket() {
        if (redPacketDialog != null && redPacketDialog.isShowing()) {
            return;
        }
        if (redPacketsAdapter == null) {
            redPacketsAdapter = new RedPacketsListAdapter(redPackets);
        } else {
            redPacketsAdapter.notifyDataSetChanged();
        }
        redPacketDialog = DialogUtil.createRedPacketDialog(redPacketDialog,
                this,
                redPacketsAdapter,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        pendingRedPacket = (RedPacket) parent.getAdapter()
                                .getItem(position);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redPacketDialog.cancel();
                        if (pendingRedPacket != null) {
                            selectedRedPacket = pendingRedPacket;
                        }
                        setPriceInfo();
                        setPriceSavedMoney();
                    }
                },
                redPackets.indexOf(selectedRedPacket));
        redPacketDialog.show();
        redPacketsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(couponSub, shippingFeeSub, defaultAddressSub, initSub);
    }
}
