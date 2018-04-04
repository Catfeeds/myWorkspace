package com.hunliji.hljnotelibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.note.NoteSpot;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.TagAdapter;
import com.hunliji.hljnotelibrary.interfaces.ITagView;
import com.hunliji.hljnotelibrary.models.Direction;
import com.hunliji.hljnotelibrary.utils.DirectionUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteInspirationView extends FrameLayout {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.note_spots_view)
    FrameLayout noteSpotsView;
    @BindView(R2.id.note_spot_empty_view)
    View noteSpotEmptyView;
    private boolean isEdit;
    private TagViewGroup.OnTagGroupClickListener onTagGroupClickListener;
    private TagViewGroup.OnTagGroupDragListener onTagGroupDragListener;

    public NoteInspirationView(Context context) {
        this(context, null);
    }

    public NoteInspirationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoteInspirationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.note_inspiration_view___note, this);
        ButterKnife.bind(this, view);
    }

    /**
     * 设置数据
     */
    public void setDate(ArrayList<NoteSpot> noteSpots) {
        noteSpotsView.removeAllViews();
        for (Iterator iterator = noteSpots.iterator(); iterator.hasNext(); ) {
            NoteSpot noteSpot = (NoteSpot) iterator.next();
            if (CommonUtil.isCollectionEmpty(noteSpot.getTags())) {
                iterator.remove();
            } else {
                addNoteSpotView(noteSpot);
            }
        }
    }

    /**
     * 添加一个标签组
     *
     * @param noteSpot
     */
    public void addNoteSpotView(final NoteSpot noteSpot) {
        final TagViewGroup tagViewGroup = new TagViewGroup(getContext());
        if (isEdit) {
            tagViewGroup.setOnTagGroupDragListener(onTagGroupDragListener);
        } else {
            tagViewGroup.setTagGroupAnimation();
            tagViewGroup.setVisibility(INVISIBLE);
            tagViewGroup.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        tagViewGroup.setLinesRatio(0);
                        tagViewGroup.setTagAlpha(0);
                        tagViewGroup.startShowAnimator();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 240);
        }
        tagViewGroup.setOnTagGroupClickListener(onTagGroupClickListener);
        tagViewGroup.setPercent(noteSpot.getNoteSpotLayout()
                        .getX(),
                noteSpot.getNoteSpotLayout()
                        .getY());
        tagViewGroup.setTagAdapter(new TagAdapter() {

            @Override
            public int getCount() {
                return noteSpot.getTags()
                        .size();
            }

            @Override
            public ITagView getItem(int position) {
                TagTextView tv = new TagTextView(getContext());
                List<String> tags = noteSpot.getTags();
                int type = noteSpot.getNoteSpotLayout()
                        .getType();
                tv.setText(tags.get(position));
                try {
                    final List<Direction[][]> directions = DirectionUtil.getInstance()
                            .getDirections();
                    tv.setDirection(directions.get(tags.size() - 1)[type][position]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return tv;
            }
        });
        noteSpotsView.addView(tagViewGroup);
    }

    /**
     * 移除标签组
     *
     * @param tagViewGroup
     */
    public void removeNoteSpotView(TagViewGroup tagViewGroup) {
        noteSpotsView.removeView(tagViewGroup);
    }

    /**
     * 通过tagViewGroup获取到noteSpot Index
     *
     * @param tagViewGroup
     * @return
     */
    public int getNoteSpotPosition(TagViewGroup tagViewGroup) {
        return noteSpotsView.indexOfChild(tagViewGroup);
    }

    /**
     * 未添加标签时显示点击添加标签
     *
     * @param showNoteSpotEmptyView
     */
    public void setShowNoteSpotEmptyView(boolean showNoteSpotEmptyView) {
        noteSpotEmptyView.setVisibility(showNoteSpotEmptyView ? VISIBLE : GONE);
    }

    /**
     * 加载图片
     *
     * @param imagePath
     * @param imageWidth
     * @param imageHeight
     */
    public void loadImage(String imagePath, int imageWidth, int imageHeight) {
        Glide.with(getContext())
                .load(ImagePath.buildPath(imagePath)
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image)
                        .override(imageWidth, imageHeight)
                        .fitCenter())
                .into(imgCover);
    }

    /**
     * 执行动画
     */
    public void executeTagsAnimation() {
        for (int i = 0, size = noteSpotsView.getChildCount(); i < size; i++) {
            TagViewGroup tagViewGroup = (TagViewGroup) noteSpotsView.getChildAt(i);
            if (tagViewGroup.getVisibility() == INVISIBLE) {
                tagViewGroup.startShowAnimator();
            } else {
                tagViewGroup.startHideAnimator();
            }
        }
    }

    /**
     * 是否是编辑模式
     *
     * @param edit
     */
    public void setEdit(boolean edit) {
        this.isEdit = edit;
    }

    public void setOnTagGroupClickListener(
            TagViewGroup.OnTagGroupClickListener onTagGroupClickListener) {
        this.onTagGroupClickListener = onTagGroupClickListener;
    }

    public void setOnTagGroupDragListener(
            TagViewGroup.OnTagGroupDragListener onTagGroupDragListener) {
        this.onTagGroupDragListener = onTagGroupDragListener;
    }

}