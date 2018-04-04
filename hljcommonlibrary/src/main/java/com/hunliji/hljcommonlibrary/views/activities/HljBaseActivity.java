package com.hunliji.hljcommonlibrary.views.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;
import com.hunliji.hljcommonlibrary.views.widgets.SwipeActivityRootView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscription;

@RuntimePermissions
public class HljBaseActivity extends HljTrackedActivity {


    protected CustomRootView customRootView;

    private long startTimeMillis;
    private boolean cannotBack;

    private boolean isFinishCalled;
    private List<Subscription> subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomRootView();
    }

    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        if (customRootView != null && customRootView.contentLayout != null) {
            T view = customRootView.contentLayout.findViewById(id);
            if (view != null) {
                return view;
            }
        }
        return super.findViewById(id);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        customRootView.tvToolbarTitle.setText(getTitle());
    }

    public void setSwipeBackEnable(boolean enable) {
        customRootView.activityRootView.setSwipeEnable(enable);
    }

    public void setCannotBack(boolean cannotBack) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        this.cannotBack = cannotBack;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return !(keyCode == KeyEvent.KEYCODE_BACK && cannotBack) && super.onKeyDown(keyCode, event);
    }

    public void setOkButton(int resource) {
        if (resource == 0) {
            return;
        }
        customRootView.itemBtn.setImageDrawable(ContextCompat.getDrawable(this, resource));
        customRootView.itemBtn.setVisibility(View.VISIBLE);
        customRootView.itemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkButtonClick();
            }
        });
    }

    public void setOkButton2(int resource) {
        if (resource == 0) {
            return;
        }
        customRootView.itemBtn2.setImageDrawable(ContextCompat.getDrawable(this, resource));
        customRootView.itemBtn2.setVisibility(View.VISIBLE);
        customRootView.itemBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkButton2Click();
            }
        });
    }

    public void setBackButton(int resource) {
        if (resource != 0 && getSupportActionBar() != null) {
            Drawable drawable = ContextCompat.getDrawable(this, resource);
            if (drawable == null) {
                return;
            }
            int color = ThemeUtil.getAttrColor(this, R.attr.hljHomeIndicatorColor, 0);
            if ((color & 0xff000000) != 0) {
                drawable.mutate()
                        .setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            getSupportActionBar().setHomeAsUpIndicator(drawable);
        }
    }

    public void hideOkButton() {
        customRootView.itemBtn.setVisibility(View.GONE);
    }

    public void hideOkButton2() {
        customRootView.itemBtn2.setVisibility(View.GONE);
    }


    public void showOkButton() {
        customRootView.itemBtn.setVisibility(View.VISIBLE);
    }

    public void hideBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    public void setOkText(int resource) {
        customRootView.item.setText(resource);
        customRootView.item.setVisibility(View.VISIBLE);
        customRootView.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkButtonClick();
            }
        });
    }

    public void setOkTextColor(int color) {
        if (color != 0) {
            customRootView.item.setTextColor(color);
        }
    }

    public void setOkText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        customRootView.item.setText(text);
        customRootView.item.setVisibility(View.VISIBLE);
        customRootView.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkButtonClick();
            }
        });
    }

    public void setOkTextSize(int size) {
        customRootView.item.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void hideOkText() {
        customRootView.item.setVisibility(View.GONE);
    }

    public void showOkText() {
        customRootView.item.setVisibility(View.VISIBLE);
    }

    public void hideDividerView() {
        customRootView.divider.setVisibility(View.GONE);
    }

    public void showDividerView() {
        customRootView.divider.setVisibility(View.VISIBLE);
    }

    public void setCustomBarItem(View view) {
        if (view == null) {
            customRootView.customItemLayout.setVisibility(View.GONE);
        } else {
            customRootView.customItemLayout.setVisibility(View.VISIBLE);
            customRootView.customItemLayout.removeAllViews();
            customRootView.customItemLayout.addView(view);
        }
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        if (imm != null && this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onOkButtonClick() {

    }

    public void onOkButton2Click() {

    }

    /**
     * 初始化ActionBar和滑动返回视图嵌入
     */
    private void initCustomRootView() {
        SwipeActivityRootView rootView = (SwipeActivityRootView) getLayoutInflater().inflate(R
                        .layout.custom_app_bar_layout,
                null);
        customRootView = new CustomRootView(rootView);
        rootView.initWithViews(customRootView.dragView, customRootView.shadowView);

        setSupportActionBar(customRootView.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        setBackButton(R.mipmap.icon_back_primary);
        customRootView.tvToolbarTitle.setText(getTitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            customRootView.appBarLayout.setElevation(0);
            customRootView.toolbar.setElevation(0);

            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));

            setMiuiStatusBarDarkMode(ThemeUtil.getAttrBoolean(this,
                    R.attr.hljMiuiDarkStatusBar,
                    false));

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            customRootView.appBarLayout.setPadding(0, getStatusBarHeight(this), 0, 0);
        }
        customRootView.dragView.setFitsSystemWindows(true);

        replaceActivityRootView();
    }

    /**
     * MIUI的沉浸状态栏
     *
     * @param darkmode
     */
    public void setMiuiStatusBarDarkMode(boolean darkmode) {
        Class<? extends Window> clazz = getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception ignored) {
        }
    }

    public int getStatusBarHeight() {
        return getStatusBarHeight(this);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = context.getResources()
                    .getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources()
                        .getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public void setActionBarPadding(View... views) {
        for (View view : views) {
            setActionBarPadding(this, view);
        }
    }

    public static void setActionBarPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight(context);

            view.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    public void replaceActivityRootView() {
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decor.removeView(decorChild);
        customRootView.contentLayout.addView(decorChild);
        decor.addView(customRootView.activityRootView);
        decor.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startActivityForResult(
            Intent intent, int requestCode, @Nullable Bundle options) {
        if (Math.abs(System.currentTimeMillis() - startTimeMillis) > 1000) {
            startTimeMillis = System.currentTimeMillis();
            super.startActivityForResult(intent, requestCode, options);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(null);
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    public void callUp(Uri uri) {
        if (!uri.toString()
                .contains(",")) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, uri);
            super.startActivity(phoneIntent);
        } else {
            HljBaseActivityPermissionsDispatcher.onCallUpWithCheck(this, uri);
        }
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void onCallUp(Uri uri) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL, uri);
        super.startActivity(phoneIntent);
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    void onRationaleCallUP(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_call_up___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        HljBaseActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && !isFinishCalled) {
            onFinish();
        }
    }

    protected void onFinish() {
        isFinishCalled = true;
        if (!CommonUtil.isCollectionEmpty(subscriptions)) {
            for (Subscription sub : subscriptions) {
                if (sub != null && !sub.isUnsubscribed()) {
                    sub.unsubscribe();
                }
            }
        }
    }

    /**
     * 系统生命周期调用时间会延迟使用{@link #onFinish}方法做Activity退出操作
     *
     * @deprecated use {@link #onFinish} instead
     */
    @Override
    @Deprecated
    protected void onDestroy() {
        if (!isFinishCalled) {
            onFinish();
        }
        super.onDestroy();
    }

    /**
     * 在Activity代码外部加入RxSub的依赖，使得activity被消除时，也消除对应的sub
     *
     * @param sub
     */
    public void insertSubFromOutSide(Subscription sub) {
        if (subscriptions == null) {
            subscriptions = new ArrayList<>();
        }
        if (!subscriptions.contains(sub)) {
            subscriptions.add(sub);
        }
    }

    public View getItemView() {
        return customRootView.item;
    }

    public View getItemButton() {
        return customRootView.itemBtn;
    }

    private Class getCurrentClass() {
        return this.getClass();
    }

    class CustomRootView {
        SwipeActivityRootView activityRootView;
        TextView tvToolbarTitle;
        TextView tvToolbarSubTitle;
        TextView item;
        ImageButton itemBtn;
        ImageButton itemBtn2;
        FrameLayout customItemLayout;
        AppBarLayout appBarLayout;
        Toolbar toolbar;
        LinearLayout contentLayout;
        RelativeLayout drawerContentLayout;
        View dragView;
        View shadowView;
        View divider;
        DrawerLayout drawer;
        NavigationView navigationView;

        CustomRootView(SwipeActivityRootView view) {
            this.activityRootView = view;
            this.tvToolbarTitle = view.findViewById(R.id.root_tv_toolbar_title);
            this.tvToolbarSubTitle = view.findViewById(R.id.root_tv_toolbar_sub_title);
            this.item = view.findViewById(R.id.root_item);
            this.appBarLayout = view.findViewById(R.id.root_appbar);
            this.toolbar = view.findViewById(R.id.root_toolbar);
            this.contentLayout = view.findViewById(R.id.root_content_layout);
            this.drawerContentLayout = view.findViewById(R.id.drawer_content_layout);
            this.dragView = view.findViewById(R.id.root_drag_view);
            this.shadowView = view.findViewById(R.id.root_shadow_view);
            this.itemBtn = view.findViewById(R.id.root_img_btn);
            this.divider = view.findViewById(R.id.root_divider);
            this.drawer = view.findViewById(R.id.drawer_layout);
            this.navigationView = view.findViewById(R.id.nav_view);
            this.itemBtn2 = view.findViewById(R.id.root_img_btn_2);
            this.customItemLayout = view.findViewById(R.id.root_custom_item_layout);

            init();
        }

        private void init() {
            if (!HljCommon.debug) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HljBaseActivity.this,
                        drawer,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();
                View headerView = navigationView.getHeaderView(0);
                if (headerView != null) {
                    TextView tvHeader = headerView.findViewById(R.id.tv_header_title);
                    TextView tvSubHeader = headerView.findViewById(R.id.tv_header_sub_title);
                    if (tvHeader != null) {
                        tvHeader.setText(getCurrentClass().getSimpleName());
                        tvSubHeader.setText(getCurrentClass().getPackage()
                                .getName());
                    }
                }
                navigationView.setNavigationItemSelectedListener(new NavigationView
                        .OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.nav_network) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Debug.HTTP_LOG_LIST)
                                    .navigation(HljBaseActivity.this);
                        } else if (id == R.id.nav_change_host) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.Debug.CHANGE_HOST)
                                    .navigation(HljBaseActivity.this);
                        } else if (id == R.id.nav_tracker) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Debug.TRACKER_LOG_LIST)
                                    .navigation(HljBaseActivity.this);
                        } else if (id == R.id.nav_crash) {
                            startActivity(new Intent(HljBaseActivity.this,
                                    CrashListActivity.class));
                        } else if (id == R.id.nav_page_name) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Debug.PAGE_LABEL_CHECK)
                                    .navigation(HljBaseActivity.this);
                        }
                        drawer.closeDrawer(GravityCompat.END);
                        menuItem.setChecked(false);
                        return true;
                    }
                });
            }
        }
    }
}
