package me.suncloud.marrymemo.fragment.ad;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.ad.MiaoZhenUtil;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.api.ad.MadApi;
import me.suncloud.marrymemo.model.Splash;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.ad.MadPoster;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TrackerUtil;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangtao on 2018/2/26.
 */

public class LaunchAdFragment extends RefreshFragment {

    private static final String ARG_MAD_OPEN = "is_mad_open";
    private static final String SPLASH_CPM_SOURCE = "splash_banner";

    @BindView(R.id.iv_splash)
    ImageView ivSplash;
    @BindView(R.id.tv_skip)
    TextView tvSkip;
    @BindView(R.id.splash_layout)
    FrameLayout splashLayout;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    Unbinder unbinder;


    private Subscription skipCounterSubscription;
    private Subscription initSubscription;
    private boolean isMadOpen;

    private Handler handler = new Handler();
    private Runnable finishRunnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (getActivity() != null && getActivity() instanceof LaunchAdFragmentInterface) {
                ((LaunchAdFragmentInterface) getActivity()).onLaunchFinish();
            }
        }
    };


    public static LaunchAdFragment newInstance(boolean isMadOpen) {
        LaunchAdFragment fragment = new LaunchAdFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_MAD_OPEN, isMadOpen);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_launch_ad, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initData();
        initView();
        return rootView;
    }

    private void initData() {
        if (getArguments() != null) {
            isMadOpen = getArguments().getBoolean(ARG_MAD_OPEN);
        }
    }

    private void initView() {
        int width = CommonUtil.getDeviceSize(splashLayout.getContext()).x;
        splashLayout.getLayoutParams().height = Math.round(width * 3 / 2);
        ivLogo.getLayoutParams().width = Math.round(width * 19 / 54);
        ivLogo.getLayoutParams().height = Math.round(width * 7 / 54);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        handler.postDelayed(finishRunnable, 2000);
        Observable<? extends Poster> splashObservable;
        User user = Session.getInstance()
                .getCurrentUser(getContext());
        if (user == null) {
            splashObservable = Observable.empty();
        } else if (isMadOpen) {
            splashObservable = MadApi.getSplashMadAd(getContext())
                    .filter(new Func1<MadPoster, Boolean>() {
                        @Override
                        public Boolean call(MadPoster poster) {
                            return poster != null && !TextUtils.isEmpty(poster.getPath());
                        }
                    });
        } else {
            splashObservable = CustomCommonApi.getSplash()
                    .filter(new Func1<Splash, Boolean>() {
                        @Override
                        public Boolean call(Splash splash) {
                            return splash != null && splash.getPoster() != null;
                        }
                    })
                    .map(new Func1<Splash, Poster>() {
                        @Override
                        public Poster call(Splash splash) {
                            Poster poster = splash.getPoster();
                            poster.setPath(splash.getCoverImage());
                            return poster;
                        }
                    });
        }
        initSubscription = splashObservable.subscribe(new Subscriber<Poster>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Poster poster) {
                loadPosterCover(poster);
            }
        });
    }

    private void loadPosterCover(final Poster poster) {
        handler.removeCallbacks(finishRunnable);
        handler.postDelayed(finishRunnable, 3000);
        HljVTTagger.buildTagger(ivSplash)
                .tagName(HljTaggerName.SPLASH)
                .poster(poster)
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, SPLASH_CPM_SOURCE)
                .addMiaoZhenClickUrl(MiaoZhenUtil.getClickUrl(getContext(),
                        MiaoZhenUtil.PId.SPLASH_POSTER))
                .addMiaoZhenImpUrl(MiaoZhenUtil.getImpUrl(getContext(),
                        MiaoZhenUtil.PId.SPLASH_POSTER))
                .tag();
        Glide.with(this)
                .load(ImagePath.buildPath(poster.getPath())
                        .width(ivSplash.getWidth())
                        .ignoreFormat(true)
                        .path())
                .apply(new RequestOptions().placeholder(R.mipmap.image_splash))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(
                            @Nullable GlideException e,
                            Object model,
                            Target<Drawable> target,
                            boolean isFirstResource) {
                        handler.removeCallbacks(finishRunnable);
                        handler.post(finishRunnable);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(
                            Drawable resource,
                            Object model,
                            Target<Drawable> target,
                            DataSource dataSource,
                            boolean isFirstResource) {
                        try {
                            showSkip();

                            ivSplash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.removeCallbacks(finishRunnable);
                                    if (getActivity() != null && getActivity() instanceof
                                            LaunchAdFragmentInterface) {
                                        ((LaunchAdFragmentInterface) getActivity()).onPosterClick(
                                                poster);
                                    }
                                }
                            });
                            handler.removeCallbacks(finishRunnable);
                            handler.postDelayed(finishRunnable, 3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .into(ivSplash);
    }

    private void showSkip() {
        skipCounterSubscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onStart() {
                        tvSkip.setVisibility(View.VISIBLE);
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                        tvSkip.setText(R.string.action_skip);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        tvSkip.setText(getString(R.string.label_skip_counter, 2 - aLong));
                    }
                });
    }

    @OnClick(R.id.tv_skip)
    public void onSkip() {
        if (skipCounterSubscription == null || !skipCounterSubscription.isUnsubscribed()) {
            return;
        }
        if (getActivity() != null && getActivity() instanceof LaunchAdFragmentInterface) {
            ((LaunchAdFragmentInterface) getActivity()).onLaunchFinish();
        }
    }

    public boolean isFinishEnabled() {
        return tvSkip.isEnabled();
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        CommonUtil.unSubscribeSubs(initSubscription, skipCounterSubscription);
        handler.removeCallbacks(finishRunnable);
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface LaunchAdFragmentInterface {

        void onLaunchFinish();

        void onPosterClick(Poster poster);
    }
}
