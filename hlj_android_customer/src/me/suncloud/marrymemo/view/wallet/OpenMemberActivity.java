package me.suncloud.marrymemo.view.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.constructors.JsInterfaceConstructor;
import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;
import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.MemberOrder;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.UserTask;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.MemberAddressListActivity;

/**
 * Created by mo_yu on 2017/4/17.开通会员引导页
 */

@Route(path = RouterPath.IntentPath.Customer.OPEN_MEMBER)
public class OpenMemberActivity extends HljBaseActivity {

    @Override
    public String pageTrackTagName() {
        return "会员尊享页";
    }

    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.bottom_line)
    View bottomLine;
    @BindView(R.id.tv_open_states_tip)
    TextView tvOpenStatesTip;
    @BindView(R.id.tv_open_states)
    TextView tvOpenStates;
    @BindView(R.id.member_bottom_view)
    LinearLayout memberBottomView;
    @BindView(R.id.tv_member_pay_price)
    TextView tvMemberPayPrice;
    @BindView(R.id.tv_member_original_price)
    TextView tvMemberOriginalPrice;
    @BindView(R.id.member_pay_price_view)
    LinearLayout memberPayPriceView;
    @BindView(R.id.action_open_member)
    RelativeLayout actionOpenMember;
    private User user;
    private RxBusSubscriber paySubscriber;
    private HljHttpSubscriber getSubscriber;
    protected MemberOrder memberOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_member);
        ButterKnife.bind(this);
        tvMemberOriginalPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
        refreshMemberInfo();
        setMemberInfo();
    }

    private void refreshMemberInfo() {
        user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && user.getMember() != null) {
            bottomLine.setVisibility(View.VISIBLE);
            memberBottomView.setVisibility(View.VISIBLE);
            actionOpenMember.setVisibility(View.GONE);
            if (user.getMember()
                    .getAddressId() > 0) {
                tvOpenStatesTip.setText(getString(R.string.label_member_wallet_tip));
                tvOpenStates.setText(getString(R.string.label_member_go_wallet));
            } else {
                tvOpenStatesTip.setText(getString(R.string.label_member_no_address));
                tvOpenStates.setText(getString(R.string.label_member_edit_address));
            }
        } else {
            bottomLine.setVisibility(View.GONE);
            memberBottomView.setVisibility(View.GONE);
            actionOpenMember.setVisibility(View.VISIBLE);
            if (user == null) {
                memberPayPriceView.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setMemberInfo() {
        if (user != null && user.getMember() == null) {
            getOrderInfo();
        }
        String path;
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        if (dataConfig != null && !TextUtils.isEmpty(dataConfig.getIntroUrl())) {
            path = dataConfig.getIntroUrl();
        } else {
            path = Constants.INTRO_URL;
        }
        Map<String, String> header = new HashMap<>();
        path = WebUtil.addPathQuery(this, path);
        if (Uri.parse(path)
                .getHost() != null && (Uri.parse(path)
                .getHost()
                .contains("hunliji") || HljHttp.getHOST()
                .contains(Uri.parse(path)
                        .getHost()))) {
            header = WebUtil.getWebHeaders(this);
            BaseWebHandler webHandler = JsInterfaceConstructor.getJsInterface(this,
                    path,
                    webView,
                    null);
            //js调用的函数接口
            if (webHandler != null) {
                webView.addJavascriptInterface(webHandler, "handler");
            }
        }
        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
        //        wenbview缓存
        webView.getSettings()
                .setDomStorageEnabled(true);
        webView.getSettings()
                .setAppCachePath(webView.getContext()
                        .getCacheDir()
                        .getAbsolutePath());
        webView.getSettings()
                .setAppCacheEnabled(true);
        if (HljCommon.debug) {
            webView.getSettings()
                    .setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new HljWebClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        if (!header.isEmpty()) {
            webView.loadUrl(path, header);
        } else {
            webView.loadUrl(path);
        }
    }

    @OnClick({R.id.tv_open_states, R.id.action_open_member})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_open_states:
                if (user != null && user.getMember() != null) {
                    if (user.getMember()
                            .getAddressId() > 0) {
                        //是会员，已设置地址，跳转到我的红包
                        intent.setClass(OpenMemberActivity.this, MyRedPacketListActivity.class);
                        startActivity(intent);
                    } else {
                        //是会员，未设置地址
                        intent.setClass(OpenMemberActivity.this, MemberAddressListActivity.class);
                        startActivityForResult(intent, Constants.RequestCode.EDIT_SHIPPING_ADDRESS);
                    }
                }
                break;
            case R.id.action_open_member:
                if (!Util.loginBindChecked(this, Constants.RequestCode.LOGIN_OPEN_MEMBER)) {
                    return;
                }
                submitPayOrder();
                break;
        }
    }

    //提交购买信息
    private void getOrderInfo() {
        if (getSubscriber == null || getSubscriber.isUnsubscribed()) {
            getSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<MemberOrder>() {
                        @Override
                        public void onNext(MemberOrder data) {
                            if (data.getPayMoney() > 0) {
                                tvMemberPayPrice.setText(getString(R.string.label_member_open_tip,
                                        CommonUtil.formatDouble2String(data.getPayMoney())));
                                tvMemberOriginalPrice.setText(getString(R.string.label_price___cm,
                                        CommonUtil.formatDouble2String(data.getOriginalPrice())));
                                memberPayPriceView.setVisibility(View.VISIBLE);
                            }
                            memberOrder = data;
                        }
                    })
                    .build();
            CustomerCardApi.submitMemberOrderObb()
                    .subscribe(getSubscriber);
        }
    }

    private void submitPayOrder() {
        if (memberOrder == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", memberOrder.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (memberOrder.getPayMoney() > 0) {
            goPayOrder(jsonObject, memberOrder.getPayMoney());
        }
    }

    //提交购买信息后跳转到支付页面
    private void goPayOrder(JSONObject jsonObject, double money) {
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            ToastUtil.showToast(OpenMemberActivity.this, "恭喜您成为婚礼纪尊享会员", 0);
                            // 支付成功，跳转成功页面
                            new UserTask(OpenMemberActivity.this, new OnHttpRequestListener() {
                                @Override
                                public void onRequestCompleted(Object obj) {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    RxBus.getDefault()
                                            .post(new RxEvent(RxEvent.RxEventType
                                                    .OPEN_MEMBER_SUCCESS,
                                                    null));
                                    refreshMemberInfo();
                                    Intent intent = new Intent(OpenMemberActivity.this,
                                            MemberAddressListActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onRequestFailed(Object obj) {
                                }
                            }).execute();
                            break;
                        case PAY_CANCEL:
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
                .path(Constants.getAbsUrl(Constants.HttpPath.MEMBER_ORDER_PAYMENT))
                .price(money > 0 ? money : 0)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_SHIPPING_ADDRESS:
                    refreshMemberInfo();
                    break;
                case Constants.RequestCode.LOGIN_OPEN_MEMBER:
                    refreshMemberInfo();
                    if (user != null && user.getMember() == null) {
                        getOrderInfo();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        webView.destroy();
        super.onFinish();
        CommonUtil.unSubscribeSubs(paySubscriber, getSubscriber);
    }
}
