package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Media;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.models.note.NoteSpot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 灵感通用viewholder
 * Created by jinxin on 2017/7/12 0012.
 */

public class CommonNoteInspirationViewHolder extends BaseViewHolder<NoteInspiration> {

    public final static int STYLE_RATIO_1_TO_1 = 0; //图片1:1比例
    public final static int STYLE_RATIO_3_TO_4 = 1; //图片3:4比例
    public final static int STYLE_RATIO_4_TO_3 = 2; //图片4:3比例

    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_note_type_tag)
    ImageView imgNoteTypeTag;

    private int imageWidth;
    private int imageHeight;
    private String sign = "#";
    private String space;
    private OnItemClickListener<NoteInspiration> onItemClickListener;

    public CommonNoteInspirationViewHolder(View itemView, int style) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - (CommonUtil.dp2px(
                itemView.getContext(),
                28))) / 2;
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
        space = measureSpace(itemView.getContext());
    }

    private String measureSpace(Context context) {
        StringBuilder s = new StringBuilder();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(CommonUtil.dp2px(context, 12));
        float singleLength = paint.measureText(" ");
        float allLength = CommonUtil.dp2px(context, 8);
        int count = (int) Math.ceil(allLength / singleLength);
        for (int i = 0; i < count; i++) {
            s.append(" ");
        }
        space = s.toString();
        return space;
    }

    @Override
    protected void setViewData(
            Context mContext, NoteInspiration noteInspiration, int position, int viewType) {
        if (noteInspiration == null) {
            return;
        }
        String imgPath = null;
        NoteMedia media = noteInspiration.getNoteMedia();
        if (media != null) {
            imgPath = media.getCoverPath();
        }
        Note note = noteInspiration.getNote();
        if (note != null) {
            if (note.getNoteType() == Note.TYPE_RICH) { //长文类的笔记
                imgNoteTypeTag.setVisibility(View.VISIBLE);
                imgNoteTypeTag.setImageResource(R.mipmap.icon_note_tag_36_36);
            } else if (note.getNoteType() == Note.TYPE_VIDEO) { //视频类的笔记
                imgNoteTypeTag.setVisibility(View.VISIBLE);
                imgNoteTypeTag.setImageResource(R.mipmap.icon_play_round_white_36_36);
            } else {
                imgNoteTypeTag.setVisibility(View.GONE);
            }
        } else {
            imgNoteTypeTag.setVisibility(View.GONE);
        }

        Glide.with(mContext)
                .load(ImagePath.buildPath(imgPath)
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .override(imageWidth, imageHeight)
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);


        List<NoteSpot> spots = noteInspiration.getNoteSpots();
        StringBuilder spotString = new StringBuilder();
        if (spots != null) {
            for (NoteSpot spot : spots) {
                if (spot != null && spot.getNoteMark() != null && spot.getNoteMark()
                        .getId() > 0) {
                    spotString.append(sign)
                            .append(spot.getNoteMark()
                                    .getName())
                            .append(space);
                }
            }
        }
        tvTitle.setText(spotString.toString());
        if (spotString.length() == 0) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
        }
    }

    public void setOnItemClickListener(OnItemClickListener<NoteInspiration> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
