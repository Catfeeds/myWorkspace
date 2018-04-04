package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.userprofile.UserPartnerWrapper;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljnotelibrary.views.fragments.MyNoteListFragment;
import com.hunliji.hljquestionanswer.fragments.MyAnswersFragment;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.fragment.UserCommunityThreadsFragment;
import me.suncloud.marrymemo.fragment.user.UserCommentFragment;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.task.UserTask;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;
import me.suncloud.marrymemo.view.wallet.OpenMemberActivity;
import rx.Observable;

@Route(path = RouterPath.IntentPath.Customer.USER_PROFILE)
public class UserProfileActivity extends HljBaseNoBarActivity {

    @BindView(R.id.img_partner_avatar)
    RoundedImageView imgPartnerAvatar;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_praised_count)
    TextView tvPraisedCount;
    @BindView(R.id.tv_followed_count)
    TextView tvFollowedCount;
    @BindView(R.id.tv_fans_count)
    TextView tvFansCount;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_wedding_day)
    TextView tvWeddingDay;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_thread_count)
    TextView tvThreadCount;
    @BindView(R.id.cb_item1)
    CheckableLinearButton cbItem1;
    @BindView(R.id.tv_answer_count)
    TextView tvAnswerCount;
    @BindView(R.id.cb_item2)
    CheckableLinearButton cbItem2;
    @BindView(R.id.cb_item3)
    CheckableLinearButton cbItem3;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.cb_item4)
    CheckableLinearButton cbItem4;
    @BindView(R.id.cb_group)
    CheckableLinearGroup cbGroup;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout scrollableLayout;
    @BindView(R.id.btn_follow)
    Button btnFollow;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_follow2)
    Button btnFollow2;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_back2)
    ImageView btnBack2;
    @BindView(R.id.avatars_layout)
    RelativeLayout avatarsLayout;
    @BindView(R.id.nick_layout)
    LinearLayout nickLayout;
    @BindView(R.id.img_partner_avatar_under)
    RoundedImageView imgPartnerAvatarUnder;
    @BindView(R.id.img_avatar_under)
    RoundedImageView imgAvatarUnder;
    @BindView(R.id.avatars_layout_under)
    RelativeLayout avatarsLayoutUnder;
    @BindView(R.id.tv_member_label)
    TextView tvMemberLabel;
    @BindView(R.id.tv_vip_label)
    TextView tvVipLabel;
    @BindView(R.id.tv_note_count)
    TextView tvNoteCount;

    private UserPartnerWrapper userPartnerWrapper;
    private SparseArray<ScrollAbleFragment> fragments;
    private HljHttpSubscriber userProfileSub;

    private long id;

    private RoundedImageView avatarLeft;
    private RoundedImageView avatarRight;
    private float avatarLeftX;
    private float avatarRightX;
    private float avatarLeftWidth;
    private float avatarRightWidth;
    private Dialog followDialog;
    private boolean isAnimating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        setDefaultStatusBarPadding();

        id = getIntent().getLongExtra("id", 0);
        initViews();
        initLoad();
    }

    private void initViews() {
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        cbItem1.setChecked(true);
                        break;
                    case 1:
                        cbItem2.setChecked(true);
                        break;
                    case 2:
                        cbItem3.setChecked(true);
                        break;
                    case 3:
                        cbItem4.setChecked(true);
                        break;
                }
                if (fragments != null && fragments.size() > position) {
                    scrollableLayout.getHelper()
                            .setCurrentScrollableContainer(fragments.get(position));
                }
            }
        });
        cbItem1.setChecked(true);
        cbGroup.setOnCheckedChangeListener(new CheckableLinearGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cb_item1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.cb_item2:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.cb_item3:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.cb_item4:
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });
        scrollableLayout.setOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                if (currentY > maxY) {
                    actionLayout.setAlpha(1);
                } else {
                    float f = currentY / maxY;
                    actionLayout.setAlpha(f);
                }
            }
        });
        actionLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return actionLayout.getAlpha() == 1;
            }
        });
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }

    private void initLoad() {
        Observable<UserPartnerWrapper> observable = UserApi.getUserProfileObb(id);
        userProfileSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setContentView(scrollableLayout)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<UserPartnerWrapper>() {
                    @Override
                    public void onNext(UserPartnerWrapper userPartner) {
                        userPartnerWrapper = userPartner;
                        userPartnerWrapper.setCurrentUserById(id);
                        setAvatars();
                        setUserProfileView();
                        viewPager.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        observable.subscribe(userProfileSub);
    }

    private void setUserProfileView() {
        if (userPartnerWrapper == null || userPartnerWrapper.getUser() == null ||
                userPartnerWrapper.getUser()
                .getUserProfile() == null) {
            return;
        }
        tvNick.setText(userPartnerWrapper.getUser()
                .getUserProfile()
                .getNick());
        tvTitle.setText(userPartnerWrapper.getUser()
                .getUserProfile()
                .getNick());
        tvGender.setText(userPartnerWrapper.getUser()
                .getUserProfile()
                .getGender() == 1 ? "男" : "女");
        //达人标志
        if (TextUtils.isEmpty(userPartnerWrapper.getUser()
                .getUserProfile()
                .getSpecialty()) || userPartnerWrapper.getUser()
                .getUserProfile()
                .getSpecialty()
                .endsWith("普通用户")) {
            tvVipLabel.setVisibility(View.GONE);
        } else {
            tvVipLabel.setText(userPartnerWrapper.getUser()
                    .getUserProfile()
                    .getSpecialty());
            tvVipLabel.setVisibility(View.VISIBLE);
        }
        //是否是会员
        if (userPartnerWrapper.getUser()
                .getUserProfile()
                .getMember() != null) {
            tvMemberLabel.setVisibility(View.VISIBLE);
            tvMemberLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserProfileActivity.this, OpenMemberActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            tvMemberLabel.setVisibility(View.GONE);
        }

        tvCity.setText(TextUtils.isEmpty(userPartnerWrapper.getUser()
                .getUserProfile()
                .getHometown()) ? getString(R.string.label_no_home_town) : userPartnerWrapper
                .getUser()
                .getUserProfile()
                .getHometown());
        if (userPartnerWrapper.getUser()
                .getUserProfile()
                .getWeddingDay() != null && userPartnerWrapper.getUser()
                .getUserProfile()
                .getWeddingDay()
                .isBeforeNow()) {
            tvWeddingDay.setText(userPartnerWrapper.getUser()
                    .getUserProfile()
                    .getGender() == 1 ? getString(R.string.label_married2) : getString(R.string
                    .label_married));
        } else {
            if (userPartnerWrapper.getUser()
                    .getUserProfile()
                    .getWeddingDay() != null && userPartnerWrapper.getUser()
                    .getUserProfile()
                    .isPending() != 0) {
                Date userWeddingDate = new Date(userPartnerWrapper.getUser()
                        .getUserProfile()
                        .getWeddingDay()
                        .getMillis());
                if (userWeddingDate.before(Calendar.getInstance()
                        .getTime())) {
                    tvWeddingDay.setText(userPartnerWrapper.getUser()
                            .getUserProfile()
                            .getGender() == 1 ? getString(R.string.label_married2) : getString(R
                            .string.label_married));
                } else {
                    tvWeddingDay.setText(getString(R.string.label_wedding_time1,
                            new SimpleDateFormat(Constants.DATE_FORMAT_SHORT,
                                    Locale.getDefault()).format(userWeddingDate)));
                }
            } else {
                tvWeddingDay.setText(userPartnerWrapper.getUser()
                        .getUserProfile()
                        .getGender() == 1 ? "" : getString(R.string.label_no_wedding_day));
            }
        }


        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && user.getId()
                .equals(userPartnerWrapper.getUser()
                        .getUserProfile()
                        .getId())) {
            // 目前显示的是用户自己的主页
            btnFollow.setText(R.string.label_edit);
            btnFollow2.setText(R.string.label_edit);
            btnFollow.setVisibility(View.GONE);
            btnFollow2.setVisibility(View.GONE);
        } else {
            // 显示的是其他用户的主页
            btnFollow.setVisibility(View.VISIBLE);
            btnFollow2.setVisibility(View.VISIBLE);
            if (userPartnerWrapper.getUser()
                    .getUserProfile()
                    .isFollowing()) {
                btnFollow.setText(R.string.label_followed);
                btnFollow2.setText(R.string.label_followed);
            } else {
                btnFollow.setText(R.string.label_follow);
                btnFollow2.setText(R.string.label_follow);
            }
        }

        if (userPartnerWrapper.getPartner() == null) {
            // 如果没有绑定伴侣,并且当前主页是自己的,则显示添加按钮,否则不显示
            if (user != null && user.getId()
                    .equals(userPartnerWrapper.getUser()
                            .getUserProfile()
                            .getId())) {
                imgPartnerAvatar.setVisibility(View.VISIBLE);
                imgPartnerAvatarUnder.setVisibility(View.VISIBLE);
            } else {
                imgPartnerAvatar.setVisibility(View.GONE);
                imgPartnerAvatarUnder.setVisibility(View.GONE);
            }
        } else {
            imgPartnerAvatar.setVisibility(View.VISIBLE);
            imgPartnerAvatarUnder.setVisibility(View.VISIBLE);
        }

        tvDesc.setText(TextUtils.isEmpty(userPartnerWrapper.getUser()
                .getUserProfile()
                .getDescription()) ? getString(R.string.label_no_intro2) : userPartnerWrapper
                .getUser()
                .getUserProfile()
                .getDescription());

        tvPraisedCount.setText(String.valueOf(userPartnerWrapper.getUser()
                .getUserProfile()
                .getPraiseCount()));
        tvFollowedCount.setText(String.valueOf(userPartnerWrapper.getUser()
                .getUserProfile()
                .getFollowCount()));
        tvFansCount.setText(String.valueOf(userPartnerWrapper.getUser()
                .getUserProfile()
                .getFansCount()));

        tvThreadCount.setText(String.valueOf(userPartnerWrapper.getUser()
                .getCommunityThreadCount()));
        tvAnswerCount.setText(String.valueOf(userPartnerWrapper.getUser()
                .getAnswerCount()));
        tvCommentCount.setText(String.valueOf(userPartnerWrapper.getUser()
                .getCommentCount()));
        tvNoteCount.setText(String.valueOf(userPartnerWrapper.getUser()
                .getNoteCount()));

        nickLayout.requestLayout();
        tvNick.requestLayout();
    }

    /**
     * 设置user和partner的avatar
     */
    private void setAvatars() {
        if (userPartnerWrapper == null || userPartnerWrapper.getUser() == null) {
            return;
        }
        String avatarPath = ImageUtil.getImagePath(userPartnerWrapper.getUser()
                .getUserProfile()
                .getAvatar(), CommonUtil.dp2px(this, 60));
        Glide.with(this)
                .load(avatarPath)
                .apply(new RequestOptions().dontAnimate())
                .into(imgAvatar);
        imgAvatar.setTag(imgAvatar.getId(), avatarPath);
        Glide.with(this)
                .load(avatarPath)
                .apply(new RequestOptions().dontAnimate())
                .into(imgAvatarUnder);
        imgAvatarUnder.setTag(imgAvatarUnder.getId(), avatarPath);
        if (userPartnerWrapper.getPartner() != null) {
            imgPartnerAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgPartnerAvatar.setBackground(null);
            imgPartnerAvatarUnder.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgPartnerAvatarUnder.setBackground(null);
            String avatarPartnerPath = ImageUtil.getImagePath(userPartnerWrapper.getPartner()
                    .getUserProfile()
                    .getAvatar(), CommonUtil.dp2px(this, 60));
            Glide.with(this)
                    .load(avatarPartnerPath)
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgPartnerAvatar);
            imgPartnerAvatar.setTag(imgPartnerAvatar.getId(), avatarPartnerPath);
            imgPartnerAvatarUnder.setTag(imgPartnerAvatarUnder.getId(), avatarPartnerPath);
            Glide.with(this)
                    .load(avatarPartnerPath)
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgPartnerAvatarUnder);
        } else {
            imgPartnerAvatar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imgPartnerAvatar.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.sp_oval_line));
            imgPartnerAvatar.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.icon_cross_add_gray));
        }

        avatarLeft = imgAvatar;
        avatarRight = imgPartnerAvatar;

        imgAvatar.post(new Runnable() {
            @Override
            public void run() {
                avatarLeftX = imgAvatar.getX();
                avatarRightX = imgPartnerAvatar.getX();
                avatarLeftWidth = imgAvatar.getWidth();
                avatarRightWidth = imgPartnerAvatar.getWidth();
            }
        });
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (fragments == null) {
                fragments = new SparseArray<>();
            }
            ScrollAbleFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case 0:
                    fragment = MyNoteListFragment.newInstance(MyNoteListFragment.TYPE_USER,
                            userPartnerWrapper.getUser()
                                    .getUserProfile()
                                    .getId());
                    fragment.setOnTabTextChangeListener(new OnTabTextChangeListener() {
                        @Override
                        public void onTabTextChange(int totalCount) {
                            tvNoteCount.setText(String.valueOf(totalCount));
                        }
                    });
                    break;
                case 1:
                    fragment = UserCommunityThreadsFragment.newInstance(userPartnerWrapper.getUser()
                            .getUserProfile()
                            .getId());
                    break;
                case 2:
                    fragment = new MyAnswersFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("is_for_user_profile", true);
                    args.putLong("user_id",
                            userPartnerWrapper.getUser()
                                    .getUserProfile()
                                    .getId());
                    fragment.setArguments(args);
                    break;
                case 3:
                    fragment = UserCommentFragment.newInstance(userPartnerWrapper.getUser()
                            .getUserProfile()
                            .getId());
                    break;
            }
            if (fragment != null) {
                fragments.put(position, fragment);
            }
            scrollableLayout.getHelper()
                    .setCurrentScrollableContainer(fragments.get(0));

            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @OnClick({R.id.btn_follow, R.id.btn_follow2})
    void onRightAction() {
        if (userPartnerWrapper == null) {
            return;
        }
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && user.getId()
                .equals(userPartnerWrapper.getUser()
                        .getUserProfile()
                        .getId())) {
            // 编辑自己
            Intent intentEdit = new Intent(this, AccountEditActivity.class);
            startActivityForResult(intentEdit, Constants.RequestCode.EDIT_USER_INFO);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            // 关注或者取消关注
            onFollow(userPartnerWrapper.getUser()
                            .getUserProfile()
                            .getId(),
                    userPartnerWrapper.getUser()
                            .getUserProfile()
                            .isFollowing());
        }
    }

    /**
     * @param following 0 未关注 1 已关注
     */
    public void onFollow(final long userId, boolean following) {
        if (!Util.loginBindChecked(this)) {
            return;
        }
        if (!following) {
            // 没有关注, 点击后重新关注
            progressBar.setVisibility(View.VISIBLE);
            try {
                JSONObject object = new JSONObject();
                object.put("user_id", String.valueOf(userId));
                new StatusHttpPostTask(UserProfileActivity.this, new StatusRequestListener() {
                    @Override
                    public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                        progressBar.setVisibility(View.GONE);
                        userPartnerWrapper.getUser()
                                .getUserProfile()
                                .setFollowing(true);
                        btnFollow.setText(R.string.label_followed);
                        btnFollow2.setText(R.string.label_followed);
                        Util.showToast(getString(R.string.msg_follow_success),
                                UserProfileActivity.this);
                    }

                    @Override
                    public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                        progressBar.setVisibility(View.GONE);
                        Util.showToast(getString(R.string.msg_fail_to_follow),
                                UserProfileActivity.this);
                    }
                }).execute(Constants.getAbsUrl(Constants.HttpPath.NEW_FOLLOW_USER),
                        object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            // 已经关注, 点击取消关注
            if (followDialog != null && followDialog.isShowing()) {
                return;
            }

            followDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_msg_notice, null);
            ImageView imageView = (ImageView) dialogView.findViewById(R.id.img_notice);
            Button confirm = (Button) dialogView.findViewById(R.id.btn_notice_confirm);
            Button cancel = (Button) dialogView.findViewById(R.id.btn_notice_cancel);
            cancel.setVisibility(View.VISIBLE);
            TextView tvMsg = (TextView) dialogView.findViewById(R.id.tv_notice_msg);
            tvMsg.setText(getString(R.string.msg_cancel_following));
            imageView.setImageResource(R.drawable.icon_notice_bell_primary);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followDialog.cancel();
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        JSONObject object = new JSONObject();
                        object.put("user_id", String.valueOf(userId));
                        new StatusHttpPostTask(UserProfileActivity.this,
                                new StatusRequestListener() {
                                    @Override
                                    public void onRequestCompleted(
                                            Object object, ReturnStatus returnStatus) {
                                        progressBar.setVisibility(View.GONE);
                                        userPartnerWrapper.getUser()
                                                .getUserProfile()
                                                .setFollowing(false);
                                        btnFollow.setText(R.string.label_follow);
                                        btnFollow2.setText(R.string.label_follow);

                                        Util.showToast(getString(R.string
                                                        .msg_cancel_following_success),
                                                UserProfileActivity.this);
                                    }

                                    @Override
                                    public void onRequestFailed(
                                            ReturnStatus returnStatus, boolean network) {
                                        progressBar.setVisibility(View.GONE);
                                        Util.showToast(getString(R.string
                                                        .msg_fail_to_cancel_follow),
                                                UserProfileActivity.this);
                                    }
                                }).execute(Constants.getAbsUrl(Constants.HttpPath.NEW_FOLLOW_USER),
                                object.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followDialog.cancel();
                }
            });
            followDialog.setContentView(dialogView);
            Window window = followDialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            layoutParams.width = Math.round(point.x * 5 / 7);
            window.setAttributes(layoutParams);
            followDialog.show();
        }
    }

    @OnClick({R.id.btn_back, R.id.btn_back2})
    void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.follow_count_layout)
    void onFollowers() {
        Intent intent = new Intent(this, FollowActivity.class);
        intent.putExtra("userId",
                userPartnerWrapper.getUser()
                        .getUserProfile()
                        .getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.fans_count_layout)
    void onFans() {
        Intent intent = new Intent(this, FansActivity.class);
        // 转换一个主工程常用的user实体给FansActivity使用,只有两个有效字段
        User user = new User(userPartnerWrapper.getUser()
                .getUserProfile()
                .getId());
        user.setGender(userPartnerWrapper.getUser()
                .getUserProfile()
                .getGender());
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick({R.id.img_partner_avatar, R.id.img_avatar, R.id.img_avatar_under, R.id
            .img_partner_avatar_under})
    void onAvatarClick(View view) {
        if (userPartnerWrapper.getPartner() == null && view.getId() == R.id.img_partner_avatar) {
            Intent intent = new Intent(this, BindingPartnerActivity.class);
            startActivity(intent);
        } else {
            onSwapUser(view);
        }
    }

    /**
     * 交换user和partner的avatars视图
     *
     * @param view
     */
    void onSwapUser(View view) {
        if (view.getId() == R.id.img_partner_avatar_under || view.getId() == R.id
                .img_partner_avatar) {
            if (isAnimating)
                return;
            isAnimating = true;
            avatarsLayoutUnder.setVisibility(View.GONE);
            avatarsLayout.setVisibility(View.VISIBLE);
            long anim_duration = 500;

            AnimUtil.swapViewsHorizontal(avatarLeft,
                    avatarRight,
                    avatarLeftWidth,
                    avatarRightWidth,
                    avatarLeftX,
                    avatarRightX,
                    anim_duration);
            String avatarPath = (String) imgAvatarUnder.getTag(imgAvatarUnder.getId());
            String avatarPartnerPath = (String) imgPartnerAvatarUnder.getTag
                    (imgPartnerAvatarUnder.getId());
            Glide.with(UserProfileActivity.this)
                    .load(avatarPath)
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgPartnerAvatarUnder);
            imgPartnerAvatarUnder.setTag(imgPartnerAvatarUnder.getId(), avatarPath);
            Glide.with(UserProfileActivity.this)
                    .load(avatarPartnerPath)
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgAvatarUnder);
            imgAvatarUnder.setTag(imgAvatarUnder.getId(), avatarPartnerPath);

            new CountDownTimer(anim_duration - 100, anim_duration - 100) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    isAnimating = false;
                    userPartnerWrapper.swapProfile();
                    setUserProfileView();

                    avatarsLayout.setVisibility(View.GONE);
                    avatarsLayoutUnder.setVisibility(View.VISIBLE);
                    refreshFragments();
                }
            }.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_USER_INFO:
                    if (data != null && data.getBooleanExtra("refresh", false)) {
                        // 编辑用户信息后重新刷新
                        initLoad();
                        fragments.get(0)
                                .refresh(userPartnerWrapper.getUser()
                                        .getUserProfile()
                                        .getId());
                        new UserTask(this, null).execute();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshFragments() {
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i)
                    .refresh(userPartnerWrapper.getUser()
                            .getUserProfile()
                            .getId());
        }
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(userProfileSub);
        super.onFinish();
    }
}
