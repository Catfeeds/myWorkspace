package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import org.joda.time.DateTime;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.ShareUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/2/10.
 */

public class CardV2ShareSpeechFragment extends DialogFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_cover)
    ImageView ivCover;
    @BindView(R.id.et_speech)
    EditText etSpeech;

    private String type;
    private String coverPath;
    private String shareLink;
    private ShareUtil shareUtil;

    private Unbinder unbinder;

    /**
     * @param card 请帖信息
     * @param type 分享途径
     * @return
     */
    public static CardV2ShareSpeechFragment newInstance(
            CardV2 card, String type, String cover, String shareLink) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("card", card);
        bundle.putString("type", type);
        bundle.putString("cover", cover);
        bundle.putString("share_link", shareLink);
        CardV2ShareSpeechFragment fragment = new CardV2ShareSpeechFragment();
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
        View rootView = inflater.inflate(R.layout.dialog_fragment_edit_speech, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
        unbinder = ButterKnife.bind(this, rootView);
        etSpeech.addTextChangedListener(new TextCountWatcher(etSpeech, 36));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            //键盘方式
            getDialog().setCanceledOnTouchOutside(true);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
        CardV2 card = null;
        if (getArguments() != null) {
            card = (CardV2) getArguments().getSerializable("card");
            type = getArguments().getString("type");
            coverPath = getArguments().getString("cover");
            shareLink = getArguments().getString("share_link");
        }
        if (card == null || TextUtils.isEmpty(type) || TextUtils.isEmpty(coverPath) || TextUtils
                .isEmpty(
                shareLink)) {
            dismiss();
            return;
        }
        String title = getString(R.string.wedding_share_title,
                card.getGroomName(),
                card.getBrideName());
        String speech = getContext().getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                .getString("speech",
                        new DateTime(card.getTime()).toString(getString(R.string
                                .wedding_share_description2)));

        shareUtil = new ShareUtil(getContext(), shareLink, title, speech, null, null, null);
        tvTitle.setText(title);
        etSpeech.setText(speech);
        Glide.with(this)
                .load(ImagePath.buildPath(coverPath)
                        .width(CommonUtil.dp2px(getContext(), 68))
                        .cropPath())
                .into(ivCover);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.btn_cancel)
    public void onBackPressed() {
        dismiss();
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirm() {
        String speech = etSpeech.getText()
                .toString();
        if (TextUtils.isEmpty(speech)) {
            ToastUtil.showToast(getContext(), null, R.string.msg_speech_empty_err);
            return;
        }
        getContext().getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
                .putString("speech", speech)
                .apply();
        shareUtil.setDescription(speech);
        switch (type) {
            case "Timeline":
                //朋友圈
                shareUtil.setNewTitle(speech);
                shareUtil.shareToPengYou(coverPath);
                break;
            case "Session":
                //微信好友
                shareUtil.shareToWeixin(coverPath);
                break;
            case "QQ":
                final WeakReference<Activity> activityWeakReference = new WeakReference<Activity>(
                        getActivity());
                Observable.just(coverPath)
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String path) {
                                if (path.startsWith("http://") || path.startsWith("https://")) {
                                    return path;
                                }
                                String thumbPath = Environment.getExternalStorageDirectory() +
                                        File.separator + "Wedding Invitation.jpg";
                                FileUtil.copyFile(coverPath, thumbPath);
                                return thumbPath;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(String thumbPath) {
                                Activity activity = activityWeakReference.get();
                                if (activity == null || activity.isFinishing()) {
                                    return;
                                }
                                Bundle params = new Bundle();
                                params.putString(QQShare.SHARE_TO_QQ_TITLE, shareUtil.getTitle());
                                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
                                        shareUtil.getWebPath());
                                params.putString(QQShare.SHARE_TO_QQ_SUMMARY,
                                        shareUtil.getDescription());
                                if (thumbPath.startsWith("http://") || thumbPath.startsWith(
                                        "https://")) {
                                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, thumbPath);
                                } else {
                                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,
                                            thumbPath);
                                }
                                params.putString(QQShare.SHARE_TO_QQ_APP_NAME,
                                        activity.getString(R.string.app_name));
                                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, 1);
                                Tencent.createInstance(Constants.QQKEY, activity)
                                        .shareToQQ(activity, params, null);
                            }
                        });
                break;
        }
        dismiss();
    }
}
