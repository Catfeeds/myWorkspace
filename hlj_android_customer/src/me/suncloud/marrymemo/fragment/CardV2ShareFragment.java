package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.activities.WeiboShareActivity;
import com.hunliji.hljtrackerlibrary.HljTracker;
import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.card.CardApi;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.card.ShareLink;
import me.suncloud.marrymemo.task.DrawPageTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
/**
 * Created by wangtao on 2017/4/1.
 */

public class CardV2ShareFragment extends DialogFragment {

    private Unbinder unbinder;
    private CardV2 card;
    private Subscription shareSubscription;
    private Subscription postSubscription;

    private static class ShareType {
        static final String TIME_LINE = "Timeline"; //朋友圈
        static final String SESSION = "Session"; //微信
        static final String QQ = "QQ"; //QQ
        static final String SMS = "SMS"; //短信
        static final String EMAIL = "Mail"; //邮件
        static final String WEIBO = "Weibo"; //微博
    }

    /**
     * @param card 请帖信息
     * @return
     */
    public static CardV2ShareFragment newInstance(CardV2 card) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("card", card);
        CardV2ShareFragment fragment = new CardV2ShareFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.bubble_dialog_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_card_share, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.dialog_anim_rise_style);
            getDialog().setCanceledOnTouchOutside(true);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
        if (getArguments() != null) {
            card = (CardV2) getArguments().getSerializable("card");
            if (card != null) {
                postSubscription = CardApi.postShareClick(card.getId())
                        .subscribe(new Subscriber() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Object o) {

                            }
                        });
            }
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(shareSubscription, postSubscription);
        super.onDestroyView();
    }

    @OnClick({R.id.share_pengyou, R.id.share_weixing, R.id.share_weibo, R.id.share_qq, R.id
            .share_email, R.id.share_sms, R.id.action_cancel})
    public void onViewClicked(View view) {
        if (card == null) {
            dismiss();
            return;
        }
        switch (view.getId()) {
            case R.id.share_pengyou:
            case R.id.share_weixing:
            case R.id.share_weibo:
            case R.id.share_qq:
            case R.id.share_email:
            case R.id.share_sms:
                initShareInfo(view.getId());
                break;
            case R.id.action_cancel:
                dismiss();
                break;
        }
    }

    private void initShareInfo(int shareType) {
        if (shareSubscription != null && !shareSubscription.isUnsubscribed()) {
            return;
        }
        shareSubscription = Observable.just(shareType)
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        switch (integer) {
                            case R.id.share_pengyou:
                                return ShareType.TIME_LINE;
                            case R.id.share_weixing:
                                return ShareType.SESSION;
                            case R.id.share_weibo:
                                return ShareType.WEIBO;
                            case R.id.share_qq:
                                return ShareType.QQ;
                            case R.id.share_email:
                                return ShareType.EMAIL;
                            case R.id.share_sms:
                                return ShareType.SMS;
                        }
                        throw new CardShareException("分享途径不支持");
                    }
                })
                .concatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String shareType) {
                        return Observable.just(shareType);
                    }
                })
                .concatMap(new Func1<String, Observable<ShareZip>>() {
                    @Override
                    public Observable<ShareZip> call(String shareType) {
                        return Observable.zip(Observable.just(shareType),
                                getShareCoverObb(shareType),
                                getShareLinkObb(),
                                new Func3<String, String, String, ShareZip>() {
                                    @Override
                                    public ShareZip call(
                                            String shareType, String coverPath, String shareLink) {
                                        return new ShareZip(shareType, coverPath, shareLink);
                                    }
                                });
                    }
                })
                .doOnNext(new Action1<ShareZip>() {
                    @Override
                    public void call(ShareZip shareZip) {
                        switch (shareZip.type) {
                            case ShareType.TIME_LINE:
                            case ShareType.QQ:
                            case ShareType.SESSION:
                                DialogFragment dialogFragment = CardV2ShareSpeechFragment
                                        .newInstance(
                                        card,
                                        shareZip.type,
                                        shareZip.coverPath,
                                        shareZip.shareLink);
                                dialogFragment.show(getFragmentManager(), "editSpeechFragment");
                                break;
                            case ShareType.SMS:
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("smsto:"));
                                intent.putExtra("sms_body", getMessage(shareZip.shareLink));
                                try {
                                    startActivity(intent);
                                } catch (Exception e) {
                                    throw new CardShareException(getContext().getString(R.string
                                            .unfind_msg));
                                }
                                break;
                        }
                    }
                })
                .concatMap(new Func1<ShareZip, Observable<String>>() {
                    @Override
                    public Observable<String> call(ShareZip shareZip) {
                        switch (shareZip.type) {
                            case ShareType.EMAIL:
                                return shareEmailObb(shareZip);
                            case ShareType.WEIBO:
                                return shareWeiboObb(shareZip);
                        }
                        return Observable.just(shareZip.type);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        dismissAllowingStateLoss();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                        .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                        .setOnNextListener(new SubscriberOnNextListener<String>() {
                            @Override
                            public void onNext(String shareType) {
                                new HljTracker.Builder(getContext()).eventableId(card.getId())
                                        .eventableType("CardV3")
                                        .action("share_click")
                                        .additional(shareType)
                                        .build()
                                        .add();
                            }
                        })
                        .build());
    }

    private String getMessage(String shareLink) {
        return getString(R.string.msg_send_content,
                (card.getTime() == null ? "" : Util.getCardTimeString(getContext(),
                        card.getTime())),
                card.getGroomName(),
                card.getBrideName(),
                shareLink);
    }

    private Observable<String> shareEmailObb(final ShareZip shareZip) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String thumbPath = Environment.getExternalStorageDirectory() + File.separator +
                        "Wedding Invitation.jpg";
                FileUtil.copyFile(shareZip.coverPath, thumbPath);
                subscriber.onNext(thumbPath);
                subscriber.onCompleted();
            }
        })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String thumbPath) {
                        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        ArrayList<Uri> imageUris = new ArrayList<>();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUris.add(FileProvider.getUriForFile(getContext(),
                                    getContext().getPackageName(),
                                    new File(thumbPath)));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            imageUris.add(Uri.fromFile(new File(thumbPath)));
                        }
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.msg_send_subject));
                        intent.putExtra(Intent.EXTRA_TEXT, getMessage(shareZip.shareLink));
                        intent.setType("application/octet-stream");
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            throw new CardShareException(getContext().getString(R.string
                                    .unfind_mail));
                        }
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return shareZip.type;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<String> shareWeiboObb(final ShareZip shareZip) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String thumbPath = Environment.getExternalStorageDirectory() + File.separator +
                        "Wedding Invitation.jpg";
                FileUtil.copyFile(shareZip.coverPath, thumbPath);
                subscriber.onNext(thumbPath);
                subscriber.onCompleted();
            }
        })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String thumbPath) {
                        Intent intent = new Intent(getContext(), WeiboShareActivity.class);
                        intent.putExtra(WeiboShareActivity.ASG_IMAGE, thumbPath);
                        intent.putExtra(WeiboShareActivity.ASG_CONTENT,
                                getString(R.string.wedding_share_description,
                                        card.getGroomName(),
                                        card.getBrideName()));
                        intent.putExtra(WeiboShareActivity.ASG_URL, shareZip.shareLink);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().overridePendingTransition(0, 0);
                        }
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return shareZip.type;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<String> getShareCoverObb(String shareType) {
        return Observable.just(shareType)
                .concatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String shareType) {
                        switch (shareType) {
                            case ShareType.SMS:
                                //短信分享不需要图片
                                return Observable.just("");
                            case ShareType.SESSION:
                            case ShareType.TIME_LINE:
                            case ShareType.QQ:
                                try {
                                    String path = card.getFrontPage()
                                            .getImages()
                                            .get(0)
                                            .getImagePath();
                                    if (!TextUtils.isEmpty(path)) {
                                        return Observable.just(path);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(final Subscriber<? super String> subscriber) {
                                if (card.getFrontPage() == null) {
                                    subscriber.onError(new CardShareException("请帖数据错误"));
                                }
                                String oKey = CardResourceUtil.getInstance()
                                        .getPageMapKey(getContext(),
                                                card.getFrontPage()
                                                        .getId());
                                String nKey = card.getFrontPage()
                                        .getCardPageKey();
                                if (!nKey.equals(oKey) && !JSONUtil.isEmpty(oKey)) {
                                    FileUtil.deleteFile(FileUtil.createPageFile(getContext(),
                                            oKey));
                                }
                                File file = FileUtil.createPageFile(getContext(), nKey);
                                if (file != null && file.exists() && file.length() > 0) {
                                    subscriber.onNext(file.getAbsolutePath());
                                    subscriber.onCompleted();
                                } else {
                                    new DrawPageTask(getContext(),
                                            card.getFrontPage(),
                                            new OnHttpRequestListener() {
                                                @Override
                                                public void onRequestCompleted(Object obj) {
                                                    subscriber.onNext((String) obj);
                                                    subscriber.onCompleted();
                                                }

                                                @Override
                                                public void onRequestFailed(Object obj) {
                                                    subscriber.onError(new CardShareException(
                                                            "生成缩略图失败"));
                                                }
                                            }).executeOnExecutor(Constants.THEADPOOL);
                                }
                            }
                        });
                    }
                });
    }

    private Observable<String> getShareLinkObb() {
        return CardApi.getShareLinkObb(card.getId())
                .flatMap(new Func1<ShareLink, Observable<String>>() {
                    @Override
                    public Observable<String> call(final ShareLink shareLink) {
                        if (!TextUtils.isEmpty(shareLink.getMsg())) {
                            return Observable.create(new Observable.OnSubscribe<String>() {
                                @Override
                                public void call(final Subscriber<? super String> subscriber) {
                                    DialogUtil.createDoubleButtonDialog(getContext(),
                                            shareLink.getMsg(),
                                            "继续发送",
                                            "取消发送",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    subscriber.onNext(shareLink.getLink());
                                                    subscriber.onCompleted();
                                                }
                                            },
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    subscriber.onError(new CardShareException
                                                            ("取消发送"));
                                                }
                                            })
                                            .show();
                                }
                            });
                        } else {
                            return Observable.just(shareLink.getLink());
                        }
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                });
    }


    private class CardShareException extends RuntimeException {


        CardShareException(
                String detailMessage) {
            super(detailMessage);
        }
    }

    private class ShareZip {
        String type;
        String coverPath;
        String shareLink;

        ShareZip(String type, String coverPath, String shareLink) {
            this.type = type;
            this.coverPath = coverPath;
            this.shareLink = shareLink;
        }
    }
}
