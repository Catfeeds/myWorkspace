package com.hunliji.hljpushlibrary.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.BannerJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.widgets.QueueDialog;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljpushlibrary.R;
import com.hunliji.hljpushlibrary.models.PushData;
import com.hunliji.hljpushlibrary.models.activity.ActivityData;
import com.hunliji.hljpushlibrary.models.live.LiveData;
import com.hunliji.hljpushlibrary.models.notify.NotifyData;
import com.hunliji.hljpushlibrary.models.notify.NotifyTask;
import com.hunliji.hljpushlibrary.widgets.HljNotify;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by wangtao on 2017/12/1.
 */

public enum PushUtil {

    INSTANCE;

    private SoftReference<PushData> dataSoftReference;
    private WeakReference<Context> contextWeakReference;

    private Subscription saveTaskSubscription;

    public void newLive(LiveData live) {
        this.dataSoftReference = new SoftReference<>(new PushData(live));
    }

    public void newActivity(ActivityData activity) {
        this.dataSoftReference = new SoftReference<>(new PushData(activity));
    }

    public void newNotifyTask(Context context, NotifyData task) {
        this.dataSoftReference = new SoftReference<>(new PushData(task));

        saveNotifyDataToLocalFile(context);
    }

    public PushData getPushData(Context context) {
        if (dataSoftReference != null && dataSoftReference.get() != null) {
            PushData data = dataSoftReference.get();
            dataSoftReference.clear();
            if (!data.isExceed()) {
                return data;
            }
        }

        return getNotifyDataFromLocalFile(context);
    }

