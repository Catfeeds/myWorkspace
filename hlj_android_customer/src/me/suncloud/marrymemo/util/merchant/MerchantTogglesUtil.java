package me.suncloud.marrymemo.util.merchant;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.PostPraiseBody;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by chen_bin on 2016/5/10.商家操作类，包含点赞，收藏等。
 */
public class MerchantTogglesUtil {
    private static MerchantTogglesUtil INSTANCE;

    private MerchantTogglesUtil() {}

    public static MerchantTogglesUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MerchantTogglesUtil();
        }
        return INSTANCE;
    }

    /**
     * 评论点赞
     *
     * @param activity
     * @param checkPraised
     * @param imgPraise
     * @param comment
     * @param praiseSub
     * @param onFinishedListener
     */
    public HljHttpSubscriber onServiceOrderCommentPraise(
            final Activity activity,
            final ServiceComment comment,
            final CheckableLinearLayout checkPraised,
            final ImageView imgPraise,
            final TextView tvPraiseCount,
            HljHttpSubscriber praiseSub,
            final OnFinishedListener onFinishedListener) {
        if (!AuthUtil.loginBindCheck(activity)) {
            return praiseSub;
        }
        checkPraised.setEnabled(false);
        if (comment.isLike()) {
            comment.setLike(false);
            comment.setLikesCount(comment.getLikesCount() - 1);
            checkPraised.setChecked(false);
        } else {
            AnimUtil.pulseAnimate(imgPraise);
            comment.setLike(true);
            comment.setLikesCount(comment.getLikesCount() + 1);
            checkPraised.setChecked(true);
        }
        tvPraiseCount.setText(comment.getLikesCount() <= 0 ? activity.getString(R.string
                .label_be_of_use) : String.valueOf(
                comment.getLikesCount()));
        PostPraiseBody body = new PostPraiseBody();
        body.setEntityType("OrderComment");
        body.setId(comment.getId());
        body.setType(7);
        praiseSub = HljHttpSubscriber.buildSubscriber(activity)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object object) {
                        ToastUtil.showCustomToast(activity,
                                comment.isLike() ? R.string.msg_success_to_praise___cm : R.string
                                        .msg_success_to_un_praise___cm);
                        checkPraised.setEnabled(true);
                        if (onFinishedListener != null) {
                            onFinishedListener.onFinished();
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object object) {
                        checkPraised.setEnabled(true);
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
                        tvPraiseCount.setText(comment.getLikesCount() <= 0 ? activity.getString(R
                                .string.label_be_of_use) : String.valueOf(
                                comment.getLikesCount()));
                    }
                })
                .setDataNullable(true)
                .build();
        CommonApi.postPraiseObb(body)
                .subscribe(praiseSub);
        return praiseSub;
    }

    /**
     * 关注商家
     *
     * @param activity
     * @param checkCollected
     * @param tvCollect
     * @param merchant
     * @param collectSub
     */
    public HljHttpSubscriber onMerchantCollect(
            final Activity activity,
            final Merchant merchant,
            final CheckableLinearLayout checkCollected,
            final TextView tvCollect,
            HljHttpSubscriber collectSub) {
        if (!AuthUtil.loginBindCheck(activity)) {
            return collectSub;
        }
        checkCollected.setClickable(false);
        if (merchant.isCollected()) {
            merchant.setCollected(false);
            checkCollected.setChecked(false);
            tvCollect.setText(R.string.label_collect___cm);
        } else {
            merchant.setCollected(true);
            checkCollected.setChecked(true);
            tvCollect.setText(R.string.label_collected___cm);
        }
        collectSub = HljHttpSubscriber.buildSubscriber(activity)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object object) {
                        checkCollected.setClickable(true);
                        if (merchant.isCollected()) {
                            if (Util.isNewFirstCollect(activity, 6)) {
                                Util.showFirstCollectNoticeDialog(activity, 6);
                            } else {
                                ToastUtil.showCustomToast(activity,
                                        R.string.msg_success_to_collect___cm);
                            }
                        } else {
                            ToastUtil.showCustomToast(activity,
                                    R.string.msg_success_to_un_collect___cm);
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object object) {
                        checkCollected.setClickable(true);
                        if (merchant.isCollected()) {
                            ToastUtil.showToast(activity, null, R.string.msg_fail_to_collect___cm);
                            merchant.setCollected(false);
                            checkCollected.setChecked(false);
                            tvCollect.setText(R.string.label_collect___cm);
                        } else {
                            ToastUtil.showToast(activity,
                                    null,
                                    R.string.msg_fail_to_cancel_collect___cm);
                            merchant.setCollected(true);
                            checkCollected.setChecked(true);
                            tvCollect.setText(R.string.label_collected___cm);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        if (merchant.isCollected()) {
            CommonApi.deleteMerchantFollowObb(merchant.getId())
                    .subscribe(collectSub);
        } else {
            CommonApi.postMerchantFollowObb(merchant.getId())
                    .subscribe(collectSub);
        }
        return collectSub;
    }

}