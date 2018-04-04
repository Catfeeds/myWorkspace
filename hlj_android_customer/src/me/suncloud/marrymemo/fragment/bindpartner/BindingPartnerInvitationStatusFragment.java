package me.suncloud.marrymemo.fragment.bindpartner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.userprofile.PartnerInvitation;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.bindpartner.PartnerApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.UserTask;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.binding_partner.BindPartnerActivity;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;
import rx.Observable;

/**
 * Created by werther on 16/11/14.
 */

public class BindingPartnerInvitationStatusFragment extends Fragment {
    @BindView(R.id.img_bg)
    ImageView imgBg;
    @BindView(R.id.top_view)
    View topView;
    @BindView(R.id.invite_hint_layout)
    LinearLayout inviteHintLayout;
    @BindView(R.id.tv_success_hint)
    TextView tvSuccessHint;
    @BindView(R.id.invite_success_layout)
    LinearLayout inviteSuccessLayout;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.btn_agree)
    Button btnAgree;
    @BindView(R.id.invitation_layout)
    LinearLayout invitationLayout;
    @BindView(R.id.center_layout1)
    LinearLayout centerLayout1;
    @BindView(R.id.btn_init_invitation)
    ImageButton btnInitInvitation;
    @BindView(R.id.center_layout2)
    LinearLayout centerLayout2;
    @BindView(R.id.center_layout)
    LinearLayout centerLayout;
    @BindView(R.id.bottom_view)
    View bottomView;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;

    PartnerInvitation invitation;
    User user;
    HljHttpSubscriber acceptInviteSub;
    private Unbinder unbinder;

    private int type;

    public static BindingPartnerInvitationStatusFragment newInstance(
            PartnerInvitation partnerInvitation, int type) {
        BindingPartnerInvitationStatusFragment fragment = new
                BindingPartnerInvitationStatusFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("invitation", partnerInvitation);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = Session.getInstance()
                .getCurrentUser(getContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            invitation = bundle.getParcelable("invitation");
            type = bundle.getInt("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_binding_partner_status_layout,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);

        initViews();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setViews();
    }

    private void initViews() {
        // 渲染完成视图之后重新计算上下边距的高度，按比例分配
        centerLayout.post(new Runnable() {
            @Override
            public void run() {
                int topHeight = (contentLayout.getHeight() - centerLayout.getHeight()) / 11 * 3;
                ViewGroup.LayoutParams topParams = topView.getLayoutParams();
                topParams.height = topHeight;
            }
        });
    }

    private void setViews() {
        if (invitation == null) {
            // 没有邀请，显示邀请提示
            inviteHintLayout.setVisibility(View.VISIBLE);
            inviteSuccessLayout.setVisibility(View.GONE);
            invitationLayout.setVisibility(View.GONE);
        } else if (user != null && user.getId() > 0 && invitation != null) {
            if (invitation.getUserId() == user.getId()) {
                // 本用户是邀请者，显示已成功发出邀请的信息
                inviteHintLayout.setVisibility(View.GONE);
                inviteSuccessLayout.setVisibility(View.VISIBLE);
                invitationLayout.setVisibility(View.GONE);

                // 显示被邀请这的号码
                String phoneStr = invitation.getPartnerUserPhone()
                        .substring(0, 3) + "****" + invitation.getPartnerUserPhone()
                        .substring(7,
                                invitation.getPartnerUserPhone()
                                        .length());
                tvSuccessHint.setText(getString(R.string.fmt_invite_partner_success, phoneStr));
            } else {
                // 本用户是被邀请，显示被邀请的信息
                inviteHintLayout.setVisibility(View.GONE);
                inviteSuccessLayout.setVisibility(View.GONE);
                invitationLayout.setVisibility(View.VISIBLE);

                // 显示发起者的用户信息
                Glide.with(getContext())
                        .load(ImageUtil.getImagePath(invitation.getUserAvatar(),
                                CommonUtil.dp2px(getContext(), 30)))
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary)
                                .dontAnimate())
                        .into(imgAvatar);
                tvNick.setText(invitation.getUserNick());
            }
        }
    }

    @OnClick(R.id.btn_init_invitation)
    void onInitInvitation() {
        Intent intent = new Intent(getActivity(), BindPartnerActivity.class);
        //type 代表登陆时的类型，控制完成时的后续操作
        intent.putExtra("type", type);
        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_agree)
    void onAcceptInvitation() {
        Observable observable = PartnerApi.getPostAgreeBindPartnerObb(invitation.getUserId());
        acceptInviteSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        // 成功同意了邀请
                        // 更新用户信息
                        new UserTask(getContext(), null).execute();

                        // 更新当前Activity页面
                        ToastUtil.showToast(getContext(), "成功绑定伴侣", 0);
                        ((BindingPartnerActivity) getActivity()).refresh();
                    }
                })
                .build();

        observable.subscribe(acceptInviteSub);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(acceptInviteSub);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
