package com.hunliji.hljnotelibrary.views.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.PostCommentBody;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTips;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.R;

import org.joda.time.DateTime;

/**
 * 本地服务评价回复
 * Created by chen_bin on 2017/4/14 0014.
 */
public class PostServiceCommentActivity extends BasePostCommentActivity implements
        BasePostCommentActivity.CommentInterface {
    private ServiceComment comment;
    private RepliedComment repliedComment;
    private boolean isMerchant;
    private HljHttpSubscriber postCommentSub;
    private Dialog filterWordDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCommentInterface(this);
        comment = getIntent().getParcelableExtra("comment");
        repliedComment = getIntent().getParcelableExtra("replied_comment");
        isMerchant = getIntent().getBooleanExtra("is_merchant", false); //是否商家端
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getHint() {
        if (repliedComment == null) {
            return getString(isMerchant ? R.string.hint_reply_something___note : R.string
                    .hint_comment_something___note);
        } else {
            return "@" + (repliedComment.getUser()
                    .isMerchant() ? getString(R.string.label_comment_for_merchant___cm) :
                    repliedComment.getUser()
                    .getName());
        }
    }

    @Override
    public boolean isNeedMeasureKeyboardHeight() {
        return true;
    }

    @Override
    public int getMaxLength() {
        return 500;
    }

    @Override
    public void onComment() {
        if (comment == null) {
            return;
        }
        String contentStr = etContent.getText()
                .toString();
        if (containFilterWords(contentStr)) {
            showFilterWordDlg();
            return;
        }
        final PostCommentBody body = new PostCommentBody();
        body.setContent(etContent.getText()
                .toString());
        body.setEntityType("OrderComment");
        body.setEntityId(comment.getId());
        if (repliedComment != null) {
            body.setReplyId(repliedComment.getId());
        }
        CommonUtil.unSubscribeSubs(postCommentSub);
        postCommentSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<RepliedComment>() {
                    @Override
                    public void onNext(RepliedComment object) {
                        ToastUtil.showCustomToast(PostServiceCommentActivity.this,
                                R.string.msg_success_to_comment___note);
                        object.setContent(body.getContent());
                        object.setCreatedAt(new DateTime(HljTimeUtils.getServerCurrentTimeMillis
                                ()));
                        if (repliedComment != null) {
                            object.setReplyUser(repliedComment.getUser());
                        }
                        setResult(RESULT_OK, getIntent().putExtra("comment_response", object));
                        finish();
                        overridePendingTransition(0, 0);
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        CommonApi.addFuncObb(body)
                .subscribe(postCommentSub);
    }

    private void showFilterWordDlg() {
        if (filterWordDlg == null) {
            filterWordDlg = DialogUtil.createSingleButtonDialog(this,
                    "回复中不能含有第三方平台联系方式等相关敏感词，请修改！",
                    "我知道了",
                    null);
        }
        filterWordDlg.show();
    }

    private boolean containFilterWords(String contentStr) {
        String s = contentStr.trim();
        s = s.replaceAll(" ", "");

        for (String words : WSTips.ARRAY_CONTACT_FILTER_WORDS) {
            if (!TextUtils.isEmpty(s) && s.contains(words)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            CommonUtil.unSubscribeSubs(postCommentSub);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(postCommentSub);
    }
}