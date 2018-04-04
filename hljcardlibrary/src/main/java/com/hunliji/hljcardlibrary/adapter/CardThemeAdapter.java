package com.hunliji.hljcardlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.models.Theme;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/6/21.
 * 选择模板
 */

public class CardThemeAdapter extends RecyclerView.Adapter<BaseViewHolder<Theme>> {

    private Context context;
    private List<Theme> freeThemes;
    private List<Theme> lockThemes;
    private boolean isShared;
    public static final int LOCK_TEMPLATE_HEADER = -1;
    public static final int THREE_TEMPLATE_ITEM = 1;
    private int member;

    public CardThemeAdapter(Context context) {
        this.context = context;
    }

    /**
     * 会员专属tab
     *
     * @param member 1
     */
    public void setMember(int member) {
        this.member = member;
    }

    public enum ItemSpace {
        Header, Left, Right, Middle
    }

    public void setDataList(List<Theme> freeThemes, List<Theme> lockThemes) {
        this.freeThemes = freeThemes;
        this.lockThemes = lockThemes;
    }

    public void setShared(boolean isShared) {
        this.isShared = isShared;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<Theme> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case THREE_TEMPLATE_ITEM:
                return new ThemeViewHolder(getView(parent, viewType));
        }
        return new ExtraViewHolder(getView(parent, viewType));
    }

    public class ExtraViewHolder extends BaseViewHolder<Theme> {

        @BindView(R2.id.view_white)
        View viewWhite;
        @BindView(R2.id.view_gray)
        View viewGray;
        @BindView(R2.id.ll_theme_header)
        View llThemeHeader;
        @BindView(R2.id.btn_share)
        Button btnShare;

        public ExtraViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(final Context mContext, Theme item, int position, int viewType) {
            if (member == 1) {
                itemView.setVisibility(View.GONE);
            } else {
                btnShare.setVisibility(isShared ? View.GONE : View.VISIBLE);
                llThemeHeader.setVisibility(View.VISIBLE);
                if (freeThemes == null || freeThemes.isEmpty()) {
                    viewWhite.setVisibility(View.GONE);
                    viewGray.setVisibility(View.GONE);
                } else {
                    viewWhite.setVisibility(View.VISIBLE);
                    viewGray.setVisibility(View.VISIBLE);
                }
                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCardThemeClickListener != null) {
                            onCardThemeClickListener.onCardShare();
                        }
                    }
                });
            }
        }
    }

    private View getView(ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType) {
            case THREE_TEMPLATE_ITEM:
                layoutId = R.layout.card_item_temple___card;
                break;
            default:
                layoutId = R.layout.card_template_header___card;
                break;
        }
        return LayoutInflater.from(context)
                .inflate(layoutId, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Theme> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    private Theme getItem(int position) {
        if (freeThemes != null && !freeThemes.isEmpty()) {
            if (freeThemes.size() > position) {
                return freeThemes.get(position);
            }
            position -= freeThemes.size();
        }
        if (lockThemes != null && !lockThemes.isEmpty()) {
            if (position == 0) {
                return null;
            }
            position--;
            if (lockThemes.size() > position) {
                return lockThemes.get(position);
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (freeThemes != null && !freeThemes.isEmpty()) {
            if (freeThemes.size() > position) {
                return THREE_TEMPLATE_ITEM;
            }
            position -= freeThemes.size();
        }
        if (lockThemes != null && !lockThemes.isEmpty()) {
            if (position == 0) {
                return LOCK_TEMPLATE_HEADER;
            }
            position--;
            if (lockThemes.size() > position) {
                return THREE_TEMPLATE_ITEM;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (freeThemes != null && !freeThemes.isEmpty()) {
            count += freeThemes.size();
        }
        if (lockThemes != null && !lockThemes.isEmpty()) {
            count += lockThemes.size() + 1;
        }
        return count;
    }

    public ItemSpace getSpaceType(int position) {
        if (freeThemes != null && !freeThemes.isEmpty()) {
            if (freeThemes.size() > position) {
                switch (position % 2) {
                    case 0:
                        return ItemSpace.Left;
                    case 1:
                        return ItemSpace.Right;
                }
            }
            position -= freeThemes.size();
        }

        if (lockThemes != null && !lockThemes.isEmpty()) {
            if (position == 0) {
                return ItemSpace.Header;
            }
            position--;
            if (lockThemes.size() > position) {
                switch (position % 2) {
                    case 0:
                        return ItemSpace.Left;
                    case 1:
                        return ItemSpace.Right;
                }
            }
        }
        return ItemSpace.Header;
    }

    public class ThemeViewHolder extends BaseViewHolder<Theme> {

        @BindView(R2.id.iv_thumb)
        RoundedImageView ivThumb;
        @BindView(R2.id.theme_layout)
        RelativeLayout themeLayout;
        @BindView(R2.id.iv_lock)
        ImageView ivLock;
        @BindView(R2.id.iv_tag)
        ImageView ivTag;
        @BindView(R2.id.view_shadow)
        View viewShadow;
        private int imageWidth;
        private int height;

        public ThemeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Context context = itemView.getContext();
            imageWidth = Math.round((CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                    44)) / 2);
            height = Math.round((imageWidth * 122) / 75 + CommonUtil.dp2px(context, 1));
            themeLayout.getLayoutParams().height = height;
            viewShadow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCardThemeClickListener != null) {
                        onCardThemeClickListener.onCardThemePreview(getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(Context context, final Theme theme, int position, int viewType) {
            if (theme != null) {
                itemView.setVisibility(View.VISIBLE);
                String path = theme.getThumbPath();
                path = ImagePath.buildPath(path)
                        .width(imageWidth)
                        .height(height)
                        .cropPath();
                if (!TextUtils.isEmpty(path)) {
                    Glide.with(context)
                            .load(path)
                            .apply(new RequestOptions().override(imageWidth, height)
                                    .placeholder(R.mipmap.icon_empty_image)
                                    .error(R.mipmap.icon_empty_image))
                            .into(ivThumb);
                } else {
                    Glide.with(context)
                            .clear(ivThumb);
                    ivThumb.setImageBitmap(null);
                }
                boolean isLock = theme.isLocked();
                ivLock.setVisibility(isLock ? View.VISIBLE : View.GONE);
                if (isShared || member == 1) {
                    ivLock.setVisibility(View.GONE);
                }
                if (member == 1 || theme.isMember()) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_card_vip___card);
                    ivLock.setVisibility(View.GONE);
                } else if (theme.isSupportVideo()) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_media___card);
                } else {
                    ivTag.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setOnCardThemeClickListener(OnCardThemeClickListener onCardThemeClickListener) {
        this.onCardThemeClickListener = onCardThemeClickListener;
    }

    private OnCardThemeClickListener onCardThemeClickListener;

    public interface OnCardThemeClickListener {

        void onCardShare();

        void onCardThemePreview(Theme theme);

    }

}
