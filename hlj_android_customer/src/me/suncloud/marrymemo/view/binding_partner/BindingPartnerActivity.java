package me.suncloud.marrymemo.view.binding_partner;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.userprofile.PartnerInvitation;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.bindpartner.PartnerApi;
import me.suncloud.marrymemo.fragment.bindpartner.BindingPartnerInvitationStatusFragment;
import me.suncloud.marrymemo.fragment.bindpartner.MyBindedPartnerFragment;
import me.suncloud.marrymemo.task.UserTask;

/**
 * 绑定伴侣的集合主页，各个功能都在这个页面集中显示和操作
 * 可以从设置页面进入，也可以从通知列表中进入
 */
public class BindingPartnerActivity extends HljBaseActivity {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.activity_binding_partner)
    RelativeLayout activityBindingPartner;
    private HljHttpSubscriber subscriber;
    private long noticeInvitationUserId;
    private String noticeInvitationUserNick;
    private int type; // 不为0时来自婚期设置；Constants.Login.REGISTER 注册，Constants.Login.LOGINCHECK 登陆检测
    private PartnerInvitation partnerInvitation;
    private HljHttpSubscriber unbindSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", 0);
        setContentView(R.layout.activity_binding_partner);
        ButterKnife.bind(this);

        initValuesAndViews();
        initLoad();
    }


    /**
     * 关闭按钮点击
     */
    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (partnerInvitation != null && partnerInvitation.getStatus()
                .equals(PartnerInvitation.PARTNER_STATUS_ESTA)) {
            // 接触绑定
            onUnbind();
        }
    }

    private void initValuesAndViews() {
        // 从通知列表进入的时候，根据传入的数据判定是否是过期的邀请通知，显示提示信息
        noticeInvitationUserId = getIntent().getLongExtra("user_id", 0);
        noticeInvitationUserNick = getIntent().getStringExtra("user_nick");
    }

    private void initLoad() {
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<PartnerInvitation>() {
                    @Override
                    public void onNext(PartnerInvitation pi) {
                        partnerInvitation = pi;
                        // 如果通知传入的邀请者与最新的邀请信息的邀请者不一样，则提示这个邀请已失效
                        if (noticeInvitationUserId > 0 && partnerInvitation != null &&
                                partnerInvitation.getUserId() != noticeInvitationUserId) {
                            Toast.makeText(BindingPartnerActivity.this,
                                    noticeInvitationUserNick + "的邀请已过期了哦",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                        setContent();
                    }
                })
                .build();

        PartnerApi.getPartnerInvitationStatusObb()
                .subscribe(subscriber);
    }

    /**
     * 根据结伴状态，显示不同的fragment
     */
    private void setContent() {
        if (partnerInvitation != null && partnerInvitation.getStatus()
                .equals(PartnerInvitation.PARTNER_STATUS_ESTA)) {
            // 已建立结伴关系，显示伴侣主页
            setTitle("伴侣主页");
            setOkText("解除绑定");
            MyBindedPartnerFragment fragment = MyBindedPartnerFragment.newInstance
                    (partnerInvitation);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content_layout, fragment, "my_binded_partner_fragment");
            ft.commitAllowingStateLoss();
        } else {
            // 没有任何邀请或者收到邀请或者发出邀请，等待接受
            setTitle("邀请另一半");
            hideOkText();
            //把type传到fragment控制后续操作
            BindingPartnerInvitationStatusFragment fragment =
                    BindingPartnerInvitationStatusFragment.newInstance(
                    partnerInvitation,
                    type);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content_layout, fragment, "binding_partner_status_fragment");
            ft.commitAllowingStateLoss();
        }
    }

    private void onUnbind() {
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_unbind_partner),
                getString(R.string.label_confirm),
                getString(R.string.label_cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unbind();
                    }
                },
                null)
                .show();
    }

    private void unbind() {
        unbindSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Toast.makeText(BindingPartnerActivity.this, "解绑成功", Toast.LENGTH_SHORT)
                                .show();
                        new UserTask(BindingPartnerActivity.this, null).execute();
                        initLoad();
                    }
                })
                .build();

        PartnerApi.unBindPartner()
                .subscribe(unbindSub);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(subscriber);
    }

    public void refresh() {
        CommonUtil.unSubscribeSubs(subscriber);
        // 重置传入参数
        noticeInvitationUserId = 0;
        initLoad();
    }
}
