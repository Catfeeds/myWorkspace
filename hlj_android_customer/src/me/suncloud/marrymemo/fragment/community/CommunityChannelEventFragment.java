package me.suncloud.marrymemo.fragment.community;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.example.suncloud.hljweblibrary.views.widgets.CustomWebView;
import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljNestedScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;

/**
 * 新娘圈活动页
 * Created by jinxin on 2018/3/16 0016.
 */

public class CommunityChannelEventFragment extends RefreshFragment {

    @BindView(R.id.web_view)
    CustomWebView webView;
    Unbinder unbinder;
    @BindView(R.id.ll_web_view)
    LinearLayout llWebView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.scrollView)
    HljNestedScrollView scrollView;

    private int offset;

    public static CommunityChannelEventFragment newInstance() {
        CommunityChannelEventFragment fragment = new CommunityChannelEventFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offset = CommonUtil.dp2px(getContext(), 20);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community_channel_event,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initWidget();
        return rootView;
    }

    private void initWidget() {
        emptyView.showEmptyView();
    }

    private void loadWebView(CommunityEvent event) {
        if (event == null || TextUtils.isEmpty(event.getContent())) {
            emptyView.showEmptyView();
            return;
        }
        emptyView.hideEmptyView();
        webView.loadDataWithBaseURL(null, event.getContent(), "text/html", "UTF-8", null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof CommunityEvent) {
            CommunityEvent event = (CommunityEvent) params[0];
            loadWebView(event);
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        if (llWebView != null && llWebView.getChildCount() > 0) {
            llWebView.removeView(webView);
        }
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }
}
