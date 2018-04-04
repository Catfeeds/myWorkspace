package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.UserNoteRecyclerAdapter;
import com.hunliji.hljcommonlibrary.models.note.Note;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/6/29.用户相关笔记
 */

public class UserRelevantNoteViewHolder extends BaseViewHolder<ArrayList<Note>> {

    @BindView(R2.id.tv_relevant_header)
    TextView tvRelevantHeader;
    @BindView(R2.id.relevant_header_layout)
    LinearLayout relevantHeaderLayout;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.tv_look_all)
    TextView tvLookAll;
    @BindView(R2.id.line_layout)
    View lineLayout;
    public UserNoteRecyclerAdapter adapter;
    private ArrayList<Note> userNotes;
    private int noteType;
    private int totalUserNoteCount;
    private boolean gender;
    private OnItemClickListener<Note> onItemClickListener;

    public UserRelevantNoteViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        userNotes = new ArrayList<>();
        adapter = new UserNoteRecyclerAdapter(itemView.getContext(), userNotes);
        adapter.setOnUserNoteItemListener(new UserNoteRecyclerAdapter.OnUserNoteItemListener() {
            @Override
            public void onUserItemClick(int position, Note note) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, note);
                }
            }
        });
        recyclerView.addItemDecoration(new SpacesItemDecoration(itemView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public void setTotalUserNoteCount(int totalUserNoteCount) {
        this.totalUserNoteCount = totalUserNoteCount;
    }

    @Override
    protected void setViewData(
            Context mContext, ArrayList<Note> notes, int position, int viewType) {
        userNotes.clear();
        userNotes.addAll(notes);
        if (CommonUtil.isCollectionEmpty(userNotes)) {
            tvLookAll.setVisibility(View.GONE);
            lineLayout.setVisibility(View.GONE);
        } else {
            tvLookAll.setVisibility(View.VISIBLE);
            lineLayout.setVisibility(View.VISIBLE);
            tvLookAll.setText(mContext.getString(R.string.fmt_look_all_notes___note,
                    String.valueOf(totalUserNoteCount),
                    getNotebookTypeStr(noteType)));
            relevantHeaderLayout.setVisibility(View.VISIBLE);
            tvRelevantHeader.setText(getNotebookTypeTitle(noteType));
        }
        adapter.notifyDataSetChanged();
    }

    public String getNotebookTypeTitle(int notebookType) {
        StringBuilder str = new StringBuilder();
        str.append(gender ? "他" : "她");
        switch (notebookType) {
            case 2:
                return str.append("的婚礼筹备")
                        .toString();
            case 3:
                return str.append("的婚品筹备")
                        .toString();
            case 4:
                return "商家动态";
            default:
                return str.append("的婚纱照")
                        .toString();
        }
    }

    //notebookType 1婚纱照2婚礼筹备3婚品筹备4婚礼人
    public String getNotebookTypeStr(int notebookType) {
        switch (notebookType) {
            case 2:
                return "婚礼筹备";
            case 3:
                return "婚品筹备";
            case 4:
                return "动态";
            default:
                return "婚纱照";
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private int middleSpace;

        SpacesItemDecoration(Context context) {
            this.space = CommonUtil.dp2px(context, 16);
            this.middleSpace = CommonUtil.dp2px(context, 8);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int left = 0;
            int right;
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                left = space;
                right = middleSpace;
            } else if (position == parent.getAdapter()
                    .getItemCount() - 1) {
                right = space;
            } else {
                right = middleSpace;
            }
            outRect.set(left, 0, right, 0);
        }
    }


    public void setOnItemClickListener(OnItemClickListener<Note> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }
}
