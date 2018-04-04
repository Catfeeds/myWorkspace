package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;

import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

/**
 * Created by mo_yu on 2017/8/21.礼物礼金回复
 */

public class ReplyCardUserActivity extends BaseReplyActivity {

    private long id;
    private HljHttpSubscriber replySubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getLongExtra("id", 0);
    }

    @Override
    public void onReply(String message) {
        if (id <= 0) {
            return;
        }
        if (replySubscriber == null || replySubscriber.isUnsubscribed()) {
            replySubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showToast(ReplyCardUserActivity.this, "回复成功", 0);
                            Intent intent = getIntent();
                            intent.putExtra("status", 1);
                            setResult(RESULT_OK, intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    })
                    .build();
            CustomerCardApi.replyCardUserObb(id, message)
                    .subscribe(replySubscriber);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(replySubscriber);
        }
    }

    @Override
    protected void onDestroy() {
        CommonUtil.unSubscribeSubs(replySubscriber);
        super.onDestroy();
    }
}
