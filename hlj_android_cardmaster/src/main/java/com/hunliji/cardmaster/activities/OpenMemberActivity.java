package com.hunliji.cardmaster.activities;

import android.annotation.SuppressLint;
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
import com.example.suncloud.hljweblibrary.HljWeb;
import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.constructors.JsInterfaceConstructor;
import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;
import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.hunliji.cardmaster.Constants;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.models.DataConfig;
import com.hunliji.cardmaster.utils.DataConfigUtil;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.MemberOrder;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hua_rong on 2017/11/27
 * 开通会员
 */

@Route(path = RouterPath.IntentPath.Customer.OPEN_MEMBER)
public class OpenMemberActivity extends HljBaseActivity {


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
    private HljHttpSubscriber getSubscriber;
    private CustomerUser customerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_member);
        ButterKnife.bind(this);
        initValue();
        refreshMemberInfo();
        setMemberInfo();
    }

    private void initValue() {
        tvMemberOriginalPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
        User user = UserSession.getInstance()
                .getUser(this);
        if (user instanceof CustomerUser) {
            customerUser = (CustomerUser) user;
        }
        if (customerUser != null && customerUser.getMember() == null) {
            getOrderInfo();
        }
    }

    private void refreshMemberInfo() {
        if (customerUser != null && customerUser.getMember() != null) {
            bottomLine.setVisibility(View.VISIBLE);
            memberBottomView.setVisibility(View.VISIBLE);
            actionOpenMember.setVisibility(View.GONE);
            if (customerUser.getMember()
                    .getAddressId() > 0) {
                tvOpenStatesTip.setText(R.string.label_member_wallet_tip);
                tvOpenStates.setText(R.string.label_member_go_wallet);
            } else {
                tvOpenStatesTip.setText(R.string.label_member_no_address);
                tvOpenStates.setText(R.string.label_member_edit_address);
            }
        } else {
            bottomLine.setVisibility(View.GONE);
            memberBottomView.setVisibility(View.GONE);
            actionOpenMember.setVisibility(View.VISIBLE);
            if (customerUser == null) {
                memberPayPriceView.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setMemberInfo() {
        String path = Constants.INTRO_URL;
        DataConfig dataConfig = DataConfigUtil.INSTANCE.getDataConfig(this);
        if (dataConfig != null && !TextUtils.isEmpty(dataConfig.getIntroUrl())) {
            path = dataConfig.getIntroUrl();
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
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        //        wenbview缓存
        settings.setDomStorageEnabled(true);
        settings.setAppCachePath(webView.getContext()
                .getCacheDir()
                .getAbsolutePath());
        settings.setAppCacheEnabled(true);
        if (HljCommon.debug) {
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
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
                        }
                    })
                    .build();
            CustomerCardApi.submitMemberOrderObb()
                    .subscribe(getSubscriber);
        }
    }

    @OnClick({R.id.action_open_member, R.id.member_bottom_view})
    void onOpenMember(View view) {
        HljWeb.startWebView(this, HljCard.CARD_MASTER_MEMBER);
    }

    @Override
    protected void onFinish() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        super.onFinish();
        CommonUtil.unSubscribeSubs(getSubscriber);
    }

}
