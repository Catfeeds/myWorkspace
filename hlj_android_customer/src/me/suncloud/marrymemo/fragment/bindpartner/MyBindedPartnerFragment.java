package me.suncloud.marrymemo.fragment.bindpartner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.userprofile.PartnerInvitation;
import com.hunliji.hljcommonlibrary.models.userprofile.UserPartnerWrapper2;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.bindpartner.PartnerApi;
import me.suncloud.marrymemo.fragment.WeddingDateFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;

/**
 * Created by werther on 16/11/14.
 * 用于结伴主页显示"伴侣主页"的fragment
 */

public class MyBindedPartnerFragment extends Fragment {

    @BindView(R.id.img_bg)
    ImageView imgBg;
    @BindView(R.id.top_view)
    View topView;
    @BindView(R.id.img_center)
    ImageView imgCenter;
    @BindView(R.id.bottom_view)
    View bottomView;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_days_label)
    TextView tvDaysLabel;
    @BindView(R.id.tv_days)
    TextView tvDays;
    @BindView(R.id.days_layout)
    LinearLayout daysLayout;
    @BindView(R.id.tv_wedding_date)
    TextView tvWeddingDate;
    @BindView(R.id.wedding_date_layout)
    LinearLayout weddingDateLayout;
    @BindView(R.id.img_partner_avatar)
    RoundedImageView imgPartnerAvatar;
    @BindView(R.id.tv_partner_nick)
    TextView tvPartnerNick;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_date_hint)
    TextView tvDateHint;
    private HljHttpSubscriber subscriber;
    private UserPartnerWrapper2 userPartnerWrapper;
    private Unbinder unbinder;

    public static MyBindedPartnerFragment newInstance(PartnerInvitation partnerInvitation) {
        MyBindedPartnerFragment fragment = new MyBindedPartnerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("invitation", partnerInvitation);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_binded_partner_layout, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initViews() {
        // 渲染完成视图之后重新计算上下边距的高度，按比例分配
        imgCenter.post(new Runnable() {
            @Override
            public void run() {
                int topHeight = (contentLayout.getHeight() - imgCenter.getHeight()) / 13 * 8;

                ViewGroup.LayoutParams topParams = topView.getLayoutParams();
                topParams.height = topHeight;
            }
        });
    }

    private void initLoad() {
        Observable<UserPartnerWrapper2> observable = PartnerApi.getUserPartnerInfoObb();

        subscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setProgressBar(progressBar)
                .setContentView(contentLayout)
                .setOnNextListener(new SubscriberOnNextListener<UserPartnerWrapper2>() {
                    @Override
                    public void onNext(UserPartnerWrapper2 userPartner) {
                        userPartnerWrapper = userPartner;
                        setUserInfo();
                    }
                })
                .build();

        observable.subscribe(subscriber);
    }

    private void setUserInfo() {
        tvNick.setText(userPartnerWrapper.getUser()
                .getNick());
        tvPartnerNick.setText(userPartnerWrapper.getPartner()
                .getNick());
        Glide.with(this)
                .load(ImageUtil.getAvatar(userPartnerWrapper.getUser()
                        .getAvatar()))
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.icon_avatar_primary)
                .dontAnimate())
                .into(imgAvatar);
        Glide.with(this)
                .load(ImageUtil.getAvatar(userPartnerWrapper.getPartner()
                        .getAvatar()))
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.icon_avatar_primary)
                .dontAnimate())
                .into(imgPartnerAvatar);

        // 设置倒计时
        if (userPartnerWrapper.getUser()
                .getWeddingDay() != null) {
            DateTime weddingDate = new DateTime(userPartnerWrapper.getUser()
                    .getWeddingDay());
            DateTime currentDate = new DateTime();
            int days = Days.daysBetween(currentDate.toLocalDate(), weddingDate.toLocalDate())
                    .getDays();
            tvWeddingDate.setText(weddingDate.toString("yyyy-MM-dd"));
            tvWeddingDate.setTextColor(getResources().getColor(R.color.colorBlack3));
            tvDateHint.setVisibility(View.GONE);
            if (days > 0) {
                tvDaysLabel.setText("距离喜结良缘");
                tvDays.setText(String.valueOf(days));
            } else {
                tvDaysLabel.setText("已携手");
                tvDays.setText(String.valueOf(Math.abs(days)));
            }
        } else {
            //设置婚期
            tvWeddingDate.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvWeddingDate.setText(getString(R.string.label_click_setting1));
        }
    }

    @OnClick(R.id.tv_wedding_date)
    public void onWeddingDate(View view) {
        User u = Session.getInstance()
                .getCurrentUser(getContext());
        if (u == null || u.getWeddingDay() != null) {
            return;
        }

        showWeddingDateDialog();
    }

    private void showWeddingDateDialog() {
        WeddingDateFragment fragment = WeddingDateFragment
                .newInstance(
                        new DateTime(Calendar.getInstance()
                                .getTime()));
        fragment.show(getChildFragmentManager(),
                "WeddingDateFragment");
        fragment.setOnDateSelectedListener(new WeddingDateFragment.onDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar) {
                initLoad();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(subscriber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
