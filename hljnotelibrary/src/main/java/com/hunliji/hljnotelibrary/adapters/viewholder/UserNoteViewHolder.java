package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 她的笔记
 * Created by mo_yu on 2017/6/24 0024.
 */
public class UserNoteViewHolder extends BaseViewHolder<Note> {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_note_type_tag)
    ImageView imgNoteTypeTag;
    private int imageWidth;
    private int imageHeight;
    private OnItemClickListener onItemClickListener;
    public final static int STYLE_RATIO_1_TO_1 = 0; //图片1:1比例
    public final static int STYLE_RATIO_3_TO_4 = 1; //图片3:4比例
    public final static int STYLE_RATIO_4_TO_3 = 2; //图片4:3比例

    public UserNoteViewHolder(View itemView, int style) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 110);
        switch (style) {
            case STYLE_RATIO_4_TO_3:
                this.imageHeight = Math.round(imageWidth * NoteMedia.RATIO_3_TO_4);
                break;
            case STYLE_RATIO_3_TO_4:
                this.imageHeight = Math.round(imageWidth * NoteMedia.RATIO_4_TO_3);
                break;
            default:
                this.imageHeight = imageWidth;
                break;
        }
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, Note note, int position, int viewType) {
        if (note == null) {
            return;
        }
        if (note.getNoteType() == Note.TYPE_RICH) { //长文类的笔记
            imgNoteTypeTag.setVisibility(View.VISIBLE);
            imgNoteTypeTag.setImageResource(R.mipmap.icon_note_tag_36_36);
        } else if (note.getNoteType() == Note.TYPE_VIDEO) { //视频类的笔记
            imgNoteTypeTag.setVisibility(View.VISIBLE);
            imgNoteTypeTag.setImageResource(R.mipmap.icon_play_round_white_36_36);
        } else {
            imgNoteTypeTag.setVisibility(View.GONE);
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(note.getCover()
                        .getCoverPath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .override(imageWidth, imageHeight)
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}