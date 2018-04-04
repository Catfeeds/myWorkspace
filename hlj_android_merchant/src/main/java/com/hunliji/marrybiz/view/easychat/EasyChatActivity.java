package com.hunliji.marrybiz.view.easychat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.chat.ChatApi;
import com.hunliji.marrybiz.fragment.easychat.EasyChatSettingFragment;
import com.hunliji.marrybiz.fragment.easychat.EasyChatWebFragment;
import com.hunliji.marrybiz.model.easychat.EasyChat;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by wangtao on 2017/8/14.
 */

public class EasyChatActivity extends HljBaseActivity {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Subscription easyChatSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_chat);
        ButterKnife.bind(this);
        boolean isActive=getIntent().getBooleanExtra("isActive",false);
        if(isActive){
            onEasyChatFragment(true);
        }else {
            initLoad();
        }
    }

    private void initLoad() {
        if(easyChatSubscription!=null&&!easyChatSubscription.isUnsubscribed()){
            return;
        }
        easyChatSubscription = ChatApi.getCheck()
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setEmptyView(emptyView)
                        .setProgressBar(progressBar)
                        .setOnNextListener(new SubscriberOnNextListener<EasyChat>() {
                            @Override
                            public void onNext(EasyChat easyChat) {
                                onEasyChatFragment(easyChat != null && easyChat.isActive());
                            }
                        })
                        .build());
    }


    private void onEasyChatFragment(boolean isActive) {
        if (isActive) {
            setOkText(R.string.label_look_help);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                    "easyChatSettingFragment");
            if (fragment != null) {
                return;
            }
            fragment = EasyChatSettingFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_content, fragment, "easyChatSettingFragment")
                    .commit();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag
                    ("easyChatWebFragment");
            if (fragment != null) {
                return;
            }
            fragment = EasyChatWebFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_content, fragment, "easyChatWebFragment")
                    .commit();

        }
    }

    @Override
    public void onOkButtonClick() {
        HljWeb.startWebView(this, Constants.getAbsWebUrl(Constants.HttpPath.EASY_CHAT_HELP));
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(easyChatSubscription);
        super.onFinish();
    }
}
