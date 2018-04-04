package me.suncloud.marrymemo.view.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljpushlibrary.websocket.PushSocket;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;
import com.slider.library.Indicators.CirclePageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.fragment.login.PhoneLoginFragment;
import me.suncloud.marrymemo.fragment.login.ThirdLoginBindFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.login.LoginResult;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.CityListActivity;
import me.suncloud.marrymemo.view.MainActivity;
import rx.Subscriber;

/**
 * Created by wangtao on 2017/7/12.
 */

@Route(path = RouterPath.IntentPath.Customer.LoginActivityPath.LOGIN)
public class LoginActivity extends HljBaseNoBarActivity implements PhoneLoginFragment
        .LoginCallback {

    @BindView(R.id.guide_pager)
    ViewPager guidePager;
    @BindView(R.id.btn_start_login)
    Button btnStartLogin;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.tv_terms)
    TextView tvTerms;
    @BindView(R.id.indicator)
    CirclePageIndicator indicator;
    private int type;

    private boolean isReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", 0);
        isReset = getIntent().getBooleanExtra(RouterPath.IntentPath.Customer.Login.ARG_IS_RESET,false);
        setContentView(R.layout.activity_login);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        if(isReset) {
            Session.getInstance()
                    .logout(this);
        }
        initView();
    }

    private void initView() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager
                .OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    btnStartLogin.setVisibility(View.GONE);
                    tvTerms.setVisibility(View.VISIBLE);
                } else {
                    btnStartLogin.setVisibility(View.VISIBLE);
                    tvTerms.setVisibility(View.GONE);
                }
            }
        });
        guidePager.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (guidePager.getWidth() > 0 && guidePager.getHeight() > 0) {
                            guidePager.getViewTreeObserver()
                                    .removeOnPreDrawListener(this);
                            guidePager.setAdapter(new GuideAdapter());
                            indicator.setViewPager(guidePager);
                        }
                        return false;
                    }
                });
    }

    @OnClick(R.id.btn_start_login)
    public void onStartLogin() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("phoneLoginFragment");
        if (fragment != null) {
            return;
        }
        fragment = PhoneLoginFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_content, fragment, "phoneLoginFragment");
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @OnClick(R.id.tv_terms)
    public void onTerms() {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", Constants.USER_PROTOCOL_URL);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onComplete(LoginResult loginResult) {
        Session.getInstance()
                .setCurrentUser(this, loginResult.getUser());
        User user = Session.getInstance()
                .getCurrentUser();
        Session.getInstance()
                .refreshCartItemsCount(this);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        String cid = sharedPreferences.getString("clientid", "null");
        CustomCommonApi.saveClientInfo(this, cid)
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });

        if (user.getWeddingDay() == null && user.getIsPending() != 1) {
            //设置婚期
            Intent intent = new Intent(this, WeddingDateSetActivity.class);
            intent.putExtra("type", type);
            intent.putExtra(RouterPath.IntentPath.Customer.Login.ARG_IS_RESET, isReset);
            startActivity(intent);
            finish();
        } else if(isReset){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.activity_anim_default, R.anim.activity_anim_default);
        }else if (type != HljCommon.Login.REGISTER) {
            PushSocket.INSTANCE.onStart(this);
            setResult(Activity.RESULT_OK);
            finish();
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
        } else {
            gotoMainActivity();
        }
    }

    private void gotoMainActivity() {
        Intent intent;
        if (Session.getInstance()
                .hasSetMyCity(this)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, CityListActivity.class);
            intent.putExtra(CityListActivity.ARG_IS_INITIAL_PAGE, true);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.activity_anim_default);
    }

    @Override
    public void onThirdBind(ThirdLoginParameter parameter) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("thirdLoginBindFragment");
        if (fragment != null) {
            return;
        }
        fragment = ThirdLoginBindFragment.newInstance(parameter);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_content, fragment, "thirdLoginBindFragment");
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }


    private class GuideAdapter extends PagerAdapter {

        private int[] images = {R.drawable.image_login_background_1, R.drawable
                .image_login_background_2, R.drawable.image_login_background_3, R.drawable
                .image_login_background_4};
        private int[] imageX2s = {R.drawable.image_login_background_x2_1, R.drawable
                .image_login_background_x2_2, R.drawable.image_login_background_x2_3, R.drawable
                .image_login_background_x2_4};

        @Override
        public int getCount() {
            return images.length;
        }

        private int getData(int position) {
            if (guidePager.getHeight() >= guidePager.getWidth() * 1.9) {
                return imageX2s[position];
            } else {
                return images[position];
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View convertView = View.inflate(container.getContext(),
                    R.layout.login_guide_item,
                    null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            Glide.with(LoginActivity.this)
                    .load(getData(position))
                    .apply(new RequestOptions().dontAnimate())
                    .into(viewHolder.ivImage);
            container.addView(convertView);
            return convertView;
        }

        private class ViewHolder {
            ImageView ivImage;

            ViewHolder(View view) {
                ivImage = (ImageView) view.findViewById(R.id.iv_image);
            }
        }
    }
}
