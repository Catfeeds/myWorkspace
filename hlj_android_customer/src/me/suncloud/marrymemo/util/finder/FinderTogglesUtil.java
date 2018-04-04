package me.suncloud.marrymemo.util.finder;

import android.app.Activity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.PostCollectBody;
import com.hunliji.hljcommonlibrary.models.PostPraiseBody;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.finder.EntityComment;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by chen_bin on 2016/5/10.发现页操作类，包含点赞，收藏等。
 */
public class FinderTogglesUtil {
    private static FinderTogglesUtil INSTANCE;

    private FinderTogglesUtil() {}

    public static FinderTogglesUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FinderTogglesUtil();
        }
        return INSTANCE;
    }

    /**
     * 专题详情点赞
     *
     * @param activity
     * @param topic
     * @param webView
     * @param checkPraised
     * @param imgPraise
     * @param tvPraiseCount
     * @param tvPraiseAdd
     * @param praiseSub
     */
    public HljHttpSubscriber onSubPagePraise(
            final Activity activity,
            final TopicUrl topic,
            final WebView webView,
            final CheckableLinearLayout checkPraised,
            final ImageView imgPraise,
            final TextView tvPraiseCount,
            final TextView tvPraiseAdd,
            HljHttpSubscriber praiseSub) {
        if (!Util.loginBindChecked(activity, Constants.Login.PRAISE_LOGIN)) {
            return praiseSub;
        }
        checkPraised.setClickable(false);
        if (topic.isPraised()) {
            topic.setPraised(false);
            topic.setPraiseCount(topic.getPraiseCount() - 1);
            checkPraised.setChecked(false);
        } else {
            AnimUtil.pulseAnimate(imgPraise);
            AnimUtil.zoomInUpAnimate(tvPraiseAdd);
            topic.setPraised(true);
            topic.setPraiseCount(topic.getPraiseCount() + 1);
            checkPraised.setChecked(true);
        }
        tvPraiseCount.setText(topic.getPraiseCount() > 0 ? activity.getString(R.string
                .label_praise) + "·" + topic.getPraiseCount() : activity.getString(
                R.string.label_praise));
        PostPraiseBody body = new PostPraiseBody();
        body.setEntityType("SubPage");
        body.setId(topic.getId());
        body.setType(3);
        praiseSub = HljHttpSubscriber.buildSubscriber(activity)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        checkPraised.setClickable(true);
                        if (topic.isPraised()) {
                            ToastUtil.showCustomToast(activity,
                                    R.string.msg_success_to_praise___cm);
                            webView.loadUrl("javascript:praise()");
                        } else {
                            ToastUtil.showCustomToast(activity,
                                    R.string.msg_success_to_un_praise___cm);
                            webView.loadUrl("javascript:unpraise()");
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        checkPraised.setClickable(true);
                        if (topic.isPraised()) {
                            ToastUtil.showToast(activity,
                                    null,
                                    R.string.msg_fail_to_praise_post___cm);
                            topic.setPraised(false);
                            topic.setPraiseCount(topic.getPraiseCount() - 1);
                            checkPraised.setChecked(false);
                        } else {
                            ToastUtil.showToast(activity,
                                    null,
                                    R.string.msg_fail_to_cancel_praise_post___cm);
                            topic.setPraised(true);
                            topic.setPraiseCount(topic.getPraiseCount() + 1);
                            checkPraised.setChecked(true);
                        }
                        tvPraiseCount.setText(topic.getPraiseCount() > 0 ? activity.getString(R
                                .string.label_praise) + "·" + topic.getPraiseCount() : activity
                                .getString(
                                R.string.label_praise));
                    }
                })
                .setDataNullable(true)
                .build();
        CommonApi.postPraiseObb(body)
                .subscribe(praiseSub);
        return praiseSub;
    }

    /**
     * 专题详情收藏
     *
     * @param activity
     * @param topic
     * @param checkCollected
     * @param tvCollect
     * @param collectSub
     */
    public HljHttpSubscriber onSubPageCollect(
            final Activity activity,
            final TopicUrl topic,
            final CheckableLinearLayout checkCollected,
            final TextView tvCollect,
            HljHttpSubscriber collectSub) {
        if (!AuthUtil.loginBindCheck(activity)) {
            return collectSub;
        }
        checkCollected.setClickable(false);
        if (topic.isCollected()) {
            topic.setCollected(false);
            checkCollected.setChecked(false);
            tvCollect.setText(R.string.label_collect_answer___cm);
        } else {
            topic.setCollected(true);
            checkCollected.setChecked(true);
            tvCollect.setText(R.string.label_collected___cm);
        }
        PostCollectBody body = new PostCollectBody();
        body.setFollowableType("SubPage");
        body.setId(topic.getId());
        collectSub = HljHttpSubscriber.buildSubscriber(activity)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        ToastUtil.showCustomToast(activity,
                                topic.isCollected() ? R.string.msg_success_to_collect___cm : R
                                        .string.msg_success_to_un_collect___cm);
                        checkCollected.setClickable(true);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        checkCollected.setClickable(true);
                        if (topic.isCollected()) {
                            ToastUtil.showToast(activity, null, R.string.msg_fail_to_collect___cm);
                            topic.setCollected(false);
                            checkCollected.setChecked(false);
                        } else {
                            ToastUtil.showToast(activity,
                                    null,
                                    R.string.msg_fail_to_cancel_collect___cm);
                            topic.setCollected(true);
                            checkCollected.setChecked(true);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        CommonApi.postCollectObb(body, !topic.isCollected())
                .subscribe(collectSub);
        return collectSub;
    }

    /**
     * 专题评论列表点赞
     *
     * @param activity
     * @param comment
     * @param checkPraised
     * @param imgPraise
     * @param tvPraiseCount
     * @param tvPraiseAdd
     * @param praiseSub
     */
    public HljHttpSubscriber onSubPageCommentPraise(
            final Activity activity,
            final EntityComment comment,
            final CheckableLinearLayout checkPraised,
            final ImageView imgPraise,
            final TextView tvPraiseCount,
            final TextView tvPraiseAdd,
            HljHttpSubscriber praiseSub) {
        if (!AuthUtil.loginBindCheck(activity)) {
            return praiseSub;
        }
        checkPraised.setClickable(false);
        if (comment.isLike()) {
            comment.setLike(false);
            comment.setLikesCount(comment.getLikesCount() - 1);
            checkPraised.setChecked(false);
        } else {
            comment.setLike(true);
            comment.setLikesCount(comment.getLikesCount() + 1);
            AnimUtil.pulseAnimate(imgPraise);
            AnimUtil.zoomInUpAnimate(tvPraiseAdd);
            checkPraised.setChecked(true);
        }
        tvPraiseCount.setText(String.valueOf(comment.getLikesCount()));
        PostPraiseBody body = new PostPraiseBody();
        body.setEntityType("CommunityComment");
        body.setId(comment.getId());
        body.setType(3);
        praiseSub = HljHttpSubscriber.buildSubscriber(activity)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        ToastUtil.showCustomToast(activity,
                                comment.isLike() ? R.string.msg_success_to_praise___cm : R.string
                                        .msg_success_to_un_praise___cm);
                        checkPraised.setClickable(true);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        checkPraised.setClickable(true);
                        if (comment.isLike()) {
                            ToastUtil.showToast(activity,
                                    null,
                                    R.string.msg_fail_to_praise_post___cm);
                            comment.setLike(false);
                            comment.setLikesCount(comment.getLikesCount() - 1);
                            checkPraised.setChecked(false);
                        } else {
                            ToastUtil.showToast(activity,
                                    null,
                                    R.string.msg_fail_to_cancel_praise_post___cm);
                            comment.setLike(true);
                            comment.setLikesCount(comment.getLikesCount() + 1);
                            checkPraised.setChecked(true);
                        }
                        tvPraiseCount.setText(String.valueOf(comment.getLikesCount()));
                    }
                })
                .setDataNullable(true)
                .build();
        CommonApi.postPraiseObb(body)
                .subscribe(praiseSub);
        return praiseSub;
    }
}