    /**
     * 将应用内弹窗存储到本地文件中以备后面使用
     *
     * @param context
     */
    public void saveNotifyDataToLocalFile(Context context) {
        contextWeakReference = new WeakReference<>(context);
        CommonUtil.unSubscribeSubs(saveTaskSubscription);
        saveTaskSubscription = Observable.timer(5, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (dataSoftReference == null || dataSoftReference.get() == null) {
                            return;
                        }
                        if (contextWeakReference == null || contextWeakReference.get() == null) {
                            return;
                        }
                        PushData data = dataSoftReference.get();
                        if (data.getNotifyData() != null) {

                            dataSoftReference.clear();
                            try {
                                FileOutputStream fileOutputStream = contextWeakReference.get()
                                        .openFileOutput(HljCommon.FileNames.NOTIFY_FILE,
                                                Context.MODE_PRIVATE);
                                if (fileOutputStream != null) {
                                    OutputStreamWriter out = new OutputStreamWriter
                                            (fileOutputStream);
                                    out.write(GsonUtil.getGsonInstance()
                                            .toJson(data.getNotifyData()));
                                    out.flush();
                                    out.close();
                                    fileOutputStream.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 获取本地文件中已存在的应用内通知
     *
     * @param context
     * @return
     */
    public PushData getNotifyDataFromLocalFile(Context context) {
        try {
            if (context.getFileStreamPath(HljCommon.FileNames.NOTIFY_FILE) != null && context
                    .getFileStreamPath(
                    HljCommon.FileNames.NOTIFY_FILE)
                    .exists()) {
                InputStream in = context.openFileInput(HljCommon.FileNames.NOTIFY_FILE);
                NotifyData task = null;
                try {
                    task = GsonUtil.getGsonInstance()
                            .fromJson(new InputStreamReader(in), NotifyData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                in.close();
                FileUtil.deleteFile(context.getFileStreamPath(HljCommon.FileNames.NOTIFY_FILE));
                if (task != null && !task.getTask()
                        .isExceed()) {
                    return new PushData(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void onClear(Context context) {
        CommonUtil.unSubscribeSubs(saveTaskSubscription);
        if (dataSoftReference != null) {
            dataSoftReference.clear();
        }
        FileUtil.deleteFile(context.getFileStreamPath(HljCommon.FileNames.NOTIFY_FILE));
    }

    /**
     * 顶部直播通知条
     *
     * @param context
     * @param liveData
     * @param clickListener
     */
    public static void showLiveOnView(
            Context context, LiveData liveData, final View.OnClickListener clickListener) {
        final LiveChannel liveChannel = liveData.getLiveChannel();
        View view = View.inflate(context, R.layout.common_notify_top_layout___push, null);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvContent.setVisibility(View.VISIBLE);
        tvTitle.setMaxLines(1);
        tvTitle.setText(liveChannel.getTitle());
        String countStr;
        String strColor = String.format("#%06X",
                0xFFFFFF & ContextCompat.getColor(context, R.color.colorPrimary));
        if (liveChannel.getType() == LiveChannel.TYPE_BARGIN) {
            countStr = context.getString(R.string.html_fmt_live_hint_count1___push,
                    strColor,
                    liveChannel.getWatch_count());
        } else {
            countStr = context.getString(R.string.html_fmt_live_hint_count2___push,
                    strColor,
                    liveChannel.getWatch_count());
        }
        tvContent.setText(Html.fromHtml(countStr));

        View clickView = view.findViewById(R.id.content_layout);

        HljVTTagger.buildTagger(clickView)
                .tagName(HljTaggerName.APP_PUSH)
                .dataId(liveData.getLogId())
                .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_LIVE_LOG)
                .hitTag();

        final HljNotify hljNotify = new HljNotify(context, view);
        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Live.LIVE_CHANNEL_ACTIVITY)
                        .withLong("id", liveChannel.getId())
                        .navigation(v.getContext());
                hljNotify.cancel();
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        });
        HljViewTracker.fireViewVisibleEvent(clickView);
        hljNotify.show();
    }

    /**
     * 顶部通知条
     *
     * @param context
     * @param notifyData
     * @param clickListener
     */
    public static void showNotifyView(
            Context context, NotifyData notifyData, final View.OnClickListener clickListener) {
        final NotifyTask notifyTask = notifyData.getTask();
        View view = View.inflate(context, R.layout.common_notify_top_layout___push, null);
        view.findViewById(R.id.tv_content)
                .setVisibility(View.GONE);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setMaxLines(2);
        tvTitle.setText(notifyTask.getTitle());
        View clickView = view.findViewById(R.id.content_layout);

        HljVTTagger.buildTagger(clickView)
                .tagName(HljTaggerName.APP_PUSH)
                .dataId(notifyData.getLogId())
                .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_TASK_LOG)
                .hitTag();

        final HljNotify hljNotify = new HljNotify(context, view);
        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hljNotify.cancel();
                BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                        .build(RouterPath.ServicePath.BANNER_JUMP)
                        .navigation(v.getContext());
                if (bannerJumpService != null) {
                    bannerJumpService.bannerJump(v.getContext(), notifyTask.getPoster(), null);
                }
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        });
        HljViewTracker.fireViewVisibleEvent(clickView);
        hljNotify.show();
    }

    /**
     * 金融超市显示poster的dialog，因为样式要统一，所以直接写在推送util里
     *
     * @param context
     * @param poster
     * @param confirmListener
     * @param cancelListener
     * @return
     */
    public static QueueDialog createNotifyPostDlg(
            final Context context,
            final Poster poster,
            final View.OnClickListener confirmListener,
            final View.OnClickListener cancelListener) {
        final QueueDialog dialog = new QueueDialog(context, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.common_notify_dialog___push);
        final ImageView ivCover = dialog.findViewById(R.id.iv_cover);
        int width = Math.round(CommonUtil.getDeviceSize(context).x * 60 / 75);
        int height = Math.round(width * 80 / 60);
        dialog.findViewById(R.id.layout)
                .getLayoutParams().width = width;
        ivCover.getLayoutParams().width = width;
        ivCover.getLayoutParams().height = height;
        Glide.with(context)
                .load(ImagePath.buildPath(poster.getPath())
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate())
                .into(ivCover);

        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                        .build(RouterPath.ServicePath.BANNER_JUMP)
                        .navigation(v.getContext());
                if (bannerJumpService != null) {
                    bannerJumpService.bannerJump(v.getContext(), poster, null);
                }
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        dialog.findViewById(R.id.btn_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (cancelListener != null) {
                            cancelListener.onClick(v);
                        }
                    }
                });

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = dialog.getWindow()
                    .getAttributes();
            lp.dimAmount = 0.8f;
            dialog.getWindow()
                    .setAttributes(lp);
            dialog.getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }

    /**
     * 首页通知弹窗
     *
     * @return
     */
    public static QueueDialog createNotifyPosterDlg(
            final Context context,
            NotifyData notifyData,
            final View.OnClickListener confirmListener,
            final View.OnClickListener cancelListener) {
        final NotifyTask notifyTask = notifyData.getTask();
        final QueueDialog dialog = new QueueDialog(context, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.common_notify_dialog___push);
        final ImageView ivCover = dialog.findViewById(R.id.iv_cover);
        int width = Math.round(CommonUtil.getDeviceSize(context).x * 60 / 75);
        int height = Math.round(width * 80 / 60);
        dialog.findViewById(R.id.layout)
                .getLayoutParams().width = width;
        ivCover.getLayoutParams().width = width;
        ivCover.getLayoutParams().height = height;
        Glide.with(context)
                .load(ImagePath.buildPath(notifyTask.getImagePath())
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate())
                .into(ivCover);

        HljVTTagger.buildTagger(ivCover)
                .tagName(HljTaggerName.APP_PUSH)
                .dataId(notifyData.getLogId())
                .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_TASK_LOG)
                .hitTag();

        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                        .build(RouterPath.ServicePath.BANNER_JUMP)
                        .navigation(v.getContext());
                if (bannerJumpService != null) {
                    bannerJumpService.bannerJump(v.getContext(), notifyTask.getPoster(), null);
                }
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        dialog.findViewById(R.id.btn_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (cancelListener != null) {
                            cancelListener.onClick(v);
                        }
                    }
                });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                HljViewTracker.fireViewVisibleEvent(ivCover);
            }
        });

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = dialog.getWindow()
                    .getAttributes();
            lp.dimAmount = 0.8f;
            dialog.getWindow()
                    .setAttributes(lp);
            dialog.getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }

    /**
     * 首页活动弹窗
     *
     * @return
     */
    public static QueueDialog createActivityDlg(
            final Context context,
            ActivityData activityData,
            final View.OnClickListener confirmListener,
            final View.OnClickListener cancelListener) {
        final EventInfo eventInfo = activityData.getFinderActivity();
        final QueueDialog dialog = new QueueDialog(context, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.common_notify_dialog___push);
        final ImageView ivCover = dialog.findViewById(R.id.iv_cover);
        int width = Math.round(CommonUtil.getDeviceSize(context).x * 60 / 75);
        int height = Math.round(width * 80 / 60);
        dialog.findViewById(R.id.layout)
                .getLayoutParams().width = width;
        ivCover.getLayoutParams().width = width;
        ivCover.getLayoutParams().height = height;
        Glide.with(context)
                .load(ImagePath.buildPath(eventInfo.getPopupImg())
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate())
                .into(ivCover);

        HljVTTagger.buildTagger(ivCover)
                .tagName(HljTaggerName.APP_PUSH)
                .dataId(activityData.getLogId())
                .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_POP_ACTIVITY)
                .hitTag();

        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (eventInfo.getId() > 0) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.EVENT_DETAIL_ACTIVITY)
                            .withLong("id", eventInfo.getId())
                            .navigation(v.getContext());
                }
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        dialog.findViewById(R.id.btn_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (cancelListener != null) {
                            cancelListener.onClick(v);
                        }
                    }
                });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                HljViewTracker.fireViewVisibleEvent(ivCover);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = dialog.getWindow()
                    .getAttributes();
            lp.dimAmount = 0.8f;
            dialog.getWindow()
                    .setAttributes(lp);
            dialog.getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }
}
