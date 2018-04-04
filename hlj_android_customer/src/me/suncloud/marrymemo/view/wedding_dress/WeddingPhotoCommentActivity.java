package me.suncloud.marrymemo.view.wedding_dress;

import android.os.Bundle;

import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.views.activities.BasePostCommentActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.weddingdress.WeddingDressApi;
import me.suncloud.marrymemo.model.weddingdress.WeddingCommentBody;
import rx.Observable;

/**
 * Created by hua_rong on 2017/5/16.
 * 晒婚纱照评价
 */

public class WeddingPhotoCommentActivity extends BasePostCommentActivity implements
        BasePostCommentActivity.CommentInterface {

    private HljHttpSubscriber commentSubscriber;
    private CommunityPost post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCommentInterface(this);
        post = getIntent().getParcelableExtra("post");
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getHint() {
        return getString(R.string.label_comment_wedding_dress);
    }

    @Override
    public boolean isNeedMeasureKeyboardHeight() {
        return false;
    }

    @Override
    public int getMaxLength() {
        return 140;
    }

    @Override
    public void onComment() {
        if (post != null) {
            WeddingCommentBody body = new WeddingCommentBody();
            body.setMessage(etContent.getText()
                    .toString()
                    .trim());
            body.setThreadId(post.getCommunityThreadId());
            Observable observable = WeddingDressApi.postWeddingPhotoComment(body);
            if (commentSubscriber == null || commentSubscriber.isUnsubscribed()) {
                commentSubscriber = HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnNextListener(new SubscriberOnNextListener() {

                            @Override
                            public void onNext(Object response) {
                                ToastUtil.showCustomToast(WeddingPhotoCommentActivity.this,
                                        R.string.msg_success_to_comment___note);
                                setResult(RESULT_OK);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        })
                        .build();
                observable.subscribe(commentSubscriber);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(commentSubscriber);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(commentSubscriber);
        }
    }
}
