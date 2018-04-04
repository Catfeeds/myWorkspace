package com.hunliji.hljquestionanswer.utils;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.PostPraiseBody;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.AnimUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.AnswerComment;
import com.hunliji.hljquestionanswer.models.PostPraiseIdBody;

import rx.Observable;

/**
 * Created by mo_yu on 2016/5/10.关注和喜欢工具类
 */
public class QuestionAnswerTogglesUtil {

    private static HljHttpSubscriber praiseSubscriber;


    /**
     * 回答评论点赞
     *
     * @param activity
     * @param praiseView
     * @param imgThumb
     * @param praiseCount
     * @param tvAdd
     * @param item
     */
    public static void onAnswerCommentPraise(
            final Activity activity,
            final LinearLayout praiseView,
            final ImageView imgThumb,
            final TextView praiseCount,
            final TextView tvAdd,
            final AnswerComment item) {
        if (AuthUtil.loginBindCheck(activity)) {
            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            final int red = ContextCompat.getColor(activity, R.color.colorPrimary);
            final int gray = ContextCompat.getColor(activity, R.color.colorGray);
            if (item.isLike()) {
                imgThumb.setImageResource(R.mipmap.icon_praise_gray_40_40);
                praiseCount.setTextColor(gray);
                item.setLike(false);
                item.setLikesCount(item.getLikesCount() - 1);
            } else {
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                imgThumb.setImageResource(R.mipmap.icon_praise_primary_40_40);
                praiseCount.setTextColor(red);
                item.setLike(true);
                item.setLikesCount(item.getLikesCount() + 1);
            }
            praiseCount.setText(String.valueOf(item.getLikesCount()));

            PostPraiseBody body = new PostPraiseBody();
            body.setId(item.getId());
            body.setEntityType("CommunityComment");
            body.setType(5);
            praiseSubscriber = HljHttpSubscriber.buildSubscriber(activity)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showCustomToast(activity,
                                    item.isLike() ? R.string.msg_success_to_praise___cm : R
                                            .string.msg_success_to_un_praise___cm);
                            praiseView.setClickable(true);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            ToastUtil.showCustomToast(activity,
                                    item.isLike() ? R.string.msg_fail_to_praise_post___cm : R
                                            .string.msg_fail_to_cancel_praise_post___cm);
                            // 失败的话变回原样
                            if (item.isLike()) {
                                imgThumb.setImageResource(R.mipmap.icon_praise_gray_40_40);
                                praiseCount.setTextColor(gray);
                                item.setLike(false);
                                item.setLikesCount(item.getLikesCount() - 1);
                            } else {
                                imgThumb.setImageResource(R.mipmap.icon_praise_primary_40_40);
                                praiseCount.setTextColor(red);
                                item.setLike(true);
                                item.setLikesCount(item.getLikesCount() + 1);
                            }
                            praiseCount.setText(String.valueOf(item.getLikesCount()));
                            praiseView.setClickable(true);
                        }
                    })
                    .build();
            CommonApi.postPraiseObb(body)
                    .subscribe(praiseSubscriber);
        }
    }

    /**
     * 点赞/取消点赞 回答
     */
    public static void onAnswerPraise(
            final Activity activity,
            Object item,
            final CheckableLinearButton praiseView,
            final ImageView imgThumb,
            final TextView praiseCount,
            final TextView tvAdd) {
        final Answer answer = (Answer) item;
        if (AuthUtil.loginBindCheck(activity)) {
            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            if (answer.getLikeType() == 1) {
                // 已经赞过,取消点赞
                answer.setLikeType(0);
                answer.setUpCount(answer.getUpCount() - 1);
            } else {
                // 没有赞过,变为赞
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                answer.setLikeType(1);
                answer.setUpCount(answer.getUpCount() + 1);
            }
            praiseView.setChecked(answer.getLikeType() == 1);
            praiseCount.setText(answer.getUpCount() == 0 ? "赞" : String.valueOf(answer.getUpCount
                    ()));

            PostPraiseIdBody body = new PostPraiseIdBody();
            body.setId(answer.getId());
            body.setValue(answer.getLikeType());
            Observable observable = QuestionAnswerApi.postPraiseAnswerObb(body);
            praiseSubscriber = HljHttpSubscriber.buildSubscriber(activity)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showCustomToast(activity,
                                    answer.getLikeType() == 1 ? R.string
                                            .msg_success_to_praise___cm : R.string
                                            .msg_success_to_un_praise___cm);
                            praiseView.setClickable(true);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            ToastUtil.showCustomToast(activity,
                                    answer.getLikeType() == 1 ? R.string
                                            .msg_fail_to_praise_post___cm : R.string
                                            .msg_fail_to_cancel_praise_post___cm);
                            // 失败的话变回原样
                            if (answer.getLikeType() == 1) {
                                // 之前已经赞过,现在取消
                                answer.setLikeType(0);
                                answer.setUpCount(answer.getUpCount() - 1);
                            } else {
                                // 没有赞过,变为赞
                                answer.setLikeType(1);
                                answer.setUpCount(answer.getUpCount() + 1);
                            }
                            praiseView.setChecked(answer.getLikeType() == 1 ? true : false);
                            praiseCount.setText(answer.getUpCount() == 0 ? "赞" : String.valueOf(
                                    answer.getUpCount()));
                            praiseView.setClickable(true);
                        }
                    })
                    .build();
            observable.subscribe(praiseSubscriber);
        }
    }

    public static void destroySubscriber() {
        if (praiseSubscriber != null && !praiseSubscriber.isUnsubscribed()) {
            praiseSubscriber.unsubscribe();
        }
    }
}
