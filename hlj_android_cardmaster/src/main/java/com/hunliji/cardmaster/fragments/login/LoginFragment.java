package com.hunliji.cardmaster.fragments.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.cardmaster.Constants;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.api.login.LoginApi;
import com.hunliji.cardmaster.models.login.LoginResult;
import com.hunliji.hljcardlibrary.views.activities.CardThemeActivity;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wangtao on 2017/11/27.
 */

public class LoginFragment extends RefreshFragment implements PhoneLoginFragment
        .ChildLoginCallback {

    public static final String ARG_IS_GUIDE = "isGuide";

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_preview)
    Button btnPreview;
    @BindView(R.id.button_layout)
    LinearLayout buttonLayout;
    @BindView(R.id.tv_terms)
    TextView tvTerms;
    Unbinder unbinder;

    public static LoginFragment newInstance(boolean isGuide) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_GUIDE, isGuide);
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }


    private void initView() {
        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager
                .OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                    buttonLayout.setVisibility(View.GONE);
                    tvTerms.setVisibility(View.VISIBLE);
                } else {
                    buttonLayout.setVisibility(View.VISIBLE);
                    tvTerms.setVisibility(View.GONE);
                }
            }
        });
        if (getArguments() != null && !getArguments().getBoolean(ARG_IS_GUIDE, false)) {
            onStartLogin();
        }
    }

    @OnClick(R.id.btn_login)
    public void onStartLogin() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag("phoneLoginFragment");
        if (fragment != null) {
            return;
        }
        fragment = PhoneLoginFragment.newInstance();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.child_fragment_content, fragment, "phoneLoginFragment");
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @OnClick(R.id.btn_preview)
    public void onPreview() {
        Intent intent = new Intent(getContext(), CardThemeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_terms)
    public void onTerms() {
        Intent intent = new Intent(getContext(), HljWebViewActivity.class);
        intent.putExtra("path", Constants.USER_PROTOCOL_URL);
        startActivity(intent);
    }


    @Override
    public void onComplete(LoginResult loginResult) {
        try {
            UserSession.getInstance()
                    .setUser(getContext(), loginResult.getUser());
            HljViewTracker.INSTANCE.setCurrentUserId(loginResult.getUser()
                    .getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cid = SPUtils.getString(getContext(), "clientid", "");
        LoginApi.saveClientInfo(getContext(), cid)
                .subscribe(new EmptySubscriber());
        if (getActivity() != null && getActivity() instanceof LoginCallback) {
            ((LoginCallback) getActivity()).onComplete();
        }
    }

    @Override
    public void onThirdBind(ThirdLoginParameter parameter) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag("thirdLoginBindFragment");
        if (fragment != null) {
            return;
        }
        fragment = ThirdLoginBindFragment.newInstance(parameter);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.child_fragment_content, fragment, "thirdLoginBindFragment");
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface LoginCallback {

        void onComplete();
    }
}
