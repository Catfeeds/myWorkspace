package com.hunliji.hljlivelibrary.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljchatlibrary.views.widgets.ChatRecordView;
import com.hunliji.hljchatlibrary.views.widgets.SpeakView;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.Video;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljimagelibrary.views.activities.GifSupportImageChooserActivity;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.adapters.LiveImagesAdapter;
import com.hunliji.hljlivelibrary.models.LiveContent;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRxEvent;
import com.hunliji.hljvideolibrary.activities.BaseVideoTrimActivity;
import com.hunliji.hljvideolibrary.activities.VideoChooserActivity;
import com.hunliji.hljvideolibrary.activities.VideoPreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by luohanlin on 2017/11/23.
 */

@RuntimePermissions
public class HostLiveChannelFragment extends BaseLiveChannelFragment implements LiveImagesAdapter
        .OnImageRemoveListener, LiveImagesAdapter.OnAddImageListener {

    @BindView(R2.id.layout)
    RelativeLayout layout;
    @BindView(R2.id.live_status_holder)
    LinearLayout liveStatusHolder;
    @BindView(R2.id.stick_images_recycler)
    RecyclerView stickImagesRecycler;
    @BindView(R2.id.stick_images_holder)
    LinearLayout stickImagesHolder;
    @BindView(R2.id.tab_page_indicator)
    TabPageIndicator tabPageIndicator;
    @BindView(R2.id.tab_layout)
    FrameLayout tabLayout;
    @BindView(R2.id.appbar)
    AppBarLayout appbar;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R2.id.btn_share2)
    ImageButton btnShare2;
    @BindView(R2.id.btn_add)
    ImageView btnAdd;
    @BindView(R2.id.tv_image_count)
    TextView tvImageCount;
    @BindView(R2.id.btn_audio)
    ImageView btnAudio;
    @BindView(R2.id.btn_speak)
    SpeakView btnSpeak;
    @BindView(R2.id.et_content)
    EditText etContent;
    @BindView(R2.id.tv_sender)
    TextView tvSender;
    @BindView(R2.id.bottom_layout_host)
    LinearLayout bottomLayoutHost;
    @BindView(R2.id.add_image_layout)
    LinearLayout addImageLayout;
    @BindView(R2.id.add_video_layout)
    LinearLayout addVideoLayout;
    @BindView(R2.id.add_menu_layout)
    RelativeLayout addMenuLayout;
    @BindView(R2.id.iv_video)
    ImageView ivVideo;
    @BindView(R2.id.btn_video_delete)
    ImageButton btnVideoDelete;
    @BindView(R2.id.video_item_layout)
    RelativeLayout videoItemLayout;
    @BindView(R2.id.videos_layout)
    RelativeLayout videosLayout;
    @BindView(R2.id.images_recycler)
    RecyclerView imagesRecycler;
    @BindView(R2.id.tv_image_limit)
    TextView tvImageLimit;
    @BindView(R2.id.images_layout)
    RelativeLayout imagesLayout;
    @BindView(R2.id.menus_layout)
    RelativeLayout menusLayout;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R2.id.iv_mic)
    ImageView ivMic;
    @BindView(R2.id.tv_record_hint)
    TextView tvRecordHint;
    @BindView(R2.id.record_view)
    ChatRecordView recordView;
    @BindView(R2.id.click_view)
    View clickView;

    Unbinder unbinder;

    private static final int CUSTOMER_IMAGE_SIZE = 3;
    private static final int HOST_GUEST_IMAGE_SIZE = 6;

    private int audioIconResId = R.mipmap.icon_voice___live;

    private boolean immIsShow;
    private boolean showMenu;
    private InputMethodManager imm;
    private LiveMessage replyMessage;
    private LiveImagesAdapter imagesAdapter;
    private HljRoundProgressDialog progressDialog;
    private SubscriptionList uploadSubscriptions;
    private Video video;

    private LiveChannelMessageFragment chatFragment;
    private MessageFragmentAdapter fragmentAdapter;
    private TextView liveNewsView;
    private TextView chatNewsView;
    private Subscription rxSub;


    public static HostLiveChannelFragment newInstance(LiveChannel channel) {
        Bundle args = new Bundle();
        HostLiveChannelFragment fragment = new HostLiveChannelFragment();
        args.putParcelable(ARG_CHANNEL, channel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Override
    public void onResume() {
        super.onResume();
        initRxSub();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_live_channel, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();
        return view;
    }

    @Override
    void initValues() {
        super.initValues();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        fragmentAdapter = new MessageFragmentAdapter(getChildFragmentManager());
    }

    @Override
    protected void initViews() {
        setViewPager();
        setStatusLayout(channel);

        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                //判断键盘状态
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getActivity().getWindow()
                        .getDecorView()
                        .getHeight();
                immIsShow = (double) (bottom - top) / height < 0.8;
                if (immIsShow) {
                    onImmShow();
                } else {
                    onImmHide();
                }

            }
        });
        clickView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //在有回复状态时取消回复
                if (replyMessage != null) {
                    replyMessage = null;
                    etContent.setHint(R.string.hint_content_edit___live);
                }
                if (immIsShow && getActivity().getCurrentFocus() != null) {
                    immIsShow = false;
                    imm.toggleSoftInputFromWindow(getActivity().getCurrentFocus()
                            .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                } else if (menusLayout.getVisibility() == View.VISIBLE) {
                    onBottomVisibleMode(false);
                    return true;
                }
                return false;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imagesRecycler.setLayoutManager(layoutManager);
        imagesRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(
                    Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = 0;
                } else {
                    outRect.left = CommonUtil.dp2px(getContext(), 10);
                }
            }
        });
        if (imagesAdapter == null) {
            imagesAdapter = new LiveImagesAdapter(getContext(),
                    channel.getLiveRole() == HljLive.ROLE.CUSTORMER ? CUSTOMER_IMAGE_SIZE :
                            HOST_GUEST_IMAGE_SIZE,
                    this);
            imagesAdapter.setAddImageListener(this);
            imagesRecycler.setAdapter(imagesAdapter);
        }
        if (channel.getLiveRole() == HljLive.ROLE.CUSTORMER) {
            addVideoLayout.setVisibility(View.GONE);
        } else {
            addVideoLayout.setVisibility(View.VISIBLE);
        }
        setRecorderBtn();
    }

    private void initRxSub() {
        if (rxSub == null || rxSub.isUnsubscribed()) {
            rxSub = RxBus.getDefault()
                    .toObservable(LiveRxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<LiveRxEvent>() {
                        @Override
                        protected void onEvent(LiveRxEvent liveRxEvent) {
                            if (liveRxEvent.getChannelId() != channel.getId()) {
                                return;
                            }
                            switch (liveRxEvent.getType()) {
                                case REPLY_MESSAGE:
                                    //回复消息
                                    replyMessage = (LiveMessage) liveRxEvent.getObject();
                                    if (replyMessage != null && replyMessage.getUser() != null) {
                                        etContent.setHint("@" + replyMessage.getUser()
                                                .getName());
                                    } else {
                                        etContent.setHint(R.string.hint_content_edit___live);
                                    }
                                    if (!immIsShow && getActivity().getCurrentFocus() != null) {
                                        etContent.requestFocus();
                                        imm.toggleSoftInputFromWindow(getActivity()
                                                        .getCurrentFocus()
                                                        .getWindowToken(),
                                                0,
                                                InputMethodManager.HIDE_NOT_ALWAYS);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }


    private void setRecorderBtn() {
        //设置录音按钮
        btnSpeak.setChatRecordView(recordView);
        btnSpeak.setUserName("live" + channel.getId());
        btnSpeak.setMaxSec(60);
        btnSpeak.setOnSpeakStatusListener(new SpeakView.OnSpeakStatusListener() {
            @Override
            public void recorderDone(String filePath, double time) {
                if (getLiveChannelActivity().getLiveSocket() == null) {
                    return;
                }

                final LiveMessage message = getLiveChannelActivity().initLiveMessage(new
                        LiveContent(
                        filePath,
                        time), replyMessage);
                RxBus.getDefault()
                        .post(new LiveRxEvent(LiveRxEvent.RxEventType.SEND_MESSAGE,
                                channel.getId(),
                                message));
                Subscription uploadSubscription = new HljFileUploadBuilder(new File(filePath))
                        .tokenPath(
                        HljFileUploadBuilder.QINIU_VOICE_URL,
                        HljFileUploadBuilder.UploadFrom.MESSAGE_VOICE)
                        .build()
                        .subscribe(new Subscriber<HljUploadResult>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(HljUploadResult hljUploadResult) {
                                LiveContent content = message.getLiveContent();
                                content.setVoicePath(hljUploadResult.getUrl());
                                getLiveChannelActivity().sendMessage(message);
                            }
                        });
                if (uploadSubscriptions == null) {
                    uploadSubscriptions = new SubscriptionList();
                }
                uploadSubscriptions.add(uploadSubscription);

            }
        });
    }

    private void onImmShow() {
        showMenu = false;
        onBottomVisibleMode(false);

        // 修改样式
        bottomLayoutHost.getLayoutParams().height = CommonUtil.dp2px(getContext(), 50);

        ViewGroup.LayoutParams params = btnAudio.getLayoutParams();
        params.width = CommonUtil.dp2px(getContext(), 28);
        params.height = CommonUtil.dp2px(getContext(), 28);
        btnAudio.setLayoutParams(params);
        btnAudio.setImageResource(R.mipmap.icon_audio_gray);

        ViewGroup.LayoutParams params2 = btnAdd.getLayoutParams();
        params2.width = CommonUtil.dp2px(getContext(), 28);
        params2.height = CommonUtil.dp2px(getContext(), 28);
        btnAdd.setLayoutParams(params2);
        btnAdd.setImageResource(R.mipmap.icon_cross_add_44_44);

        ViewGroup.LayoutParams params3 = etContent.getLayoutParams();
        params3.height = CommonUtil.dp2px(getContext(), 36);
        etContent.setLayoutParams(params3);
        etContent.setBackgroundResource(R.drawable.sp_r18_stroke_line2);

        btnShare2.setVisibility(View.GONE);
        btnAudio.requestLayout();
        btnAdd.requestLayout();
        etContent.requestLayout();
        bottomLayoutHost.requestLayout();

        bottomLayout.requestLayout();
    }

    private void onImmHide() {
        // 修改样式
        bottomLayoutHost.getLayoutParams().height = CommonUtil.dp2px(getContext(), 60);

        ViewGroup.LayoutParams params = btnAudio.getLayoutParams();
        params.width = CommonUtil.dp2px(getContext(), 40);
        params.height = CommonUtil.dp2px(getContext(), 40);
        btnAudio.setLayoutParams(params);
        btnAudio.setImageResource(audioIconResId);

        ViewGroup.LayoutParams params2 = btnAdd.getLayoutParams();
        params2.width = CommonUtil.dp2px(getContext(), 40);
        params2.height = CommonUtil.dp2px(getContext(), 40);
        btnAdd.setLayoutParams(params2);
        btnAdd.setImageResource(R.mipmap.icon_add___live);

        ViewGroup.LayoutParams params3 = etContent.getLayoutParams();
        params3.height = CommonUtil.dp2px(getContext(), 40);
        etContent.setLayoutParams(params3);
        etContent.setBackgroundResource(R.drawable.sp_r20_f5f5f9);

        btnShare2.setVisibility(View.VISIBLE);
        btnAudio.requestLayout();
        btnAdd.requestLayout();
        etContent.requestLayout();
        bottomLayoutHost.requestLayout();

        if (showMenu) {
            onBottomVisibleMode(true);
            etContent.requestFocus();
        }
    }

    /**
     * 选择图片菜单切换
     *
     * @param isShow
     */
    private void onBottomVisibleMode(boolean isShow) {
        if (isShow) {
            menusLayout.setVisibility(View.VISIBLE);
        } else {
            menusLayout.setVisibility(View.GONE);
        }
        setChooseMediaCount();
    }

    private void onBottomChildViewVisibleChange(View visibleView) {
        for (int i = 0; i < menusLayout.getChildCount(); i++) {
            View childView = menusLayout.getChildAt(i);
            childView.setVisibility(childView == visibleView ? View.VISIBLE : View.GONE);
        }
    }

    private void setChooseMediaCount() {
        int count;
        if (video != null) {
            count = 1;
        } else {
            count = imagesAdapter == null ? 0 : imagesAdapter.getImageSize();
        }
        if (count > 0) {
            tvImageCount.setVisibility(menusLayout.getVisibility() == View.VISIBLE ? View.GONE :
                    View.VISIBLE);
        } else {
            tvImageCount.setVisibility(View.GONE);
        }
        tvImageCount.setText(String.valueOf(count));
    }

    /**
     * 更新当前图片数
     */
    private void onImagesLayoutChange() {
        int size = imagesAdapter == null ? 0 : imagesAdapter.getImageSize();
        if (channel.getLiveRole() == HljLive.ROLE.CUSTORMER) {
            tvImageLimit.setVisibility(View.VISIBLE);
            tvImageLimit.setText(size + "/" + CUSTOMER_IMAGE_SIZE);
        } else {
            tvImageLimit.setVisibility(View.VISIBLE);
            tvImageLimit.setText(size + "/" + HOST_GUEST_IMAGE_SIZE);
        }
        if (size > 0) {
            onBottomChildViewVisibleChange(imagesLayout);
        } else {
            onBottomChildViewVisibleChange(addMenuLayout);
        }
        setChooseMediaCount();
    }

    private void onVideosLayoutChange() {
        if (video != null) {
            onBottomChildViewVisibleChange(videosLayout);
            Glide.with(this)
                    .load(video.getLocalPath())
                    .apply(new RequestOptions().frame(0))
                    .into(ivVideo);
        } else {
            onBottomChildViewVisibleChange(addMenuLayout);
        }
        setChooseMediaCount();
    }

    @OnClick(R2.id.btn_add)
    public void onShowImage() {
        if (channel == null) {
            return;
        }
        etContent.setVisibility(View.VISIBLE);
        btnSpeak.setVisibility(View.GONE);
        tvSender.setVisibility(View.VISIBLE);
        audioIconResId = R.mipmap.icon_voice___live;
        btnAudio.setImageResource(audioIconResId);
        etContent.requestFocus();
        if (menusLayout.getVisibility() == View.GONE && !immIsShow) {
            onBottomVisibleMode(true);
        } else if (getActivity().getCurrentFocus() != null) {
            showMenu = menusLayout.getVisibility() != View.VISIBLE;
            imm.toggleSoftInputFromWindow(getActivity().getCurrentFocus()
                    .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R2.id.add_image_layout)
    public void onAddImage() {
        int limit;
        int size = imagesAdapter == null ? 0 : imagesAdapter.getImageSize();
        if (channel.getLiveRole() == HljLive.ROLE.CUSTORMER) {
            limit = CUSTOMER_IMAGE_SIZE;
        } else {
            limit = HOST_GUEST_IMAGE_SIZE;
        }
        Intent intent = new Intent(getContext(), GifSupportImageChooserActivity.class);
        intent.putExtra("limit", limit - size);
        startActivityForResult(intent, HljLive.CHOOSE_PHOTO);
    }

    @OnClick(R2.id.add_video_layout)
    public void onAddVideo() {
        Intent intent = new Intent(getContext(), VideoChooserActivity.class);
        intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH, 15);
        startActivityForResult(intent, HljLive.CHOOSE_VIDEO);
    }

    @OnClick(R2.id.btn_video_delete)
    public void onVideoDelete() {
        video = null;
        onVideosLayoutChange();
    }

    @OnClick(R2.id.video_item_layout)
    public void onVideoPreview() {
        if (video == null || TextUtils.isEmpty(video.getLocalPath())) {
            return;
        }
        Intent intent = new Intent(getContext(), VideoPreviewActivity.class);
        intent.putExtra("uri", Uri.parse(video.getLocalPath()));
        startActivity(intent);
    }

    @OnClick(R2.id.btn_audio)
    public void onVoiceMode() {
        if (channel == null) {
            return;
        }
        HostLiveChannelFragmentPermissionsDispatcher.onRecordAudioWithCheck(this);
    }

    @OnClick(R2.id.tv_sender)
    void onSend() {
        if (channel == null) {
            return;
        }
        final List<Photo> images = imagesAdapter == null ? null : imagesAdapter.getImages();
        if (etContent.getText()
                .length() == 0 && (images == null || images.isEmpty()) && video == null) {
            return;
        }
        if (video != null) {
            uploadVideo();
        } else if (images != null && !images.isEmpty()) {
            uploadPhoto(0);
        } else {
            sendContent();
        }
    }

    @OnClick(R2.id.btn_share2)
    void onShare() {
        getLiveChannelActivity().onShare();
    }

    /**
     * 上传图文类型消息
     */
    private void sendContent() {
        LiveContent liveContent;
        if (video != null) {
            liveContent = new LiveContent(etContent.getText()
                    .toString(), video);
        } else {
            List<Photo> images = imagesAdapter == null ? null : imagesAdapter.getImages();
            List<String> paths = null;
            if (images != null && !images.isEmpty()) {
                paths = new ArrayList<>();
                for (Photo photo : images) {
                    paths.add(photo.getImagePath());
                }
            }
            if (CommonUtil.isCollectionEmpty(paths)) {
                liveContent = new LiveContent(etContent.getText()
                        .toString());
            } else {
                liveContent = new LiveContent(etContent.getText()
                        .toString(), paths);
            }
        }
        getLiveChannelActivity().sendMessage(getLiveChannelActivity().initLiveMessage(liveContent,
                replyMessage));
        etContent.setText(null);
        if (imagesAdapter != null && imagesAdapter.getImageSize() > 0) {
            imagesAdapter.clear();
            onImagesLayoutChange();
        }
        if (video != null) {
            video = null;
            onVideosLayoutChange();
        }
    }

    private void uploadVideo() {
        if (video == null || !TextUtils.isEmpty(video.getOriginPath())) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            sendContent();
            return;
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.getRoundProgress(getContext());
        }
        progressDialog.show();
        final Subscription uploadSubscription = new HljFileUploadBuilder(new File(video
                .getLocalPath())).progressListener(
                new HljUploadListener() {
                    @Override
                    public void transferred(long transBytes) {
                        if (progressDialog != null) {
                            progressDialog.setProgress(transBytes);
                        }
                    }

                    @Override
                    public void setContentLength(long contentLength) {
                        if (progressDialog != null) {
                            progressDialog.setMax(contentLength);
                        }
                    }
                })
                .tokenPath(HljFileUploadBuilder.QINIU_VIDEO_URL,
                        HljFileUploadBuilder.UploadFrom.LIVE_VIDEO)
                .build()
                .subscribe(new Subscriber<HljUploadResult>() {

                    @Override
                    public void onStart() {
                        progressDialog.setMessage("视频上传");
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        sendContent();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onNext(HljUploadResult hljUploadResult) {
                        video.setOriginPath(hljUploadResult.getUrl());
                        video.setPersistentId(hljUploadResult.getPersistentId());
                        if (video.getWidth() == 0 || video.getHeight() == 0) {
                            if (hljUploadResult.getVideoWidth() > 0 && hljUploadResult
                                    .getVideoHeight() > 0) {
                                video.setWidth(hljUploadResult.getVideoWidth());
                                video.setHeight(hljUploadResult.getVideoHeight());
                            }
                            if (hljUploadResult.getVideoDuration() > 0) {
                                video.setDuration(hljUploadResult.getVideoDuration());
                            }
                        }
                    }
                });
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CommonUtil.unSubscribeSubs(uploadSubscription);
            }
        });
        if (uploadSubscriptions == null) {
            uploadSubscriptions = new SubscriptionList();
        }
        uploadSubscriptions.add(uploadSubscription);
    }

    /**
     * 依次上传图片，如果上传结束发送信息
     *
     * @param index 当前图片位置
     */
    private void uploadPhoto(final int index) {
        final List<Photo> images = imagesAdapter == null ? null : imagesAdapter.getImages();
        if (images == null || images.isEmpty() || index >= images.size()) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            sendContent();
            return;
        }
        final Photo photo = images.get(index);
        String path = photo.getImagePath();
        if (!TextUtils.isEmpty(path) && !path.startsWith("http://") && !path.startsWith
                ("https://")) {
            if (progressDialog == null) {
                progressDialog = DialogUtil.getRoundProgress(getContext());
            }
            progressDialog.show();
            final Subscription uploadSubscription = new HljFileUploadBuilder(new File(photo
                    .getImagePath())).compress(
                    getContext())
                    .progressListener(new HljUploadListener() {
                        @Override
                        public void transferred(long transBytes) {
                            if (progressDialog != null) {
                                progressDialog.setProgress(transBytes);
                            }
                        }

                        @Override
                        public void setContentLength(long contentLength) {
                            if (progressDialog != null) {
                                progressDialog.setMax(contentLength);
                            }
                        }
                    })
                    .tokenPath(HljFileUploadBuilder.QINIU_IMAGE_TOKEN)
                    .build()
                    .subscribe(new Subscriber<HljUploadResult>() {

                        @Override
                        public void onStart() {
                            progressDialog.setMessage((index + 1) + "/" + images.size());
                            super.onStart();
                        }

                        @Override
                        public void onCompleted() {
                            uploadPhoto(index + 1);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                        }

                        @Override
                        public void onNext(HljUploadResult hljUploadResult) {
                            photo.setImagePath(hljUploadResult.getUrl());
                        }
                    });
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    CommonUtil.unSubscribeSubs(uploadSubscription);
                }
            });
            if (uploadSubscriptions == null) {
                uploadSubscriptions = new SubscriptionList();
            }
            uploadSubscriptions.add(uploadSubscription);
        } else {
            uploadPhoto(index + 1);
        }
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void onRecordAudio() {
        if (etContent.getVisibility() == View.VISIBLE) {
            showMenu = false;
            etContent.setVisibility(View.GONE);
            btnSpeak.setVisibility(View.VISIBLE);
            tvSender.setVisibility(View.GONE);
            onBottomVisibleMode(false);
            audioIconResId = R.mipmap.icon_keyboard___live;
            btnAudio.setImageResource(audioIconResId);
            if (!immIsShow)
                return;
        } else {
            etContent.setVisibility(View.VISIBLE);
            btnSpeak.setVisibility(View.GONE);
            tvSender.setVisibility(View.VISIBLE);
            audioIconResId = R.mipmap.icon_voice___live;
            btnAudio.setImageResource(audioIconResId);
            etContent.requestFocus();
        }
        if (getActivity().getCurrentFocus() != null) {
            imm.toggleSoftInputFromWindow(getActivity().getCurrentFocus()
                    .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        HostLiveChannelFragmentPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljLive.CHOOSE_PHOTO:
                    if (data != null) {
                        ArrayList<Photo> photos = data.getParcelableArrayListExtra
                                ("selectedPhotos");
                        if (photos != null && imagesAdapter != null) {
                            imagesAdapter.addImages(photos);
                            onImagesLayoutChange();
                        }
                    }
                    break;
                case HljLive.CHOOSE_VIDEO:
                    if (data != null) {
                        Photo photo = data.getParcelableExtra("photo");
                        if (photo != null) {
                            video = new Video(photo);
                            onVideosLayoutChange();
                        }
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    void onLiveRxEvent(LiveRxEvent liveRxEvent) {
        super.onLiveRxEvent(liveRxEvent);
        switch (liveRxEvent.getType()) {
            case LIVE_NEWS:
                noticeLiveNews((int) liveRxEvent.getObject());
                break;
            case CHAT_NEWS:
                noticeChatNews((int) liveRxEvent.getObject());
                break;
        }
    }

    @Override
    protected void setDanmakuViews(List<LiveMessage> liveMessages) {

    }

    @Override
    protected RecyclerView getStickRecyclerView() {
        return stickImagesRecycler;
    }

    @Override
    void updateMerchantInfo(Merchant merchant) {
        super.updateMerchantInfo(merchant);
    }

    @Override
    public void toggleDanmaku(boolean visible) {

    }

    @Override
    public void backRoleRoom() {
        if (channel.getLiveRole() == HljLive.ROLE.CUSTORMER) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onShopping() {

    }

    @Override
    void setStatusLayout(LiveChannel liveChannel) {
        super.setStatusLayout(liveChannel);
        liveStatusHolder.setVisibility(View.VISIBLE);
        liveStatusHolder.removeAllViews();
        liveStatusHolder.addView(statusViewHolder.getView());
        statusViewHolder.setStatus(liveChannel);
    }

    private void noticeLiveNews(int newsCount) {
        if (newsCount > 0 && viewPager.getCurrentItem() == 1) {
            liveNewsView.setVisibility(View.VISIBLE);
            liveNewsView.setText(newsCount > 99 ? "99+" : String.valueOf(newsCount));
        } else {
            liveNewsView.setVisibility(View.GONE);
        }
    }

    private void noticeChatNews(int newsCount) {
        if (newsCount > 0 && viewPager.getCurrentItem() == 0) {
            chatNewsView.setVisibility(View.VISIBLE);
            chatNewsView.setText(newsCount > 99 ? "99+" : String.valueOf(newsCount));
        } else {
            chatNewsView.setVisibility(View.GONE);
        }
    }

    private void setViewPager() {
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setAdapter(fragmentAdapter);
        tabPageIndicator.setTabViewId(R.layout.menu_tab_view___live);
        tabPageIndicator.setPagerAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabPageIndicator.setCurrentItem(position);
            }
        });
        tabPageIndicator.setOnTabChangeListener(new TabPageIndicator.OnTabChangeListener() {
            @Override
            public void onTabChanged(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        tabPageIndicator.setCurrentItem(0);
        viewPager.setCurrentItem(0);
        for (int i = 0; i < fragmentAdapter.getCount(); i++) {
            View tabView = tabPageIndicator.getTabView(i);
            if (tabView != null) {
                TextView textView = tabView.findViewById(R.id.title);
                TextView countView = tabView.findViewById(R.id.news_count);
                if (i == 0) {
                    liveNewsView = countView;
                } else if (i == 1) {
                    chatNewsView = countView;
                }
                textView.getLayoutParams().width = (int) textView.getPaint()
                        .measureText(textView.getText()
                                .toString()
                                .trim()) + CommonUtil.dp2px(getContext(), 24);
            }
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onPause() {
        super.onPause();
        CommonUtil.unSubscribeSubs(rxSub);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(rxSub);
        unbinder.unbind();
    }


    @Override
    public void onRemove() {
        onImagesLayoutChange();
    }

    @Override
    public void onAdd(int limit) {
        Intent intent = new Intent(getContext(), GifSupportImageChooserActivity.class);
        intent.putExtra("limit", limit);
        startActivityForResult(intent, HljLive.CHOOSE_PHOTO);
    }

    private class MessageFragmentAdapter extends FragmentStatePagerAdapter {

        public MessageFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            LiveChannelMessageFragment fragment;
            switch (position) {
                case 1:
                    if (liveMessageFragment == null) {
                        liveMessageFragment = LiveChannelMessageFragment.newInstance(HljLive.ROOM
                                        .CHAT,
                                channel.getId());
                    }
                    fragment = liveMessageFragment;
                    break;
                default:
                    if (chatFragment == null) {
                        chatFragment = LiveChannelMessageFragment.newInstance(HljLive.ROOM.LIVE,
                                channel.getId());
                    }
                    fragment = chatFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "直播";
            }

            return "聊天室";
        }
    }
}
