package com.hunliji.hljnotelibrary.views.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.PostCommentBody;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljtrackerlibrary.HljTracker;

/**
 * 笔记回复
 * Created by chen_bin on 2017/4/14 0014.
 */
public class PostNoteCommentActivity extends BasePostCommentActivity implements
        BasePostCommentActivity.CommentInterface {
    private long id;
    private String entityType;
    private String replyAuthName;
    private long replyId;
    private RepliedComment repliedComment;
    private HljHttpSubscriber postCommentSubscriber;
    private String hintContent;
    private String callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCommentInterface(this);
        id = getIntent().getLongExtra("id", 0);
        entityType = getIntent().getStringExtra("entity_type");
        repliedComment = getIntent().getParcelableExtra("replied_comment");
        replyAuthName = getIntent().getStringExtra("replied_auth_name");
        replyId = getIntent().getLongExtra("reply_id", 0);
        hintContent = getIntent().getStringExtra("hint_content");
        callback = getIntent().getStringExtra("callback");
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getHint() {
        if (repliedComment == null && TextUtils.isEmpty(replyAuthName)) {
            if (TextUtils.isEmpty(hintContent)) {
                return getString(R.string.hint_post_comment___note);
            } else {
                return hintContent;
            }
        } else {
            if (repliedComment != null) {
                return "@" + repliedComment.getUser()
                        .getName();
            } else {
                return "@" + replyAuthName;
            }

        }
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
        new HljTracker.Builder(this).eventableId(id)
                .eventableType("Note")
                .action("comment")
                .build()
                .send();
        PostCommentBody body = new PostCommentBody();
        body.setContent(etContent.getText()
                .toString());
        body.setEntityId(id);
        body.setEntityType(entityType);
        if (repliedComment != null) {
            body.setReplyId(repliedComment.getId());
        } else if (replyId != 0) {
            body.setReplyId(replyId);
        }
        CommonUtil.unSubscribeSubs(postCommentSubscriber);
        if (postCommentSubscriber == null || postCommentSubscriber.isUnsubscribed()) {
            postCommentSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .setOnNextListener(new SubscriberOnNextListener<RepliedComment>() {
                        @Override
                        public void onNext(RepliedComment response) {
                            if (!TextUtils.isEmpty(callback)) {
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.REPLY_NOTE_COMMENT,
                                                callback));
                            }
                            ToastUtil.showCustomToast(PostNoteCommentActivity.this,
                                    R.string.msg_success_to_comment___note);
                            Intent intent = getIntent();
                            intent.putExtra("comment_response", response);
                            setResult(RESULT_OK, intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    })
                    .build();
            CommonApi.addFuncObb(body)
                    .subscribe(postCommentSubscriber);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(postCommentSubscriber);
        }
    }

    @Override
    protected void onDestroy() {
        CommonUtil.unSubscribeSubs(postCommentSubscriber);
        super.onDestroy();
    }
}
