package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.merchant_feed.MerchantFeed;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljhttplibrary.utils.AuthUtil;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CommunityPost;
import me.suncloud.marrymemo.model.CommunityThread;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Twitter;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;

/**
 * Created by mo_yu on 2016/5/10.关注和喜欢工具类
 */
public class CommunityTogglesUtil {

    /**
     * 频道关注
     *
     * @param communityChannel
     * @param followView
     * @param unFollowView
     */
    public static void onCommunityChannelFollow(
            final Activity activity,
            long id,
            final CommunityChannel communityChannel,
            final View followView,
            final View unFollowView) {
        if (Util.loginBindChecked(activity)) {
            if(followView != null){
                followView.setEnabled(false);
            }
            if(unFollowView != null){
                unFollowView.setEnabled(false);
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new NewHttpPostTask(activity, new OnHttpRequestListener() {
                @Override
                public void onRequestFailed(Object obj) {
                    if(followView != null){
                        followView.setEnabled(true);
                    }
                    if(unFollowView != null){
                        unFollowView.setEnabled(true);
                    }
                }

                @Override
                public void onRequestCompleted(Object obj) {
                    if(followView != null){
                        followView.setEnabled(true);
                    }
                    if(unFollowView != null){
                        unFollowView.setEnabled(true);
                    }
                    JSONObject object = (JSONObject) obj;
                    ReturnStatus status = new ReturnStatus(object.optJSONObject("status"));
                    if (status.getRetCode() == 0) {
                        if (communityChannel.isFollowed()) {
                            if(followView !=null){
                                followView.setVisibility(View.VISIBLE);
                            }
                            if(unFollowView != null){
                                unFollowView.setVisibility(View.GONE);
                            }
                            Util.showToast(R.string.hint_discollect_complete2, activity);
                            communityChannel.setFollowed(false);
                        } else {
                            if(followView != null){
                                followView.setVisibility(View.GONE);
                            }
                            if(unFollowView != null){
                                unFollowView.setVisibility(View.VISIBLE);
                            }
                            Util.showToast(R.string.hint_collect_complete2, activity);
                            communityChannel.setFollowed(true);
                        }
                        Intent intent = activity.getIntent();
                        activity.setResult(activity.RESULT_OK, intent);
                        MessageEvent messageEvent = new MessageEvent(MessageEvent.EventType
                                .COMMUNITY_CHANNEL_FOLLOW_FLAG,
                                status);
                        EventBus.getDefault()
                                .post(messageEvent);
                    }
                }
            }).execute(Constants.getAbsUrl(communityChannel.isFollowed() ? Constants.HttpPath
                            .COMMUNITY_SETUP_UNFOLLOW_URL : Constants.HttpPath
                            .COMMUNITY_SETUP_FOLLOW_URL),
                    jsonObject.toString());
            //            }
        }
    }


    /**
     * 话题点赞
     * 针对新的model修改的老方法
     *
     * @param activity
     * @param praiseView
     * @param imgThumb
     * @param praiseCount
     * @param tvAdd
     * @param communityThread
     */
    public static void onNewCommunityThreadListPraise(
            final Activity activity,
            final CheckableLinearButton praiseView,
            final ImageView imgThumb,
            final TextView praiseCount,
            final TextView tvAdd,
            final com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread
                    communityThread) {
        if (Util.loginBindChecked(activity)) {
            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            if (communityThread.isPraised()) {
                // 之前已经赞过,现在取消
                communityThread.setPraised(false);
                communityThread.setPraisedSum(communityThread.getPraisedSum() - 1);
            } else {
                // 没有赞过,变为赞
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                communityThread.setPraised(true);
                communityThread.setPraisedSum(communityThread.getPraisedSum() + 1);
            }
            praiseView.setChecked(communityThread.isPraised());
            praiseCount.setText(communityThread.getPraisedSum() == 0 ? "赞" : String.valueOf(
                    communityThread.getPraisedSum()));

            // 提交请求
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("post_id",
                        communityThread.getPost()
                                .getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            new StatusHttpPostTask(activity, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    JSONObject dataObject = (JSONObject) object;
                    if (dataObject != null) {
                        boolean isPraise = dataObject.optInt("belong_praise") > 0;
                        communityThread.setPraised(isPraise);
                        Util.showToast(isPraise ? R.string.hint_praised_complete : R.string
                                        .hint_dispraised_complete,
                                activity);
                        // 成功,什么都不用处理
                    }
                    praiseView.setClickable(true);
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    Util.postFailToast(activity,
                            returnStatus,
                            R.string.msg_fail_to_praise_post,
                            network);
                    // 失败的话变回原样
                    if (communityThread.isPraised()) {
                        // 之前已经赞过,现在取消
                        communityThread.setPraised(false);
                        communityThread.setPraisedSum(communityThread.getPraisedSum() - 1);
                    } else {
                        // 没有赞过,变为赞
                        communityThread.setPraised(true);
                        communityThread.setPraisedSum(communityThread.getPraisedSum() + 1);
                    }
                    praiseView.setChecked(communityThread.isPraised());
                    praiseCount.setText(communityThread.getPraisedSum() == 0 ? "赞" : String.valueOf(
                            communityThread.getPraisedSum()));

                    praiseView.setClickable(true);
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_PRAISE_URL),
                    jsonObject.toString());
        } else {
            praiseView.setChecked(!praiseView.isChecked());
        }
    }

    /**
     * 话题点赞
     *
     * @param activity
     * @param praiseView
     * @param imgThumb
     * @param praiseCount
     * @param tvAdd
     * @param communityThread
     */
    public static void onCommunityThreadPraise(
            final Activity activity,
            final CheckableLinearLayoutButton praiseView,
            final ImageView imgThumb,
            final TextView praiseCount,
            final TextView tvAdd,
            final CommunityThread communityThread,
            @Nullable final OnFinishedListener onFinishedListener) {
        if (Util.loginBindChecked(activity)) {
            final User user = Session.getInstance()
                    .getCurrentUser(activity);

            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            if (communityThread.isPraised()) {
                // 之前已经赞过,现在取消
                communityThread.setPraised(false);
                communityThread.setPraisedSum(communityThread.getPraisedSum() - 1);
                communityThread.removePraisedUser(user.getId());
            } else {
                // 没有赞过,变为赞
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                communityThread.setPraised(true);
                communityThread.setPraisedSum(communityThread.getPraisedSum() + 1);
                communityThread.pushedPraisedUser(user);
            }
            praiseView.setChecked(communityThread.isPraised());
            praiseCount.setText(String.valueOf(communityThread.getPraisedSum()));

            // 提交请求
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("post_id",
                        communityThread.getPost()
                                .getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            new StatusHttpPostTask(activity, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    JSONObject dataObject = (JSONObject) object;
                    if (dataObject != null) {
                        boolean isPraise = dataObject.optInt("belong_praise") > 0;
                        communityThread.setPraised(isPraise);
                        Util.showToast(isPraise ? R.string.hint_praised_complete : R.string
                                        .hint_dispraised_complete,
                                activity);
                        // 成功,什么都不用处理
                        if (onFinishedListener != null) {
                            onFinishedListener.onFinished();
                        }
                    }
                    praiseView.setClickable(true);
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    Util.postFailToast(activity,
                            returnStatus,
                            R.string.msg_fail_to_praise_post,
                            network);
                    // 失败的话变回原样
                    if (communityThread.isPraised()) {
                        // 之前已经赞过,现在取消
                        communityThread.setPraised(false);
                        communityThread.setPraisedSum(communityThread.getPraisedSum() - 1);
                        communityThread.removePraisedUser(user.getId());
                    } else {
                        // 没有赞过,变为赞
                        communityThread.setPraised(true);
                        communityThread.setPraisedSum(communityThread.getPraisedSum() + 1);
                        communityThread.pushedPraisedUser(user);
                    }
                    praiseView.setChecked(communityThread.isPraised());
                    praiseCount.setText(communityThread.getPraisedSum() == 0 ? "赞" : String.valueOf(
                            communityThread.getPraisedSum()));

                    praiseView.setClickable(true);
                    if (onFinishedListener != null) {
                        onFinishedListener.onFinished();
                    }
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_PRAISE_URL),
                    jsonObject.toString());
        } else {
            praiseView.setChecked(!praiseView.isChecked());
        }
    }

    /**
     * 话题点赞
     *
     * @param activity
     * @param praiseView
     * @param imgThumb
     * @param praiseCount
     * @param tvAdd
     * @param communityThread
     */
    public static void onNewCommunityThreadPraise(
            final Activity activity,
            final CheckableLinearLayout praiseView,
            final ImageView imgThumb,
            final TextView praiseCount,
            final TextView tvAdd,
            final com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread
                    communityThread,
            @Nullable final OnFinishedListener onFinishedListener) {
        if (Util.loginBindChecked(activity)) {
            final User user = Session.getInstance()
                    .getCurrentUser(activity);
            final CommunityAuthor author = new CommunityAuthor();
            author.setId(user.getId());
            author.setAvatar(user.getAvatar());
            author.setName(user.getNick());

            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            if (communityThread.isPraised()) {
                // 之前已经赞过,现在取消
                communityThread.setPraised(false);
                communityThread.setPraisedSum(communityThread.getPraisedSum() - 1);
                communityThread.removePraisedUser(user.getId());
            } else {
                // 没有赞过,变为赞
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                communityThread.setPraised(true);
                communityThread.setPraisedSum(communityThread.getPraisedSum() + 1);

                communityThread.pushedPraisedUser(author);
            }
            praiseView.setChecked(communityThread.isPraised());
            praiseCount.setText(String.valueOf(communityThread.getPraisedSum()));

            // 提交请求
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("post_id",
                        communityThread.getPost()
                                .getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            new StatusHttpPostTask(activity, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    JSONObject dataObject = (JSONObject) object;
                    if (dataObject != null) {
                        boolean isPraise = dataObject.optInt("belong_praise") > 0;
                        communityThread.setPraised(isPraise);
                        Util.showToast(isPraise ? R.string.hint_praised_complete : R.string
                                        .hint_dispraised_complete,
                                activity);
                        // 成功,什么都不用处理
                        if (onFinishedListener != null) {
                            onFinishedListener.onFinished();
                        }
                    }
                    praiseView.setClickable(true);
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    Util.postFailToast(activity,
                            returnStatus,
                            R.string.msg_fail_to_praise_post,
                            network);
                    // 失败的话变回原样
                    if (communityThread.isPraised()) {
                        // 之前已经赞过,现在取消
                        communityThread.setPraised(false);
                        communityThread.setPraisedSum(communityThread.getPraisedSum() - 1);
                        communityThread.removePraisedUser(user.getId());
                    } else {
                        // 没有赞过,变为赞
                        communityThread.setPraised(true);
                        communityThread.setPraisedSum(communityThread.getPraisedSum() + 1);
                        communityThread.pushedPraisedUser(author);
                    }
                    praiseView.setChecked(communityThread.isPraised());
                    praiseCount.setText(communityThread.getPraisedSum() == 0 ? "赞" : String.valueOf(
                            communityThread.getPraisedSum()));

                    praiseView.setClickable(true);
                    if (onFinishedListener != null) {
                        onFinishedListener.onFinished();
                    }
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_PRAISE_URL),
                    jsonObject.toString());
        } else {
            praiseView.setChecked(!praiseView.isChecked());
        }
    }

    public static void onCommunityPostPraise(
            final Activity activity,
            final CheckableLinearLayoutButton praiseView,
            final ImageView imgThumb,
            final TextView praiseCount,
            final TextView tvAdd,
            final com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost post) {
        if (Util.loginBindChecked(activity)) {
            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            if (post.isPraised()) {
                // 之前已经赞过,现在取消
                post.setPraised(false);
                post.setPraisedCount(post.getPraisedCount() - 1);
            } else {
                // 没有赞过,变为赞
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                post.setPraised(true);
                post.setPraisedCount(post.getPraisedCount() + 1);
            }
            praiseView.setChecked(post.isPraised());
            praiseCount.setText(post.getPraisedCount() > 0 ? String.valueOf(post.getPraisedCount
                    ()) : "赞");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("post_id", post.getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            new StatusHttpPostTask(activity, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    JSONObject dataObject = (JSONObject) object;
                    if (dataObject != null) {
                        boolean isPraise = dataObject.optInt("belong_praise") > 0;
                        post.setPraised(isPraise);
                        Util.showToast(isPraise ? R.string.hint_praised_complete : R.string
                                        .hint_dispraised_complete,
                                activity);
                        // 成功,什么都不用处理
                        praiseView.setClickable(true);
                    }
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    Util.postFailToast(activity,
                            returnStatus,
                            R.string.msg_fail_to_praise_post,
                            network);
                    // 失败的话变回原样
                    if (post.isPraised()) {
                        // 之前已经赞过,现在取消
                        post.setPraised(false);
                        post.setPraisedCount(post.getPraisedCount() - 1);
                    } else {
                        // 没有赞过,变为赞
                        post.setPraised(true);
                        post.setPraisedCount(post.getPraisedCount() + 1);
                    }
                    praiseView.setChecked(post.isPraised());
                    praiseCount.setText(post.getPraisedCount() > 0 ? String.valueOf(post
                            .getPraisedCount()) : "赞");

                    praiseView.setClickable(true);
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_PRAISE_URL),
                    jsonObject.toString());
        } else {
            praiseView.setChecked(!praiseView.isChecked());
        }
    }

    /**
     * 商家动态点赞
     *
     * @param activity
     * @param praiseView
     * @param imgThumb
     * @param praiseCount
     * @param tvAdd
     * @param merchantFeed
     */
    public static void onMerchantFeedPraise(
            final Activity activity,
            final CheckableLinearLayout praiseView,
            final ImageView imgThumb,
            final TextView praiseCount,
            final TextView tvAdd,
            final MerchantFeed merchantFeed) {
        if (Util.loginBindChecked(activity)) {
            String url;
            if (merchantFeed.isLike()) {
                url = Constants.HttpPath.COMMUNITY_MERCHANTFEED_UNLIKE_URL;
            } else {
                url = Constants.HttpPath.COMMUNITY_MERCHANTFEED_LIKE_URL;
            }
            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            if (merchantFeed.isLike()) {
                // 之前已经赞过,现在取消
                merchantFeed.setLike(false);
                merchantFeed.setLikesCount(merchantFeed.getLikesCount() - 1);
            } else {
                // 没有赞过,变为赞
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                merchantFeed.setLike(true);
                merchantFeed.setLikesCount(merchantFeed.getLikesCount() + 1);
            }
            praiseView.setChecked(merchantFeed.isLike());
            praiseCount.setText(merchantFeed.getLikesCount() == 0 ? "赞" : String.valueOf(
                    merchantFeed.getLikesCount()));

            // 提交请求
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", merchantFeed.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new NewHttpPostTask(activity, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object object) {
                    JSONObject dataObject = (JSONObject) object;
                    if (dataObject != null) {
                        ReturnStatus status = new ReturnStatus(dataObject.optJSONObject("status"));
                        if (status.getRetCode() == 0) {
                            boolean isPraise = merchantFeed.isLike();
                            merchantFeed.setLike(isPraise);
                            Util.showToast(isPraise ? R.string.hint_praised_complete : R.string
                                            .hint_dispraised_complete,
                                    activity);
                            // 成功,什么都不用处理
                        }
                        praiseView.setClickable(true);
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    Util.showToast(R.string.msg_fail_to_praise_post, activity);
                    // 失败的话变回原样
                    if (merchantFeed.isLike()) {
                        // 之前已经赞过,现在取消
                        merchantFeed.setLike(false);
                        merchantFeed.setLikesCount(merchantFeed.getLikesCount() - 1);
                    } else {
                        // 没有赞过,变为赞
                        merchantFeed.setLike(true);
                        merchantFeed.setLikesCount(merchantFeed.getLikesCount() + 1);
                    }
                    praiseView.setChecked(merchantFeed.isLike());
                    praiseCount.setText(merchantFeed.getLikesCount() == 0 ? "赞" : String.valueOf(
                            merchantFeed.getLikesCount()));
                    praiseView.setClickable(true);
                }
            }).execute(Constants.getAbsUrl(url), jsonObject.toString());
        } else {
            praiseView.setChecked(!praiseView.isChecked());
        }
    }


    /**
     * 商家动态点赞
     *
     * @param activity
     * @param praiseView
     * @param imgThumb
     * @param praiseCount
     * @param tvAdd
     */
    public static void onTwitterPraise(
            final Activity activity,
            final CheckableLinearLayoutButton praiseView,
            final ImageView imgThumb,
            final TextView praiseCount,
            final TextView tvAdd,
            final MerchantFeed feed) {
        if (Util.loginBindChecked(activity)) {
            String url;
            if (feed.isLike()) {
                url = Constants.HttpPath.COMMUNITY_MERCHANTFEED_UNLIKE_URL;
            } else {
                url = Constants.HttpPath.COMMUNITY_MERCHANTFEED_LIKE_URL;
            }
            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            if (feed.isLike()) {
                // 之前已经赞过,现在取消
                feed.setLike(false);
                feed.setLikesCount(feed.getLikesCount() - 1);
            } else {
                // 没有赞过,变为赞
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                feed.setLike(true);
                feed.setLikesCount(feed.getLikesCount() + 1);
            }
            praiseView.setChecked(feed.isLike());
            praiseCount.setText(feed.getLikesCount() == 0 ? "赞" : String.valueOf(feed
                    .getLikesCount()));

            // 提交请求
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", feed.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new NewHttpPostTask(activity, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object object) {
                    JSONObject dataObject = (JSONObject) object;
                    if (dataObject != null) {
                        ReturnStatus status = new ReturnStatus(dataObject.optJSONObject("status"));
                        if (status.getRetCode() == 0) {
                            boolean isPraise = feed.isLike();
                            feed.setLike(isPraise);
                            Util.showToast(isPraise ? R.string.hint_praised_complete : R.string
                                            .hint_dispraised_complete,
                                    activity);
                            // 成功,什么都不用处理
                        }
                        praiseView.setClickable(true);
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    Util.showToast(R.string.msg_fail_to_praise_post, activity);
                    // 失败的话变回原样
                    if (feed.isLike()) {
                        // 之前已经赞过,现在取消
                        feed.setLike(false);
                        feed.setLikesCount(feed.getLikesCount() - 1);
                    } else {
                        // 没有赞过,变为赞
                        feed.setLike(true);
                        feed.setLikesCount(feed.getLikesCount() + 1);
                    }
                    praiseView.setChecked(feed.isLike());
                    praiseCount.setText(feed.getLikesCount() == 0 ? "赞" : String.valueOf(feed
                            .getLikesCount()));
                    praiseView.setClickable(true);
                }
            }).execute(Constants.getAbsUrl(url), jsonObject.toString());
        } else {
            praiseView.setChecked(!praiseView.isChecked());
        }
    }

}
