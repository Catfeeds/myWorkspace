package me.suncloud.marrymemo.adpter.community.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerCommunityThreadViewHolder;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;

/**
 * Created by mo_yu on 2018/3/15.推荐话题列表项
 */

public class CommunityThreadViewHolder extends TrackerCommunityThreadViewHolder {

    @BindView(R.id.tv_thread_title)
    TextView tvThreadTitle;
    @BindView(R.id.riv_auth_avatar)
    RoundedImageView rivAuthAvatar;
    @BindView(R.id.tv_auth_name)
    TextView tvAuthName;
    @BindView(R.id.auth_view)
    LinearLayout authView;
    @BindView(R.id.tv_thread_content)
    TextView tvThreadContent;
    @BindView(R.id.img_single_img)
    ImageView imgSingleCover;
    @BindView(R.id.img_thread_1)
    ImageView imgThread1;
    @BindView(R.id.img_thread_2)
    ImageView imgThread2;
    @BindView(R.id.img_thread_3)
    ImageView imgThread3;
    @BindView(R.id.thread_img_view)
    LinearLayout threadImgView;
    @BindView(R.id.tv_thread_img_count)
    TextView tvThreadImgCount;
    @BindView(R.id.thread_img_count_view)
    LinearLayout threadImgCountView;
    @BindView(R.id.thread_cover_view)
    RelativeLayout threadCoverView;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    private int dynamicImgSize;
    private int singleImgWidth;
    private int singleImgHeight;
    private int faceSize;
    private int avatarSize;
    private ArrayList<ImageView> imgLists;

    public CommunityThreadViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_thread_list_item___cv, parent, false));
    }

    public CommunityThreadViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        final Activity context = (Activity) itemView.getContext();
        dynamicImgSize = (CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 40)) / 3;
        singleImgHeight = CommonUtil.dp2px(context, 120);
        singleImgWidth = CommonUtil.dp2px(context, 160);
        faceSize = CommonUtil.dp2px(context, 16);
        avatarSize = CommonUtil.dp2px(context, 20);
        imgLists = new ArrayList<>(Arrays.asList(imgThread1, imgThread2, imgThread3));
        imgThread1.getLayoutParams().width = dynamicImgSize;
        imgThread1.getLayoutParams().height = dynamicImgSize;
        imgThread2.getLayoutParams().width = dynamicImgSize;
        imgThread2.getLayoutParams().height = dynamicImgSize;
        imgThread3.getLayoutParams().width = dynamicImgSize;
        imgThread3.getLayoutParams().height = dynamicImgSize;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityThread communityThread = getItem();
                if (communityThread == null) {
                    return;
                }
                Intent intent = new Intent(v.getContext(), CommunityThreadDetailActivity.class);
                intent.putExtra(CommunityThreadDetailActivity.ARG_ID, communityThread.getId());
                v.getContext()
                        .startActivity(intent);
            }
        });
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            Context mContext, CommunityThread communityThread, int position, int viewType) {
        if (communityThread == null) {
            return;
        }
        final Activity activity = (Activity) mContext;
        //pages字段 为null时 话题不是富文本话题
        tvThreadContent.setText(EmojiUtil.parseEmojiByText2(mContext,
                communityThread.getShowSubtitle(),
                faceSize));
        tvCommentCount.setText(String.valueOf(communityThread.getPostCount()));
        tvWatchCount.setText(String.valueOf(communityThread.getClickCount()));
        showThreadText(tvThreadTitle, communityThread.getShowTitle(), communityThread, mContext);
        setPhotoView(activity, communityThread.getShowPhotos());
        setAuthView(activity, communityThread.getAuthor());
    }

    /**
     * 话题作者
     */
    private void setAuthView(final Activity context, final CommunityAuthor author) {
        String avatarUrl = ImagePath.buildPath(author.getAvatar())
                .height(avatarSize)
                .width(avatarSize)
                .cropPath();
        Glide.with(context)
                .load(avatarUrl)
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary))
                .into(rivAuthAvatar);
        tvAuthName.setText(author.getName());
        rivAuthAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                        .withLong("id", author.getId())
                        .navigation(context);
            }
        });
    }

    /**
     * 话题标志
     */
    private void showThreadText(
            TextView tvThreadTitle, String title, CommunityThread item, Context mContext) {
        //话题标志
        if (TextUtils.isEmpty(title)) {
            tvThreadTitle.setVisibility(View.GONE);
        } else {
            tvThreadTitle.setText(EmojiUtil.parseEmojiByText2(mContext, title, faceSize));
        }
    }

    /**
     * 设置话题图片
     */
    private void setPhotoView(
            Activity context, List<Photo> photos) {
        int size = 0;
        if (photos != null) {
            size = photos.size();//图片数量
        }
        //社区频道页不显示三图样式
        if (size >= 3) {
            //三图样式时
            tvThreadTitle.setMaxLines(2);
            tvThreadContent.setMaxLines(2);
            threadCoverView.setVisibility(View.VISIBLE);
            imgSingleCover.setVisibility(View.GONE);
            threadImgCountView.setVisibility(View.GONE);
            for (int i = 0; i < 3; i++) {
                String url = ImageUtil.getImagePath(photos.get(i)
                        .getImagePath(), dynamicImgSize);
                if (!TextUtils.isEmpty(url)) {
                    imgLists.get(i)
                            .setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                            .into(imgLists.get(i));
                } else {
                    imgLists.get(i)
                            .setVisibility(View.GONE);
                }
            }
            if (size > 3) {
                threadImgCountView.setVisibility(View.VISIBLE);
                tvThreadImgCount.setText(String.valueOf(photos.size()));
            }
        } else if (size > 0) {
            tvThreadTitle.setMaxLines(4);
            tvThreadTitle.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            tvThreadTitle.getViewTreeObserver()
                                    .removeOnPreDrawListener(this);
                            int titleLineCount = tvThreadTitle.getLineCount();
                            tvThreadContent.setMaxLines(5 - titleLineCount);
                            tvThreadContent.invalidate();
                            return false;
                        }
                    });
            threadCoverView.setVisibility(View.GONE);
            String url = ImagePath.buildPath(photos.get(0)
                    .getImagePath())
                    .width(singleImgWidth)
                    .height(singleImgHeight)
                    .cropPath();
            if (!TextUtils.isEmpty(url)) {
                imgSingleCover.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(url)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imgSingleCover);
            } else {
                imgSingleCover.setVisibility(View.GONE);
            }
        } else {
            tvThreadTitle.setMaxLines(2);
            tvThreadContent.setMaxLines(3);
            threadImgCountView.setVisibility(View.GONE);
            imgSingleCover.setVisibility(View.GONE);
            threadCoverView.setVisibility(View.GONE);
        }
    }
}
