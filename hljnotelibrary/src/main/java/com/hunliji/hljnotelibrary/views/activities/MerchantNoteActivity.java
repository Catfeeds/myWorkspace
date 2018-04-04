package com.hunliji.hljnotelibrary.views.activities;

import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.BannerJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.views.fragments.MerchantNoteListFragment;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/9/21.商家动态（笔记）用户端
 */
public class MerchantNoteActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener, ScrollableLayout.OnScrollListener {

    public static final String SORT_NOTE_HOT = "collect_count";//笔记最热
    public static final String SORT_NOTE_NEW = "id";//笔记最新
    @BindView(R2.id.btn_back)
    ImageView btnBack;
    @BindView(R2.id.img_merchant_avatar_top)
    RoundedImageView imgMerchantAvatarTop;
    @BindView(R2.id.tv_merchant_name_top)
    TextView tvMerchantNameTop;
    @BindView(R2.id.merchant_info_top)
    LinearLayout merchantInfoTop;
    @BindView(R2.id.btn_follow)
    TextView btnFollow;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.img_merchant_avatar)
    RoundedImageView imgMerchantAvatar;
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.img_level)
    ImageView imgLevel;
    @BindView(R2.id.img_bond)
    ImageView imgBond;
    @BindView(R2.id.rating_bar)
    RatingBar ratingBar;
    @BindView(R2.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R2.id.tv_merchant_property)
    TextView tvMerchantProperty;
    @BindView(R2.id.tv_follow_count)
    TextView tvFollowCount;
    @BindView(R2.id.merchant_info_layout)
    LinearLayout merchantInfoLayout;
    @BindView(R2.id.indicator)
    TabPageIndicator indicator;
    @BindView(R2.id.tv_sort)
    TextView tvSort;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R2.id.related_work_view)
    LinearLayout relatedWorkView;
    @BindView(R2.id.message_merchant_view)
    LinearLayout messageMerchantView;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;

    private long id;
    private int logoWidth;
    private int logoWidthS;
    private boolean isFollow;
    private Merchant merchant;
    private String sort;

    private CheckableLinearGroup sortCheckGroup;
    private ViewPagerFragmentAdapter fragmentAdapter;
    private MerchantNoteListFragment noteListFragment;
    private PopupWindow sortPopupWindow;

    private HljHttpSubscriber loadSubscriber;
    private HljHttpSubscriber followSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_note___note);
        ButterKnife.bind(this);
        initValue();
        initView();
        initLoad();
        setDefaultStatusBarPadding();
    }

    private void initValue() {
        merchant = getIntent().getParcelableExtra("merchant");
        id = getIntent().getLongExtra("id", 0);
        sort = SORT_NOTE_NEW;
        logoWidth = CommonUtil.dp2px(this, 62);
        logoWidthS = CommonUtil.dp2px(this, 30);
    }

    private void initView() {
        scrollableLayout.setMode(PullToRefreshBase.Mode.DISABLED);
        scrollableLayout.setVisibility(View.GONE);
        scrollableLayout.getRefreshableView()
                .addOnScrollListener(this);
    }

    private void initLoad() {
        if (merchant != null) {
            setMerchantInfo(merchant);
        } else {
            loadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setContentView(scrollableLayout)
                    .setOnNextListener(new SubscriberOnNextListener<Merchant>() {
                        @Override
                        public void onNext(Merchant merchantData) {
                            merchant = merchantData;
                            setMerchantInfo(merchant);
                        }
                    })
                    .build();
            NoteApi.getMerchantInfoV2(id)
                    .subscribe(loadSubscriber);
        }
    }

    private void setMerchantInfo(Merchant merchant) {
        if (merchant == null || merchant.getId() == 0) {
            return;
        }
        sort = SORT_NOTE_NEW;
        tvSort.setText("最新");
        noteListFragment = MerchantNoteListFragment.newInstance(merchant.getUserId());
        fragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        indicator.setOnTabChangeListener(this);
        indicator.setTabViewId(R.layout.merchant_note_tab_view___note);
        indicator.setPagerAdapter(fragmentAdapter);
        viewPager.setAdapter(fragmentAdapter);
        scrollableLayout.getRefreshableView()
                .getHelper()
                .setCurrentScrollableContainer(noteListFragment);
        scrollableLayout.setVisibility(View.VISIBLE);

        isFollow = merchant.isCollected();
        setBtnFollow();
        tvMerchantName.setText(merchant.getName());
        tvMerchantNameTop.setText(merchant.getName());
        tvCommentCount.setText(merchant.getCommentCount() + "条");
        tvFollowCount.setText("关注 " + merchant.getFansCount());
        tvMerchantProperty.setText(merchant.getPropertyName());
        ratingBar.setRating(merchant.getCommentStatistics() == null ? 0 : (float) merchant
                .getCommentStatistics()
                .getScore());
        Glide.with(this)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(logoWidth)
                        .height(logoWidth)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                .into(imgMerchantAvatar);
        Glide.with(this)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(logoWidthS)
                        .height(logoWidthS)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                .into(imgMerchantAvatarTop);
        int res = 0;
        switch (merchant.getGrade()) {
            case 2:
                res = R.mipmap.icon_merchant_level2_32_32;
                break;
            case 3:
                res = R.mipmap.icon_merchant_level3_32_32;
                break;
            case 4:
                res = R.mipmap.icon_merchant_level4_32_32;
                break;
            default:
                break;
        }
        if (res != 0) {
            imgLevel.setVisibility(View.VISIBLE);
            imgLevel.setImageResource(res);
        } else {
            imgLevel.setVisibility(View.GONE);
        }
        imgBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        bottomLayout.setVisibility(View.VISIBLE);
    }

    private void setBtnFollow() {
        btnFollow.setText(isFollow ? getString(R.string.label_followed___cm) : getString(R.string
                .label_follow___cv));
        btnFollow.setBackgroundResource(isFollow ? R.drawable.sp_r4_stroke1_gray : R.drawable
                .sp_r4_stroke1_primary_solid_white);
        int color = isFollow ? getResources().getColor(R.color.colorGray) : getResources().getColor(
                R.color.colorPrimary);
        btnFollow.setTextColor(color);
    }

    @OnClick(R2.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R2.id.btn_follow)
    void onFollow() {
        if (merchant == null) {
            return;
        }
        if (followSubscriber != null && !followSubscriber.isUnsubscribed()) {
            return;
        }
        if (isFollow) {
            //已关注
            followSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object object) {
                            isFollow = false;
                            setBtnFollow();
                            Toast.makeText(MerchantNoteActivity.this, "取消关注成功！", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .build();

            CommonApi.deleteMerchantFollowObb(merchant.getId())
                    .subscribe(followSubscriber);
        } else {
            followSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object object) {
                            isFollow = true;
                            setBtnFollow();
                            Toast.makeText(MerchantNoteActivity.this, "关注成功！", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .build();
            CommonApi.postMerchantFollowObb(merchant.getId())
                    .subscribe(followSubscriber);
        }
    }

    @OnClick(R2.id.message_merchant_view)
    void onChatMerchant() {
        //联系商家
        if (merchant == null) {
            return;
        }
        if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            Poster poster = new Poster();
            poster.setTargetType(93);
            poster.setTargetId(0L);
            BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                    .build(RouterPath.ServicePath.BANNER_JUMP)
                    .navigation(MerchantNoteActivity.this);
            if (bannerJumpService != null) {
                bannerJumpService.bannerJump(MerchantNoteActivity.this, poster, null);
            }
        } else {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                            .WS_CUSTOMER_CHAT_ACTIVITY)
                    .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER,
                            merchant.toUser())
                    .navigation(this);
        }
    }

    @OnClick(R2.id.related_work_view)
    void onRelatedWork() {
        //相关套餐
        if (merchant == null) {
            return;
        }
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.MERCHANT_WORK_LIST)
                .withLong("id", merchant.getId())
                .withInt("style", SmallWorkViewHolder.STYLE_MERCHANT_HOME_PAGE)
                .navigation(this);
    }

    @OnClick(R2.id.tv_sort)
    void onSort() {
        showSortWindow(tvSort);
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private void showSortWindow(View view) {
        if (sortPopupWindow != null && sortPopupWindow.isShowing()) {
            sortPopupWindow.dismiss();
        }

        if (sortPopupWindow == null) {
            sortPopupWindow = new PopupWindow(this);
            View contentView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_note_sort___note, null, false);
            sortPopupWindow.setContentView(contentView);
            ColorDrawable mDrawable = new ColorDrawable(ContextCompat.getColor(this,
                    android.R.color.transparent));
            sortPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            sortPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            sortPopupWindow.setBackgroundDrawable(mDrawable);
            sortPopupWindow.setOutsideTouchable(true);
            sortCheckGroup = contentView.findViewById(R.id.check_group);
            sortCheckGroup.setOnCheckedChangeListener(new CheckableLinearGroup
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                    onSortChecked(checkedId, true);
                }
            });
        }
        TextView tvNew = sortPopupWindow.getContentView()
                .findViewById(R.id.tv_new);
        TextView tvDefault = sortPopupWindow.getContentView()
                .findViewById(R.id.tv_default);
        tvDefault.setText("最新");
        tvNew.setText("最热");
        sortPopupWindow.getContentView()
                .findViewById(R.id.cb_price_high)
                .setVisibility(View.GONE);
        sortPopupWindow.getContentView()
                .findViewById(R.id.cb_price_low)
                .setVisibility(View.GONE);
        int[] location = getShowLocation(view);
        sortPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1]);
    }

    private int[] getShowLocation(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        Paint.FontMetrics fontMetrics = tvSort.getPaint()
                .getFontMetrics();
        int textHeight = (int) (Math.ceil(fontMetrics.descent - fontMetrics.ascent) + 2);
        location[1] += CommonUtil.dp2px(this, 20) + textHeight / 2;
        return location;
    }

    private void onSortChecked(int checkedId, boolean needRefresh) {
        String sortValueText = null;
        if (checkedId == R.id.cb_default) {
            sort = SORT_NOTE_NEW;
            sortValueText = "最新";
        } else if (checkedId == R.id.cb_new) {
            sort = SORT_NOTE_HOT;
            sortValueText = "最热";
        }
        if (!TextUtils.isEmpty(sortValueText)) {
            tvSort.setText(sortValueText);
        }
        if (needRefresh) {
            sortPopupWindow.dismiss();
            getCurrentFragment().refresh(sort);
        }
    }

    public ScrollAbleFragment getCurrentFragment() {
        return (ScrollAbleFragment) fragmentAdapter.getItem(viewPager.getCurrentItem());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (sortPopupWindow != null && sortPopupWindow.isShowing()) {
            sortPopupWindow.dismiss();
        }
        CommonUtil.unSubscribeSubs(loadSubscriber, followSubscriber);
    }

    @Override
    public void onScroll(int currentY, int maxY) {
        float alpha = currentY * 1.0f / maxY;
        merchantInfoTop.setAlpha(alpha);
    }

    private class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

        public ViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return noteListFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = "动态";
                    break;
            }
            return title;
        }
    }
}
