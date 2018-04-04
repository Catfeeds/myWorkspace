package com.hunliji.hljsharelibrary.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.BannerJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.R;
import com.hunliji.hljsharelibrary.R2;
import com.hunliji.hljsharelibrary.adapters.ShareActionAdapter;
import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionViewHolder;
import com.hunliji.hljsharelibrary.models.ShareAction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by wangtao on 2018/3/20.
 */

public class ShareDialog extends Dialog implements DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener {

    private static final String SHARE_CPM_SOURCE = "share_banner";

    @BindView(R2.id.iv_share_poster)
    ImageView ivSharePoster;
    @BindView(R2.id.tv_alert_title)
    TextView tvAlertTitle;
    @BindView(R2.id.share_recycler_view)
    RecyclerView shareRecyclerView;
    @BindView(R2.id.action_cancel)
    Button actionCancel;

    private OnDismissListener onDismissListener;
    private OnShowListener onShowListener;
    private ShareItemDecoration itemDecoration;

    private int posterWidth;
    private int posterHeight;

    private Subscription posterSubscription;
    private Poster poster;
    private String pageClassName;


    private ShareActionAdapter adapter;

    public ShareDialog(@NonNull Context context) {
        super(context, R.style.BubbleDialogTheme);
        setContentView(R.layout.dialog_share_menu___share);
        ButterKnife.bind(this);

        if (context instanceof Activity) {
            pageClassName = context.getClass()
                    .getName();
        }
        initView();

        Window win = getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(context).x;
            win.setAttributes(params);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
    }

    private void initView() {
        shareRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        adapter = new ShareActionAdapter();
        shareRecyclerView.setAdapter(adapter);

        posterWidth = CommonUtil.getDeviceSize(getContext()).x;
        posterHeight = Math.round(posterWidth * 100 / 750);
        ivSharePoster.getLayoutParams().height = posterHeight;
        super.setOnShowListener(this);
        super.setOnDismissListener(this);
    }


    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
        this.onShowListener = listener;
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        shareRecyclerView.addItemDecoration(itemDecoration);
    }

    public void setOnShareClickListener(
            final ShareActionViewHolder.OnShareClickListener onShareClickListener) {
        adapter.setOnShareClickListener(new ShareActionViewHolder.OnShareClickListener() {
            @Override
            public void onShare(View v, ShareAction action) {
                dismiss();
                onShareClickListener.onShare(v, action);
            }
        });
    }


    public void setActions(ShareAction... actions) {
        adapter.setActions(actions);
        if (itemDecoration != null) {
            shareRecyclerView.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ShareItemDecoration(getContext(), actions.length);
        shareRecyclerView.addItemDecoration(itemDecoration);
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            tvAlertTitle.setVisibility(View.GONE);
        } else {
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertTitle.setText(title);
        }
    }

    @OnClick(R2.id.action_cancel)
    public void onCancel() {
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
        CommonUtil.unSubscribeSubs(posterSubscription);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (onShowListener != null) {
            onShowListener.onShow(dialog);
        }
        if(poster!=null){
            return;
        }
        ivSharePoster.setVisibility(View.INVISIBLE);
        if (HljShare.showSharePoster(pageClassName)) {
            loadPoster();
        }
    }

    private void loadPoster() {
        City city = LocationSession.getInstance()
                .getCity(getContext());
        posterSubscription = CommonApi.getBanner(getContext(),
                HljCommon.BLOCK_ID.ShareDialog,
                city.getCid())
                .map(new Func1<PosterData, Poster>() {
                    @Override
                    public Poster call(PosterData posterData) {
                        List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                                HljCommon.POST_SITES.SITE_SHARE_TOP_BANNER,
                                false);
                        return CommonUtil.isCollectionEmpty(posters) ? null : posters.get(0);
                    }
                })
                .subscribe(new Subscriber<Poster>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Poster poster) {
                        if (poster != null) {
                            ShareDialog.this.poster = poster;
                            showPoser();
                        }
                    }
                });
    }

    private void showPoser() {
        ivSharePoster.setVisibility(View.VISIBLE);
        HljVTTagger.buildTagger(ivSharePoster)
                .tagName(HljTaggerName.SHARE_BANNER)
                .poster(poster)
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, SHARE_CPM_SOURCE)
                .addMiaoZhenClickUrl(HljShare.SHARE_IMP_URL)
                .addMiaoZhenImpUrl(HljShare.SHARE_CLICK_URL)
                .hitTag();
        HljViewTracker.fireViewVisibleEvent(ivSharePoster);
        Glide.with(ivSharePoster)
                .load(ImagePath.buildPath(poster.getPath())
                        .width(posterWidth)
                        .height(posterHeight)
                        .ignoreFormat(true)
                        .cropPath())
                .transition(new DrawableTransitionOptions().crossFade(300))
                .into(ivSharePoster);
    }

    @OnClick(R2.id.iv_share_poster)
    public void onPosterClicked() {
        if (poster != null) {
            BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                    .build(RouterPath.ServicePath.BANNER_JUMP)
                    .navigation();
            if (bannerJumpService != null) {
                bannerJumpService.bannerJump(getContext(), poster, null);
            }
        }
    }


    private class ShareItemDecoration extends RecyclerView.ItemDecoration {

        private int start;
        private int end;
        private int itemSpace;
        private static final int ITEM_WIDTH_DP = 70;

        ShareItemDecoration(Context context, int size) {
            int screenWidth = CommonUtil.getDeviceSize(context).x;
            int itemWidth = CommonUtil.dp2px(context, ITEM_WIDTH_DP);
            start = end = CommonUtil.dp2px(context, 24);
            if (size > 4) {
                this.itemSpace = CommonUtil.dp2px(context, 14);
            } else {
                itemSpace = Math.round((screenWidth - start - end - itemWidth * size) / 3);
            }
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = start;
            } else {
                outRect.left = itemSpace;
            }
            if (parent.getChildAdapterPosition(view) == parent.getAdapter()
                    .getItemCount() - 1) {
                outRect.right = end;
            }
        }
    }
}
