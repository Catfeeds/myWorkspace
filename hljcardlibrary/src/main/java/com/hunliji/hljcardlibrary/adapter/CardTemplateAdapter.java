package com.hunliji.hljcardlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.models.Template;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardTemplateAdapter extends RecyclerView.Adapter<BaseViewHolder<Template>> {

    private OnTemplateClickListener onTemplateClickListener;
    private Context context;
    private List<Template> sameThemeTemplates;
    private List<Template> otherTemplates;

    public static final int LOCK_TEMPLATE_HEADER = -1;
    public static final int THREE_TEMPLATE_ITEM = 1;
    private boolean isShared;

    public CardTemplateAdapter(Context context) {
        this.context = context;
    }

    public void setDataList(List<Template> sameThemeTemplates, List<Template> otherTemplates) {
        this.sameThemeTemplates = sameThemeTemplates;
        this.otherTemplates = otherTemplates;
    }

    public void setShared(boolean isShared) {
        this.isShared = isShared;
        notifyDataSetChanged();
    }

    public void setOnTemplateClickListener(OnTemplateClickListener onTemplateClickListener) {
        this.onTemplateClickListener = onTemplateClickListener;
    }

    public enum ItemSpace {
        Header, Left, Right, Middle
    }

    @Override
    public BaseViewHolder<Template> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case THREE_TEMPLATE_ITEM:
                return new TemplateViewHolder(getView(parent, viewType));
        }
        return new ExtraViewHolder(getView(parent, viewType));
    }

    public class ExtraViewHolder extends BaseViewHolder<Template> {

        @BindView(R2.id.view_white)
        View viewWhite;
        @BindView(R2.id.view_gray)
        View viewGray;
        @BindView(R2.id.iv_template_header)
        ImageView ivTemplateHeader;

        public ExtraViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(final Context mContext, Template item, int position, int viewType) {
            ivTemplateHeader.setVisibility(View.VISIBLE);
            if (sameThemeTemplates == null || sameThemeTemplates.isEmpty()) {
                viewWhite.setVisibility(View.GONE);
                viewGray.setVisibility(View.GONE);
            } else {
                viewWhite.setVisibility(View.VISIBLE);
                viewGray.setVisibility(View.VISIBLE);
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
    public void onBindViewHolder(BaseViewHolder<Template> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    private Template getItem(int position) {
        if (sameThemeTemplates != null && !sameThemeTemplates.isEmpty()) {
            if (sameThemeTemplates.size() > position) {
                return sameThemeTemplates.get(position);
            }
            position -= sameThemeTemplates.size();
        }
        if (otherTemplates != null && !otherTemplates.isEmpty()) {
            if (position == 0) {
                return null;
            }
            position--;
            if (otherTemplates.size() > position) {
                return otherTemplates.get(position);
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (sameThemeTemplates != null && !sameThemeTemplates.isEmpty()) {
            if (sameThemeTemplates.size() > position) {
                return THREE_TEMPLATE_ITEM;
            }
            position -= sameThemeTemplates.size();
        }
        if (otherTemplates != null && !otherTemplates.isEmpty()) {
            if (position == 0) {
                return LOCK_TEMPLATE_HEADER;
            }
            position--;
            if (otherTemplates.size() > position) {
                return THREE_TEMPLATE_ITEM;
            }
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        int count = 0;
        if (sameThemeTemplates != null && !sameThemeTemplates.isEmpty()) {
            count += sameThemeTemplates.size();
        }
        if (otherTemplates != null && !otherTemplates.isEmpty()) {
            count += otherTemplates.size() + 1;
        }
        return count;
    }

    public ItemSpace getSpaceType(int position) {
        if (sameThemeTemplates != null && !sameThemeTemplates.isEmpty()) {
            if (sameThemeTemplates.size() > position) {
                switch (position % 3) {
                    case 0:
                        return ItemSpace.Left;
                    case 1:
                        return ItemSpace.Middle;
                    case 2:
                        return ItemSpace.Right;
                }
            }
            position -= sameThemeTemplates.size();
        }

        if (otherTemplates != null && !otherTemplates.isEmpty()) {
            if (position == 0) {
                return ItemSpace.Header;
            }
            position--;
            if (otherTemplates.size() > position) {
                switch (position % 3) {
                    case 0:
                        return ItemSpace.Left;
                    case 1:
                        return ItemSpace.Middle;
                    case 2:
                        return ItemSpace.Right;
                }
            }
        }
        return ItemSpace.Header;
    }

    public class TemplateViewHolder extends BaseViewHolder<Template> {

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

        public TemplateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Context context = itemView.getContext();
            imageWidth = Math.round((CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                    64)) / 3);
            height = Math.round((imageWidth * 122) / 75 + CommonUtil.dp2px(context, 1));
            themeLayout.getLayoutParams().height = height;
            viewShadow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Template template = getItem();
                    if (template == null || onTemplateClickListener == null) {
                        return;
                    }
                    if (template.isMember()) {
                        onTemplateClickListener.onTemplateMember(getItem());
                    } else if (isShared || !template.isLocked()) {
                        onTemplateClickListener.onTemplateUse(getItem());
                    } else {
                        onTemplateClickListener.onTemplateShare();
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context context, final Template template, int position, int viewType) {
            if (template != null) {
                String path = template.getThumbPath();
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
                ivLock.setVisibility(template.isLocked() ? View.VISIBLE : View.GONE);
                if (isShared) {
                    ivLock.setVisibility(View.GONE);
                }
                ivTag.setVisibility(View.VISIBLE);
                if (template.isMember()) {
                    ivTag.setImageResource(R.mipmap.icon_card_vip___card);
                } else if (template.isSupportVideo()) {
                    ivTag.setImageResource(R.mipmap.icon_media___card);
                } else {
                    ivTag.setVisibility(View.GONE);
                }
            }
        }
    }


    public interface OnTemplateClickListener {

        void onTemplateShare();

        void onTemplateUse(Template template);

        void onTemplateMember(Template template);

    }

}
