package me.suncloud.marrymemo.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.hunliji.hljchatlibrary.views.fragments.WSChatFragment;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.chat.CustomerWSChatFragment;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

import static com.hunliji.hljchatlibrary.views.fragments.WSChatFragment.ARG_CHANNEL_ID;
import static com.hunliji.hljchatlibrary.views.fragments.WSChatFragment.ARG_SOURCE;
import static com.hunliji.hljcommonlibrary.modules.helper.RouterPath.IntentPath.Customer
        .BaseWsChat.ARG_IS_COLLECTED;

@Route(path = RouterPath.IntentPath.Customer.WsCustomDialogChatActivityPath
        .WS_CUSTOMER_CHAT_DIALOG_ACTIVITY)
public class WSCustomerDialogChatActivity extends FragmentActivity implements WSChatFragment
        .OnChatFragmentStateListener {

    @BindView(R.id.blank_view)
    View blankView;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.title_layout)
    LinearLayout titleLayout;
    @BindView(R.id.content_layout)
    FrameLayout contentLayout;
    private CustomerWSChatFragment wsChatFragment;
    private final static String FRAGMENT_TAG_WS_CHAT = "fragment_tag_chat";
    private HljHttpSubscriber followSub;
    private boolean isCollected;
    private MerchantUser merchantUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wscustomer_dialog_chat);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        User sessionUser = getIntent().getParcelableExtra(RouterPath.IntentPath.Customer
                .WsCustomDialogChatActivityPath.ARG_USER);
        String autoMsg = getIntent().getStringExtra(RouterPath.IntentPath.Customer.BaseWsChat
                .ARG_AUTO_MSG);
        String channelId = getIntent().getStringExtra(ARG_CHANNEL_ID);
        int source = getIntent().getIntExtra(ARG_SOURCE, 1);
        WSTrack wsTrack = getIntent().getParcelableExtra(RouterPath.IntentPath.Customer
                .BaseWsChat.ARG_WS_TRACK);
        isCollected = getIntent().getBooleanExtra(ARG_IS_COLLECTED, false);
        long userId = getIntent().getLongExtra(RouterPath.IntentPath.Customer.BaseWsChat
                        .ARG_USER_ID,
                0);
        City city = getIntent().getParcelableExtra(RouterPath.IntentPath.Customer.BaseWsChat
                .ARG_CITY);
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

        tvCollect.setText(isCollected ? "已关注" : "+关注");
    }

    private void initLoad() {

    }

    @OnClick(R.id.blank_view)
    void onBlankView() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onSessionUserInfo(User sessionUser) {
        if (sessionUser != null) {
            if (sessionUser instanceof MerchantUser) {
                merchantUser = (MerchantUser) sessionUser;
            }
            Glide.with(this)
                    .load(ImageUtil.getAvatar(sessionUser.getAvatar()))
                    .into(imgAvatar);
            tvTitle.setText(sessionUser.getNick());
        }
    }

    @Override
    public void onStateChange(String state) {
        tvTitle.setText(state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(followSub);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            CommonUtil.unSubscribeSubs(followSub);
        }
    }

    @OnClick(R.id.img_avatar)
    void onAvatar() {
        if (merchantUser != null) {
            Intent intent = new Intent(this, MerchantDetailActivity.class);
            intent.putExtra("id", merchantUser.getMerchantId());
            startActivity(intent);
        }
    }

    @OnClick(R.id.tv_collect)
    void onCollect() {
        if (isCollected) {
            return;
        }
        followSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object object) {
                        tvCollect.setText("已关注");
                        isCollected = true;
                        Toast.makeText(WSCustomerDialogChatActivity.this,
                                "关注成功！",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .build();
        if (merchantUser != null) {
            CommonApi.postMerchantFollowObb(merchantUser.getMerchantId())
                    .subscribe(followSub);
        }
    }
}
