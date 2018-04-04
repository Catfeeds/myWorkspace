package com.hunliji.marrybiz.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.utils.ShareMinProgramUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.tools.ToolsApi;
import com.hunliji.marrybiz.model.WXWall;
import com.hunliji.marrybiz.util.DialogUtil;

import rx.Subscription;


public class WXWallActivity extends HljWebViewActivity {

    private Button btnShare;
    private WXWall wxWall;

    private Subscription loadSubscription;

    @Override
    protected String pageTitle() {
        try {
            return getTitle().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoad();
    }

    @Override
    protected void initBottomLayout(ViewGroup bottomLayout) {
        super.initBottomLayout(bottomLayout);
        View bottomView = View.inflate(this, R.layout.wx_wall_bottom_layout, bottomLayout);
        btnShare = bottomView.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.createWXWallShareDlg(WXWallActivity.this,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ShareMinProgramUtil shareMinProgramUtil = new ShareMinProgramUtil(
                                        WXWallActivity.this,
                                        wxWall.getShareInfo(),
                                        null);
                                shareMinProgramUtil.shareToWeiXin();
                            }
                        },
                        null)
                        .show();
            }
        });
    }

    private void initLoad() {
        loadSubscription = ToolsApi.getWXWall()
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressBar(progressBar)
                        .setOnNextListener(new SubscriberOnNextListener<WXWall>() {
                            @Override
                            public void onNext(WXWall wxWall) {
                                WXWallActivity.this.wxWall = wxWall;
                                if (!TextUtils.isEmpty(wxWall.getUrl())) {
                                    loadUrl(wxWall.getUrl());
                                }
                                if (wxWall.getShareInfo() != null) {
                                    btnShare.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .build());
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(loadSubscription);
        super.onFinish();
    }
}