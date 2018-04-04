package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnGetSimilarListener;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.viewholder.tracker.TrackerNoteViewHolder;
import com.hunliji.hljnotelibrary.views.widgets.NoteClickHintView;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通用笔记瀑布流列表
 * Created by chen_bin on 2017/6/24 0024.
 */
public class CommonNoteViewHolder extends TrackerNoteViewHolder {

    @BindView(R2.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R2.id.mask_view)
    View maskView;
    @BindView(R2.id.img_similar_click_hint)
    ImageView imgSimilarClickHint;
    @BindView(R2.id.btn_get_similar)
    ImageButton btnGetSimilar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_collect_count)
    TextView tvCollectCount;
    @BindView(R2.id.note_click_hint_view)
    NoteClickHintView noteClickHintView;

    private int coverWidth;
    private int coverHeight;
    private int avatarSize;
    private int emojiSize;

    private OnGetSimilarListener onGetSimilarListener;
    private OnItemClickListener onItemClickListener;

    public final static int STYLE_RATIO_1_TO_1 = 0; //图片1:1比例
    public final static int STYLE_RATIO_3_TO_4 = 1; //图片3:4比例
    public final static int STYLE_RATIO_4_TO_3 = 2; //图片4:3比例

    public CommonNoteViewHolder(View itemView, int style) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        avatarSize = CommonUtil.dp2px(context, 20);
        emojiSize = CommonUtil.dp2px(context, 14);
        coverWidth = (CommonUtil.getDeviceSize(context).x - (CommonUtil.dp2px(context, 28))) / 2;
        switch (style) {
            case STYLE_RATIO_4_TO_3:
                coverHeight = Math.round(coverWidth * NoteMedia.RATIO_3_TO_4);
                break;
            case STYLE_RATIO_3_TO_4:
                coverHeight = Math.round(coverWidth * NoteMedia.RATIO_4_TO_3);
                break;
            default:
                coverHeight = coverWidth;
                break;
        }
        imgCover.getLayoutParams().height = coverHeight;
        btnGetSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGetSimilarListener != null) {
                    onGetSimilarListener.onGetSimilar(getAdapterPosition());
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
        try {
            HljVTTagger.buildTagger(btnGetSimilar)
                    .tagName(HljTaggerName.BTN_GET_SIMILAR)
                    .hitTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(Context mContext, Note note, int position, int viewType) {
        if (note == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(note.getCover()
                        .getCoverPath())
                        .width(coverWidth)
                        .height(coverHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);

        String title = CommonUtil.isEmpty(note.getTitle()) ? note.getContent() : note.getTitle();
        if (CommonUtil.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            if (!note.isChoice()) { //没有加精
                tvTitle.setText(title);
            } else {
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(mContext,
                        "  " + title,
                        emojiSize);
                if (builder != null) {
                    Drawable drawable = ContextCompat.getDrawable(mContext,
                            R.mipmap.icon_refined_tag_32_32);
                    drawable.setBounds(0,
                            0,
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    builder.setSpan(new HljImageSpan(drawable),
                            0,
                            1,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                tvTitle.setText(builder);
            }
        }

        Author author = note.getAuthor();
        Glide.with(mContext)
                .load(ImagePath.buildPath(author.getAvatar())
                        .width(avatarSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        tvName.setText(author.getName());
        int rightRes = 0;
        if (author.getMember() != null && author.getMember()
                .getId() > 0) {
            rightRes = R.mipmap.icon_member_28_28;
        }
        tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, rightRes, 0);
        tvCollectCount.setText(String.valueOf(note.getCollectCount()));
    }

    public void setShowNoteClickHint(boolean showNoteClickHint) {
        if (!showNoteClickHint) {
            noteClickHintView.setVisibility(View.GONE);
        } else {
            noteClickHintView.setVisibility(View.VISIBLE);
            noteClickHintView.setCenterPoint(coverWidth / 2, coverHeight / 2)
                    .addRippleView();
        }
    }

    public void setShowSimilarClickHint(boolean showSimilarClickHint) {
        if (!showSimilarClickHint) {
            imgSimilarClickHint.setVisibility(View.GONE);
        } else {
            imgSimilarClickHint.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 是否显示找相似icon
     *
     * @param showSimilarIcon
     */
    public void setShowSimilarIcon(boolean showSimilarIcon) {
        if (showSimilarIcon) {
            btnGetSimilar.setVisibility(View.VISIBLE);
            maskView.setVisibility(View.VISIBLE);
        } else {
            maskView.setVisibility(View.GONE);
            btnGetSimilar.setVisibility(View.GONE);
        }
    }

    public void setOnGetSimilarListener(OnGetSimilarListener onGetSimilarListener) {
        this.onGetSimilarListener = onGetSimilarListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}