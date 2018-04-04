package me.suncloud.marrymemo.view.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljchatlibrary.views.fragments.WSChatFragment;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljtrackerlibrary.HljTracker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.fragment.chat.CustomerWSChatFragment;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

import static com.hunliji.hljchatlibrary.views.fragments.WSChatFragment.ARG_CHANNEL_ID;
import static com.hunliji.hljchatlibrary.views.fragments.WSChatFragment.ARG_SOURCE;

@Route(path = RouterPath.IntentPath.Customer.WsCustomChatActivityPath.WS_CUSTOMER_CHAT_ACTIVITY)
public class WSCustomerChatActivity extends HljBaseNoBarActivity implements WSChatFragment
        .OnChatFragmentStateListener {

    public static final String ARG_CONTACT_PHONES = "contact_phones";

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_merchant)
    ImageButton btnMerchant;
    @BindView(R.id.btn_call)
    ImageButton btnCall;
    @BindView(R.id.action_layout)
    FrameLayout actionLayout;
    @BindView(R.id.content_layout)
    FrameLayout contentLayout;
    @BindView(R.id.right_btn_layout)
    LinearLayout rightBtnLayout;
    private CustomerWSChatFragment wsChatFragment;
    private List<String> contactPhones;
    private final static String FRAGMENT_TAG_WS_CHAT = "fragment_tag_chat";
    private MerchantUser merchantUser;


    private Subscription phonesSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wscustomer_chat);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    private void initValues() {
        User sessionUser = getIntent().getParcelableExtra(RouterPath.IntentPath.Customer
                .BaseWsChat.ARG_USER);
        String autoMsg = getIntent().getStringExtra(RouterPath.IntentPath.Customer.BaseWsChat
                .ARG_AUTO_MSG);
        City city = getIntent().getParcelableExtra(RouterPath.IntentPath.Customer.BaseWsChat
                .ARG_CITY);
        String channelId = getIntent().getStringExtra(ARG_CHANNEL_ID);
        int source = getIntent().getIntExtra(ARG_SOURCE, 1);
        WSTrack wsTrack = getIntent().getParcelableExtra(RouterPath.IntentPath.Customer
                .BaseWsChat.ARG_WS_TRACK);
        long userId = getIntent().getLongExtra(RouterPath.IntentPath.Customer.BaseWsChat
                        .ARG_USER_ID,
                0);
        contactPhones = getIntent().getStringArrayListExtra(ARG_CONTACT_PHONES);

        wsChatFragment = CustomerWSChatFragment.newInstance(sessionUser,
                channelId,
                source,
                wsTrack,
                userId,
                autoMsg,
                city);
        wsChatFragment.setChatFragmentStateListener(this);
    }

    private void initViews() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.content_layout, wsChatFragment, FRAGMENT_TAG_WS_CHAT);
        ft.commit();
        setDefaultStatusBarPadding();
        rightBtnLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if ((oldRight - oldLeft) != (right - left)) {
                    requestTitleLayout();
                }
            }
        });
        tvTitle.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if ((oldRight - oldLeft) != (right - left)) {
                    requestTitleLayout();
                }
            }
        });
    }

    private void requestTitleLayout() {
        int minLeftMargin = btnBack.getMeasuredWidth();
        int barWidth = actionLayout.getMeasuredWidth();
        int minRightMargin = rightBtnLayout.getMeasuredWidth();
        int titleWidth = tvTitle.getMeasuredWidth();
        int maxWidth = barWidth - minLeftMargin - minRightMargin;
        tvTitle.setMaxWidth(maxWidth);
        if (titleWidth > maxWidth) {
            titleWidth = maxWidth;
        }
        ViewGroup.MarginLayoutParams layoutParams = ((ViewGroup.MarginLayoutParams) tvTitle
                .getLayoutParams());
        if (minLeftMargin >= minRightMargin) {
            layoutParams.leftMargin = Math.max(minLeftMargin, (barWidth - titleWidth) / 2);
        } else {
            int rightMargin = Math.max(minRightMargin, (barWidth - titleWidth) / 2);
            layoutParams.leftMargin = barWidth - titleWidth - rightMargin;
        }
        tvTitle.setLayoutParams(layoutParams);
        tvTitle.setLeft(layoutParams.leftMargin);
        tvTitle.setRight(layoutParams.leftMargin + titleWidth);
    }

    @Override
    public void onSessionUserInfo(User sessionUser) {
        if (sessionUser == null || !(sessionUser instanceof MerchantUser)) {
            return;
        }
        merchantUser = (MerchantUser) sessionUser;

        if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_CAR) {
            btnMerchant.setVisibility(View.GONE);
        } else {
            btnMerchant.setVisibility(View.VISIBLE);
        }
        if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_PRODUCT) {
            btnCall.setVisibility(View.GONE);
        } else {
            CommonUtil.unSubscribeSubs(phonesSubscription);
            phonesSubscription = Observable.concat(Observable.just(contactPhones),
                    MerchantApi.getMerchantPhones(sessionUser.getId()))
                    .filter(new Func1<List<String>, Boolean>() {
                        @Override
                        public Boolean call(List<String> strings) {
                            return !CommonUtil.isCollectionEmpty(strings);
                        }
                    })
                    .first()
                    .subscribe(new Subscriber<List<String>>() {

                        @Override
                        public void onStart() {
                            super.onStart();
                            btnCall.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(List<String> strings) {
                            contactPhones = strings;
                            btnCall.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public void onStateChange(String state) {
        tvTitle.setText(state);
    }

    @Override
    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_call)
    public void onCallBtnClick() {
        if (CommonUtil.isCollectionEmpty(contactPhones)) {
            return;
        }
        if (contactPhones.size() == 1) {
            onCall(contactPhones.get(0));
            return;
        }
        DialogUtil.createPhoneListDialog(this,
                contactPhones,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        onCall((String) parent.getAdapter()
                                .getItem(position));
                    }
                })
                .show();
    }

    @OnClick(R.id.btn_merchant)
    public void onMerchantClick() {
        if (merchantUser == null || merchantUser.getMerchantId() <= 0 || merchantUser.getShopType
                () == MerchantUser.SHOP_TYPE_CAR) {
            return;
        }
        Intent intent;
        if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_PRODUCT) {
            intent = new Intent(this, ProductMerchantActivity.class);
        } else {
            intent = new Intent(this, MerchantDetailActivity.class);
        }
        intent.putExtra("id", merchantUser.getMerchantId());
        startActivity(intent);
    }

    private void onCall(String phone) {
        if (!TextUtils.isEmpty(phone) && phone.trim()
                .length() != 0) {
            long merchantId = merchantUser == null ? 0 : merchantUser.getMerchantId();
            new HljTracker.Builder(this).eventableId(merchantId)
                    .eventableType("Merchant")
                    .screen("chat")
                    .action("call")
                    .build()
                    .add();
            try {
                callUp(Uri.parse("tel:" + phone.trim()));
                new HljTracker.Builder(this).eventableId(merchantId)
                        .eventableType("Merchant")
                        .screen("chat")
                        .action("real_call")
                        .build()
                        .add();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(phonesSubscription);
        super.onFinish();
    }
}
