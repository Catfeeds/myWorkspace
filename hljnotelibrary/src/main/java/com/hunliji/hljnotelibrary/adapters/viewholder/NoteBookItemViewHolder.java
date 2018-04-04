package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljnotelibrary.utils.NotebookGridInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/7/5.笔记本列表项
 */

public class NoteBookItemViewHolder extends BaseViewHolder<Note> {
    @BindView(R2.id.grid_view)
    HljGridView gridView;
    @BindView(R2.id.tv_note_title)
    TextView tvNoteTitle;
    @BindView(R2.id.tv_note_content)
    TextView tvNoteContent;
    @BindView(R2.id.tv_note_collect_count)
    TextView tvNoteCollectCount;
    @BindView(R2.id.bottom_line_view)
    View bottomLineView;
    private OnNotebookItemClickListener onNotebookItemClickListener;

    public NoteBookItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        gridView.setGridInterface(new NotebookGridInterface(itemView.getContext()));
    }

    @Override
    protected void setViewData(
            Context mContext, Note item, final int position, int viewType) {
        if (item != null) {
            ArrayList<Photo> photos = new ArrayList<>();
            for (int i = 0; i < item.getInspirations()
                    .size(); i++) {
                Photo photo = item.getInspirations()
                        .get(i)
                        .getNoteMedia()
                        .getPhoto();
                if (!TextUtils.isEmpty(photo.getImagePath())) {
                    photos.add(photo);
                }
            }
            if (!CommonUtil.isCollectionEmpty(photos)) {
                gridView.setVisibility(View.VISIBLE);
                gridView.setDate(photos);
            } else {
                gridView.setVisibility(View.GONE);
            }
            tvNoteTitle.setVisibility(TextUtils.isEmpty(item.getTitle())?View.GONE:View.VISIBLE);
            tvNoteTitle.setText(item.getTitle());
            tvNoteCollectCount.setText(item.getCollectCount() + "收藏");
            tvNoteContent.setVisibility(TextUtils.isEmpty(item.getContent())?View.GONE:View.VISIBLE);
            tvNoteContent.setText(item.getContent());
            gridView.setItemClickListener(new HljGridView.GridItemClickListener() {
                @Override
                public void onItemClick(View itemView, int index) {
                    if (onNotebookItemClickListener != null) {
                        onNotebookItemClickListener.onNotebookItemClick(position, index);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNotebookItemClickListener != null) {
                        onNotebookItemClickListener.onNotebookItemClick(position, 0);
                    }
                }
            });
        }
    }

    public interface OnNotebookItemClickListener {
        void onNotebookItemClick(int notePosition, int inspirationPosition);
    }

    public void setOnNotebookItemClickListener(
            OnNotebookItemClickListener onNotebookItemClickListener) {
        this.onNotebookItemClickListener = onNotebookItemClickListener;
    }

    public void showOrHideBottomLineView(boolean isShow) {
        bottomLineView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
