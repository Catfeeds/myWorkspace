package com.hunliji.hljlivelibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.activities.LiveMediaPagerActivity;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luohanlin on 2017/11/27.
 */

public class StickMessageAdapter extends RecyclerView.Adapter<BaseViewHolder<LiveMessage>> {

    private Context context;
    private List<LiveMessage> messageList;
    private int width;
    private int height;

    public static final int TYPE_ITEM = 1;

    public StickMessageAdapter(
            Context context, List<LiveMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
        width = CommonUtil.dp2px(context, 96);
        height = CommonUtil.dp2px(context, 60);
    }

    @Override
    public BaseViewHolder<LiveMessage> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.stick_message_item_layout, parent, false);
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<LiveMessage> holder, int position) {
        if (holder instanceof MsgViewHolder) {
            holder.setView(context,
                    messageList.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    class MsgViewHolder extends BaseViewHolder<LiveMessage> {
        @BindView(R2.id.start_padding)
        View startPadding;
        @BindView(R2.id.img_cover)
        ImageView imgCover;
        @BindView(R2.id.img_tag)
        ImageView imgTag;
        @BindView(R2.id.tv_tag)
        TextView tvTag;
        @BindView(R2.id.end_padding)
        View endPadding;

        MsgViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        @Override
        public void setView(Context mContext, LiveMessage item, int position, int viewType) {
            try {
                HljVTTagger.buildTagger(imgCover)
                        .tagName(HljTaggerName.LIVE_MEDIA)
                        .atPosition(position)
                        .hitTag();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.setView(mContext, item, position, viewType);
        }

        @Override
        protected void setViewData(
                Context mContext, final LiveMessage item, int position, int viewType) {
            if (item == null) {
                return;
            }
            startPadding.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            endPadding.setVisibility(position == messageList.size() - 1 ? View.VISIBLE : View.GONE);
            MultiTransformation transformation = new MultiTransformation(new CenterCrop(),
                    new RoundedCorners(CommonUtil.dp2px(context, 3)));
            if (item.getLiveContent()
                    .getVideoMedia() != null) {
                Glide.with(context)
                        .load(item.getLiveContent()
                                .getVideoMedia()
                                .getMedia()
                                .getVideo()
                                .getScreenShot())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .transform(transformation))
                        .into(imgCover);
                imgTag.setImageResource(R.mipmap.icon_video_tag___live);
                tvTag.setText(getDurationStr(item.getLiveContent()
                        .getVideoMedia()
                        .getMedia()
                        .getVideo()
                        .getDuration()));
            } else if (!CommonUtil.isCollectionEmpty(item.getLiveContent()
                    .getImageMedias())) {
                Glide.with(context)
                        .load(item.getLiveContent()
                                .getImageMedias()
                                .get(0)
                                .getMedia()
                                .getPhoto()
                                .getImagePath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .transform(transformation))
                        .into(imgCover);
                imgTag.setImageResource(R.mipmap.icon_pic_tag___live);
                tvTag.setText(String.valueOf(item.getLiveContent()
                        .getImageMedias()
                        .size()));
            }
            imgCover.setOnClickListener(new OnImagesClickListener(item));
        }

        private String getDurationStr(float duration) {
            int minutes;
            int seconds;
            if (duration > 60) {
                minutes = (int) (duration / 60);
                seconds = (int) (duration - (minutes * 60));
            } else {
                minutes = 0;
                seconds = (int) duration;
            }

            return pendingZero(minutes) + ":" + pendingZero(seconds);
        }

        private String pendingZero(int n) {
            if (n < 10) {
                return "0" + n;
            } else {
                return String.valueOf(n);
            }
        }

        /**
         * 图片点击整合所有消息图片，然后跳转多图详情
         */
        private class OnImagesClickListener implements View.OnClickListener {

            private LiveMessage clickMessage;

            private OnImagesClickListener(LiveMessage message) {
                clickMessage = message;
            }

            @Override
            public void onClick(View v) {
                ArrayList<LiveMessage> list = new ArrayList<>(messageList);
                Intent intent = new Intent(v.getContext(), LiveMediaPagerActivity.class);
                intent.putParcelableArrayListExtra(LiveMediaPagerActivity.ARG_MESSAGES, list);
                intent.putExtra(LiveMediaPagerActivity.ARG_IS_REPLY, false);
                intent.putExtra(LiveMediaPagerActivity.ARG_CLICK_MESSAGE, clickMessage);
                intent.putExtra(LiveMediaPagerActivity.ARG_SUB_POSITION, 0);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        }
    }
}
